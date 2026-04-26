package com.github.alexthe666.alexsmobs.client.gui;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.inventory.MenuTransmutationTable;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

public class GUITransmutationTable extends AbstractContainerScreen<MenuTransmutationTable> {
    public static final Identifier TEXTURE = Identifier.parse("alexsmobs:textures/gui/transmutation_table.png");
    private int tickCount = 0;
    private ButtonTransmute transmuteBtn1;
    private ButtonTransmute transmuteBtn2;
    private ButtonTransmute transmuteBtn3;

    public GUITransmutationTable(MenuTransmutationTable menu, Inventory inventory, Component name) {
        super(menu, inventory, name, DEFAULT_IMAGE_WIDTH, 201);
    }

    @Override
    protected void init() {
        super.init();
        int i = this.leftPos;
        int j = this.topPos;
        this.addRenderableWidget(transmuteBtn1 = new ButtonTransmute(this, i + 30, j + 16, (button) -> {
            this.menu.clickMenuButton(minecraft.player, 0);
        }));
        this.addRenderableWidget(transmuteBtn2 = new ButtonTransmute(this, i + 30, j + 35, (button) -> {
            this.menu.clickMenuButton(minecraft.player, 1);
        }));
        this.addRenderableWidget(transmuteBtn3 = new ButtonTransmute(this, i + 30, j + 54, (button) -> {
            this.menu.clickMenuButton(minecraft.player, 2);
        }));
        transmuteBtn1.visible = false;
        transmuteBtn2.visible = false;
        transmuteBtn3.visible = false;
    }

    @Override
    public void extractBackground(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);
        int i = this.leftPos;
        int j = this.topPos;
        graphics.blit(RenderPipelines.GUI_TEXTURED, TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTick);
        this.renderItemsTransmute(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void containerTick() {
        tickCount++;
        boolean thingIn = !this.menu.getSlot(0).getItem().isEmpty();
        transmuteBtn1.visible = !AlexsMobs.PROXY.getDisplayTransmuteResult(0).isEmpty() && thingIn;
        transmuteBtn2.visible = !AlexsMobs.PROXY.getDisplayTransmuteResult(1).isEmpty() && thingIn;
        transmuteBtn3.visible = !AlexsMobs.PROXY.getDisplayTransmuteResult(2).isEmpty() && thingIn;
    }

    @Override
    protected void extractLabels(GuiGraphicsExtractor guiGraphics, int x, int y) {
        this.titleLabelX = (this.imageWidth - this.font.width(this.title)) / 2;
        guiGraphics.text(font, this.title, this.titleLabelX, this.titleLabelY, 0X4EFF21, false);
    }

    protected void renderItemsTransmute(GuiGraphicsExtractor guiGraphics, int x, int y) {
        int i = this.leftPos;
        int j = this.topPos;
        if (!this.menu.getSlot(0).getItem().isEmpty()) {
            guiGraphics.item(AlexsMobs.PROXY.getDisplayTransmuteResult(0), i + 31, j + 17);
            guiGraphics.item(AlexsMobs.PROXY.getDisplayTransmuteResult(1), i + 31, j + 36);
            guiGraphics.item(AlexsMobs.PROXY.getDisplayTransmuteResult(2), i + 31, j + 55);
        }
    }

}
