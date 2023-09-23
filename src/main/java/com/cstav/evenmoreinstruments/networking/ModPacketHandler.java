package com.cstav.evenmoreinstruments.networking;

import java.util.List;

import com.cstav.evenmoreinstruments.Main;
import com.cstav.evenmoreinstruments.networking.packet.LooperPlayStatePacket;
import com.cstav.evenmoreinstruments.networking.packet.LooperRecordStatePacket;
import com.cstav.evenmoreinstruments.networking.packet.LooperRemovedPacket;
import com.cstav.evenmoreinstruments.networking.packet.ModOpenInstrumentPacket;
import com.cstav.evenmoreinstruments.networking.packet.OpenNoteBlockInstrumentPacket;
import com.cstav.evenmoreinstruments.networking.packet.SyncModTagPacket;
import com.cstav.evenmoreinstruments.networking.packet.UpdateLooperRemovedForInstrument;
import com.cstav.genshinstrument.networking.IModPacket;
import com.cstav.genshinstrument.util.ServerUtil;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.Channel.VersionTest;
import net.minecraftforge.network.ChannelBuilder;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.SimpleChannel;

// Copy pasta
@EventBusSubscriber(modid = Main.MODID, bus = Bus.MOD)
public class ModPacketHandler {
    @SuppressWarnings("unchecked")
    private static final List<Class<IModPacket>> ACCEPTABLE_PACKETS = List.of(new Class[] {
        LooperRecordStatePacket.class, OpenNoteBlockInstrumentPacket.class, ModOpenInstrumentPacket.class,
        // Sync stuff
        UpdateLooperRemovedForInstrument.class, LooperRemovedPacket.class, SyncModTagPacket.class,
        LooperPlayStatePacket.class
    });

    private static int id = 0;
    public static void registerPackets() {
        ServerUtil.registerModPackets(INSTANCE, ACCEPTABLE_PACKETS, () -> id++);
    }


    private static final String PROTOCOL_VERSION = "1.1";

    private static int protocolVersion() {
        return Integer.parseInt(PROTOCOL_VERSION.replace(".", ""));
    }


    private static final SimpleChannel INSTANCE = ChannelBuilder
        .named(new ResourceLocation(Main.MODID, "mod_networking"))
        .networkProtocolVersion(protocolVersion())
        .acceptedVersions(VersionTest.exact(protocolVersion()))
    .simpleChannel();


    public static <T> void sendToServer(final T packet) {
        INSTANCE.send(packet, PacketDistributor.SERVER.noArg());
    }
    public static <T> void sendToClient(final T packet, final ServerPlayer player) {
        INSTANCE.send(packet, PacketDistributor.PLAYER.with(player));
    }

}
