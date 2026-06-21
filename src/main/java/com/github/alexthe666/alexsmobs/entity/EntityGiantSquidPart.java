package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.network.MessageHurtMultipart;
import com.github.alexthe666.alexsmobs.network.MessageInteractMultipart;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;

public class EntityGiantSquidPart extends Entity implements IHurtableMultipart {

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    private final EntityDimensions size;
    @Nullable
    private EntityGiantSquid parent;
    public float scale = 1;
    private boolean collisionOnly = false;

    public EntityGiantSquidPart(EntityType<? extends EntityGiantSquidPart> type, Level level) {
        super(type, level);
        this.parent = null;
        this.size = EntityDimensions.scalable(1F, 1F);
    }

    public EntityGiantSquidPart(EntityGiantSquid parent, float sizeX, float sizeY) {
        super(AMEntityRegistry.GIANT_SQUID_PART, parent.level());
        this.parent = parent;
        this.size = EntityDimensions.scalable(sizeX, sizeY);
        this.refreshDimensions();
    }

    public EntityGiantSquidPart(EntityGiantSquid parent, float sizeX, float sizeY, boolean collisionOnly) {
        this(parent, sizeX, sizeY);
        this.collisionOnly = collisionOnly;
    }

    @Nullable
    public EntityGiantSquid getParent() {
        return parent;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.size.scale(this.scale);
    }

    public boolean fireImmune() {
        return true;
    }

    protected void collideWithNearbyEntities() {
    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        if (this.level().isClientSide() && this.getParent() != null) {
            AlexsMobs.sendMSGToServer(new MessageInteractMultipart(this.getParent().getId(), hand == InteractionHand.OFF_HAND));
        }
        return this.getParent() == null ? InteractionResult.PASS : this.getParent().mobInteract(player, hand);
    }

    public boolean canBeCollidedWith() {
        return !collisionOnly;
    }

    protected void collideWithEntity(Entity entityIn) {
        if (!collisionOnly) {
            entityIn.push(this);
        }
    }

    public boolean isPickable() {
        return !collisionOnly;
    }

    @Nullable
    public ItemStack getPickResult() {
        Entity p = this.getParent();
        return p != null ? p.getPickResult() : ItemStack.EMPTY;
    }

    @Override
    public boolean hurtServer(ServerLevel serverLevel, DamageSource source, float amount) {
        EntityGiantSquid p = this.getParent();
        if (this.level().isClientSide() && p != null && !p.isInvulnerableTo(serverLevel, source) && !collisionOnly) {
            AlexsMobs.sendMSGToServer(new MessageHurtMultipart(this.getId(), p.getId(), amount, source.getMsgId()));
        }
        return !collisionOnly && p != null && !p.isInvulnerableTo(serverLevel, source) && p.attackEntityPartFrom(this, source, amount);
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

    @Override
    public void onAttackedFromServer(LivingEntity parent, float damage, DamageSource damageSource) {
        if (damageSource != null && parent.level() instanceof ServerLevel sl) {
            parent.hurtServer(sl, damageSource, damage);
        }
    }
}
