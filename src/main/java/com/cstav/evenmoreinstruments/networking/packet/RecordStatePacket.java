package com.cstav.evenmoreinstruments.networking.packet;

import java.util.Optional;
import java.util.function.Supplier;

import com.cstav.evenmoreinstruments.block.LooperBlock;
import com.cstav.evenmoreinstruments.block.blockentity.LooperBlockEntity;
import com.cstav.evenmoreinstruments.util.LooperUtil;
import com.cstav.genshinstrument.networking.IModPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class RecordStatePacket implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_SERVER;

    private final Optional<InteractionHand> usedHand;
    private final Optional<BlockPos> instrumentBlockPos;
    private final boolean recording;

    public RecordStatePacket(boolean recording, Optional<InteractionHand> usedHand, Optional<BlockPos> instrumentBlockPos) {
        this.recording = recording;
        this.usedHand = usedHand;
        this.instrumentBlockPos = instrumentBlockPos;
    }
    public RecordStatePacket(final FriendlyByteBuf buf) {
        recording = buf.readBoolean();
        usedHand = buf.readOptional((fbb) -> fbb.readEnum(InteractionHand.class));
        instrumentBlockPos = buf.readOptional(FriendlyByteBuf::readBlockPos);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(recording);
        buf.writeOptional(usedHand, FriendlyByteBuf::writeEnum);
        buf.writeOptional(instrumentBlockPos, FriendlyByteBuf::writeBlockPos);
    }


    @Override
    public boolean handle(Supplier<Context> arg0) {
        Context ctx = arg0.get();

        ctx.enqueueWork(() -> {
            final Player player = ctx.getSender();

            if (usedHand.isPresent())
                handleItem(player);
            else
                handleBlock(player);
        });

        return true;
    }

    private void handleBlock(final Player player) {
        final BlockEntity instrumentBlock = player.level().getBlockEntity(instrumentBlockPos.get());
        final CompoundTag looperTag = LooperUtil.looperTag(instrumentBlock);

        if (isMaliciousPos(player, looperTag))
            return;

        final LooperBlockEntity lbe = LooperBlockEntity.getLBE(player.level(), instrumentBlock);
        changeRecordingState(player, looperTag, lbe, () -> LooperUtil.remLooperTag(instrumentBlock));
    }
    private void handleItem(final Player player) {
        final ItemStack instrumentItem = player.getItemInHand(usedHand.get());
        final CompoundTag looperTag = LooperUtil.looperTag(instrumentItem);

        if (isMaliciousPos(player, looperTag))
            return;


        final LooperBlockEntity lbe = LooperBlockEntity.getLBE(player.level(), instrumentItem);
        changeRecordingState(player, looperTag, lbe, () -> LooperUtil.remLooperTag(instrumentItem));
    }

    private void changeRecordingState(Player player, CompoundTag looperTag, LooperBlockEntity lbe, Runnable removeLooperTagRunnable) {
        if (!recording) {
            lbe.lock();

            player.level().setBlock(lbe.getBlockPos(),
                lbe.getBlockState().setValue(LooperBlock.PLAYING, true)
            , 3);

            removeLooperTagRunnable.run();
        } else
            LooperUtil.setRecording(looperTag, true);
    }

    // According to Forge
    @SuppressWarnings("deprecation")
    private static boolean isMaliciousPos(final Player player, final CompoundTag looperTag) {
        final BlockPos looperPos = LooperUtil.getLooperPos(looperTag);
        return !player.level().hasChunkAt(looperPos);
    }

}
