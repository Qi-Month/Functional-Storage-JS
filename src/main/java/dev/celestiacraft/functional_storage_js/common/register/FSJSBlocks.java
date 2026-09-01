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
		return ResourceLocation.fromNamespaceAndPath(
				drawer.getNamespace(),
				drawer.getName() + "_" + type.getSlots()
		);
	}

	public static RegistryObject<Block> getBlock(ResourceLocation id) {
		return BLOCKS_BY_NAME.get(id.toString());
	}

	private static void onRegisterBlocks(RegisterEvent event) {
		if (!event.getRegistryKey().equals(ForgeRegistries.Keys.BLOCKS)) {
			return;
		}

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
				event.register(ForgeRegistries.Keys.BLOCKS, id, () -> {
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