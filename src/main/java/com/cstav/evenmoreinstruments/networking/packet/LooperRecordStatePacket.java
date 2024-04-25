package com.cstav.evenmoreinstruments.networking.packet;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.block.blockentity.LooperBlockEntity;
import com.cstav.evenmoreinstruments.networking.EMIPacketHandler;
import com.cstav.evenmoreinstruments.util.LooperUtil;
import com.cstav.evenmoreinstruments.util.ServerUtil;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.networking.IModPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

import java.util.Optional;

public class LooperRecordStatePacket implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_SERVER;

    private final Optional<InteractionHand> usedHand;
    private final boolean recording;

    public LooperRecordStatePacket(boolean recording, InteractionHand usedHand) {
        this.recording = recording;
        this.usedHand = Optional.ofNullable(usedHand);
    }

    public LooperRecordStatePacket(final FriendlyByteBuf buf) {
        recording = buf.readBoolean();
        usedHand = buf.readOptional((fbb) -> fbb.readEnum(InteractionHand.class));
    }

    @Override
    public void write(final FriendlyByteBuf buf) {
        buf.writeBoolean(recording);
        buf.writeOptional(usedHand, FriendlyByteBuf::writeEnum);
    }


    //TODO extract all the below to its own method in LooperUtil

    @Override
    public void handle(final Context context) {
        final ServerPlayer player = context.getSender();

        if (usedHand.isPresent())
            handleItem(player);
        else
            handleBlock(player);
    }

    private void handleBlock(final ServerPlayer player) {
        final BlockPos instrumentBlockPos = InstrumentOpenProvider.getBlockPos(player);

        final BlockEntity instrumentBlock = player.getLevel().getBlockEntity(instrumentBlockPos);
        final CompoundTag looperTag = LooperUtil.looperTag(instrumentBlock);

        if (ServerUtil.isMaliciousPos(player, looperTag))
            return;

        final LooperBlockEntity lbe = LooperUtil.getFromInstrument(player.getLevel(), instrumentBlock);
        if (lbe == null) {
            EMIPacketHandler.sendToClient(new LooperRemovedPacket(), player);
            return;
        }

        changeRecordingState(player, lbe, () -> LooperUtil.remLooperTag(instrumentBlock));
        EMIPacketHandler.sendToClient(new SyncModTagPacket(EMIMain.modTag(instrumentBlock), instrumentBlockPos), player);
    }

    private void handleItem(final ServerPlayer player) {
        final ItemStack instrumentItem = player.getItemInHand(usedHand.get());
        final CompoundTag looperTag = LooperUtil.looperTag(instrumentItem);

        if (ServerUtil.isMaliciousPos(player, looperTag))
            return;


        final LooperBlockEntity lbe = LooperUtil.getFromInstrument(player.getLevel(), instrumentItem);
        if (lbe == null) {
            EMIPacketHandler.sendToClient(new LooperRemovedPacket(), player);
            return;
        }

        changeRecordingState(player, lbe, () -> LooperUtil.remLooperTag(instrumentItem));
    }

    private void changeRecordingState(ServerPlayer player, LooperBlockEntity lbe, Runnable looperTagRemover) {
        if (lbe.isLocked() && !lbe.isLockedBy(player.getUUID()))
            return;

        if (!recording) {
            lbe.lock();

            player.getLevel().setBlockAndUpdate(
                lbe.getBlockPos(),
                lbe.setPlaying(true, lbe.getBlockState())
            );

            looperTagRemover.run();

            LooperUtil.setNotRecording(player);
        } else
            LooperUtil.setRecording(player, lbe.getBlockPos());
    }

}
