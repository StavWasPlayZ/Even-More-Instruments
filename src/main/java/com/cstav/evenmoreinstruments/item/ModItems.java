package com.cstav.evenmoreinstruments.item;

import java.util.HashMap;
import java.util.Map;

import com.cstav.evenmoreinstruments.Main;
import com.cstav.genshinstrument.ModCreativeModeTabs;

import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
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
    public static final String NOTEBLOCK_INSTRUMENT_SUFFIX = "_note_block_instrument";
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);
    public static void register(final IEventBus bus) {
        ITEMS.register(bus);
    }


    // public static final RegistryObject<Item>
        
    // ;

    public static final Map<NoteBlockInstrument, RegistryObject<Item>> NOTEBLOCK_INSTRUMENTS = initNoteBlockInstruments();


    public static HashMap<NoteBlockInstrument, RegistryObject<Item>> initNoteBlockInstruments() {
        final NoteBlockInstrument[] instruments = NoteBlockInstrument.values();
        final HashMap<NoteBlockInstrument, RegistryObject<Item>> result = new HashMap<>(instruments.length);

        for (final NoteBlockInstrument instrument : instruments) {
            if (!instrument.isTunable())
                continue;

            result.put(instrument,
                ITEMS.register(getInstrumentId(instrument),
                () -> new NoteBlockInstrumentItem(instrument))
            );
        }
        
        return result;
    }

    public static String getInstrumentId(final NoteBlockInstrument instrument) {
        return instrument.getSerializedName() + NOTEBLOCK_INSTRUMENT_SUFFIX;
    }


    @SubscribeEvent
    public static void addCreative(final BuildCreativeModeTabContentsEvent event) {
        if (
            (event.getTab() != ModCreativeModeTabs.INSTRUMENTS_TAB.get()) &&
            (event.getTabKey() != CreativeModeTabs.FUNCTIONAL_BLOCKS)
        ) return;

        ITEMS.getEntries().forEach((entry) -> event.accept(entry.get()));
    }

}
