package com.cstav.evenmoreinstruments.util;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.block.blockentity.LooperBlockEntity;
import com.cstav.evenmoreinstruments.networking.EMIPacketHandler;
import com.cstav.evenmoreinstruments.networking.packet.LooperUnplayablePacket;
import com.cstav.evenmoreinstruments.networking.packet.SyncModTagPacket;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Optional;

public class LooperRecordStateUtil {

    /**
     * Handles as item if {@code hand} is present,
     * as block otherwise
     */
    public static void handle(ServerPlayer player, Optional<InteractionHand> hand, boolean recording) {
        if (hand.isPresent()) {
            LooperRecordStateUtil.handleItem(player, hand.get(), recording);
        } else {
            LooperRecordStateUtil.handleBlock(player, recording);
        }
    }

    public static void handleBlock(ServerPlayer player, boolean recording) {
        final BlockPos instrumentBlockPos = InstrumentOpenProvider.getBlockPos(player);

        final BlockEntity instrumentBlock = player.level().getBlockEntity(instrumentBlockPos);
        final CompoundTag looperTag = LooperUtil.looperTag(instrumentBlock);

        if (looperTag.isEmpty())
            return;
        if (ServerUtil.isMaliciousPos(player, looperTag))
            return;

        final LooperBlockEntity lbe = LooperUtil.getFromBlockInstrument(player.level(), instrumentBlock);
        if (lbe == null) {
            notifyLooperUnplayable(player);
            return;
        }

        changeRecordingState(player, lbe, () -> LooperUtil.remLooperTag(instrumentBlock), recording);
        EMIPacketHandler.sendToClient(new SyncModTagPacket(EMIMain.modTag(instrumentBlock), instrumentBlockPos), player);
    }

    public static void handleItem(ServerPlayer player, InteractionHand hand, boolean recording) {
        final ItemStack instrumentItem = player.getItemInHand(hand);
        final CompoundTag looperTag = LooperUtil.looperTag(instrumentItem);

        if (looperTag.isEmpty())
            return;
        if (ServerUtil.isMaliciousPos(player, looperTag))
            return;


        final LooperBlockEntity lbe = LooperUtil.getFromItemInstrument(player.level(), instrumentItem);
        if (lbe == null) {
            notifyLooperUnplayable(player);
            return;
        }

        changeRecordingState(player, lbe, () -> LooperUtil.remLooperTag(instrumentItem), recording);
    }

    public static void changeRecordingState(ServerPlayer player, LooperBlockEntity lbe,
                                            Runnable looperTagRemover,
                                            boolean recording) {

        if (recording) {
            if (lbe.isLocked() && !lbe.isLockedBy(player)) {
                notifyLooperUnplayable(player);
                return;
            }

            LooperUtil.setRecording(player, lbe.getBlockPos());
        } else {
            if (!lbe.isLockedBy(player))
                return;

            lbe.lock();

            player.level().setBlockAndUpdate(
                lbe.getBlockPos(),
                lbe.setPlaying(true, lbe.getBlockState())
            );

            looperTagRemover.run();

            LooperUtil.setNotRecording(player);
        }
    }

    private static void notifyLooperUnplayable(final ServerPlayer player) {
        EMIPacketHandler.sendToClient(new LooperUnplayablePacket(), player);
    }

}
