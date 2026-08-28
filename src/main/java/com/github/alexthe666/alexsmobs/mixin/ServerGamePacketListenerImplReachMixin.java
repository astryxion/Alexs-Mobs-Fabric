package com.github.alexthe666.alexsmobs.mixin;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import com.github.alexthe666.alexsmobs.item.ItemModArmor;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ServerGamePacketListenerImpl.class)
public class ServerGamePacketListenerImplReachMixin {

    @Shadow
    public ServerPlayer player;

    @Redirect(
            method = "handleInteract",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/server/network/ServerGamePacketListenerImpl;MAX_INTERACTION_DISTANCE:D",
                    opcode = org.objectweb.asm.Opcodes.GETSTATIC
            )
    )
    private double alexsmobs$entityReachDistance() {
        double vanillaPacketReach = 6.0D;
        if (player == null) {
            return vanillaPacketReach * vanillaPacketReach;
        }
        double packetReach = vanillaPacketReach + getKimonoEntityReachBonus(player);
        return packetReach * packetReach;
    }

    private static double getKimonoEntityReachBonus(ServerPlayer player) {
        if (AMItemRegistry.ENTITY_REACH_ATTRIBUTE != null) {
            AttributeInstance attr = player.getAttribute(AMItemRegistry.ENTITY_REACH_ATTRIBUTE);
            if (attr != null) {
                double bonus = Math.max(0.0D, attr.getValue() - attr.getBaseValue());
                if (bonus > 0.0D) {
                    return bonus;
                }
            }
        }
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.getItem() instanceof ItemModArmor armor && armor.getArmorMaterialWrapper() == AMItemRegistry.KIMONO_MATERIAL) {
            return 2.0D;
        }
        return 0.0D;
    }
}
