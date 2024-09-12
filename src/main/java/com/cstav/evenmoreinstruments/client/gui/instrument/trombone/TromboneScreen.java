package com.cstav.evenmoreinstruments.client.gui.instrument.trombone;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.sound.ModSounds;
import com.cstav.genshinstrument.client.gui.screen.instrument.nightwind_horn.NightwindHornScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid.GridInstrumentScreen;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class TromboneScreen extends GridInstrumentScreen {
    public static final ResourceLocation INSTRUMENT_ID = EMIMain.loc("trombone");

    @Override
    public NoteSound[] getInitSounds() {
        return ModSounds.TROMBONE;
    }

    @Override
    public ResourceLocation getInstrumentId() {
        return INSTRUMENT_ID;
    }
    @Override
    public boolean isGenshinInstrument() {
        return false;
    }


    public static final InstrumentThemeLoader THEME_LOADER = InstrumentThemeLoader.fromOther(
        NightwindHornScreen.THEME_LOADER,
        INSTRUMENT_ID
    );
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }
    
}
