package com.cstav.evenmoreinstruments.item.partial;

import com.cstav.evenmoreinstruments.client.ModArmPose;
import com.cstav.genshinstrument.event.PosePlayerArmEvent;
import com.cstav.genshinstrument.item.InstrumentItem;
import com.cstav.genshinstrument.networking.OpenInstrumentPacketSender;

public class WindInstrumentItem extends InstrumentItem {

    public WindInstrumentItem(OpenInstrumentPacketSender onOpenRequest) {
        super(onOpenRequest);
    }
    public WindInstrumentItem(OpenInstrumentPacketSender onOpenRequest, Properties properties) {
        super(onOpenRequest, properties);
    }

    @Override
    public void onPosePlayerArm(PosePlayerArmEvent args) {
        ModArmPose.poseWindInstrument(args);
    }
}
