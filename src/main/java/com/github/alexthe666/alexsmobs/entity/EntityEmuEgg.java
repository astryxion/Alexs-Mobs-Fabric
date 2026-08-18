package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
// NetworkHooks removed in NeoForge 1.21

public class EntityEmuEgg extends ThrowableItemProjectile {

    public EntityEmuEgg(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public EntityEmuEgg(Level worldIn, LivingEntity throwerIn) {
        super(AMEntityRegistry.EMU_EGG, throwerIn, worldIn, new ItemStack(AMItemRegistry.EMU_EGG));
    }

    public EntityEmuEgg(Level worldIn, double x, double y, double z) {
        super(AMEntityRegistry.EMU_EGG, x, y, z, worldIn, new ItemStack(AMItemRegistry.EMU_EGG));
    }
    // getAddEntityPacket is no longer needed in 1.21
    // public Packet<ClientGamePacketListener> getAddEntityPacket() {
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem().getItem()), this.getX(), this.getY(), this.getZ(), ((double) this.random.nextFloat() - 0.5D) * 0.08D, ((double) this.random.nextFloat() - 0.5D) * 0.08D, ((double) this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }

    }

    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide() && this.level() instanceof ServerLevel serverLevel) {
            if (this.random.nextInt(8) == 0) {
                int lvt_2_1_ = 1;
                if (this.random.nextInt(32) == 0) {
                    lvt_2_1_ = 4;
                }
                for (int lvt_3_1_ = 0; lvt_3_1_ < lvt_2_1_; ++lvt_3_1_) {
                    EntityEmu lvt_4_1_ = AMEntityRegistry.EMU.create(serverLevel, EntitySpawnReason.TRIGGERED);
                    if(this.random.nextInt(50) == 0){
                        lvt_4_1_.setVariant(2);
                    }else if(random.nextInt(3) == 0){
                        lvt_4_1_.setVariant(1);
                    }
                    lvt_4_1_.setAge(-24000);
                    lvt_4_1_.snapTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                    this.level().addFreshEntity(lvt_4_1_);
                }
            }
            this.level().broadcastEntityEvent(this, (byte) 3);
            this.remove(RemovalReason.DISCARDED);
        }

    }

    protected Item getDefaultItem() {
        return AMItemRegistry.EMU_EGG;
    }
}
