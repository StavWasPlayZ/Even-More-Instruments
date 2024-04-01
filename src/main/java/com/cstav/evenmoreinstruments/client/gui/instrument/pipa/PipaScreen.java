package com.cstav.evenmoreinstruments.client.gui.instrument.pipa;

import com.cstav.evenmoreinstruments.Main;
import com.cstav.evenmoreinstruments.client.gui.instrument.partial.CyclableInstrumentScreen;
import com.cstav.evenmoreinstruments.client.gui.options.PipaOptionsScreen;
import com.cstav.evenmoreinstruments.sound.ModSounds;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.GridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.SoundTypeOptionsScreen;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
//NOTE: There to load it on startup
@EventBusSubscriber(Dist.CLIENT)
public class PipaScreen extends CyclableInstrumentScreen<PipaSoundType> {
    public static final ResourceLocation INSTRUMENT_ID = new ResourceLocation(Main.MODID, "pipa");

    @Override
    protected SoundTypeOptionsScreen<PipaSoundType> initInstrumentOptionsScreen() {
        return new PipaOptionsScreen(this);
    }

    @Override
    public ResourceLocation getInstrumentId() {
        return INSTRUMENT_ID;
    }
    @Override
    public boolean isGenshinInstrument() {
        return false;
    }


    private static final InstrumentThemeLoader THEME_LOADER = new InstrumentThemeLoader(INSTRUMENT_ID);
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }

}