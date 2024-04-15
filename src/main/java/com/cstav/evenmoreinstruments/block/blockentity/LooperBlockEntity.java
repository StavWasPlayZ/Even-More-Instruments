package com.cstav.evenmoreinstruments.block.blockentity;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.block.LooperBlock;
import com.cstav.evenmoreinstruments.block.ModBlocks;
import com.cstav.evenmoreinstruments.capability.recording.RecordingCapabilityProvider;
import com.cstav.evenmoreinstruments.gamerule.ModGameRules;
import com.cstav.evenmoreinstruments.item.ModItems;
import com.cstav.evenmoreinstruments.item.partial.emirecord.EMIRecordItem;
import com.cstav.evenmoreinstruments.item.partial.emirecord.RecordRepository;
import com.cstav.evenmoreinstruments.networking.ModPacketHandler;
import com.cstav.evenmoreinstruments.networking.packet.LooperPlayStatePacket;
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
import org.slf4j.Logger;

import java.util.UUID;

import static com.cstav.evenmoreinstruments.item.partial.emirecord.EMIRecordItem.*;
import static com.cstav.evenmoreinstruments.item.partial.emirecord.WritableRecordItem.NOTES_TAG;
import static com.cstav.evenmoreinstruments.item.partial.emirecord.WritableRecordItem.WRITABLE_TAG;

@EventBusSubscriber(bus = Bus.FORGE, modid = EMIMain.MODID)
public class LooperBlockEntity extends BlockEntity implements ContainerSingleItem {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final String
        BURNED_MEDIA_TAG = "BurnedMedia",
        CHANNEL_TAG = "Channel",
    
        RECORD_TAG = "Record",
        RECORDING_TAG = "Recording",
    
        TICKS_TAG = "Ticks",
        REPEAT_TICK_TAG = "RepeatTick",
    
        LOCKED_TAG = "Locked",
        LOCKED_BY_TAG = "LockedBy"
    ;

    private UUID lockedBy;
    private ItemStack recordIn = ItemStack.EMPTY;

    private CompoundTag channel;


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
        final CompoundTag recordData = recordIn.getOrCreateTag();

