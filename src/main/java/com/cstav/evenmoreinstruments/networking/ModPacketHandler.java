package com.cstav.evenmoreinstruments.networking;

import java.util.List;

import com.cstav.evenmoreinstruments.Main;
import com.cstav.evenmoreinstruments.networking.packet.LooperRemovedPacket;
import com.cstav.evenmoreinstruments.networking.packet.ModOpenInstrumentPacket;
import com.cstav.evenmoreinstruments.networking.packet.OpenNoteBlockInstrumentPacket;
import com.cstav.evenmoreinstruments.networking.packet.RecordStatePacket;
import com.cstav.evenmoreinstruments.networking.packet.SyncModTagPacket;
import com.cstav.evenmoreinstruments.networking.packet.UpdateLooperRemovedForInstrument;
import com.cstav.genshinstrument.networking.IModPacket;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

// Copy pasta
@EventBusSubscriber(modid = Main.MODID, bus = Bus.MOD)
public class ModPacketHandler {
    @SuppressWarnings("unchecked")
    private static final List<Class<IModPacket>> ACCEPTABLE_PACKETS = List.of(new Class[] {
        RecordStatePacket.class, OpenNoteBlockInstrumentPacket.class, ModOpenInstrumentPacket.class,
        UpdateLooperRemovedForInstrument.class, LooperRemovedPacket.class, SyncModTagPacket.class
    });


    private static final String PROTOCOL_VERSION = "1";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(Main.MODID, "main"),
        () -> PROTOCOL_VERSION,
        PROTOCOL_VERSION::equals,
        PROTOCOL_VERSION::equals
    );


    public static <T> void sendToServer(final T packet) {
        INSTANCE.sendToServer(packet);
    }
    public static <T> void sendToClient(final T packet, final ServerPlayer player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }


    @SubscribeEvent
    public static void registerPackets(final FMLCommonSetupEvent event) {
        event.enqueueWork(() ->
            com.cstav.genshinstrument.networking.ModPacketHandler.registerModPackets(INSTANCE, ACCEPTABLE_PACKETS)    
        );
    }

}
