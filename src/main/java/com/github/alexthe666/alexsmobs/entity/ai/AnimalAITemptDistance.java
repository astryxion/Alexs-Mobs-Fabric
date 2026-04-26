package com.github.alexthe666.alexsmobs.entity.ai;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.EnumSet;

public class AnimalAITemptDistance extends Goal {
    private final double maxDistance;
    protected final PathfinderMob mob;
    private final double speedModifier;
    private double px;
    private double py;
    private double pz;
    private double pRotX;
    private double pRotY;
    protected Player player;
    private int calmDown;
    private boolean isRunning;
    private final Ingredient items;
    private final boolean canScare;

    public AnimalAITemptDistance(PathfinderMob p_25939_, double p_25940_, Ingredient p_25941_, boolean p_25942_, double distance) {
        this.mob = p_25939_;
        this.speedModifier = p_25940_;
        this.items = p_25941_;
        this.canScare = p_25942_;
        this.maxDistance = distance;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    public boolean canUse() {
        if (this.calmDown > 0) {
            --this.calmDown;
            return false;
        } else {
            if (!(this.mob.level() instanceof ServerLevel serverLevel)) {
                return false;
            }
            this.player = serverLevel.getNearestPlayer(
                    this.mob.getX(),
                    this.mob.getY(),
                    this.mob.getZ(),
                    this.maxDistance,
                    this::isTemptingPlayer);
            return this.player != null;
        }
    }

    private boolean isTemptingPlayer(Entity entity) {
        if (!(entity instanceof Player player)) {
            return false;
        }
        return this.items.test(player.getMainHandItem()) || this.items.test(player.getOffhandItem());
    }

    public boolean canContinueToUse() {
        if (this.canScare()) {
            if (this.mob.distanceToSqr(this.player) < 36.0D) {
                if (this.player.distanceToSqr(this.px, this.py, this.pz) > 0.010000000000000002D) {
                    return false;
                }

                if (Math.abs((double)this.player.getXRot() - this.pRotX) > 5.0D || Math.abs((double)this.player.getYRot() - this.pRotY) > 5.0D) {
                    return false;
                }
            } else {
                this.px = this.player.getX();
                this.py = this.player.getY();
                this.pz = this.player.getZ();
            }

            this.pRotX = (double)this.player.getXRot();
            this.pRotY = (double)this.player.getYRot();
        }

        return this.canUse();
    }

    protected boolean canScare() {
        return this.canScare;
    }

    public void start() {
        this.px = this.player.getX();
        this.py = this.player.getY();
        this.pz = this.player.getZ();
        this.isRunning = true;
    }

    public void stop() {
        this.player = null;
        this.mob.getNavigation().stop();
        this.calmDown = 100;
        this.isRunning = false;
    }

    public void tick() {
        this.mob.getLookControl().setLookAt(this.player, (float)(this.mob.getMaxHeadYRot() + 20), (float)this.mob.getMaxHeadXRot());
        if (this.mob.distanceToSqr(this.player) < 6.25D) {
            this.mob.getNavigation().stop();
        } else {
            this.mob.getNavigation().moveTo(this.player, this.speedModifier);
        }

    }

    public boolean isRunning() {
        return this.isRunning;
    }
}