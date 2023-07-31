package com.cstav.evenmoreinstruments.client;

import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.IArmPoseTransformer;

@OnlyIn(Dist.CLIENT)
public class ModArmPose {
    public static void register() {}

    public static final ArmPose PLAYING_TROMBONE = ArmPose.create("playing_trombone_instrument", true, new IArmPoseTransformer() {

        @Override
        public void applyTransform(HumanoidModel<?> model, LivingEntity entity, HumanoidArm arm) {
            model.rightArm.xRot = -1.5f;
            model.rightArm.zRot = -0.35f;
            model.rightArm.yRot = -0.5f;
            
            model.leftArm.xRot = -1.5f;
            model.leftArm.zRot = 0.55f;
            model.leftArm.yRot = 0.5f;
        }
        
    });
}
