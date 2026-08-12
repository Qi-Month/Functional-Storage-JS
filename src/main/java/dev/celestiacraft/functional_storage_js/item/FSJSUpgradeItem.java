package dev.celestiacraft.functional_storage_js.item;

import com.buuz135.functionalstorage.item.StorageUpgradeItem;
import com.hrznstudio.titanium.item.BasicItem;
import dev.celestiacraft.functional_storage_js.event.register.builder.UpgradeBuilder;
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

	@Override
	public boolean hasTooltipDetails(BasicItem.Key key) {
		return key == null;
	}

	@Override
	public void addTooltipDetails(BasicItem.Key key, ItemStack stack, List<Component> tooltip, boolean advanced) {
		DecimalFormat format = new DecimalFormat();

		tooltip.add(Component.translatable("storageupgrade.desc.item").withStyle(ChatFormatting.GRAY)
				.append(format.format(builder.getMultiplier())));
		tooltip.add(Component.translatable("storageupgrade.desc.fluid").withStyle(ChatFormatting.GRAY)
				.append(format.format(builder.getFluidMultiplier())));
		tooltip.add(Component.translatable("storageupgrade.desc.range", format.format(builder.getRangeMultiplier()))
				.withStyle(ChatFormatting.GRAY));
		tooltip.addAll(builder.getTooltip());
	}
}