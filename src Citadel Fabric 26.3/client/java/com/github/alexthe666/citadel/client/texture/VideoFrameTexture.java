package com.github.alexthe666.citadel.client.texture;

import com.mojang.blaze3d.platform.NativeImage;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

import java.awt.image.BufferedImage;

public class VideoFrameTexture extends DynamicTexture {

    private final NativeImage backing;

    public VideoFrameTexture(Identifier textureId, NativeImage image) {
        super(textureId::toString, image);
        this.backing = image;
    }

    public void setPixels(NativeImage nativeImage) {
        int w = Math.min(this.backing.getWidth(), nativeImage.getWidth());
        int h = Math.min(this.backing.getHeight(), nativeImage.getHeight());
        for (int x = 0; x < w; x++) {
            for (int y = 0; y < h; y++) {
                this.backing.setPixel(x, y, nativeImage.getPixel(x, y));
            }
        }
        this.upload();
    }

    public void setPixelsFromBufferedImage(BufferedImage bufferedImage) {
        for (int i = 0; i < Math.min(this.backing.getWidth(), bufferedImage.getWidth()); i++) {
            for (int j = 0; j < Math.min(this.backing.getHeight(), bufferedImage.getHeight()); j++) {
                int color = bufferedImage.getRGB(i, j);
                int r = color >> 16 & 255;
                int g = color >> 8 & 255;
                int b = color & 255;
                int a = color >> 24 & 255;
                this.backing.setPixel(i, j, ARGB.color(a, r, g, b));
            }
        }
        this.upload();
    }
}
