package dev.celestiacraft.fsjs;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.item.StorageUpgradeItem;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import dev.celestiacraft.fsjs.api.FSJSWoodType;
import dev.celestiacraft.fsjs.common.register.FSJSBlockEntity;
import dev.celestiacraft.fsjs.common.register.FSJSBlocks;
import dev.celestiacraft.fsjs.common.register.FSJSItems;
import dev.celestiacraft.fsjs.event.FunctionalStorageJSEvents;
import dev.celestiacraft.fsjs.event.register.FunctionalStorageRegisterEventJS;
import dev.celestiacraft.fsjs.event.register.builder.DrawerBuilder;
import dev.celestiacraft.fsjs.event.register.builder.UpgradeBuilder;
import dev.latvian.mods.kubejs.KubeJSPaths;
import dev.latvian.mods.kubejs.KubeJSPlugin;
import dev.latvian.mods.kubejs.client.LangEventJS;
import dev.latvian.mods.kubejs.generator.AssetJsonGenerator;
import dev.latvian.mods.kubejs.generator.DataJsonGenerator;
import dev.latvian.mods.kubejs.script.BindingsEvent;
import dev.latvian.mods.kubejs.script.ScriptType;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Mod(FunctionalStorageJS.MODID)
public class FunctionalStorageJS extends KubeJSPlugin {
	public static final String MODID = "functional_storage_js";
	public static final String NAME = "Functional Storage for KubeJS";
	public static final Logger LOGGER = LogManager.getLogger(NAME);

	private static final int[] DRAWER_SLOTS = {1, 2, 4};
	private static final List<DrawerBuilder> REGISTERED_DRAWERS = new ArrayList<>();
	private static final List<UpgradeBuilder> REGISTERED_UPGRADES = new ArrayList<>();
	private static boolean drawersRegistered = false;

	public static ResourceLocation loadResource(String path) {
		return loc(MODID, path);
	}

	public static ResourceLocation loadFunctionalStorage(String path) {
		return loc(FunctionalStorage.MOD_ID, path);
	}

	public FunctionalStorageJS() {
	}

	public FunctionalStorageJS(FMLJavaModLoadingContext context) {
		IEventBus bus = context.getModEventBus();

		FSJSBlocks.register(bus);
		FSJSBlockEntity.register(bus);
		FSJSItems.register(bus);
	}

	@Override
	public void registerBindings(BindingsEvent event) {
		event.add("FunctionalStorageJS", FunctionalStorageJS.class);
		event.add("FunctionalStorageJSWoodType", FSJSWoodType.class);
	}

	@Override
	public void registerEvents() {
		FunctionalStorageJSEvents.init();
	}

	@Override
	public void initStartup() {
		FunctionalStorageRegisterEventJS event = new FunctionalStorageRegisterEventJS();
		FunctionalStorageJSEvents.REGISTER.post(ScriptType.STARTUP, event);
		REGISTERED_DRAWERS.addAll(event.getValidDrawers());

		LOGGER.info("Collected {} drawer wood type(s) from KubeJS startup scripts", REGISTERED_DRAWERS.size());
		registerDrawers();

		REGISTERED_UPGRADES.addAll(event.getUpgrades());
		FSJSItems.registerUpgrades(REGISTERED_UPGRADES);
		LOGGER.info("Collected {} drawer upgrade(s) from KubeJS startup scripts", REGISTERED_UPGRADES.size());
	}

	private static void registerDrawers() {
		if (drawersRegistered) {
			return;
		}

		FSJSBlocks.registerDrawers(REGISTERED_DRAWERS);
		FSJSBlockEntity.registerDrawers(REGISTERED_DRAWERS);
		drawersRegistered = true;
	}

