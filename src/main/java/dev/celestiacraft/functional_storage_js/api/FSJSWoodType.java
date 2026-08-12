package dev.celestiacraft.functional_storage_js.api;

import com.buuz135.functionalstorage.util.IWoodType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.ForgeRegistries;

/**
 * A drawer wood type registered from KubeJS startup scripts.
 * <p>
 * Wood/planks are resolved lazily from the block registry on every call, so the ids are valid even
 * though the startup scripts run before block registration happens.
 * <p>
 * 从 KubeJS 启动脚本中注册的抽屉木材类型。
 * <p>
 * 木材/木板会在每次调用时从方块注册表中延迟解析，因此即使启动脚本运行时方块注册尚未完成，
 * 这些 ID 仍然有效。
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
		return ForgeRegistries.BLOCKS.getValue(wood);
	}

	@Override
	public Block getPlanks() {
		return ForgeRegistries.BLOCKS.getValue(planks);
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