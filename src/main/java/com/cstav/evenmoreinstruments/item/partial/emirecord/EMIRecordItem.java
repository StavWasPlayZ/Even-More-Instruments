package com.cstav.evenmoreinstruments.item.partial.emirecord;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;

public abstract class EMIRecordItem extends Item {

    public EMIRecordItem(final Properties properties) {
        super(properties.stacksTo(1));
    }

    public abstract @Nullable CompoundTag getRecording(ItemStack stack);
}
