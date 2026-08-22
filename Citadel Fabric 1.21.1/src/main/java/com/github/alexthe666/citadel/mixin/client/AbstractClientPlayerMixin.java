package com.github.alexthe666.citadel.mixin.client;

import com.github.alexthe666.citadel.client.rewards.CitadelCapes;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
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

    public AbstractClientPlayerMixin(Level p_250508_, BlockPos p_250289_, float p_251702_, GameProfile p_252153_) {
        super(p_250508_, p_250289_, p_251702_, p_252153_);
    }

    @Inject(at = @At("RETURN"), method = "getSkin", cancellable = true)
    private void citadel_getSkin(CallbackInfoReturnable<PlayerSkin> cir) {
        CitadelCapes.Cape cape = CitadelCapes.getCurrentCape(this);
        if (cape != null) {
            PlayerSkin original = cir.getReturnValue();
            cir.setReturnValue(new PlayerSkin(
                    original.texture(),
                    original.textureUrl(),
                    cape.getTexture(),
                    cape.getTexture(),
                    original.model(),
                    original.secure()
            ));
        }
    }
}
