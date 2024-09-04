package com.cstav.evenmoreinstruments.util;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.block.blockentity.LooperBlockEntity;
import com.cstav.evenmoreinstruments.block.partial.IDoubleBlock;
import com.cstav.evenmoreinstruments.capability.recording.RecordingCapabilityProvider;
import com.cstav.evenmoreinstruments.item.component.ModDataComponents;
import com.cstav.evenmoreinstruments.item.emirecord.EMIRecordItem;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Optional;

import static java.util.Map.entry;

public class LooperUtil {
    public static final String LOOPER_TAG = "looper", POS_TAG = "pos";
    

    // Handle instrument's looper tag
    public static boolean hasLooperTag(final ItemStack instrument) {
        return instrument.has(ModDataComponents.LOOPER_TAG.get());
    }
    public static boolean hasLooperTag(final BlockEntity instrument) {
        return hasLooperTag(EMIMain.modTag(instrument));
    }
    private static boolean hasLooperTag(final CompoundTag modTag) {
        return modTag.contains(LOOPER_TAG, CompoundTag.TAG_COMPOUND) && !modTag.getCompound(LOOPER_TAG).isEmpty();
    }

    public static void remLooperTag(final ItemStack instrument) {
        instrument.remove(ModDataComponents.LOOPER_TAG.get());
    }
    public static void remLooperTag(final BlockEntity instrument) {
        EMIMain.modTag(instrument).remove(LOOPER_TAG);
    }

    public static void createLooperTag(final ItemStack instrument, final BlockPos looperPos) {
        instrument.set(ModDataComponents.LOOPER_TAG.get(), CustomData.of(new CompoundTag()));
        constructLooperTag(looperTag(instrument), looperPos);
    }
    public static void createLooperTag(final BlockEntity instrument, final BlockPos looperPos) {
        EMIMain.modTag(instrument).put(LOOPER_TAG, new CompoundTag());
        constructLooperTag(looperTag(instrument), looperPos);
    }
    private static void constructLooperTag(final CompoundTag looperTag, final BlockPos looperPos) {
        looperTag.put(POS_TAG, NbtUtils.writeBlockPos(looperPos));
    }

    public static CompoundTag looperTag(final ItemStack instrument) {
        return instrument.has(ModDataComponents.LOOPER_TAG.get())
            ? instrument.get(ModDataComponents.LOOPER_TAG.get()).getUnsafe()
            : new CompoundTag();

    }
    public static CompoundTag looperTag(final BlockEntity instrument) {
        return looperTag(EMIMain.modTag(instrument));
    }
    public static CompoundTag looperTag(final CompoundTag parentTag) {
        return parentTag.contains(LOOPER_TAG, CompoundTag.TAG_COMPOUND)
            ? parentTag.getCompound(LOOPER_TAG)
            : new CompoundTag();
    }


    public static CompoundTag getLooperTagFromEvent(final InstrumentPlayedEvent<?> event) {
        if (!event.isByPlayer())
            return new CompoundTag();

        final InstrumentPlayedEvent<?>.EntityInfo entityInfo = event.entityInfo().get();
        final Player player = (Player) entityInfo.entity;

        return (!entityInfo.isBlockInstrument())
            ? looperTag(player.getItemInHand(entityInfo.hand.get()))
            : looperTag(event.level().getBlockEntity(event.soundMeta().pos()));
    }

    @Nullable
    public static LooperBlockEntity getFromEvent(final InstrumentPlayedEvent<?> event) {
        if (!event.isByPlayer())
            return null;

        final InstrumentPlayedEvent<?>.EntityInfo entityInfo = event.entityInfo().get();
        final Player player = (Player) entityInfo.entity;
        final Level level = event.level();

        if (entityInfo.isItemInstrument())
            return getFromItemInstrument(level, player.getItemInHand(entityInfo.hand.get()));
        else if (entityInfo.isBlockInstrument())
            return getFromBlockInstrument(level, level.getBlockEntity(event.soundMeta().pos()));

        return null;
    }

