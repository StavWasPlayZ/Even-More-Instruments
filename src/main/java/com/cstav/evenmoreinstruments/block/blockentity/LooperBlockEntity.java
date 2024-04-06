package com.cstav.evenmoreinstruments.block.blockentity;

import java.util.UUID;

import com.cstav.evenmoreinstruments.capability.recording.RecordingCapabilityProvider;
import com.cstav.evenmoreinstruments.item.ModItems;
import com.cstav.evenmoreinstruments.item.partial.emirecord.EMIRecordItem;
import com.cstav.evenmoreinstruments.item.partial.emirecord.RecordRepository;
import com.cstav.evenmoreinstruments.networking.ModPacketHandler;
import com.cstav.evenmoreinstruments.networking.packet.LooperPlayStatePacket;
import net.minecraft.client.telemetry.events.WorldUnloadEvent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.ticks.ContainerSingleItem;
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
public class LooperBlockEntity extends BlockEntity implements ContainerSingleItem {
    private static final Logger LOGGER = LogUtils.getLogger();
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

        if (recordData.contains("channel", Tag.TAG_COMPOUND)) {
            channel = recordData.getCompound("channel");
        }
        else if (recordData.contains("burned_media", Tag.TAG_STRING)) {
            final ResourceLocation recLoc = getBurnedMediaLoc();
            RecordRepository.consumeRecord(getBlockPos(), recLoc, this::setChannel);
        }
    }
    protected ResourceLocation getBurnedMediaLoc() {
        return new ResourceLocation(recordIn.getTag().getString("burned_media"));
    }

    private void unloadRecord() {
        final ResourceLocation burnedMedia = getBurnedMediaLoc();
        if (burnedMedia != null) {
            RecordRepository.removeSub(getBlockPos(), burnedMedia);
        }
    }

    private void updateRecordNBT() {
        getPersistentData().put("record", recordIn.save(new CompoundTag()));
    }

    public boolean hasFootage() {
        final CompoundTag channel = getChannel();
        return (channel != null) &&
            channel.contains("notes", Tag.TAG_LIST) && !channel.getList("notes", Tag.TAG_COMPOUND).isEmpty();
    }

    public boolean isWritable() {
        return (getChannel() != null) && getChannel().getBoolean("writable");
    }
    public void setWritable(final boolean writable) {
        getChannel().putBoolean("writable", writable);
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
        recordIn = ItemStack.of(getPersistentData().getCompound("record"));
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

        getPersistentData().remove("record");
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

        if (!data.contains("ticks", CompoundTag.TAG_INT))
            setTicks(0);
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
        getChannel().putInt("repeatTick", tick);
    }

    public void setLockedBy(final UUID player) {
        lockedBy = player;
    }

    public void lock() {
        getPersistentData().putBoolean("locked", true);
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
        getPersistentData().remove("locked");
        getPersistentData().remove("lockedBy");
        lockedBy = null;

        setTicks(0);

        setChanged();
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
        final CompoundTag channel = getChannel();

        if (channel.contains("repeatTick"))
            return channel.getInt("repeatTick");
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

        noteTag.putInt("soundIndex", sound.index);
        noteTag.putString("soundType", sound.baseSoundLocation.toString());

        noteTag.putInt("pitch", pitch);
        noteTag.putFloat("volume", volume / 100f);

        noteTag.putInt("timestamp", timestamp);


        CommonUtil.getOrCreateListTag(getChannel(), "notes").add(noteTag);
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
        final ResourceLocation instrumentId = new ResourceLocation(channel.getString("instrumentId"));

        channel.getList("notes", Tag.TAG_COMPOUND).stream()
            .map((note) -> (CompoundTag) note)
            .filter((note) -> note.getInt("timestamp") == ticks)
            .forEach((note) -> lbe.playNote(note, instrumentId));

        lbe.incrementTick();
    }
    private void playNote(final CompoundTag note, final ResourceLocation instrumentId) {
        try {
            final int pitch = note.getInt("pitch");
            final float volume = note.getFloat("volume");

            final ResourceLocation soundLocation = new ResourceLocation(note.getString("soundType"));

            ServerUtil.sendPlayNotePackets(getLevel(), getBlockPos(),
                NoteSoundRegistrar.getSounds(soundLocation)[note.getInt("soundIndex")],
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
                recordData.remove("notes");
            // Empty record; empty data.
            if (!hasFootage())
                recordData.remove("channel");
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
            looperBE.getChannel().putString("instrumentId", event.instrumentId.toString());
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
