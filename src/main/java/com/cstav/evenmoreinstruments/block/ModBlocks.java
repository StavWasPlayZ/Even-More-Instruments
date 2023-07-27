package com.cstav.evenmoreinstruments.block;

import java.util.function.Supplier;

import com.cstav.evenmoreinstruments.Main;
import com.cstav.evenmoreinstruments.item.ModItems;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Main.MODID);
    public static void register(final IEventBus bus) {
        BLOCKS.register(bus);
    }

    public static final RegistryObject<Block>
        KEYBOARD = registerBlockItem("keyboard", () -> new KeyboardBlock(Properties.copy(Blocks.WHITE_CONCRETE))),
        LOOPER = registerBlockItem("looper", () -> new LooperBlock(Properties.copy(Blocks.NOTE_BLOCK)))
    ;


    private static <T extends Block> RegistryObject<Block> registerBlockItem(final String name, final Supplier<T> supplier) {
        final RegistryObject<Block> block = BLOCKS.register(name, supplier);

        ModItems.ITEMS.register(name, () -> new BlockItem(
            block.get(),
            new net.minecraft.world.item.Item.Properties()
        ));

        return block;
    }

}
