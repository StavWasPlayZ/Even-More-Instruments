package com.cstav.evenmoreinstruments.client.gui.instrument.koto;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.client.gui.instrument.pipa.PipaScreen;
import com.cstav.evenmoreinstruments.sound.ModSounds;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid.GridInstrumentScreen;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class KotoScreen extends GridInstrumentScreen {
    public static final ResourceLocation INSTRUMENT_ID = new ResourceLocation(EMIMain.MODID, "koto");

    @Override
    public NoteSound[] getInitSounds() {
        return ModSounds.KOTO;
    }

    @Override
    public ResourceLocation getInstrumentId() {
        return INSTRUMENT_ID;
    }
    @Override
    public boolean isGenshinInstrument() {
        return false;
    }

    @Override
    public ResourceLocation getSourcePath() {
        return PipaScreen.INSTRUMENT_ID;
    }

    private static final InstrumentThemeLoader THEME_LOADER = InstrumentThemeLoader.fromOther(
        PipaScreen.INSTRUMENT_ID,
        INSTRUMENT_ID
    );
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }
    
}