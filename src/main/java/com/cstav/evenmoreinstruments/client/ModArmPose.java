package com.cstav.evenmoreinstruments.client;

import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModArmPose {
    public static void register() {}

    public static final ArmPose PLAYING_WIND_INSTRUMENT = ArmPose.create("playing_trombone_instrument", true,
        (model, entity, arm) -> {
            model.rightArm.xRot = -1.5f;
            model.rightArm.zRot = -0.35f;
            model.rightArm.yRot = -0.5f;

            model.leftArm.xRot = -1.5f;
            model.leftArm.zRot = 0.55f;
            model.leftArm.yRot = 0.5f;
        }
    );
}
