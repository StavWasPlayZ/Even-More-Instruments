package com.cstav.evenmoreinstruments.capability;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.capability.recording.RecordingCapabilityProvider;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.FORGE, modid = EMIMain.MODID)
public class ModCapabilities {

    @SubscribeEvent
    public static void registerCapabilities(final AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {

            if (!event.getObject().getCapability(RecordingCapabilityProvider.CAPABILITY).isPresent())
                event.addCapability(new ResourceLocation(EMIMain.MODID, "emi_caps"), new RecordingCapabilityProvider());

        }
    }

}
