package com.cstav.evenmoreinstruments.client.gui.instrument;

import com.cstav.evenmoreinstruments.Main;
import com.cstav.evenmoreinstruments.mixins.required.ScreenAccessor;
import com.cstav.evenmoreinstruments.event.ScreenCloseEvent;
import com.cstav.evenmoreinstruments.networking.ModPacketHandler;
import com.cstav.evenmoreinstruments.networking.packet.DoesLooperExistPacket;
import com.cstav.evenmoreinstruments.networking.packet.LooperRecordStatePacket;
import com.cstav.evenmoreinstruments.util.LooperUtil;
import com.cstav.genshinstrument.capability.instrumentOpen.InstrumentOpenProvider;
import com.cstav.genshinstrument.client.gui.screen.instrument.partial.InstrumentScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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

    private static InstrumentScreen screen = null;
    private static boolean isRecording = false;
    private static Button recordBtn;

    @SuppressWarnings("resource")
    @SubscribeEvent
    public static void onScreenInit(final ScreenEvent.InitScreenEvent.Post event) {
        if (!(event.getScreen() instanceof InstrumentScreen instrumentScreen))
            return;

        final Player player = Minecraft.getInstance().player;

        if (InstrumentOpenProvider.isItem(player)) {
            final InteractionHand hand = InstrumentOpenProvider.getHand(player);
            final ItemStack instrumentItem = player.getItemInHand(hand);

            if (!LooperUtil.hasLooperTag(instrumentItem))
                return;

            ModPacketHandler.sendToServer(new DoesLooperExistPacket(hand));
        } else {
            final BlockPos instrumentBlockPos = InstrumentOpenProvider.getBlockPos(player);
            final BlockEntity instrumentBE = player.getLevel().getBlockEntity(instrumentBlockPos);

            if (!LooperUtil.hasLooperTag(instrumentBE))
                return;

            ModPacketHandler.sendToServer(new DoesLooperExistPacket());
        }

        LooperOverlayInjector.screen = instrumentScreen;

        event.addListener(
            recordBtn = new Button((screen.width - REC_BTN_WIDTH) / 2, 5, REC_BTN_WIDTH, 20,
                new TranslatableComponent("button.evenmoreinstruments.record"),
                LooperOverlayInjector::onRecordPress
            )
        );
    }
    public static void handleLooperRemoved() {
        removeRecordButton();
        screen = null;
    }

    @SubscribeEvent
    public static void onScreenClose(final ScreenCloseEvent event) {
        if (!isRecording || (event.screen != screen))
            return;

        final Player player = Minecraft.getInstance().player;

        ModPacketHandler.sendToServer(
            new LooperRecordStatePacket(false,
                InstrumentOpenProvider.isItem(player)
                    ? InstrumentOpenProvider.getHand(player)
                    : null
            )
        );

        isRecording = false;
        LooperOverlayInjector.screen = null;
    }

    @SuppressWarnings("resource")
    private static void onRecordPress(final Button btn) {
        final LocalPlayer player = Minecraft.getInstance().player;

        final boolean isItem = InstrumentOpenProvider.isItem(player);
        final InteractionHand hand = isItem ?
            InstrumentOpenProvider.getHand(Minecraft.getInstance().player)
            : null;

        isRecording = isItem
            ? LooperUtil.isRecording(LooperUtil.looperTag(player.getItemInHand(hand)))
            : LooperUtil.isRecording(LooperUtil.looperTag(getIBE(player)));


        if (isRecording) {
            removeRecordButton();
            screen = null;
        } else
            btn.setMessage(new TranslatableComponent("button.evenmoreinstruments.stop"));

        isRecording = !isRecording;
        ModPacketHandler.sendToServer(new LooperRecordStatePacket(isRecording, hand));
    }

    private static BlockEntity getIBE(final Player player) {
        final BlockPos instrumentPos = InstrumentOpenProvider.getBlockPos(player);

        return (instrumentPos == null) ? null
            : player.getLevel().getBlockEntity(instrumentPos);
    }


    public static void removeRecordButton() {
        if (screen == null)
            return;

        ((ScreenAccessor)screen).invokeRemoveWidget(recordBtn);
    }
}
