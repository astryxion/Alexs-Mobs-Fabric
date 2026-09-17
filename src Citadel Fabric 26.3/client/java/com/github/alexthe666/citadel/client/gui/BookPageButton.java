package com.github.alexthe666.citadel.client.gui;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;

/**
 * Page-turn arrows from {@code citadel:textures/gui/book/widgets.png}.
 * NeoForge 26.1 atlas places arrows at u = 0 + uOffset (idle/hover), v = 0 forward / 13 back, 18×12 (1.21.1 used u = 100).
 */
public class BookPageButton extends Button {
    private final boolean isForward;
    private final boolean playTurnSound;
    private final GuiBasicBook bookGUI;

    public BookPageButton(GuiBasicBook bookGUI, int p_i51079_1_, int p_i51079_2_, boolean p_i51079_3_, OnPress p_i51079_4_, boolean p_i51079_5_) {
        super(p_i51079_1_, p_i51079_2_, 23, 13, CommonComponents.EMPTY, p_i51079_4_, DEFAULT_NARRATION);
        this.isForward = p_i51079_3_;
        this.playTurnSound = p_i51079_5_;
        this.bookGUI = bookGUI;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        if (bookGUI.deferPageArrowsToPostPass()) {
            return;
        }
        int uOffset = this.isHovered ? 23 : 0;
        int vOffset = this.isForward ? 0 : 13;
        this.drawNextArrow(guiGraphics, this.getX(), this.getY(), uOffset, vOffset, 18, 12, this.isHovered);
    }

    /**
     * Second pass from {@link GuiBasicBook#extractRenderState}. Uses {@code mouseX}/{@code mouseY} for hover — deferred
     * extraction may not set {@link #isHovered} before widget {@code extractContents} runs.
     */
    public void blitPageArrowPostPass(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY) {
        if (!this.visible) {
            return;
        }
        boolean hovered = mouseX >= this.getX() && mouseX < this.getX() + this.width && mouseY >= this.getY() && mouseY < this.getY() + this.height;
        int uOffset = hovered ? 23 : 0;
        int vOffset = this.isForward ? 0 : 13;
        this.drawNextArrow(guiGraphics, this.getX(), this.getY(), uOffset, vOffset, 18, 12, hovered);
    }

    public void drawNextArrow(GuiGraphicsExtractor guiGraphics, int destX, int destY, int uOffset, int vOffset, int regionW, int regionH, boolean hoveredForTint) {
        Identifier tex = bookGUI.getBookWidgetTexture();
        int srcU = uOffset;
        if (hoveredForTint) {
            int color = bookGUI.getWidgetColor();
            int r = (color & 0xFF0000) >> 16;
            int g = (color & 0xFF00) >> 8;
            int b = color & 0xFF;
            BookBlit.blitWithColor(guiGraphics, tex, destX, destY, srcU, vOffset, regionW, regionH, 256, 256, r, g, b, 255);
        } else {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, tex, destX, destY, (float) srcU, (float) vOffset, regionW, regionH, 256, 256, 0xFFFFFFFF);
        }
    }

    @Override
    public void playDownSound(SoundManager p_230988_1_) {
        if (this.playTurnSound) {
            p_230988_1_.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
        }
    }
}
