package com.cstav.evenmoreinstruments;

import java.util.List;

import com.cstav.evenmoreinstruments.item.ModItems;
import com.cstav.genshinstrument.ModCreativeModeTabs;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(modid = Main.MODID, bus = Bus.MOD)
public class EMIModCreativeModeTabs {

    private static CreativeModeTab instrumentAccessoryTab;
    public static CreativeModeTab getInstrumentAccessoryTab() {
        return instrumentAccessoryTab;
    }

    @SubscribeEvent
    public static void regsiterCreativeModeTabs(final CreativeModeTabEvent.Register event) {
        instrumentAccessoryTab = event.registerCreativeModeTab(new ResourceLocation(Main.MODID, "instrument_accessories_tab"),
            List.of(ModCreativeModeTabs.getInstrumentsTab()), List.of(),
            (builder) -> builder
                .title(Component.translatable("evenmoreinstruments.itemGroup.instrument_accessories_tab"))
                .icon(() -> new ItemStack(ModItems.KEYBOARD_STAND.get()))

            .build()
        );
    }

}
