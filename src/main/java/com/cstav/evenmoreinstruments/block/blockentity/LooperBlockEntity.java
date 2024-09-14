package com.cstav.evenmoreinstruments.block.blockentity;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.block.LooperBlock;
import com.cstav.evenmoreinstruments.block.ModBlocks;
import com.cstav.evenmoreinstruments.block.util.WritableNoteType;
import com.cstav.evenmoreinstruments.capability.recording.RecordingCapabilityProvider;
import com.cstav.evenmoreinstruments.gamerule.ModGameRules;
import com.cstav.evenmoreinstruments.item.ModItems;
import com.cstav.evenmoreinstruments.item.component.ModDataComponents;
import com.cstav.evenmoreinstruments.item.emirecord.EMIRecordItem;
import com.cstav.evenmoreinstruments.item.emirecord.RecordRepository;
import com.cstav.evenmoreinstruments.networking.EMIPacketHandler;
import com.cstav.evenmoreinstruments.networking.packet.LooperPlayStatePacket;
import com.cstav.evenmoreinstruments.util.CommonUtil;
import com.cstav.evenmoreinstruments.util.LooperUtil;
import com.cstav.genshinstrument.networking.packet.instrument.NoteSoundMetadata;
import com.cstav.genshinstrument.networking.packet.instrument.util.HeldNoteSoundPacketUtil;
import com.cstav.genshinstrument.networking.packet.instrument.util.HeldSoundPhase;
import com.cstav.genshinstrument.networking.packet.instrument.util.NoteSoundPacketUtil;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.sound.held.HeldNoteSound;
import com.cstav.genshinstrument.sound.held.InitiatorID;
import com.cstav.genshinstrument.sound.registrar.HeldNoteSoundRegistrar;
import com.cstav.genshinstrument.sound.registrar.NoteSoundRegistrar;
import com.cstav.genshinstrument.util.BiValue;
import com.mojang.logging.LogUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.ContainerSingleItem;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.HashSet;
import java.util.Optional;

import static com.cstav.evenmoreinstruments.item.emirecord.BurnedRecordItem.*;

@EventBusSubscriber(bus = Bus.FORGE, modid = EMIMain.MODID)
public class LooperBlockEntity extends BlockEntity implements ContainerSingleItem {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final String
        RECORD_TAG = "Record",
        RECORDING_TAG = "Recording",
    
        TICKS_TAG = "Ticks"
    ;

    private CompoundTag looperData = new CompoundTag();

    private boolean locked = false;
    private Player lockedBy = null;

    private ItemStack recordIn = ItemStack.EMPTY;

    private CompoundTag channel = new CompoundTag();

    private final InitiatorID looperInitiatorID;

    /**
     * A set of cached notes as to use them
     * for pausing and resuming the looper.
     */
    protected final HashSet<BiValue<HeldNoteSound, NoteSoundMetadata>> cachedHeldNotes = new HashSet<>();
    protected void stopAndClearHeldSounds() {
        notifyHeldNotesPhase(HeldSoundPhase.RELEASE);
        cachedHeldNotes.clear();
    }


    public CompoundTag getPersistentData() {
        return looperData;
    }


    /**
     * Retrieves the channel (footage) information from the inserted record
     */
    public CompoundTag getChannel() {
        return channel;
    }

    private void setChannel(final CompoundTag channel) {
        this.channel = channel;
    }

    /**
     * Retrieves the channel (footage) information from the inserted record
     */
    private void updateChannel() {
        if (recordIn.has(ModDataComponents.CHANNNEL.get())) {
            setChannel(recordIn.get(ModDataComponents.CHANNNEL.get()).getUnsafe());
        }
        else if (recordIn.has(ModDataComponents.BURNED_MEDIA.get())) {
            setChannel(RecordRepository.getRecord(getBurnedMediaLoc()).orElse(null));
        }
        else {
            setChannel(new CompoundTag());
        }
    }
    protected ResourceLocation getBurnedMediaLoc() {
        return recordIn.get(ModDataComponents.BURNED_MEDIA.get());
    }

