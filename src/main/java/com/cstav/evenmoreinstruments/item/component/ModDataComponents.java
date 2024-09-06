package com.cstav.evenmoreinstruments.item.component;

import com.cstav.evenmoreinstruments.EMIMain;
import com.cstav.evenmoreinstruments.item.emirecord.RecordRepository;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponentType.Builder;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.component.CustomData;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.UnaryOperator;

public class ModDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, EMIMain.MODID);
    public static void register(final IEventBus bus) {
        DATA_COMPONENTS.register(bus);
    }

    /**
     * Used for burned records to identify their location on the {@link RecordRepository}
     */
    public static final RegistryObject<DataComponentType<ResourceLocation>> BURNED_MEDIA = register("burned_media", (builder) ->
        builder.persistent(ResourceLocation.CODEC).cacheEncoding() // idk what caching does here tbh lmao
    );
    /**
     * Stores data that should be played by the Looper
     */
    public static final RegistryObject<DataComponentType<CustomData>> CHANNNEL = register("channel", (builder) ->
        builder.persistent(CustomData.CODEC).cacheEncoding()
    );
    /**
     * Stores the position of a looper block
     */
    public static final RegistryObject<DataComponentType<BlockPos>> LOOPER_POS = register("looper_pos", (builder) ->
        builder.persistent(BlockPos.CODEC).cacheEncoding()
    );
    /**
     * Stores the position of an instrument block
     */
    public static final RegistryObject<DataComponentType<BlockPos>> BLOCK_INSTRUMENT_POS = register("block_instrument_pos", (builder) ->
        builder.persistent(BlockPos.CODEC).cacheEncoding()
    );

    /**
     * Stores information about a looper, primarily its position.
     */
    public static final RegistryObject<DataComponentType<CustomData>> LOOPER_TAG = register("looper_tag", (builder) ->
        builder.persistent(CustomData.CODEC).networkSynchronized(CustomData.STREAM_CODEC).cacheEncoding()
    );

    private static <T> RegistryObject<DataComponentType<T>> register(final String name, final UnaryOperator<Builder<T>> builder) {
        return DATA_COMPONENTS.register(name, () -> builder.apply(DataComponentType.builder()).build());
    }
}
