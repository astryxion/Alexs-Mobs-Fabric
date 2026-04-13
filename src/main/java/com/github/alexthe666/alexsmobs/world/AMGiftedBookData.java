package com.github.alexthe666.alexsmobs.world;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Tracks players who have received the Animal Dictionary in this world save.
 * Player persistent NBT is awkward under split minecraft-common; world SavedData matches vanilla per-world scope.
 */
public class AMGiftedBookData extends SavedData {

    private static final String IDENTIFIER = "alexsmobs_gifted_books";
    private static final String PLAYERS_TAG = "GiftedBookPlayers";

    private final Set<UUID> giftedPlayers = new HashSet<>();

    public AMGiftedBookData() {}

    public static AMGiftedBookData get(ServerLevel anyLevel) {
        ServerLevel overworld = anyLevel.getServer().getLevel(Level.OVERWORLD);
        return overworld.getDataStorage()
                .computeIfAbsent(AMGiftedBookData::load, AMGiftedBookData::new, IDENTIFIER);
    }

    public static AMGiftedBookData load(CompoundTag tag) {
        AMGiftedBookData data = new AMGiftedBookData();
        ListTag list = tag.getList(PLAYERS_TAG, Tag.TAG_STRING);
        for (int i = 0; i < list.size(); i++) {
            data.giftedPlayers.add(UUID.fromString(list.getString(i)));
        }
        return data;
    }

    /** Returns true if this is the first time we record a gift for this player (caller should give items). */
    public boolean claimFirstTimeGift(UUID playerId) {
        if (giftedPlayers.contains(playerId)) {
            return false;
        }
        giftedPlayers.add(playerId);
        setDirty();
        return true;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        ListTag list = new ListTag();
        for (UUID id : giftedPlayers) {
            list.add(StringTag.valueOf(id.toString()));
        }
        tag.put(PLAYERS_TAG, list);
        return tag;
    }
}
