package com.cstav.evenmoreinstruments.networking.packet;

import java.util.function.Supplier;

import com.cstav.evenmoreinstruments.client.gui.instrument.noteblockinstrument.NoteBlockInstrumentScreen;
import com.cstav.genshinstrument.networking.IModPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class OpenNoteBlockInstrumentPacket implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;

    private final NoteBlockInstrument instrument;
    private final InteractionHand hand;

    public OpenNoteBlockInstrumentPacket(final NoteBlockInstrument instrument, final InteractionHand hand) {
        this.instrument = instrument;
        this.hand = hand;
    }

    public OpenNoteBlockInstrumentPacket(final FriendlyByteBuf buf) {
        instrument = buf.readEnum(NoteBlockInstrument.class);
        hand = buf.readEnum(InteractionHand.class);
    }
    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeEnum(instrument);
        buf.writeEnum(hand);
    }


    @Override
    public boolean handle(Supplier<Context> arg0) {
        final Context context = arg0.get();

        context.enqueueWork(() ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                Minecraft.getInstance().setScreen(new NoteBlockInstrumentScreen(hand, instrument))
        ));

        return true;
    }
    
}
