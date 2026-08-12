package dev.celestiacraft.functional_storage_js.event;

import com.buuz135.functionalstorage.util.DrawerWoodType;
import com.buuz135.functionalstorage.util.IWoodType;
import dev.celestiacraft.functional_storage_js.api.FSJSWoodType;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ConsoleJS;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;

public class FunctionalStorageRegisterEventJS extends EventJS {
	private final List<DrawerBuilder> drawers = new ArrayList<>();

	@Info("Starts a new drawer wood type and configures it with a callback. Call .log(...), .planks(...), .frontTexture(...) and .sideTexture(...) on the builder. Functional Storage will create 1x1, 1x2 and 2x2 drawers for it.")
	public void addDrawer(String name, Consumer<DrawerBuilder> builder) {
		DrawerBuilder drawer = DrawerBuilder.of(normalizeName(name));
		drawers.add(drawer);
		builder.accept(drawer);
	}

	@Info("Placeholder for the upcoming upgrade registration API.")
	public void addUpgrade() {
		ConsoleJS.STARTUP.warn("FunctionalStorageJSEvents.register#addUpgrade is not implemented yet");
	}

	public List<DrawerBuilder> getValidDrawers() {
		List<DrawerBuilder> result = new ArrayList<>();
		Set<String> seen = new HashSet<>();

		for (DrawerBuilder builder : drawers) {
			String name = builder.getName();

			if (name == null || name.isBlank()) {
				ConsoleJS.STARTUP.warn("Skipped a drawer wood type with an empty name");
				continue;
			}

			if (!seen.add(name)) {
				ConsoleJS.STARTUP.warn("Drawer wood type '%s' was already registered, skipped".formatted(name));
				continue;
			}

			boolean conflict = false;

			for (DrawerWoodType vanilla : DrawerWoodType.values()) {
				if (vanilla.getName().equals(name)) {
					ConsoleJS.STARTUP.warn("Drawer wood type '%s' conflicts with a vanilla Functional Storage wood type, skipped".formatted(name));
					conflict = true;
					break;
				}
			}

			if (conflict) {
				continue;
			}

			if (builder.getLog() == null || builder.getPlanks() == null) {
				ConsoleJS.STARTUP.warn("Drawer wood type '%s' is missing a log or planks block, skipped".formatted(name));
				continue;
			}

			result.add(builder);
		}

		return result;
	}

	public List<IWoodType> getWoodTypes() {
		List<IWoodType> result = new ArrayList<>();

		for (DrawerBuilder builder : getValidDrawers()) {
			result.add(FSJSWoodType.of(builder.getName(), builder.getLog(), builder.getPlanks()));
		}

		return result;
	}

	private static String normalizeName(String name) {
		if (name == null || name.isBlank()) {
			ConsoleJS.STARTUP.warn("Drawer wood type name is empty, skipped");
			return null;
		}

		String normalized = name.trim().toLowerCase(Locale.ROOT);
		int colon = normalized.indexOf(':');

		if (colon != -1) {
			ConsoleJS.STARTUP.warn("Drawer wood type '%s' contains a namespace, using '%s' as the name".formatted(name, normalized.substring(colon + 1)));
			normalized = normalized.substring(colon + 1);
		}

		return normalized;
	}
}