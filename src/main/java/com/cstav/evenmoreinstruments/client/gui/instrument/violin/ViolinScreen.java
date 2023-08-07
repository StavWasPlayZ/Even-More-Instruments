package com.cstav.evenmoreinstruments.client.gui.instrument.violin;

import com.cstav.evenmoreinstruments.Main;
import com.cstav.evenmoreinstruments.client.KeyMappings;
import com.cstav.evenmoreinstruments.client.gui.options.ViolinOptionsScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.AbstractGridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screens.options.instrument.AbstractInstrumentOptionsScreen;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

//NOTE: There to load it on startup
@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public class ViolinScreen extends AbstractGridInstrumentScreen {
    public static final String INSTRUMENT_ID = "violin";

    public ViolinScreen(InteractionHand hand) {
        super(hand);
    }


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
        return ((ViolinOptionsScreen)optionsScreen).getPerferredSoundType();
    }

    @Override
    public NoteSound[] getSounds() {
        return getSoundType().getSoundArr().get();
    }

    @Override
    public boolean keyPressed(int pKeyCode, int pScanCode, int pModifiers) {
        return changeSoundType(pKeyCode, pScanCode) || super.keyPressed(pKeyCode, pScanCode, pModifiers);
    }
    @Override
    public boolean keyReleased(int pKeyCode, int pScanCode, int pModifiers) {
        return changeSoundType(pKeyCode, pScanCode) || super.keyReleased(pKeyCode, pScanCode, pModifiers);
    }
    private boolean changeSoundType(final int keyCode, final int scanCode) {
        if (KeyMappings.VIOLIN_TYPE_MODIFIER.get().matches(keyCode, scanCode)) {
            updateSoundType(getSoundType().getOpposite());
            return true;
        }

        return false;
    }

    private void updateSoundType(final ViolinSoundType sound) {
        noteGrid.setNoteSounds((soundType = sound).getSoundArr().get());
    }

    @Override
    public void onOptionsOpen() {
        updateSoundType(defSoundType());
        super.onOptionsOpen();
    }


    @Override
    public ResourceLocation getInstrumentId() {
        return new ResourceLocation(Main.MODID, INSTRUMENT_ID);
    }
    @Override
    public boolean isGenshinInstrument() {
        return false;
    }


    @Override
    protected AbstractInstrumentOptionsScreen initInstrumentOptionsScreen() {
        return new ViolinOptionsScreen(this);
    }


    private static final InstrumentThemeLoader THEME_LOADER = initThemeLoader(Main.MODID, INSTRUMENT_ID);
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }
    
}
