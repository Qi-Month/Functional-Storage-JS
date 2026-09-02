package dev.celestiacraft.fsjs.common.item;

import com.buuz135.functionalstorage.item.StorageUpgradeItem;
import com.hrznstudio.titanium.item.BasicItem;
import dev.celestiacraft.fsjs.event.register.builder.UpgradeBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import java.text.DecimalFormat;
import java.util.List;

public class FSJSUpgradeItem extends StorageUpgradeItem {
	private final UpgradeBuilder builder;

	public FSJSUpgradeItem(UpgradeBuilder builder) {
		super(StorageUpgradeItem.StorageTier.MAX_STORAGE);
		this.builder = builder;
	}

	@Override
	public int getStorageMultiplier() {
		return builder.getMultiplier();
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return builder.isFoil();
	}

	/**
	 * 物品抽屉使用的倍率({@link getStorageMultiplier} 已经返回该倍率)
	 * <p>
	 * 在此处提供该值, 以便 Mixin 能与流体倍率和范围倍率一起读取
	 */
	public double getFluidMultiplier() {
		return builder.getFluidMultiplier();
	}

	/**
	 * 此升级为抽屉控制器增加的范围加成(以方块为单位)
	 * <p>
	 * 在此处提供该值, 以便 Mixin 读取
	 */
	public double getRangeMultiplier() {
		return builder.getRangeMultiplier();
	}

	@Override
	public boolean hasTooltipDetails(BasicItem.Key key) {
		return key == null;
	}

	@Override
	public void addTooltipDetails(BasicItem.Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
		DecimalFormat format = new DecimalFormat();

		tooltip.add(Component.translatable("storageupgrade.desc.item").withStyle(ChatFormatting.GRAY)
				.append(format.format(getStorageMultiplier())));
		tooltip.add(Component.translatable("storageupgrade.desc.fluid").withStyle(ChatFormatting.GRAY)
				.append(format.format(getFluidMultiplier())));
		tooltip.add(Component.translatable("storageupgrade.desc.range", format.format(getRangeMultiplier()))
				.withStyle(ChatFormatting.GRAY));
		tooltip.addAll(builder.getTooltip());
	}
}