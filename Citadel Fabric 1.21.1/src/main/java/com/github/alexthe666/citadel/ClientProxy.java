package com.github.alexthe666.citadel;

import com.github.alexthe666.citadel.animation.IAnimatedEntity;
import com.github.alexthe666.citadel.client.CitadelItemRenderProperties;
import com.github.alexthe666.citadel.client.event.EventRenderSplashText;
import com.github.alexthe666.citadel.client.game.Tetris;
import com.github.alexthe666.citadel.client.gui.GuiCitadelBook;
import com.github.alexthe666.citadel.client.gui.GuiCitadelCapesConfig;
import com.github.alexthe666.citadel.client.gui.GuiCitadelPatreonConfig;
import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.citadel.client.model.TabulaModelHandler;
import com.github.alexthe666.citadel.client.CitadelItemstackRenderer;
import com.github.alexthe666.citadel.client.CitadelItemstackRenderer;
import com.github.alexthe666.citadel.client.render.CitadelLecternRenderer;
import com.github.alexthe666.citadel.client.render.pathfinding.WorldEventContext;
import com.github.alexthe666.citadel.client.rewards.CitadelCapes;
import com.github.alexthe666.citadel.client.rewards.CitadelPatreonRenderer;
import com.github.alexthe666.citadel.client.rewards.SpaceStationPatreonRenderer;
import com.github.alexthe666.citadel.client.shader.CitadelInternalShaders;
import com.github.alexthe666.citadel.client.shader.PostEffectRegistry;
import com.github.alexthe666.citadel.client.tick.ClientTickRateTracker;
import com.github.alexthe666.citadel.config.ServerConfig;
import com.github.alexthe666.citadel.item.ItemWithHoverAnimation;
import com.github.alexthe666.citadel.server.entity.CitadelEntityData;
import com.github.alexthe666.citadel.server.entity.pathfinding.raycoms.Pathfinding;
import com.github.alexthe666.citadel.server.event.EventChangeEntityTickRate;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.BackupConfirmScreen;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.options.SkinCustomizationScreen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenEvents;
import net.fabricmc.fabric.api.client.screen.v1.ScreenKeyboardEvents;
import net.minecraft.client.gui.GuiGraphics;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ClientProxy extends ServerProxy {
    public static TabulaModel CITADEL_MODEL;
    public static boolean hideFollower = false;
    private final Map<ItemStack, Float> prevMouseOverProgresses = new HashMap<>();

    private final Map<ItemStack, Float> mouseOverProgresses = new HashMap<>();
    private ItemStack lastHoveredItem = null;
    private Tetris aprilFoolsTetrisGame = null;
    public static final ResourceLocation RAINBOW_AURA_POST_SHADER = ResourceLocation.parse("citadel:shaders/post/rainbow_aura.json");

    public ClientProxy() {
        super();
    }

    public void onClientInit() {
        try {
            CITADEL_MODEL = new TabulaModel(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/citadel/models/citadel_model"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        net.fabricmc.fabric.api.client.rendering.v1.CoreShaderRegistrationCallback.EVENT.register(context -> {
            try {
                context.register(ResourceLocation.parse("citadel:rendertype_rainbow_aura"), DefaultVertexFormat.POSITION_TEX_COLOR, CitadelInternalShaders::setRenderTypeRainbowAura);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        });
        BlockEntityRenderers.register(Citadel.LECTERN_BE, CitadelLecternRenderer::new);
        net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.INSTANCE.register(Citadel.FANCY_ITEM, (stack, mode, matrices, vertexConsumers, light, overlay) -> new CitadelItemstackRenderer().renderByItem(stack, mode, matrices, vertexConsumers, light, overlay));
        net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.INSTANCE.register(Citadel.EFFECT_ITEM, (stack, mode, matrices, vertexConsumers, light, overlay) -> new CitadelItemstackRenderer().renderByItem(stack, mode, matrices, vertexConsumers, light, overlay));
        net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry.INSTANCE.register(Citadel.ICON_ITEM, (stack, mode, matrices, vertexConsumers, light, overlay) -> new CitadelItemstackRenderer().renderByItem(stack, mode, matrices, vertexConsumers, light, overlay));
        CitadelPatreonRenderer.register("citadel", new SpaceStationPatreonRenderer(ResourceLocation.parse("citadel:patreon_space_station"), new int[]{}));
        CitadelPatreonRenderer.register("citadel_red", new SpaceStationPatreonRenderer(ResourceLocation.parse("citadel:patreon_space_station_red"), new int[]{0XB25048, 0X9D4540, 0X7A3631, 0X71302A}));
        CitadelPatreonRenderer.register("citadel_gray", new SpaceStationPatreonRenderer(ResourceLocation.parse("citadel:patreon_space_station_gray"), new int[]{0XA0A0A0, 0X888888, 0X646464, 0X575757}));
        if (CitadelConstants.debugShaders()) {
            PostEffectRegistry.registerEffect(RAINBOW_AURA_POST_SHADER);
        }
        ClientProxy self = this;
        ScreenEvents.AFTER_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof SkinCustomizationScreen && Minecraft.getInstance().player != null) {
                try {
                    String username = Minecraft.getInstance().player.getName().getString();
                    int height = -20;
                    if (Citadel.PATREONS.contains(username)) {
                        Button button1 = Button.builder(Component.translatable("citadel.gui.patreon_rewards_option").withStyle(ChatFormatting.GREEN), (p_213080_2_) -> {
                            Minecraft.getInstance().setScreen(new GuiCitadelPatreonConfig(screen, Minecraft.getInstance().options));
                        }).bounds(screen.width / 2 - 100, screen.height / 6 + 150 + height, 200, 20).build();
                        screen.addRenderableWidget(button1);
                        height += 25;
                    }
                    if (!CitadelCapes.getCapesFor(Minecraft.getInstance().player.getUUID()).isEmpty()) {
                        Button button2 = Button.builder(Component.translatable("citadel.gui.capes_option").withStyle(ChatFormatting.GREEN), (p_213080_2_) -> {
                            Minecraft.getInstance().setScreen(new GuiCitadelCapesConfig(screen, Minecraft.getInstance().options));
                        }).bounds(screen.width / 2 - 100, screen.height / 6 + 150 + height, 200, 20).build();
                        screen.addRenderableWidget(button2);
                        height += 25;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        ScreenEvents.BEFORE_INIT.register((client, screen, scaledWidth, scaledHeight) -> {
            if (screen instanceof TitleScreen) {
                ScreenEvents.afterRender(screen).register((screen1, guiGraphics, mouseX, mouseY, tickDelta) -> {
                    if (CitadelConstants.isAprilFools()) {
                        if (self.aprilFoolsTetrisGame == null) {
                            self.aprilFoolsTetrisGame = new Tetris();
                        } else {
                            self.aprilFoolsTetrisGame.render((TitleScreen) screen1, guiGraphics, tickDelta);
                        }
                    }
                });
                ScreenKeyboardEvents.allowKeyPress(screen).register((screen2, key, scancode, modifiers) -> {
                    if (self.aprilFoolsTetrisGame != null && self.aprilFoolsTetrisGame.isStarted()) {
                        if (key == InputConstants.KEY_LEFT || key == InputConstants.KEY_RIGHT || key == InputConstants.KEY_DOWN || key == InputConstants.KEY_UP) {
                            return false;
                        }
                    }
                    return true;
                });
            }
            if (ServerConfig.skipWarnings && screen != null) {
                try {
                    if (screen instanceof BackupConfirmScreen confirmBackupScreen) {
                        MutableComponent title = Component.translatable("selectWorld.backupQuestion.experimental");
                        if (confirmBackupScreen.getTitle().equals(title)) {
                            try {
                                java.lang.reflect.Field listenerField = BackupConfirmScreen.class.getDeclaredField("onProceed");
                                listenerField.setAccessible(true);
                                Object listener = listenerField.get(confirmBackupScreen);
                                listener.getClass().getMethod("proceed", boolean.class, boolean.class).invoke(listener, false, true);
                            } catch (Exception e) {
                                Citadel.LOGGER.warn("Citadel: could not invoke BackupConfirmScreen.onProceed.proceed", e);
                            }
                        }
                    }
                    if (screen instanceof ConfirmScreen confirmScreen) {
                        MutableComponent title = Component.translatable("selectWorld.backupQuestion.experimental");
                        if (confirmScreen.getTitle().equals(title)) {
                            try {
                                java.lang.reflect.Field callbackField = ConfirmScreen.class.getDeclaredField("callback");
                                callbackField.setAccessible(true);
                                Object callback = callbackField.get(confirmScreen);
                                callback.getClass().getMethod("accept", boolean.class).invoke(callback, true);
                            } catch (Exception e) {
                                Citadel.LOGGER.warn("Citadel: could not invoke ConfirmScreen.callback.accept", e);
                            }
                        }
                    }
                } catch (Exception e) {
                    Citadel.LOGGER.warn("Citadel couldn't skip world loadings");
                    e.printStackTrace();
                }
            }
        });
        WorldRenderEvents.LAST.register(context -> {
            if (Pathfinding.isDebug()) {
                WorldEventContext.INSTANCE.renderWorldLastEvent(context);
            }
        });
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (!self.isGamePaused() && client.isRunning() && client.level != null && client.player != null) {
                ClientTickRateTracker.getForClient(client).masterTick();
                self.tickMouseOverAnimations();
            }
            if (!self.isGamePaused() && CitadelConstants.isAprilFools()) {
                if (self.aprilFoolsTetrisGame != null) {
                    if (client.screen instanceof TitleScreen) {
                        self.aprilFoolsTetrisGame.tick();
                    } else {
                        self.aprilFoolsTetrisGame.reset();
                    }
                }
            }
        });
        net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback.EVENT.register((stack, context, tooltipType, lines) -> {
            if (stack.getItem() instanceof ItemWithHoverAnimation hoverOver && hoverOver.canHoverOver(stack)) {
                self.lastHoveredItem = stack;
            } else {
                self.lastHoveredItem = null;
            }
        });
        EventRenderSplashText.Pre.addPreListener(event -> {
            if (CitadelConstants.isAprilFools() && self.aprilFoolsTetrisGame != null) {
                event.setResult(EventRenderSplashText.Result.ALLOW);
                float hue = (System.currentTimeMillis() % 6000) / 6000f;
                event.getGuiGraphics().pose().mulPose(Axis.ZP.rotationDegrees((float) Math.sin(hue * Math.PI) * 360));
                if (!self.aprilFoolsTetrisGame.isStarted()) {
                    event.setSplashText("Psst... press 'T' ;)");
                } else {
                    event.setSplashText("");
                }
                event.setSplashTextColor(Color.HSBtoRGB(hue, 0.6f, 1));
            }
        });
    }

    public static void afterRenderPlayer(PoseStack poseStack, net.minecraft.client.renderer.MultiBufferSource bufferSource, int packedLight, float partialTick, Player player) {
        if (!player.isModelPartShown(PlayerModelPart.CAPE) || player.isSpectator()) return;
        String username = player.getName().getString();
        if (Citadel.PATREONS.contains(username)) {
            CompoundTag tag = CitadelEntityData.getOrCreateCitadelTag(player);
            String rendererName = tag.contains("CitadelFollowerType") ? tag.getString("CitadelFollowerType") : "citadel";
            if (!rendererName.equals("none") && !hideFollower) {
                CitadelPatreonRenderer renderer = CitadelPatreonRenderer.get(rendererName);
                if (renderer != null) {
                    float distance = tag.contains("CitadelRotateDistance") ? tag.getFloat("CitadelRotateDistance") : 2F;
                    float speed = tag.contains("CitadelRotateSpeed") ? tag.getFloat("CitadelRotateSpeed") : 1;
                    float height = tag.contains("CitadelRotateHeight") ? tag.getFloat("CitadelRotateHeight") : 1F;
                    renderer.render(poseStack, bufferSource, packedLight, partialTick, player, distance, speed, height);
                }
            }
        }
    }

    private void tickMouseOverAnimations() {
        prevMouseOverProgresses.putAll(mouseOverProgresses);
        if (lastHoveredItem != null) {
            float prev = mouseOverProgresses.getOrDefault(lastHoveredItem, 0F);
            float maxTime = 5F;
            if (lastHoveredItem.getItem() instanceof ItemWithHoverAnimation hoverOver) {
                maxTime = hoverOver.getMaxHoverOverTime(lastHoveredItem);
            }
            if (prev < maxTime) {
                mouseOverProgresses.put(lastHoveredItem, prev + 1);
            }
        }

        if (!mouseOverProgresses.isEmpty()) {
            Iterator<Map.Entry<ItemStack, Float>> it = mouseOverProgresses.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<ItemStack, Float> next = it.next();
                float progress = next.getValue();
                if (lastHoveredItem == null || next.getKey() != lastHoveredItem) {
                    if (progress == 0) {
                        it.remove();
                    } else {
                        next.setValue(progress - 1);
                    }
                }
            }
        }
        lastHoveredItem = null;
    }

    @Override
    public float getMouseOverProgress(ItemStack itemStack) {
        float prev = prevMouseOverProgresses.getOrDefault(itemStack, 0F);
        float current = mouseOverProgresses.getOrDefault(itemStack, 0F);
        float lerped = prev + (current - prev) * ((com.github.alexthe666.citadel.mixin.client.MinecraftAccessor) Minecraft.getInstance()).citadel$getTimer().getGameTimeDeltaPartialTick(false);
        float maxTime = 5F;
        if (itemStack.getItem() instanceof ItemWithHoverAnimation hoverOver) {
            maxTime = hoverOver.getMaxHoverOverTime(itemStack);
        }
        return lerped / maxTime;
    }

    @Override
    public void handleAnimationPacket(int entityId, int index) {
        if (Minecraft.getInstance().level != null) {
            IAnimatedEntity entity = (IAnimatedEntity) Minecraft.getInstance().level.getEntity(entityId);
            if (entity != null) {
                if (index == -1) {
                    entity.setAnimation(IAnimatedEntity.NO_ANIMATION);
                } else {
                    entity.setAnimation(entity.getAnimations()[index]);
                }
                entity.setAnimationTick(0);
            }
        }
    }

    @Override
    public void handlePropertiesPacket(String propertyID, CompoundTag compound, int entityID) {
        if (compound == null || Minecraft.getInstance().level == null) {
            return;
        }
        Entity entity = Minecraft.getInstance().level.getEntity(entityID);
        if ((propertyID.equals("CitadelPatreonConfig") || propertyID.equals("CitadelTagUpdate")) && entity instanceof LivingEntity) {
            CitadelEntityData.setCitadelTag((LivingEntity) entity, compound);
        }
    }


    @Override
    public void handleClientTickRatePacket(CompoundTag compound) {
        ClientTickRateTracker.getForClient(Minecraft.getInstance()).syncFromServer(compound);
    }

    @Override
    public Object getISTERProperties() {
        return new CitadelItemRenderProperties();
    }

    @Override
    public void openBookGUI(ItemStack book) {
        Minecraft.getInstance().setScreen(new GuiCitadelBook(book));
    }

    public boolean isGamePaused() {
        return Minecraft.getInstance().isPaused();
    }

    public Player getClientSidePlayer() {
        return Minecraft.getInstance().player;
    }

    public boolean canEntityTickClient(Level level, Entity entity) {
        ClientTickRateTracker tracker = ClientTickRateTracker.getForClient(Minecraft.getInstance());
        if (tracker.isTickingHandled(entity)) {
            return false;
        } else if (!tracker.hasNormalTickRate(entity)) {
            EventChangeEntityTickRate event = new EventChangeEntityTickRate(entity, tracker.getEntityTickLengthModifier(entity));
            EventChangeEntityTickRate.post(event);
            if (event.isCanceled()) {
                return true;
            } else {
                tracker.addTickBlockedEntity(entity);
                return false;
            }
        }
        return true;
    }

    @Nullable
    @Override
    public MinecraftServer getMinecraftServer() {
        return null;
    }
}
