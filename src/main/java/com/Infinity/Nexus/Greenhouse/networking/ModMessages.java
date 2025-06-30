package com.Infinity.Nexus.Greenhouse.networking;

import com.Infinity.Nexus.Greenhouse.networking.packet.AreaVisualizerC2SPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

public class ModMessages {
    @SubscribeEvent
    public static void register(final RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1")
                .versioned("1.0")
                .optional();

        registrar.playToServer(
                AreaVisualizerC2SPacket.TYPE,
                AreaVisualizerC2SPacket.STREAM_CODEC,
                AreaVisualizerC2SPacket::handle
        );
    }

    public static void sendToServer(AreaVisualizerC2SPacket packet) {
        PacketDistributor.sendToServer(packet);
    }
}