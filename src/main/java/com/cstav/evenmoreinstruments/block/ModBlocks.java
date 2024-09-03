package com.cstav.evenmoreinstruments.block;

import com.cstav.evenmoreinstruments.EMIMain;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, EMIMain.MODID);
    public static void register(final IEventBus bus) {
        BLOCKS.register(bus);
    }

    public static final RegistryObject<Block>
        KEYBOARD = BLOCKS.register("keyboard", () -> new KeyboardBlock(
            Properties.of().noOcclusion().strength(.3f)
        )),
        KEYBOARD_STAND = BLOCKS.register("keyboard_stand", () -> new KeyboardStandBlock(
            Properties.of().noOcclusion().strength(.3f)
        )),

        KOTO = BLOCKS.register("koto", () -> new KotoBlock(
            Properties.of().noOcclusion().strength(.3f).sound(SoundType.WOOD)
        )),

        LOOPER = BLOCKS.register("looper", () -> new LooperBlock(Properties.ofFullCopy(Blocks.NOTE_BLOCK)))
    ;

}
