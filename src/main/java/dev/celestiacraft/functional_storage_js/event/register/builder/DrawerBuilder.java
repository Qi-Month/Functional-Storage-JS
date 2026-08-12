package dev.celestiacraft.functional_storage_js.event.register.builder;

import dev.celestiacraft.functional_storage_js.FunctionalStorageJS;
import dev.latvian.mods.kubejs.typings.Info;
import dev.latvian.mods.kubejs.util.ConsoleJS;
import lombok.Getter;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

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
			
			设置抽屉所使用的`原木`方块
			""")
	public DrawerBuilder log(Block block) {
		log = getBlockId(block, "log");
		return this;
	}

	@Info("""
			Sets the planks block used by the drawer
			
			设置抽屉所使用的`木板`方块
			""")
	public DrawerBuilder planks(Block block) {
		planks = getBlockId(block, "planks");
		return this;
	}

	@Info("""
			Sets the base front texture path.
			The `1x1`, `1x2` and `2x2` models use the path with `_1`, `_2` and `_4` appended.
			Defaults to `functional_storage_js:block/<name>_front`,
			which renders as the missing texture when no png is provided.
			
			设置基础正面纹理路径
			`1x1`、`1x2` 和 `2x2` 模型会分别在该路径后追加 `_1`、`_2` 和 `_4`.
			默认为 `functional_storage_js:block/<name>_front`,
			如果没有提供对应的 png 文件, 则会显示为缺失纹理.
			""")
	public DrawerBuilder frontTexture(ResourceLocation texture) {
		frontTexture = texture;
		return this;
	}

	@Info("""
			Sets the side texture path.
			Defaults to `functional_storage_js:block/<name>_side`,
			which renders as the missing texture when no png is provided.
			
			设置侧面纹理路径.
			默认为 `functional_storage_js:block/<name>_side`,
			如果没有提供对应的 png 文件，则会显示为缺失纹理.
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

	private static ResourceLocation parse(String id, String kind) {
		if (id == null || id.isBlank()) {
			ConsoleJS.STARTUP.warn("Drawer %s block id is empty, skipped".formatted(kind));
			return null;
		}

		try {
			return ResourceLocation.parse(id);
		} catch (ResourceLocationException exception) {
			ConsoleJS.STARTUP.warn("Drawer %s block id is invalid: " + id, exception);
			return null;
		}
	}

	private static ResourceLocation getBlockId(Block block, String kind) {
		ResourceLocation id = ForgeRegistries.BLOCKS.getKey(block);

		if (id == null) {
			ConsoleJS.STARTUP.warn("Could not resolve the id of %s block: %s".formatted(kind, block));
		}

		return id;
	}
}