package com.github.alexthe666.citadel.mixin.client;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.client.event.EventRenderSplashText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.SplashRenderer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SplashRenderer.class)
public class SplashRendererMixin {

    @Mutable
    @Shadow
    @Final
    private String splash;

    private int splashTextColor = -1;

    @Inject(
            method = {"Lnet/minecraft/client/gui/components/SplashRenderer;render(Lnet/minecraft/client/gui/GuiGraphics;ILnet/minecraft/client/gui/Font;I)V"},
            remap = CitadelConstants.REMAPREFS,
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;mulPose(Lorg/joml/Quaternionf;)V",
                    shift = At.Shift.BEFORE
            ))
    protected void citadel_preRenderSplashText(GuiGraphics guiGraphics, int width, Font font, int loadProgress, CallbackInfo ci) {
        guiGraphics.pose().pushPose();
        EventRenderSplashText.Pre event = new EventRenderSplashText.Pre(splash, guiGraphics, ((com.github.alexthe666.citadel.mixin.client.MinecraftAccessor) Minecraft.getInstance()).citadel$getTimer().getGameTimeDeltaPartialTick(false), 16776960);
        EventRenderSplashText.Pre.invokePre(event);

        if (event.getResult() == EventRenderSplashText.Result.ALLOW) {
            splash = event.getSplashText();
            splashTextColor = event.getSplashTextColor();
        }
    }

    @Inject(
            method = {"Lnet/minecraft/client/gui/components/SplashRenderer;render(Lnet/minecraft/client/gui/GuiGraphics;ILnet/minecraft/client/gui/Font;I)V"},
            remap = CitadelConstants.REMAPREFS,
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/GuiGraphics;drawCenteredString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;III)V",
                    shift = At.Shift.AFTER
            )
    )
    protected void citadel_postRenderSplashText(GuiGraphics guiGraphics, int width, Font font, int loadProgress, CallbackInfo ci) {
        EventRenderSplashText.Post event = new EventRenderSplashText.Post(splash, guiGraphics, ((com.github.alexthe666.citadel.mixin.client.MinecraftAccessor) Minecraft.getInstance()).citadel$getTimer().getGameTimeDeltaPartialTick(false));
        EventRenderSplashText.Post.invokePost(event);
        guiGraphics.pose().popPose();
    }

    @ModifyConstant(
            method = {"Lnet/minecraft/client/gui/components/SplashRenderer;render(Lnet/minecraft/client/gui/GuiGraphics;ILnet/minecraft/client/gui/Font;I)V"},
            remap = CitadelConstants.REMAPREFS,
            constant = @Constant(intValue = 16776960))
    private int citadel_splashTextColor(int value) {
        return splashTextColor == -1 ? value : splashTextColor;
    }
}
