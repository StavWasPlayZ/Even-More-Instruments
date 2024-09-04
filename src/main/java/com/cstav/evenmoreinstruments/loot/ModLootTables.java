package com.cstav.evenmoreinstruments.loot;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.item.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Map;
import java.util.function.Supplier;

@EventBusSubscriber(modid = EMIMain.MODID, bus = Bus.FORGE)
public class ModLootTables {
    private static final float RECORD_DROP_PROBABILITY = .056f;

    private static final Map<ResourceLocation, Supplier<LootPool>> TO_INJECT = Map.of(
        BuiltInLootTables.JUNGLE_TEMPLE.location(), () -> createRecordPool(ModItems.RECORD_SUPER_IDOL.get()),
        BuiltInLootTables.VILLAGE_SAVANNA_HOUSE.location(), () -> createRecordPool(ModItems.RECORD_SAD_VIOLIN.get()),
        BuiltInLootTables.BASTION_OTHER.location(), () -> createRecordPool(ModItems.RECORD_OVEN_KID.get()),
        BuiltInLootTables.ABANDONED_MINESHAFT.location(), () -> createRecordPool(ModItems.RECORD_RICKROLL.get()),
        BuiltInLootTables.BURIED_TREASURE.location(), () -> createRecordPool(ModItems.RECORD_JOHNNY.get())
    );

    private static LootPool createRecordPool(final Item item) {
        return LootPool.lootPool()
            .name("emi_"+ ForgeRegistries.ITEMS.getKey(item).getPath())
            .add(
                LootItem.lootTableItem(item)
                    .when(LootItemRandomChanceCondition.randomChance(RECORD_DROP_PROBABILITY))
            )
            .build()
        ;
    }


    @SubscribeEvent
    public static void onLootTablesLoad(final LootTableLoadEvent event) {
        final ResourceLocation id = event.getName();
        if (!TO_INJECT.containsKey(id))
            return;

        event.getTable().addPool(TO_INJECT.get(id).get());
    }

}
