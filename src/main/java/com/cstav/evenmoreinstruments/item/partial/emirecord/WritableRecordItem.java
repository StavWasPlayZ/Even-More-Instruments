package com.cstav.evenmoreinstruments.item.partial.emirecord;

import com.cstav.evenmoreinstruments.block.blockentity.LooperBlockEntity;
import com.cstav.evenmoreinstruments.util.CommonUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class WritableRecordItem extends EMIRecordItem {
    public WritableRecordItem(Properties properties) {
        super(properties);
    }

    public static boolean isBurned(final ItemStack stack) {
        return stack.getOrCreateTag().contains("channel", Tag.TAG_COMPOUND) &&
               !stack.getTagElement("channel").getBoolean("writable");
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return isBurned(pStack);
    }

    @Override
    public void onInsert(final ItemStack stack, final LooperBlockEntity lbe) {
        final CompoundTag channel = CommonUtil.getOrCreateElementTag(stack.getOrCreateTag(), "channel");
        if (!channel.getBoolean("writable") && !channel.contains("notes", Tag.TAG_LIST))
            channel.putBoolean("writable", true);
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
