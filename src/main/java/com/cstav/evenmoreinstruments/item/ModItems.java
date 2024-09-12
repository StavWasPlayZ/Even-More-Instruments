package com.cstav.evenmoreinstruments.item;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.EMIModCreativeModeTabs;
import com.cstav.evenmoreinstruments.block.ModBlocks;
import com.cstav.evenmoreinstruments.item.emirecord.BurnedRecordItem;
import com.cstav.evenmoreinstruments.item.emirecord.WritableRecordItem;
import com.cstav.evenmoreinstruments.item.partial.instrument.CreditableBlockInstrumentItem;
import com.cstav.evenmoreinstruments.item.partial.instrument.CreditableInstrumentItem;
import com.cstav.evenmoreinstruments.item.partial.instrument.CreditableWindInstrumentItem;
import com.cstav.genshinstrument.GICreativeModeTabs;
import com.cstav.genshinstrument.networking.packet.instrument.util.InstrumentPacketUtil;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;

@EventBusSubscriber(modid = EMIMain.MODID, bus = Bus.MOD, value = Dist.CLIENT)
public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EMIMain.MODID);
    public static void register(final IEventBus bus) {
        ITEMS.register(bus);
    }


    private static final LinkedHashMap<ResourceKey<CreativeModeTab>, ArrayList<RegistryObject<Item>>> CREATIVE_TABS_MAP = new LinkedHashMap<>();
    private static ArrayList<RegistryObject<Item>> getCreativeItems(final ResourceKey<CreativeModeTab> tabKey) {
        if (!CREATIVE_TABS_MAP.containsKey(tabKey))
            CREATIVE_TABS_MAP.put(tabKey, new ArrayList<>());
        return CREATIVE_TABS_MAP.get(tabKey);
    }

    @SuppressWarnings("unchecked")
    private static final ResourceKey<CreativeModeTab>[] DEFAULT_INSTRUMENTS_TABS = new ResourceKey[] {
        GICreativeModeTabs.INSTRUMENTS_TAB.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES
    };
    @SuppressWarnings("unchecked")
    private static final ResourceKey<CreativeModeTab>[] DEFAULT_INSTRUMENT_BLOCK_TABS = new ResourceKey[] {
        GICreativeModeTabs.INSTRUMENTS_TAB.getKey(), CreativeModeTabs.TOOLS_AND_UTILITIES, CreativeModeTabs.FUNCTIONAL_BLOCKS
    };


    public static final RegistryObject<Item>
        VIOLIN_BOW = register("violin_bow",
            () -> new InstrumentAccessoryItem(
                new Properties().stacksTo(1).durability(InstrumentAccessoryItem.MAX_DURABILITY)
            )
        ),
        VIOLIN = register("violin", ViolinItem::new, DEFAULT_INSTRUMENTS_TABS, VIOLIN_BOW),

        GUITAR = register("guitar", () -> new CreditableInstrumentItem(
            (player) -> InstrumentPacketUtil.sendOpenPacket(
                player, loc("guitar")
            ),
            "Philharmonia"
        )),
        PIPA = register("pipa", () -> new CreditableInstrumentItem(
            (player) -> InstrumentPacketUtil.sendOpenPacket(
                player, loc("pipa")
            ),
            "DSK Asian DreamZ"
        )),

        BACHI = register("bachi",
            () -> new InstrumentAccessoryItem(
                new Properties().stacksTo(1).durability(InstrumentAccessoryItem.MAX_DURABILITY)
            )
        ),
        SHAMISEN = register("shamisen",
            () -> new AccessoryInstrumentItem(
                (player) -> InstrumentPacketUtil.sendOpenPacket(
                    player, loc("shamisen")
                ),
                (InstrumentAccessoryItem) BACHI.get(),
                "Roland SC-88"
            ),
            DEFAULT_INSTRUMENTS_TABS,
            BACHI
        ),

        KOTO = register("koto", () ->
            new CreditableBlockInstrumentItem(
                ModBlocks.KOTO.get(), new Properties().stacksTo(1),
                "DSK Asian DreamZ"
            ),
            DEFAULT_INSTRUMENT_BLOCK_TABS
        ),

        TROMBONE = register("trombone", () -> new CreditableWindInstrumentItem(
            (player) -> InstrumentPacketUtil.sendOpenPacket(
                player, loc("trombone")
            ),
            "Philharmonia"
        )),
        SAXOPHONE = register("saxophone", () -> new CreditableWindInstrumentItem(
            (player) -> InstrumentPacketUtil.sendOpenPacket(
                player, loc("saxophone")
            ),
            "Philharmonia"
        )),
        KEYBOARD = register("keyboard", () ->
            new KeyboardBlockItem(
                ModBlocks.KEYBOARD.get(), new Properties().stacksTo(1),
                null
            ),
            DEFAULT_INSTRUMENT_BLOCK_TABS
        ),

        KEYBOARD_STAND = registerBlockItem(ModBlocks.KEYBOARD_STAND,
            GICreativeModeTabs.INSTRUMENTS_TAB.getKey()
        ),

        LOOPER = registerBlockItem(ModBlocks.LOOPER,
            EMIModCreativeModeTabs.MUSIC_PRODUCTION_TAB.getKey(), CreativeModeTabs.FUNCTIONAL_BLOCKS,
            CreativeModeTabs.REDSTONE_BLOCKS
        ),
        LOOPER_ADAPTER = register("looper_adapter",
            () -> new LooperAdapterItem(new Properties().stacksTo(1)),
            CreativeModeTabs.REDSTONE_BLOCKS, EMIModCreativeModeTabs.MUSIC_PRODUCTION_TAB.getKey()
        ),

    RECORD_WRITABLE = register("record_writable", () -> new WritableRecordItem(new Properties().stacksTo(16)),
            CreativeModeTabs.TOOLS_AND_UTILITIES, EMIModCreativeModeTabs.MUSIC_PRODUCTION_TAB.getKey()
        ),
        RECORD_JOHNNY = register("record_johnny", () ->
            new BurnedRecordItem(
                new Properties().stacksTo(1).rarity(Rarity.RARE),
                EMIMain.loc("johnny"),
                "Hänschen klein - Franz Wiedemann",
                null
            ),
            CreativeModeTabs.TOOLS_AND_UTILITIES, EMIModCreativeModeTabs.MUSIC_PRODUCTION_TAB.getKey()
        ),
        RECORD_SUPER_IDOL = register("record_super_idol", () ->
            new BurnedRecordItem(
                new Properties().stacksTo(1).rarity(Rarity.RARE),
                EMIMain.loc("super_idol"),
                "Super Idol - De Xian Rong",
                "Saxophy"
            ),
            CreativeModeTabs.TOOLS_AND_UTILITIES, EMIModCreativeModeTabs.MUSIC_PRODUCTION_TAB.getKey()
        ),
        RECORD_OVEN_KID = register("record_oven_kid", () ->
            new BurnedRecordItem(
                new Properties().stacksTo(1).rarity(Rarity.RARE),
                EMIMain.loc("oven_kid"),
                "Timmy Trumpet & Savage - Freaks",
                "StavWasPlayZ"
            ),
            CreativeModeTabs.TOOLS_AND_UTILITIES, EMIModCreativeModeTabs.MUSIC_PRODUCTION_TAB.getKey()
        ),
        RECORD_SAD_VIOLIN = register("record_sad_violin", () ->
            new BurnedRecordItem(
                new Properties().stacksTo(1).rarity(Rarity.RARE),
                EMIMain.loc("sad_violin"),
                "Sad Romance - Ji Pyeongkeyon",
                "StavWasPlayZ"
            ),
            CreativeModeTabs.TOOLS_AND_UTILITIES, EMIModCreativeModeTabs.MUSIC_PRODUCTION_TAB.getKey()
        ),
        RECORD_RICKROLL = register("record_rickroll", () ->
            new BurnedRecordItem(
                new Properties().stacksTo(1).rarity(Rarity.EPIC),
                EMIMain.loc("rickroll"),
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


    private static ResourceLocation loc(final String path) {
        return EMIMain.loc(path);
    }


    // private static RegistryObject<Item> registerBlockItem(final RegistryObject<Block> block) {
    //     return registerBlockItem(block, DEFAULT_INSTRUMENT_BLOCK_TABS);
    // }
    @SafeVarargs
    private static RegistryObject<Item> registerBlockItem(RegistryObject<Block> block, ResourceKey<CreativeModeTab>... tabs) {
        return register(
            block.getId().getPath(),
            () -> new BlockItem(block.get(), new Properties()),
            tabs
        );
    }

    private static RegistryObject<Item> register(String name, Supplier<Item> supplier, ResourceKey<CreativeModeTab>[] tabs,
                                                 RegistryObject<Item> appearsBefore) {
        final RegistryObject<Item> item = ITEMS.register(name, supplier);

        for (final ResourceKey<CreativeModeTab> tabKey : tabs) {
            final ArrayList<RegistryObject<Item>> items = getCreativeItems(tabKey);
            if (items.contains(appearsBefore)) {
                items.add(items.indexOf(appearsBefore), item);
            } else {
                items.add(item);
            }
        }

        return item;
    }
    @SafeVarargs
    private static RegistryObject<Item> register(String name, Supplier<Item> supplier, ResourceKey<CreativeModeTab>... tabs) {
        final RegistryObject<Item> item = ITEMS.register(name, supplier);

        for (final ResourceKey<CreativeModeTab> tabKey: tabs) {
            getCreativeItems(tabKey).add(item);
        }

        return item;
    }
    private static RegistryObject<Item> register(String name, Supplier<Item> supplier) {
        return register(name, supplier, DEFAULT_INSTRUMENTS_TABS);
    }


    @SubscribeEvent
    public static void addCreative(final BuildCreativeModeTabContentsEvent event) {
        CREATIVE_TABS_MAP.keySet().forEach((tabKey) -> {
            if (!event.getTabKey().equals(tabKey))
                return;

            event.acceptAll(
                getCreativeItems(tabKey).stream()
                    .map((item) -> new ItemStack(item.get()))
                    .toList()
            );
        });
    }

}
