# Alex's Mobs (Fabric 1.20.1) — Full Spawning Breakdown

This document describes **every** spawning path: registration flow, rates, biomes per mob, structure spawns, special spawners, and placement rules.

---

## 1. Registration flow (how spawns get into the game)

### 1.1 Startup order (`AlexsMobs.java`)

1. **`ConfigHolder.loadConfig()`** — Loads common config (spawn weights, etc.).
2. **`BiomeConfig.init()`** — Builds the biome-matching cache: for each `Pair<String, SpawnBiomeData>` in `BiomeConfig`, creates a `SpawnBiomeConfig` and stores it in `biomeConfigValues` keyed by the config ID (e.g. `alexsmobs:grizzly_bear_spawns`). **Must run before any spawn modifier.**
3. **`AMMobSpawnBiomeModifier.register()`** — Registers the **global biome modifier** with Fabric's `BiomeModifications`.
4. **`AMLeafcutterAntBiomeModifier.register()`** — Registers the modifier that adds the **leafcutter anthill feature** to biomes.
5. **`AMMobSpawnStructureModifier.register()`** — Placeholder; structure spawns are intended to be applied via datapack or mixin (see §4).

### 1.2 Biome modifier (natural mob spawns)

- **Class:** `AMMobSpawnBiomeModifier`
- **ID:** `alexsmobs:am_mob_spawns`
- **Phase:** `ModificationPhase.ADDITIONS`
- **Predicate:** `ctx -> true` (applies to every biome).
- **Action:** For each biome, calls `AMWorldRegistry.addBiomeSpawns(biome, spawnContext)`.

### 1.3 What `addBiomeSpawns` does

For **each** mob type that has a `BiomeConfig` entry and a spawn weight &gt; 0:

1. **`AMWorldRegistry.testBiome(BiomeConfig.<mob>, biome)`** is called.
2. **`BiomeConfig.test(entry, biome, biomeName)`** looks up `biomeConfigValues.get(entry.getKey())` (the `SpawnBiomeConfig` built from `DefaultBiomes`) and calls **`SpawnBiomeConfig.matches(biome, name)`** (Citadel API). That uses the `SpawnBiomeData` entries (tags + registry names, with include/exclude and groups) to decide if the biome matches.
3. If the result is **true**, `spawnContext.addSpawn(category, new SpawnerData(entityType, weight, minCount, maxCount))` is called.

So: **biome list per mob** = whatever the corresponding `DefaultBiomes.*` constant describes (vanilla tags + registry names + mod biomes like Terralith). **Rates** = `AMConfig.<mob>SpawnWeight` and the **min/max group size** hardcoded in `AMWorldRegistry.addBiomeSpawns`.

---

## 2. Spawn rates and group sizes (by mob)

All weights and min/max are from **`AMConfig`** (defaults) and **`AMWorldRegistry.addBiomeSpawns`**.  
**MobCategory** is CREATURE, MONSTER, AMBIENT, WATER_CREATURE, or WATER_AMBIENT.

