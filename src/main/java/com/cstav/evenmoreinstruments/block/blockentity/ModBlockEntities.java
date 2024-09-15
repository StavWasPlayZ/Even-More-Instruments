package com.cstav.evenmoreinstruments.block.blockentity;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {

    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, EMIMain.MODID);
    public static void register(final IEventBus bus) {
        BLOCK_ENTITIES.register(bus);
    }


    public static final RegistryObject<BlockEntityType<LooperBlockEntity>> LOOPER = BLOCK_ENTITIES.register("looper", () ->
        BlockEntityType.Builder.of(
            LooperBlockEntity::new, ModBlocks.LOOPER.get()
        ).build(null)
    );

    public static final RegistryObject<BlockEntityType<ModInstrumentBlockEntity>> INSTRUMENT = BLOCK_ENTITIES.register(EMIMain.MODID+"_instrument", () ->
        BlockEntityType.Builder.of(
            ModInstrumentBlockEntity::new,
            ModBlocks.KEYBOARD.get(),
            ModBlocks.KEYBOARD_STAND.get(),
            ModBlocks.KOTO.get()
        ).build(null)
    );
    
}