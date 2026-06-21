package com.github.alexthe666.citadel.client.gui;

import com.github.alexthe666.citadel.ClientProxy;
import com.github.alexthe666.citadel.client.rewards.CitadelCapes;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.citadel.server.message.PropertiesMessage;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;

public class GuiCitadelCapesConfig extends OptionsSubScreen {

    @Nullable
    private String capeType;
    private Button button;


    public GuiCitadelCapesConfig(Screen parentScreenIn, Options gameSettingsIn) {
        super(parentScreenIn, gameSettingsIn, Component.translatable("citadel.gui.capes"));
        CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
        String capeStr = tag.getStringOr("CitadelCapeType", "");
        capeType = capeStr.isEmpty() ? null : capeStr;
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int mouseX, int mouseY, float partialTicks) {
        guiGraphics.centeredText(this.font, this.title, this.width / 2, 20, 16777215);
        super.extractRenderState(guiGraphics, mouseX, mouseY, partialTicks);
        int i = this.width / 2;
        int j = this.height / 6;
        guiGraphics.pose().pushMatrix();
        ClientProxy.hideFollower = true;
        renderBackwardsEntity(guiGraphics, i, j + 144, 60, 0, 0, Minecraft.getInstance().player, partialTicks);
        ClientProxy.hideFollower = false;
        guiGraphics.pose().popMatrix();
    }

    public static void renderBackwardsEntity(GuiGraphicsExtractor guiGraphics, int x, int y, int size, float angleXComponent, float angleYComponent, LivingEntity entity, float partialTick) {
        float f2 = entity.yBodyRot;
        float f3 = entity.getYRot();
        float f4 = entity.getXRot();
        float f5 = entity.yHeadRotO;
        float f6 = entity.yHeadRot;
        entity.yBodyRot = 180.0F + angleXComponent * 20.0F;
        entity.setYRot(180.0F + angleXComponent * 40.0F);
        entity.setXRot(-angleYComponent * 20.0F);
        entity.yHeadRot = entity.getYRot();
        entity.yHeadRotO = entity.getYRot();

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderState state = dispatcher.extractEntity(entity, partialTick);
        if (state instanceof LivingEntityRenderState livingState) {
            if (livingState.pose != Pose.FALL_FLYING) {
                livingState.xRot = entity.getXRot();
            } else {
                livingState.xRot = 0.0F;
            }
            livingState.bodyRot = entity.yBodyRot;
            livingState.yRot = entity.getYRot() - 180.0F;
            livingState.boundingBoxWidth /= livingState.scale;
            livingState.boundingBoxHeight /= livingState.scale;
            livingState.scale = 1.0F;
        }

        Quaternionf qBody = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf qPitch = new Quaternionf().rotateX(angleYComponent * 20.0F * (float) (Math.PI / 180.0D));
        qBody.mul(qPitch);
        qBody.rotateY((float) Math.PI);
        Quaternionf qPitchOnly = new Quaternionf().rotateX(angleYComponent * 20.0F * (float) (Math.PI / 180.0D));

        float boxH = state.boundingBoxHeight;
        Vector3f translate = new Vector3f(0.0F, boxH / 2.0F + (float) size, 1050.0F);
        int half = Math.max(16, size);
        guiGraphics.entity(state, partialTick, translate, qBody, qPitchOnly, x - half, y - half, x + half, y + half);

        entity.yBodyRot = f2;
        entity.setYRot(f3);
        entity.setXRot(f4);
        entity.yHeadRotO = f5;
        entity.yHeadRot = f6;
    }


    protected void init() {
        super.init();
        int i = this.width / 2;
        int j = this.height / 6;
        Button doneButton = Button.builder(CommonComponents.GUI_DONE, (p_213079_1_) -> this.minecraft.setScreenAndShow(this.lastScreen)).size(200, 20).pos(i - 100, j + 160).build();
        this.addRenderableWidget(doneButton);
        button = Button.builder(getTypeText(), (p_213079_1_) -> {
            CitadelCapes.Cape nextCape = CitadelCapes.getNextCape(capeType, Minecraft.getInstance().player.getUUID());
            this.capeType = nextCape == null ? null : nextCape.getIdentifier();
            CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(Minecraft.getInstance().player);
            if (capeType == null) {
                tag.putString("CitadelCapeType", "");
                tag.putBoolean("CitadelCapeDisabled", true);
            } else {
                tag.putString("CitadelCapeType", capeType);
                tag.putBoolean("CitadelCapeDisabled", false);
            }
            CitadelEntityData.setCitadelTag(Minecraft.getInstance().player, tag);
            ClientPlayNetworking.send(new PropertiesMessage("CitadelTagUpdate", tag, Minecraft.getInstance().player.getId()));
            button.setMessage(getTypeText());
        }).size(200, 20).pos(i - 100, j).build();
        this.addRenderableWidget(button);

    }

    @Override
    protected void addOptions() {

    }

    private Component getTypeText() {
        Component suffix;

        if (capeType == null) {
            suffix = Component.translatable("citadel.gui.no_cape");
        } else {

            CitadelCapes.Cape cape = CitadelCapes.getById(capeType);
            if (cape == null) {
                suffix = Component.translatable("citadel.gui.no_cape");
            } else {
                suffix = Component.translatable("cape." + cape.getIdentifier());
            }
        }
        return Component.translatable("citadel.gui.cape_type").append(" ").append(suffix);
    }
}
