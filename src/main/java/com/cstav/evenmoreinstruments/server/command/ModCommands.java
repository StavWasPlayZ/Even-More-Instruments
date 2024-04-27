package com.cstav.evenmoreinstruments.server.command;

import com.cstav.evenmoreinstruments.EMIMain;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.FORGE, modid = EMIMain.MODID)
public class ModCommands {

    @SubscribeEvent
    public static void onCommandsRegister(final RegisterCommandsEvent event) {
        EMIRecordCommand.register(event.getDispatcher());
    }

}
