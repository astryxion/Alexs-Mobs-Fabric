package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.inventory.MenuTransmutationTable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import java.util.function.Supplier;

public class MessageTransmuteFromMenu {

    private int playerId;
    private int choice;

    public MessageTransmuteFromMenu(int playerId, int choice) {
        this.playerId = playerId;
        this.choice = choice;
    }

    public MessageTransmuteFromMenu() {
    }

    public static MessageTransmuteFromMenu read(FriendlyByteBuf buf) {
        return new MessageTransmuteFromMenu(buf.readInt(), buf.readInt());
    }

    public static void write(MessageTransmuteFromMenu message, FriendlyByteBuf buf) {
        buf.writeInt(message.playerId);
        buf.writeInt(message.choice);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageTransmuteFromMenu message, Supplier<AlexsMobs.PacketContext> context) {
            context.get().setPacketHandled(true);
            context.get().enqueueWork(() -> {
                Player player = context.get().getSender();
                if (context.get().isClient()) {
                    player = AlexsMobs.PROXY.getClientSidePlayer();
                }
                if (player.getId() == message.playerId && player.containerMenu instanceof MenuTransmutationTable) {
                    MenuTransmutationTable table = (MenuTransmutationTable) player.containerMenu;
                    table.transmute(player, message.choice);
                }
            });
        }
    }

}