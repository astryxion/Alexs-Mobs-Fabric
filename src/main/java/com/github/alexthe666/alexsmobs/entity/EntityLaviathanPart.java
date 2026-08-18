package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.misc.AMEntityHooks;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

public class EntityLaviathanPart extends Entity {

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    private final EntityDimensions size;
    @Nullable
    private EntityLaviathan parent;
    public float scale = 1;

    public EntityLaviathanPart(EntityType<? extends EntityLaviathanPart> type, Level level) {
        super(type, level);
        this.parent = null;
        this.size = EntityDimensions.scalable(1F, 1F);
    }

    public EntityLaviathanPart(EntityLaviathan parent, float sizeX, float sizeY) {
        super(AMEntityRegistry.LAVIATHAN_PART, parent.level());
        AMEntityHooks.assignClientPartId(this);
        this.parent = parent;
        this.size = EntityDimensions.scalable(sizeX, sizeY);
        this.refreshDimensions();
    }

    public EntityLaviathanPart(EntityLaviathan parent, float sizeX, float sizeY, EntityDimensions size) {
        super(AMEntityRegistry.LAVIATHAN_PART, parent.level());
        AMEntityHooks.assignClientPartId(this);
        this.parent = parent;
        this.size = size;
        this.refreshDimensions();
    }

    @Nullable
    public EntityLaviathan getParent() {
        return parent;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.size.scale(this.scale);
    }

    public boolean fireImmune() {
        return true;
    }

    public Vec3 getLeashOffset() {
        return new Vec3(0.0D, (double) this.getEyeHeight() * 0.15F, (double) (this.getBbWidth() * 0.1F));
    }

    protected void collideWithNearbyEntities() {
    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        return this.getParent() == null ? InteractionResult.PASS : this.getParent().mobInteract(player, hand);
    }

    public boolean canBeCollidedWith() {
        return false;
    }

    protected void collideWithEntity(Entity entityIn) {
        if (!(entityIn instanceof EntityLaviathan)) {
            entityIn.push(this);
        }
    }

    public boolean isPickable() {
        return true;
    }

    @Nullable
    public ItemStack getPickResult() {
        Entity p = this.getParent();
        return p != null ? p.getPickResult() : ItemStack.EMPTY;
    }

    @Override
    public boolean hurtServer(ServerLevel level, DamageSource source, float amount) {
        EntityLaviathan p = this.getParent();
        if (p != null && !p.isInvulnerableTo(level, source)) {
            return !this.isInvulnerableToBase(source) && p.attackEntityPartFrom(this, source, amount);
        }
        return false;
    }

    @Override
    public boolean is(Entity entityIn) {
        return this == entityIn || this.getParent() == entityIn;
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void readAdditionalSaveData(ValueInput compound) {
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput compound) {
    }
}
