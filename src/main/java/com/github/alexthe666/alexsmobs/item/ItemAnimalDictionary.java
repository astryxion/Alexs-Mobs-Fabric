package com.github.alexthe666.alexsmobs.item;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.entity.*;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ItemAnimalDictionary extends Item {
    public ItemAnimalDictionary(Properties properties) {
        super(properties);
    }

    private boolean usedOnEntity = false;

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player playerIn, LivingEntity target, InteractionHand hand) {
        ItemStack itemStackIn = playerIn.getItemInHand(hand);
        if (playerIn instanceof ServerPlayer) {
            ServerPlayer serverplayerentity = (ServerPlayer)playerIn;
            CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, itemStackIn);
            serverplayerentity.awardStat(Stats.ITEM_USED.get(this));
            // Trigger the "Alex's Mobs" root advancement when opening the dictionary
            AMAdvancementTriggerRegistry.OPEN_ANIMAL_DICTIONARY.trigger(serverplayerentity);
        }
        Identifier entityId = BuiltInRegistries.ENTITY_TYPE.getKey(target.getType());
        if (playerIn.level().isClientSide() && entityId != null && AlexsMobs.MODID.equals(entityId.getNamespace())) {
            usedOnEntity = true;
            String id = entityId.getPath();
            if(target instanceof EntityBoneSerpent || target instanceof EntityBoneSerpentPart){
                id = "bone_serpent";
            }
            if(target instanceof EntityCentipedeHead || target instanceof EntityCentipedeBody || target instanceof EntityCentipedeTail){
                id = "cave_centipede";
            }
            if(target instanceof EntityVoidWorm || target instanceof EntityVoidWormPart){
                id = "void_worm";
            }
            if(target instanceof EntityAnaconda || target instanceof EntityAnacondaPart){
                id = "anaconda";
            }
            if(target instanceof EntityMurmur || target instanceof EntityMurmurHead){
                id = "murmur";
            }
            AlexsMobs.PROXY.openBookGUI(itemStackIn, id);
        }
        return InteractionResult.CONSUME;
    }

    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand handIn) {
        ItemStack itemStackIn = playerIn.getItemInHand(handIn);
        if (!usedOnEntity) {
            if (playerIn instanceof ServerPlayer) {
                ServerPlayer serverplayerentity = (ServerPlayer) playerIn;
                CriteriaTriggers.CONSUME_ITEM.trigger(serverplayerentity, itemStackIn);
                serverplayerentity.awardStat(Stats.ITEM_USED.get(this));
                // Trigger the "Alex's Mobs" root advancement when opening the dictionary
                AMAdvancementTriggerRegistry.OPEN_ANIMAL_DICTIONARY.trigger(serverplayerentity);
            }
            if (worldIn.isClientSide()) {
                AlexsMobs.PROXY.openBookGUI(itemStackIn);
            }
        }
        usedOnEntity = false;

        return InteractionResult.PASS;
    }

    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable("item.alexsmobs.animal_dictionary.desc").withStyle(ChatFormatting.GRAY));
    }
}
