package com.cstav.evenmoreinstruments.item.partial.emirecord;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class BurnedRecordItem extends EMIRecordItem {
    private final ResourceLocation burnedMedia;

    public BurnedRecordItem(final Properties properties, final ResourceLocation burnedMedia) {
        super(properties.stacksTo(1));
        this.burnedMedia = burnedMedia;
    }

    @Override
    public CompoundTag toLooperData(final ItemStack stack) {
        final CompoundTag tag = new CompoundTag();
        tag.putString("burned_media", burnedMedia.toString());
        tag.putString("record_id", ForgeRegistries.ITEMS.getKey(stack.getItem()).toString());
        return tag;
    }




}