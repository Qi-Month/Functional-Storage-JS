package dev.celestiacraft.functional_storage_js.event.register.builder;

import com.buuz135.functionalstorage.block.config.FunctionalStorageConfig;
import com.buuz135.functionalstorage.item.StorageUpgradeItem;
import dev.latvian.mods.kubejs.typings.Info;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

@Getter
public class UpgradeBuilder {
	private final ResourceLocation id;
	private int multiplier = -1;
	private double fluidMultiplier = -1;
	private double rangeMultiplier = -1;
	private ResourceLocation texture;
	private final List<Component> tooltip = new ArrayList<>();
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
			""")
	public UpgradeBuilder multiplier(int multiplier) {
		this.multiplier = multiplier;
		return this;
	}

	@Info("""
			Sets the fluid storage multiplier shown in the tooltip, Defaults to the item `multiplier / 2`.
			""")
	public UpgradeBuilder fluidMultiplier(double multiplier) {
		fluidMultiplier = multiplier;
		return this;
	}

	@Info("""
			Sets the controller radius multiplier shown in the tooltip, Defaults to the `fluid multiplier / 2`.
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
			Adds extra tooltip lines
			""")
	public UpgradeBuilder tooltip(Consumer<List<Component>> consumer) {
		consumer.accept(tooltip);
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
		return rangeMultiplier < 0 ? getFluidMultiplier() / 2.0 : rangeMultiplier;
	}

	public ResourceLocation getTexture() {
		return texture != null ? texture : ResourceLocation.fromNamespaceAndPath(id.getNamespace(), "item/upgrade/" + baseName());
	}

	public boolean addToTab() {
		return addToTab;
	}

	public String baseName() {
		String path = id.getPath();
		return path.endsWith("_upgrade") ? path.substring(0, path.length() - "_upgrade".length()) : path;
	}
}