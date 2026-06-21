package com.github.alexthe666.alexsmobs.tileentity;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.config.AMConfig;
import com.github.alexthe666.alexsmobs.network.MessageUpdateTransmutablesToDisplay;
import com.github.alexthe666.alexsmobs.misc.AMAdvancementTriggerRegistry;
import com.github.alexthe666.alexsmobs.misc.AMSoundRegistry;
import com.github.alexthe666.alexsmobs.misc.TransmutationData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.UUIDUtil;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.*;

public class TileEntityTransmutationTable  extends BlockEntity {

    private static final Identifier COMMON_ITEMS = Identifier.fromNamespaceAndPath("alexsmobs", "gameplay/transmutation_table_common");
    private static final Identifier UNCOMMON_ITEMS = Identifier.fromNamespaceAndPath("alexsmobs", "gameplay/transmutation_table_uncommon");
    private static final Identifier RARE_ITEMS = Identifier.fromNamespaceAndPath("alexsmobs", "gameplay/transmutation_table_rare");
    public int ticksExisted;
    private int totalTransmuteCount = 0;
    private final Map<UUID, TransmutationData> playerToData = new HashMap<>();
    private final ItemStack[] possiblities = new ItemStack[3];
    private static final Random RANDOM = new Random();

    private UUID rerollPlayerUUID = null;

    public TileEntityTransmutationTable(BlockPos pos, BlockState state) {
        super(AMTileEntityRegistry.TRANSMUTATION_TABLE, pos, state);
    }

    public static void commonTick(Level level, BlockPos pos, BlockState state, TileEntityTransmutationTable entity) {
        entity.tick();
    }

    private static ItemStack createFromLootTable(Player player, Identifier loc) {
        if (player.level().isClientSide()) {
            return ItemStack.EMPTY;
        } else {
            LootTable loottable = player.level().getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, loc));
            List<ItemStack> loots = loottable.getRandomItems((new LootParams.Builder((ServerLevel) player.level())).create(LootContextParamSets.EMPTY));
            return loots.isEmpty() ? ItemStack.EMPTY : loots.get(0);
        }
    }

    @Override
    protected void loadAdditional(ValueInput input) {
        super.loadAdditional(input);
        totalTransmuteCount = input.getIntOr("TotalCount", 0);
        playerToData.clear();
        for (ValueInput row : input.childrenListOrEmpty("PlayerTransmutationData")) {
            row.read("UUID", UUIDUtil.CODEC).ifPresent(uuid -> {
                row.child("TransmutationData").ifPresent(td -> playerToData.put(uuid, TransmutationData.load(td)));
            });
        }
        for (int i = 0; i < 3; i++) {
            possiblities[i] = null;
        }
        for (int i = 0; i < 3; i++) {
            final int slot = i;
            input.read("Possiblity" + slot, ItemStack.CODEC).ifPresent(stack -> possiblities[slot] = stack.isEmpty() ? null : stack);
        }
    }

    @Override
    protected void saveAdditional(ValueOutput output) {
        super.saveAdditional(output);
        output.putInt("TotalCount", totalTransmuteCount);
        ValueOutput.ValueOutputList plist = output.childrenList("PlayerTransmutationData");
        for (Map.Entry<UUID, TransmutationData> entry : playerToData.entrySet()) {
            ValueOutput inner = plist.addChild();
            inner.store("UUID", UUIDUtil.CODEC, entry.getKey());
            entry.getValue().save(inner.child("TransmutationData"));
        }
        for (int i = 0; i < 3; i++) {
            if (possiblities[i] != null && !possiblities[i].isEmpty()) {
                output.store("Possiblity" + i, ItemStack.CODEC, possiblities[i]);
            }
        }
    }


    private void randomizeResults(Player player){
        rollPossiblity(player, 0);
        rollPossiblity(player, 1);
        rollPossiblity(player, 2);
        int dataIndex = RANDOM.nextInt(2);
        if(playerToData.containsKey(player.getUUID()) && !AMConfig.limitTransmutingToLootTables){
            TransmutationData data = playerToData.get(player.getUUID());
            if(RANDOM.nextFloat() < Math.min(0.01875F * data.getTotalWeight(), 0.2F)){
                ItemStack stack = data.getRandomItem(RANDOM);
                if(stack != null && !stack.isEmpty()){
                    possiblities[dataIndex] = stack;
                }
            }
        }
        AlexsMobs.sendMSGToAll(new MessageUpdateTransmutablesToDisplay(player.getId(), possiblities[0], possiblities[1], possiblities[2]));
    }

    public void rollPossiblity(Player player, int i){
        if(player == null || player.level().isClientSide() || !(player.level() instanceof ServerLevel)){
            return;
        }
        Identifier loot;
        int safeIndex = Mth.clamp(i, 0, 2);
        switch (safeIndex){
            default:
            case 0:
                loot = COMMON_ITEMS;
                break;
            case 1:
                loot = UNCOMMON_ITEMS;
                break;
            case 2:
                loot = RARE_ITEMS;
                break;
        }
        possiblities[safeIndex] = createFromLootTable(player, loot);
    }

    public boolean hasPossibilities(){
        for(int i = 0; i < 3; i++){
            if(possiblities[i] == null || possiblities[i].isEmpty()){
                return false;
            }
        }
        return true;
    }

    public ItemStack getPossibility(int i){
        int safeIndex = Mth.clamp(i, 0, 2);
        ItemStack possible = possiblities[safeIndex];
        return possible == null ? ItemStack.EMPTY : possible;
    }

    public void postTransmute(Player player, ItemStack from, ItemStack to){
        TransmutationData data;
        if(playerToData.containsKey(player.getUUID())){
            data = playerToData.get(player.getUUID());
        }else{
            data = new TransmutationData();
        }
        data.onTransmuteItem(from, to);
        playerToData.put(player.getUUID(), data);
        totalTransmuteCount += from.getCount();
        if(player instanceof ServerPlayer && totalTransmuteCount >= 1000){
            AMAdvancementTriggerRegistry.TRANSMUTE_1000_ITEMS.trigger((ServerPlayer)player);
        }
        setRerollPlayerUUID(player.getUUID());
    }

    public void tick() {
        ticksExisted++;
        if(rerollPlayerUUID != null){
            Player player = level.getPlayerByUUID(rerollPlayerUUID);
            if(player != null){
                this.level.playSound(null, this.getBlockPos(), AMSoundRegistry.TRANSMUTE_ITEM, SoundSource.BLOCKS, 1F, 0.9F + player.getRandom().nextFloat() * 0.2F);
                this.randomizeResults(player);
            }
            rerollPlayerUUID = null;
        }
    }

    public void setRerollPlayerUUID(UUID uuid){
        this.rerollPlayerUUID = uuid;
    }
}
