package com.cstav.evenmoreinstruments.item.partial.emirecord;

import com.cstav.evenmoreinstruments.Main;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.slf4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(bus = Bus.FORGE, modid = Main.MODID)
public class RecordRepository {
    private static final ConcurrentHashMap<ResourceLocation, CompoundTag> RECORDS = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String DATA_DIR = Main.MODID+"/records";

    public static CompoundTag getRecord(final ResourceLocation loc) {
        return RECORDS.get(loc);
    }

    private static final Gson GSON = new Gson();
    @SubscribeEvent
    public static void registerReloadEvent(final AddReloadListenerEvent event) {
        event.addListener(new SimpleJsonResourceReloadListener(GSON, DATA_DIR) {
            @Override
            protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
                reloadRecords(pObject, pResourceManager, pProfiler);
            }
        });
    }

    private static void reloadRecords(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
        RECORDS.clear();

        pObject.forEach((loc, tag) -> {
            final JsonElement channelObj = pObject.get(loc);

            RECORDS.put(loc, (CompoundTag) JsonOps.INSTANCE.convertTo(NbtOps.INSTANCE, channelObj));
            LOGGER.info("Successfully loaded burned record {}", loc);
        });
    }
}
