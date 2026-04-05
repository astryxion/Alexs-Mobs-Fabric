package com.github.alexthe666.alexsmobs.client.render;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import java.awt.*;
import java.awt.image.BufferedImage;

public class OctopusColorRegistry {

    public static final BlockState FALLBACK_BLOCK = Blocks.SAND.defaultBlockState();
    public static Object2IntMap<String> TEXTURES_TO_COLOR = new Object2IntOpenHashMap<>();;

    public static int getBlockColor(BlockState stack) {
        String blockName = stack.toString();
        if (TEXTURES_TO_COLOR.containsKey(blockName)) {
            return TEXTURES_TO_COLOR.getInt(blockName);
        } else {
            int colorizer = -1;
            try{
                colorizer = Minecraft.getInstance().getBlockColors().getColor(stack, null, null, 0);
            }catch (Exception e){
                AlexsMobs.LOGGER.warn("Another mod did not use block colorizers correctly.");
            }
            int color = 0XFFFFFF;
            if(colorizer == -1){
                BufferedImage texture = null;
                try {
                    Color texColour = getAverageColour(getTextureAtlas(stack));
                    color = texColour.getRGB();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }else{
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
                int pixel = getPixelRGBA(image, 0, (int) i, (int) j);
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
        return new Color((int) (red / count), (int) (green / count), (int) (blue / count));
    }

    /** Fabric 1.20.1: TextureAtlasSprite.getPixelRGBA not in API; use contents() or reflection. */
    private static int getPixelRGBA(TextureAtlasSprite image, int frame, int x, int y) {
        try {
            Object contents = image.contents();
            return (Integer) contents.getClass().getMethod("getPixelRGBA", int.class, int.class, int.class).invoke(contents, frame, x, y);
        } catch (Exception e) {
            try {
                java.lang.reflect.Method m = TextureAtlasSprite.class.getDeclaredMethod("getPixelRGBA", int.class, int.class, int.class);
                m.setAccessible(true);
                return (Integer) m.invoke(image, frame, x, y);
            } catch (Exception e2) { return 0; }
        }
    }

    private static TextureAtlasSprite getTextureAtlas(BlockState state) {
        return Minecraft.getInstance().getBlockRenderer().getBlockModelShaper().getBlockModel(state).getParticleIcon();
    }
}
