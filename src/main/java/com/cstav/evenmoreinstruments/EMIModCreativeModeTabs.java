package com.cstav.evenmoreinstruments;

import com.cstav.evenmoreinstruments.item.ModItems;
import com.cstav.genshinstrument.ModCreativeModeTabs;

import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class EMIModCreativeModeTabs {
    
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Main.MODID);
    public static void register(final IEventBus bus) {
        TABS.register(bus);
    }

    public static final RegistryObject<CreativeModeTab>
        INSTRUMENT_ACCESSORY_TAB = TABS.register("instrument_accessories_tab",
            () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.evenmoreinstruments.instrument_accessories_tab"))
                .icon(() -> new ItemStack(ModItems.KEYBOARD_STAND.get()))
                .withTabsBefore(ModCreativeModeTabs.INSTRUMENTS_TAB.getKey())
            .build()
        ),
        MUSIC_PRODUCTION_TAB = TABS.register("music_production_tab",
            () -> CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.evenmoreinstruments.music_production_tab"))
                .icon(() -> new ItemStack(ModItems.LOOPER.get()))
                .withTabsBefore(INSTRUMENT_ACCESSORY_TAB.getKey())
            .build()
        )
    ;

}
