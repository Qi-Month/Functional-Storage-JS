package dev.celestiacraft.functional_storage_js;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.util.IWoodType;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.celestiacraft.functional_storage_js.api.FSJSWoodType;
import dev.celestiacraft.functional_storage_js.event.DrawerBuilder;
import dev.celestiacraft.functional_storage_js.event.FunctionalStorageJSEventGroup;
import dev.celestiacraft.functional_storage_js.event.FunctionalStorageRegisterEventJS;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.client.LangEventJS;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.generator.DataJsonGenerator;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mod(FunctionalStorageJS.MODID)
public class FunctionalStorageJS extends KubeJSPlugin {
	public static final String MODID = "functional_storage_js";
	public static final String NAME = "Functional Storage for KubeJS";
	public static final Logger LOGGER = LogManager.getLogger(NAME);

	private static final int[] DRAWER_SLOTS = {1, 2, 4};
	private static final List<DrawerBuilder> REGISTERED_DRAWERS = new ArrayList<>();
	private static boolean pushedWoodTypes = false;

	public static ResourceLocation loadResource(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

	public FunctionalStorageJS() {
	}

	public FunctionalStorageJS(FMLJavaModLoadingContext context) {
		IEventBus bus = context.getModEventBus();
		pushWoodTypes();
	}

	@Override
	public void registerBindings(BindingsEvent event) {
		event.add("FunctionalStorageJS", FunctionalStorageJS.class);
		event.add("FunctionalStorageJSWoodType", FSJSWoodType.class);
	}

	@Override
	public void registerEvents() {
		FunctionalStorageJSEventGroup.init();
	}

	@Override
	public void initStartup() {
		FunctionalStorageRegisterEventJS event = new FunctionalStorageRegisterEventJS();
		FunctionalStorageJSEventGroup.REGISTER.post(ScriptType.STARTUP, event);
		REGISTERED_DRAWERS.addAll(event.getValidDrawers());

		LOGGER.info("Collected {} drawer wood type(s) from KubeJS startup scripts", REGISTERED_DRAWERS.size());
		pushWoodTypes();
	}

	/**
	 * Pushes all collected drawer wood types into {@link FunctionalStorage#WOOD_TYPES}.
	 * <p>
	 * Called from both the {@code @Mod} constructor (runs during mod construction, before
	 * Functional Storage consumes WOOD_TYPES) and {@link #initStartup()} (runs after the KubeJS
	 * startup scripts have been executed). The {@code pushedWoodTypes} flag makes sure the types
	 * are only added once, no matter which call gets there first.
	 */
	private static synchronized void pushWoodTypes() {
		if (pushedWoodTypes) {
			return;
		}

		List<IWoodType> woodTypes = new ArrayList<>();

		for (DrawerBuilder drawer : REGISTERED_DRAWERS) {
			woodTypes.add(FSJSWoodType.of(drawer.getName(), drawer.getLog(), drawer.getPlanks()));
		}

		if (woodTypes.isEmpty()) {
			LOGGER.info("No drawer wood types to push into FunctionalStorage.WOOD_TYPES yet ({} collected)", REGISTERED_DRAWERS.size());
			return;
		}

		FunctionalStorage.WOOD_TYPES.addAll(woodTypes);
		pushedWoodTypes = true;
		LOGGER.info("Pushed {} drawer wood type(s) into FunctionalStorage.WOOD_TYPES: {}",
				woodTypes.size(),
				woodTypes.stream().map(IWoodType::getName).collect(Collectors.joining(", ")));
	}

	@Override
	public void generateAssetJsons(AssetJsonGenerator generator) {
		for (DrawerBuilder drawer : REGISTERED_DRAWERS) {
			String name = drawer.getName();

			for (int slots : DRAWER_SLOTS) {
				String base = name + "_" + slots;
				String model = "functionalstorage:block/" + base;
				String locked = model + "_locked";
				String front = drawer.getFrontTexture() + "_" + slots;
				String side = drawer.getSideTexture().toString();

				generator.blockModel(loc("functionalstorage", base), (modelGenerator) -> {
					modelGenerator.parent("functionalstorage:block/base_x_" + slots);
					modelGenerator.texture("particle", front);
					modelGenerator.texture("front", front);
					modelGenerator.texture("side", side);
				});

				generator.blockModel(loc("functionalstorage", base + "_locked"), (modelGenerator) -> {
					modelGenerator.parent(model);
					modelGenerator.texture("lock_icon", "functionalstorage:block/lock");
				});

				generator.blockState(loc("functionalstorage", base), state -> {
					state.variant("locked=false,subfacing=east", (variant) -> variant.model(model).y(90).uvlock());
					state.variant("locked=false,subfacing=north", (variant) -> variant.model(model).uvlock());
					state.variant("locked=false,subfacing=south", (variant) -> variant.model(model).y(180).uvlock());
					state.variant("locked=false,subfacing=west", (variant) -> variant.model(model).y(270).uvlock());
					state.variant("locked=true,subfacing=east", (variant) -> variant.model(locked).y(90).uvlock());
					state.variant("locked=true,subfacing=north", (variant) -> variant.model(locked).uvlock());
					state.variant("locked=true,subfacing=south", (variant) -> variant.model(locked).y(180).uvlock());
					state.variant("locked=true,subfacing=west", (variant) -> variant.model(locked).y(270).uvlock());
				});

				generator.itemModel(loc("functionalstorage", base), (modelGenerator) -> modelGenerator.parent("minecraft:builtin/entity"));
			}
		}
	}

	@Override
	public void generateLang(LangEventJS event) {
		for (DrawerBuilder drawer : REGISTERED_DRAWERS) {
			String name = drawer.getName();
			String display = toDisplayName(name);

			event.add("functionalstorage", "block.functionalstorage." + name + "_1", display + " Drawer (1x1)");
			event.add("functionalstorage", "block.functionalstorage." + name + "_2", display + " Drawer (1x2)");
			event.add("functionalstorage", "block.functionalstorage." + name + "_4", display + " Drawer (2x2)");
		}
	}

	@Override
	public void generateDataJsons(DataJsonGenerator generator) {
		JsonArray values = new JsonArray();

		for (DrawerBuilder drawer : REGISTERED_DRAWERS) {
			for (int slots : DRAWER_SLOTS) {
				values.add("functionalstorage:" + drawer.getName() + "_" + slots);
			}
		}

		if (!values.isEmpty()) {
			JsonObject tag = new JsonObject();
			tag.addProperty("replace", false);
			tag.add("values", values);
			generator.json(ResourceLocation.fromNamespaceAndPath("minecraft", "tags/blocks/mineable/axe"), tag);
		}
	}

	private static ResourceLocation loc(String namespace, String path) {
		return ResourceLocation.fromNamespaceAndPath(namespace, path);
	}

	private static String toDisplayName(String name) {
		return Arrays.stream(name.split("_"))
				.map((string) -> {
					return string.isEmpty()
							? string
							: Character.toUpperCase(string.charAt(0)) + string.substring(1);
				})
				.collect(Collectors.joining(" "));
	}
}