    private void updateRecordNBT() {
        getPersistentData().put(RECORD_TAG, recordIn.save(level.registryAccess()));
    }

    public boolean hasFootage() {
        final CompoundTag channel = getChannel();
        return (channel != null) &&
            channel.contains(NOTES_TAG, Tag.TAG_LIST) && !channel.getList(NOTES_TAG, Tag.TAG_COMPOUND).isEmpty();
    }

    public boolean isWritable() {
        return getChannel().getBoolean(WRITABLE_TAG);
    }
    public void setWritable(final boolean writable) {
        getChannel().putBoolean(WRITABLE_TAG, writable);
    }

    public boolean isRecordIn() {
        return !recordIn.isEmpty();
    }
    protected CompoundTag getRecordData() {
        return channel;
    }

    @Override
    protected void loadAdditional(CompoundTag pTag, Provider pRegistries) {
        super.loadAdditional(pTag, pRegistries);
        // idk how to use 1.20.6 "ForgeCaps". Will resort to the same, usual it exists.
        // Also saves me from making a migration system.
        looperData = pTag.getCompound("ForgeData");

        recordIn = ItemStack.parseOptional(pRegistries, getPersistentData().getCompound(RECORD_TAG));
        updateChannel();
    }

    @Override
    protected void saveAdditional(CompoundTag pTag, Provider pRegistries) {
        super.saveAdditional(pTag, pRegistries);
        pTag.put("ForgeData", looperData);
    }

    //#region ContainerSingleItem implementation

    // Assuming for single container, slots irrelevant:

    //#region ContainerSingleItem implementation

    // Assuming for single container, slots irrelevant:

    @Override
    public @NotNull ItemStack getTheItem() {
        return recordIn;
    }

    @Override
    public void setTheItem(ItemStack pStack) {
        if (!(pStack.getItem() instanceof EMIRecordItem recordItem))
            return;

        recordIn = pStack.copyWithCount(1);
        recordItem.onInsert(recordIn, this);

        updateChannel();

        BlockState newState = getBlockState().setValue(LooperBlock.RECORD_IN, true);
        if (hasFootage())
            newState = setPlaying(true, newState);

        updateRecordNBT();

        getLevel().setBlock(getBlockPos(), newState, 3);
        setChanged();
    }

    @Override
    public ItemStack splitTheItem(int pAmount) {
        if (!isRecordIn() || pAmount <= 0)
            return ItemStack.EMPTY;

        final ItemStack prev = recordIn;
        recordIn = ItemStack.EMPTY;

        getLevel().setBlock(getBlockPos(),
            setPlaying(false, getBlockState())
                .setValue(LooperBlock.RECORD_IN, false),
            3
        );

        getPersistentData().remove(RECORD_TAG);
        reset();

        return prev;
    }

    @Override
    public int getMaxStackSize() {
        return 1;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return !(isLocked() && isLockedBy(pPlayer));
    }

    @Override
    public boolean canPlaceItem(int pIndex, ItemStack pStack) {
        return (pStack.getItem() instanceof EMIRecordItem) && !isRecordIn();
    }

    @Override
    public boolean canTakeItem(Container pTarget, int pIndex, ItemStack pStack) {
        return !isRecordIn();
    }

    //#endregion


