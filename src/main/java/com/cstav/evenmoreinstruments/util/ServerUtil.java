package com.cstav.evenmoreinstruments.util;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;

public abstract class ServerUtil {

    // According to Forge
    @SuppressWarnings("deprecation")
    public static boolean isMaliciousPos(final Player player, final CompoundTag looperTag) {
        final BlockPos looperPos = LooperUtil.getLooperPos(looperTag);
        return !player.level().hasChunkAt(looperPos);
    }


}
