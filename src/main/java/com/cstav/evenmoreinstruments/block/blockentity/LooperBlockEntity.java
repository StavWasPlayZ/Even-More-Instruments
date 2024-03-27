package com.cstav.evenmoreinstruments.block.blockentity;

import java.util.UUID;

import com.cstav.evenmoreinstruments.capability.recording.RecordingCapabilityProvider;
import net.minecraft.world.entity.player.Player;
import org.slf4j.Logger;

import com.cstav.evenmoreinstruments.Main;
import com.cstav.evenmoreinstruments.block.LooperBlock;
import com.cstav.evenmoreinstruments.block.ModBlocks;
import com.cstav.evenmoreinstruments.gamerule.ModGameRules;
import com.cstav.evenmoreinstruments.util.CommonUtil;
import com.cstav.evenmoreinstruments.util.LooperUtil;
import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.NoteSoundRegistrar;
import com.cstav.genshinstrument.util.ServerUtil;
import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.FORGE, modid = Main.MODID)
public class LooperBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private UUID lockedBy;

    public CompoundTag getChannel() {
        return getChannel(getPersistentData());
    }
    public CompoundTag getChannel(final CompoundTag data) {
        return CommonUtil.getOrCreateElementTag(data, "channel");
    }

    public boolean hasFootage() {
        return getPersistentData().contains("channel");
    }

    public void removeRecordData() {
        getPersistentData().remove("record");
    }
    public void setRecordData(final CompoundTag recordData) {
        getPersistentData().put("record", recordData);
    }


    public LooperBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.LOOPER.get(), pPos, pBlockState);

        final CompoundTag data = getPersistentData();

        // Construct all the data stuff
        if (!data.contains("ticks", CompoundTag.TAG_INT))
            setTicks(0);
        if (!data.contains("repeatTick", CompoundTag.TAG_INT))
            setRepeatTick(-1);
    }
    

    public void setRecording(final boolean recording) {
        getPersistentData().putBoolean("recording", recording);
    }

    public void setTicks(final int ticks) {
        getPersistentData().putInt("ticks", ticks);
    }
    /**
     * Increment the ticks of this looper by 1. Wrap back to the start
     * if the track finished playing.
     * @return The new tick value
     */
    public int incrementTick() {
        int ticks = getTicks();

        // Wrap back to the start when we finished playing
        final int repTick = getRepeatTick();
        if ((repTick != -1) && (ticks > repTick))
            ticks = 0;
        else
            ticks++;

        setTicks(ticks);
        setChanged();

        return ticks;
    }
    public void setRepeatTick(final int tick) {
        getPersistentData().putInt("repeatTick", tick);
    }

    public void setLockedBy(final UUID player) {
        lockedBy = player;
    }

    public void lock() {
        getPersistentData().putBoolean("locked", true);
        lockedBy = null;

        setRepeatTick(getTicks());
        setRecording(false);

        setChanged();
    }
    //TODO implement to Looper GUI
    /**
     * This method resets the looper, assuming it is not recording.
     */
    public void reset() {
        getPersistentData().remove("locked");
        getPersistentData().remove("lockedBy");
        lockedBy = null;

        setRepeatTick(-1);
        setTicks(0);

        getPersistentData().remove("channel");
    }

    public boolean isLocked() {
        return lockedByAnyone() || getPersistentData().getBoolean("locked");
    }
    public boolean isRecording() {
        return getPersistentData().getBoolean("recording");
    }

    public boolean isAllowedToRecord(final UUID playerUUID) {
        return !lockedByAnyone() || isLockedBy(playerUUID);
    }
    public boolean lockedByAnyone() {
        return lockedBy != null;
    }
    public boolean isLockedBy(final UUID playerUUID) {
        return playerUUID.equals(lockedBy);
    }

    public int getTicks() {
        return getPersistentData().getInt("ticks");
    }
    public int getRepeatTick() {
        return getPersistentData().getInt("repeatTick");
    }


    public void addNote(NoteSound sound, int pitch, int volume, int timestamp) {
        final CompoundTag channel = getChannel();
        final CompoundTag noteTag = new CompoundTag();


        noteTag.putInt("soundIndex", sound.index);
        noteTag.putString("soundType", sound.baseSoundLocation.toString());

        noteTag.putInt("pitch", pitch);
        noteTag.putFloat("volume", volume / 100f);

        noteTag.putInt("timestamp", timestamp);


        CommonUtil.getOrCreateListTag(channel, "notes").add(noteTag);
        setChanged();
    }


    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        // idk why but it needs to be here for it to work
        final LooperBlockEntity lbe = LooperUtil.getFromPos(pLevel, pPos);
        final boolean isPlaying = lbe.getBlockState().getValue(LooperBlock.PLAYING);

        if (!isPlaying && !lbe.isRecording())
            return;

        final int ticks = lbe.incrementTick();

        if (!isPlaying)
            return;


        final CompoundTag channel = lbe.getChannel();
        final ResourceLocation instrumentId = new ResourceLocation(channel.getString("instrumentId"));

        for (final Tag pNote : channel.getList("notes", Tag.TAG_COMPOUND)) {
            if (!(pNote instanceof CompoundTag note))
                continue;

            if (ticks != note.getInt("timestamp"))
                continue;

            try {

                final int pitch = note.getInt("pitch");
                final float volume = note.getFloat("volume");

                final ResourceLocation soundLocation = new ResourceLocation(note.getString("soundType"));
                
                ServerUtil.sendPlayNotePackets(pLevel, pPos,
                    NoteSoundRegistrar.getSounds(soundLocation)[note.getInt("soundIndex")],
                    instrumentId, pitch, (int)(volume * 100)
                );

                pLevel.blockEvent(pPos, ModBlocks.LOOPER.get(), 42, pitch);

            } catch (Exception e) {
                LOGGER.error("Attempted to play a looper note, but met with an exception", e);
            }
        }
    }



    @SubscribeEvent
    public static void onInstrumentPlayed(final InstrumentPlayedEvent.ByPlayer event) {
        if (event.isClientSide || !LooperUtil.isRecording(event.player))
            return;

        final Level level = event.player.level();
            
        final LooperBlockEntity looperBE = LooperUtil.getFromEvent(event);
        if (looperBE == null)
            return;

        if (looperBE.isCapped(level))
            return;


        if (looperBE.isLocked()) {
            if (!looperBE.isRecording() || !looperBE.isAllowedToRecord(event.player.getUUID()))
                return;
        } else {
            looperBE.setLockedBy(event.player.getUUID());
            looperBE.setRecording(true);
            looperBE.getChannel().putString("instrumentId", event.instrumentId.toString());
        }
            
        looperBE.addNote(event.sound, event.pitch, event.volume, looperBE.getTicks());
        looperBE.setChanged();
    }

    /**
     * A capped looper is a looper that cannot have any more notes in it, as defined in {@link ModGameRules#RULE_LOOPER_MAX_NOTES}.
     * Any negative will make the looper uncappable.
     * @return Whether this looper is capped
     */
    public boolean isCapped(final Level level) {
        final int cap = level.getGameRules().getInt(ModGameRules.RULE_LOOPER_MAX_NOTES);
        return (cap >= 0) && (getChannel().getList("notes", Tag.TAG_COMPOUND).size() >= cap);
    }


    // If the player leaves the world, we shouldn't record anymore
    @SubscribeEvent
    public static void onPlayerLeave(final PlayerLoggedOutEvent event) {
        final Player player = event.getEntity();
        if (!RecordingCapabilityProvider.isRecording(player))
            return;

        event.getEntity().level()
            .getBlockEntity(RecordingCapabilityProvider.getLooperPos(player), ModBlockEntities.LOOPER.get())
            .filter((lbe) -> lbe.lockedBy.equals(event.getEntity().getUUID()))
            .ifPresent((lbe) -> {
                    lbe.reset();
                    lbe.getPersistentData().putBoolean("recording", false);
                }
            );

        LooperUtil.setNotRecording(player);
    }

}