| Mob | Category | Default weight | Min | Max | Config / notes |
|-----|----------|----------------|-----|-----|----------------|
| Grizzly Bear | CREATURE | 8 | 2 | 3 | |
| Roadrunner | CREATURE | 9 | 2 | 2 | |
| Bone Serpent | MONSTER | 8 | 1 | 1 | |
| Gazelle | CREATURE | 40 | 7 | 7 | |
| Crocodile | CREATURE | 20 | 1 | 2 | |
| Fly | AMBIENT | 3 | 2 | 3 | |
| Hummingbird | CREATURE | 19 | 7 | 7 | |
| Orca | WATER_CREATURE | 2 | 3 | 4 | |
| Sunbird | CREATURE | 5 | 1 | 1 | |
| Gorilla | CREATURE | 25 | 7 | 7 | |
| Crimson Mosquito | MONSTER | 15 | 4 | 4 | |
| Rattlesnake | CREATURE | 12 | 1 | 2 | |
| Endergrade | CREATURE | 10 | 2 | 6 | |
| Hammerhead Shark | WATER_CREATURE | 8 | 2 | 3 | |
| Lobster | WATER_AMBIENT | 7 | 3 | 5 | |
| Komodo Dragon | CREATURE | 16 | 1 | 2 | |
| Capuchin Monkey | CREATURE | 28 | 9 | 16 | |
| Cave Centipede | MONSTER | 8 | 1 | 1 | |
| Warped Toad | CREATURE | 30 | 5 | 5 | |
| Moose | CREATURE | 9 | 3 | 4 | |
| Mimicube | MONSTER | 40 | 1 | 3 | Only if **not** `mimicubeSpawnInEndCity` (else structure-only) |
| Raccoon | CREATURE | 10 | 2 | 4 | |
| Blobfish | WATER_AMBIENT | 30 | 2 | 2 | `blobfishSpawnHeight` = 25 (deep) |
| Seal | CREATURE | 20 | 3 | 8 | |
| Cockroach | AMBIENT | 4 | 5 | 5 | |
| Shoebill | CREATURE | 10 | 1 | 2 | |
| Elephant | CREATURE | 30 | 3 | 5 | |
| Soul Vulture | MONSTER | 30 | 2 | 3 | Only if **not** `soulVultureSpawnOnFossil` (else structure-only) |
| Snow Leopard | CREATURE | 18 | 1 | 2 | |
| Spectre | CREATURE | 10 | 1 | 2 | |
| Crow | CREATURE | 10 | 3 | 5 | |
| Alligator Snapping Turtle | CREATURE | 20 | 1 | 2 | |
| Mungus | CREATURE | 4 | 3 | 5 | |
| Mantis Shrimp | WATER_CREATURE | 15 | 1 | 4 | |
| Guster | MONSTER | 35 | 1 | 2 | Can be limited to weather: `limitGusterSpawnsToWeather` |
| Warped Mosco | MONSTER | 1 | 1 | 1 | Uses EMPTY biomes (no natural spawn unless datapack adds) |
| Straddler | MONSTER | 70 | 1 | 3 | |
| Stradpole | WATER_CREATURE | 10 | 1 | 1 | |
| Emu | CREATURE | 20 | 2 | 5 | |
| Platypus | CREATURE | 20 | 1 | 2 | |
| Dropbear | MONSTER | 19 | 1 | 1 | |
| Tasmanian Devil | CREATURE | 10 | 1 | 2 | |
| Kangaroo | CREATURE | 25 | 3 | 5 | |
| Cachalot Whale | WATER_CREATURE | 2 | 1 | 2 | |
| Enderiophage | CREATURE | 4 | 2 | 2 | |
| Bald Eagle | CREATURE | 15 | 2 | 4 | |
| Tiger | CREATURE | 30 | 1 | 3 | |
| Tarantula Hawk | CREATURE | 6 | 1 | 1 | |
| Void Worm | MONSTER | 0 | 1 | 1 | Default weight 0 (summon-only unless config changed) |
| Frilled Shark | WATER_CREATURE | 11 | 1 | 1 | |
| Mimic Octopus | WATER_CREATURE | 9 | 1 | 2 | |
| Seagull | CREATURE | 21 | 3 | 6 | |
| Froststalker | CREATURE | 20 | 5 | 7 | |
| Tusklin | CREATURE | 18 | 3 | 5 | |
| Laviathan | CREATURE | 15 | 1 | 1 | |
| Cosmaw | CREATURE | 9 | 1 | 2 | |
| Toucan | CREATURE | 23 | 5 | 5 | |
| Maned Wolf | CREATURE | 8 | 1 | 1 | |
| Anaconda | CREATURE | 12 | 1 | 1 | |
| Anteater | CREATURE | 7 | 1 | 3 | |
| Rocky Roller | MONSTER | 60 | 1 | 1 | |
| Flutter | AMBIENT | 13 | 2 | 4 | |
| Gelada Monkey | CREATURE | 5 | 9 | 16 | `geladaMonkeySpawnHeight` = 100 |
| Jerboa | AMBIENT | 12 | 1 | 3 | |
| Terrapin | WATER_AMBIENT | 4 | 1 | 2 | |
| Comb Jelly | WATER_AMBIENT | 5 | 2 | 3 | |
| Cosmic Cod | AMBIENT | 5 | 9 | 13 | |
| Bunfungus | CREATURE | 3 | 1 | 1 | |
| Bison | CREATURE | 9 | 6 | 10 | |
| Giant Squid | WATER_CREATURE | 3 | 1 | 2 | |
| Devil's Hole Pupfish | WATER_AMBIENT | 23 | 5 | 12 | **Restricted:** only in one “pupfish chunk” per world if `restrictPupfishSpawns` |
| Catfish | WATER_AMBIENT | 4 | 1 | 3 | |
| Flying Fish | WATER_AMBIENT | 8 | 3 | 6 | |
| Skelewag | MONSTER | 15 | 2 | 3 | Only if **not** `restrictSkelewagSpawns` (else structure-only) |
| Rain Frog | AMBIENT | 10 | 1 | 3 | |
| Potoo | CREATURE | 15 | 1 | 1 | |
| Mudskipper | CREATURE | 28 | 2 | 4 | |
| Rhinoceros | CREATURE | 24 | 3 | 5 | |
| Sugar Glider | CREATURE | 15 | 2 | 4 | |
| Farseer | MONSTER | 30 | 1 | 1 | **Restricted:** only near world border if `restrictFarseerSpawns` (distance &lt; `farseerBorderSpawnDistance`) |
| Skreecher | MONSTER | 10 | 1 | 1 | Biomes: exclude mushroom; require tag `alexsmobs:skreechers_can_spawn_wardens` (e.g. deep_dark) |
| Underminer | AMBIENT | 50 | 1 | 1 | Only if **not** `restrictUnderminerSpawns` (else structure-only) |
| Murmur | MONSTER | 5 | 1 | 1 | `murmurSpawnHeight` = -30 |
| Skunk | CREATURE | 7 | 1 | 2 | |
| Banana Slug | CREATURE | 14 | 2 | 3 | |
| Blue Jay | CREATURE | 16 | 2 | 4 | |
| Caiman | CREATURE | 29 | 2 | 4 | |
| Triops | WATER_AMBIENT | 8 | 2 | 6 | |

