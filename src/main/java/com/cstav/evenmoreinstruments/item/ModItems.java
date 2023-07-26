package com.cstav.evenmoreinstruments.item;

import com.cstav.evenmoreinstruments.Main;
import com.cstav.evenmoreinstruments.block.ModBlocks;
import com.cstav.genshinstrument.ModCreativeModeTabs;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@EventBusSubscriber(modid = Main.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class ModItems {
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);
    public static void register(final IEventBus bus) {
        ITEMS.register(bus);
    }

    public static final RegistryObject<Item>
        LOOPER = ITEMS.register("looper", () -> new BlockItem(ModBlocks.LOOPER.get(), new Properties()))
    ;


    @SubscribeEvent
    public static void addCreative(final BuildCreativeModeTabContentsEvent event) {
        if (
            (event.getTab() != ModCreativeModeTabs.INSTRUMENTS_TAB.get()) ||
            (event.getTabKey() != CreativeModeTabs.FUNCTIONAL_BLOCKS)
        ) return;

        ITEMS.getEntries().forEach((entry) -> event.accept(entry.get()));
    }

}
