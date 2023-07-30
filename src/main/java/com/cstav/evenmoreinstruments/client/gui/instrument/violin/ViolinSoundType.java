package com.cstav.evenmoreinstruments.client.gui.instrument.violin;

import java.util.function.Supplier;

import com.cstav.evenmoreinstruments.sound.ModSounds;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum ViolinSoundType {
    FULL_NOTE(() -> ModSounds.VIOLIN_FULL_NOTE),
    HALF_NOTE(() -> ModSounds.VIOLIN_HALF_NOTE);

    private Supplier<NoteSound[]> soundArr;
    private ViolinSoundType(final Supplier<NoteSound[]> soundType) {
        this.soundArr = soundType;
    }

    public Supplier<NoteSound[]> soundArr() {
        return soundArr;
    }
}