	@Override
	public void generateAssetJsons(AssetJsonGenerator generator) {
		for (DrawerBuilder drawer : REGISTERED_DRAWERS) {
			String name = drawer.getName();

			for (int slots : DRAWER_SLOTS) {
				String base = name + "_" + slots;
				String model = drawer.getNamespace() + ":block/" + base;
				String locked = model + "_locked";
				String front = drawer.getFrontTexture() + "_" + slots;
				String side = drawer.getSideTexture().toString();

				generator.blockModel(loc(drawer.getNamespace(), base), (modelGenerator) -> {
					modelGenerator.parent("functionalstorage:block/base_x_" + slots);
					modelGenerator.texture("particle", front);
					modelGenerator.texture("front", front);
					modelGenerator.texture("side", side);
				});

				generator.blockModel(loc(drawer.getNamespace(), base + "_locked"), (modelGenerator) -> {
					modelGenerator.parent(model);
					modelGenerator.texture("lock_icon", "functionalstorage:block/lock");
				});

				generator.blockState(loc(drawer.getNamespace(), base), (state) -> {
					state.variant("locked=false,subfacing=east", (variant) -> {
						variant.model(model)
								.y(90)
								.uvlock();
					});
					state.variant("locked=false,subfacing=north", (variant) -> {
						variant.model(model)
								.uvlock();
					});
					state.variant("locked=false,subfacing=south", (variant) -> {
						variant.model(model)
								.y(180)
								.uvlock();
					});
					state.variant("locked=false,subfacing=west", (variant) -> {
						variant.model(model)
								.y(270)
								.uvlock();
					});
					state.variant("locked=true,subfacing=east", (variant) -> {
						variant.model(locked)
								.y(90)
								.uvlock();
					});
					state.variant("locked=true,subfacing=north", (variant) -> {
						variant.model(locked)
								.uvlock();
					});
					state.variant("locked=true,subfacing=south", (variant) -> {
						variant.model(locked)
								.y(180)
								.uvlock();
					});
					state.variant("locked=true,subfacing=west", (variant) -> {
						variant.model(locked)
								.y(270)
								.uvlock();
					});
				});

				generator.itemModel(loc(drawer.getNamespace(), base), (modelGenerator) -> {
					modelGenerator.parent("minecraft:builtin/entity");
				});
				copyFrontTexture(generator, drawer, slots);
			}
		}

		for (UpgradeBuilder upgrade : REGISTERED_UPGRADES) {
			generator.itemModel(upgrade.getId(), (modelGenerator) -> {
				modelGenerator.parent("minecraft:item/generated");
				modelGenerator.texture("layer0", upgrade.getTexture().toString());
			});
		}
	}

	/**
	 * Functional Storage 的抽屉 GUI 始终会从 `functionalstorage:textures/block/<name>_front_<slots>.png` 读取正面纹理
	 * <p>
	 * 而不会使用方块模型中由 `DrawerBuilder#frontTexture` 指定的自定义纹理。
	 * <p>
	 * 为了使 GUI 能够支持任意纹理位置, 需要将源正面纹理临时复制到 KubeJS 生成的资源包中的这个固定路径下
	 *
	 * @param generator
	 * @param drawer
	 * @param slots
	 */
	private static void copyFrontTexture(AssetJsonGenerator generator, DrawerBuilder drawer, int slots) {
		ResourceLocation frontBase = drawer.getFrontTexture();
		ResourceLocation source = loc(frontBase.getNamespace(), "textures/" + frontBase.getPath() + "_" + slots + ".png");
		ResourceLocation target = loadFunctionalStorage("textures/block/" + drawer.getName() + "_front_" + slots + ".png");

		byte[] bytes = readTexture(source);

		if (bytes != null) {
			generator.add(target, () -> bytes);
		} else {
			LOGGER.warn("Could not find front texture {}, the drawer GUI will use the missing texture", source);
		}
	}

	private static byte[] readTexture(ResourceLocation id) {
		// 优先使用资源管理器, 这样也能支持来自 Mod 文件的纹理
		try {
			ResourceManager manager = Minecraft.getInstance().getResourceManager();
			Optional<Resource> resource = manager.getResource(id);

			if (resource.isPresent()) {
				try (InputStream inputStream = resource.get().open()) {
					return inputStream.readAllBytes();
				}
			}
		} catch (Exception ignored) {
		}

		// 备用方案: 使用 `kubejs/assets/<namespace>/<path>` 中的文件
		try {
			Path path = KubeJSPaths.ASSETS.resolve(id.getNamespace() + "/" + id.getPath());

			if (Files.exists(path)) {
				return Files.readAllBytes(path);
			}
		} catch (Exception ignored) {
		}

		return null;
	}

