package com.cstav.genshinstrumentp.util;

import javax.annotation.Nullable;

import com.cstav.genshinstrumentp.Main;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.world.item.ItemStack;

public class LooperUtil {
    public static final String LOOPER_TAG = "looper",
        POS_TAG = "pos", RECORDING_TAG = "recording", CHANNEL_TAG = "channel";
    

    public static boolean hasLooperTag(final ItemStack instrument) {
        return Main.modTag(instrument).contains(LOOPER_TAG, CompoundTag.TAG_COMPOUND);
    }
    public static void remLooperTag(final ItemStack instrument) {
        Main.modTag(instrument).remove(LOOPER_TAG);
    }
    public static void createLooperTag(final ItemStack instrument, final BlockPos looperPos) {
        Main.modTag(instrument).put(LOOPER_TAG, new CompoundTag());

        looperTag(instrument).put(POS_TAG, NbtUtils.writeBlockPos(looperPos));
        setRecording(instrument, false);
        setChannel(instrument, 0);
    }

    public static CompoundTag looperTag(final ItemStack instrument) {
        final CompoundTag tag = Main.modTag(instrument);
        return tag.contains(LOOPER_TAG, CompoundTag.TAG_COMPOUND)
            ? tag.getCompound(LOOPER_TAG)
            : CommonUtil.TAG_EMPTY;
    }



    /**
     * @param instrument The item to check its NBT against
     * @param pos The position of the block to check for {@code item}
     * @return Whether {@code item} is pointing to a looper, and if its position is equal to {@code pos}'s
     */
    public static boolean isSameBlock(final ItemStack instrument, final BlockPos pos) {
        try {
            return getLooperPos(instrument).equals(pos);
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Nullable
    public static BlockPos getLooperPos(final ItemStack instrument) {
        final CompoundTag looperPosTag = looperTag(instrument).getCompound(POS_TAG);
        return (looperPosTag == null) ? null : NbtUtils.readBlockPos(looperPosTag);
    }
    
    public static void setRecording(final ItemStack instrument, final boolean recording) {
        looperTag(instrument).putBoolean(RECORDING_TAG, recording);
    }
    public static void setChannel(final ItemStack instrument, final int channel) {
        looperTag(instrument).putInt(CHANNEL_TAG, channel);
    }
    public static boolean isRecording(final ItemStack instrument) {
        return looperTag(instrument).getBoolean(RECORDING_TAG);
    }
    public static int getChannel(final ItemStack instrument) {
        return looperTag(instrument).getInt(CHANNEL_TAG);
    }
    
}
