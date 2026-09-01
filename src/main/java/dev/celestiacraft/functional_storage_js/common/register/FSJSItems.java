package dev.celestiacraft.functional_storage_js.common.register;

import com.buuz135.functionalstorage.FunctionalStorage;
import dev.celestiacraft.functional_storage_js.FunctionalStorageJS;
import dev.celestiacraft.functional_storage_js.event.register.builder.UpgradeBuilder;
import dev.celestiacraft.functional_storage_js.item.FSJSUpgradeItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;

public class FSJSItems {

	private static final List<UpgradeBuilder> UPGRADES = new ArrayList<>();
	private static boolean upgradesRegistered = false;

	public static final DeferredRegister<Item> ITEMS =
			DeferredRegister.create(ForgeRegistries.ITEMS, FunctionalStorageJS.MODID);

	public static void register(IEventBus bus) {
		ITEMS.register(bus);
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

			if (upgrade.addToTab()) {
				FunctionalStorage.TAB.getTabList().add(item.get());
			}
		}
	}
}