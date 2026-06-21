package com.github.alexthe666.citadel.mixin.client;

import com.github.alexthe666.citadel.client.rewards.CitadelCapes;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.ClientAsset;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {
    public AbstractClientPlayerMixin(Level level, GameProfile gameProfile) {
        super(level, gameProfile);
    }

    @ModifyReturnValue(method = "getSkin", at = @At("TAIL"))
    private PlayerSkin citadel_getSkin(PlayerSkin original) {
        return Optional.ofNullable(CitadelCapes.getCurrentCape(this))
                .map(CitadelCapes.Cape::getTexture)
                .map(capeTexture -> {
                    ClientAsset.Texture capeAsset = new ClientAsset.Texture() {
                        @Override
                        public Identifier id() {
                            return capeTexture;
                        }

                        @Override
                        public Identifier texturePath() {
                            return capeTexture;
                        }
                    };
                    return new PlayerSkin(
                            original.body(),
                            capeAsset,
                            original.elytra(),
                            original.model(),
                            original.secure());
                })
                .orElse(original);
    }
}
