package dev.celestiacraft.fsjs.api;

import com.buuz135.functionalstorage.block.tile.ControllableDrawerTile;

public class UpgradeContext {
	private static final ThreadLocal<ControllableDrawerTile<?>> CURRENT_TILE = new ThreadLocal<>();

	public static void setCurrentTile(ControllableDrawerTile<?> tile) {
		CURRENT_TILE.set(tile);
	}

	public static ControllableDrawerTile<?> currentTile() {
		return CURRENT_TILE.get();
	}
}