package com.cstav.evenmoreinstruments.item.partial.emirecord;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.util.BiKey;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.level.ChunkEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;

@EventBusSubscriber(bus = Bus.FORGE, modid = EMIMain.MODID)
public class RecordRepository {
    // idk of thread safety is important here, idk how minecraft handles data loading. better be safe than sorry.
    private static final ConcurrentHashMap<ResourceLocation, CompoundTag> RECORDS = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String DATA_DIR = EMIMain.MODID+"/records";

    private static final ConcurrentHashMap<BiKey<ResourceLocation, BlockPos>, Consumer<CompoundTag>> reloadSubscribers = new ConcurrentHashMap<>();

    /**
     * Feeds the consumer with the cached record data and subscribes it to any
     * future change on its regard.
     */
    public static void consumeRecord(BlockPos pos, ResourceLocation loc, Consumer<CompoundTag> recordConsumer) {
        recordConsumer.accept(getRecord(loc));
        reloadSubscribers.put(new BiKey<>(loc, pos), recordConsumer);
    }
    public static void removeSub(BlockPos pos, ResourceLocation loc) {
        reloadSubscribers.remove(new BiKey<>(loc, pos));
    }

    public static CompoundTag getRecord(final ResourceLocation loc) {
        return RECORDS.get(loc).copy();
    }


    private static final Gson GSON = new Gson();
    @SubscribeEvent
    public static void registerReloadEvent(final AddReloadListenerEvent event) {
        event.addListener(new SimpleJsonResourceReloadListener(GSON, DATA_DIR) {
            @Override
            protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
                reloadRecords(pObject);
            }
        });
    }

    private static void reloadRecords(Map<ResourceLocation, JsonElement> pObject) {
        RECORDS.clear();

        pObject.forEach((loc, tag) -> {
            final JsonElement channelObj = pObject.get(loc);

            RECORDS.put(loc, (CompoundTag) JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, channelObj));
            LOGGER.info("Successfully loaded burned record {}", loc);
        });

        reloadSubscribers.forEach((bikey, consumer) -> consumer.accept(getRecord(bikey.k1)));
    }


    // Remove all subscribers upon world unload
    @SubscribeEvent
    public static void onChunkUnload(final ChunkEvent.Unload event) {
        final ArrayList<BiKey<ResourceLocation, BlockPos>> toRemove = new ArrayList<>();

        final ChunkPos pos = event.getChunk().getPos();
        final Level level = (Level)event.getLevel();
        final AABB chunkArea = new AABB(
            pos.getMinBlockX(), level.getMinBuildHeight(), pos.getMinBlockZ(),
            pos.getMaxBlockX(), level.getMaxBuildHeight(), pos.getMaxBlockZ()
        );

        for (final BiKey<ResourceLocation, BlockPos> key : reloadSubscribers.keySet()) {
            if (chunkArea.contains(key.k2.getCenter()))
                toRemove.add(key);
        }

        toRemove.forEach(reloadSubscribers::remove);
    }
    @SubscribeEvent
    public static void onWorldUnload(final LevelEvent.Unload event) {
        reloadSubscribers.clear();
    }
}
