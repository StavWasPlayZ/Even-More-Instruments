package com.cstav.evenmoreinstruments.networking.packet;

import java.util.Optional;
import java.util.function.Supplier;

import com.cstav.evenmoreinstruments.Main;
import com.cstav.evenmoreinstruments.block.LooperBlock;
import com.cstav.evenmoreinstruments.block.blockentity.LooperBlockEntity;
import com.cstav.evenmoreinstruments.networking.ModPacketHandler;
import com.cstav.evenmoreinstruments.util.LooperUtil;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.networking.IModPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class RecordStatePacket implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_SERVER;

    private final Optional<InteractionHand> usedHand;
    private final boolean recording;

    public RecordStatePacket(boolean recording, Optional<InteractionHand> usedHand) {
        this.recording = recording;
        this.usedHand = usedHand;
    }
    public RecordStatePacket(final FriendlyByteBuf buf) {
        recording = buf.readBoolean();
        usedHand = buf.readOptional((fbb) -> fbb.readEnum(InteractionHand.class));
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(recording);
        buf.writeOptional(usedHand, FriendlyByteBuf::writeEnum);
    }


    @Override
    public void handle(Supplier<Context> arg0) {
        final Context context = arg0.get();

        context.enqueueWork(() -> {
            final ServerPlayer player = context.getSender();

            if (usedHand.isPresent())
                handleItem(player);
            else
                handleBlock(player);
        });

        context.setPacketHandled(true);
    }

    private void handleBlock(final ServerPlayer player) {
        final BlockPos instrumentBlockPos = InstrumentOpenProvider.getBlockPos(player);

        final BlockEntity instrumentBlock = player.getLevel().getBlockEntity(instrumentBlockPos);
        final CompoundTag looperTag = LooperUtil.looperTag(instrumentBlock);

        if (isMaliciousPos(player, looperTag))
            return;

        final LooperBlockEntity lbe = LooperBlockEntity.getLBE(player.getLevel(), instrumentBlock);
        changeRecordingState(player, looperTag, lbe, () -> LooperUtil.remLooperTag(instrumentBlock));

        ModPacketHandler.sendToClient(new SyncModTagPacket(Main.modTag(instrumentBlock), instrumentBlockPos), player);
    }
    private void handleItem(final ServerPlayer player) {
        final ItemStack instrumentItem = player.getItemInHand(usedHand.get());
        final CompoundTag looperTag = LooperUtil.looperTag(instrumentItem);

        if (isMaliciousPos(player, looperTag))
            return;


        final LooperBlockEntity lbe = LooperBlockEntity.getLBE(player.getLevel(), instrumentItem);
        changeRecordingState(player, looperTag, lbe, () -> LooperUtil.remLooperTag(instrumentItem));
    }

    private void changeRecordingState(Player player, CompoundTag looperTag, LooperBlockEntity lbe, Runnable removeLooperTagRunnable) {
        if (lbe.isLocked() && !lbe.isLockedBy(player.getUUID()))
            return;

        if (!recording) {
            lbe.lock();

            player.getLevel().setBlock(lbe.getBlockPos(),
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
        return !player.getLevel().hasChunkAt(looperPos);
    }

}
