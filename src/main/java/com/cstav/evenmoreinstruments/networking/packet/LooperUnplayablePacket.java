package com.cstav.evenmoreinstruments.networking.packet;

import com.cstav.evenmoreinstruments.client.gui.instrument.LooperOverlayInjector;
import com.cstav.genshinstrument.networking.IModPacket;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent.Context;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;

/**
 * A packet telling the client, who just attempted
 * to record into a looper - that they cannot.
 */
public class LooperUnplayablePacket implements IModPacket {
    public static final NetworkDirection NETWORK_DIRECTION = NetworkDirection.PLAY_TO_CLIENT;
    
    public LooperUnplayablePacket() {}
    public LooperUnplayablePacket(final FriendlyByteBuf buf) {}

    @Override
    public void handle(final Context context) {
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> LooperOverlayInjector::handleLooperRemoved);
    }
    
}
