package com.cstav.evenmoreinstruments.networking.packet;

import com.cstav.evenmoreinstruments.client.gui.instrument.LooperOverlayInjector;
import com.cstav.genshinstrument.networking.IModPacket;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;

public class LooperRemovedPacket implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;
    
    public LooperRemovedPacket() {}
    public LooperRemovedPacket(final FriendlyByteBuf buf) {}

    @Override
    public void handle(final Context context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> LooperOverlayInjector::handleLooperRemoved);
    }
    
}
