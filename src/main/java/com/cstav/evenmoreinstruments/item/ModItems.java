package com.cstav.evenmoreinstruments.item;

import java.util.*;
import java.util.function.Supplier;

import com.cstav.evenmoreinstruments.EMIModCreativeModeTabs;
import com.cstav.evenmoreinstruments.Main;
import com.cstav.evenmoreinstruments.block.ModBlocks;
import com.cstav.evenmoreinstruments.item.partial.instrument.AccessoryInstrumentItem;
import com.cstav.evenmoreinstruments.item.partial.instrument.CreditableInstrumentItem;
import com.cstav.evenmoreinstruments.item.partial.emirecord.BurnedRecordItem;
import com.cstav.evenmoreinstruments.item.partial.instrument.WindInstrumentItem;
import com.cstav.evenmoreinstruments.item.partial.emirecord.WritableRecordItem;
import com.cstav.evenmoreinstruments.networking.ModPacketHandler;
import com.cstav.evenmoreinstruments.networking.packet.ModOpenInstrumentPacket;
import com.cstav.genshinstrument.ModCreativeModeTabs;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;
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

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, Main.MODID);
    public static void register(final IEventBus bus) {
        ITEMS.register(bus);
    }


    private static final LinkedHashMap<RegistryObject<Item>, ResourceKey<CreativeModeTab>[]> CREATIVE_TABS_MAP = new LinkedHashMap<>();

    @SuppressWarnings("unchecked")
    private static final ResourceKey<CreativeModeTab>[] DEFAULT_INSTRUMENTS_TABS = new ResourceKey[] {
        ModCreativeModeTabs.INSTRUMENTS_TAB.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES
    };
    @SuppressWarnings("unchecked")
    private static final ResourceKey<CreativeModeTab>[] DEFAULT_INSTRUMENT_BLOCK_TABS = new ResourceKey[] {
        ModCreativeModeTabs.INSTRUMENTS_TAB.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.FUNCTIONAL_BLOCKS
    };


    public static final RegistryObject<Item>
        VIOLIN_BOW = register("violin_bow", () -> new Item(new Properties().stacksTo(1).durability(960))),
        VIOLIN = register("violin", ViolinItem::new, DEFAULT_INSTRUMENTS_TABS, VIOLIN_BOW),

        GUITAR = register("guitar", () -> new CreditableInstrumentItem(
            (player) -> ModPacketHandler.sendToClient(
                new ModOpenInstrumentPacket("guitar"), player
            ),
            "Philharmonia"
        )),
        PIPA = register("pipa", () -> new CreditableInstrumentItem(
            (player) -> ModPacketHandler.sendToClient(
                new ModOpenInstrumentPacket("pipa"), player
            ),
            "DSK Asian DreamZ"
        )),
        TROMBONE = register("trombone", () -> new WindInstrumentItem(
            (player) -> ModPacketHandler.sendToClient(
                new ModOpenInstrumentPacket("trombone"), player
            ),
            "Philharmonia"
        )),
        SAXOPHONE = register("saxophone", () -> new WindInstrumentItem(
            (player) -> ModPacketHandler.sendToClient(
                new ModOpenInstrumentPacket("saxophone"), player
            ),
            "Philharmonia"
        )),
        KEYBOARD = register("keyboard", () ->
            new KeyboardBlockItem(
                ModBlocks.KEYBOARD.get(), new Properties(),
                "Philharmonia"
            ),
            DEFAULT_INSTRUMENT_BLOCK_TABS
        ),

        KEYBOARD_STAND = registerBlockItem(ModBlocks.KEYBOARD_STAND,
            ModCreativeModeTabs.INSTRUMENTS_TAB.getKey()
        ),

        LOOPER = registerBlockItem(ModBlocks.LOOPER,
            EMIModCreativeModeTabs.MUSIC_PRODUCTION_TAB.getKey(), CreativeModeTabs.FUNCTIONAL_BLOCKS,
            CreativeModeTabs.REDSTONE_BLOCKS
        ),
        LOOPER_ADAPTER = register("looper_adapter", () -> new LooperAdapterItem(new Properties()),
            CreativeModeTabs.REDSTONE_BLOCKS, EMIModCreativeModeTabs.MUSIC_PRODUCTION_TAB.getKey()
        ),

        RECORD_WRITABLE = register("record_writable", () -> new WritableRecordItem(new Properties()),
            CreativeModeTabs.TOOLS_AND_UTILITIES, EMIModCreativeModeTabs.MUSIC_PRODUCTION_TAB.getKey()
        ),
        RECORD_JOHNNY = register("record_johnny", () ->
            new BurnedRecordItem(
                new Properties().stacksTo(1).rarity(Rarity.RARE),
                new ResourceLocation(Main.MODID, "johnny"),
                "Hänschen klein - Franz Wiedemann",
                null
            ),
            CreativeModeTabs.TOOLS_AND_UTILITIES, EMIModCreativeModeTabs.MUSIC_PRODUCTION_TAB.getKey()
        ),
        RECORD_SUPER_IDOL = register("record_super_idol", () ->
            new BurnedRecordItem(
                new Properties().stacksTo(1).rarity(Rarity.RARE),
                new ResourceLocation(Main.MODID, "super_idol"),
                "Super Idol - De Xian Rong",
                "Saxophy"
            ),
            CreativeModeTabs.TOOLS_AND_UTILITIES, EMIModCreativeModeTabs.MUSIC_PRODUCTION_TAB.getKey()
        ),
        RECORD_OVEN_KID = register("record_oven_kid", () ->
            new BurnedRecordItem(
                new Properties().stacksTo(1).rarity(Rarity.RARE),
                new ResourceLocation(Main.MODID, "oven_kid"),
                "Timmy Trumpet & Savage - Freaks",
                "StavWasPlayZ"
            ),
            CreativeModeTabs.TOOLS_AND_UTILITIES, EMIModCreativeModeTabs.MUSIC_PRODUCTION_TAB.getKey()
        ),
        RECORD_SAD_VIOLIN = register("record_sad_violin", () ->
            new BurnedRecordItem(
                new Properties().stacksTo(1).rarity(Rarity.RARE),
                new ResourceLocation(Main.MODID, "sad_violin"),
                "Sad Romance - Ji Pyeongkeyon",
                "StavWasPlayZ"
            ),
            CreativeModeTabs.TOOLS_AND_UTILITIES, EMIModCreativeModeTabs.MUSIC_PRODUCTION_TAB.getKey()
        ),
        RECORD_RICKROLL = register("record_rickroll", () ->
            new BurnedRecordItem(
                new Properties().stacksTo(1).rarity(Rarity.EPIC),
                new ResourceLocation(Main.MODID, "rickroll"),
                null,
                "StavWasPlayZ",
                Component.translatable("item.evenmoreinstruments.interesting_record")
            ),
            CreativeModeTabs.TOOLS_AND_UTILITIES, EMIModCreativeModeTabs.MUSIC_PRODUCTION_TAB.getKey()
        )
    ;

    public static final Map<NoteBlockInstrument, RegistryObject<Item>> NOTEBLOCK_INSTRUMENTS = initNoteBlockInstruments();

    public static HashMap<NoteBlockInstrument, RegistryObject<Item>> initNoteBlockInstruments() {
        final NoteBlockInstrument[] instruments = NoteBlockInstrument.values();
        final HashMap<NoteBlockInstrument, RegistryObject<Item>> result = new HashMap<>(instruments.length);

        for (final NoteBlockInstrument instrument : instruments) {
            if (!instrument.isTunable())
                continue;

            result.put(instrument,
                register(NoteBlockInstrumentItem.getId(instrument),
                    () -> new NoteBlockInstrumentItem(instrument)
                )
            );
        }
        
        return result;
    }


    // private static RegistryObject<Item> registerBlockItem(final RegistryObject<Block> block) {
    //     return registerBlockItem(block, DEFAULT_INSTRUMENT_BLOCK_TABS);
    // }
    @SafeVarargs
    private static RegistryObject<Item> registerBlockItem(RegistryObject<Block> block, ResourceKey<CreativeModeTab>... tabs) {
        return register(block.getId().getPath(),
            () -> new BlockItem(block.get(), new Properties())
        , tabs);
    }

    private static RegistryObject<Item> register(String name, Supplier<Item> supplier, ResourceKey<CreativeModeTab>[] tabs,
                                                 RegistryObject<Item> appearsBefore) {
        final RegistryObject<Item> item = ITEMS.register(name, supplier);

        final LinkedHashMap<RegistryObject<Item>, ResourceKey<CreativeModeTab>[]> temp = new LinkedHashMap<>();
        final List<RegistryObject<Item>> keys = new ArrayList<>(CREATIVE_TABS_MAP.keySet().stream().toList());

        RegistryObject<Item> removed = null;
        // Pop every element up to and including the specified element to temp; later re-add
        while (removed != appearsBefore) {
            removed = keys.get(keys.size() - 1);
            temp.put(removed, CREATIVE_TABS_MAP.remove(removed));
            keys.remove(keys.size() - 1);
        }

        CREATIVE_TABS_MAP.put(item, tabs);
        CREATIVE_TABS_MAP.putAll(temp);

        return item;
    }
    @SafeVarargs
    private static RegistryObject<Item> register(String name, Supplier<Item> supplier, ResourceKey<CreativeModeTab>... tabs) {
        final RegistryObject<Item> item = ITEMS.register(name, supplier);
        CREATIVE_TABS_MAP.put(item, tabs);

        return item;
    }
    private static RegistryObject<Item> register(String name, Supplier<Item> supplier) {
        return register(name, supplier, DEFAULT_INSTRUMENTS_TABS);
    }


    @SubscribeEvent
    public static void addCreative(final BuildCreativeModeTabContentsEvent event) {
        CREATIVE_TABS_MAP.forEach((key, value) -> {
            for (final ResourceKey<CreativeModeTab> tabKey : value) 
                if (event.getTabKey().equals(tabKey))
                    event.accept(key);
        });
    }

}
