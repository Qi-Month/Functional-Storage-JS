package dev.celestiacraft.functional_storage_js.common.register;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.block.DrawerBlock;
import dev.celestiacraft.functional_storage_js.FunctionalStorageJS;
import dev.celestiacraft.functional_storage_js.event.register.builder.DrawerBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FSJSBlockEntity {

	private static final List<DrawerBuilder> DRAWERS = new ArrayList<>();
	private static final Map<String, RegistryObject<BlockEntityType<?>>> TILES_BY_NAME = new HashMap<>();
	private static boolean registered = false;

	public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES =
			DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, FunctionalStorageJS.MODID);

	public static void register(IEventBus bus) {
		BLOCK_ENTITY_TYPES.register(bus);
	}

	public static void registerDrawers(List<DrawerBuilder> drawers) {
		if (registered) {
			return;
		}

		DRAWERS.addAll(drawers);
		registered = true;

		registerDrawerBlockEntities();
	}

	private static void registerDrawerBlockEntities() {
		for (DrawerBuilder drawer : DRAWERS) {
			for (FunctionalStorage.DrawerType type : FunctionalStorage.DrawerType.values()) {
				ResourceLocation id = FSJSBlocks.drawerId(drawer, type);
				RegistryObject<Block> block = FSJSBlocks.getBlock(id);

				if (block == null) {
					continue;
				}

				RegistryObject<BlockEntityType<?>> tile = BLOCK_ENTITY_TYPES.register(id.getPath(), () -> {
					return BlockEntityType.Builder.of(
							((DrawerBlock) block.get()).getTileEntityFactory(),
							block.get()
					).build(null);
				});

				TILES_BY_NAME.put(id.toString(), tile);

				FunctionalStorage.DRAWER_TYPES.computeIfAbsent(type, (drawerType) -> {
					return new ArrayList<>();
				}).add(Pair.of(block, tile));
			}
		}
	}
}