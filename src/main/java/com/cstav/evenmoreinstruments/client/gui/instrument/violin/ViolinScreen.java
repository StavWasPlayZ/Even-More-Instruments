package com.cstav.evenmoreinstruments.client.gui.instrument.violin;

import com.cstav.evenmoreinstruments.Main;
import com.cstav.evenmoreinstruments.client.KeyMappings;
import com.cstav.evenmoreinstruments.client.gui.options.ViolinOptionsScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.GridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.options.instrument.partial.InstrumentOptionsScreen;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
//NOTE: There to load it on startup
@EventBusSubscriber(Dist.CLIENT)
public class ViolinScreen extends GridInstrumentScreen {
    public static final ResourceLocation INSTRUMENT_ID = new ResourceLocation(Main.MODID, "violin");


    //#region Handle long/short sounds

    private ViolinSoundType soundType = null;
    public ViolinSoundType getSoundType() {
        return (soundType == null)
            ? (soundType = defSoundType())
            : soundType;
    }
    public void setSoundType(ViolinSoundType soundType) {
        this.soundType = soundType;
    }

    public ViolinSoundType defSoundType() {
        return ((ViolinOptionsScreen)optionsScreen).getPreferredSoundType();
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

        updateSoundType(getSoundType().getOpposite());
        soundTypeChanged = pressed;

        return true;
    }
    private boolean checkSoundTypeKey(int keyCode, int scanCode) {
        return KeyMappings.INSTRUMENT_TYPE_MODIFIER.get().matches(keyCode, scanCode);
    }


    private void updateSoundType(final ViolinSoundType sound) {
        setNoteSounds((soundType = sound).getSoundArr().get());
    }

    @Override
    public void onOptionsOpen() {
        updateSoundType(defSoundType());
        super.onOptionsOpen();
    }

    //#endregion


    @Override
    public ResourceLocation getInstrumentId() {
        return INSTRUMENT_ID;
    }
    @Override
    public boolean isGenshinInstrument() {
        return false;
    }


    @Override
    protected InstrumentOptionsScreen initInstrumentOptionsScreen() {
        return new ViolinOptionsScreen(this);
    }


    private static final InstrumentThemeLoader THEME_LOADER = new InstrumentThemeLoader(INSTRUMENT_ID);
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }
    
}
