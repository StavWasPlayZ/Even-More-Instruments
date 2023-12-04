package com.cstav.evenmoreinstruments.client.gui.instrument.violin;

import java.util.function.Supplier;

import com.cstav.evenmoreinstruments.sound.ModSounds;
import com.cstav.genshinstrument.client.config.enumType.SoundType;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public enum ViolinSoundType implements SoundType {
    FULL_NOTE(() -> ModSounds.VIOLIN_FULL_NOTE),
    HALF_NOTE(() -> ModSounds.VIOLIN_HALF_NOTE);

    private Supplier<NoteSound[]> soundArr;
    private ViolinSoundType(final Supplier<NoteSound[]> soundType) {
        this.soundArr = soundType;
    }

    public Supplier<NoteSound[]> getSoundArr() {
        return soundArr;
    }

    public ViolinSoundType getOpposite() {
        return (this == FULL_NOTE) ? HALF_NOTE : FULL_NOTE;
    }
}
