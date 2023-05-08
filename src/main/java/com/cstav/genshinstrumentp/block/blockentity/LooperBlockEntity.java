package com.cstav.genshinstrumentp.block.blockentity;

import java.util.Optional;

import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrumentp.Main;
import com.cstav.genshinstrumentp.Util;
import com.cstav.genshinstrumentp.block.LooperBlock;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.FORGE, modid = Main.MODID)
public class LooperBlockEntity extends BlockEntity {

    /**
     * <p>The channels list is constructed so that every entry represents a layer
     * of sounds to be played back ny the looper.</p>
     * That means that this is a 2D list, where each list inside it is a singular channel
     * containing a list of notes and their respected play timestamp in ticks.
     * @return The channels list of this looper entity block
     */
    public ListTag getChannels() {
        return getChannels(getPersistentData());
    }
    public ListTag getChannels(final CompoundTag data) {
        return Util.getOrCreateListTag(data, "channels");
    }


    public LooperBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.LOOPER.get(), pPos, pBlockState);

        final CompoundTag data = getPersistentData();

        // Construct all the data stuff
        if (!data.contains("channels", CompoundTag.TAG_LIST))
            getChannels().add(new ListTag());

        if (!data.contains("playing", CompoundTag.TAG_BYTE))
            setPlaying(false);
        if (!data.contains("isRecording", CompoundTag.TAG_BYTE))
            setRecording(false);

        if (!data.contains("ticks", CompoundTag.TAG_INT))
            setTicks(0);
        if (!data.contains("repeatTick", CompoundTag.TAG_INT))
            setRepeatTick(-1);
    }
    

    public void setPlaying(final boolean playing) {
        getPersistentData().putBoolean("playing", playing);
    }
    public void setRecording(final boolean recording) {
        getPersistentData().putBoolean("recording", recording);
    }
    public void setTicks(final int ticks) {
        getPersistentData().putInt("ticks", ticks);
    }
    public void setRepeatTick(final int tick) {
        getPersistentData().putInt("repeatTick", tick);
    }

    public boolean isPlaying() {
        return getPersistentData().getBoolean("playing");
    }
    public boolean isRecording() {
        return getPersistentData().getBoolean("recording");
    }
    public int getTicks() {
        return getPersistentData().getInt("ticks");
    }
    public int getRepeatTick() {
        return getPersistentData().getInt("repeatTick");
    }


    protected void addNote(final NoteSound sound, final ItemStack instrument, int channel, int timestamp) {
        final ListTag channels = getChannels();
        if (channels.size() < channel)
            return;

        final CompoundTag noteTag = new CompoundTag();
        noteTag.putFloat("pitch", sound.getPitch());
        noteTag.putString("mono", sound.mono.getLocation().toString());
        if (sound.hasStereo())
            noteTag.putString("stereo", sound.stereo.getLocation().toString());
        noteTag.putInt("timestamp", timestamp);


        channels.getList(channel).add(noteTag);
    }


    public void tick(Level pLevel, BlockPos pPos, BlockState pState) {
        if (!isPlaying() && !isRecording())
            return;

        setTicks(getTicks() + 1);
        if ((getRepeatTick() != -1) && (getTicks() > getRepeatTick()))
            setTicks(0);
    }


    /**
     * Attempts to get the looper pointed out by {@code instrument}. Removes its reference if not found.
     * @param level The level to get the BE from
     * @param instrument The subject instrument to get the looper data from
     * @return The Looper's block entity as pointed in the {@code instrument}'s data.
     * Null if not found
     */
    public static LooperBlockEntity getLBE(final Level level, final ItemStack instrument) {
        final LooperBlockEntity looperBE = getLBE(level, LooperBlock.getLooperPos(instrument));

        if (looperBE == null)
            LooperBlock.remLooperTag(instrument);

        return looperBE;
    }
    public static LooperBlockEntity getLBE(final Level level, final BlockPos pos) {
        final Optional<LooperBlockEntity> opLooperBE =
            level.getBlockEntity(pos, ModBlockEntities.LOOPER.get());

        return opLooperBE.isPresent() ? opLooperBE.get() : null;
    }

    @SubscribeEvent
    public static void onInstrumentPlayed(final InstrumentPlayedEvent event) {
        //TODO replace with event.hand once exists
        final ItemStack instrument = event.player.getItemInHand(Util.getInstrumentHand(event.player));
        if (!LooperBlock.isRecording(instrument))
            return;
            
            
        final LooperBlockEntity looperBE = getLBE(event.player.getLevel(), instrument);
        if (looperBE == null)
            return;
            

        looperBE.setRecording(true);
            
        looperBE.addNote(
            event.sound, event.instrument,
            LooperBlock.looperTag(event.instrument).getInt("channel"),
            looperBE.getTicks()
        );

        looperBE.setChanged();
    }
    
}
