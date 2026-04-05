package com.github.alexthe666.alexsmobs.client.gui;

import com.github.alexthe666.alexsmobs.client.render.RenderLaviathan;
import com.github.alexthe666.alexsmobs.client.render.RenderMurmurBody;
import com.github.alexthe666.alexsmobs.client.render.RenderUnderminer;
import com.github.alexthe666.citadel.client.gui.GuiBasicBook;
import com.github.alexthe666.citadel.client.gui.data.LineData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class GUIAnimalDictionary extends GuiBasicBook {

    private static final ResourceLocation ROOT = ResourceLocation.parse("alexsmobs:book/animal_dictionary/root.json");

    public GUIAnimalDictionary(ItemStack bookStack) {
        super(bookStack, Component.translatable("animal_dictionary.title"));
    }

    public GUIAnimalDictionary(ItemStack bookStack, String page) {
        super(bookStack, Component.translatable("animal_dictionary.title"));
        this.currentPageJSON = ResourceLocation.parse(this.getTextFileDirectory() + page + ".json");
    }

    @Override
    protected void renderBlurredBackground(float partialTick) {
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
    }

    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
        RenderLaviathan.renderWithoutShaking = true;
        RenderMurmurBody.renderWithHead = true;
        RenderUnderminer.renderWithPickaxe = true;
        super.render(guiGraphics, x, y, partialTicks);
        RenderLaviathan.renderWithoutShaking = false;
        RenderMurmurBody.renderWithHead = false;
        RenderUnderminer.renderWithPickaxe = false;
    }

    @Override
    protected int getBindingColor() {
        return 6318886;
    }

    @Override
    protected void writePageText(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        Font font = this.font;
        int bookX = (this.width - this.xSize) / 2;
        int bookY = (this.height - this.ySize + 128) / 2;
        boolean russianText = isRussianDictionaryLanguage();
        float bodyScale = russianText ? 0.9F : 1.0F;
        float titleScale = russianText ? 1.8F : 2.0F;

        for (LineData line : this.lines) {
            if (line.getPage() != this.currentPageCounter) {
                continue;
            }
            int drawX = bookX + 10 + line.getxIndex();
            int drawY = bookY + 10 + line.getyIndex() * 12;
            if (bodyScale != 1.0F) {
                guiGraphics.pose().pushPose();
                guiGraphics.pose().translate(drawX, drawY, 0.0F);
                guiGraphics.pose().scale(bodyScale, bodyScale, bodyScale);
                guiGraphics.drawString(font, line.getText(), 0, 0, this.getTextColor(), false);
                guiGraphics.pose().popPose();
            } else {
                guiGraphics.drawString(font, line.getText(), drawX, drawY, this.getTextColor(), false);
            }
        }

        if (this.currentPageCounter == 0 && !this.writtenTitle.isEmpty()) {
            String title = I18n.get(this.writtenTitle);
            guiGraphics.pose().pushPose();
            float titleWidth = font.width(title) * titleScale;
            float titleX = bookX + 50 - titleWidth * 0.5F;
            float titleY = bookY + 20;
            guiGraphics.pose().translate(titleX, titleY, 0.0F);
            guiGraphics.pose().scale(titleScale, titleScale, titleScale);
            guiGraphics.drawString(font, title, 0, 0, this.getTitleColor(), false);
            guiGraphics.pose().popPose();
        }

        this.buttonNextPage.visible = this.currentPageCounter < this.maxPagesFromPrinting;
        this.buttonPreviousPage.visible = this.currentPageCounter > 0 || !this.currentPageJSON.equals(this.getRootPage());
    }

    public ResourceLocation getRootPage() {
        return ROOT;
    }

    public String getTextFileDirectory() {
        return "alexsmobs:book/animal_dictionary/";
    }

    private static boolean isRussianDictionaryLanguage() {
        String languageCode = Minecraft.getInstance().getLanguageManager().getSelected();
        return languageCode.startsWith("ru_");
    }
}
