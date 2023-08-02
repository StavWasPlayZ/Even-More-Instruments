package com.cstav.evenmoreinstruments.networking.packet;

import java.util.function.Supplier;

import com.cstav.evenmoreinstruments.Main;
import com.cstav.genshinstrument.networking.IModPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class SyncModTagPacket implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;
    
    private final CompoundTag modTag;
    private final BlockPos pos;

    public SyncModTagPacket(final CompoundTag modTag, final BlockPos pos) {
        this.modTag = modTag;
        this.pos = pos;
    }
    public SyncModTagPacket(final FriendlyByteBuf buf) {
        // Assuming we only send the INITIAL data of a looper,
        // we don't need to read over 0x20000 or however many zeroes.
        modTag = buf.readNbt();
        pos = buf.readBlockPos();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeNbt(modTag);
        buf.writeBlockPos(pos);
    }

    @SuppressWarnings("resource")
    @Override
    public void handle(Supplier<Context> arg0) {
        final Context context = arg0.get();

        //TODO Try without the safe method
        context.enqueueWork(() -> context.enqueueWork(() ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                final BlockEntity be = Minecraft.getInstance().player.level().getBlockEntity(pos);
                
                if (be != null)
                    be.getPersistentData().put(Main.MODID, modTag);
            })
        ));

        context.setPacketHandled(true);
    }
}
