package com.cstav.evenmoreinstruments.networking.packet;

import com.cstav.evenmoreinstruments.util.LooperRecordStateUtil;
import com.cstav.genshinstrument.networking.IModPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
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

    @Override
    public void handle(final Context context) {
        LooperRecordStateUtil.handle(context.getSender(), usedHand, recording);
    }

}