    @Nullable
    public static LooperBlockEntity getFromItemInstrument(final Level level, final ItemStack instrument) {
        return getFromInstrument(level, LooperUtil.looperTag(instrument), () -> LooperUtil.remLooperTag(instrument));
    }
    @Nullable
    public static LooperBlockEntity getFromBlockInstrument(final Level level, final BlockEntity instrument) {
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
        if (!validateFootagePresence(lbe, pairingPlayer))
            return false;

        pairPerformer.run();

        pairingPlayer.displayClientMessage(
            Component.translatable("item.evenmoreinstruments.looper_adapter.instrument.success_pair").withStyle(ChatFormatting.GREEN)
        , true);

        return true;
    }
    public static boolean validateFootagePresence(final LooperBlockEntity lbe, final Player pairingPlayer) {
        if (!lbe.hasFootage())
            return true;

        pairingPlayer.displayClientMessage(
            Component.translatable("evenmoreinstruments.looper.pair_conflict").withStyle(ChatFormatting.GREEN)
        , true);

        return false;
    }


    /**
     * @param pos The position of the block to check for
     * @return Whether {@code looperTag} contains any position, and if it's equal to {@code pos}
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
        return NbtUtils.readBlockPos(looperTag, POS_TAG).orElse(null);
    }

    public static void setRecording(final Player player, final BlockPos looperPos) {
        RecordingCapabilityProvider.setRecording(player, looperPos);
    }
    public static void setNotRecording(final Player player) {
        RecordingCapabilityProvider.setNotRecording(player);
    }
    public static boolean isRecording(final Player player) {
        return RecordingCapabilityProvider.isRecording(player);
    }


    //#region Legacy Looper Migration

    /**
     * Maps the old keys to the new ones
     */
    private static final Map<String, String> LOOPER_LEGACY_MAPPER = Map.ofEntries(
        // Record
        entry("instrumentId", EMIRecordItem.INSTRUMENT_ID_TAG),
        entry("notes", EMIRecordItem.NOTES_TAG),
        entry("volume", EMIRecordItem.VOLUME_TAG),
        entry("pitch", EMIRecordItem.PITCH_TAG),
        entry("soundIndex", EMIRecordItem.SOUND_INDEX_TAG),
        entry("soundType", EMIRecordItem.SOUND_TYPE_TAG),
        entry("timestamp", EMIRecordItem.TIMESTAMP_TAG),
        // Looper
        entry("recording", LooperBlockEntity.RECORDING_TAG),
        entry("ticks", LooperBlockEntity.TICKS_TAG),
        // Looper -> Record
        entry("channel", EMIRecordItem.CHANNEL_TAG),
        entry("repeatTick", EMIRecordItem.REPEAT_TICK_TAG)
    );

    /**
     * Migrates all keys of a looper, if it is a legacy one.
     * @return A new record channel compound data containing the old looper's data.
     * To be burned into a record.
     */
    public static Optional<CompoundTag> migrateLegacyLooper(final LooperBlockEntity lbe) {
        final CompoundTag lbed = lbe.getPersistentData();

        if (!lbed.contains("channel", Tag.TAG_COMPOUND))
            return Optional.empty();

        final CompoundTag looperData = CommonUtil.deepConvertCompound(lbed, LOOPER_LEGACY_MAPPER);
        final CompoundTag channel = looperData.getCompound(EMIRecordItem.CHANNEL_TAG);
        // Writable is a new tag. This record will be burned:
        looperData.putBoolean(EMIRecordItem.WRITABLE_TAG, false);
        // RepeatTick moved from looper to channel
        CommonUtil.moveTags(looperData, channel, EMIRecordItem.REPEAT_TICK_TAG);

        // Remove all old looper tags
        lbed.getAllKeys()
            .stream().toList() // Convert to list as to not mess with the internal map
            .forEach(lbed::remove);

        // Add everything back except for the channel; which belongs to a record
        looperData.getAllKeys().stream()
            .filter((key) -> !key.equals(EMIRecordItem.CHANNEL_TAG))
            .forEach((key) -> lbed.put(key, looperData.get(key)));

        return Optional.of(channel);
    }

    //#endregion
    
}
