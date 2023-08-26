package com.cstav.evenmoreinstruments.client.gui.instrument.noteblockinstrument;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.AbstractGridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.NoteGridButton;

public class NoteBlockInstrumentNote extends NoteGridButton {

    public NoteBlockInstrumentNote(int row, int column, AbstractGridInstrumentScreen instrumentScreen, int pitch) {
        super(row, column, instrumentScreen, pitch);
    }

    // Layout starts from the bottom in a note block instrument, not the top
    // Hence, perform a column flip
    @Override
    public int getNoteOffset() {
        final AbstractGridInstrumentScreen gridInstrument = (AbstractGridInstrumentScreen)instrumentScreen;
        return row + gridInstrument.noteGrid.getFlippedColumn(column) * gridInstrument.rows();
    }
    
}
