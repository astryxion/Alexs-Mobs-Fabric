package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.throwableitemprojectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
// NetworkHooks removed in NeoForge 1.21

public class EntityTossedItem extends ThrowableItemProjectile {

    protected static final EntityDataAccessor<Boolean> DART = SynchedEntityData.defineId(EntityTossedItem.class, EntityDataSerializers.BOOLEAN);

    public EntityTossedItem(EntityType<? extends EntityTossedItem> type, Level level) {
        super(type, level);
    }

    public EntityTossedItem(Level worldIn, LivingEntity throwerIn) {
        super(AMEntityRegistry.TOSSED_ITEM, throwerIn, worldIn, new ItemStack(Items.COBBLESTONE));
        this.setOwner(throwerIn);
        this.setPos(throwerIn.getX(), throwerIn.getEyeY() - 0.1D, throwerIn.getZ());
    }

    public EntityTossedItem(Level worldIn, double x, double y, double z) {
        super(AMEntityRegistry.TOSSED_ITEM, x, y, z, worldIn, new ItemStack(Items.COBBLESTONE));
        this.setPos(x, y, z);
    }
    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DART, false);
    }

    public boolean isDart() {
        return this.entityData.get(DART);
    }

    public void setDart(boolean dart) {
        this.entityData.set(DART, dart);
    }

    // getAddEntityPacket is no longer needed in 1.21
    // public Packet<ClientGamePacketListener> getAddEntityPacket() {
    public void handleEntityEvent(byte id) {
        if (id == 3) {
            double d0 = 0.08D;

            for(int i = 0; i < 8; ++i) {
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getItem().getItem()), this.getX(), this.getY(), this.getZ(), ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D, ((double)this.random.nextFloat() - 0.5D) * 0.08D);
            }
        }

    }
    public void lerpMotion(double x, double y, double z) {
        this.setDeltaMovement(x, y, z);
        if (this.xRotO == 0.0F && this.yRotO == 0.0F) {
            final float f = Mth.sqrt((float) (x * x + z * z));
            this.setXRot((float)(Mth.atan2(y, (double)f) * (double)Mth.RAD_TO_DEG));
            this.setYRot( (float)(Mth.atan2(x, z) * (double)Mth.RAD_TO_DEG));
            this.xRotO = this.getXRot();
            this.yRotO = this.getYRot();
            this.snapTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), this.getXRot());
        }

    }

    public void tick() {
        super.tick();
        Vec3 vector3d = this.getDeltaMovement();
        float f = Mth.sqrt((float) vector3d.horizontalDistanceSqr());
        this.setXRot(lerpRotation(this.xRotO, (float)(Mth.atan2(vector3d.y, (double)f) * (double)Mth.RAD_TO_DEG)));
        this.setYRot( lerpRotation(this.yRotO, (float)(Mth.atan2(vector3d.x, vector3d.z) * (double)Mth.RAD_TO_DEG)));
    }

    protected static float lerpRotation(float p_234614_0_, float p_234614_1_) {
        while(p_234614_1_ - p_234614_0_ < -180.0F) {
            p_234614_0_ -= 360.0F;
        }

        while(p_234614_1_ - p_234614_0_ >= 180.0F) {
            p_234614_0_ += 360.0F;
        }

        return Mth.lerp(0.2F, p_234614_0_, p_234614_1_);
    }


    protected void onHitEntity(EntityHitResult p_213868_1_) {
        super.onHitEntity(p_213868_1_);
        if(this.getOwner() instanceof EntityCapuchinMonkey){
            EntityCapuchinMonkey boss = (EntityCapuchinMonkey) this.getOwner();
            if(!boss.isAlliedTo(p_213868_1_.getEntity()) || !boss.isTame() && !(p_213868_1_.getEntity() instanceof EntityCapuchinMonkey)){
                p_213868_1_.getEntity().hurt(damageSources().thrown(this, boss), isDart() ? 8.0F : 4.0F);
            }
        }
    }

    public void addAdditionalSaveData(ValueOutput compound) {
        super.addAdditionalSaveData(compound);
        compound.putBoolean("Dart", this.isDart());
    }

    public void readAdditionalSaveData(ValueInput compound) {
        super.readAdditionalSaveData(compound);
        this.setDart(compound.getBooleanOr("Dart", false));
    }

    protected void onHit(HitResult result) {
        super.onHit(result);
        if (!this.level().isClientSide() && (!this.isDart() || result.getType() == HitResult.Type.BLOCK)) {
            this.level().broadcastEntityEvent(this, (byte)3);
            this.remove(RemovalReason.DISCARDED);
        }
    }

    protected Item getDefaultItem() {
        // entityData may be null during initialization when parent class calls this
        if (this.entityData != null && isDart()) {
            return AMItemRegistry.ANCIENT_DART;
        }
        return Items.COBBLESTONE;
    }
}
