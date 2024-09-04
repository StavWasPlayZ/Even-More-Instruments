package com.cstav.evenmoreinstruments.client;

import com.cstav.evenmoreinstruments.client.gui.instrument.pipa.PipaSoundType;
import com.cstav.evenmoreinstruments.client.gui.instrument.violin.ViolinSoundType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.Builder;
import net.minecraftforge.common.ForgeConfigSpec.EnumValue;

@OnlyIn(Dist.CLIENT)
public class ModClientConfigs {
    public static final ForgeConfigSpec CONFIGS;

    public static final EnumValue<ViolinSoundType> VIOLIN_SOUND_TYPE;
    public static final EnumValue<PipaSoundType> PIPA_SOUND_TYPE;

    static {
        final ForgeConfigSpec.Builder configBuilder = new Builder();

        VIOLIN_SOUND_TYPE = configBuilder.defineEnum("violin_sound_type", ViolinSoundType.HALF_NOTE);
        PIPA_SOUND_TYPE = configBuilder.defineEnum("pipa_sound_type", PipaSoundType.REGULAR);

        CONFIGS = configBuilder.build();
    }
}
