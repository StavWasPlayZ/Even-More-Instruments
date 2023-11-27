package com.cstav.evenmoreinstruments.client.gui.options;

import com.cstav.evenmoreinstruments.client.ModClientConfigs;
import com.cstav.evenmoreinstruments.client.gui.instrument.violin.ViolinScreen;
import com.cstav.evenmoreinstruments.client.gui.instrument.violin.ViolinSoundType;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.GridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.SoundTypeOptionsScreen;
import com.cstav.genshinstrument.client.util.TogglablePedalSound;

public class ViolinOptionsScreen extends SoundTypeOptionsScreen<ViolinSoundType> {
    private static final String SOUND_TYPE_KEY = "button.evenmoreinstruments.violin.soundType",
        OPTIONS_LABEL_KEY = "label.evenmoreinstruments.violin_options";

    public ViolinOptionsScreen(GridInstrumentScreen screen) {
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
    protected ViolinSoundType getInitSoundType() {
        return ModClientConfigs.VIOLIN_SOUND_TYPE.get();
    }

    @Override
    protected boolean isValidForSet(InstrumentScreen arg0) {
        return arg0 instanceof ViolinScreen;
    }

    @Override
    public TogglablePedalSound<ViolinSoundType> midiPedalListener() {
        return new TogglablePedalSound<>(ViolinSoundType.FULL_NOTE, ViolinSoundType.HALF_NOTE);
    }


    @Override
    protected void saveSoundType(ViolinSoundType arg0) {
        ModClientConfigs.VIOLIN_SOUND_TYPE.set(arg0);
    }

    @Override
    protected ViolinSoundType[] values() {
        return ViolinSoundType.values();
    }

}
