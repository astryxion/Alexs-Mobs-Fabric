package com.github.alexthe666.citadel.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.Identifier;
import net.minecraft.util.ARGB;

/**
 * Book texture draws for {@link GuiGraphicsExtractor} (26.1); replaces legacy {@code GuiGraphics} + immediate-mode path.
 */
public final class BookBlit {

    private BookBlit() {
    }

    public static void blitWithColor(GuiGraphicsExtractor guiGraphics, Identifier texture, int destX, int destY, int srcU, int srcV, int destW, int destH, int texW, int texH, int r, int g, int b, int a) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, destX, destY, (float) srcU, (float) srcV, destW, destH, texW, texH, ARGB.color(a, r, g, b));
    }

    public static void blitWithColor(GuiGraphicsExtractor guiGraphics, Identifier texture, int destX, int destY, float srcU, float srcV, int destW, int destH, int texW, int texH, int r, int g, int b, int a) {
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, texture, destX, destY, srcU, srcV, destW, destH, texW, texH, ARGB.color(a, r, g, b));
    }

    /**
     * Book page arrows in {@code widgets.png}: region top-left (uOffset, vOffset), 18×12 — 26.1 atlas uses u=0 base (1.21.1 used u=100).
     */
    public static void blitBookArrow(GuiGraphicsExtractor guiGraphics, Identifier texture, int destX, int destY, int uOffset, int vOffset, int r, int g, int b, int a) {
        blitWithColor(guiGraphics, texture, destX, destY, uOffset, vOffset, 18, 12, 256, 256, r, g, b, a);
    }
}
