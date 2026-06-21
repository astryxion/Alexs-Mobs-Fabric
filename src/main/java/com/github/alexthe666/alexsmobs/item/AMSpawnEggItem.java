package com.github.alexthe666.alexsmobs.item;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SpawnEggItem;

/**
 * Spawn egg with the same base / spot RGB pair as 1.21.1 {@code DeferredSpawnEggItem}.
 * Tinting is applied client-side via {@code alexsmobs:spawn_egg_layer} on the vanilla two-layer model.
 */
public class AMSpawnEggItem extends SpawnEggItem {
    private final int backgroundColor;
    private final int highlightColor;

    public AMSpawnEggItem(Item.Properties properties, int backgroundColor, int highlightColor) {
        super(properties);
        this.backgroundColor = 0xFF000000 | (backgroundColor & 0xFFFFFF);
        this.highlightColor = 0xFF000000 | (highlightColor & 0xFFFFFF);
    }

    public int getTintColor(int layer) {
        return layer == 0 ? this.backgroundColor : this.highlightColor;
    }
}
