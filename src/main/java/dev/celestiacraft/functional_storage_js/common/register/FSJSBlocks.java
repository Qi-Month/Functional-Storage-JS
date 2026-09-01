package dev.celestiacraft.functional_storage_js.common.register;

import com.buuz135.functionalstorage.FunctionalStorage;
import com.buuz135.functionalstorage.block.DrawerBlock;
import dev.celestiacraft.functional_storage_js.FunctionalStorageJS;
import dev.celestiacraft.functional_storage_js.api.FSJSWoodType;
import dev.celestiacraft.functional_storage_js.event.register.builder.DrawerBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FSJSBlocks {
	private static final List<DrawerBuilder> DRAWERS = new ArrayList<>();
	private static final Map<ResourceLocation, RegistryObject<Block>> REGISTERED_BLOCKS = new HashMap<>();
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
		return REGISTERED_BLOCKS.get(id);
	}

	private static void registerDrawerBlocks() {
		for (DrawerBuilder drawer : DRAWERS) {
			FSJSWoodType woodType = FSJSWoodType.of(
					drawer.getName(),
					drawer.getLog(),
					drawer.getPlanks()
			);

			for (FunctionalStorage.DrawerType type : FunctionalStorage.DrawerType.values()) {
				ResourceLocation id = drawerId(drawer, type);

				/*
				 * 在 BLOCKS RegisterEvent 触发时解析木板方块, 而不是在 KubeJS 启动脚本运行时解析
				 * 此时其他 Mod 的方块尚未完成注册
				 * 如果被引用的 Mod 在本 Mod 之后加载, 则回退使用橡木木板的方块属性
				 * 而不是跳过注册, 以确保抽屉仍然存在且可以被正常挖掘
				 */
				RegistryObject<Block> block = BLOCKS.register(id.getPath(), () -> {
					Block planksBlock = woodType.getPlanks();

					if (planksBlock == null) {
						FunctionalStorageJS.LOGGER.warn(
								"Drawer block {}: planks block {} is not registered yet, " +
										"falling back to oak planks block properties",
								id,
								drawer.getPlanks()
						);
						planksBlock = Blocks.OAK_PLANKS;
					}

					return new DrawerBlock(
							woodType,
							type,
							BlockBehaviour.Properties.copy(planksBlock)
					);
				});

				REGISTERED_BLOCKS.put(id, block);

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
				ResourceLocation id = drawerId(drawer, type);
				RegistryObject<Block> block = REGISTERED_BLOCKS.get(id);

				if (block == null) {
					continue;
				}

				ITEMS.register(id.getPath(), () -> {
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