package com.cstav.evenmoreinstruments.client.gui.instrument.noteblockinstrument;

import com.cstav.genshinstrument.client.gui.screen.instrument.partial.note.NoteButton;
import com.cstav.genshinstrument.client.midi.InstrumentMidiReceiver;
import org.jetbrains.annotations.Nullable;

public class NoteBlockInstrumentMIDIReceiver extends InstrumentMidiReceiver {
    public NoteBlockInstrumentMIDIReceiver(NoteBlockInstrumentScreen instrument) {
        super(instrument);
    }

    public NoteBlockInstrumentScreen self() {
        return (NoteBlockInstrumentScreen) instrument;
    }

    @Override
    protected int maxMidiNote() {
        return self().rows() * self().columns() + 6; // Starts at F#
    }

    @Override
    protected @Nullable NoteButton handleMidiPress(int note, int key) {
        final NoteBlockInstrumentScreen instrumentScreen = self();

        note -= 6; // Starts at F#
        if (note < 0)
            return null;

        return instrumentScreen.getNoteButtonByMIDINote(note);
    }
}
