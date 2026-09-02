package dev.celestiacraft.fsjs.mixin;

import com.buuz135.functionalstorage.block.tile.ControllableDrawerTile;
import com.buuz135.functionalstorage.block.tile.FluidDrawerTile;
import com.buuz135.functionalstorage.block.tile.StorageControllerTile;
import com.buuz135.functionalstorage.item.StorageUpgradeItem;
import dev.celestiacraft.fsjs.common.item.FSJSUpgradeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/**
 * Functional Storage 本身没有独立的流体/范围倍率:
 * {@code ControllableDrawerTile#maybeCacheUpgrades} 用
 * {@code item.getStorageMultiplier() / getStorageDiv()} 计算倍率
 * (普通抽屉 getStorageDiv()=1, 流体抽屉=FLUID_DIVISOR(2), 控制器=RANGE_DIVISOR(4)).
 * <p>
 * 这里把 maybeCacheUpgrades 里的 getStorageMultiplier() 调用重定向到我们自己:
 * - 流体抽屉: 让结果恰好等于升级自带的流体倍率
 * - 控制器: 让范围加成恰好等于升级自带的范围倍率
 * - 其他(物品抽屉): 维持原样
 * <p>
 * 由于 target 是 mod 自身声明的类/方法(不会被 SRG 混淆), 无需 refmap 条目。
 */
@Mixin(value = ControllableDrawerTile.class, remap = false)
public class ControllableDrawerTileMixin {
	@Redirect(
			remap = false,
			method = "maybeCacheUpgrades",
			at = @At(
					value = "INVOKE",
					target = "Lcom/buuz135/functionalstorage/item/StorageUpgradeItem;getStorageMultiplier()I"
			)
	)
	private int fsjs$redirectUpgradeMultiplier(StorageUpgradeItem upgrade) {
		int original = upgrade.getStorageMultiplier();

		if (!(upgrade instanceof FSJSUpgradeItem custom)) {
			return original;
		}

		ControllableDrawerTile<?> tile = (ControllableDrawerTile<?>) (Object) this;
		double divisor = tile.getStorageDiv();

		if (tile instanceof FluidDrawerTile) {
			/*
			 * maybeCacheUpgrades 之后会执行 itemMultiplier / divisor,
			 * 这里把除数乘回去, 使最终流体倍率恰好等于自定义值
			 */
			return (int) Math.round(custom.getFluidMultiplier() * divisor);
		}

		if (tile instanceof StorageControllerTile) {
			// 同上: 每个升级对控制器链接范围的加成恰好等于自定义范围倍率
			return (int) Math.round(custom.getRangeMultiplier() * divisor);
		}

		return original;
	}
}