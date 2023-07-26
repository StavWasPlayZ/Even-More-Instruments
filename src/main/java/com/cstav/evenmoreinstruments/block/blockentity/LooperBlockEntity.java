package com.cstav.evenmoreinstruments.block.blockentity;

import java.util.Optional;

import org.slf4j.Logger;

import com.cstav.genshinstrument.event.InstrumentPlayedEvent;
import com.cstav.genshinstrument.sound.NoteSound;
import com.cstav.genshinstrument.util.ServerUtil;
import com.cstav.evenmoreinstruments.Main;
import com.cstav.evenmoreinstruments.block.LooperBlock;
import com.cstav.evenmoreinstruments.util.CommonUtil;
import com.cstav.evenmoreinstruments.util.LooperUtil;
import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(bus = Bus.FORGE, modid = Main.MODID)
public class LooperBlockEntity extends BlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();

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
        return CommonUtil.getOrCreateListTag(data, "channels");
    }


    public LooperBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.LOOPER.get(), pPos, pBlockState);

        final CompoundTag data = getPersistentData();

        // Construct all the data stuff
        if (!data.contains("channels", CompoundTag.TAG_LIST))
            getChannels().add(new ListTag());

        if (!data.contains("isRecording", CompoundTag.TAG_BYTE))
            setRecording(false);

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
    public void setRepeatTick(final int tick) {
        getPersistentData().putInt("repeatTick", tick);
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
        // idk why but it needs to be here for it to work
        final LooperBlockEntity lbe = getLBE(pLevel, pPos);

        
        if (!lbe.getBlockState().getValue(LooperBlock.PLAYING) && !lbe.isRecording())
            return;

        int ticks = getTicks() + 1;
        final int repTick = getRepeatTick();
        if ((repTick != -1) && (ticks > repTick))
            ticks = 0;


        for (final Tag channel : lbe.getChannels()) {
            if (!(channel instanceof ListTag))
                continue;

            for (final Tag rNote : (ListTag)channel) {
                if (!(rNote instanceof CompoundTag))
                    continue;

                final CompoundTag note = (CompoundTag)rNote;
                if (ticks != note.getInt("timestamp"))
                    continue;

                try {
                    final String stereoLoc = note.getString("stereo");
                    
                    ServerUtil.sendPlayNotePackets(pLevel, pPos, new NoteSound(
                        SoundEvent.createVariableRangeEvent(new ResourceLocation(note.getString("mono"))),
                        stereoLoc.equals("") ? null
                            : SoundEvent.createVariableRangeEvent(new ResourceLocation(stereoLoc)),
                        
                        note.getFloat("pitch")
                    ));

                } catch (Exception e) {
                    LOGGER.error("Attempted to play note, but met with an exception", e);
                }
            }
        }


        lbe.setTicks(ticks);
        lbe.setChanged();
    }


    /**
     * Attempts to get the looper pointed out by {@code instrument}. Removes its reference if not found.
     * @param level The level to get the BE from
     * @param instrument The subject instrument to get the looper data from
     * @return The Looper's block entity as pointed in the {@code instrument}'s data.
     * Null if not found
     */
    public static LooperBlockEntity getLBE(final Level level, final ItemStack instrument) {
        if (!LooperUtil.hasLooperTag(instrument))
            return null;

        final LooperBlockEntity looperBE = getLBE(level, LooperUtil.getLooperPos(instrument));

        if (looperBE == null)
            LooperUtil.remLooperTag(instrument);

        return looperBE;
    }
    public static LooperBlockEntity getLBE(final Level level, final BlockPos pos) {
        final Optional<LooperBlockEntity> opLooperBE =
            level.getBlockEntity(pos, ModBlockEntities.LOOPER.get());

        return opLooperBE.isPresent() ? opLooperBE.get() : null;
    }

    @SubscribeEvent
    public static void onInstrumentPlayed(final InstrumentPlayedEvent.ByPlayer event) {
        if (!LooperUtil.isRecording(event.instrument))
            return;
            
            
        final LooperBlockEntity looperBE = getLBE(event.player.getLevel(), event.instrument);
        if (looperBE == null)
            return;
            

        looperBE.setRecording(true);
            
        looperBE.addNote(
            event.sound, event.instrument,
            LooperUtil.looperTag(event.instrument).getInt("channel"),
            looperBE.getTicks()
        );

        looperBE.setChanged();
    }
    
}
