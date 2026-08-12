package dev.celestiacraft.functional_storage_js.event;

import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public class FunctionalStorageJSEventGroup {
	private static final EventGroup GROUP;

	public static final EventHandler REGISTER;

	static {
		GROUP = EventGroup.of("FunctionalStorageJSEvents");
		REGISTER = GROUP.startup("register", () -> {
			return FunctionalStorageRegisterEventJS.class;
		});
	}

	public static void init() {
		GROUP.register();
	}
}