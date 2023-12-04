package com.cstav.evenmoreinstruments.networking.packet;

import com.cstav.evenmoreinstruments.client.gui.instrument.noteblockinstrument.NoteBlockInstrumentScreen;
import com.cstav.genshinstrument.networking.IModPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;

public class OpenNoteBlockInstrumentPacket implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;


    private final NoteBlockInstrument instrument;

    public OpenNoteBlockInstrumentPacket(final NoteBlockInstrument instrument) {
        this.instrument = instrument;
    }

    public OpenNoteBlockInstrumentPacket(final FriendlyByteBuf buf) {
        instrument = buf.readEnum(NoteBlockInstrument.class);
    }
    @Override
    public void write(final FriendlyByteBuf buf) {
        buf.writeEnum(instrument);
    }


    @Override
    public void handle(final Context context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> this::openScreen);
    }
    
    @OnlyIn(Dist.CLIENT)
    private void openScreen() {
        Minecraft.getInstance().setScreen(new NoteBlockInstrumentScreen(instrument));
    }

}
