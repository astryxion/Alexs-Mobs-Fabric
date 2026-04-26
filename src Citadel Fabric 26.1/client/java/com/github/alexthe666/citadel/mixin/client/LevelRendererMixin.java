package com.github.alexthe666.citadel.mixin.client;

import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Final
    @Shadow
    private Minecraft minecraft;

    @Inject(method = "initOutline", at = @At("TAIL"))
    private void citadel_initOutline(CallbackInfo ci) {
        PostEffectRegistry.onInitializeOutline();
    }

    @Inject(method = "resize", at = @At("TAIL"))
    private void citadel_resize(int x, int y, CallbackInfo ci) {
        PostEffectRegistry.resize(x, y);
    }
}
