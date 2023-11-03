package com.cstav.evenmoreinstruments.client.gui.instrument.noteblockinstrument;

import com.cstav.evenmoreinstruments.Main;
import com.cstav.evenmoreinstruments.item.NoteBlockInstrumentItem;
import com.cstav.evenmoreinstruments.sound.ModSounds;
import com.cstav.genshinstrument.client.gui.screen.instrument.floralzither.FloralZitherScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentThemeLoader;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.AbstractGridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.NoteGridButton;
import com.cstav.genshinstrument.sound.NoteSound;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@OnlyIn(Dist.CLIENT)
//NOTE: There to load it on startup
@EventBusSubscriber(Dist.CLIENT)
public class NoteBlockInstrumentScreen extends AbstractGridInstrumentScreen {
    public static final String[] NOTES_LAYOUT = {"F#", "G", "G#", "A", "A#", "B", "C", "C#", "D", "D#", "E", "F"};

    public final NoteBlockInstrument instrumentType;
    public final ResourceLocation instrumentId;
    
    public NoteBlockInstrumentScreen(InteractionHand hand, final NoteBlockInstrument instrumentType) {
        super(hand);

        this.instrumentType = instrumentType;
        instrumentId = new ResourceLocation(Main.MODID, NoteBlockInstrumentItem.getId(instrumentType));

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
    public ResourceLocation getSourcePath() {
        return FloralZitherScreen.INSTRUMENT_ID;
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


    private static final InstrumentThemeLoader THEME_LOADER =
        InstrumentThemeLoader.fromOther(FloralZitherScreen.INSTRUMENT_ID, new ResourceLocation(Main.MODID, "note_block_instrument"));
        
    @Override
    public InstrumentThemeLoader getThemeLoader() {
        return THEME_LOADER;
    }
}