---

## 3. Biomes per mob (DefaultBiomes → BiomeConfig mapping)

Each row: **BiomeConfig name** → **DefaultBiomes constant** → short **biome description**.  
Full tag/registry details are in `DefaultBiomes.java`.

| Config key | DefaultBiomes | Biomes (summary) |
|------------|---------------|-------------------|
| grizzly_bear_spawns | ALL_FOREST | Overworld + forest (not sparse jungle); or overworld + taiga; plus Terralith/BOP/cherry grove etc. |
| roadrunner_spawns | ROADRUNNER | Badlands, savanna, beach; Terralith desert/mesa variants |
| bone_serpent_spawns | ALL_NETHER_MONSTER | Nether, exclude mushroom_fields |
| gazelle_spawns | GAZELLE | Savanna + Terralith arid/savanna variants |
| crocodile_spawns | CROCODILE | Swamp (allows_surface_slime_spawns), mangrove_swamp, river (excl. frozen_river), tropic beach, orchid_swamp, warm_river |
| fly_spawns | FLY | All overworld |
| hummingbird_spawns | HUMMINGBIRD | Flower forest, sunflower_plains, jungle, meadow, Terralith blooming/jungle/sakura etc., cherry_grove |
| orca_spawns | ORCA | Ocean + cold overworld (snowy_plains, ice_spikes, frozen_river, snowy_taiga, snowy_beach, frozen_peaks, snowy_slopes) |
| sunbird_spawns | SUNBIRD | Overworld + mountain; plus snowy_slopes, frozen_peaks, jagged_peaks, Terralith mountains, skylands |
| gorilla_spawns | GORILLA | Jungle (excl. bamboo_jungle), Terralith jungle variants, skylands_summer |
| crimson_mosquito_spawns | CRIMSON_MOSQUITO | Crimson forest, BYG/BOP/Incendium crimson variants (excl. mushroom_fields) |
| rattlesnake_spawns | RATTLESNAKE | Same as ROADRUNNER (badlands, savanna, beach, Terralith desert/mesa) |
| endergrade_spawns | ENDERGRADE | End (excl. the_end center) |
| hammerhead_shark_spawns | HAMMERHEAD | Ocean + savanna (warm ocean) |
| lobster_spawns | LOBSTER | Beach, gravel_beach, stony_shore |
| komodo_dragon_spawns | KOMODO_DRAGON | Jungle (with extra logic), sandstone_valley, red_oasis, skylands_summer, BOP tropics |
| capuchin_monkey_spawns | CAPUCHIN_MONKEY | Jungle (excl. bamboo_jungle), mangrove_swamp, Terralith jungle, skylands_summer |
| cave_centipede_spawns | CAVES_MONSTER | Overworld, excl. ocean/mushroom/deep_dark; Terralith caves |
| warped_toad_spawns | WARPED_TOAD | Warped forest + BYG/BOP/Incendium warped variants |
| moose_spawns | MOOSE | Overworld + (snowy biomes OR badlands); overworld + taiga + snowy; BOP/Terralith snowy/cold |
| mimicube_spawns | MIMICUBE | End (excl. the_end center), excl. mushroom_fields |
| raccoon_spawns | RACCOON | Overworld + forest (excl. savanna) or overworld + plains/sunflower_plains or overworld + taiga; Terralith/BOP/cherry |
| blobfish_spawns | DEEP_SEA | Deep ocean only |
| seal_spawns | SEAL | Beach (group 0); overworld + ocean + cold (snowy/frozen list) (group 1); gravel_beach, dune_beach, stony_shore |
| cockroach_spawns | COCKROACH | Overworld, excl. ocean, mushroom_fields, deep_dark; Terralith caves |
| shoebill_spawns | SHOEBILL | Swamp (allows_surface_slime_spawns), excl. mangrove_swamp; orchid_swamp, red_oasis |
| elephant_spawns | ELEPHANT | Savanna + Terralith savanna variants |
| soul_vulture_spawns | SOUL_VULTURE | Soul sand valley + BYG/Incendium variants (excl. mushroom_fields) |
| snow_leopard_spawns | SNOW_LEOPARD | Overworld + snowy (7 registry names) + badlands; overworld + taiga + snowy; BOP/Terralith snowy |
| spectre_spawns | SPECTRE | End (excl. the_end) |
| crow_spawns | CROW | Overworld + (forest or jungle); excl. mushroom; many Terralith/BOP/cherry |
| alligator_snapping_turtle_spawns | ALLIGATOR_SNAPPING_TURTLE | Swamp (allows_surface_slime_spawns), mangrove_swamp, river; BOP/Terralith swamp variants |
| mungus_spawns | MUNGUS | Overworld + mushroom_fields (registry) + mushroom_fields (rare); mirage_isles |
| mantis_shrimp_spawns | MANTIS_SHRIMP | Ocean + savanna; mangrove_swamp |
| guster_spawns | GUSTER | Savanna, badlands, beach (excl. mushroom); Terralith desert/cave |
| warped_mosco_spawns | EMPTY | No biomes (no natural spawn) |
| straddler_spawns | STRADDLER | Basalt deltas, BOP/Incendium nether (excl. mushroom) |
| stradpole_spawns | STRADDLER | Same as straddler |
| emu_spawns | SAVANNA_AND_MESA | Overworld + (badlands or savanna); Terralith arid/savanna/mesa, BOP lush_desert |
| platypus_spawns | ICE_FREE_RIVER | Overworld + river, excl. cold (7 snowy/frozen registry names); tundra_bog, warm_river |
| dropbear_spawns | DROPBEAR | Nether wastes, BOP crystalline chasm (excl. mushroom) |
| tasmanian_devil_spawns | TASMANIAN_DEVIL | Overworld + forest (excl. savanna, cold), excl. sparse_jungle; Terralith/BOP/cherry |
| kangaroo_spawns | SAVANNA_AND_MESA | Same as emu |
| cachalot_whale_spawns | CACHALOT_WHALE | Overworld + ocean + cold (7 snowy/frozen); lukewarm/deep ocean, alexscaves |
| cachalot_whale_beached_spawns | BEACHED_CACHALOT_WHALE | Beach, gravel_beach, dune_beach, stony_shore (used by BeachedCachalotWhaleSpawner only) |
| leafcutter_anthill_spawns | LEAFCUTTER_ANTHILL | Jungle (excl. bamboo_jungle); Terralith jungle; underground_jungle cave |
| enderiophage_spawns | ENDERIOPHAGE | End (excl. the_end) |
| bald_eagle_spawns | BALD_EAGLE | Overworld + (river or beach); Terralith/BOP beaches, skylands |
| tiger_spawns | TIGER | Jungle (excl. bamboo_jungle); Terralith jungle variants |
| tarantula_hawk_spawns | DESERT | Desert + Terralith/Incendium desert (excl. mushroom) |
| void_worm_spawns | EMPTY | No biomes (weight 0 by default) |
| frilled_shark_spawns | DEEP_SEA | Deep ocean |
| mimic_octopus_spawns | MIMIC_OCTOPUS | Deep ocean, deep_lukewarm_ocean, alexscaves |
| seagull_spawns | SEAGULL | Overworld + beach; Terralith/BOP beaches, stony_shore |
| froststalker_spawns | FROSTSTALKER | ice_spikes, frozen_peaks, Terralith frostfire/frozen/glacial, snowy badlands, gravel_desert |
| tusklin_spawns | TUSKLIN | ice_spikes, frozen_peaks; overworld + snowy + plains; Terralith snowy, BOP snowblossom |
| laviathan_spawns | ALL_NETHER | All nether |
| cosmaw_spawns | COSMAW | End (excl. the_end) |
| toucan_spawns | TOUCAN | Jungle (excl. bamboo_jungle); Terralith jungle/skylands |
| maned_wolf_spawns | MANED_WOLF | Savanna (excl. sparse_jungle); Terralith shrubland/savanna |
| anaconda_spawns | ANACONDA | Jungle (excl. bamboo_jungle); Terralith jungle |
| anteater_spawns | ANTEATER | Jungle (excl. bamboo_jungle); Terralith jungle |
| rocky_roller_spawns | ROCKY_ROLLER | Badlands + Terralith/Incendium (excl. mushroom) |
| flutter_spawns | FLUTTER | Lush caves only |
| gelada_monkey_spawns | MEADOWS | Overworld + plains/sunflower_plains + plateau (badlands_plateau, wooded_badlands_plateau); Terralith shrubland/steppe/valley |
| jerboa_spawns | DESERT | Same as tarantula_hawk (desert + Terralith/Incendium) |
| terrapin_spawns | ICE_FREE_RIVER | Same as platypus |
| comb_jelly_spawns | COMB_JELLY | frozen_ocean, deep_frozen_ocean, alexscaves |
| cosmic_cod_spawns | COSMIC_COD | End |
| bunfungus_spawns | MUNGUS | Same as mungus |
| bison_spawns | BISON | Overworld + plains/sunflower_plains (excl. savanna); meadow, BOP field/grassland, Terralith shrubland/steppe |
| giant_squid_spawns | GIANT_SQUID | Deep ocean |
| devils_hole_pupfish_spawns | ALL_OVERWORLD | All overworld (then restricted to one pupfish chunk) |
| catfish_spawns | CATFISH | Swamp (excl. mangrove), river (excl. cold); orchid_swamp, ice_marsh, warm_river |
| flying_fish_spawns | FLYING_FISH | Overworld + ocean, excl. cold, excl. savanna, excl. deep/deep_lukewarm |
| skelewag_spawns | SKELEWAG | Overworld + deep ocean (excl. mushroom) |
| rain_frog_spawns | DESERT | Same as jerboa/tarantula_hawk |
| potoo_spawns | POTOO | Dark forest only |
| mudskipper_spawns | MANGROVE | mangrove_swamp, Terralith underground_jungle |
| rhinoceros_spawns | RHINOCEROS | Savanna + Terralith savanna variants |
| sugar_glider_spawns | SUGAR_GLIDER | Birch forest, old_growth_birch_forest, Terralith white_cliffs |
| farseer | FARSEER | Excl. mushroom_fields only (overworld); plus border distance check in entity |
| skreecher | SKREECHER | Excl. mushroom_fields; requires biome tag `alexsmobs:skreechers_can_spawn_wardens` (e.g. deep_dark) |
| underminer | CAVES | Overworld, excl. ocean/mushroom/deep_dark; Terralith caves |
| murmur | MURMUR | Overworld, excl. ocean/mushroom/deep_dark; Terralith caves + cherry/snowblossom; height ≤ murmurSpawnHeight |
| skunk_spawns | SKUNK | Same biome set as TASMANIAN_DEVIL (forest, excl. savanna/cold/sparse_jungle; Terralith/BOP/cherry) |
| banana_slug_spawns | BANANA_SLUG | Old growth pine/spruce taiga; taiga + jungle + mushroom_fields (rare); Terralith/BOP/autumnity |
| blue_jay_spawns | ALL_FOREST | Same as grizzly bear |
| caiman_spawns | MANGROVE | Same as mudskipper |
| triops_spawns | DESERT | Same as jerboa/rain_frog/tarantula_hawk |

