package com.cstav.evenmoreinstruments.client.gui.instrument;

import java.util.Optional;

import com.cstav.evenmoreinstruments.Main;
import com.cstav.evenmoreinstruments.networking.ModPacketHandler;
import com.cstav.evenmoreinstruments.networking.packet.RecordStatePacket;
import com.cstav.evenmoreinstruments.util.LooperUtil;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(bus = Bus.FORGE, modid = Main.MODID, value = Dist.CLIENT)
public class LooperOverlayInjector {
    private static final int REC_BTN_WIDTH = 120;
    
    private static AbstractInstrumentScreen screen = null;
    private static BlockPos instrumentBlockPos = null;

    @SuppressWarnings("resource")
    @SubscribeEvent
    public static void onScreenDrawn(final ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof AbstractInstrumentScreen))
            return;

        final AbstractInstrumentScreen screen = (AbstractInstrumentScreen) event.getScreen();
        final Player player = Minecraft.getInstance().player;

        if (screen.interactionHand.isPresent()) {
            if (!LooperUtil.hasLooperTag(player.getItemInHand(screen.interactionHand.get())))
                return;
        } else {
            final BlockEntity be = getIBE(player);

            if (be == null || !LooperUtil.hasLooperTag(be))
                return;
            
            instrumentBlockPos = InstrumentOpenProvider.getBlockPos(player);
        }

        LooperOverlayInjector.screen = screen;

        event.addListener(
            Button.builder(
                Component.translatable("button.evenmoreinstruments.record"),
                LooperOverlayInjector::onRecordPress
            )
                .width(REC_BTN_WIDTH)
                .pos((screen.width - REC_BTN_WIDTH) / 2, 25)
                .build()
        );
    }

    @SubscribeEvent
    public static void onScreenClose(final ScreenEvent.Closing event) {
        if (event.getScreen() == screen)
            ModPacketHandler.sendToServer(
                new RecordStatePacket(false, screen.interactionHand, Optional.ofNullable(instrumentBlockPos))
            );
    }
    
    @SuppressWarnings("resource")
    private static void onRecordPress(final Button btn) {
        final LocalPlayer player = Minecraft.getInstance().player;
        final Optional<InteractionHand> hand = screen.interactionHand;

        final boolean isRecording = hand.isPresent()
            ? LooperUtil.isRecording(LooperUtil.looperTag(player.getItemInHand(hand.get())))
            : LooperUtil.isRecording(LooperUtil.looperTag(getIBE(player)));


        if (isRecording) {
            screen.renderables.removeIf((renderable) -> renderable.equals(btn));
            screen = null;
        } else
            btn.setMessage(Component.translatable("button.evenmoreinstruments.stop"));

        ModPacketHandler.sendToServer(new RecordStatePacket(!isRecording, hand, Optional.empty()));
    }

    private static BlockEntity getIBE(final Player player) {
        final BlockPos instrumentPos = InstrumentOpenProvider.getBlockPos(player);

        return (instrumentPos == null) ? null
            : player.level().getBlockEntity(instrumentPos);
    }
}