	@Override
	public void generateLang(LangEventJS event) {
		for (DrawerBuilder drawer : REGISTERED_DRAWERS) {
			String name = drawer.getName();
			String display = toDisplayName(name);

			event.add(drawer.getNamespace(), "block." + drawer.getNamespace() + "." + name + "_1", display + " Drawer (1x1)");
			event.add(drawer.getNamespace(), "block." + drawer.getNamespace() + "." + name + "_2", display + " Drawer (1x2)");
			event.add(drawer.getNamespace(), "block." + drawer.getNamespace() + "." + name + "_4", display + " Drawer (2x2)");
		}

		for (UpgradeBuilder upgrade : REGISTERED_UPGRADES) {
			event.add(
					upgrade.getId().getNamespace(),
					"item." + upgrade.getId().getNamespace() + "." + upgrade.getId().getPath(),
					toDisplayName(upgrade.getId().getPath())
			);
		}
	}

	@Override
	public void generateDataJsons(DataJsonGenerator generator) {
		JsonArray values = new JsonArray();

		for (DrawerBuilder drawer : REGISTERED_DRAWERS) {
			for (int slots : DRAWER_SLOTS) {
				values.add(drawer.getNamespace() + ":" + drawer.getName() + "_" + slots);
			}
		}

		if (!values.isEmpty()) {
			JsonObject json = new JsonObject();

			json.addProperty("replace", false);
			json.add("values", values);
			generator.json(ResourceLocation.withDefaultNamespace("tags/blocks/mineable/axe"), json);
		}

		JsonArray upgradeValues = new JsonArray();

		for (StorageUpgradeItem.StorageTier tier : StorageUpgradeItem.StorageTier.values()) {
			RegistryObject<Item> upgradeItem = FunctionalStorage.STORAGE_UPGRADES.get(tier);

			if (upgradeItem != null && upgradeItem.isPresent()) {
				upgradeValues.add(upgradeItem.getId().toString());
			}
		}

		for (UpgradeBuilder upgrade : REGISTERED_UPGRADES) {
			upgradeValues.add(upgrade.getId().toString());
		}

		if (!upgradeValues.isEmpty()) {
			JsonObject json = new JsonObject();

			json.addProperty("replace", false);
			json.add("values", upgradeValues);
			generator.json(loadFunctionalStorage("tags/items/upgrades"), json);
		}

		JsonArray drawerItemValues = new JsonArray();

		for (DrawerBuilder drawer : REGISTERED_DRAWERS) {
			for (int slots : DRAWER_SLOTS) {
				drawerItemValues.add(drawer.getNamespace() + ":" + drawer.getName() + "_" + slots);
			}
		}

		if (!drawerItemValues.isEmpty()) {
			JsonObject drawerTag = new JsonObject();

			drawerTag.addProperty("replace", false);
			drawerTag.add("values", drawerItemValues);
			generator.json(loadFunctionalStorage("tags/items/drawer"), drawerTag);
		}
	}

	/**
	 * 我说写那么长一串 {@link ResourceLocation#fromNamespaceAndPath(String, String)} 很烦有没有懂的
	 *
	 * @param namespace
	 * @param path
	 * @return
	 */
	private static ResourceLocation loc(String namespace, String path) {
		return ResourceLocation.fromNamespaceAndPath(namespace, path);
	}

	private static String toDisplayName(String name) {
		return Arrays.stream(name.split("_")).map((string) -> {
			return string.isEmpty()
					? string
					: Character.toUpperCase(string.charAt(0))
					+ string.substring(1);
		}).collect(Collectors.joining(" "));
	}
}