---

## 4. Structure-based spawns

**`AMWorldRegistry.modifyStructure(Holder<Structure>, StructureSpawnTarget)`** is the single place that adds spawns to structures. On Fabric, **nothing in code calls this**; the comment says structure overrides are applied via **datapack (structure JSON spawn_overrides) or mixin**. So by default, these structure spawns **do not run** unless you add a mixin that iterates structures and calls `modifyStructure` or you add datapack overrides.

When implemented, the logic is:

| Structure | Condition | Mob | Category | Weight | Min | Max |
|-----------|------------|-----|----------|--------|-----|-----|
| End City | `mimicubeSpawnInEndCity && mimicubeSpawnWeight > 0` | Mimicube | MONSTER | config | 1 | 3 |
| Nether Fossil | `soulVultureSpawnOnFossil && soulVultureSpawnWeight > 0` | Soul Vulture | MONSTER | config | 1 | 1 |
| Shipwreck | `restrictSkelewagSpawns && skelewagSpawnWeight > 0` | Skelewag | MONSTER | config | 1 | 2 |
| Any in tag `alexsmobs:spawns_underminers` (e.g. mineshaft) | `restrictUnderminerSpawns && underminerSpawnWeight > 0` | Underminer | AMBIENT | config | 1 | 1 |

Tag file: `data/alexsmobs/tags/worldgen/structure/spawns_underminers.json` → `minecraft:mineshaft`.

