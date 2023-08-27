package com.cstav.evenmoreinstruments.item;

import com.cstav.evenmoreinstruments.networking.ModPacketHandler;
import com.cstav.evenmoreinstruments.networking.packet.OpenNoteBlockInstrumentPacket;
import com.cstav.genshinstrument.item.InstrumentItem;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public class NoteBlockInstrumentItem extends InstrumentItem {
    public static final String NOTEBLOCK_INSTRUMENT_SUFFIX = "_note_block_instrument";

    public final NoteBlockInstrument instrument;
    public NoteBlockInstrumentItem(NoteBlockInstrument instrument) {
        super((player, hand) -> ModPacketHandler.sendToClient(
            new OpenNoteBlockInstrumentPacket(instrument, hand), player
        ));

        this.instrument = instrument;
    }
    

    @Override
    public Component getName(ItemStack pStack) {
        return Component.translatable("item.evenmoreinstruments.note_block_instrument", getInstrumentName());
    }

    private String getInstrumentName() {
        final String[] words = instrument.getSerializedName().split("_");

        final StringBuilder result = new StringBuilder();
        for (final String word : words) {
            result.append(Character.toUpperCase(word.charAt(0)));
            result.append(word.substring(1));
            result.append(" ");
        }

        return result.toString().trim();
    }

    public static String getId(final NoteBlockInstrument instrument) {
        return instrument.getSerializedName() + NOTEBLOCK_INSTRUMENT_SUFFIX;
    }

}
