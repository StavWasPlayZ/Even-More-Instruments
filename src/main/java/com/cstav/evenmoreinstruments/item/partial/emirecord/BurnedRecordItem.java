package com.cstav.evenmoreinstruments.item.partial.emirecord;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.function.Function;

public class BurnedRecordItem extends EMIRecordItem {

    private final Function<ItemStack, CompoundTag> dataSupplier;

    public BurnedRecordItem(final Properties properties, final Function<ItemStack, CompoundTag> dataSupplier) {
        super(properties.stacksTo(1));
        this.dataSupplier = dataSupplier;
    }

    @Override
    public @Nullable CompoundTag getRecording(ItemStack stack) {
        return dataSupplier.apply(stack);
    }
}
