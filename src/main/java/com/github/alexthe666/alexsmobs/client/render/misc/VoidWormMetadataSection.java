package com.github.alexthe666.alexsmobs.client.render.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.packs.metadata.MetadataSectionType;

/**
 * Texture {@code .mcmeta} section {@code void_worm} (boolean {@code end_portal_texture}).
 */
public record VoidWormMetadataSection(boolean endPortalTexture) {

    public static final Codec<VoidWormMetadataSection> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                            Codec.BOOL.fieldOf("end_portal_texture").forGetter(VoidWormMetadataSection::endPortalTexture))
                    .apply(instance, VoidWormMetadataSection::new));

    public static final MetadataSectionType<VoidWormMetadataSection> TYPE = new MetadataSectionType<>("void_worm", CODEC);

    public boolean isEndPortalTexture() {
        return this.endPortalTexture;
    }
}
