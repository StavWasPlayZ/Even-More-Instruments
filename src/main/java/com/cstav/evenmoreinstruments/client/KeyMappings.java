package com.cstav.evenmoreinstruments.client;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.genshinstrument.client.keyMaps.InstrumentKeyMappings;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(bus = Bus.MOD, modid = EMIMain.MODID, value = Dist.CLIENT)
public class KeyMappings {
    public static final String CATEGORY = EMIMain.MODID+".keymaps";
    
    public static final Lazy<KeyMapping>
        INSTRUMENT_TYPE_MODIFIER = Lazy.of(
            () -> new KeyMapping(
                CATEGORY+".instrument_type_modifier",
                InstrumentKeyMappings.INSTRUMENT_KEY_CONFLICT_CONTEXT,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_ALT,
                CATEGORY
            )
        ),
        RECORD = Lazy.of(
            () -> new KeyMapping(
                CATEGORY+".record",
                InstrumentKeyMappings.INSTRUMENT_KEY_CONFLICT_CONTEXT,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT,
                CATEGORY
            )
        )
    ;

    @SubscribeEvent
    public static void registerKeybinds(final RegisterKeyMappingsEvent event) {
        event.register(INSTRUMENT_TYPE_MODIFIER.get());
        event.register(RECORD.get());
    }

}
