package com.cstav.evenmoreinstruments.client.gui.instrument.pipa;

import com.cstav.evenmoreinstruments.client.gui.instrument.partial.CyclableSoundType;
import com.cstav.evenmoreinstruments.sound.ModSounds;
import com.cstav.genshinstrument.sound.NoteSound;

import java.util.function.Supplier;

public enum PipaSoundType implements CyclableSoundType<PipaSoundType> {
    REGULAR(() -> ModSounds.PIPA_REGULAR),
    TREMOLO(() -> ModSounds.PIPA_TERMOLO);

    private final Supplier<NoteSound[]> soundArr;
    private PipaSoundType(final Supplier<NoteSound[]> soundType) {
        this.soundArr = soundType;
    }

    public Supplier<NoteSound[]> getSoundArr() {
        return soundArr;
    }

    public PipaSoundType getNext() {
        return values()[(ordinal() + 1) % values().length];
    }

}
