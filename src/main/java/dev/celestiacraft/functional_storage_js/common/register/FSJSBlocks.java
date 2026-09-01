package dev.celestiacraft.functional_storage_js.common.register;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.block.DrawerBlock;
import dev.celestiacraft.functional_storage_js.FunctionalStorageJS;
import dev.celestiacraft.functional_storage_js.api.FSJSWoodType;
import dev.celestiacraft.functional_storage_js.event.register.builder.DrawerBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class FSJSBlocks {

	private static final List<DrawerBuilder> DRAWERS = new ArrayList<>();
	private static boolean registered = false;

	public static final DeferredRegister<Block> BLOCKS =
			DeferredRegister.create(ForgeRegistries.BLOCKS, FunctionalStorageJS.MODID);

	public static final DeferredRegister<Item> ITEMS =
			DeferredRegister.create(ForgeRegistries.ITEMS, FunctionalStorageJS.MODID);

	public static void register(IEventBus bus) {
		BLOCKS.register(bus);
		ITEMS.register(bus);
	}

	public static void registerDrawers(List<DrawerBuilder> drawers) {
		if (registered) {
			return;
		}

		DRAWERS.addAll(drawers);
		registered = true;

		registerDrawerBlocks();
		registerDrawerItems();
	}

	public static ResourceLocation drawerId(DrawerBuilder drawer, FunctionalStorage.DrawerType type) {
		return ResourceLocation.fromNamespaceAndPath(
				FunctionalStorageJS.MODID,
				drawer.getName() + "_" + type.getSlots()
		);
	}

	public static RegistryObject<Block> getBlock(ResourceLocation id) {
		return BLOCKS.getEntries()
				.stream()
				.filter((entry) -> {
					return entry.getId().equals(id);
				})
				.map((entry) -> {
					return (RegistryObject<Block>) entry;
				})
				.findFirst()
				.orElse(null);
	}

	private static void registerDrawerBlocks() {
		for (DrawerBuilder drawer : DRAWERS) {
			FSJSWoodType woodType = FSJSWoodType.of(
					drawer.getName(),
					drawer.getLog(),
					drawer.getPlanks()
			);

			Block planksBlock = woodType.getPlanks();

			if (planksBlock == null) {
				FunctionalStorageJS.LOGGER.error(
						"Skipping drawer wood type {}: planks block {} is not registered. " +
								"If it belongs to another mod, that mod must load before functional_storage_js",
						drawer.getName(),
						drawer.getPlanks()
				);
				continue;
			}

			if (woodType.getWood() == null) {
				FunctionalStorageJS.LOGGER.error(
						"Skipping drawer wood type {}: log block {} is not registered. " +
								"If it belongs to another mod, that mod must load before functional_storage_js",
						drawer.getName(),
						drawer.getLog()
				);
				continue;
			}

			final Block finalPlanksBlock = planksBlock;

			for (FunctionalStorage.DrawerType type : FunctionalStorage.DrawerType.values()) {
				ResourceLocation id = drawerId(drawer, type);
				BlockBehaviour.Properties properties = BlockBehaviour.Properties.copy(finalPlanksBlock);

				BLOCKS.register(id.getPath(), () -> {
					return new DrawerBlock(woodType, type, properties);
				});

				FunctionalStorageJS.LOGGER.info(
						"Registered drawer block {}",
						id
				);
			}
		}
	}

	private static void registerDrawerItems() {
		for (DrawerBuilder drawer : DRAWERS) {
			for (FunctionalStorage.DrawerType type : FunctionalStorage.DrawerType.values()) {
				String name = drawer.getName() + "_" + type.getSlots();

				ITEMS.register(name, () -> {
					RegistryObject<Block> block = BLOCKS.getEntries()
							.stream()
							.filter((entry) -> {
								return entry.getId()
										.getPath()
										.equals(name);
							})
							.map((entry) -> {
								return (RegistryObject<Block>) entry;
							})
							.findFirst()
							.orElseThrow();

					Item created = new DrawerBlock.DrawerItem(
							(DrawerBlock) block.get(),
							new Item.Properties(),
							FunctionalStorage.TAB
					);

					FunctionalStorage.TAB.getTabList().add(created);

					return created;
				});
			}
		}
	}
}