package dev.celestiacraft.fsjs.mixin;

import com.buuz135.functionalstorage.block.tile.ControllableDrawerTile;
import dev.celestiacraft.fsjs.api.UpgradeContext;
import dev.celestiacraft.fsjs.common.item.FSJSUpgradeItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Functional Storage 在 {@code maybeCacheUpgrades()} 中计算升级倍率时
 * <p>
 * 只会读取 {@code StorageUpgradeItem#getStorageMultiplier()},
 * <p>
 * 但该方法无法获取升级所在的方块
 *
 * <p>
 * 因此, 在缓存升级倍率前, 将当前方块写入 {@link UpgradeContext},
 * 由 {@link FSJSUpgradeItem} 根据方块类型返回对应倍率:
 * <ul>
 *     <li>流体抽屉: 流体倍率 × 2</li>
 *     <li>控制器: 范围倍率 × 4</li>
 *     <li>其他方块: 原始存储倍率</li>
 * </ul>
 *
 * <p>
 * 使用 {@code ThreadLocal} 而不是 {@code @Redirect},
 * 是因为 {@code Storage Tweaks} 的 additive 模式会在 {@code maybeCacheUpgrades()} 开头取消原方法,
 * <p>
 * 使 {@code @Redirect} 修改的原方法体无法执行
 * <p>
 * {@code ThreadLocal} 不依赖 Mixin 的应用顺序, 因此两种计算路径都可以正确获取当前方块
 */
@Mixin(value = ControllableDrawerTile.class, remap = false)
public class ControllableDrawerTileMixin {
	@Inject(method = "getStorageMultiplier", at = @At("HEAD"))
	private void fsjs$captureTile(CallbackInfoReturnable<Integer> cir) {
		UpgradeContext.setCurrentTile((ControllableDrawerTile<?>) (Object) this);
	}

	@Inject(method = "isVoid", at = @At("HEAD"))
	private void fsjs$captureTileVoid(CallbackInfoReturnable<Boolean> cir) {
		UpgradeContext.setCurrentTile((ControllableDrawerTile<?>) (Object) this);
	}

	@Inject(method = "isCreative", at = @At("HEAD"))
	private void fsjs$captureTileCreative(CallbackInfoReturnable<Boolean> cir) {
		UpgradeContext.setCurrentTile((ControllableDrawerTile<?>) (Object) this);
	}
}