package com.cstav.evenmoreinstruments.networking.packet;

import java.util.Map;
import java.util.function.Supplier;

import com.cstav.evenmoreinstruments.client.gui.instrument.keyboard.KeyboardScreen;
import com.cstav.genshinstrument.networking.packet.instrument.OpenInstrumentPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;

public class ModOpenInstrumentPacket extends OpenInstrumentPacket {
    private static final Map<String, Supplier<ScreenOpenDelegate>> INSTRUMENT_MAP = Map.of(
        "keyboard", () -> KeyboardScreen::new
    );


    @Override
    protected Map<String, Supplier<ScreenOpenDelegate>> getInstrumentMap() {
        return INSTRUMENT_MAP;
    }

    public ModOpenInstrumentPacket(final String instrumentId, final InteractionHand hand) {
        super(instrumentId, hand);
    }
    
    public ModOpenInstrumentPacket(FriendlyByteBuf buf) {
        super(buf);
    }

}
