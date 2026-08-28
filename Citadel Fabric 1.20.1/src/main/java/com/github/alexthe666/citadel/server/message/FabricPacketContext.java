package com.github.alexthe666.citadel.server.message;

import net.minecraft.world.entity.player.Player;

public class FabricPacketContext implements PacketContext {
    private boolean packetHandled;
    private final Player sender;
    private final PacketDirection direction;

    public FabricPacketContext(Player sender, PacketDirection direction) {
        this.sender = sender;
        this.direction = direction;
    }

    @Override
    public void setPacketHandled(boolean handled) {
        this.packetHandled = handled;
    }

    @Override
    public void enqueueWork(Runnable runnable) {
        runnable.run();
    }

    @Override
    public PacketDirection getDirection() {
        return direction;
    }

    @Override
    public Player getSender() {
        return sender;
    }
}
