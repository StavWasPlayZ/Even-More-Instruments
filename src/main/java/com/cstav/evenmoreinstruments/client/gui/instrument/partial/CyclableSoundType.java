package com.cstav.evenmoreinstruments.client.gui.instrument.partial;

import com.cstav.genshinstrument.client.config.enumType.SoundType;

public interface CyclableSoundType<T extends SoundType> extends SoundType {
    public T getNext();
}
