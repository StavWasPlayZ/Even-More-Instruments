package com.cstav.evenmoreinstruments.item.partial.emirecord;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class WritableRecordItem extends EMIRecordItem {
    public WritableRecordItem(Properties properties) {
        super(properties);
    }

    public static boolean isBurned(final ItemStack stack) {
        return stack.getOrCreateTag().contains("data", Tag.TAG_COMPOUND);
    }
    public static void burn(final ItemStack stack, final CompoundTag data) {
        stack.getOrCreateTag().put("data", data);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return isBurned(pStack);
    }

    @Override
    public @Nullable CompoundTag getRecording(ItemStack stack) {
        return isBurned(stack) ? stack.getTagElement("data") : null;
    }

    @Override
    public Component getName(ItemStack pStack) {
        return Component.translatable(String.format(
            "item.evenmoreinstruments.%s_record",
            isBurned(pStack) ? "burned" : "writable"
        ));
    }
}
