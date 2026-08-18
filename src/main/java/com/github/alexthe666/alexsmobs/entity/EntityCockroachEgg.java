package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
// NetworkHooks removed in NeoForge 1.21

public class EntityCockroachEgg extends ThrowableItemProjectile {

    public EntityCockroachEgg(EntityType<? extends ThrowableItemProjectile> type, Level level) {
        super(type, level);
    }

    public EntityCockroachEgg(Level worldIn, LivingEntity throwerIn) {
        super(AMEntityRegistry.COCKROACH_EGG, throwerIn, worldIn, new ItemStack(AMItemRegistry.COCKROACH_OOTHECA));
    }

    public EntityCockroachEgg(Level worldIn, double x, double y, double z) {
        super(AMEntityRegistry.COCKROACH_EGG, x, y, z, worldIn, new ItemStack(AMItemRegistry.COCKROACH_OOTHECA));
    }
    // getAddEntityPacket is no longer needed in 1.21
    // public Packet<ClientGamePacketListener> getAddEntityPacket() {
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem().getItem()), this.getX(), this.getY(), this.getZ(), ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }

    }

    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide()) {
            ServerLevel serverLevel = (ServerLevel) this.level();
            this.level().broadcastEntityEvent(this, (byte)3);
            int i = random.nextInt(3);
            for (int j = 0; j < i; ++j) {
                final EntityCockroach croc = AMEntityRegistry.COCKROACH.create(serverLevel, EntitySpawnReason.TRIGGERED);
                croc.setAge(-24000);
                croc.snapTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0F);
                croc.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(this.blockPosition()), EntitySpawnReason.TRIGGERED, (SpawnGroupData)null);
                croc.setHomeTo(this.blockPosition(), 20);
                this.level().addFreshEntity(croc);
            }
            this.level().broadcastEntityEvent(this, (byte)3);
            this.remove(RemovalReason.DISCARDED);
        }

    }

    protected Item getDefaultItem() {
        return AMItemRegistry.COCKROACH_OOTHECA;
    }
}
