package com.cstav.evenmoreinstruments.block;

import com.cstav.evenmoreinstruments.Main;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
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
        LOOPER = BLOCKS.register("looper", () -> new LooperBlock(Properties.of(Material.STONE)))
    ;

}
