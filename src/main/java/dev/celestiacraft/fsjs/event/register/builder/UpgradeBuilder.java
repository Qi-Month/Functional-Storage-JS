package dev.celestiacraft.fsjs.event.register.builder;

import com.buuz135.functionalstorage.block.config.FunctionalStorageConfig;
import com.buuz135.functionalstorage.item.StorageUpgradeItem;
import dev.celestiacraft.fsjs.api.UpgradeTooltipConsumer;
import dev.latvian.mods.kubejs.typings.Info;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;

@Getter
public class UpgradeBuilder {
	private final ResourceLocation id;
	private int multiplier = -1;
	private double fluidMultiplier = -1;
	private double rangeMultiplier = -1;
	private ResourceLocation texture;
	private UpgradeTooltipConsumer tooltipConsumer;
	private boolean foil = false;
	private boolean addToTab = true;

	private UpgradeBuilder(ResourceLocation id) {
		this.id = id;
	}

	public static UpgradeBuilder of(ResourceLocation id) {
		return new UpgradeBuilder(id);
	}

	@Info("""
			Sets the item storage multiplier.
			Defaults to the tier multiplier from the Functional Storage config.
			
			Sets the item storage multiplier.
			Defaults to the tier multiplier from the Functional Storage config.
			""")
	public UpgradeBuilder multiplier(int multiplier) {
		this.multiplier = multiplier;
		return this;
	}

	@Info("""
			Sets the fluid multiplier used by fluid drawers.
			Defaults to `multiplier / 2`, which is what the vanilla storage upgrades do.
			""")
	public UpgradeBuilder fluidMultiplier(double multiplier) {
		fluidMultiplier = multiplier;
		return this;
	}

	@Info("""
			Sets the controller range bonus added by this upgrade.
			Defaults to `multiplier / 4`, which is what the vanilla storage upgrades do.
			""")
	public UpgradeBuilder rangeMultiplier(double multiplier) {
		rangeMultiplier = multiplier;
		return this;
	}

	@Info("""
			Sets the item icon texture path, Defaults to `<namespace>:item/upgrade/<name>`.
			""")
	public UpgradeBuilder texture(ResourceLocation texture) {
		this.texture = texture;
		return this;
	}

	@Info("""
			Adds extra tooltip lines.
			The consumer is invoked every time the item tooltip renders, with the actual
			tooltip list, TooltipFlag, Player and ItemStack.
			""")
	public UpgradeBuilder tooltip(UpgradeTooltipConsumer consumer) {
		tooltipConsumer = consumer;
		return this;
	}

	@Info("""
			Whether the item shows the enchantment glint (default false)
			""")
	public UpgradeBuilder foil(boolean foil) {
		this.foil = foil;
		return this;
	}

	@Info("""
			Whether the item is added to the Functional Storage creative tab (default true)
			""")
	public UpgradeBuilder addToTab(boolean addToTab) {
		this.addToTab = addToTab;
		return this;
	}

	public int getMultiplier() {
		return multiplier < 0 ? FunctionalStorageConfig.getLevelMult(StorageUpgradeItem.StorageTier.DIAMOND.getLevel()) : multiplier;
	}

	public double getFluidMultiplier() {
		return fluidMultiplier < 0 ? getMultiplier() / 2.0 : fluidMultiplier;
	}

	public double getRangeMultiplier() {
		return rangeMultiplier < 0 ? getMultiplier() / 4.0 : rangeMultiplier;
	}

	public ResourceLocation getTexture() {
		return texture != null ? texture : ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/upgrade/" + baseName());
	}

	public boolean addToTab() {
		return addToTab;
	}

	public String baseName() {
		String path = id.getPath();
		return path.endsWith("_upgrade")
				? path.substring(0, path.length() - "_upgrade".length())
				: path;
	}
}