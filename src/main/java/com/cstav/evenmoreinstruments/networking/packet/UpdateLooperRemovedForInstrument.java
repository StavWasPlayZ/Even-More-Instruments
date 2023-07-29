package com.cstav.evenmoreinstruments.networking.packet;

import java.util.Optional;
import java.util.function.Supplier;

import com.cstav.evenmoreinstruments.block.blockentity.LooperBlockEntity;
import com.cstav.evenmoreinstruments.networking.ModPacketHandler;
import com.cstav.genshinstrument.networking.IModPacket;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class UpdateLooperRemovedForInstrument implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_SERVER;

    final Optional<InteractionHand> hand;
    final Optional<BlockPos> blockPos;

    public UpdateLooperRemovedForInstrument(final InteractionHand hand) {
        this.hand = Optional.of(hand);
        blockPos = Optional.empty();
    }
    public UpdateLooperRemovedForInstrument(final BlockPos blockPos) {
        this.blockPos = Optional.of(blockPos);
        this.hand = Optional.empty();
    }
    public UpdateLooperRemovedForInstrument(FriendlyByteBuf buf) {
        hand = buf.readOptional((fbb) -> fbb.readEnum(InteractionHand.class));
        blockPos = buf.readOptional(FriendlyByteBuf::readBlockPos);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeOptional(hand, FriendlyByteBuf::writeEnum);
        buf.writeOptional(blockPos, FriendlyByteBuf::writeBlockPos);
    }

    @Override
    public boolean handle(Supplier<Context> arg0) {
        final Context context = arg0.get();

        context.enqueueWork(() -> {
            final ServerPlayer player = context.getSender();
            final Level level = player.level();

            LooperBlockEntity result;

            if (hand.isPresent())
                result = LooperBlockEntity.getLBE(level, player.getItemInHand(hand.get()));
            else
                result = LooperBlockEntity.getLBE(level, level.getBlockEntity(blockPos.get()));

            if (result == null)
                ModPacketHandler.sendToClient(new LooperRemovedPacket(), player);
        });

        return true;
    }
    
}
