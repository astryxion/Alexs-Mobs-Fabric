package com.github.alexthe666.alexsmobs.client.gui;

import com.github.alexthe666.alexsmobs.client.render.RenderLaviathan;
import com.github.alexthe666.alexsmobs.client.render.RenderMurmurBody;
import com.github.alexthe666.alexsmobs.client.render.RenderUnderminer;
import com.github.alexthe666.citadel.client.gui.GuiBasicBook;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
public class GUIAnimalDictionary extends GuiBasicBook {

    private static final Identifier ROOT = Identifier.parse("alexsmobs:book/animal_dictionary/root.json");

    public GUIAnimalDictionary(ItemStack bookStack) {
        super(bookStack, Component.translatable("item.alexsmobs.animal_dictionary"));
    }

    public GUIAnimalDictionary(ItemStack bookStack, String page) {
        super(bookStack, Component.translatable("item.alexsmobs.animal_dictionary"));
        this.currentPageJSON = Identifier.parse(this.getTextFileDirectory() + page + ".json");
    }

    @Override
    protected void extractBlurredBackground(GuiGraphicsExtractor graphics) {
        // Don't render blur - we want to see the world behind the book
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        // No dimmed pane (1:1 with old renderBackground), but we must still prime the GUI stratum — an empty body
        // prevents book text, models, and widgets from compositing on 26.1.
        this.extractTransparentBackground(guiGraphics);
        // Match Screen.extractBackground in-game tail (subtitles deferred into this pass).
        Minecraft.getInstance().gui.extractDeferredSubtitles();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int x, int y, float partialTicks) {
        RenderLaviathan.renderWithoutShaking = true;
        RenderMurmurBody.renderWithHead = true;
        RenderUnderminer.renderWithPickaxe = true;
        super.extractRenderState(guiGraphics, x, y, partialTicks);
        RenderLaviathan.renderWithoutShaking = false;
        RenderMurmurBody.renderWithHead = false;
        RenderUnderminer.renderWithPickaxe = false;
    }

    protected int getBindingColor() {
        return 0X606B26;
    }

    public Identifier getRootPage() {
        return ROOT;
    }

    public String getTextFileDirectory() {
        return "alexsmobs:book/animal_dictionary/";
    }
}
