package dev.celestiacraft.fsjs.event.register;

import com.buuz135.functionalstorage.util.DrawerWoodType;
import com.buuz135.functionalstorage.util.IWoodType;
import dev.celestiacraft.fsjs.api.FSJSWoodType;
import dev.celestiacraft.fsjs.event.register.builder.DrawerBuilder;
import dev.celestiacraft.fsjs.event.register.builder.UpgradeBuilder;
import dev.latvian.mods.kubejs.event.EventJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.function.Consumer;

@Getter
public class FunctionalStorageRegisterEventJS extends EventJS {
	private final List<DrawerBuilder> drawers = new ArrayList<>();
	private final List<UpgradeBuilder> upgrades = new ArrayList<>();

	@Info("""
			Starts a new drawer wood type and configures it with a callback.
			Call `.log(...)`, `.planks(...)`, `.frontTexture(...)` and `.sideTexture(...)` on the builder.
			Functional Storage will create `1x1`, `1x2` and `2x2` drawers for it.
			
			开始创建一种新的抽屉木材类型，并通过回调对其进行配置.
			在构建器上调用 `.log(...)`, `.planks(...)`, `.frontTexture(...)` 和 `.sideTexture(...)` 进行配置.
			Functional Storage 会为该木材类型创建 `1x1`, `1x2` 和 `2x2` 三种抽屉.
			""")
	public void addDrawer(String name, Consumer<DrawerBuilder> builder) {
		String[] parts = parseDrawerName(name);

		if (parts == null) {
			return;
		}

		DrawerBuilder drawer = DrawerBuilder.of(parts[0], parts[1]);
		drawers.add(drawer);
		builder.accept(drawer);
	}

	@Info("""
			Starts a new drawer storage upgrade and configures it with a callback.
			Call `.multiplier(...)`, `.texture(...)?` and `.tooltip(...)?` on the builder.
			
			开始创建一种新的抽屉存储升级，并通过回调对其进行配置.
			在构建器上调用 `.multiplier(...)`, `.texture(...)?` 和 `.tooltip(...)?` 进行配置.
			""")
	public void addUpgrade(String name, Consumer<UpgradeBuilder> builder) {
		ResourceLocation id = normalizeUpgradeId(name);

		if (id == null) {
			return;
		}

		UpgradeBuilder upgrade = UpgradeBuilder.of(id);
		upgrades.add(upgrade);
		builder.accept(upgrade);
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

			String key = builder.getNamespace() + ":" + name;

			if (!seen.add(key)) {
				ConsoleJS.STARTUP.warn("Drawer wood type %s was already registered, skipped".formatted(key));
				continue;
			}

			if (builder.getNamespace().equals("functionalstorage")) {
				boolean conflict = false;

				for (DrawerWoodType vanilla : DrawerWoodType.values()) {
					if (vanilla.getName().equals(name)) {
						ConsoleJS.STARTUP.warn("Drawer wood type %s conflicts with a vanilla Functional Storage wood type, skipped".formatted(key));
						conflict = true;
						break;
					}
				}

				if (conflict) {
					continue;
				}
			}

			if (builder.getLog() == null || builder.getPlanks() == null) {
				ConsoleJS.STARTUP.warn("Drawer wood type %s is missing a log or planks block, skipped".formatted(key));
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

	private static String[] parseDrawerName(String name) {
		if (name == null || name.isBlank()) {
			ConsoleJS.STARTUP.warn("Drawer wood type name is empty, skipped");
			return null;
		}

		String normalized = name.trim().toLowerCase(Locale.ROOT);
		int colon = normalized.indexOf(":");

		if (colon != -1) {
			String namespace = normalized.substring(0, colon);
			String path = normalized.substring(colon + 1);

			if (namespace.isEmpty() || path.isEmpty()) {
				ConsoleJS.STARTUP.warn("Drawer wood type name %s is invalid, skipped".formatted(name));
				return null;
			}

			return new String[]{namespace, path};
		}

		return new String[]{"kubejs", normalized};
	}

	private static ResourceLocation normalizeUpgradeId(String name) {
		if (name == null || name.isBlank()) {
			ConsoleJS.STARTUP.warn("Upgrade name is empty, skipped");
			return null;
		}

		String trimmed = name.trim();

		if (trimmed.contains(":")) {
			return ResourceLocation.parse(trimmed);
		}

		String path = trimmed;

		if (!path.endsWith("_upgrade")) {
			path += "_upgrade";
		}

		return ResourceLocation.fromNamespaceAndPath("kubejs", path);
	}
}
