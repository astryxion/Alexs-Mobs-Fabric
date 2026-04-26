package com.github.alexthe666.citadel.client.texture;

import com.mojang.blaze3d.platform.NativeImage;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.renderer.texture.SimpleTexture;
import net.minecraft.client.renderer.texture.TextureContents;
import net.minecraft.client.resources.metadata.texture.TextureMetadataSection;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.ARGB;
import javax.annotation.Nullable;
import java.io.IOException;
import java.io.InputStream;

public class ColorMappedTexture extends SimpleTexture {

    private final int[] colors;

    public ColorMappedTexture(Identifier textureId, int[] colors) {
        super(textureId);
        this.colors = colors;
    }

    @Override
    public TextureContents loadContents(ResourceManager resourceManager) throws IOException {
        TextureContents loaded = TextureContents.load(resourceManager, this.resourceId());
        NativeImage nativeimage = loaded.image();
        Resource resource = resourceManager.getResource(this.resourceId()).orElseThrow();
        ColorsMetadataSection section = resource.metadata().getSection(ColorsMetadataSection.TYPE).orElse(new ColorsMetadataSection(null));
        if (section.getColorRamp() != null) {
            NativeImage nativeimage2 = getNativeImage(resourceManager, section.getColorRamp());
            if (nativeimage2 != null) {
                try {
                    processColorMap(nativeimage, nativeimage2);
                } finally {
                    nativeimage2.close();
                }
            }
        }
        TextureMetadataSection meta = loaded.metadata();
        return new TextureContents(nativeimage, meta);
    }

    private NativeImage getNativeImage(ResourceManager resourceManager, @Nullable Identifier id) {
        if (id == null) {
            return null;
        }
        try {
            Resource resource = resourceManager.getResourceOrThrow(id);
            try (InputStream inputstream = resource.open()) {
                return NativeImage.read(inputstream);
            }
        } catch (Throwable throwable1) {
            return null;
        }
    }

    private void processColorMap(NativeImage nativeImage, NativeImage colorMap) {
        int[] fromColorMap = new int[colorMap.getHeight()];
        for (int i = 0; i < fromColorMap.length; i++) {
            fromColorMap[i] = colorMap.getPixel(0, i);
        }
        for (int i = 0; i < nativeImage.getWidth(); i++) {
            for (int j = 0; j < nativeImage.getHeight(); j++) {
                int colorAt = nativeImage.getPixel(i, j);
                if (ARGB.alpha(colorAt) == 0) {
                    continue;
                }
                int replaceIndex = -1;
                for (int k = 0; k < fromColorMap.length; k++) {
                    if (colorAt == fromColorMap[k]) {
                        replaceIndex = k;
                    }
                }
                if (replaceIndex >= 0 && colors.length > replaceIndex) {
                    int r = colors[replaceIndex] >> 16 & 255;
                    int g = colors[replaceIndex] >> 8 & 255;
                    int b = colors[replaceIndex] & 255;
                    nativeImage.setPixel(i, j, ARGB.color(ARGB.alpha(colorAt), r, g, b));
                }
            }
        }
    }

    private static class ColorsMetadataSection {
        public static final Codec<ColorsMetadataSection> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(Identifier.CODEC.fieldOf("color_ramp").forGetter(ColorsMetadataSection::getColorRamp))
                        .apply(instance, ColorsMetadataSection::new));
        public static final MetadataSectionType<ColorsMetadataSection> TYPE = new MetadataSectionType<>("colors", CODEC);

        private final Identifier colorRamp;

        public ColorsMetadataSection(Identifier colorRamp) {
            this.colorRamp = colorRamp;
        }

        public Identifier getColorRamp() {
            return colorRamp;
        }
    }
}
