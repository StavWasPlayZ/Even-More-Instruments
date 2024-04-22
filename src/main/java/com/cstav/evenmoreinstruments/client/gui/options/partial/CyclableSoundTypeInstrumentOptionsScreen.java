package com.cstav.evenmoreinstruments.client.gui.options.partial;

import com.cstav.evenmoreinstruments.client.gui.instrument.partial.CyclableInstrumentScreen;
import com.cstav.evenmoreinstruments.client.gui.instrument.partial.CyclableSoundType;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.GridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.SoundTypeOptionsScreen;
import net.minecraft.client.gui.screens.Screen;

public abstract class CyclableSoundTypeInstrumentOptionsScreen<T extends CyclableSoundType<T>> extends SoundTypeOptionsScreen<T> {
    public CyclableSoundTypeInstrumentOptionsScreen(GridInstrumentScreen screen) {
        super(screen);
    }
    public CyclableSoundTypeInstrumentOptionsScreen(Screen lastScreen) {
        super(lastScreen);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void setPreferredSoundType(T preferredSoundType) {
        super.setPreferredSoundType(preferredSoundType);

        if (isValidForSet(instrumentScreen))
            ((CyclableInstrumentScreen<T>)instrumentScreen).setSoundType(preferredSoundType);
    }
}
