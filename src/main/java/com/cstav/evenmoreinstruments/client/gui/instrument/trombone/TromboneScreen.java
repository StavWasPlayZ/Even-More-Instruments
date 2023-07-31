package com.cstav.evenmoreinstruments.client.gui.instrument.trombone;

import com.cstav.evenmoreinstruments.Main;
import com.cstav.evenmoreinstruments.sound.ModSounds;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.AbstractGridInstrumentScreen;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

//NOTE: There to load it on startup
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public class TromboneScreen extends AbstractGridInstrumentScreen {
    public static final String INSTRUMENT_ID = "trombone";

    public TromboneScreen(InteractionHand hand) {
        super(hand);
    }

    @Override
    public NoteSound[] getSounds() {
        return ModSounds.TROMBONE;
    }

    @Override
    public ResourceLocation getInstrumentId() {
        return new ResourceLocation(Main.MODID, INSTRUMENT_ID);
    }
    @Override
    public boolean isGenshinInstrument() {
        return false;
    }


    private static final InstrumentThemeLoader THEME_LOADER = initThemeLoader(Main.MODID, INSTRUMENT_ID);
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }
    
}