package com.cstav.evenmoreinstruments.util;

import com.cstav.evenmoreinstruments.EMIMain;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;

import java.util.Map;
import java.util.function.Supplier;

public class CommonUtil {

    public static final CompoundTag TAG_EMPTY = new CompoundTag();

    public static CompoundTag getOrCreateElementTag(final ItemStack item, final String key) {
        return getOrCreateElementTag(EMIMain.modTag(item), key);
    }
    public static CompoundTag getOrCreateElementTag(final CompoundTag parent, final String key) {
        return getOrCreateTag(parent, key, CompoundTag.TAG_COMPOUND, CompoundTag::new);
    }

    public static ListTag getOrCreateListTag(final CompoundTag parent, final String key) {
        return getOrCreateTag(parent, key, CompoundTag.TAG_LIST, ListTag::new);
    }

    public static <T extends Tag> T getOrCreateTag(ItemStack item, String key, int type, Supplier<T> orElse) {
        return getOrCreateTag(EMIMain.modTag(item), key, type, orElse);
    }
    @SuppressWarnings("unchecked")
    public static <T extends Tag> T getOrCreateTag(CompoundTag parent, String key, int type, Supplier<T> orElse) {
        if (parent.contains(key, type))
            return (T) parent.get(key);

        final T tag = orElse.get();
        parent.put(key, tag);
        return tag;
    }

    public static InteractionHand getOffhand(final InteractionHand hand) {
        return (hand == InteractionHand.MAIN_HAND) ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND;
    }


    public static CompoundTag deepConvertCompound(final CompoundTag compound, final Map<String, String> oldToNewMapper) {
        final CompoundTag result = new CompoundTag();

        compound.getAllKeys().forEach((key) -> {
            final String newKey = oldToNewMapper.get(key);
            if (newKey == null)
                return; // idk why this is even a case..

            if (compound.contains(key, Tag.TAG_COMPOUND))
                result.put(newKey, deepConvertCompound(compound.getCompound(key), oldToNewMapper));
            else if (compound.contains(key, Tag.TAG_LIST))
                result.put(newKey, deepConvertList((ListTag) compound.get(key), oldToNewMapper));
            else
                result.put(newKey, compound.get(key).copy());
        });

        return result;
    }
    /**
     * Converts all compound keys within this list
     */
    public static ListTag deepConvertList(final ListTag list, final Map<String, String> oldToNewMapper) {
        final ListTag result = new ListTag();

        list.forEach((tag) -> {
            if (tag instanceof ListTag lt)
                result.add(deepConvertList(lt, oldToNewMapper));
            else if (tag instanceof CompoundTag ct)
                result.add(deepConvertCompound(ct, oldToNewMapper));
            else
                result.add(tag.copy());
        });

        return result;
    }

    public static void moveTags(final CompoundTag source, final CompoundTag dest, final String key) {
        final Tag value = source.get(key);
        dest.put(key, value);
        source.remove(key);
    }

}
