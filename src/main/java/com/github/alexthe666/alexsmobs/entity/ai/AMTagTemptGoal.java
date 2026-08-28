package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.TemptGoal;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * Tempt goal that matches item tags at test time via {@link ItemStack#is(TagKey)}.
 * {@link Ingredient} tag values can resolve empty in 1.21.1, which makes vanilla
 * TemptGoal follow players holding nothing.
 */
public class AMTagTemptGoal extends TemptGoal {

    private static final TargetingConditions TEMPT_TARGETING = TargetingConditions.forNonCombat().range(10.0D).ignoreLineOfSight();
    private final TagKey<Item>[] tags;
    private final TargetingConditions targeting;
    private final boolean canScare;
    private final double followSpeed;
    private Player nearbyPlayer;
    private int calmDown;
    private boolean isRunning;
    private double px;
    private double py;
    private double pz;
    private double pRotX;
    private double pRotY;

    @SafeVarargs
    public AMTagTemptGoal(PathfinderMob mob, double speed, boolean canScare, TagKey<Item>... tags) {
        super(mob, speed, Ingredient.of(Items.BARRIER), canScare);
        this.tags = tags;
        this.canScare = canScare;
        this.followSpeed = speed;
        this.targeting = TEMPT_TARGETING.copy().selector(this::shouldFollowTags);
    }

    private boolean shouldFollowTags(LivingEntity entity) {
        return matches(entity.getMainHandItem()) || matches(entity.getOffhandItem());
    }

    private boolean matches(ItemStack stack) {
        if (stack.isEmpty()) {
            return false;
        }
        for (TagKey<Item> tag : tags) {
            if (stack.is(tag)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean canUse() {
        if (this.calmDown > 0) {
            --this.calmDown;
            return false;
        }
        this.nearbyPlayer = this.mob.level().getNearestPlayer(this.targeting, this.mob);
        return this.nearbyPlayer != null;
    }

    @Override
    public boolean canContinueToUse() {
        if (this.nearbyPlayer == null || !this.nearbyPlayer.isAlive()) {
            return false;
        }
        if (this.canScare) {
            if (this.mob.distanceToSqr(this.nearbyPlayer) < 36.0D) {
                if (this.nearbyPlayer.distanceToSqr(this.px, this.py, this.pz) > 0.010000000000000002D) {
                    return false;
                }
                if (Math.abs((double) this.nearbyPlayer.getXRot() - this.pRotX) > 5.0D || Math.abs((double) this.nearbyPlayer.getYRot() - this.pRotY) > 5.0D) {
                    return false;
                }
            } else {
                this.px = this.nearbyPlayer.getX();
                this.py = this.nearbyPlayer.getY();
                this.pz = this.nearbyPlayer.getZ();
            }
            this.pRotX = this.nearbyPlayer.getXRot();
            this.pRotY = this.nearbyPlayer.getYRot();
        }
        return this.canUse();
    }

    @Override
    public void start() {
        this.px = this.nearbyPlayer.getX();
        this.py = this.nearbyPlayer.getY();
        this.pz = this.nearbyPlayer.getZ();
        this.isRunning = true;
    }

    @Override
    public void stop() {
        this.nearbyPlayer = null;
        this.mob.getNavigation().stop();
        this.calmDown = reducedTickDelay(100);
        this.isRunning = false;
    }

    @Override
    public void tick() {
        this.mob.getLookControl().setLookAt(this.nearbyPlayer, (float) (this.mob.getMaxHeadYRot() + 20), (float) this.mob.getMaxHeadXRot());
        if (this.mob.distanceToSqr(this.nearbyPlayer) < 6.25D) {
            this.mob.getNavigation().stop();
        } else {
            this.mob.getNavigation().moveTo(this.nearbyPlayer, this.followSpeed);
        }
    }

    public boolean isRunning() {
        return this.isRunning;
    }

    public Player getTemptingPlayer() {
        return this.nearbyPlayer;
    }
}
