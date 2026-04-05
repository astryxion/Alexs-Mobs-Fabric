package com.github.alexthe666.alexsmobs.particle;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class AMParticleRegistry {

    private static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(AlexsMobs.MODID, path);
    }

    /** Fabric 1.20.1: SimpleParticleType(boolean) is protected; use reflection for 1:1 behavior. */
    private static SimpleParticleType register(String name, boolean overrideLimiter) {
        try {
            java.lang.reflect.Constructor<SimpleParticleType> c = SimpleParticleType.class.getDeclaredConstructor(boolean.class);
            c.setAccessible(true);
            return Registry.register(BuiltInRegistries.PARTICLE_TYPE, id(name), c.newInstance(overrideLimiter));
        } catch (Exception e) {
            throw new RuntimeException("Failed to create SimpleParticleType " + name, e);
        }
    }

    public static SimpleParticleType GUSTER_SAND_SPIN;
    public static SimpleParticleType GUSTER_SAND_SHOT;
    public static SimpleParticleType GUSTER_SAND_SPIN_RED;
    public static SimpleParticleType GUSTER_SAND_SHOT_RED;
    public static SimpleParticleType GUSTER_SAND_SPIN_SOUL;
    public static SimpleParticleType GUSTER_SAND_SHOT_SOUL;
    public static SimpleParticleType HEMOLYMPH;
    public static SimpleParticleType PLATYPUS_SENSE;
    public static SimpleParticleType WHALE_SPLASH;
    public static SimpleParticleType DNA;
    public static SimpleParticleType SHOCKED;
    public static SimpleParticleType WORM_PORTAL;
    public static SimpleParticleType INVERT_DIG;
    public static SimpleParticleType TEETH_GLINT;
    public static SimpleParticleType SMELLY;
    public static SimpleParticleType BUNFUNGUS_TRANSFORMATION;
    public static SimpleParticleType FUNGUS_BUBBLE;
    public static SimpleParticleType BEAR_FREDDY;
    public static SimpleParticleType SUNBIRD_FEATHER;
    public static SimpleParticleType STATIC_SPARK;
    public static SimpleParticleType SKULK_BOOM;
    public static SimpleParticleType BIRD_SONG;

    public static void init() {
        GUSTER_SAND_SPIN = register("guster_sand_spin", false);
        GUSTER_SAND_SHOT = register("guster_sand_shot", false);
        GUSTER_SAND_SPIN_RED = register("guster_sand_spin_red", false);
        GUSTER_SAND_SHOT_RED = register("guster_sand_shot_red", false);
        GUSTER_SAND_SPIN_SOUL = register("guster_sand_spin_soul", false);
        GUSTER_SAND_SHOT_SOUL = register("guster_sand_shot_soul", false);
        HEMOLYMPH = register("hemolymph", false);
        PLATYPUS_SENSE = register("platypus_sense", false);
        WHALE_SPLASH = register("whale_splash", false);
        DNA = register("dna", false);
        SHOCKED = register("shocked", false);
        WORM_PORTAL = register("worm_portal", false);
        INVERT_DIG = register("invert_dig", true);
        TEETH_GLINT = register("teeth_glint", false);
        SMELLY = register("smelly", false);
        BUNFUNGUS_TRANSFORMATION = register("bunfungus_transformation", false);
        FUNGUS_BUBBLE = register("fungus_bubble", false);
        BEAR_FREDDY = register("bear_freddy", true);
        SUNBIRD_FEATHER = register("sunbird_feather", false);
        STATIC_SPARK = register("static_spark", false);
        SKULK_BOOM = register("skulk_boom", false);
        BIRD_SONG = register("bird_song", false);
    }
}
