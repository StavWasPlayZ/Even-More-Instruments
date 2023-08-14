package com.cstav.evenmoreinstruments.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.cstav.evenmoreinstruments.block.LooperBlock;
import com.cstav.evenmoreinstruments.block.ModBlocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Parrot;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

@Mixin(Parrot.class)
public abstract class ParrotLooperDanceInjector extends Entity {

    public ParrotLooperDanceInjector(EntityType<?> pEntityType, Level pLevel) {
        super(pEntityType, pLevel);
    }


    @Shadow
    private BlockPos jukebox;
    @Shadow
    private boolean partyParrot;

    @Unique
    private BlockPos jukeboxBefore;
    @Unique
    private boolean partiedBefore;


    @Inject(at = @At(value = "HEAD"), method = "aiStep()V")
    private void aiStepHead(final CallbackInfo info) {
        jukeboxBefore = jukebox;
    }

    @Inject(at = @At(value = "TAIL"), method = "aiStep()V")
    private void aiStepTail(final CallbackInfo info) {
        if (jukeboxBefore == null)
            return;

        final BlockState state = level().getBlockState(jukeboxBefore);

        if (jukeboxBefore.closerToCenterThan(position(), 3.46) && state.is(ModBlocks.LOOPER.get()) && state.getValue(LooperBlock.PLAYING)) {
            partyParrot = true;
            jukebox = jukeboxBefore;
        } else {
            jukebox = null;
            partyParrot = false;
        }
    }

    
    
}
