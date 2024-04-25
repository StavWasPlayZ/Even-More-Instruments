package com.cstav.evenmoreinstruments;

import com.cstav.evenmoreinstruments.block.ModBlocks;
import com.cstav.evenmoreinstruments.block.blockentity.ModBlockEntities;
import com.cstav.evenmoreinstruments.criteria.ModCriteria;
import com.cstav.evenmoreinstruments.gamerule.ModGameRules;
import com.cstav.evenmoreinstruments.item.ModItems;
import com.cstav.evenmoreinstruments.item.crafting.ModRecipeSerializers;
import com.cstav.evenmoreinstruments.networking.EMIPacketHandler;
import com.cstav.evenmoreinstruments.sound.ModSounds;
import com.cstav.evenmoreinstruments.util.CommonUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

/**
 * The main mod class of the "Even More Instruments!" mod
 */
@Mod(EMIMain.MODID)
public class EMIMain
{
    public static final String MODID = "evenmoreinstruments";
    public static CompoundTag modTag(final ItemStack item) {
        return item.getOrCreateTagElement(MODID);
    }
    public static CompoundTag modTag(final BlockEntity be) {
        return CommonUtil.getOrCreateElementTag(be.getPersistentData(), MODID);
    }
    
    public EMIMain()
    {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        EMIPacketHandler.registerPackets();

        ModSounds.register(bus);

        ModGameRules.load();
        ModCriteria.load();

        ModBlocks.register(bus);
        ModBlockEntities.register(bus);
        ModItems.register(bus);
        ModRecipeSerializers.register(bus);

        MinecraftForge.EVENT_BUS.register(this);
    }
}
