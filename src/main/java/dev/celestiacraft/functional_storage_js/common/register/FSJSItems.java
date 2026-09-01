package dev.celestiacraft.functional_storage_js.common.register;

import com.buuz135.functionalstorage.FunctionalStorage;
import dev.celestiacraft.functional_storage_js.event.register.builder.UpgradeBuilder;
import dev.celestiacraft.functional_storage_js.item.FSJSUpgradeItem;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;

public class FSJSItems {
	private static final List<UpgradeBuilder> UPGRADES = new ArrayList<>();
	private static boolean upgradesRegistered = false;

	public static void register(IEventBus bus) {
		bus.addListener(FSJSItems::onRegisterItems);
	}

	public static void registerUpgrades(List<UpgradeBuilder> upgrades) {
		if (upgradesRegistered) {
			return;
		}

		UPGRADES.addAll(upgrades);
		upgradesRegistered = true;
	}

	private static void onRegisterItems(RegisterEvent event) {
		if (!event.getRegistryKey().equals(ForgeRegistries.Keys.ITEMS)) {
			return;
		}

		for (UpgradeBuilder upgrade : UPGRADES) {
			FSJSUpgradeItem item = new FSJSUpgradeItem(upgrade);

			event.register(ForgeRegistries.Keys.ITEMS, upgrade.getId(), () -> {
				return item;
			});

			// 此时物品已注册完成, 可以安全地加入创造模式标签页
			if (upgrade.addToTab()) {
				FunctionalStorage.TAB.getTabList().add(item);
			}
		}
	}
}