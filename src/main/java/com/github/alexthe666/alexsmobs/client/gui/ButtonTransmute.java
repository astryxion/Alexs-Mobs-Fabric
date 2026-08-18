package com.github.alexthe666.alexsmobs.client.gui;

import com.github.alexthe666.alexsmobs.config.AMConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ButtonTransmute extends AbstractButton {
    private final Screen parent;
    private final java.util.function.Consumer<ButtonTransmute> pressAction;

    public ButtonTransmute(Screen parent, int x, int y, java.util.function.Consumer<ButtonTransmute> onPress) {
        super(x, y, 117, 19, CommonComponents.EMPTY);
        this.parent = parent;
        this.pressAction = onPress;
    }

    @Override
    public void onPress(InputWithModifiers input) {
        if (canBeTransmuted(AMConfig.transmutingExperienceCost)) {
            this.pressAction.accept(this);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {
        this.defaultButtonNarrationText(narrationElementOutput);
    }

    @Override
    protected void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        int color = 8453920;
        int cost = AMConfig.transmutingExperienceCost;
        if (!canBeTransmuted(cost)) {
            color = 16736352;
        } else if (this.active && this.isHovered()) {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, GUITransmutationTable.TEXTURE, this.getX(), this.getY(), 0, 201, 117, 19, 256, 256);
            color = 0XC7FFD0;
        }
        guiGraphics.pose().pushMatrix();
        guiGraphics.text(Minecraft.getInstance().font, Component.translatable("alexsmobs.container.transmutation_table.cost").append(" " + cost), this.getX() + 21, this.getY() + (this.getHeight() - 8) / 2, color, false);
        guiGraphics.pose().popMatrix();
    }

    public boolean canBeTransmuted(int cost) {
        return Minecraft.getInstance().player.experienceLevel >= cost || Minecraft.getInstance().player.getAbilities().instabuild;
    }

    @Override
    public void playDownSound(SoundManager sounds) {
        if (canBeTransmuted(AMConfig.transmutingExperienceCost)) {
            super.playDownSound(sounds);
        }
    }
}
