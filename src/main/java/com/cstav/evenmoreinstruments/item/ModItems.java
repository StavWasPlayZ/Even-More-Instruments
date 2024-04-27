package com.cstav.evenmoreinstruments.item;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.EMIModCreativeModeTabs;
import com.cstav.evenmoreinstruments.block.ModBlocks;
import com.cstav.evenmoreinstruments.item.emirecord.BurnedRecordItem;
import com.cstav.evenmoreinstruments.item.emirecord.WritableRecordItem;
import com.cstav.evenmoreinstruments.item.partial.instrument.CreditableBlockInstrumentItem;
import com.cstav.evenmoreinstruments.item.partial.instrument.CreditableInstrumentItem;
import com.cstav.evenmoreinstruments.item.partial.instrument.CreditableWindInstrumentItem;
import com.cstav.evenmoreinstruments.networking.EMIPacketHandler;
import com.cstav.evenmoreinstruments.networking.packet.EMIOpenInstrumentPacket;
import com.cstav.genshinstrument.ModCreativeModeTabs;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, EMIMain.MODID);
    public static void register(final IEventBus bus) {
        ITEMS.register(bus);
    }


    public static final RegistryObject<Item>
        VIOLIN_BOW = register("violin_bow",
            () -> new InstrumentAccessoryItem(
                new Properties()
                    .stacksTo(1)
                    .durability(InstrumentAccessoryItem.MAX_DURABILITY)
                    .tab(ModCreativeModeTabs.instrumentsTab)
            )
        ),
        VIOLIN = register("violin", ViolinItem::new),

        GUITAR = register("guitar", () -> new CreditableInstrumentItem(
            (player) -> EMIPacketHandler.sendToClient(
                new EMIOpenInstrumentPacket("guitar"), player
            ),
            new Properties().tab(ModCreativeModeTabs.instrumentsTab),
            "Philharmonia"
        )),
        PIPA = register("pipa", () -> new CreditableInstrumentItem(
            (player) -> EMIPacketHandler.sendToClient(
                new EMIOpenInstrumentPacket("pipa"), player
            ),
            new Properties().tab(ModCreativeModeTabs.instrumentsTab),
            "DSK Asian DreamZ"
        )),

        BACHI = register("bachi",
            () -> new InstrumentAccessoryItem(
                new Properties()
                    .stacksTo(1)
                    .durability(InstrumentAccessoryItem.MAX_DURABILITY)
                    .tab(ModCreativeModeTabs.instrumentsTab)
            )
        ),
        SHAMISEN = register("shamisen",
            () -> new AccessoryInstrumentItem(
                (player) -> EMIPacketHandler.sendToClient(
                    new EMIOpenInstrumentPacket("shamisen"), player
                ),
                new Properties().tab(ModCreativeModeTabs.instrumentsTab),
                (InstrumentAccessoryItem) BACHI.get(),
                "Roland SC-88"
            )
        ),

        KOTO = register("koto", () ->
            new CreditableBlockInstrumentItem(
                ModBlocks.KOTO.get(), new Properties()
                .stacksTo(1)
                .tab(ModCreativeModeTabs.instrumentsTab),
                "DSK Asian DreamZ"
            )
        ),

        TROMBONE = register("trombone", () -> new CreditableWindInstrumentItem(
            (player) -> EMIPacketHandler.sendToClient(
                new EMIOpenInstrumentPacket("trombone"), player
            ),
            new Properties().tab(ModCreativeModeTabs.instrumentsTab),
            "Philharmonia"
        )),
        SAXOPHONE = register("saxophone", () -> new CreditableWindInstrumentItem(
            (player) -> EMIPacketHandler.sendToClient(
                new EMIOpenInstrumentPacket("saxophone"), player
            ),
            new Properties().tab(ModCreativeModeTabs.instrumentsTab),
            "Philharmonia"
        )),
        KEYBOARD = register("keyboard", () ->
            new KeyboardBlockItem(
                ModBlocks.KEYBOARD.get(), new Properties()
                .tab(ModCreativeModeTabs.instrumentsTab)
                .stacksTo(1),
                null
            )
        ),
        KEYBOARD_STAND = registerBlockItem(ModBlocks.KEYBOARD_STAND, ModCreativeModeTabs.instrumentsTab),

        LOOPER = registerBlockItem(ModBlocks.LOOPER, EMIModCreativeModeTabs.musicProductionTab),
        LOOPER_ADAPTER = register(
            "looper_adapter", () -> new LooperAdapterItem(new Properties()
                .tab(EMIModCreativeModeTabs.musicProductionTab)
                .stacksTo(1)
            )
        ),

        RECORD_WRITABLE = register(
            "record_writable", () -> new WritableRecordItem(new Properties()
                .tab(EMIModCreativeModeTabs.musicProductionTab)
            )
        ),
        RECORD_JOHNNY = register("record_johnny", () ->
            new BurnedRecordItem(
                new Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .tab(EMIModCreativeModeTabs.musicProductionTab),
                new ResourceLocation(EMIMain.MODID, "johnny"),
                "Hänschen klein - Franz Wiedemann",
                null
            )
        ),
        RECORD_SUPER_IDOL = register("record_super_idol", () ->
            new BurnedRecordItem(
                new Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .tab(EMIModCreativeModeTabs.musicProductionTab),
                new ResourceLocation(EMIMain.MODID, "super_idol"),
                "Super Idol - De Xian Rong",
                "Saxophy"
            )
        ),
        RECORD_OVEN_KID = register("record_oven_kid", () ->
            new BurnedRecordItem(
                new Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .tab(EMIModCreativeModeTabs.musicProductionTab),
                new ResourceLocation(EMIMain.MODID, "oven_kid"),
                "Timmy Trumpet & Savage - Freaks",
                "StavWasPlayZ"
            )
        ),
        RECORD_SAD_VIOLIN = register("record_sad_violin", () ->
            new BurnedRecordItem(
                new Properties()
                    .stacksTo(1)
                    .rarity(Rarity.RARE)
                    .tab(EMIModCreativeModeTabs.musicProductionTab),
                new ResourceLocation(EMIMain.MODID, "sad_violin"),
                "Sad Romance - Ji Pyeongkeyon",
                "StavWasPlayZ"
            )
        ),
        RECORD_RICKROLL = register("record_rickroll", () ->
            new BurnedRecordItem(
                new Properties()
                    .stacksTo(1)
                    .rarity(Rarity.EPIC)
                    .tab(EMIModCreativeModeTabs.musicProductionTab),
                new ResourceLocation(EMIMain.MODID, "rickroll"),
                null,
                "StavWasPlayZ",
                new TranslatableComponent("item.evenmoreinstruments.interesting_record")
            )
        )
    ;

    public static final Map<NoteBlockInstrument, RegistryObject<Item>> NOTEBLOCK_INSTRUMENTS = initNoteBlockInstruments();

    public static HashMap<NoteBlockInstrument, RegistryObject<Item>> initNoteBlockInstruments() {
        final NoteBlockInstrument[] instruments = NoteBlockInstrument.values();
        final HashMap<NoteBlockInstrument, RegistryObject<Item>> result = new HashMap<>(instruments.length);

        for (final NoteBlockInstrument instrument : instruments) {
            result.put(instrument,
                register(
                    NoteBlockInstrumentItem.getId(instrument),
                    () -> new NoteBlockInstrumentItem(instrument)
                )
            );
        }

        return result;
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