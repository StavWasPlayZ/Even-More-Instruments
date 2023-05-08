package com.cstav.genshinstrumentp.item;

import com.cstav.genshinstrument.ModCreativeModeTabs;
import com.cstav.genshinstrumentp.Main;
import com.cstav.genshinstrumentp.block.ModBlocks;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.CreativeModeTabEvent;
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
    public static void addCreative(final CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() != ModCreativeModeTabs.getInstrumentsTab())
            return;

        ITEMS.getEntries().forEach((entry) -> event.accept(entry.get()));
    }

}
