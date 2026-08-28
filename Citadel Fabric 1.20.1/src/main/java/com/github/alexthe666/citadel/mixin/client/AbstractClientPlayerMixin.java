package com.github.alexthe666.citadel.mixin.client;

import com.github.alexthe666.citadel.client.rewards.CitadelCapes;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {

    public AbstractClientPlayerMixin(Level level, BlockPos pos, float yRot, GameProfile profile) {
        super(level, pos, yRot, profile);
    }

    @Inject(at = @At("RETURN"), method = "getCloakTextureLocation", cancellable = true)
    private void citadel_getCloakTextureLocation(CallbackInfoReturnable<ResourceLocation> cir) {
        CitadelCapes.Cape cape = CitadelCapes.getCurrentCape(this);
        if (cape != null) {
            cir.setReturnValue(cape.getTexture());
        }
    }
}
