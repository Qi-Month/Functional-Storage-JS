package dev.celestiacraft.fsjs.common.item;

import com.buuz135.functionalstorage.block.tile.ControllableDrawerTile;
import com.buuz135.functionalstorage.block.tile.FluidDrawerTile;
import com.buuz135.functionalstorage.block.tile.StorageControllerTile;
import com.buuz135.functionalstorage.item.StorageUpgradeItem;
import com.hrznstudio.titanium.item.BasicItem;
import dev.celestiacraft.fsjs.FunctionalStorageJS;
import dev.celestiacraft.fsjs.api.UpgradeContext;
import dev.celestiacraft.fsjs.api.UpgradeTooltipConsumer;
import dev.celestiacraft.fsjs.event.register.builder.UpgradeBuilder;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.text.DecimalFormat;
import java.util.List;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = FunctionalStorageJS.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class FSJSUpgradeItem extends StorageUpgradeItem {
	private final UpgradeBuilder builder;

	public FSJSUpgradeItem(UpgradeBuilder builder) {
		super(StorageUpgradeItem.StorageTier.MAX_STORAGE);
		this.builder = builder;
	}

	@SubscribeEvent
	public static void onItemTooltip(ItemTooltipEvent event) {
		ItemStack stack = event.getItemStack();
		List<Component> tooltip = event.getToolTip();
		TooltipFlag flags = event.getFlags();
		Player player = event.getEntity();

		if (!(stack.getItem() instanceof FSJSUpgradeItem upgrade)) {
			return;
		}

		UpgradeTooltipConsumer consumer = upgrade.builder.getTooltipConsumer();

		if (consumer == null) {
			return;
		}

		consumer.accept(tooltip, flags, player, stack);
	}

	public UpgradeTooltipConsumer getTooltipConsumer() {
		return builder.getTooltipConsumer();
	}

	@Override
	public int getStorageMultiplier() {
		ControllableDrawerTile<?> tile = UpgradeContext.currentTile();

		if (tile == null) {
			return builder.getMultiplier();
		}

		double divisor = tile.getStorageDiv();

		if (tile instanceof FluidDrawerTile) {
			return (int) Math.round(builder.getFluidMultiplier() * divisor);
		}

		if (tile instanceof StorageControllerTile) {
			return (int) Math.round(builder.getRangeMultiplier() * divisor);
		}

		return builder.getMultiplier();
	}

	@Override
	public boolean isFoil(ItemStack stack) {
		return builder.isFoil();
	}

	public double getFluidMultiplier() {
		return builder.getFluidMultiplier();
	}

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
				.append(format.format(builder.getMultiplier())));
		tooltip.add(Component.translatable("storageupgrade.desc.fluid").withStyle(ChatFormatting.GRAY)
				.append(format.format(getFluidMultiplier())));
		tooltip.add(Component.translatable("storageupgrade.desc.range", format.format(getRangeMultiplier()))
				.withStyle(ChatFormatting.GRAY));
	}
}