package com.github.alexthe666.citadel.mixin.client;

import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.client.event.EventRenderSplashText;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.SplashRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.TriState;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * 26.1 {@link SplashRenderer#extractRenderState} calls {@code ActiveTextCollector.accept(...)} with a different
 * signature than older versions; {@code @ModifyArg} on that invoke no longer matches a handler shaped like the
 * outer method. We redirect only the second {@code splash} field read (the one passed to {@code accept}) so width
 * measurement still uses the original field.
 */
@Mixin(SplashRenderer.class)
public class SplashRendererMixin {

    @Shadow
    @Final
    private Component splash;

    @Unique
    private GuiGraphicsExtractor citadel$splashGraphics;

    @Inject(
            method = "extractRenderState",
            remap = CitadelConstants.REMAPREFS,
            at = @At("HEAD"))
    private void citadel$captureGraphics(GuiGraphicsExtractor graphics, int width, Font font, float fade, CallbackInfo ci) {
        this.citadel$splashGraphics = graphics;
    }

    @Redirect(
            method = "extractRenderState",
            remap = CitadelConstants.REMAPREFS,
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD,
                    ordinal = 1,
                    target = "Lnet/minecraft/client/gui/components/SplashRenderer;splash:Lnet/minecraft/network/chat/Component;"))
    private Component citadel_modifySplash(SplashRenderer instance) {
        Component splashIn = this.splash;
        Minecraft mc = Minecraft.getInstance();
        EventRenderSplashText.Pre event = new EventRenderSplashText.Pre(
                splashIn,
                this.citadel$splashGraphics,
                mc.getDeltaTracker().getRealtimeDeltaTicks(),
                16776960);
        event.post();
        if (event.getResult() == TriState.TRUE) {
            MutableComponent out = event.getSplashText().copy();
            int c = event.getSplashTextColor();
            if (c != 16776960) {
                out = out.withColor(c);
            }
            return out;
        }
        return splashIn;
    }

    @Inject(
            method = "extractRenderState",
            remap = CitadelConstants.REMAPREFS,
            at = @At("RETURN"))
    private void citadel_postSplash(GuiGraphicsExtractor graphics, int width, Font font, float fade, CallbackInfo ci) {
        Minecraft mc = Minecraft.getInstance();
        new EventRenderSplashText.Post(this.splash, graphics, mc.getDeltaTracker().getRealtimeDeltaTicks()).post();
    }
}
