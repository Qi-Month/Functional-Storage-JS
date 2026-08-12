package dev.celestiacraft.functional_storage_js.common.register;

import com.buuz135.functionalstorage.FunctionalStorage;
import dev.celestiacraft.functional_storage_js.FunctionalStorageJS;
import com.buuz135.functionalstorage.block.DrawerBlock;
import dev.celestiacraft.functional_storage_js.api.FSJSWoodType;
import dev.celestiacraft.functional_storage_js.event.register.builder.DrawerBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FSJSBlocks {
	private static final List<DrawerBuilder> DRAWERS = new ArrayList<>();
	private static final Map<String, RegistryObject<Block>> BLOCKS_BY_NAME = new HashMap<>();
	private static boolean registered = false;

	public static void register(IEventBus bus) {
		bus.addListener(FSJSBlocks::onRegisterBlocks);
		bus.addListener(FSJSBlocks::onRegisterItems);
	}

	public static void registerDrawers(List<DrawerBuilder> drawers) {
		if (registered) {
			return;
		}

		DRAWERS.addAll(drawers);
		registered = true;
	}

	public static ResourceLocation drawerId(DrawerBuilder drawer, FunctionalStorage.DrawerType type) {
		return ResourceLocation.fromNamespaceAndPath(drawer.getNamespace(), drawer.getName() + "_" + type.getSlots());
	}

	public static RegistryObject<Block> getBlock(ResourceLocation id) {
		return BLOCKS_BY_NAME.get(id.toString());
	}

	private static void onRegisterBlocks(RegisterEvent event) {
		if (!event.getRegistryKey().equals(ForgeRegistries.Keys.BLOCKS)) {
			return;
		}

		for (DrawerBuilder drawer : DRAWERS) {
			FSJSWoodType woodType = FSJSWoodType.of(drawer.getName(), drawer.getLog(), drawer.getPlanks());

			for (FunctionalStorage.DrawerType type : FunctionalStorage.DrawerType.values()) {
				ResourceLocation id = drawerId(drawer, type);

				event.register(ForgeRegistries.Keys.BLOCKS, id, () -> {
					return new DrawerBlock(woodType, type, BlockBehaviour.Properties.copy(woodType.getPlanks()));
				});
				BLOCKS_BY_NAME.put(id.toString(), RegistryObject.create(id, ForgeRegistries.BLOCKS));
				FunctionalStorageJS.LOGGER.info("Registered drawer block {}", id);
			}
		}
	}

	private static void onRegisterItems(RegisterEvent event) {
		if (!event.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS)) {
			return;
		}

		for (DrawerBuilder drawer : DRAWERS) {
			for (FunctionalStorage.DrawerType type : FunctionalStorage.DrawerType.values()) {
				ResourceLocation id = drawerId(drawer, type);
				RegistryObject<Block> block = BLOCKS_BY_NAME.get(id.toString());

				if (block == null) {
					continue;
				}

				event.register(ForgeRegistries.Keys.ITEMS, id, () -> {
					Item created = new DrawerBlock.DrawerItem((DrawerBlock) block.get(), new Item.Properties(), FunctionalStorage.TAB);
					FunctionalStorage.TAB.getTabList().add(created);
					return created;
				});
			}
		}
	}
}
