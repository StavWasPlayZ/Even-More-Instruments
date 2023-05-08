package com.cstav.genshinstrumentp;

import com.cstav.genshinstrumentp.block.ModBlocks;
import com.cstav.genshinstrumentp.block.blockentity.ModBlockEntities;
import com.cstav.genshinstrumentp.item.ModItems;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Main.MODID)
public class Main
{
    public static final String MODID = "genshinstrumentp";
    public static CompoundTag modTag(final ItemStack item) {
        return item.getOrCreateTagElement(MODID);
    }
    
    public Main()
    {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        ModBlocks.register(bus);
        ModBlockEntities.register(bus);
        ModItems.register(bus);

        MinecraftForge.EVENT_BUS.register(this);
    }
}
