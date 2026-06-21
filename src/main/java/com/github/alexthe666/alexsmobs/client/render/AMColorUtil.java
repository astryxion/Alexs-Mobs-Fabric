package com.github.alexthe666.alexsmobs.client.render;

import net.minecraft.util.ARGB;

public final class AMColorUtil {
    private AMColorUtil() {
    }

    public static int packColor(float r, float g, float b, float a) {
        return ARGB.colorFromFloat(a, r, g, b);
    }
}
