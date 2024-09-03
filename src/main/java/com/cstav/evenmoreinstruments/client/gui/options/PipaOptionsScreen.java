package com.cstav.evenmoreinstruments.client.gui.options;

import com.cstav.evenmoreinstruments.client.ModClientConfigs;
import com.cstav.evenmoreinstruments.client.gui.instrument.pipa.PipaScreen;
import com.cstav.evenmoreinstruments.client.gui.instrument.pipa.PipaSoundType;
import com.cstav.evenmoreinstruments.client.gui.options.partial.CyclableSoundTypeInstrumentOptionsScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid.GridInstrumentScreen;
import com.cstav.genshinstrument.client.util.TogglablePedalSound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PipaOptionsScreen extends CyclableSoundTypeInstrumentOptionsScreen<PipaSoundType> {
    private static final String SOUND_TYPE_KEY = "button.evenmoreinstruments.pipa.soundType",
        OPTIONS_LABEL_KEY = "label.evenmoreinstruments.pipa_options";

    public PipaOptionsScreen(GridInstrumentScreen screen) {
        super(screen);
    }


    @Override
    protected String soundTypeButtonKey() {
        return SOUND_TYPE_KEY;
    }
    @Override
    protected String optionsLabelKey() {
        return OPTIONS_LABEL_KEY;
    }


    @Override
    protected PipaSoundType getInitSoundType() {
        return ModClientConfigs.PIPA_SOUND_TYPE.get();
    }

    @Override
    protected boolean isValidForSet(InstrumentScreen arg0) {
        return arg0 instanceof PipaScreen;
    }

    @Override
    public TogglablePedalSound<PipaSoundType> midiPedalListener() {
        return new TogglablePedalSound<>(PipaSoundType.REGULAR, PipaSoundType.TREMOLO);
    }


    @Override
    protected void saveSoundType(PipaSoundType arg0) {
        ModClientConfigs.PIPA_SOUND_TYPE.set(arg0);
    }

    @Override
    protected PipaSoundType[] values() {
        return PipaSoundType.values();
    }

}
