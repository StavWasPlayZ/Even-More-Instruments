package com.cstav.evenmoreinstruments.client;

import com.cstav.evenmoreinstruments.Main;
import com.cstav.evenmoreinstruments.client.gui.instrument.violin.ViolinSoundType;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.config.ModConfig.Type;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

@OnlyIn(Dist.CLIENT)
@EventBusSubscriber(bus = Bus.MOD, modid = Main.MODID, value = Dist.CLIENT)
public class ModClientConfigs {
    public static final ForgeConfigSpec CONFIGS;

    public static final EnumValue<ViolinSoundType> VIOLIN_SOUND_TYPE;

    static {
        final ForgeConfigSpec.Builder configBuilder = new Builder();

        VIOLIN_SOUND_TYPE = configBuilder.defineEnum("violin_sound_type", ViolinSoundType.HALF_NOTE);

        CONFIGS = configBuilder.build();
    }


    @SubscribeEvent
    public static void registerConfigs(final FMLConstructModEvent event) {
        ModLoadingContext.get().registerConfig(Type.CLIENT, CONFIGS, "evenmore_instrument_configs.toml");
    }
}
