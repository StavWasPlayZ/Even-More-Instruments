package com.cstav.evenmoreinstruments.networking.packet;

import com.cstav.evenmoreinstruments.capability.ModTagCapabilityProvider;
import com.cstav.genshinstrument.networking.IModPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;

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
            final Level level = Minecraft.getInstance().player.level();
            final BlockEntity be = level.getBlockEntity(pos);

            be.getCapability(ModTagCapabilityProvider.CAPABILITY).resolve().get().setTag(modTag);
        });
    }
}
