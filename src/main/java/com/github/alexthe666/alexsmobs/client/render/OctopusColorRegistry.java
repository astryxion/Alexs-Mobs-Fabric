package com.github.alexthe666.alexsmobs.client.render;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import java.awt.*;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
public class OctopusColorRegistry {

    public static final BlockState FALLBACK_BLOCK = Blocks.SAND.defaultBlockState();
    public static Object2IntMap<String> TEXTURES_TO_COLOR = new Object2IntOpenHashMap<>();;

    public static int getBlockColor(BlockState stack) {
        String blockName = stack.toString();
        if (TEXTURES_TO_COLOR.containsKey(blockName)) {
            return TEXTURES_TO_COLOR.getInt(blockName);
        } else {
            int colorizer = -1;
            int color = 0XFFFFFF;
            if (colorizer == -1) {
                try {
                    Color texColour = getAverageColour(getTextureAtlas(stack));
                    color = texColour.getRGB();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            } else {
                color = colorizer;
            }
            TEXTURES_TO_COLOR.put(blockName, color);
            return color;
        }
    }

    private static Color getAverageColour(TextureAtlasSprite image) {
        float red = 0;
        float green = 0;
        float blue = 0;
        float count = 0;
        int uMax = image.contents().width();
        int vMax = image.contents().height();
        for (float i = 0; i < uMax; i++)
            for (float j = 0; j < vMax; j++) {
                int pixel = getPixelRgba(image, (int) i, (int) j);
                int alpha = pixel >> 24 & 0xFF;
                if (alpha == 0) {
                    continue;
                }
                red += pixel >> 0 & 0xFF;
                green += pixel >> 8 & 0xFF;
                blue += pixel >> 16 & 0xFF;
                count++;
            }
        //Average color
        if (count == 0) {
            return Color.WHITE;
        }
        return new Color((int) (red / count), (int) (green / count), (int) (blue / count));
    }

    private static int getPixelRgba(TextureAtlasSprite sprite, int x, int y) {
        try {
            Field originalImageField = sprite.contents().getClass().getDeclaredField("originalImage");
            originalImageField.setAccessible(true);
            Object nativeImage = originalImageField.get(sprite.contents());
            Method getPixel = nativeImage.getClass().getMethod("getPixel", int.class, int.class);
            return (int) getPixel.invoke(nativeImage, x, y);
        } catch (ReflectiveOperationException ex) {
            return 0xFFFFFFFF;
        }
    }

    private static TextureAtlasSprite getTextureAtlas(BlockState state) {
        return Minecraft.getInstance().getModelManager().getBlockStateModelSet().get(state).particleMaterial().sprite();
    }
}
