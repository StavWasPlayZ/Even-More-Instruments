package com.cstav.evenmoreinstruments.client.gui.instrument.noteblockinstrument;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.item.NoteBlockInstrumentItem;
import com.cstav.evenmoreinstruments.sound.ModSounds;
import com.cstav.genshinstrument.client.gui.screen.instrument.floralzither.FloralZitherScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.grid.GridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.grid.NoteGridButton;
import com.cstav.genshinstrument.client.midi.InstrumentMidiReceiver;
import com.cstav.genshinstrument.sound.NoteSound;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class NoteBlockInstrumentScreen extends GridInstrumentScreen {
    public static final String[] NOTES_LAYOUT = {"F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F"};

    public final NoteBlockInstrument instrumentType;
    public final ResourceLocation instrumentId;
    
    public NoteBlockInstrumentScreen(final NoteBlockInstrument instrumentType) {
        this.instrumentType = instrumentType;
        instrumentId = EMIMain.loc(NoteBlockInstrumentItem.getId(instrumentType));

        // Update the sound to match the note block's
        noteGrid.setNoteSounds(ModSounds.getNoteblockSounds(instrumentType));
    }

    @Override
    public boolean isGenshinInstrument() {
        return false;
    }


    @Override
    public int rows() {
        return 8;
    }

    public int getNoteSize() {
        return (int)(super.getNoteSize() * .85f);
    }
    @Override
    public NoteGridButton createNote(int row, int column, int pitch) {
        return new NoteBlockInstrumentNote(row, column, this, pitch);
    }


    @Override
    public ResourceLocation getInstrumentId() {
        return instrumentId;
    }
    

    @Override
    public NoteSound[] getInitSounds() {
        return ModSounds.getNoteblockSounds(NoteBlockInstrument.HARP);
    }

    @Override
    public String[] noteLayout() {
        return NOTES_LAYOUT;
    }

    @Override
    public boolean isSSTI() {
        return true;
    }


    @Override
    public InstrumentMidiReceiver initMidiReceiver() {
        return new NoteBlockInstrumentMIDIReceiver(this);
    }

    public static final InstrumentThemeLoader THEME_LOADER = InstrumentThemeLoader.fromOther(
        FloralZitherScreen.THEME_LOADER,
        EMIMain.loc("note_block_instrument")
    );
        
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }
}
