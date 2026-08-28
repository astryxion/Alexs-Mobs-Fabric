package com.github.alexthe666.alexsmobs.entity;


import net.minecraft.world.entity.MobType;
import com.github.alexthe666.alexsmobs.item.AMItemRegistry;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.Level;

public class EntitySharkToothArrow extends Arrow {

    public EntitySharkToothArrow(EntityType type, Level worldIn) {
        super(type, worldIn);
    }

    public EntitySharkToothArrow(EntityType type, double x, double y, double z, Level worldIn) {
        this(type, worldIn);
        this.setPos(x, y, z);
    }

    public EntitySharkToothArrow(Level worldIn, LivingEntity shooter) {
        this(AMEntityRegistry.SHARK_TOOTH_ARROW, shooter.getX(), shooter.getEyeY() - (double)0.1F, shooter.getZ(), worldIn);
        this.setOwner(shooter);
        if (shooter instanceof Player) {
            this.pickup = AbstractArrow.Pickup.ALLOWED;
        }
    }

    /** Apply potion from tipped-arrow style stack if present. */
    public void initPotionFromItem(ItemStack stack) {
        if (PotionUtils.getPotion(stack) != net.minecraft.world.item.alchemy.Potions.EMPTY || !PotionUtils.getMobEffects(stack).isEmpty()) {
            this.setEffectsFromItem(stack);
        }
    }

    protected void damageShield(Player player, float damage) {
        if (damage >= 3.0F && AMItemRegistry.isShieldBlocking(player.getUseItem())) {
            ItemStack copyBeforeUse = player.getUseItem().copy();
            int i = 1 + Mth.floor(damage);
            player.getUseItem().hurtAndBreak(i, player, (e) -> e.broadcastBreakEvent((player.getUsedItemHand() == net.minecraft.world.InteractionHand.MAIN_HAND ) ? net.minecraft.world.entity.EquipmentSlot.MAINHAND : net.minecraft.world.entity.EquipmentSlot.OFFHAND));

            if (player.getUseItem().isEmpty()) {
                InteractionHand Hand = player.getUsedItemHand();
                if (Hand == net.minecraft.world.InteractionHand.MAIN_HAND) {
                    player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
                } else {
                    player.setItemSlot(EquipmentSlot.OFFHAND, ItemStack.EMPTY);
                }
                player.stopUsingItem();
                this.playSound(SoundEvents.SHIELD_BREAK, 0.8F, 0.8F + this.level().random.nextFloat() * 0.4F);
            }
        }
    }

    protected void doPostHurtEffects(LivingEntity living) {
        if (living instanceof Player) {
            this.damageShield((Player) living, (float) this.getBaseDamage());
        }
        Entity entity1 = this.getOwner();
        if(AMMobTypes.getMobType(living) == MobType.WATER || living instanceof Drowned || AMMobTypes.getMobType(living) != MobType.UNDEAD && living.canBreatheUnderwater()){
            DamageSource damagesource;
            if (entity1 == null) {
                damagesource = damageSources().arrow(this, this);
            } else {
                damagesource = damageSources().arrow(this, entity1);
            }
            living.hurt(damagesource, 7);
        }
    }


    public boolean isInWater() {
        return false;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return new ClientboundAddEntityPacket(this);
    }


    @Override
    protected ItemStack getPickupItem() {
        return new ItemStack(AMItemRegistry.SHARK_TOOTH_ARROW);
    }

}
