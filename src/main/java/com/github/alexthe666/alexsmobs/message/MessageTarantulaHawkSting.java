package com.github.alexthe666.alexsmobs.message;

import com.github.alexthe666.alexsmobs.AlexsMobs;
import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityTarantulaHawk;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import com.github.alexthe666.alexsmobs.entity.MobType;
import net.minecraft.world.entity.player.Player;
import java.util.function.Supplier;

public class MessageTarantulaHawkSting {

    public int hawk;
    public int spider;

    public MessageTarantulaHawkSting(int rider, int mount) {
        this.hawk = rider;
        this.spider = mount;
    }

    public MessageTarantulaHawkSting() {
    }

    public static MessageTarantulaHawkSting read(FriendlyByteBuf buf) {
        return new MessageTarantulaHawkSting(buf.readInt(), buf.readInt());
    }

    public static void write(MessageTarantulaHawkSting message, FriendlyByteBuf buf) {
        buf.writeInt(message.hawk);
        buf.writeInt(message.spider);
    }

    public static class Handler {
        public Handler() {
        }

        public static void handle(MessageTarantulaHawkSting message, Supplier<AlexsMobs.PacketContext> context) {
            context.get().setPacketHandled(true);
            context.get().enqueueWork(() -> {
                Player player = context.get().getSender();
                if (context.get().isClient()) {
                    player = AlexsMobs.PROXY.getClientSidePlayer();
                }

                if (player != null) {
                    if (player.level() != null) {
                        Entity entity = player.level().getEntity(message.hawk);
                        Entity spider = player.level().getEntity(message.spider);
                        if (entity instanceof EntityTarantulaHawk && spider instanceof LivingEntity && MobType.getMobType((LivingEntity) spider) == MobType.ARTHROPOD) {
                            ((LivingEntity) spider).addEffect(new MobEffectInstance(net.minecraft.core.registries.BuiltInRegistries.MOB_EFFECT.wrapAsHolder(AMEffectRegistry.DEBILITATING_STING), EntityTarantulaHawk.STING_DURATION));
                        }
                    }
                }
            });
        }
    }
}