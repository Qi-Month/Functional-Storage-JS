package dev.celestiacraft.fsjs.api;

import com.buuz135.functionalstorage.util.IWoodType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * A drawer wood type registered from KubeJS startup scripts.
 * <p>
 * The log/planks ids are kept exactly as the script provided them (no registry round-trip at
 * script time) and are resolved from the block registry when the drawer blocks are registered.
 * Resolution uses {@code containsKey} instead of {@code getValue} so a not-yet-registered block
 * returns {@code null} instead of the registry default ({@code minecraft:air}), which would
 * otherwise silently give every drawer air-like block properties.
 * <p>
 * 从 KubeJS 启动脚本中注册的抽屉木材类型
 * <p>
 * 原木/木板 id 会原样保留脚本传入的值(不在脚本阶段查询注册表), 并在抽屉方块注册时从方块注册表解析
 * 解析使用 {@code containsKey} 而不是 {@code getValue}, 因此尚未注册的方块会返回 {@code null},
 * 而不是注册表默认值({@code minecraft:air}), 避免抽屉方块被静默地赋予空气方块属性
 * <p>
 */
public class FSJSWoodType implements IWoodType {
	private final String name;
	private final ResourceLocation wood;
	private final ResourceLocation planks;

	private FSJSWoodType(String name, ResourceLocation wood, ResourceLocation planks) {
		this.name = name;
		this.wood = wood;
		this.planks = planks;
	}

	public static FSJSWoodType of(String name, ResourceLocation wood, ResourceLocation planks) {
		return new FSJSWoodType(name, wood, planks);
	}

	@Override
	public Block getWood() {
		return resolve(wood);
	}

	@Override
	public Block getPlanks() {
		return resolve(planks);
	}

	private static Block resolve(ResourceLocation id) {
		if (id == null) {
			return null;
		}

		return ForgeRegistries.BLOCKS.containsKey(id)
				? ForgeRegistries.BLOCKS.getValue(id)
				: null;
	}

	@Override
	public String getName() {
		return name;
	}

	public ResourceLocation getWoodId() {
		return wood;
	}

	public ResourceLocation getPlanksId() {
		return planks;
	}

	@Override
	public String toString() {
		return name;
	}
}