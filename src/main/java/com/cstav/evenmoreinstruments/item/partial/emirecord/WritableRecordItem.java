package com.cstav.evenmoreinstruments.item.partial.emirecord;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class WritableRecordItem extends EMIRecordItem {
    public WritableRecordItem(Properties properties) {
        super(properties);
    }

    public static boolean isBurned(final ItemStack stack) {
        return stack.getOrCreateTag().contains("channel", Tag.TAG_COMPOUND);
    }
    public static void burn(final ItemStack stack, final CompoundTag data) {
        stack.getOrCreateTag().put("channel", data);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return isBurned(pStack);
    }

    @Override
    public CompoundTag toLooperData(final ItemStack stack) {
        final CompoundTag tag = new CompoundTag();

        if (isBurned(stack))
            tag.put("channel", stack.getTagElement("channel"));
        else {
            final CompoundTag channel = new CompoundTag();
            channel.putBoolean("writable", true);
            tag.put("channel", channel);
        }

        return tag;
    }

    @Override
    public Component getName(ItemStack pStack) {
        return Component.translatable(String.format(
            "item.evenmoreinstruments.%s_record",
            isBurned(pStack) ? "burned" : "writable"
        ));
    }

    @Override
    public int getMaxStackSize(ItemStack stack) {
        return isBurned(stack) ? super.getMaxStackSize(stack) : 64;
    }
}
