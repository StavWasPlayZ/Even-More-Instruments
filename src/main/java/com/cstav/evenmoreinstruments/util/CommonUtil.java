package com.cstav.evenmoreinstruments.util;

import java.util.function.Supplier;

import com.cstav.evenmoreinstruments.Main;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class CommonUtil {

    public static final CompoundTag TAG_EMPTY = new CompoundTag();

    public static CompoundTag getOrCreateElementTag(final ItemStack item, final String key) {
        return getOrCreateElementTag(Main.modTag(item), key);
    }
    public static CompoundTag getOrCreateElementTag(final CompoundTag parent, final String key) {
        return getOrCreateTag(parent, key, CompoundTag.TAG_COMPOUND, CompoundTag::new);
    }

    public static ListTag getOrCreateListTag(final CompoundTag parent, final String key) {
        return getOrCreateTag(parent, key, CompoundTag.TAG_LIST, ListTag::new);
    }

    public static <T extends Tag> T getOrCreateTag(ItemStack item, String key, int type, Supplier<T> orElse) {
        return getOrCreateTag(Main.modTag(item), key, type, orElse);
    }
    @SuppressWarnings("unchecked")
    public static <T extends Tag> T getOrCreateTag(CompoundTag parent, String key, int type, Supplier<T> orElse) {
        if (parent.contains(key, type))
            return (T) parent.get(key);

        final T tag = orElse.get();
        parent.put(key, tag);
        return tag;
    }

}
