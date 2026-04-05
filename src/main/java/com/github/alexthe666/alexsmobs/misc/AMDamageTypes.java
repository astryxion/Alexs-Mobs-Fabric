package com.github.alexthe666.alexsmobs.misc;

import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class AMDamageTypes {

    public static final ResourceKey<DamageType> FARSEER = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("alexsmobs:farseer"));
    public static final ResourceKey<DamageType> FREDDY = ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("alexsmobs:freddy"));

    public static DamageSource causeFarseerDamage(LivingEntity attacker){
        return createDamageSourceWithHolder(attacker.level().registryAccess().registry(Registries.DAMAGE_TYPE).get().getHolderOrThrow(FARSEER), attacker);
    }

    public static DamageSource causeFreddyBearDamage(LivingEntity attacker){
        return createDamageSourceFromSources(attacker.level().damageSources(), FREDDY, attacker);
    }

    /** Fabric 1.20.1: DamageSource constructor and DamageSources.create are private; use reflection for 1:1 behavior. */
    private static DamageSource createDamageSourceFromSources(net.minecraft.world.damagesource.DamageSources sources, ResourceKey<DamageType> key, @Nullable Entity entity) {
        try {
            java.lang.reflect.Method m = sources.getClass().getDeclaredMethod("create", ResourceKey.class, Entity.class);
            m.setAccessible(true);
            return (DamageSource) m.invoke(sources, key, entity);
        } catch (Exception e) {
            try {
                java.lang.reflect.Method reg = sources.getClass().getMethod("registry");
                @SuppressWarnings("unchecked")
                net.minecraft.core.Registry<DamageType> registry = (net.minecraft.core.Registry<DamageType>) reg.invoke(sources);
                return createDamageSourceWithHolder(registry.getHolderOrThrow(key), entity);
            } catch (Exception e2) {
                return sources.generic();
            }
        }
    }

    /** Creates DamageSource via reflection (private constructor); Farseer custom death message is applied via mixin. */
    static DamageSource createDamageSourceWithHolder(Holder<DamageType> holder, @Nullable Entity entity) {
        try {
            java.lang.reflect.Constructor<DamageSource> c = DamageSource.class.getDeclaredConstructor(Holder.class, Entity.class);
            c.setAccessible(true);
            return c.newInstance(holder, entity);
        } catch (Exception e) {
            if (entity instanceof LivingEntity living) return living.damageSources().generic();
            return null;
        }
    }

    /** Used by mixin to build Farseer random death message (1:1 with former DamageSourceRandomMessages). */
    public static Component getFarseerDeathMessage(DamageSource source, LivingEntity attacked) {
        int type = attacked.getRandom().nextInt(3);
        LivingEntity livingentity = attacked.getKillCredit();
        String s = "death.attack." + source.getMsgId() + "_" + type;
        String s1 = s + ".player";
        return livingentity != null ? Component.translatable(s1, attacked.getDisplayName(), livingentity.getDisplayName()) : Component.translatable(s, attacked.getDisplayName());
    }
}
