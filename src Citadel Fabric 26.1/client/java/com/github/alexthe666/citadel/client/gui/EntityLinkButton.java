package com.github.alexthe666.citadel.client.gui;

import com.github.alexthe666.citadel.client.gui.data.EntityLinkData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.util.Mth;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.HashMap;
import java.util.Map;

/**
 * Entity slot on a book page — 1:1 with 1.21.1 {@code renderWidget} draw order and atlas UVs
 * ({@code widgets.png}: base u=0, idle tint u=24, hover u=48, v=30, 24×24), using {@link GuiGraphicsExtractor} on 26.1.
 */
public class EntityLinkButton extends Button {

    private static final Map<String, Entity> renderedEntites = new HashMap<>();
    /** Same as Citadel 1.21.1 {@code EntityLinkButton#ENTITY_ROTATION}. */
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
        int px = this.getX();
        int py = this.getY();
        var pose = guiGraphics.pose();

        // Base frame (untinted) — same as 1.21.1 first drawBtn(false, …, 0, 30, 24, 24) inside translate + scale
        pose.pushMatrix();
        pose.translate(px, py);
        pose.scale(f, f);
        this.drawBtn(false, guiGraphics, 0, 0, 0, v, 24, 24);
        pose.popMatrix();

        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(Identifier.parse(data.getEntity())).map(Holder.Reference::value).orElse(null);
        Entity model = type != null ? renderedEntites.computeIfAbsent(data.getEntity(), k -> type.create(Minecraft.getInstance().level, EntitySpawnReason.LOAD)) : null;

        if (model != null) {
            model.tickCount = Minecraft.getInstance().player.tickCount;
            float renderScale = (float) (data.getEntityScale() * f * 10);
            // Same inner clip as 1.21.1 (design 4–20). Use that *exact* rect for PiP — a smaller square often sat
            // off-scissor or mis-centered vs what the deferred PiP renderer expects, which hid or clipped mobs.
            int sx0 = px + Math.round(f * 4.0F);
            int sy0 = py + Math.round(f * 4.0F);
            int sx1 = px + Math.round(f * 20.0F);
            int sy1 = py + Math.round(f * 20.0F);
            guiGraphics.enableScissor(sx0, sy0, sx1, sy1);
            int pipSize = Mth.clamp(Math.round(renderScale), 8, 36);
            if (model instanceof LivingEntity living) {
                // Nudge "mouse" from slot centre so JSON offsets match 1.21.1 pivot (11+int, 22+int) vs inner centre (12,12).
                int oix = (int) (data.getOffset_x() * data.getEntityScale());
                int oiy = (int) (data.getOffset_y() * data.getEntityScale());
                float mx = (sx0 + sx1) * 0.5F + (oix - 1) * f;
                float my = (sy0 + sy1) * 0.5F + (oiy + 10) * f;
                renderLivingPreviewLikeInventory(guiGraphics, living, partialTick, pipSize, sx0, sy0, sx1, sy1, mx, my);
            } else {
                renderEntityPreviewLike1121(guiGraphics, model, partialTick, renderScale, sx0, sy0, sx1, sy1);
            }
            guiGraphics.disableScissor();
        }

        if (this.isHovered) {
            bookGUI.setEntityTooltip(this.data.getHoverText());
        }

        // Second layer — 1.21.1: drawBtn(!hovered): idle → tinted u=24; hover → untinted u=48
        int u = this.isHovered ? 48 : 24;
        pose.pushMatrix();
        pose.translate(px, py);
        pose.scale(f, f);
        this.drawBtn(!this.isHovered, guiGraphics, 0, 0, u, v, 24, 24);
        pose.popMatrix();
    }

    /**
     * Mirrors 1.21.1 {@code drawBtn}: {@code color == true} → binding tint via {@link BookBlit}; else vanilla-style untinted blit.
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

    /**
     * 26.1 deferred PiP for book slots — matches 1.21.1 {@code renderEntityInInventory} pose order:
     * {@code translate(x,y,50) * scaling(s,s,-s) * ENTITY_ROTATION} in widget space, with {@link GuiBasicBook} PiP translate (Z=0).
     */
    private static void renderEntityPreviewLike1121(GuiGraphicsExtractor guiGraphics, Entity entity, float partialTick, float scale, int x0, int y0, int x1, int y1) {
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderState state = dispatcher.extractEntity(entity, partialTick);
        if (state instanceof LivingEntityRenderState livingState) {
            livingState.boundingBoxWidth /= livingState.scale;
            livingState.boundingBoxHeight /= livingState.scale;
            livingState.scale = 1.0F;
        }
        Quaternionf qBody = new Quaternionf().rotateZ((float) Math.PI).mul(ENTITY_ROTATION);
        Quaternionf qPitch = new Quaternionf();
        Vector3f translate = new Vector3f(0.0F, state.boundingBoxHeight / 2.0F + 0.0625F, 0.0F);
        guiGraphics.entity(state, scale, translate, qBody, qPitch, x0, y0, x1, y1);
    }

    private static void renderLivingPreviewLikeInventory(
            GuiGraphicsExtractor guiGraphics,
            LivingEntity living,
            float partialTick,
            float scale,
            int x0,
            int y0,
            int x1,
            int y1,
            float mouseX,
            float mouseY
    ) {
        float customYaw = (x0 + x1) * 0.5F - mouseX;
        float customPitch = (y0 + y1) * 0.5F - mouseY;
        float yaw = (float) Math.atan(customYaw / 40.0F);
        float pitch = (float) Math.atan(customPitch / 40.0F);

        living.setXRot(pitch * 20.0F);
        living.setYRot(yaw * 20.0F);
        living.yBodyRot = yaw * 20.0F;
        living.yBodyRotO = living.yBodyRot;
        living.yHeadRot = living.getYRot();
        living.yHeadRotO = living.yHeadRot;

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderState state = dispatcher.extractEntity(living, partialTick);
        if (state instanceof LivingEntityRenderState livingState) {
            livingState.bodyRot = 180.0F + yaw * 20.0F;
            livingState.yRot = yaw * 20.0F;
            if (livingState.pose != Pose.FALL_FLYING) {
                livingState.xRot = -pitch * 20.0F;
            } else {
                livingState.xRot = 0.0F;
            }
            livingState.boundingBoxWidth /= livingState.scale;
            livingState.boundingBoxHeight /= livingState.scale;
            livingState.scale = 1.0F;
        }

        Quaternionf qBody = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf qPitch = new Quaternionf().rotateX(pitch * 20.0F * (float) (Math.PI / 180.0D));
        qBody.mul(qPitch);
        Vector3f translate = new Vector3f(0.0F, state.boundingBoxHeight / 2.0F + 0.0625F, 0.0F);
        guiGraphics.entity(state, scale, translate, qBody, qPitch, x0, y0, x1, y1);
    }
}
