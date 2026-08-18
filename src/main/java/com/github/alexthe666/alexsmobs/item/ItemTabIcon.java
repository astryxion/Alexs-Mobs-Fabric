package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.item.data.TabIconDisplay;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;

import javax.annotation.Nullable;

public class ItemTabIcon extends ItemInventoryOnly {
    public ItemTabIcon(Item.Properties properties) {
        super(properties);
    }

    public static boolean hasCustomEntityDisplay(ItemStack stack) {
        TabIconDisplay display = stack.get(AMDataComponents.TAB_ICON_DISPLAY);
        return display != null && !display.displayEntityType().isEmpty();
    }

    public static String getCustomDisplayEntityString(ItemStack stack) {
        TabIconDisplay display = stack.get(AMDataComponents.TAB_ICON_DISPLAY);
        return display != null ? display.displayEntityType() : "";
    }

    public static int getDisplayMobFlags(ItemStack stack) {
        TabIconDisplay display = stack.get(AMDataComponents.TAB_ICON_DISPLAY);
        return display != null ? display.displayMobFlags() : 0;
    }

    public static float getDisplayMobScale(ItemStack stack) {
        TabIconDisplay display = stack.get(AMDataComponents.TAB_ICON_DISPLAY);
        return display != null ? display.displayMobScale() : 0.0f;
    }

    @Nullable
    public static EntityType getEntityType(ItemStack stack) {
        TabIconDisplay display = stack.get(AMDataComponents.TAB_ICON_DISPLAY);
        if (display != null && !display.displayEntityType().isEmpty()) {
            return BuiltInRegistries.ENTITY_TYPE.get(Identifier.tryParse(display.displayEntityType())).map(Holder::value).orElse(null);
        }
        return null;
    }

    // Legacy method for compatibility - reads from CompoundTag (used by renderer)
    @Nullable
    public static EntityType getEntityType(@Nullable CompoundTag tag) {
        if (tag != null && tag.contains("DisplayEntityType")) {
            String entityType = tag.getStringOr("DisplayEntityType", "");
            if (!entityType.isEmpty()) {
                return BuiltInRegistries.ENTITY_TYPE.get(Identifier.tryParse(entityType)).map(Holder::value).orElse(null);
            }
        }
        return null;
    }
}
