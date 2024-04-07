package com.cstav.evenmoreinstruments.item.partial.emirecord;

import com.cstav.evenmoreinstruments.block.blockentity.LooperBlockEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public abstract class EMIRecordItem extends Item {
    public static final String
        SOUND_INDEX_TAG = "SoundIndex",
        SOUND_TYPE_TAG = "SoundType",
        PITCH_TAG = "Pitch",
        VOLUME_TAG = "Volume",
        TIMESTAMP_TAG = "Timestamp",
    
        INSTRUMENT_ID_TAG = "InstrumentId",
        WRITABLE_TAG = "Writable"
    ;
    
    
    public EMIRecordItem(final Properties properties) {
        super(properties.stacksTo(1));
    }

    public abstract void onInsert(final ItemStack stack, final LooperBlockEntity lbe);
}
