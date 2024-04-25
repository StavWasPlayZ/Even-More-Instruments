package com.cstav.evenmoreinstruments.client.gui.instrument.violin;

import com.cstav.evenmoreinstruments.client.gui.instrument.partial.CyclableSoundType;
import com.cstav.evenmoreinstruments.sound.ModSounds;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.function.Supplier;

@OnlyIn(Dist.CLIENT)
public enum ViolinSoundType implements CyclableSoundType<ViolinSoundType> {
    FULL_NOTE(() -> ModSounds.VIOLIN_FULL_NOTE),
    HALF_NOTE(() -> ModSounds.VIOLIN_HALF_NOTE);

    private final Supplier<NoteSound[]> soundArr;
    private ViolinSoundType(final Supplier<NoteSound[]> soundType) {
        this.soundArr = soundType;
    }

    public Supplier<NoteSound[]> getSoundArr() {
        return soundArr;
    }

    public ViolinSoundType getNext() {
        return values()[(ordinal() + 1) % values().length];
    }
}
