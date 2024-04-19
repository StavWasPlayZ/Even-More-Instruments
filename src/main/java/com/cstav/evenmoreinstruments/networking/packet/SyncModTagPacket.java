package com.cstav.evenmoreinstruments.networking.packet;

import com.cstav.evenmoreinstruments.EMIMain;
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

/**
 * Syncs the given mod tag on the client to the block position.
 * Primarily used to update block instruments for looper record states.
 */
public class SyncModTagPacket implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;
    
    private final CompoundTag modTag;
    private final BlockPos pos;

    public SyncModTagPacket(final CompoundTag modTag, final BlockPos pos) {
        this.modTag = modTag;
        this.pos = pos;
    }
    public SyncModTagPacket(final FriendlyByteBuf buf) {
        // We don't need to read over 0x20000 or however many zeroes.
        modTag = buf.readNbt();
        pos = buf.readBlockPos();
    }

    @Override
    public void write(final FriendlyByteBuf buf) {
        buf.writeNbt(modTag);
        buf.writeBlockPos(pos);
    }

    @SuppressWarnings("resource")
    @Override
    public void handle(final Context context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            final BlockEntity be = Minecraft.getInstance().player.getLevel().getBlockEntity(pos);
            
            if (be != null)
                be.getPersistentData().put(EMIMain.MODID, modTag);
        });
    }
}
