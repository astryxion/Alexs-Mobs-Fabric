package com.github.alexthe666.alexsmobs.network;

import com.github.alexthe666.alexsmobs.effect.AMEffectRegistry;
import com.github.alexthe666.alexsmobs.entity.EntityTarantulaHawk;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.EntityType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

/**
 * Server -> Client packet to apply tarantula hawk sting effect.
 */
public record MessageTarantulaHawkSting(int hawk, int spider) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<MessageTarantulaHawkSting> ID =
        new CustomPacketPayload.Type<>(Identifier.fromNamespaceAndPath("alexsmobs", "tarantula_hawk_sting"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MessageTarantulaHawkSting> CODEC = new StreamCodec<>() {
        @Override
        public MessageTarantulaHawkSting decode(RegistryFriendlyByteBuf buf) {
            int hawk = buf.readInt();
            int spider = buf.readInt();
            return new MessageTarantulaHawkSting(hawk, spider);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, MessageTarantulaHawkSting packet) {
            buf.writeInt(packet.hawk);
            buf.writeInt(packet.spider);
        }
    };

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return ID;
    }

    public static void handleClient(MessageTarantulaHawkSting payload, ClientPlayNetworking.Context context) {
                    var player = context.player();
            if (player != null && player.level() != null) {
                Entity entity = player.level().getEntity(payload.hawk);
                Entity spider = player.level().getEntity(payload.spider);
                if (entity instanceof EntityTarantulaHawk && spider instanceof LivingEntity livingSpider
                        && livingSpider.getType().builtInRegistryHolder().is(net.minecraft.tags.EntityTypeTags.ARTHROPOD) /* TODO: verify tag */) {
                    livingSpider.addEffect(new MobEffectInstance(net.minecraft.core.Holder.direct(AMEffectRegistry.DEBILITATING_STING),
                        EntityTarantulaHawk.STING_DURATION));
                }
            }

    }
}
