package dev.celestiacraft.functional_storage_js.common.register;

import com.buuz135.functionalstorage.FunctionalStorage;
import dev.celestiacraft.functional_storage_js.event.register.builder.UpgradeBuilder;
import dev.celestiacraft.functional_storage_js.item.FSJSUpgradeItem;
import net.minecraft.resources.ResourceKey;
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
		ResourceKey<?> key = event.getRegistryKey();

		if (!key.equals(ForgeRegistries.Keys.ITEMS)) {
			return;
		}

		for (UpgradeBuilder upgrade : UPGRADES) {
			FSJSUpgradeItem item = new FSJSUpgradeItem(upgrade);
			event.register(ForgeRegistries.Keys.ITEMS, upgrade.getId(), () -> item);

			if (upgrade.addToTab()) {
				FunctionalStorage.TAB.getTabList().add(item);
			}
		}
	}
}