package com.cstav.evenmoreinstruments;

import com.cstav.evenmoreinstruments.block.ModBlocks;
import com.cstav.evenmoreinstruments.block.blockentity.ModBlockEntities;
import com.cstav.evenmoreinstruments.client.ModArmPose;
import com.cstav.evenmoreinstruments.item.ModItems;
import com.cstav.evenmoreinstruments.sound.ModSounds;
import com.cstav.evenmoreinstruments.util.CommonUtil;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Main.MODID)
public class Main
{
    public static final String MODID = "evenmoreinstruments";
    public static CompoundTag modTag(final ItemStack item) {
        return item.getOrCreateTagElement(MODID);
    }
    public static CompoundTag modTag(final BlockEntity be) {
        return CommonUtil.getOrCreateElementTag(be.getPersistentData(), MODID);
    }
    
    public Main()
    {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(Main::initClient);

        ModSounds.register(bus);

        EMIModCreativeModeTabs.register(bus);

        ModBlocks.register(bus);
        ModBlockEntities.register(bus);
        ModItems.register(bus);

        MinecraftForge.EVENT_BUS.register(this);
    }

    public static void initClient(final FMLClientSetupEvent event) {
        ModArmPose.register();
    }
}
