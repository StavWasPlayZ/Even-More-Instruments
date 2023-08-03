package com.cstav.evenmoreinstruments.client.gui.instrument.noteblockinstrument;

import java.util.Optional;

import com.cstav.genshinstrument.GInstrumentMod;
import com.cstav.genshinstrument.client.gui.screens.instrument.floralzither.FloralZitherScreen;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.notegrid.AbstractGridInstrumentScreen;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(Dist.CLIENT)
public class NoteBlockInstrumentScreen extends AbstractGridInstrumentScreen {
    public static final String INSTRUMENT_ID = "banjo";
    public static final String[] NOTES_LAYOUT = {"F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F"};
    private static final NoteSound[] DEFAULT_NOTE_SOUNDS = new NoteSound[] {
        new NoteSound(NoteBlockInstrument.BASS.getSoundEvent(), Optional.empty())
    };

    public final NoteBlockInstrument instrumentType;
    
    public NoteBlockInstrumentScreen(InteractionHand hand, final NoteBlockInstrument instrumentType) {
        super(hand);

        this.instrumentType = instrumentType;
        // Update the sound to match the note block's
        noteGrid.setNoteSounds(new NoteSound[] {new NoteSound(instrumentType.getSoundEvent(), Optional.empty())});
    }
    @Override
    public boolean isGenshinInstrument() {
        return false;
    }


    @Override
    public int rows() {
        return 8;
    }

    @SuppressWarnings("resource")
    public int getNoteSize() {
        final int guiScale = Minecraft.getInstance().options.guiScale().get();

        return switch (guiScale) {
            case 0 -> 40;
            case 1 -> 35;
            case 2 -> 41;
            case 3 -> 48;
            case 4 -> 41;
            default -> guiScale * 18;
        };
    }


    @Override
    public ResourceLocation getInstrumentId() {
        return new ResourceLocation(GInstrumentMod.MODID, INSTRUMENT_ID);
    }


    @Override
    protected ResourceLocation getSourcePath() {
        return new ResourceLocation(GInstrumentMod.MODID, FloralZitherScreen.INSTRUMENT_ID);
    }
    

    @Override
    public NoteSound[] getSounds() {
        return DEFAULT_NOTE_SOUNDS;
    }

    @Override
    public String[] noteLayout() {
        return NOTES_LAYOUT;
    }

    @Override
    public boolean isSSTI() {
        return true;
    }


    private static final InstrumentThemeLoader THEME_LOADER = initThemeLoader(GInstrumentMod.MODID, FloralZitherScreen.INSTRUMENT_ID);
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }
}
