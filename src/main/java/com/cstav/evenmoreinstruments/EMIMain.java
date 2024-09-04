package com.cstav.evenmoreinstruments;

import com.cstav.evenmoreinstruments.block.ModBlocks;
import com.cstav.evenmoreinstruments.block.blockentity.ModBlockEntities;
import com.cstav.evenmoreinstruments.criteria.ModCriteria;
import com.cstav.evenmoreinstruments.gamerule.ModGameRules;
import com.cstav.evenmoreinstruments.item.ModItems;
import com.cstav.evenmoreinstruments.item.component.ModDataComponents;
import com.cstav.evenmoreinstruments.item.crafting.ModRecipeSerializers;
import com.cstav.evenmoreinstruments.networking.EMIPacketHandler;
import com.cstav.evenmoreinstruments.sound.ModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The main mod class of the "Even More Instruments!" mod
 */
@Mod(EMIMain.MODID)
public class EMIMain
{
    public static final String MODID = "evenmoreinstruments";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

//    public static CompoundTag modTag(final ItemStack item) {
//        return item.getOrCreateTagElement(MODID);
//    }
    public static CompoundTag modTag(final BlockEntity be) {
        if (be.components().has(ModDataComponents.MOD_TAG.get()))
            return be.components().get(ModDataComponents.MOD_TAG.get()).getUnsafe();

        //FIXME
        final CompoundTag modTag = new CompoundTag();
//        be.components().
        CustomData.of(modTag).loadInto(be, Minecraft.getInstance().level.registryAccess());
        return modTag;
    }
    
    public EMIMain()
    {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        EMIPacketHandler.registerPackets();

        ModSounds.register(bus);

        ModGameRules.load();
        ModCriteria.register(bus);

        EMIModCreativeModeTabs.register(bus);

        ModDataComponents.register(bus);
        ModBlocks.register(bus);
        ModBlockEntities.register(bus);
        ModItems.register(bus);
        ModRecipeSerializers.register(bus);

        MinecraftForge.EVENT_BUS.register(this);
    }
}
