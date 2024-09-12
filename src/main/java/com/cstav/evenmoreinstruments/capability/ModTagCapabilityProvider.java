package com.cstav.evenmoreinstruments.capability;

import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Migration for "mod tag" of previous versions
 */
public class ModTagCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<ModTagCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    private ModTagCapability cap;
    private final LazyOptional<ModTagCapability> optional = LazyOptional.of(this::getInstance);
    private ModTagCapability getInstance() {
        return (cap == null) ? (cap = new ModTagCapability()) : cap;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return CAPABILITY.orEmpty(cap, optional);
    }

    @Override
    public CompoundTag serializeNBT(Provider registryAccess) {
        return getInstance().getTag();
    }

    @Override
    public void deserializeNBT(Provider registryAccess, CompoundTag nbt) {
        getInstance().setTag(nbt);
    }
}
