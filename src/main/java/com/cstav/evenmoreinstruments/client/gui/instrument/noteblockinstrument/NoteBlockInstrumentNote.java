package com.cstav.evenmoreinstruments.client.gui.instrument.noteblockinstrument;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.GridInstrumentScreen;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.notegrid.NoteGridButton;

public class NoteBlockInstrumentNote extends NoteGridButton {

    public NoteBlockInstrumentNote(int row, int column, GridInstrumentScreen instrumentScreen, int pitch) {
        super(row, column, instrumentScreen, pitch);
    }

    // Layout starts from the bottom in a note block instrument, not the top
    // Hence, perform a column flip
    @Override
    public int getNoteOffset() {
        final GridInstrumentScreen gridInstrument = (GridInstrumentScreen)instrumentScreen;
        return row + gridInstrument.noteGrid.getFlippedColumn(column) * gridInstrument.rows();
    }
    
}
