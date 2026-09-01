package dev.celestiacraft.functional_storage_js.common.register;

import com.buuz135.functionalstorage.FunctionalStorage;
import dev.celestiacraft.functional_storage_js.FunctionalStorageJS;
import dev.celestiacraft.functional_storage_js.event.register.builder.UpgradeBuilder;
import dev.celestiacraft.functional_storage_js.item.FSJSUpgradeItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

public class FSJSItems {
	private static final List<UpgradeBuilder> UPGRADES = new ArrayList<>();
	private static final List<Pair<UpgradeBuilder, RegistryObject<Item>>> UPGRADE_ITEMS = new ArrayList<>();
	private static boolean upgradesRegistered = false;

	public static final DeferredRegister<Item> ITEMS =
			DeferredRegister.create(ForgeRegistries.ITEMS, FunctionalStorageJS.MODID);

	public static void register(IEventBus bus) {
		ITEMS.register(bus);
		// 使用较低优先级, 确保在 DeferredRegister 实际完成物品注册后再执行
		bus.addListener(EventPriority.LOW, FSJSItems::onRegisterItems);
	}

	public static void registerUpgrades(List<UpgradeBuilder> upgrades) {
		if (upgradesRegistered) {
			return;
		}

		UPGRADES.addAll(upgrades);
		upgradesRegistered = true;

		registerUpgradeItems();
	}

	private static void registerUpgradeItems() {
		for (UpgradeBuilder upgrade : UPGRADES) {
			RegistryObject<Item> item = ITEMS.register(upgrade.getId().getPath(), () -> {
				return new FSJSUpgradeItem(upgrade);
			});

			UPGRADE_ITEMS.add(Pair.of(upgrade, item));
		}
	}

	private static void onRegisterItems(RegisterEvent event) {
		if (!event.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS)) {
			return;
		}

		/*
		 * 只有在 ITEMS 的 RegisterEvent 执行完毕后才能调用 RegistryObject#get
		 * 否则该物品尚未注册到注册表中，调用 get() 会抛出异常
		 */
		for (Pair<UpgradeBuilder, RegistryObject<Item>> entry : UPGRADE_ITEMS) {
			if (entry.getLeft().addToTab()) {
				FunctionalStorage.TAB.getTabList().add(entry.getRight().get());
			}
		}
	}
}