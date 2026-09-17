package com.github.alexthe666.citadel.mixin.client;

import com.github.alexthe666.citadel.client.event.EventGetOutlineColor;
import net.minecraft.util.TriState;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityTeamColorMixin {

    @Inject(method = "getTeamColor", at = @At("RETURN"), cancellable = true)
    private void citadel_outlineTeamColor(CallbackInfoReturnable<Integer> cir) {
        Entity self = (Entity) (Object) this;
        int color = cir.getReturnValue();
        EventGetOutlineColor event = new EventGetOutlineColor(self, color);
        event.post();
        if (event.getResult() == TriState.TRUE) {
            cir.setReturnValue(event.getColor());
        }
    }
}
