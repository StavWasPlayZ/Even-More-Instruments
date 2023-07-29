package com.cstav.evenmoreinstruments.networking.packet;

import java.util.function.Supplier;

import com.cstav.evenmoreinstruments.client.gui.instrument.LooperOverlayInjector;
import com.cstav.genshinstrument.networking.IModPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent.Context;

public class LooperRemovedPacket implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;
    
    public LooperRemovedPacket() {}
    public LooperRemovedPacket(final FriendlyByteBuf buf) {}

    @Override
    public boolean handle(Supplier<Context> arg0) {
        final Context context = arg0.get();

        context.enqueueWork(() ->
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () ->
                LooperOverlayInjector.removeRecordButton()
            )
        );

        return true;
    }
    
}
