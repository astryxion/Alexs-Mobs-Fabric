package com.github.alexthe666.citadel.client.gui;

import com.github.alexthe666.citadel.client.gui.data.EntityLinkData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.joml.Quaternionf;

import java.util.HashMap;
import java.util.Map;

/**
 * Entity slot on a book page. The inner 16×16 of the 24×24 widget is scissored in
 * widget space; the mob itself is auto-fitted from its bounding box.
 */
public class EntityLinkButton extends Button {

    private static final Map<String, Entity> renderedEntites = new HashMap<>();
    private static final Quaternionf ENTITY_ROTATION = new Quaternionf().rotationXYZ((float) Math.toRadians(30), (float) Math.toRadians(130), (float) Math.PI);
    private final EntityLinkData data;
    private final GuiBasicBook bookGUI;

    public EntityLinkButton(GuiBasicBook bookGUI, EntityLinkData linkData, int k, int l, Button.OnPress o) {
        super(k + linkData.getX() - 12, l + linkData.getY(), (int) (24 * linkData.getScale()), (int) (24 * linkData.getScale()), CommonComponents.EMPTY, o, DEFAULT_NARRATION);
        this.data = linkData;
        this.bookGUI = bookGUI;
    }

    @Override
    public void extractContents(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTick) {
        int v = 30;
        float f = (float) data.getScale();
        var pose = guiGraphics.pose();

        pose.pushMatrix();
        pose.translate(this.getX(), this.getY());
        pose.scale(f, f);
        this.drawBtn(false, guiGraphics, 0, 0, 0, v, 24, 24);

        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(Identifier.parse(data.getEntity())).map(Holder.Reference::value).orElse(null);
        Entity model = type != null ? renderedEntites.computeIfAbsent(data.getEntity(), k -> GuiBasicBook.createBookPreviewEntity(type)) : null;

        guiGraphics.enableScissor(4, 4, 20, 20);
        if (model != null) {
            model.tickCount = Minecraft.getInstance().player.tickCount;
            float widest = Math.max(model.getBbHeight(), model.getBbWidth() * 1.4F);
            float renderScale = Math.min(10.0F * (float) data.getEntityScale(), 16.0F / Math.max(widest, 0.01F));
            int xPos = 12;
            int yPos = Math.round(12.0F + model.getBbHeight() * renderScale * 0.5F);
            GuiBasicBook.submitBookEntity(guiGraphics, model,
                this.getX() + Math.round(f * xPos),
                this.getY() + Math.round(f * yPos),
                f * renderScale, 0.0F, ENTITY_ROTATION, null);
        }
        guiGraphics.disableScissor();

        if (this.isHovered) {
            bookGUI.setEntityTooltip(this.data.getHoverText());
        }

        int u = this.isHovered ? 48 : 24;
        this.drawBtn(!this.isHovered, guiGraphics, 0, 0, u, v, 24, 24);
        pose.popMatrix();
    }

    /**
     * {@code tintBinding == true} tints the overlay with the book binding color; otherwise an untinted blit.
     */
    private void drawBtn(boolean tintBinding, GuiGraphicsExtractor guiGraphics, int destX, int destY, int srcU, int srcV, int destW, int destH) {
        if (tintBinding) {
            int widgetColor = bookGUI.getWidgetColor();
            int r = (widgetColor & 0xFF0000) >> 16;
            int g = (widgetColor & 0xFF00) >> 8;
            int b = widgetColor & 0xFF;
            BookBlit.blitWithColor(guiGraphics, bookGUI.getBookWidgetTexture(), destX, destY, srcU, srcV, destW, destH, 256, 256, r, g, b, 255);
        } else {
            guiGraphics.blit(RenderPipelines.GUI_TEXTURED, bookGUI.getBookWidgetTexture(), destX, destY, (float) srcU, (float) srcV, destW, destH, 256, 256, 0xFFFFFFFF);
        }
    }
}
