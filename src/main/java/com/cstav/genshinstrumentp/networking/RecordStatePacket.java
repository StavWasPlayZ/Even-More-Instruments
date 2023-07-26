package com.cstav.genshinstrumentp.networking;

import java.util.function.Supplier;

import com.cstav.genshinstrument.item.InstrumentItem;
import com.cstav.genshinstrument.networking.ModPacket;
import com.cstav.genshinstrumentp.block.LooperBlock;
import com.cstav.genshinstrumentp.block.blockentity.LooperBlockEntity;
import com.cstav.genshinstrumentp.util.LooperUtil;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class RecordStatePacket implements ModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_SERVER;

    private final InteractionHand usedHand;
    private final boolean recording;
    public RecordStatePacket(final boolean recording, final InteractionHand usedHand) {
        this.recording = recording;
        this.usedHand = usedHand;
    }
    public RecordStatePacket(final FriendlyByteBuf buf) {
        recording = buf.readBoolean();
        usedHand = buf.readEnum(InteractionHand.class);
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(recording);
        buf.writeEnum(usedHand);
    }


    @Override
    public boolean handle(Supplier<Context> arg0) {
        Context ctx = arg0.get();

        ctx.enqueueWork(() -> {
            final ItemStack item = ctx.getSender().getItemInHand(usedHand);
            final BlockPos looperPos = LooperUtil.getLooperPos(item);
            if (!ctx.getSender().level.hasChunkAt(looperPos))
                return;


            final boolean prevRecState = LooperUtil.isRecording(item);
            if (!prevRecState && !recording)
                return;

            final LooperBlockEntity lbe = LooperBlockEntity.getLBE(ctx.getSender().level, item);

            if (item.getItem() instanceof InstrumentItem) {
                LooperUtil.setRecording(item, recording);
                lbe.setRecording(false);
            }

            if (prevRecState && !recording) {
                lbe.setRepeatTick(lbe.getTicks());
                lbe.setChanged();
                
                ctx.getSender().level.setBlock(looperPos,
                    lbe.getBlockState().setValue(LooperBlock.PLAYING, true)
                , 3);
            }
        });

        return true;
    }
    
}
