package com.cstav.evenmoreinstruments.item;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import com.cstav.evenmoreinstruments.EMIModCreativeModeTabs;
import com.cstav.evenmoreinstruments.Main;
import com.cstav.evenmoreinstruments.block.ModBlocks;
import com.cstav.evenmoreinstruments.networking.ModPacketHandler;
import com.cstav.evenmoreinstruments.networking.packet.ModOpenInstrumentPacket;
import com.cstav.genshinstrument.ModCreativeModeTabs;
import com.cstav.genshinstrument.item.InstrumentItem;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
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


    public static final RegistryObject<Item>
        VIOLIN = register("violin", () -> new InstrumentItem(
            (player, hand) -> ModPacketHandler.sendToClient(
                new ModOpenInstrumentPacket("violin", hand), player
            )
        )),
        TROMBONE = register("trombone", () -> new TromboneItem()),

        KEYBOARD = register("keyboard", () ->
            new KeyboardBlockItem(ModBlocks.KEYBOARD.get(), new Properties().tab(ModCreativeModeTabs.instrumentsTab))
        ),


        LOOPER = registerBlockItem(ModBlocks.LOOPER, EMIModCreativeModeTabs.instrumentAccessoryTab),
        LOOPER_ADAPTER = register("looper_adapter", () -> new LooperAdapterItem(new Properties()
            .tab(EMIModCreativeModeTabs.instrumentAccessoryTab)
        )),
        KEYBOARD_STAND = registerBlockItem(ModBlocks.KEYBOARD_STAND, EMIModCreativeModeTabs.instrumentAccessoryTab)
    ;

    public static final Map<NoteBlockInstrument, RegistryObject<Item>> NOTEBLOCK_INSTRUMENTS = initNoteBlockInstruments();

    public static HashMap<NoteBlockInstrument, RegistryObject<Item>> initNoteBlockInstruments() {
        final NoteBlockInstrument[] instruments = NoteBlockInstrument.values();
        final HashMap<NoteBlockInstrument, RegistryObject<Item>> result = new HashMap<>(instruments.length);

        for (final NoteBlockInstrument instrument : instruments) {
            result.put(instrument,
                register(getInstrumentId(instrument),
                    () -> new NoteBlockInstrumentItem(instrument)
                )
            );
        }
        
        return result;
    }
    public static String getInstrumentId(final NoteBlockInstrument instrument) {
        return instrument.getSerializedName() + NOTEBLOCK_INSTRUMENT_SUFFIX;
    }


    // private static RegistryObject<Item> registerBlockItem(final RegistryObject<Block> block) {
    //     return registerBlockItem(block, DEFAULT_INSTRUMENT_BLOCK_TABS);
    // }
    private static RegistryObject<Item> registerBlockItem(RegistryObject<Block> block, CreativeModeTab tab) {
        return register(block.getId().getPath(),
            () -> new BlockItem(block.get(), new Properties().tab(tab))
        );
    }

    private static RegistryObject<Item> register(String name, Supplier<Item> supplier) {
        return ITEMS.register(name, supplier);
    }

}
