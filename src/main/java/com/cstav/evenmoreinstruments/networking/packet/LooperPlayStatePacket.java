package com.cstav.evenmoreinstruments.networking.packet;

import java.util.function.Supplier;

import com.cstav.genshinstrument.networking.IModPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class LooperPlayStatePacket implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;

    private final boolean isPlaying;
    private final BlockPos blockPos;

    public LooperPlayStatePacket(final boolean isPlaying, final BlockPos blockPos) {
        this.isPlaying = isPlaying;
        this.blockPos = blockPos;
    }
    public LooperPlayStatePacket(final FriendlyByteBuf buf) {
        isPlaying = buf.readBoolean();
        blockPos = buf.readBlockPos();
    }

    @Override
    public void toBytes(FriendlyByteBuf buf) {
        buf.writeBoolean(isPlaying);
        buf.writeBlockPos(blockPos);
    }


    @SuppressWarnings("resource")
    @Override
    public void handle(Supplier<Context> arg0) {
        final Context context = arg0.get();

        context.enqueueWork(() ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                final Level level = Minecraft.getInstance().player.getLevel();

                // Parrots go brrrr
                for (final LivingEntity livingentity : level.getEntitiesOfClass(LivingEntity.class, (new AABB(blockPos)).inflate(3)))
                    livingentity.setRecordPlayingNearby(blockPos, isPlaying);
            }
        ));

        context.setPacketHandled(true);
    }

}
