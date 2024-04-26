package com.cstav.evenmoreinstruments.item.emirecord;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.mixins.optional.MinecraftServerAccessor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.JsonOps;
import net.minecraft.FileUtil;
import net.minecraft.ResourceLocationException;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import org.slf4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@EventBusSubscriber(bus = Bus.FORGE, modid = EMIMain.MODID)
public class RecordRepository {
    // idk of thread safety is important here, idk how minecraft handles data loading. better be safe than sorry.
    private static final ConcurrentHashMap<ResourceLocation, CompoundTag> RECORDS = new ConcurrentHashMap<>();
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final String RECORDS_DIR = "records";
    private static final String DATA_DIR = EMIMain.MODID+"/"+RECORDS_DIR;

    public static Optional<CompoundTag> getRecord(final ResourceLocation loc) {
        return RECORDS.containsKey(loc)
            ? Optional.of(RECORDS.get(loc).copy())
            : Optional.empty();
    }

    public static Collection<ResourceLocation> records() {
        return Collections.unmodifiableSet(RECORDS.keySet());
    }


    private static final Gson GSON = new Gson();
    @SubscribeEvent
    public static void registerReloadEvent(final AddReloadListenerEvent event) {
        event.addListener(new SimpleJsonResourceReloadListener(GSON, DATA_DIR) {
            @Override
            protected void apply(Map<ResourceLocation, JsonElement> pObject, ResourceManager pResourceManager, ProfilerFiller pProfiler) {
                //TODO load generated into map
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
    }


    public static boolean saveRecord(final Level level, final ResourceLocation name, final CompoundTag channel) {
        try {
            final Path genPath = getGenPath(level.getServer(), name);
            final Path path = FileUtil.createPathToResource(genPath, name.getPath(), ".json");

            // Copied from StructureTemplateManager#createAndValidatePathToStructure
            if (!(path.startsWith(genPath) && FileUtil.isPathNormalized(path) && FileUtil.isPathPortable(path)))
                throw new ResourceLocationException("Invalid resource path: " + path);

            Files.createDirectories(path.getParent());

            try (final FileWriter outStream = new FileWriter(path.toFile())) {
                final JsonElement jsonData = NbtOps.INSTANCE.convertTo(JsonOps.INSTANCE, channel);

                final Gson gson = new GsonBuilder().setPrettyPrinting().create();
                gson.toJson(jsonData, outStream);
            }

            RECORDS.put(name, channel);
            return true;
        } catch (Exception e) {
            LOGGER.error("Error encountered while attempting to save record data", e);
            return false;
        }
    }

    private static Path getGenPath(final MinecraftServer server, final ResourceLocation loc) throws IOException {
        final Path path = ((MinecraftServerAccessor)server).getStorageSource()
            .getLevelPath(LevelResource.GENERATED_DIR)
            .normalize();

        if (!Files.isDirectory(path))
            throw new IOException("Path "+path+" is not directory");

        return path.resolve(loc.getNamespace()).resolve(RECORDS_DIR);
    }
}
