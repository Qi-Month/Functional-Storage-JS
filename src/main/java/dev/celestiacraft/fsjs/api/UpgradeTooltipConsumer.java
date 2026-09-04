package dev.celestiacraft.fsjs.api;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

@FunctionalInterface
public interface UpgradeTooltipConsumer {
	void accept(List<Component> tooltip, TooltipFlag flag, Player player, ItemStack stack);
}