---

## 5. Special spawners and restrictions

### 5.1 Beached Cachalot Whale (`BeachedCachalotWhaleSpawner`)

- **When:** Thundering and `beachedCachalotWhales` config; timer 1200 ticks; delay stored in `AMWorldData`.
- **Where:** Biome must match **`BiomeConfig.cachalot_whale_beached_spawns`** → `BEACHED_CACHALOT_WHALE` (beach, gravel_beach, dune_beach, stony_shore). Position chosen near a random player (within 84 blocks), must pass `NaturalSpawner.isSpawnPositionOk` (like wandering trader) and solid blocks check.
- **Effect:** Spawns one Cachalot Whale, beached, restricted to 16 blocks.

Config: `beachedCachalotWhaleSpawnChance` (default 5), `beachedCachalotWhaleSpawnDelay` (default 24000).

### 5.2 Devil's Hole Pupfish chunk

- **Restriction:** If `restrictPupfishSpawns`, only **one** “pupfish chunk” per world. `AMWorldData.tickPupfish()` / `searchForPupfishChunk()` finds a chunk (within `pupfishChunkSpawnDistance`, default 2000) where water height is between 31 and 63; that chunk is stored and used for spawns.
- **Biome:** Biome config is `ALL_OVERWORLD`; actual spawns only in that chunk.

