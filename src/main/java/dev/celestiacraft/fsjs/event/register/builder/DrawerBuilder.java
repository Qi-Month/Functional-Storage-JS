package dev.celestiacraft.fsjs.event.register.builder;

import dev.celestiacraft.fsjs.FunctionalStorageJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

@Getter
public class DrawerBuilder {
	private final String name;
	private final String namespace;
	private ResourceLocation log;
	private ResourceLocation planks;
	private ResourceLocation frontTexture;
	private ResourceLocation sideTexture;

	private DrawerBuilder(String namespace, String name) {
		this.namespace = namespace;
		this.name = name;
	}

	public static DrawerBuilder of(String name) {
		return new DrawerBuilder("kubejs", name);
	}

	public static DrawerBuilder of(String namespace, String name) {
		return new DrawerBuilder(namespace, name);
	}

	@Info("""
			Sets the `log` block used by the drawer
			""")
	public DrawerBuilder log(ResourceLocation id) {
		log = validate(id, "log");
		return this;
	}

	@Info("""
			Sets the planks block used by the drawer
			""")
	public DrawerBuilder planks(ResourceLocation id) {
		planks = validate(id, "planks");
		return this;
	}

	@Info("""
			Sets the base front texture path.
			The `1x1`, `1x2` and `2x2` models use the path with `_1`, `_2` and `_4` appended.
			Defaults to `functional_storage_js:block/<name>_front`,
			which renders as the missing texture when no png is provided.
			""")
	public DrawerBuilder frontTexture(ResourceLocation texture) {
		frontTexture = texture;
		return this;
	}

	@Info("""
			Sets the side texture path.
			Defaults to `functional_storage_js:block/<name>_side`,
			which renders as the missing texture when no png is provided.
			""")
	public DrawerBuilder sideTexture(ResourceLocation texture) {
		sideTexture = texture;
		return this;
	}

	public ResourceLocation getFrontTexture() {
		return frontTexture != null ? frontTexture : FunctionalStorageJS.loadResource("block/" + name + "_front");
	}

	public ResourceLocation getSideTexture() {
		return sideTexture != null ? sideTexture : FunctionalStorageJS.loadResource("block/" + name + "_side");
	}

	private static ResourceLocation validate(ResourceLocation id, String kind) {
		if (id == null) {
			ConsoleJS.STARTUP.warn("Drawer %s block id is null, skipped".formatted(kind));
		}

		return id;
	}
}