package dev.celestiacraft.fsjs.event;

import dev.celestiacraft.fsjs.event.register.FunctionalStorageRegisterEventJS;
import dev.latvian.mods.kubejs.event.EventGroup;
import dev.latvian.mods.kubejs.event.EventHandler;

public class FunctionalStorageJSEvents {
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