package com.github.alexthe666.citadel.server.generation;

import com.github.alexthe666.citadel.Citadel;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class VillageHouseManager {
    public static final List<ResourceLocation> VILLAGE_REPLACEMENT_POOLS = List.of(
            new ResourceLocation("minecraft:village/plains/houses"),
            new ResourceLocation("minecraft:village/desert/houses"),
            new ResourceLocation("minecraft:village/savanna/houses"),
            new ResourceLocation("minecraft:village/snowy/houses"),
            new ResourceLocation("minecraft:village/taiga/houses"));
    private static final List<Pair<ResourceLocation, Consumer<StructureTemplatePool>>> REGISTRY = new ArrayList<>();

    public static void register(ResourceLocation pool, Consumer<StructureTemplatePool> addToPool) {
        REGISTRY.add(new Pair<>(pool, addToPool));
        Citadel.LOGGER.debug("registered addition to pool: " + pool);
    }

    @SuppressWarnings("unchecked")
    public static StructureTemplatePool addToPool(StructureTemplatePool pool, StructurePoolElement element, int weight) {
        if (weight > 0 && pool != null) {
            try {
                java.lang.reflect.Field templatesField = StructureTemplatePool.class.getDeclaredField("templates");
                templatesField.setAccessible(true);
                java.lang.reflect.Field rawTemplatesField = StructureTemplatePool.class.getDeclaredField("rawTemplates");
                rawTemplatesField.setAccessible(true);
                ObjectArrayList<StructurePoolElement> templates = new ObjectArrayList<>((List<StructurePoolElement>) templatesField.get(pool));
                if (!templates.contains(element)) {
                    for (int i = 0; i < weight; i++) {
                        templates.add(element);
                    }
                    List<Pair<StructurePoolElement, Integer>> rawTemplates = new ArrayList<>((List<Pair<StructurePoolElement, Integer>>) rawTemplatesField.get(pool));
                    rawTemplates.add(new Pair<>(element, weight));
                    templatesField.set(pool, templates);
                    rawTemplatesField.set(pool, rawTemplates);
                    Citadel.LOGGER.info("Added to village structure pool");
                }
            } catch (Exception e) {
                Citadel.LOGGER.warn("Could not add to structure pool via reflection", e);
            }
        }
        return pool;
    }

    public static void addAllHouses(RegistryAccess registryAccess) {
        try {
            for (ResourceLocation villagePool : VILLAGE_REPLACEMENT_POOLS) {
                StructureTemplatePool pool = registryAccess.registryOrThrow(Registries.TEMPLATE_POOL).getOptional(villagePool).orElse(null);
                if (pool != null) {
                    for (Pair<ResourceLocation, Consumer<StructureTemplatePool>> pair : REGISTRY) {
                        if (villagePool.equals(pair.getFirst())) {
                            pair.getSecond().accept(pool);
                        }
                    }
                }
            }
        } catch (Exception e) {
            Citadel.LOGGER.error("Could not add village houses!");
            e.printStackTrace();
        }
    }
}
