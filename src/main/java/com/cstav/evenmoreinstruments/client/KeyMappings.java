package com.cstav.evenmoreinstruments.client;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.genshinstrument.client.keyMaps.InstrumentKeyMappings;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.ClientRegistry;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(bus = Bus.MOD, modid = EMIMain.MODID, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class KeyMappings {
    public static final String CATEGORY = EMIMain.MODID+".keymaps";
    
    public static final Lazy<KeyMapping>
        INSTRUMENT_TYPE_MODIFIER = Lazy.of(
            () -> new KeyMapping(CATEGORY+".instrument_type_modifier",
                InstrumentKeyMappings.INSTRUMENT_KEY_CONFLICT_CONTEXT,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_ALT
            , CATEGORY)
        ),
        RECORD = Lazy.of(
            () -> new KeyMapping(CATEGORY+".record",
                InstrumentKeyMappings.INSTRUMENT_KEY_CONFLICT_CONTEXT,
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT
            , CATEGORY)
        );

    public static void registerKeybinds() {
        ClientRegistry.registerKeyBinding(INSTRUMENT_TYPE_MODIFIER.get());
        ClientRegistry.registerKeyBinding(RECORD.get());
    }

}
