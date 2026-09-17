package com.github.alexthe666.citadel.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.ARGB;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class LinkButton extends Button {

    public ItemStack previewStack;
    public GuiBasicBook book;

    public LinkButton(GuiBasicBook book, int x, int y, int width, int height, Component component, ItemStack previewStack, Button.OnPress onPress) {
        super(x, y, width + (previewStack.isEmpty() ? 0 : 6), height, component, onPress, Button.DEFAULT_NARRATION);
        this.previewStack = previewStack;
        this.book = book;
    }

    public LinkButton(GuiBasicBook book, int x, int y, int width, int height, Component component, Button.OnPress onPress) {
        this(book, x, y, width, height, component, ItemStack.EMPTY, onPress);
    }

    public int getFGColor() {
        return this.isHovered ? book.getWidgetColor() : this.active ? 0X94745A : 10526880;
    }

    private int getTextureY() {
        int i = 1;
        if (!this.active) {
            i = 0;
        } else if (this.isHoveredOrFocused()) {
            i = 2;
        }
        return 46 + i * 20;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int guiX, int guiY, float partialTicks) {
        Minecraft minecraft = Minecraft.getInstance();
        Font font = minecraft.font;
        int i = this.getTextureY();
        int ty = 46 + i * 20;
        var buttonsTex = book.getBookButtonsTexture();
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, buttonsTex, this.getX(), this.getY(), 0.0F, (float) ty, this.width / 2, this.height, 256, 256, ARGB.color(255, 255, 255, 255));
        guiGraphics.blit(RenderPipelines.GUI_TEXTURED, buttonsTex, this.getX() + this.width / 2, this.getY(), (float) (200 - this.width / 2), (float) ty, this.width / 2, this.height, 256, 256, ARGB.color(255, 255, 255, 255));
        if (this.isHovered) {
            int color = book.getWidgetColor();
            int r = (color & 0xFF0000) >> 16;
            int g = (color & 0xFF00) >> 8;
            int b = color & 0xFF;
            i = 3;
            BookBlit.blitWithColor(guiGraphics, book.getBookButtonsTexture(), this.getX(), this.getY(), 0, 46 + i * 20, this.width / 2, this.height, 256, 256, r, g, b, 255);
            BookBlit.blitWithColor(guiGraphics, book.getBookButtonsTexture(), this.getX() + this.width / 2, this.getY(), 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height, 256, 256, r, g, b, 255);
        }

        int j = getFGColor();
        int itemTextOffset = previewStack.isEmpty() ? 0 : 8;
        if (!previewStack.isEmpty()) {
            guiGraphics.item(previewStack, this.getX() + 2, this.getY() + 1);
        }
        LinkButton.drawTextOf(guiGraphics, font, this.getMessage(), this.getX() + itemTextOffset + this.width / 2, this.getY() + (this.height - 8) / 2, j | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    public static void drawTextOf(GuiGraphicsExtractor guiGraphics, Font font, Component component, int x, int y, int color) {
        FormattedCharSequence formattedcharsequence = component.getVisualOrderText();
        int textX = x - font.width(formattedcharsequence) / 2;
        guiGraphics.text(font, formattedcharsequence, textX, y, color, false);
    }

    @Override
    public void playDownSound(SoundManager soundManager) {
        soundManager.play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
    }
}