### 5.3 Farseer world border

- **Restriction:** In `EntityFarseer.checkFarseerSpawnRules`: if `restrictFarseerSpawns`, spawn only if distance to world border &lt; `farseerBorderSpawnDistance` (default 100).

### 5.4 Murmur height

- **Restriction:** `murmurSpawnHeight` (default -30); spawn only at or below that Y (handled in entity spawn rules).

### 5.5 Guster weather

- **Optional:** `limitGusterSpawnsToWeather` can restrict Guster spawns to certain weather (implementation in entity/spawn check).

### 5.6 Blobfish depth

- **Restriction:** `blobfishSpawnHeight` (default 25); spawn only in deep water (entity/spawn check).

### 5.7 Gelada Monkey height

- **Restriction:** `geladaMonkeySpawnHeight` (default 100); spawn only at or above that Y.

---

## 6. Spawn placement (SpawnPlacements)

All entities that spawn naturally have a **SpawnPlacements** registration in **`AMEntityRegistry.registerSpawnPlacements()`**: type (ON_GROUND, IN_WATER, IN_LAVA, NO_RESTRICTIONS), heightmap, and a predicate (e.g. `Animal::checkAnimalSpawnRules`, or entity-specific like `EntityCrocodile::canCrocodileSpawn`). These predicates can further restrict by block, light, or height; see each entity class for the exact rule.

