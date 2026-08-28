package com.github.alexthe666.alexsmobs.client.render;

import com.google.common.collect.Maps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Map;

public final class AMArmorLayerUtil {
    private static final Map<String, ResourceLocation> ARMOR_TEXTURE_RES_MAP = Maps.newHashMap();

    private AMArmorLayerUtil() {
    }

    public static ResourceLocation getArmorResource(ItemStack stack, @Nullable String overlaySuffix) {
        ArmorItem item = (ArmorItem) stack.getItem();
        String texture = item.getMaterial().getName();
        String domain = "minecraft";
        int idx = texture.indexOf(':');
        if (idx != -1) {
            domain = texture.substring(0, idx);
            texture = texture.substring(idx + 1);
        }
        String key = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, 1,
                overlaySuffix == null ? "" : String.format("_%s", overlaySuffix));
        return ARMOR_TEXTURE_RES_MAP.computeIfAbsent(key, ResourceLocation::new);
    }
}
