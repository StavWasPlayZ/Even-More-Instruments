package com.cstav.evenmoreinstruments.networking.packet;

import java.util.Map;
import java.util.function.Supplier;

import com.cstav.evenmoreinstruments.client.gui.instrument.guitar.GuitarScreen;
import com.cstav.evenmoreinstruments.client.gui.instrument.keyboard.KeyboardScreen;
import com.cstav.evenmoreinstruments.client.gui.instrument.saxophone.SaxophoneScreen;
import com.cstav.evenmoreinstruments.client.gui.instrument.trombone.TromboneScreen;
import com.cstav.evenmoreinstruments.client.gui.instrument.violin.ViolinScreen;
import com.cstav.genshinstrument.networking.packet.instrument.OpenInstrumentPacket;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.FriendlyByteBuf;

public class ModOpenInstrumentPacket extends OpenInstrumentPacket {
    private static final Map<String, Supplier<Supplier<Screen>>> INSTRUMENT_MAP = Map.of(
        "keyboard", () -> KeyboardScreen::new,
        "violin", () -> ViolinScreen::new,
        "trombone", () -> TromboneScreen::new,
        "guitar", () -> GuitarScreen::new,
        "saxophone", () -> SaxophoneScreen::new
    );


    public ModOpenInstrumentPacket(final String instrumentId) {
        super(instrumentId);
    }
    
    public ModOpenInstrumentPacket(final FriendlyByteBuf buf) {
        super(buf);
    }


    @Override
    protected Map<String, Supplier<Supplier<Screen>>> getInstrumentMap() {
        return INSTRUMENT_MAP;
    }

}
