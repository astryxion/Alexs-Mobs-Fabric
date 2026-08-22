package com.github.alexthe666.alexsmobs.mixin.client;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.item.ILeftClick;
import com.github.alexthe666.alexsmobs.message.MessageSwingArm;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.HitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Minecraft.class)
public class MinecraftAttackMixin {

    @Inject(method = "startAttack", at = @At("HEAD"))
    private void alexsmobs$leftClickEmpty(CallbackInfoReturnable<Boolean> cir) {
        Minecraft mc = (Minecraft) (Object) this;
        if (mc.player == null || mc.level == null) {
            return;
        }
        // Falconry glove must fire even when looking at a block (not only air/MISS).
        ItemStack left = mc.player.getItemInHand(InteractionHand.OFF_HAND);
        ItemStack right = mc.player.getItemInHand(InteractionHand.MAIN_HAND);
        boolean holdingFalconry = left.is(com.github.alexthe666.alexsmobs.item.AMItemRegistry.FALCONRY_GLOVE)
                || right.is(com.github.alexthe666.alexsmobs.item.AMItemRegistry.FALCONRY_GLOVE);
        if (!holdingFalconry && mc.hitResult != null && mc.hitResult.getType() != HitResult.Type.MISS) {
            return;
        }
        boolean flag = false;
        if (left.getItem() instanceof ILeftClick click) {
            click.onLeftClick(left, mc.player);
            flag = true;
        }
        if (right.getItem() instanceof ILeftClick click) {
            click.onLeftClick(right, mc.player);
            flag = true;
        }
        if (flag) {
            AlexsMobs.sendMSGToServer(MessageSwingArm.INSTANCE);
        }
    }
}
