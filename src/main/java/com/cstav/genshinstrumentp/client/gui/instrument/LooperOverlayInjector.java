package com.cstav.genshinstrumentp.client.gui.instrument;

import com.cstav.genshinstrument.client.gui.screens.instrument.partial.AbstractInstrumentScreen;
import com.cstav.genshinstrumentp.Main;
import com.cstav.genshinstrumentp.Util;
import com.cstav.genshinstrumentp.block.LooperBlock;
import com.cstav.genshinstrumentp.networking.ModPacketHandler;
import com.cstav.genshinstrumentp.networking.RecordStatePacket;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
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

    @SubscribeEvent
    public static void onScreenDrawn(final ScreenEvent.Init.Post event) {
        if (!(event.getScreen() instanceof AbstractInstrumentScreen))
            return;

        final AbstractInstrumentScreen screen = (AbstractInstrumentScreen) event.getScreen();
        if (!LooperBlock.hasLooperTag(screen.instrument))
            return;

        LooperOverlayInjector.screen = screen;

        event.addListener(
            Button.builder(
                Component.translatable("button.genshinstrumentp.record"),
                LooperOverlayInjector::onRecordPress
            )
                .width(REC_BTN_WIDTH)
                .pos((screen.width - REC_BTN_WIDTH) / 2, 25)
                .build()
        );
    }

    @SubscribeEvent
    public static void onScreenClose(final ScreenEvent.Closing event) {
        if (event.getScreen() != screen)
            return;

        ModPacketHandler.sendToServer(new RecordStatePacket(false, Util.getInstrumentHand()));
    }
    
    @SuppressWarnings("resource")
    private static void onRecordPress(final Button btn) {
        final LocalPlayer player = Minecraft.getInstance().player;
        final InteractionHand hand = Util.getInstrumentHand();
        final ItemStack item = player.getItemInHand(hand);

        btn.setMessage(Component.translatable("button.genshinstrumentp."
            + (LooperBlock.isRecording(item) ? "record" : "stop")
        ));

        ModPacketHandler.sendToServer(new RecordStatePacket(!LooperBlock.isRecording(item), hand));
    }
}