**Block tag used for some spawn checks:** `data/alexsmobs/tags/blocks/am_spawns.json` — values include `#minecraft:base_stone_overworld`, dirt, sand, red_sand, gravel, terracotta, mud (used where spawn code checks “allowed blocks”).

---

## 7. Leafcutter anthill (structure feature, not mob)

- **Modifier:** `AMLeafcutterAntBiomeModifier` adds the placed feature `alexsmobs:leafcutter_anthill` to biomes that pass **`BiomeConfig.leafcutter_anthill_spawns`** → `LEAFCUTTER_ANTHILL` (jungle excl. bamboo_jungle; Terralith jungle; underground_jungle cave).
- **Chance:** `AMConfig.leafcutterAnthillSpawnChance` (default 0.005). This controls how often the feature is placed, not mob spawn weight.

---

## 8. Biome tags (alexsmobs) used in spawning

- **`alexsmobs:skreechers_can_spawn_wardens`** — Biomes where Skreechers can spawn (and can summon Warden). Default: `minecraft:deep_dark` (in `data/alexsmobs/tags/worldgen/biome/skreechers_can_spawn_wardens.json`).
- **`alexsmobs:spawns_underminers`** (structure tag) — Structures that get Underminer structure spawns when implemented. Default: `minecraft:mineshaft`.

Other biome/structure tags exist for variants (e.g. red gusters, white seals, huge catfish); they are used for **entity variant logic**, not for adding/removing spawn entries.

---

## 9. Summary table: where each mob gets its spawn list

| Source | What it does |
|--------|----------------|
| **BiomeConfig + DefaultBiomes** | For each mob, a `Pair<id, SpawnBiomeData>`; `SpawnBiomeData` = list of (tag or registry name, include/exclude, group). Citadel’s `matches(biome, name)` decides if a biome is in the set. |
| **AMWorldRegistry.addBiomeSpawns** | For every biome, for each mob: if `testBiome(BiomeConfig.<mob>, biome)` and weight &gt; 0, add one `SpawnerData(entity, weight, min, max)` to that biome’s spawn list. |
| **AMConfig** | All default spawn weights and min/max; also toggles (mimicube in end city, soul vulture on fossil, skelewag/underminer restrict) and special rules (pupfish chunk, farseer border, murmur height, etc.). |
| **Structure spawns** | Implemented only if a mixin or datapack calls `modifyStructure` or adds structure spawn_overrides (Mimicube, Soul Vulture, Skelewag, Underminer). |
| **BeachedCachalotWhaleSpawner** | Custom tick spawner; biomes from `cachalot_whale_beached_spawns`; only when thundering and config enabled. |
| **SpawnPlacements** | Per-entity placement predicate (block, light, height) after biome and weight are chosen. |

This is the full picture of spawning in the project: **rates**, **biomes per mob**, **how it’s registered**, **structure and special spawns**, and **placement rules**.
