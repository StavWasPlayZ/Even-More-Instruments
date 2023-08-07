package com.cstav.evenmoreinstruments.client;

import org.lwjgl.glfw.GLFW;

import com.cstav.evenmoreinstruments.Main;
import com.cstav.genshinstrument.client.keyMaps.InstrumentKeyMappings;
import com.mojang.blaze3d.platform.InputConstants;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.MOD, modid = Main.MODID, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class KeyMappings {
    public static final String CATEGORY = Main.MODID+".keymaps";
    
    public static final Lazy<KeyMapping> VIOLIN_TYPE_MODIFIER = Lazy.of(
        () -> new KeyMapping(CATEGORY+".violin_type_modifier",
            InstrumentKeyMappings.INSTRUMENT_KEY_CONFLICT_CONTEXT,
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_CONTROL
        , CATEGORY)
    );

    @SubscribeEvent
    public static void registerKeybinds(final RegisterKeyMappingsEvent event) {
        event.register(VIOLIN_TYPE_MODIFIER.get());
    }

}
