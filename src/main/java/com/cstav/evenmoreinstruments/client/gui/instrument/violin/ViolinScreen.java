package com.cstav.evenmoreinstruments.client.gui.instrument.violin;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.client.gui.instrument.partial.CyclableInstrumentScreen;
import com.cstav.evenmoreinstruments.client.gui.options.ViolinOptionsScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.SoundTypeOptionsScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ViolinScreen extends CyclableInstrumentScreen<ViolinSoundType> {
    public static final ResourceLocation INSTRUMENT_ID = EMIMain.loc("violin");


    @Override
    public ResourceLocation getInstrumentId() {
        return INSTRUMENT_ID;
    }
    @Override
    public boolean isGenshinInstrument() {
        return false;
    }


    @Override
    protected SoundTypeOptionsScreen<ViolinSoundType> initInstrumentOptionsScreen() {
        return new ViolinOptionsScreen(this);
    }


    public static final InstrumentThemeLoader THEME_LOADER = new InstrumentThemeLoader(INSTRUMENT_ID);
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }
    
}