    public LooperBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.LOOPER.get(), pPos, pBlockState);
        this.looperInitiatorID = new InitiatorID("block",
            String.format("x%sy%sz%s", pPos.getX(), pPos.getY(), pPos.getZ())
        );

        final CompoundTag data = getPersistentData();

        if (!data.contains(TICKS_TAG, CompoundTag.TAG_INT))
            setTicks(0);
    }
    

    public void setRecording(final boolean recording) {
        getPersistentData().putBoolean(RECORDING_TAG, recording);
    }

    public void setTicks(final int ticks) {
        getPersistentData().putInt(TICKS_TAG, ticks);
    }

    /**
     * Increment the ticks of this looper by 1. Wrap back to the start
     * if the track finished playing.
     * @return The new tick value
     */
    public int incrementTick() {
        int ticks = getTicks();
        final int repTick = getRepeatTick();

        // Finished playing?
        if ((repTick != -1) && (ticks > repTick))
            ticks = onLooperEnd();
        else
            ticks++;

        setTicks(ticks);
        return ticks;
    }

    /**
     * Called after the Looper had finished playing a cycle.
     * Responsible for either replaying or ceasing,
     * depending on the {@link LooperBlock#LOOPING} state.
     *
     * @return The ticks to set the looper at
     */
    public int onLooperEnd() {
        // If we don't loop, disable playing
        if (!getBlockState().getValue(LooperBlock.LOOPING))
            getLevel().setBlockAndUpdate(getBlockPos(), setPlaying(false, getBlockState()));

        stopAndClearHeldSounds();

        return 0;
    }

    public void setRepeatTick(final int tick) {
        getChannel().putInt(REPEAT_TICK_TAG, tick);
    }

    public void setLockedBy(final Player player) {
        lockedBy = player;
    }

    /**
     * Used for stopping the Looper's recording
     */
    public void lock() {
        locked = true;
        lockedBy = null;

        stopAndClearHeldSounds();

        setRepeatTick(getTicks());
        setRecording(false);
        setWritable(false);

        setTicks(0);

        updateRecordNBT();
        setChanged();
    }

    /**
     * This method resets the looper, assuming it is not recording.
     */
    public void reset() {
        locked = false;
        lockedBy = null;

        setTicks(0);

        setChanged();
    }

    public boolean isLocked() {
        return lockedByAnyone() || locked;
    }
    public boolean isRecording() {
        return getPersistentData().getBoolean(RECORDING_TAG);
    }

    public boolean isAllowedToRecord(final Player player) {
        return !lockedByAnyone() || isLockedBy(player);
    }
    public boolean lockedByAnyone() {
        return lockedBy != null;
    }
    public boolean isLockedBy(final Player player) {
        return player.equals(lockedBy);
    }

    public int getTicks() {
        return getPersistentData().getInt(TICKS_TAG);
    }
    public int getRepeatTick() {
        final CompoundTag channel = getChannel();

        if (channel.contains(REPEAT_TICK_TAG))
            return channel.getInt(REPEAT_TICK_TAG);
        else
            return -1;
    }


    /**
     * Updates the playing state of this looper.
     * Ignores {@code isPlaying} to be false on the condition this looper contains no footage.
     * @return The current block state with the set {@code playing} value
     */
    public BlockState setPlaying(final boolean playing, final BlockState state) {
        final boolean isPlaying = hasFootage() && playing;
        final BlockState newState = state.setValue(LooperBlock.PLAYING, isPlaying);

        // If it's the server;
        if (!getLevel().isClientSide) {
            // Update the clients
            getLevel().players().forEach((player) ->
                EMIPacketHandler.sendToClient(new LooperPlayStatePacket(isPlaying, getBlockPos()), (ServerPlayer)player)
            );

            // Cycle held notes
            notifyHeldNotesPhase(playing ? HeldSoundPhase.ATTACK : HeldSoundPhase.RELEASE);
        }

        return newState;
    }

    private void notifyHeldNotesPhase(final HeldSoundPhase phase) {
        cachedHeldNotes.forEach((bi) ->
            HeldNoteSoundPacketUtil.sendPlayNotePackets(
                level,
                bi.obj1(), bi.obj2(),
                phase,
                looperInitiatorID
            )
        );
    }


    /**
     * Writes a new note to the writable record.
     */
    public void writeNote(NoteSound sound, NoteSoundMetadata soundMeta, int timestamp) {
        if (!isWritable())
            return;

        final CompoundTag noteTag = serializeNoteMeta(soundMeta, timestamp);
        noteTag.putString(NOTE_TYPE, WritableNoteType.REGULAR.name());

        noteTag.putInt(SOUND_INDEX_TAG, sound.index);
        noteTag.putString(SOUND_TYPE_TAG, sound.baseSoundLocation.toString());

        CommonUtil.getOrCreateListTag(getChannel(), NOTES_TAG).add(noteTag);
        setChanged();
    }
    /**
     * Writes a new note to the writable record.
     */
    public void writeHeldNote(HeldNoteSound sound, HeldSoundPhase phase,
                              NoteSoundMetadata soundMeta, int timestamp) {
        if (!isWritable())
            return;

        final CompoundTag noteTag = serializeNoteMeta(soundMeta, timestamp);
        noteTag.putString(NOTE_TYPE, WritableNoteType.HELD.name());

        noteTag.putInt(SOUND_INDEX_TAG, sound.index());
        noteTag.putString(SOUND_TYPE_TAG, sound.baseSoundLocation().toString());
        noteTag.putString(HELD_PHASE, phase.name());

        CommonUtil.getOrCreateListTag(getChannel(), NOTES_TAG).add(noteTag);
        setChanged();
    }

    protected CompoundTag serializeNoteMeta(NoteSoundMetadata soundMeta, int timestamp) {
        final CompoundTag noteTag = new CompoundTag();

        noteTag.putInt(PITCH_TAG, soundMeta.pitch());
        noteTag.putFloat(VOLUME_TAG, soundMeta.volume() / 100f);

        noteTag.putInt(TIMESTAMP_TAG, timestamp);

        return noteTag;
    }


    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        // idk why the state isn't updated but here we are
        final LooperBlockEntity lbe = LooperUtil.getFromPos(pLevel, pPos);
        final boolean isPlaying = lbe.getBlockState().getValue(LooperBlock.PLAYING);

        if (!isPlaying && !lbe.isRecording())
            return;

        if (lbe.isRecording())
            lbe.incrementTick();

        if (!isPlaying)
            return;

        final CompoundTag channel = lbe.getChannel();
        if (channel == null)
            return;

        final int ticks = getTicks();
        final ResourceLocation instrumentId = ResourceLocation.parse(channel.getString(INSTRUMENT_ID_TAG));

        channel.getList(NOTES_TAG, Tag.TAG_COMPOUND).stream()
            .map((note) -> (CompoundTag) note)
            .filter((note) -> note.getInt(TIMESTAMP_TAG) == ticks)
            .forEach((note) -> lbe.playNote(note, instrumentId));

        lbe.incrementTick();
    }
    private void playNote(final CompoundTag note, final ResourceLocation instrumentId) {
        try {
            // Acquire note type
            final WritableNoteType noteType;
            final String rawNoteType = note.getString(NOTE_TYPE);

            // Support for older versions
            if (rawNoteType.isEmpty()) {
                noteType = WritableNoteType.REGULAR;
            } else {
                noteType = WritableNoteType.valueOf(rawNoteType);
            }

            switch (noteType) {
                case REGULAR:
                    playNoteSound(note, instrumentId);
                    break;
                case HELD:
                    playHeldSound(note, instrumentId);
                    break;
            }
        } catch (Exception e) {
            LOGGER.error("Attempted to play a looper note at {}, but met with an exception", getBlockPos(), e);
        }
    }

    protected void playNoteSound(final CompoundTag noteTag, final ResourceLocation instrumentId) {
        final NoteSoundMetadata meta = metaFromNoteTag(noteTag, instrumentId);

        final ResourceLocation soundLocation = ResourceLocation.parse(noteTag.getString(SOUND_TYPE_TAG));
        final int soundIndex = noteTag.getInt(SOUND_INDEX_TAG);

        NoteSoundPacketUtil.sendPlayNotePackets(
            level,
            NoteSoundRegistrar.getSounds(soundLocation)[soundIndex],
            meta
        );

        triggerEmitNoteParticle(meta.pitch());
    }
    protected void playHeldSound(final CompoundTag noteTag, final ResourceLocation instrumentId) {
        final NoteSoundMetadata meta = metaFromNoteTag(noteTag, instrumentId);

        final ResourceLocation soundLocation = ResourceLocation.parse(noteTag.getString(SOUND_TYPE_TAG));
        final int soundIndex = noteTag.getInt(SOUND_INDEX_TAG);
        final HeldNoteSound sound = HeldNoteSoundRegistrar.getSounds(soundLocation)[soundIndex];

        final HeldSoundPhase phase = HeldSoundPhase.valueOf(noteTag.getString(HELD_PHASE));

        HeldNoteSoundPacketUtil.sendPlayNotePackets(
            level, sound,
            meta, phase, looperInitiatorID
        );

        if (phase == HeldSoundPhase.ATTACK) {
            cachedHeldNotes.add(new BiValue<>(sound, meta));
            // Also emit particle here
            triggerEmitNoteParticle(meta.pitch());
        } else if (phase == HeldSoundPhase.RELEASE) {
            cachedHeldNotes.remove(new BiValue<>(sound, meta));
        }
    }

    protected NoteSoundMetadata metaFromNoteTag(final CompoundTag noteTag, final ResourceLocation instrumentId) {
        return new NoteSoundMetadata(
            getBlockPos(),
            noteTag.getInt(PITCH_TAG),
            (int)(noteTag.getFloat(VOLUME_TAG) * 100),
            instrumentId, Optional.empty()
        );
    }

    public void triggerEmitNoteParticle(final int pitch) {
        getLevel().blockEvent(getBlockPos(), ModBlocks.LOOPER.get(), 42, pitch);
    }


    public void popRecord() {
        if (recordIn.is(ModItems.RECORD_WRITABLE.get())) {
            // Record ejected while player writing to the record; remove notes
            if (isWritable())
                getRecordData().remove(NOTES_TAG);
            // Empty record; empty data.
            if (!hasFootage())
                recordIn.remove(ModDataComponents.CHANNNEL.get());
        }

        stopAndClearHeldSounds();

        // Finally, pop it
        Vec3 popVec = Vec3.atLowerCornerWithOffset(getBlockPos(), 0.5D, 1.01D, 0.5D)
            .offsetRandom(getLevel().random, 0.7F);

        ItemEntity itementity = new ItemEntity(getLevel(), popVec.x(), popVec.y(), popVec.z(), recordIn);
        itementity.setDefaultPickUpDelay();
        getLevel().addFreshEntity(itementity);

        removeItem(0, 1);
    }


    @Override
    public void setRemoved() {
        super.setRemoved();
        stopAndClearHeldSounds();
    }

    /**
     * A capped looper is a looper that cannot have any more notes in it, as defined in {@link ModGameRules#RULE_LOOPER_MAX_NOTES}.
     * Any negative will make the looper uncappable.
     * @return Whether this looper is capped
     */
    public boolean isCapped(final Level level) {
        final int cap = level.getGameRules().getInt(ModGameRules.RULE_LOOPER_MAX_NOTES);
        return (cap >= 0) && (getChannel().getList(NOTES_TAG, Tag.TAG_COMPOUND).size() >= cap);
    }


    // If the player leaves the world, we shouldn't record anymore
    @SubscribeEvent
    public static void onPlayerLeave(final PlayerLoggedOutEvent event) {
        final Player player = event.getEntity();
        if (!RecordingCapabilityProvider.isRecording(player))
            return;

        player.level()
            .getBlockEntity(RecordingCapabilityProvider.getLooperPos(player), ModBlockEntities.LOOPER.get())
            .filter((lbe) -> lbe.lockedBy.equals(player))
            .ifPresent((lbe) -> {
                lbe.reset();
                lbe.getPersistentData().putBoolean(RECORDING_TAG, false);
            });

        LooperUtil.setNotRecording(player);
    }

}
