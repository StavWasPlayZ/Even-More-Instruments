package com.cstav.evenmoreinstruments.item.emirecord;

import com.cstav.evenmoreinstruments.block.blockentity.LooperBlockEntity;
import com.cstav.evenmoreinstruments.util.CommonUtil;
import com.cstav.evenmoreinstruments.util.LooperUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;

public class WritableRecordItem extends EMIRecordItem {
    public WritableRecordItem(Properties properties) {
        super(properties);
    }

    public boolean isBurned(final ItemStack stack) {
        return stack.getOrCreateTag().contains(CHANNEL_TAG, Tag.TAG_COMPOUND) &&
               !stack.getTagElement(CHANNEL_TAG).getBoolean(WRITABLE_TAG);
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return isBurned(pStack);
    }

    @Override
    public void onInsert(final ItemStack stack, final LooperBlockEntity lbe) {
        final CompoundTag channel = CommonUtil.getOrCreateElementTag(stack.getOrCreateTag(), CHANNEL_TAG);

        if (!channel.getBoolean(WRITABLE_TAG) && !channel.contains(NOTES_TAG, Tag.TAG_LIST)) {
            // Record is empty; check if is legacy looper
            LooperUtil.migrateLegacyLooper(lbe).ifPresentOrElse(
                (recordData) -> stack.getTag().put(CHANNEL_TAG, recordData),
                // 100% empty
                () -> channel.putBoolean(WRITABLE_TAG, true)
            );
        }
    }

    @Override
    public Component getName(ItemStack pStack) {
        return new TranslatableComponent(String.format(
            "item.evenmoreinstruments.%s_record",
            isBurned(pStack) ? "burned" : "writable"
        ));
    }

    @Override
    public int getItemStackLimit(ItemStack stack) {
        return isBurned(stack) ? super.getItemStackLimit(stack) : 16;
    }
}
