package com.cstav.evenmoreinstruments.item.partial.emirecord;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public class BurnedRecordItem extends EMIRecordItem {

    private final String burnedMedia;

    public BurnedRecordItem(final Properties properties, final String recordName) {
        super(properties.stacksTo(1));
        this.burnedMedia = recordName;
    }

    @Override
    public CompoundTag toLooperData(final ItemStack stack) {
        final CompoundTag tag = new CompoundTag();
        tag.putString("burned_media", burnedMedia);
        return tag;
    }
}
