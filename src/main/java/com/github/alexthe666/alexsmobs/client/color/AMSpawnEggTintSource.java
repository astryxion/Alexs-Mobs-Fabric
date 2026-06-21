package com.github.alexthe666.alexsmobs.client.color;

import com.github.alexthe666.alexsmobs.item.AMSpawnEggItem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.client.color.item.ItemTintSource;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

public record AMSpawnEggTintSource(int layer) implements ItemTintSource {
    public static final MapCodec<AMSpawnEggTintSource> MAP_CODEC = RecordCodecBuilder.mapCodec(instance -> instance
            .group(Codec.INT.fieldOf("layer").forGetter(AMSpawnEggTintSource::layer))
            .apply(instance, AMSpawnEggTintSource::new));

    @Override
    public int calculate(ItemStack stack, @Nullable ClientLevel level, @Nullable LivingEntity entity) {
        if (stack.getItem() instanceof AMSpawnEggItem egg) {
            return egg.getTintColor(this.layer);
        }
        return -1;
    }

    @Override
    public MapCodec<? extends ItemTintSource> type() {
        return MAP_CODEC;
    }
}
