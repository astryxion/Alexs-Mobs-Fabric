package com.github.alexthe666.alexsmobs.entity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.network.MessageInteractMultipart;
import com.github.alexthe666.alexsmobs.misc.AMEntityHooks;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class EntityCachalotPart extends Entity {

    private final EntityDimensions size;
    @Nullable
    private EntityCachalotWhale parent;
    public float scale = 1;

    public EntityCachalotPart(EntityType<? extends EntityCachalotPart> type, Level level) {
        super(type, level);
        this.parent = null;
        this.size = EntityDimensions.scalable(1F, 1F);
    }

    public EntityCachalotPart(EntityCachalotWhale parent, float sizeX, float sizeY) {
        super(AMEntityRegistry.CACHALOT_PART, parent.level());
        AMEntityHooks.assignClientPartId(this);
        this.parent = parent;
        this.size = EntityDimensions.scalable(sizeX, sizeY);
        this.refreshDimensions();
    }

    public EntityCachalotPart(EntityCachalotWhale entityCachalotWhale, float sizeX, float sizeY, EntityDimensions size) {
        super(AMEntityRegistry.CACHALOT_PART, entityCachalotWhale.level());
        AMEntityHooks.assignClientPartId(this);
        this.parent = entityCachalotWhale;
        this.size = size;
        this.refreshDimensions();
    }

    @Nullable
    public EntityCachalotWhale getParent() {
        return parent;
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return this.size.scale(this.scale);
    }

    protected void collideWithNearbyEntities() {
        final List<Entity> entities = this.level().getEntities(this, this.getBoundingBox().expandTowards(0.2D, 0.0D, 0.2D));
        Entity p = this.getParent();
        if (p != null) {
            entities.stream().filter(entity -> entity != p && !(entity instanceof EntityCachalotPart other && other.getParent() == p) && entity.isPushable()).forEach(entity -> entity.push(p));
        }
    }

    public InteractionResult interact(Player player, InteractionHand hand) {
        if (this.level().isClientSide() && this.getParent() != null) {
            AlexsMobs.sendMSGToServer(new MessageInteractMultipart(this.getParent().getId(), hand == InteractionHand.OFF_HAND));
        }
        return this.getParent() == null ? InteractionResult.PASS : this.getParent().mobInteract(player, hand);
    }

    protected void collideWithEntity(Entity entityIn) {
        entityIn.push(this);
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
        if (this.getParent() != null && !this.getParent().isInvulnerableTo(level, source)) {
            return !this.isInvulnerableToBase(source) && this.getParent().attackEntityPartFrom(this, source, amount);
        }
        return false;
    }

    @Override
    public boolean is(Entity entityIn) {
        return this == entityIn || this.getParent() == entityIn;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
    }

    @Override
    public void tick() {
        super.tick();
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
    }
}
