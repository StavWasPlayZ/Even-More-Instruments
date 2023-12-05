package com.cstav.evenmoreinstruments.util;

import javax.annotation.Nullable;

import com.cstav.evenmoreinstruments.Main;
import com.cstav.evenmoreinstruments.block.IDoubleBlock;
import com.cstav.evenmoreinstruments.block.blockentity.LooperBlockEntity;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class LooperUtil {
    public static final String LOOPER_TAG = "looper",
        POS_TAG = "pos", RECORDING_TAG = "recording";
    

    // Handle instrument's looper tag
    public static boolean hasLooperTag(final ItemStack instrument) {
        return hasLooperTag(Main.modTag(instrument));
    }
    public static boolean hasLooperTag(final BlockEntity instrument) {
        return hasLooperTag(Main.modTag(instrument));
    }
    private static boolean hasLooperTag(final CompoundTag modTag) {
        return modTag.contains(LOOPER_TAG, CompoundTag.TAG_COMPOUND) && !modTag.getCompound(LOOPER_TAG).isEmpty();
    }

    public static void remLooperTag(final ItemStack instrument) {
        Main.modTag(instrument).remove(LOOPER_TAG);
    }
    public static void remLooperTag(final BlockEntity instrument) {
        Main.modTag(instrument).remove(LOOPER_TAG);
    }

    public static void createLooperTag(final ItemStack instrument, final BlockPos looperPos) {
        Main.modTag(instrument).put(LOOPER_TAG, new CompoundTag());
        constructLooperTag(looperTag(instrument), looperPos);
    }
    public static void createLooperTag(final BlockEntity instrument, final BlockPos looperPos) {
        Main.modTag(instrument).put(LOOPER_TAG, new CompoundTag());
        constructLooperTag(looperTag(instrument), looperPos);
    }
    private static void constructLooperTag(final CompoundTag looperTag, final BlockPos looperPos) {
        looperTag.put(POS_TAG, NbtUtils.writeBlockPos(looperPos));
        setRecording(looperTag, false);
    }

    public static CompoundTag looperTag(final ItemStack instrument) {
        return looperTag(Main.modTag(instrument));
    }
    public static CompoundTag looperTag(final BlockEntity instrument) {
        return looperTag(Main.modTag(instrument));
    }
    public static CompoundTag looperTag(final CompoundTag parentTag) {
        return parentTag.contains(LOOPER_TAG, CompoundTag.TAG_COMPOUND)
            ? parentTag.getCompound(LOOPER_TAG)
            : CommonUtil.TAG_EMPTY;
    }


    public static CompoundTag getLooperTagFromEvent(final InstrumentPlayedEvent.ByPlayer event) {
        return (!event.isBlockInstrument())
            ? looperTag(event.itemInstrument.get())
            : looperTag(event.level.getBlockEntity(event.playPos));
    }

    @Nullable
    public static LooperBlockEntity getFromEvent(final InstrumentPlayedEvent.ByPlayer event) {
        final Level level = event.level;

        if (event.isItemInstrument())
            return getFromInstrument(level, event.itemInstrument.get());
        else if (event.isBlockInstrument())
            return getFromInstrument(level, event.level.getBlockEntity(event.playPos));

        return null;
    }

    @Nullable
    public static LooperBlockEntity getFromInstrument(final Level level, final ItemStack instrument) {
        return getFromInstrument(level, LooperUtil.looperTag(instrument), () -> LooperUtil.remLooperTag(instrument));
    }
    @Nullable
    public static LooperBlockEntity getFromInstrument(final Level level, final BlockEntity instrument) {
        return getFromInstrument(level, LooperUtil.looperTag(instrument), () -> {
            LooperUtil.remLooperTag(instrument);

            final BlockPos pos = instrument.getBlockPos();
            final BlockState state = level.getBlockState(pos);
            if (state.getBlock() instanceof IDoubleBlock doubleBlock)
                LooperUtil.remLooperTag(level.getBlockEntity(doubleBlock.getOtherBlock(state, pos, level)));
        });
    }
    /**
     * Attempts to get the looper pointed out by {@code looperData}. Removes its reference if not found.
     * @return The Looper's block entity as pointed in the {@code instrument}'s data.
     * Null if not found
     */
    @Nullable
    private static LooperBlockEntity getFromInstrument(Level level, CompoundTag looperData, Runnable onInvalid) {
        if (looperData.isEmpty())
            return null;

        final LooperBlockEntity looperBE = getFromPos(level, LooperUtil.getLooperPos(looperData));

        if (looperBE == null)
            onInvalid.run();

        return looperBE;
    }

    public static LooperBlockEntity getFromPos(final Level level, final BlockPos pos) {
        return (level.getBlockEntity(pos) instanceof LooperBlockEntity lbe) ? lbe : null;
    }


    public static boolean performPair(LooperBlockEntity lbe, Runnable pairPerformer, Player pairingPlayer) {
        if (!performChannelCheck(lbe, pairingPlayer))
            return false;

        pairPerformer.run();

        pairingPlayer.displayClientMessage(
            new TranslatableComponent("evenmoreinstruments.looper.success_pair").withStyle(ChatFormatting.GREEN)
        , true);

        return true;
    }
    public static boolean performChannelCheck(final LooperBlockEntity lbe, final Player pairingPlayer) {
        if (!lbe.hasFootage())
            return true;

        pairingPlayer.displayClientMessage(
            new TranslatableComponent("evenmoreinstruments.looper.pair_conflict").withStyle(ChatFormatting.GREEN)
        , true);

        return false;
    }


    /**
     * @param pos The position of the block to check for {@code item}
     * @return Whether {@code item} is pointing to a looper, and if its position is equal to {@code pos}'s
     */
    public static boolean isSameBlock(final CompoundTag looperTag, final BlockPos pos) {
        try {
            return getLooperPos(looperTag).equals(pos);
        } catch (NullPointerException e) {
            return false;
        }
    }

    @Nullable
    public static BlockPos getLooperPos(final CompoundTag looperTag) {
        final CompoundTag looperPosTag = looperTag.getCompound(POS_TAG);
        return (looperPosTag == null) ? null : NbtUtils.readBlockPos(looperPosTag);
    }
    
    //TODO Move recording data to player
    public static void setRecording(final CompoundTag looperTag, final boolean recording) {
        looperTag.putBoolean(RECORDING_TAG, recording);
    }
    public static boolean isRecording(final CompoundTag looperTag) {
        return looperTag.getBoolean(RECORDING_TAG);
    }
    
}
