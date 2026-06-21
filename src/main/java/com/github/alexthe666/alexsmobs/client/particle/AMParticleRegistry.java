package com.github.alexthe666.alexsmobs.client.particle;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;

public final class AMParticleRegistry {

    private AMParticleRegistry() {
    }

    private static SimpleParticleType register(String id, boolean overrideLimiter) {
        Identifier rid = Identifier.fromNamespaceAndPath(AlexsMobs.MODID, id);
        SimpleParticleType type = FabricParticleTypes.simple(overrideLimiter);
        return Registry.register(BuiltInRegistries.PARTICLE_TYPE, rid, type);
    }
    /**
     * Call this method during mod initialization to ensure particle types are registered
     * before the registry freezes.
     */
    public static void init() {
        // This method forces class initialization, which registers all particle types
    }
    public static final SimpleParticleType GUSTER_SAND_SPIN = register("guster_sand_spin", false);
    public static final SimpleParticleType GUSTER_SAND_SHOT = register("guster_sand_shot", false);
    public static final SimpleParticleType GUSTER_SAND_SPIN_RED = register("guster_sand_spin_red", false);
    public static final SimpleParticleType GUSTER_SAND_SHOT_RED = register("guster_sand_shot_red", false);
    public static final SimpleParticleType GUSTER_SAND_SPIN_SOUL = register("guster_sand_spin_soul", false);
    public static final SimpleParticleType GUSTER_SAND_SHOT_SOUL = register("guster_sand_shot_soul", false);
    public static final SimpleParticleType HEMOLYMPH = register("hemolymph", false);
    public static final SimpleParticleType PLATYPUS_SENSE = register("platypus_sense", false);
    public static final SimpleParticleType WHALE_SPLASH = register("whale_splash", false);
    public static final SimpleParticleType DNA = register("dna", false);
    public static final SimpleParticleType SHOCKED = register("shocked", false);
    public static final SimpleParticleType WORM_PORTAL = register("worm_portal", false);
    public static final SimpleParticleType INVERT_DIG = register("invert_dig", true);
    public static final SimpleParticleType TEETH_GLINT = register("teeth_glint", false);
    public static final SimpleParticleType SMELLY = register("smelly", false);
    public static final SimpleParticleType BUNFUNGUS_TRANSFORMATION = register("bunfungus_transformation", false);
    public static final SimpleParticleType FUNGUS_BUBBLE = register("fungus_bubble", false);
    public static final SimpleParticleType BEAR_FREDDY = register("bear_freddy", true);
    public static final SimpleParticleType SUNBIRD_FEATHER = register("sunbird_feather", false);
    public static final SimpleParticleType STATIC_SPARK = register("static_spark", false);
    public static final SimpleParticleType SKULK_BOOM = register("skulk_boom", false);
    public static final SimpleParticleType BIRD_SONG = register("bird_song", false);
}
