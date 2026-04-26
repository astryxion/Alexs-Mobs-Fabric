package com.github.alexthe666.citadel.mixin;

import com.github.alexthe666.citadel.Citadel;
import com.github.alexthe666.citadel.CitadelConstants;
import com.github.alexthe666.citadel.server.entity.ICitadelDataEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Pre-26.1 Citadel stored custom NBT in {@link net.minecraft.network.syncher.SynchedEntityData}; NeoForge now rejects
 * {@link net.minecraft.network.syncher.SynchedEntityData#defineId} on entity classes touched by mod mixins. Use a
 * {@link net.neoforged.neoforge.attachment.AttachmentType} instead (see {@link Citadel#CITADEL_ENTITY_DATA}).
 */
@Mixin(LivingEntity.class)
public class LivingEntityMixin implements ICitadelDataEntity {
    @Unique
    private CompoundTag citadel$data = new CompoundTag();

    @Override
    public CompoundTag getCitadelEntityData() {
        return this.citadel$data.copy();
    }

    @Override
    public void setCitadelEntityData(CompoundTag nbt) {
        this.citadel$data = nbt == null ? new CompoundTag() : nbt.copy();
    }

    /**
     * Worlds saved before the attachment migration still have {@code CitadelData} under entity load; copy into the attachment if needed.
     */
    @Inject(at = @At("TAIL"), remap = CitadelConstants.REMAPREFS, method = "readAdditionalSaveData")
    private void citadel_migrateLegacyCitadelData(ValueInput input, CallbackInfo ci) {
        input.read("CitadelData", CompoundTag.CODEC).ifPresent(legacy -> {
            if (this.citadel$data.isEmpty()) {
                this.citadel$data = legacy.copy();
            }
        });
    }

    @Inject(at = @At("TAIL"), remap = CitadelConstants.REMAPREFS, method = "addAdditionalSaveData")
    private void citadel_writeCitadelData(ValueOutput output, CallbackInfo ci) {
        if (!this.citadel$data.isEmpty()) {
            output.store("CitadelData", CompoundTag.CODEC, this.citadel$data);
        }
    }
}
