package com.cstav.evenmoreinstruments.networking;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.networking.packet.*;
import com.cstav.genshinstrument.networking.IModPacket;
import com.cstav.genshinstrument.util.ServerUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.List;

// Copy pasta
@EventBusSubscriber(modid = EMIMain.MODID, bus = Bus.MOD)
public class EMIPacketHandler {
    @SuppressWarnings("unchecked")
    private static final List<Class<IModPacket>> ACCEPTABLE_PACKETS = List.of(new Class[] {
        LooperRecordStatePacket.class, OpenNoteBlockInstrumentPacket.class,
        // Sync stuff
        DoesLooperExistPacket.class, LooperUnplayablePacket.class, SyncModTagPacket.class,
        LooperPlayStatePacket.class
    });

    private static int id = 0;
    public static void registerPackets() {
        ServerUtil.registerModPackets(INSTANCE, ACCEPTABLE_PACKETS, () -> id++);
    }


    private static final String PROTOCOL_VERSION = "1.1";

    private static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
        new ResourceLocation(EMIMain.MODID, "mod_networking"),
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

}
