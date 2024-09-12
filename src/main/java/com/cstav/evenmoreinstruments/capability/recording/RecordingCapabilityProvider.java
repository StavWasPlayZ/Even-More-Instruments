package com.cstav.evenmoreinstruments.capability.recording;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class RecordingCapabilityProvider implements ICapabilityProvider, INBTSerializable<CompoundTag> {
    public static final Capability<RecordingCapability> CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});

    private RecordingCapability cap;
    private final LazyOptional<RecordingCapability> optional = LazyOptional.of(this::getInstance);
    private RecordingCapability getInstance() {
        return (cap == null) ? (cap = new RecordingCapability()) : cap;
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return CAPABILITY.orEmpty(cap, optional);
    }

    @Override
    public CompoundTag serializeNBT(Provider registryAccess) {
        final CompoundTag nbt = new CompoundTag();
        getInstance().saveNBTData(nbt);
        return nbt;
    }

    @Override
    public void deserializeNBT(Provider registryAccess, CompoundTag nbt) {
        getInstance().loadNBTData(nbt);
    }


    public static boolean isRecording(final Player player) {
        return getProp(player, RecordingCapability::isRecording, false);
    }
    public static BlockPos getLooperPos(final Player player) {
        return getProp(player, RecordingCapability::getLooperPos, null);
    }

    public static void setRecording(final Player player, final BlockPos looperPos) {
        player.getCapability(CAPABILITY).ifPresent((cap) ->
            cap.setRecording(looperPos)
        );
    }
    public static void setNotRecording(final Player player) {
        player.getCapability(CAPABILITY).ifPresent(RecordingCapability::setNotRecording);
    }


    private static <T> T getProp(Player player, Function<RecordingCapability, T> ifExists, T elseVal) {
        final LazyOptional<RecordingCapability> lazyOpen = player.getCapability(RecordingCapabilityProvider.CAPABILITY);

        return lazyOpen.isPresent()
            ? ifExists.apply(lazyOpen.resolve().get())
            : elseVal;
    }
}
