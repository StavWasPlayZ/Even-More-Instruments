package com.cstav.evenmoreinstruments.client.gui.instrument.partial;

import com.cstav.evenmoreinstruments.client.KeyMappings;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid.GridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.SoundTypeOptionsScreen;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * A class defining the usage of cyclable sounds within this instrument.
 * The implementation allows the player to cycle the provided sounds using
 * their keybinds.
 */
@OnlyIn(Dist.CLIENT)
public abstract class CyclableInstrumentScreen<T extends CyclableSoundType<T>> extends GridInstrumentScreen {

    private T soundType = null;
    public T getSoundType() {
        return (soundType == null)
            ? (soundType = defSoundType())
            : soundType;
    }
    public void setSoundType(T soundType) {
        this.soundType = soundType;
    }

    @SuppressWarnings("unchecked")
    public T defSoundType() {
        return ((SoundTypeOptionsScreen<T>)optionsScreen).getPreferredSoundType();
    }

    @Override
    public NoteSound[] getInitSounds() {
        return defSoundType().getSoundArr().get();
    }
    

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        return changeSoundType(pKeyCode, pScanCode, true) || super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        return changeSoundType(pKeyCode, pScanCode, false) || super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }

    @Override
    public boolean isKeyConsumed(int keyCode, int scanCode) {
        return super.isKeyConsumed(keyCode, scanCode) || checkSoundTypeKey(keyCode, scanCode);
    }


    private boolean soundTypeChanged = false;
    private boolean changeSoundType(int keyCode, int scanCode, boolean pressed) {
        if (!checkSoundTypeKey(keyCode, scanCode))
            return false;

        if (pressed) {
            if (soundTypeChanged)
                return false;
        } else if (!soundTypeChanged)
            return false;

        updateSoundType(getSoundType().getNext());
        soundTypeChanged = pressed;

        return true;
    }
    private boolean checkSoundTypeKey(int keyCode, int scanCode) {
        return KeyMappings.INSTRUMENT_TYPE_MODIFIER.get().matches(keyCode, scanCode);
    }


    private void updateSoundType(final T sound) {
        setNoteSounds((soundType = sound).getSoundArr().get());
    }

    @Override
    public void onOptionsOpen() {
        updateSoundType(defSoundType());
        super.onOptionsOpen();
    }


    @Override
    protected abstract SoundTypeOptionsScreen<T> initInstrumentOptionsScreen();

}
