package com.cstav.evenmoreinstruments.item.emirecord;

import com.cstav.evenmoreinstruments.block.blockentity.LooperBlockEntity;
import com.cstav.evenmoreinstruments.item.component.ModDataComponents;
import com.cstav.evenmoreinstruments.util.LooperUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;

public class WritableRecordItem extends EMIRecordItem {
    public WritableRecordItem(Properties properties) {
        super(properties);
    }

    public boolean isBurned(final ItemStack stack) {
        return (stack.has(ModDataComponents.CHANNNEL.get()) &&
            !stack.get(ModDataComponents.CHANNNEL.get()).getUnsafe().getBoolean(WRITABLE_TAG))
            // May also be media burned
            || stack.has(ModDataComponents.BURNED_MEDIA.get());
    }

    @Override
    public boolean isFoil(ItemStack pStack) {
        return isBurned(pStack);
    }

    @Override
    public void onInsert(final ItemStack stack, final LooperBlockEntity lbe) {
        if (stack.has(ModDataComponents.BURNED_MEDIA.get()))
            return;

        final CustomData channelRaw = stack.get(ModDataComponents.CHANNNEL.get());
        CompoundTag channel;

        if (channelRaw == null) {
            channel = new CompoundTag();
            channel.putBoolean(WRITABLE_TAG, true);
        } else {
            channel = channelRaw.getUnsafe();

            if (!channel.getBoolean(WRITABLE_TAG) && !channel.contains(NOTES_TAG, Tag.TAG_LIST)) {
                // Record is empty; check if is legacy looper
                channel = LooperUtil.migrateLegacyLooper(lbe).orElse(channel);
            }
        }

        stack.set(ModDataComponents.CHANNNEL.get(), CustomData.of(channel));
    }

    @Override
    public Component getName(ItemStack pStack) {
        return Component.translatable(String.format(
            "item.evenmoreinstruments.%s_record",
            isBurned(pStack) ? "burned" : "writable"
        ));
    }

}
