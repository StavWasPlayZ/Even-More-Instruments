package com.cstav.evenmoreinstruments.client.gui.instrument.keyboard;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.sound.ModSounds;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid.GridInstrumentScreen;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KeyboardScreen extends GridInstrumentScreen {
    public static final ResourceLocation INSTRUMENT_ID = EMIMain.loc("keyboard");

    @Override
    public boolean isGenshinInstrument() {
        return false;
    }


    @Override
    public NoteSound[] getInitSounds() {
        return ModSounds.KEYBOARD;
    }

    @Override
    public ResourceLocation getInstrumentId() {
        return INSTRUMENT_ID;
    }


    public static final InstrumentThemeLoader THEME_LOADER = new InstrumentThemeLoader(INSTRUMENT_ID);
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }
    
}
