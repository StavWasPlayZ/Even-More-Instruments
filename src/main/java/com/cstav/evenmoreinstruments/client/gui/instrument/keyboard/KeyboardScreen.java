package com.cstav.evenmoreinstruments.client.gui.instrument.keyboard;

import com.cstav.evenmoreinstruments.Main;
import com.cstav.evenmoreinstruments.sound.ModSounds;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.AbstractGridInstrumentScreen;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

//NOTE there just to make it load on setup
@EventBusSubscriber(Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class KeyboardScreen extends AbstractGridInstrumentScreen {
    public static final String INSTRUMENT_ID = "keyboard";

    public KeyboardScreen(InteractionHand hand) {
        super(hand);
    }
    @Override
    public boolean isGenshinInstrument() {
        return false;
    }


    @Override
    public NoteSound[] getSounds() {
        return ModSounds.KEYBOARD;
    }

    @Override
    public ResourceLocation getInstrumentId() {
        return new ResourceLocation(Main.MODID, INSTRUMENT_ID);
    }


    private static final InstrumentThemeLoader THEME_LOADER = initThemeLoader(Main.MODID, INSTRUMENT_ID);
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }
    
}