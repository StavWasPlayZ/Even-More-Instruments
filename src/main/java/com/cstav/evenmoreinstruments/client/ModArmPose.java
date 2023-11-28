package com.cstav.evenmoreinstruments.client;

import com.cstav.genshinstrument.event.PosePlayerArmEvent;
import com.cstav.genshinstrument.event.PosePlayerArmEvent.HandType;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModArmPose {

    public static void poseWindInstrument(final PosePlayerArmEvent event) {
        if (event.hand == HandType.RIGHT) {
            event.arm.xRot = -1.5f;
            event.arm.zRot = -0.35f;
            event.arm.yRot = -0.5f;
        } else {
            event.arm.xRot = -1.5f;
            event.arm.zRot = 0.55f;
            event.arm.yRot = 0.5f;
        }

        event.setCanceled(true);
    }

}
