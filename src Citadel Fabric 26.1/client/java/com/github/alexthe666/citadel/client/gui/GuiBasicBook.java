package com.github.alexthe666.citadel.client.gui;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.client.gui.data.*;
import com.github.alexthe666.citadel.client.model.TabulaModel;
import com.github.alexthe666.citadel.client.model.TabulaModelHandler;
import com.github.alexthe666.citadel.recipe.SpecialRecipeInGuideBook;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.client.renderer.rendertype.RenderTypes;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.LivingEntityRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.util.ProblemReporter;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.storage.TagValueInput;
import org.apache.commons.io.IOUtils;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public abstract class GuiBasicBook extends Screen {

    private static final Identifier BOOK_PAGE_TEXTURE = Identifier.parse("citadel:textures/gui/book/book_pages.png");
    private static final Identifier BOOK_BINDING_TEXTURE = Identifier.parse("citadel:textures/gui/book/book_binding.png");
    private static final Identifier BOOK_WIDGET_TEXTURE = Identifier.parse("citadel:textures/gui/book/widgets.png");
    private static final Identifier BOOK_BUTTONS_TEXTURE = Identifier.parse("citadel:textures/gui/book/link_buttons.png");
    protected final List<LineData> lines = new ArrayList<>();
    protected final List<LinkData> links = new ArrayList<>();
    protected final List<ItemRenderData> itemRenders = new ArrayList<>();
    protected final List<RecipeData> recipes = new ArrayList<>();
    protected final List<TabulaRenderData> tabulaRenders = new ArrayList<>();
    protected final List<EntityRenderData> entityRenders = new ArrayList<>();
    protected final List<EntityLinkData> entityLinks = new ArrayList<>();
    protected final List<ImageData> images = new ArrayList<>();
    protected final List<Whitespace> yIndexesToSkip = new ArrayList<>();
    private final Map<String, TabulaModel> renderedTabulaModels = new HashMap<>();
    private final Map<String, Entity> renderedEntites = new HashMap<>();
    private final Map<String, Identifier> textureMap = new HashMap<>();
    protected ItemStack bookStack;
    protected int xSize = 390;
    protected int ySize = 320;
    protected int currentPageCounter = 0;
    protected int maxPagesFromPrinting = 0;
    protected int linesFromJSON = 0;
    protected int linesFromPrinting = 0;
    protected Identifier prevPageJSON;
    protected Identifier currentPageJSON;
    protected Identifier currentPageText = null;
    protected BookPageButton buttonNextPage;
    protected BookPageButton buttonPreviousPage;
    protected BookPage internalPage = null;
    protected String writtenTitle = "";
    protected int preservedPageIndex = 0;
    protected String entityTooltip;
    private int mouseX;
    private int mouseY;

    public GuiBasicBook(ItemStack bookStack, Component title) {
        super(title);
        this.bookStack = bookStack;
        this.currentPageJSON = getRootPage();
    }

    public static void drawTabulaModelOnScreen(GuiGraphicsExtractor guiGraphics, TabulaModel model, Identifier tex, int posX, int posY, float scale, boolean follow, double xRot, double yRot, double zRot, float mouseX, float mouseY) {
        float f = (float) Math.atan(mouseX / 40.0F);
        float f1 = (float) Math.atan(mouseY / 40.0F);
        PoseStack matrixstack = new PoseStack();
        matrixstack.translate((float) posX, (float) posY, 120.0D);
        matrixstack.scale(scale, scale, scale);
        Quaternionf quaternion = new Quaternionf();
        Quaternionf quaternion1 = new Quaternionf().rotateX(f1 * 20.0F * (float) (Math.PI / 180.0D));
        if (follow) {
            quaternion.mul(quaternion1);
        }
        matrixstack.mulPose(quaternion);
        if (follow) {
            matrixstack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(180.0F + f * 40.0F)));
        }
        matrixstack.mulPose(new Quaternionf().rotateX((float) Math.toRadians(-xRot)));
        matrixstack.mulPose(new Quaternionf().rotateY((float) Math.toRadians(yRot)));
        matrixstack.mulPose(new Quaternionf().rotateZ((float) Math.toRadians(zRot)));
        MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderTypes.entityCutout(tex, false));
        model.resetToDefaultPose();
        model.renderToBuffer(matrixstack, vertexConsumer, 15728880, OverlayTexture.NO_OVERLAY, -1);
        bufferSource.endBatch();
    }

    public void drawEntityOnScreen(GuiGraphicsExtractor guiGraphics, int posX, int posY, float zOff, float scale, boolean follow, double xRot, double yRot, double zRot, float mouseX, float mouseY, Entity entity, float partialTick) {
        float customYaw = posX - mouseX;
        float customPitch = posY - mouseY;
        float f = (float) Math.atan(customYaw / 40.0F);
        float f1 = (float) Math.atan(customPitch / 40.0F);

        if (follow) {
            float setX = f1 * 20.0F;
            float setY = f * 20.0F;
            entity.setXRot(setX);
            entity.setYRot(setY);
            if (entity instanceof LivingEntity livingEntity) {
                livingEntity.yBodyRot = setY;
                livingEntity.yBodyRotO = setY;
                livingEntity.yHeadRot = setY;
                livingEntity.yHeadRotO = setY;
            }
        } else {
            f = 0;
            f1 = 0;
        }

        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        EntityRenderState state = dispatcher.extractEntity(entity, partialTick);
        if (state instanceof LivingEntityRenderState livingState) {
            if (follow) {
                livingState.bodyRot = 180.0F + f * 20.0F;
                livingState.yRot = f * 20.0F;
                if (livingState.pose != Pose.FALL_FLYING) {
                    livingState.xRot = -f1 * 20.0F;
                } else {
                    livingState.xRot = 0.0F;
                }
            }
            livingState.boundingBoxWidth /= livingState.scale;
            livingState.boundingBoxHeight /= livingState.scale;
            livingState.scale = 1.0F;
        }

        Quaternionf qBody = new Quaternionf().rotateZ((float) Math.PI);
        Quaternionf qPitch = new Quaternionf().rotateX(f1 * 20.0F * (float) (Math.PI / 180.0D));
        qBody.mul(qPitch);
        qBody.rotateAxis((float) Math.toRadians(xRot), -1.0F, 0.0F, 0.0F);
        qBody.rotateY((float) Math.toRadians(yRot));
        qBody.rotateZ((float) Math.toRadians(zRot));

        // PiP translation matches InventoryScreen / vanilla PiP: small Y lift for feet, not the portrait scale (~30).
        Vector3f translate = new Vector3f(0.0F, state.boundingBoxHeight / 2.0F + 0.0625F, 0.0F);
        // `scale` here is the PiP model scale (~30 * JSON scale), not pixel radius. Old `40 * scale` produced ~1200px half-bounds and broke/clipped book portraits.
        int half = Math.max(40, Math.min(100, Math.round(scale * 2.15F)));
        // JSON (x, y) matched 1.21.1 translate(posX, posY) at the entity origin. A symmetric PiP rect centered on y
        // sat too low (over body text); a bottom-anchored rect sat too high (over title). Blend between those
        // vertical centers: t=0 → same center as bottom-anchored portrait; t=1 → centered on posY (raise t to move down).
        final float bookEntityPipVerticalBlend = 0.58F;
        int centerY = posY - Math.round((1.0F - bookEntityPipVerticalBlend) * half);
        int x0 = posX - half;
        int x1 = posX + half;
        int y0 = centerY - half;
        int y1 = centerY + half;
        guiGraphics.entity(state, scale, translate, qBody, qPitch, x0, y0, x1, y1);

        entity.setYRot(0);
        entity.setXRot(0);
        if (entity instanceof LivingEntity livingEntity) {
            livingEntity.yBodyRot = 0;
            livingEntity.yHeadRotO = 0;
            livingEntity.yHeadRot = 0;
        }
    }

    protected void init() {
        super.init();
        playBookOpeningSound();
        addLinkButtons();
    }

    private void addNextPreviousButtons() {
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize + 128) / 2;
        this.buttonPreviousPage = this.addRenderableWidget(new BookPageButton(this, k + 10, l + 180, false, (p_214208_1_) -> this.onSwitchPage(false), true));
        this.buttonNextPage = this.addRenderableWidget(new BookPageButton(this, k + 365, l + 180, true, (p_214205_1_) -> this.onSwitchPage(true), true));
    }

    private void addLinkButtons() {
        this.clearWidgets();
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize + 128) / 2;

        for (LinkData linkData : links) {
            if (linkData.getPage() == this.currentPageCounter) {
                int maxLength = Math.max(100, Minecraft.getInstance().font.width(linkData.getTitleText()) + 20);
                yIndexesToSkip.add(new Whitespace(linkData.getPage(), linkData.getX() - maxLength / 2, linkData.getY(), 100, 20));
                this.addRenderableWidget(new LinkButton(this, k + linkData.getX() - maxLength / 2, l + linkData.getY(), maxLength, 20, Component.translatable(linkData.getTitleText()), linkData.getDisplayItem(), (p_213021_1_) -> {
                    prevPageJSON = this.currentPageJSON;
                    currentPageJSON = Identifier.parse(getTextFileDirectory() + linkData.getLinkedPage());
                    preservedPageIndex = this.currentPageCounter;
                    currentPageCounter = 0;
                }));
            }
            if (linkData.getPage() > this.maxPagesFromPrinting) {
                this.maxPagesFromPrinting = linkData.getPage();
            }
        }

        for (EntityLinkData linkData : entityLinks) {
            if (linkData.getPage() == this.currentPageCounter) {
                yIndexesToSkip.add(new Whitespace(linkData.getPage(), linkData.getX() - 12, linkData.getY(), 100, 20));
                this.addRenderableWidget(new EntityLinkButton(this, linkData, k, l, (p_213021_1_) -> {
                    prevPageJSON = this.currentPageJSON;
                    currentPageJSON = Identifier.parse(getTextFileDirectory() + linkData.getLinkedPage());
                    preservedPageIndex = this.currentPageCounter;
                    currentPageCounter = 0;
                }));
            }
            if (linkData.getPage() > this.maxPagesFromPrinting) {
                this.maxPagesFromPrinting = linkData.getPage();
            }
        }
        // 26.1 deferred GUI: later widgets paint above earlier ones. Same registration order as 1.21.1 leaves arrows under the slot grid (same y-band).
        addNextPreviousButtons();
    }

    private void onSwitchPage(boolean next) {
        if (next) {
            if (currentPageCounter < maxPagesFromPrinting) {
                currentPageCounter++;
            }
        } else {
            if (currentPageCounter > 0) {
                currentPageCounter--;
            } else {
                if (this.internalPage != null && !this.internalPage.getParent().isEmpty()) {
                    prevPageJSON = this.currentPageJSON;
                    currentPageJSON = Identifier.parse(getTextFileDirectory() + this.internalPage.getParent());
                    currentPageCounter = preservedPageIndex;
                    preservedPageIndex = 0;
                }
            }
        }
        refreshSpacing();
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor guiGraphics, int x, int y, float partialTicks) {
        this.mouseX = x;
        this.mouseY = y;
        int bindingColor = getBindingColor();
        int bindingR = bindingColor >> 16 & 255;
        int bindingG = bindingColor >> 8 & 255;
        int bindingB = bindingColor & 255;
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize + 128) / 2;
        BookBlit.blitWithColor(guiGraphics, getBookBindingTexture(), k, l, 0, 0, xSize, ySize, xSize, ySize, bindingR, bindingG, bindingB, 255);
        BookBlit.blitWithColor(guiGraphics, getBookPageTexture(), k, l, 0, 0, xSize, ySize, xSize, ySize, 255, 255, 255, 255);
        if (internalPage == null || currentPageJSON != prevPageJSON || prevPageJSON == null) {
            internalPage = generatePage(currentPageJSON);
            if (internalPage != null) {
                refreshSpacing();
            }
        }
        // Deferred PiP (entities, Tabula) must be recorded *before* page text so the next stratum paints prose on top
        // (1.21.1 achieved the same with depth via translate(..., zOff); 26.1 PiP composites by stratum order).
        if (internalPage != null) {
            guiGraphics.nextStratum();
            guiGraphics.pose().pushMatrix();
            renderBookPipEmbeds(guiGraphics, partialTicks);
            guiGraphics.pose().popMatrix();
            guiGraphics.nextStratum();
            writePageText(guiGraphics, x, y);
        }
        guiGraphics.nextStratum();
        super.extractRenderState(guiGraphics, x, y, partialTicks);
        prevPageJSON = currentPageJSON;
        if (internalPage != null) {
            guiGraphics.pose().pushMatrix();
            renderBookFlatEmbeds(guiGraphics, partialTicks);
            guiGraphics.pose().popMatrix();
        }
        // Page arrows were drawn in super with other widgets; flat embeds (images, recipes, items) run after text.
        // Blit arrows again on a new stratum so citadel:textures/gui/book/widgets.png regions stay visible (same UVs as 1.21.1).
        if (this.deferPageArrowsToPostPass() && this.buttonPreviousPage != null && this.buttonNextPage != null) {
            guiGraphics.nextStratum();
            if (this.buttonPreviousPage.visible) {
                this.buttonPreviousPage.blitPageArrowPostPass(guiGraphics, x, y);
            }
            if (this.buttonNextPage.visible) {
                this.buttonNextPage.blitPageArrowPostPass(guiGraphics, x, y);
            }
        }
        if (this.entityTooltip != null) {
            guiGraphics.setTooltipForNextFrame(font, Minecraft.getInstance().font.split(Component.translatable(entityTooltip), Math.max(this.width / 2 - 43, 170)), x, y);
            entityTooltip = null;
        }
    }

    private void refreshSpacing() {
        if (internalPage != null) {
            String lang = Minecraft.getInstance().getLanguageManager().getSelected().toLowerCase();
            currentPageText = Identifier.parse(getTextFileDirectory() + lang + "/" + internalPage.getTextFileToReadFrom());
            boolean invalid = false;
            try {
                //test if it exists. if no exception, then the language is supported
                InputStream is = Minecraft.getInstance().getResourceManager().open(currentPageText);
                is.close();
            } catch (Exception e) {
                invalid = true;
                Citadel.LOGGER.warn("Could not find language file for translation, defaulting to english");
                currentPageText = Identifier.parse(getTextFileDirectory() + "en_us/" + internalPage.getTextFileToReadFrom());
            }

            readInPageWidgets(internalPage);
            addWidgetSpacing();
            addLinkButtons();
            int maxPagesFromEntityWidgets = this.maxPagesFromPrinting;
            readInPageText(currentPageText);
            this.maxPagesFromPrinting = Math.max(this.maxPagesFromPrinting, maxPagesFromEntityWidgets);
        }
    }

    private Item getItemByRegistryName(String registryName) {
        return BuiltInRegistries.ITEM.get(Identifier.parse(registryName)).map(Holder.Reference::value).orElse(Items.AIR);
    }

    @Nullable
    private Recipe<?> getRecipeByName(String registryName) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.level == null) {
            return null;
        }
        try {
            ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, Identifier.parse(registryName));
            return mc.level.registryAccess()
                .lookup(Registries.RECIPE)
                .flatMap(reg -> reg.get(key))
                .map(Holder.Reference::value)
                .orElse(null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void addWidgetSpacing() {
        yIndexesToSkip.clear();
        for (ItemRenderData itemRenderData : itemRenders) {
            Item item = getItemByRegistryName(itemRenderData.getItem());
            yIndexesToSkip.add(new Whitespace(itemRenderData.getPage(), itemRenderData.getX(), itemRenderData.getY(), (int) (itemRenderData.getScale() * 17), (int) (itemRenderData.getScale() * 15)));

        }
        for (RecipeData recipeData : recipes) {
            Recipe recipe = getRecipeByName(recipeData.getRecipe());
            if (recipe != null) {
                yIndexesToSkip.add(new Whitespace(recipeData.getPage(), recipeData.getX(), recipeData.getY() - (int) (recipeData.getScale() * 15), (int) (recipeData.getScale() * 35), (int) (recipeData.getScale() * 60), true));
            }
        }
        for (ImageData imageData : images) {
            if (imageData != null) {
                yIndexesToSkip.add(new Whitespace(imageData.getPage(), imageData.getX(), imageData.getY(), (int) (imageData.getScale() * imageData.getWidth()), (int) (imageData.getScale() * imageData.getHeight() * 0.8F)));
            }
        }
        if (!writtenTitle.isEmpty()) {
            yIndexesToSkip.add(new Whitespace(0, 20, 5, 70, 15));
        }
    }

    /** Tabula + JSON entity portraits (PiP). Recorded in an earlier stratum than {@link #writePageText} so prose draws on top. */
    private void renderBookPipEmbeds(GuiGraphicsExtractor guiGraphics, float partialTicks) {
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize + 128) / 2;

        for (TabulaRenderData tabulaRenderData : tabulaRenders) {
            if (tabulaRenderData.getPage() == this.currentPageCounter) {
                TabulaModel model = null;
                Identifier texture;
                if (textureMap.get(tabulaRenderData.getTexture()) != null) {
                    texture = textureMap.get(tabulaRenderData.getTexture());
                } else {
                    texture = textureMap.put(tabulaRenderData.getTexture(), Identifier.parse(tabulaRenderData.getTexture()));
                }
                if (renderedTabulaModels.get(tabulaRenderData.getModel()) != null) {
                    model = renderedTabulaModels.get(tabulaRenderData.getModel());
                } else {
                    try {
                        model = new TabulaModel(TabulaModelHandler.INSTANCE.loadTabulaModel("/assets/" + tabulaRenderData.getModel().split(":")[0] + "/" + tabulaRenderData.getModel().split(":")[1]));
                    } catch (Exception e) {
                        Citadel.LOGGER.warn("Could not load in tabula model for book at {}", tabulaRenderData.getModel());
                    }
                    renderedTabulaModels.put(tabulaRenderData.getModel(), model);
                }

                if (model != null && texture != null) {
                    float scale = (float) tabulaRenderData.getScale();
                    drawTabulaModelOnScreen(guiGraphics, model, texture, k + tabulaRenderData.getX(), l + tabulaRenderData.getY(), 30 * scale, tabulaRenderData.isFollow_cursor(), tabulaRenderData.getRot_x(), tabulaRenderData.getRot_y(), tabulaRenderData.getRot_z(), mouseX, mouseY);
                }
            }
        }
        for (EntityRenderData data : entityRenders) {
            if (data.getPage() == this.currentPageCounter) {
                Entity model = null;
                EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.get(Identifier.parse(data.getEntity())).map(Holder.Reference::value).orElse(null);
                if (type != null) {
                    model = renderedEntites.putIfAbsent(data.getEntity(), type.create(Minecraft.getInstance().level, EntitySpawnReason.LOAD));
                }
                if (model != null) {
                    float scale = (float) data.getScale();
                    model.tickCount = Minecraft.getInstance().player.tickCount;
                    if (data.getEntityData() != null) {
                        try {
                            CompoundTag tag = TagParser.parseCompoundFully(data.getEntityData());
                            model.load(TagValueInput.create(ProblemReporter.DISCARDING, Minecraft.getInstance().level.registryAccess(), tag));
                        } catch (CommandSyntaxException e) {
                            e.printStackTrace();
                        }
                    }
                    drawEntityOnScreen(guiGraphics, k + data.getX(), l + data.getY(), 1050F, 30 * scale, data.isFollow_cursor(), data.getRot_x(), data.getRot_y(), data.getRot_z(), mouseX, mouseY, model, partialTicks);
                }
            }
        }
    }

    /** Images, recipe chrome, recipe contents, item icons — after page text so they stay on top of prose where intended. */
    private void renderBookFlatEmbeds(GuiGraphicsExtractor guiGraphics, float partialTicks) {
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize + 128) / 2;

        for (ImageData imageData : images) {
            if (imageData.getPage() == this.currentPageCounter) {
                Identifier tex = textureMap.get(imageData.getTexture());
                if (tex == null) {
                    tex = Identifier.parse(imageData.getTexture());
                    textureMap.put(imageData.getTexture(), tex);
                }
                float scale = (float) imageData.getScale();
                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(k + imageData.getX(), l + imageData.getY());
                guiGraphics.pose().scale(scale, scale);
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, tex, 0, 0, (float) imageData.getU(), (float) imageData.getV(), imageData.getWidth(), imageData.getHeight(), 256, 256);
                guiGraphics.pose().popMatrix();
            }
        }
        for (RecipeData recipeData : recipes) {
            if (recipeData.getPage() == this.currentPageCounter) {
                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(k + recipeData.getX(), l + recipeData.getY());
                float scale = (float) recipeData.getScale();
                guiGraphics.pose().scale(scale, scale);
                guiGraphics.blit(RenderPipelines.GUI_TEXTURED, getBookWidgetTexture(), 0, 0, 0.0F, 88.0F, 116, 53, 256, 256);
                guiGraphics.pose().popMatrix();
            }
        }
        for (RecipeData recipeData : recipes) {
            if (recipeData.getPage() == this.currentPageCounter) {
                Recipe<?> recipe = getRecipeByName(recipeData.getRecipe());
                if (recipe != null) {
                    renderRecipe(guiGraphics, recipe, recipeData, k, l, partialTicks);
                }
            }
        }
        for (ItemRenderData itemRenderData : itemRenders) {
            if (itemRenderData.getPage() == this.currentPageCounter) {
                Item item = getItemByRegistryName(itemRenderData.getItem());
                float scale = (float) itemRenderData.getScale();
                ItemStack stack = new ItemStack(item);
                if (itemRenderData.getItemTag() != null && !itemRenderData.getItemTag().isEmpty()) {
                    try {
                        CompoundTag parsedTag = TagParser.parseCompoundFully(itemRenderData.getItemTag());
                        var ops = RegistryOps.create(NbtOps.INSTANCE, Minecraft.getInstance().level.registryAccess());
                        ItemStack parsedStack = ItemStack.CODEC.parse(ops, parsedTag).result().orElse(ItemStack.EMPTY);
                        if (!parsedStack.isEmpty()) {
                            stack = parsedStack;
                        }
                    } catch (CommandSyntaxException e) {
                        e.printStackTrace();
                    }
                }
                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(k, l);
                guiGraphics.pose().scale(scale, scale);
                guiGraphics.item(stack, itemRenderData.getX(), itemRenderData.getY());
                guiGraphics.pose().popMatrix();
            }
        }
    }

    protected void renderRecipe(GuiGraphicsExtractor guiGraphics, Recipe<?> recipe, RecipeData recipeData, int k, int l, float partialTicks) {
        int playerTicks = Minecraft.getInstance().player.tickCount;
        float scale = (float) recipeData.getScale();
        NonNullList<Ingredient> ingredients;
        if (recipe instanceof SpecialRecipeInGuideBook guideBookRecipe) {
            ingredients = guideBookRecipe.getDisplayIngredients();
        } else {
            java.util.List<Ingredient> fromPlacement = recipe.placementInfo().ingredients();
            ingredients = NonNullList.createWithCapacity(fromPlacement.size());
            ingredients.addAll(fromPlacement);
        }
        NonNullList<ItemStack> displayedStacks = NonNullList.create();

        for (int i = 0; i < ingredients.size(); i++) {
            Ingredient ing = ingredients.get(i);
            ItemStack stack = ItemStack.EMPTY;
            if (!ing.isEmpty()) {
                java.util.List<ItemStack> choices = ing.items().map(h -> new ItemStack(h, 1)).toList();
                if (choices.size() > 1) {
                    int currentIndex = (int) ((playerTicks / 20F) % choices.size());
                    stack = choices.get(currentIndex);
                } else {
                    stack = choices.get(0);
                }
            }
            if (!stack.isEmpty()) {
                guiGraphics.pose().pushMatrix();
                guiGraphics.pose().translate(k, l);
                guiGraphics.pose().translate((int) (recipeData.getX() + (i % 3) * 20 * scale), (int) (recipeData.getY() + (i / 3) * 20 * scale));
                guiGraphics.pose().scale(scale, scale);
                guiGraphics.item(stack, 0, 0, 32);
                guiGraphics.pose().popMatrix();
            }
            displayedStacks.add(i, stack);
        }
        guiGraphics.pose().pushMatrix();
        guiGraphics.pose().translate(k, l);
        float finScale = scale * 1.5F;
        guiGraphics.pose().translate(recipeData.getX() + 70 * finScale, recipeData.getY() + 10 * finScale);
        guiGraphics.pose().scale(finScale, finScale);
        ItemStack result;
        if (recipe instanceof SpecialRecipeInGuideBook specialRecipeInGuideBook) {
            result = specialRecipeInGuideBook.getDisplayResultFor(displayedStacks);
        } else {
            @SuppressWarnings("unchecked")
            Recipe<CraftingInput> craftingRecipe = (Recipe<CraftingInput>) (Object) recipe;
            result = craftingRecipe.assemble(CraftingInput.EMPTY);
        }
        guiGraphics.item(result, 0, 0, 100);
        guiGraphics.pose().popMatrix();
    }

    protected void writePageText(GuiGraphicsExtractor guiGraphics, int x, int y) {
        Font font = this.font;
        int k = (this.width - this.xSize) / 2;
        int l = (this.height - this.ySize + 128) / 2;
        for (LineData line : this.lines) {
            if (line.getPage() == this.currentPageCounter) {
                guiGraphics.text(font, line.getText(), k + 10 + line.getxIndex(), l + 10 + line.getyIndex() * 12, getTextColor(), false);
            }
        }
        if (this.currentPageCounter == 0 && !writtenTitle.isEmpty()) {
            String actualTitle = I18n.get(writtenTitle);
            guiGraphics.pose().pushMatrix();
            float scale = 2F;
            if (font.width(actualTitle) > 80) {
                scale = 2.0F - Mth.clamp((font.width(actualTitle) - 80) * 0.011F, 0, 1.95F);
            }
            guiGraphics.pose().translate(k + 10, l + 10);
            guiGraphics.pose().scale(scale, scale);
            guiGraphics.text(font, actualTitle, 0, 0, getTitleColor(), false);
            guiGraphics.pose().popMatrix();
        }
        this.buttonNextPage.visible = currentPageCounter < maxPagesFromPrinting;
        this.buttonPreviousPage.visible = currentPageCounter > 0 || !currentPageJSON.equals(this.getRootPage());
    }

    public boolean isPauseScreen() {
        return false;
    }

    protected void playBookOpeningSound() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.BOOK_PAGE_TURN, 1.0F));
    }

    protected void playBookClosingSound() {
    }

    protected abstract int getBindingColor();

    protected int getWidgetColor() {
        return getBindingColor();
    }

    /**
     * When true, {@link BookPageButton} skips drawing in the widget pass and draws after {@link #renderBookFlatEmbeds}
     * so deferred GUI does not leave arrows under JSON-driven blits (26.1). Same atlas/UVs as 1.21.1.
     */
    protected boolean deferPageArrowsToPostPass() {
        return true;
    }

    protected int getTextColor() {
        // GuiGraphicsExtractor.text() skips when ARGB.alpha(color) == 0; 0x303030 alone has alpha 0.
        return 0xFF303030;
    }

    protected int getTitleColor() {
        return 0xFFBAAC98;
    }

    public abstract Identifier getRootPage();

    public abstract String getTextFileDirectory();

    protected Identifier getBookPageTexture() {
        return BOOK_PAGE_TEXTURE;
    }

    protected Identifier getBookBindingTexture() {
        return BOOK_BINDING_TEXTURE;
    }

    protected Identifier getBookWidgetTexture() {
        return BOOK_WIDGET_TEXTURE;
    }

    protected void playPageFlipSound() {
    }

    @Nullable
    protected BookPage generatePage(Identifier res) {
        Optional<Resource> resource;
        BookPage page = null;
        try {
            resource = Minecraft.getInstance().getResourceManager().getResource(res);
            if (resource.isPresent()) {
                BufferedReader inputstream = resource.get().openAsReader();
                page = BookPage.deserialize(inputstream);
            }

        } catch (IOException e1) {
            e1.printStackTrace();
            return null;
        }
        return page;
    }

    protected void readInPageWidgets(BookPage page) {
        links.clear();
        itemRenders.clear();
        recipes.clear();
        tabulaRenders.clear();
        entityRenders.clear();
        images.clear();
        entityLinks.clear();
        links.addAll(page.getLinkedButtons());
        entityLinks.addAll(page.getLinkedEntities());
        itemRenders.addAll(page.getItemRenders());
        recipes.addAll(page.getRecipes());
        tabulaRenders.addAll(page.getTabulaRenders());
        entityRenders.addAll(page.getEntityRenders());
        images.addAll(page.getImages());
        writtenTitle = page.generateTitle();
    }

    protected void readInPageText(Identifier res) {
        Resource resource = null;
        int xIndex = 0;
        int actualTextX = 0;
        int yIndex = 0;
        try {
            BufferedReader bufferedreader = Minecraft.getInstance().getResourceManager().openAsReader(res);
            try {
                List<String> readStrings = IOUtils.readLines(bufferedreader);
                this.linesFromJSON = readStrings.size();
                this.lines.clear();
                List<String> splitBySpaces = new ArrayList<>();
                for (String line : readStrings) {
                    splitBySpaces.addAll(Arrays.asList(line.split(" ")));
                }
                String lineToPrint = "";
                linesFromPrinting = 0;
                int page = 0;
                for (int i = 0; i < splitBySpaces.size(); i++) {
                    String word = splitBySpaces.get(i);
                    int cutoffPoint = xIndex > 100 ? 30 : 35;
                    boolean newline = word.equals("<NEWLINE>");
                    for (Whitespace indexes : yIndexesToSkip) {
                        int indexPage = indexes.getPage();
                        if (indexPage == page) {
                            int buttonX = indexes.getX();
                            int buttonY = indexes.getY();
                            int width = indexes.getWidth();
                            int height = indexes.getHeight();
                            if (indexes.isDown()) {
                                if (yIndex >= (buttonY) / 12F && yIndex <= (buttonY + height) / 12F) {
                                    if (buttonX < 90 && xIndex < 90 || buttonX >= 90 && xIndex >= 90) {
                                        yIndex += 2;
                                    }
                                }
                            } else {
                                if (yIndex >= (buttonY - height) / 12F && yIndex <= (buttonY + height) / 12F) {
                                    if (buttonX < 90 && xIndex < 90 || buttonX >= 90 && xIndex >= 90) {
                                        yIndex++;
                                    }
                                }
                            }
                        }
                    }
                    boolean last = i == splitBySpaces.size() - 1;
                    actualTextX += word.length() + 1;
                    if (lineToPrint.length() + word.length() + 1 >= cutoffPoint || newline) {
                        linesFromPrinting++;
                        if (yIndex > 13) {
                            if (xIndex > 0) {
                                page++;
                                xIndex = 0;
                                yIndex = 0;
                            } else {
                                xIndex = 200;
                                yIndex = 0;
                            }
                        }
                        if (last) {
                            lineToPrint = lineToPrint + " " + word;
                        }
                        this.lines.add(new LineData(xIndex, yIndex, lineToPrint, page));
                        yIndex++;
                        actualTextX = 0;
                        if (newline) {
                            yIndex++;
                        }
                        lineToPrint = word.equals("<NEWLINE>") ? "" : word;
                    } else {
                        lineToPrint = lineToPrint + " " + word;
                        if (last) {
                            linesFromPrinting++;
                            this.lines.add(new LineData(xIndex, yIndex, lineToPrint, page));
                            yIndex++;
                            actualTextX = 0;
                        }
                    }
                }
                maxPagesFromPrinting = page;
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        } catch (Exception e) {
            Citadel.LOGGER.warn("Could not load in page .txt from json from page, page: {}", res);
        }
    }

    public void setEntityTooltip(String hoverText) {
        this.entityTooltip = hoverText;
    }

    public Identifier getBookButtonsTexture() {
        return BOOK_BUTTONS_TEXTURE;
    }
}
