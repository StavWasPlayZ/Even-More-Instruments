package com.cstav.evenmoreinstruments.capability;

import net.minecraft.nbt.CompoundTag;

/**
 * Wrapper for the mod tag compound.
 */
public class ModTagCapability {
    private CompoundTag tag = new CompoundTag();

    public CompoundTag getTag() {
        return tag;
    }
    public void setTag(CompoundTag tag) {
        this.tag = tag;
    }
}