        if (recordData.contains(CHANNEL_TAG, Tag.TAG_COMPOUND)) {
            channel = recordData.getCompound(CHANNEL_TAG);
        }
        else if (recordData.contains(BURNED_MEDIA_TAG, Tag.TAG_STRING)) {
            final ResourceLocation recLoc = getBurnedMediaLoc();
            RecordRepository.consumeRecord(getBlockPos(), recLoc, this::setChannel);
        }
    }
    protected ResourceLocation getBurnedMediaLoc() {
        return new ResourceLocation(recordIn.getTag().getString(BURNED_MEDIA_TAG));
    }

    private void unloadRecord() {
        final ResourceLocation burnedMedia = getBurnedMediaLoc();
        if (burnedMedia != null) {
            RecordRepository.removeSub(getBlockPos(), burnedMedia);
        }
    }

    private void updateRecordNBT() {
        getPersistentData().put(RECORD_TAG, recordIn.save(new CompoundTag()));
    }

    public boolean hasFootage() {
        final CompoundTag channel = getChannel();
        return (channel != null) &&
            channel.contains(NOTES_TAG, Tag.TAG_LIST) && !channel.getList(NOTES_TAG, Tag.TAG_COMPOUND).isEmpty();
    }

    public boolean isWritable() {
        return (getChannel() != null) && getChannel().getBoolean(WRITABLE_TAG);
    }
    public void setWritable(final boolean writable) {
        getChannel().putBoolean(WRITABLE_TAG, writable);
    }

    public boolean isRecordIn() {
        return !recordIn.isEmpty();
    }
    protected CompoundTag getRecordData() {
        return recordIn.getOrCreateTag();
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        recordIn = ItemStack.of(getPersistentData().getCompound(RECORD_TAG));
        updateChannel();
    }

    //#region ContainerSingleItem implementation

    // Assuming for single container, slots irrelevant:

    @Override
    public ItemStack getItem(int pSlot) {
        return recordIn;
    }

    @Override
    public void setItem(int pSlot, ItemStack pStack) {
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
    public ItemStack removeItem(int pSlot, int pAmount) {
        if (!isRecordIn() || pAmount <= 0)
            return ItemStack.EMPTY;

        unloadRecord();

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
        return Container.stillValidBlockEntity(this, pPlayer);
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
        // Finished playing
        if ((repTick != -1) && (ticks > repTick)) {
            // Wrap back to the start
            ticks = 0;
            // If we don't loop, disable playing
            if (!getBlockState().getValue(LooperBlock.LOOPING))
                getLevel().setBlockAndUpdate(getBlockPos(), setPlaying(false, getBlockState()));
        } else
            ticks++;

        setTicks(ticks);
        return ticks;
    }
    public void setRepeatTick(final int tick) {
        getChannel().putInt(REPEAT_TICK_TAG, tick);
    }

    public void setLockedBy(final UUID player) {
        lockedBy = player;
    }

    public void lock() {
        getPersistentData().putBoolean(LOCKED_TAG, true);
        lockedBy = null;

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
        getPersistentData().remove(LOCKED_TAG);
        getPersistentData().remove(LOCKED_BY_TAG);
        lockedBy = null;

        setTicks(0);

        setChanged();
    }

    public boolean isLocked() {
        return lockedByAnyone() || getPersistentData().getBoolean(LOCKED_TAG);
    }
    public boolean isRecording() {
        return getPersistentData().getBoolean(RECORDING_TAG);
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

        if (!getLevel().isClientSide)
            getLevel().players().forEach((player) ->
                ModPacketHandler.sendToClient(new LooperPlayStatePacket(isPlaying, getBlockPos()), (ServerPlayer)player)
            );

        return newState;
    }


    /**
     * Writes a new note to the writable record.
     */
    public void writeNote(NoteSound sound, int pitch, int volume, int timestamp) {
        if (!isWritable())
            return;

        final CompoundTag noteTag = new CompoundTag();

        noteTag.putInt(SOUND_INDEX_TAG, sound.index);
        noteTag.putString(SOUND_TYPE_TAG, sound.baseSoundLocation.toString());

        noteTag.putInt(PITCH_TAG, pitch);
        noteTag.putFloat(VOLUME_TAG, volume / 100f);

        noteTag.putInt(TIMESTAMP_TAG, timestamp);


        CommonUtil.getOrCreateListTag(getChannel(), NOTES_TAG).add(noteTag);
        setChanged();
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
        final ResourceLocation instrumentId = new ResourceLocation(channel.getString(INSTRUMENT_ID_TAG));

        channel.getList(NOTES_TAG, Tag.TAG_COMPOUND).stream()
            .map((note) -> (CompoundTag) note)
            .filter((note) -> note.getInt(TIMESTAMP_TAG) == ticks)
            .forEach((note) -> lbe.playNote(note, instrumentId));

        lbe.incrementTick();
    }
    private void playNote(final CompoundTag note, final ResourceLocation instrumentId) {
        try {
            final int pitch = note.getInt(PITCH_TAG);
            final float volume = note.getFloat(VOLUME_TAG);

            final ResourceLocation soundLocation = new ResourceLocation(note.getString(SOUND_TYPE_TAG));

            ServerUtil.sendPlayNotePackets(getLevel(), getBlockPos(),
                NoteSoundRegistrar.getSounds(soundLocation)[note.getInt(SOUND_INDEX_TAG)],
                instrumentId, pitch, (int)(volume * 100)
            );

            getLevel().blockEvent(getBlockPos(), ModBlocks.LOOPER.get(), 42, pitch);
        } catch (Exception e) {
            LOGGER.error("Attempted to play a looper note, but met with an exception", e);
        }
    }


    public void popRecord() {
        final CompoundTag recordData = getRecordData();

        if (recordIn.is(ModItems.RECORD_WRITABLE.get())) {
            // Record ejected while player writing to the record; remove notes
            if (isWritable())
                recordData.remove(NOTES_TAG);
            // Empty record; empty data.
            if (!hasFootage())
                recordData.remove(CHANNEL_TAG);
        }

        Vec3 popVec = Vec3.atLowerCornerWithOffset(getBlockPos(), 0.5D, 1.01D, 0.5D)
            .offsetRandom(getLevel().random, 0.7F);

        ItemEntity itementity = new ItemEntity(getLevel(), popVec.x(), popVec.y(), popVec.z(), recordIn);
        itementity.setDefaultPickUpDelay();
        getLevel().addFreshEntity(itementity);

        removeItem(0, 1);
    }



    /**
     * Writes the note to the record as described by the event.
     */
    @SubscribeEvent
    public static void onInstrumentPlayed(final InstrumentPlayedEvent.ByPlayer event) {
        if (event.isClientSide || !LooperUtil.isRecording(event.player))
            return;

        final Level level = event.player.level();
            
        final LooperBlockEntity looperBE = LooperUtil.getFromEvent(event);
        if (looperBE == null || looperBE.isCapped(level))
            return;

        // Omit if record is not writable
        if (!looperBE.isWritable())
            return;


        if (looperBE.isLocked()) {
            if (!looperBE.isRecording() || !looperBE.isAllowedToRecord(event.player.getUUID()))
                return;
        } else {
            looperBE.setLockedBy(event.player.getUUID());
            looperBE.setRecording(true);
            looperBE.getChannel().putString(INSTRUMENT_ID_TAG, event.instrumentId.toString());
        }
            
        looperBE.writeNote(event.sound, event.pitch, event.volume, looperBE.getTicks());
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

        event.getEntity().level()
            .getBlockEntity(RecordingCapabilityProvider.getLooperPos(player), ModBlockEntities.LOOPER.get())
            .filter((lbe) -> lbe.lockedBy.equals(event.getEntity().getUUID()))
            .ifPresent((lbe) -> {
                    lbe.reset();
                    lbe.getPersistentData().putBoolean(RECORDING_TAG, false);
                }
            );

        LooperUtil.setNotRecording(player);
    }

}
