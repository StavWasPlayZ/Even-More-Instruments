package com.cstav.evenmoreinstruments;

import com.cstav.genshinstrument.item.InstrumentItem;
import com.cstav.evenmoreinstruments.block.blockentity.LooperBlockEntity;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.FORGE, modid = Main.MODID)
public class ServerEvents {
    
    // Removes the reference to the looper on an instrument if it does not exist anymore.
    // We do this every tick because there is no event to subsribe to an itemstack's tick event,
    // nor is there one for when a block has been destoryed outside the game (unloaded chunks, world editor tools etc)
    @SubscribeEvent
    public static void onPlayerTick(final PlayerTickEvent event) {
        if (event.side == LogicalSide.CLIENT)
            return;
            
        remLooperRef(event.player.getMainHandItem(), event.player.level());
        remLooperRef(event.player.getOffhandItem(), event.player.level());
    }

    private static void remLooperRef(final ItemStack item, final Level level) {
        if (item.getItem() instanceof InstrumentItem)
            LooperBlockEntity.getLBE(level, item);
    }

}
