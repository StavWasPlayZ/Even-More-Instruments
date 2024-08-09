package com.cstav.evenmoreinstruments.client;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.client.gui.instrument.guitar.GuitarScreen;
import com.cstav.evenmoreinstruments.client.gui.instrument.keyboard.KeyboardScreen;
import com.cstav.evenmoreinstruments.client.gui.instrument.koto.KotoScreen;
import com.cstav.evenmoreinstruments.client.gui.instrument.noteblockinstrument.NoteBlockInstrumentScreen;
import com.cstav.evenmoreinstruments.client.gui.instrument.pipa.PipaScreen;
import com.cstav.evenmoreinstruments.client.gui.instrument.saxophone.SaxophoneScreen;
import com.cstav.evenmoreinstruments.client.gui.instrument.shamisen.ShamisenScreen;
import com.cstav.evenmoreinstruments.client.gui.instrument.trombone.TromboneScreen;
import com.cstav.evenmoreinstruments.client.gui.instrument.violin.ViolinScreen;
import com.cstav.evenmoreinstruments.util.CommonUtil;
import com.cstav.genshinstrument.client.gui.screen.instrument.InstrumentScreenRegistry;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.Map;
import java.util.function.Supplier;

@EventBusSubscriber(value = Dist.CLIENT, bus = Bus.MOD, modid = EMIMain.MODID)
public class ClientInitiator {

    private static final Map<ResourceLocation, Supplier<? extends InstrumentScreen>> INSTRUMENTS = Map.of(
        KeyboardScreen.INSTRUMENT_ID, KeyboardScreen::new,
        ViolinScreen.INSTRUMENT_ID, ViolinScreen::new,
        TromboneScreen.INSTRUMENT_ID, TromboneScreen::new,
        GuitarScreen.INSTRUMENT_ID, GuitarScreen::new,
        PipaScreen.INSTRUMENT_ID, PipaScreen::new,
        ShamisenScreen.INSTRUMENT_ID, ShamisenScreen::new,
        KotoScreen.INSTRUMENT_ID, KotoScreen::new,
        SaxophoneScreen.INSTRUMENT_ID, SaxophoneScreen::new
    );

    @SubscribeEvent
    public static void setupClient(final FMLClientSetupEvent event) {
        InstrumentScreenRegistry.register(INSTRUMENTS);
    }

}
