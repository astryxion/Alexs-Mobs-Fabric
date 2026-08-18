package com.github.alexthe666.alexsmobs.mixin.client;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.client.render.AMItemstackRenderer;
import net.minecraft.client.renderer.item.ItemModelResolver;
import net.minecraft.client.renderer.item.ItemStackRenderState;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ItemOwner;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Set;

@Mixin(ItemModelResolver.class)
public abstract class ItemModelResolverSpecialItemMixin {
    private static final Set<Identifier> ALEXSMOBS_SPECIAL_ITEM_IDS = Set.of(
            Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "shield_of_the_deep"),
            Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "mysterious_worm"),
            Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "falconry_glove"),
            Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "vine_lasso"),
            Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "skelewag_sword"),
            Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "tab_icon"),
            Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "shattered_dimensional_carver"),
            Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "stink_ray"),
            Identifier.fromNamespaceAndPath(AlexsMobs.MODID, "transmutation_table"));

    @Inject(method = "updateForTopItem", at = @At("HEAD"), cancellable = true)
    private void alexsmobs$routeSpecialItemModels(ItemStackRenderState state, ItemStack stack, ItemDisplayContext displayContext, Level level, ItemOwner owner, int seed, CallbackInfo ci) {
        Identifier itemId = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (!ALEXSMOBS_SPECIAL_ITEM_IDS.contains(itemId)) {
            return;
        }
        state.appendModelIdentityElement(AMItemstackRenderer.SpecialItemModel.INSTANCE);
        state.setAnimated();
        ItemStackRenderState.LayerRenderState layer = state.newLayer();
        if (stack.hasFoil()) {
            layer.setFoilType(ItemStackRenderState.FoilType.STANDARD);
            state.appendModelIdentityElement(ItemStackRenderState.FoilType.STANDARD);
        }
        layer.setExtents(ItemStackRenderState.LayerRenderState.NO_EXTENTS_SUPPLIER);
        layer.setupSpecialModel(AMItemstackRenderer.INSTANCE, new AMItemstackRenderer.AmSpecialItemPayload(stack, displayContext, owner));
        state.appendModelIdentityElement(stack);
        ci.cancel();
    }
}
