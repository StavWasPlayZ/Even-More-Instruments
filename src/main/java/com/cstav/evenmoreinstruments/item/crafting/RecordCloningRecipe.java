package com.cstav.evenmoreinstruments.item.crafting;

import com.cstav.evenmoreinstruments.item.ModItems;
import com.cstav.evenmoreinstruments.item.emirecord.WritableRecordItem;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.Optional;

public class RecordCloningRecipe extends CustomRecipe {
    public RecordCloningRecipe(CraftingBookCategory pCategory) {
        super(pCategory);
    }

    /**
     * Used to check if a recipe matches current crafting inventory
     */
    @Override
    public boolean matches(CraftingInput pInput, Level pLevel) {
        return getIngredientsFromContainer(pInput).isPresent();
    }
    private static boolean isWritableRecord(final ItemStack stack) {
        return stack.is(ModItems.RECORD_WRITABLE.get()) && !((WritableRecordItem)stack.getItem()).isBurned(stack);
    }
    private static boolean isBurnedRecord(final ItemStack stack) {
        return stack.is(ModItems.RECORD_WRITABLE.get()) && ((WritableRecordItem)stack.getItem()).isBurned(stack);
    }

    @Override
    public ItemStack assemble(CraftingInput pContainer, Provider pRegistries) {
        final Optional<ItemStack[]> ingredients = getIngredientsFromContainer(pContainer);
        if (ingredients.isEmpty())
            return ItemStack.EMPTY;

        return ingredients.get()[1].copyWithCount(1);
    }

    @Override
    public NonNullList<ItemStack> getRemainingItems(CraftingInput pInput) {
        final NonNullList<ItemStack> result = NonNullList.withSize(pInput.size(), ItemStack.EMPTY);

        for (int i = 0; i < result.size(); ++i) {
            ItemStack stack = pInput.getItem(i);

            if (isBurnedRecord(stack)) {
                result.set(i, stack.copyWithCount(1));
                return result;
            }
        }

        return result;
    }


    /**
     * @return An array containing:
     * <ol>
     *     <li>The writable record</li>
     *     <li>The burned record</li>
     * </ol>
     * Or none if either were not present.
     */
    private Optional<ItemStack[]> getIngredientsFromContainer(CraftingInput pInv) {
        ItemStack burnedRecord = ItemStack.EMPTY;
        ItemStack writableRecord = ItemStack.EMPTY;

        for (int i = 0; i < pInv.size(); ++i) {
            ItemStack stack = pInv.getItem(i);
            if (stack.isEmpty())
                continue;

            if (isBurnedRecord(stack)) {
                // More than one writable record present?
                if (!burnedRecord.isEmpty())
                    return Optional.empty();

                burnedRecord = stack;

                if (!writableRecord.isEmpty()) {
                    return Optional.of(new ItemStack[] {writableRecord, burnedRecord});
                }
            } else {
                // Only other item must be a writable record
                // Only one record may be present
                if (!isWritableRecord(stack) || !writableRecord.isEmpty())
                    return Optional.empty();

                writableRecord = stack;

                if (!burnedRecord.isEmpty()) {
                    return Optional.of(new ItemStack[] {writableRecord, burnedRecord});
                }
            }
        }

        return Optional.empty();
    }


    @Override
    public RecipeSerializer<?> getSerializer() {
        return ModRecipeSerializers.RECORD_CLONING.get();
    }

    /**
     * Used to determine if this recipe can fit in a grid of the given width/height
     */
    @Override
    public boolean canCraftInDimensions(int pWidth, int pHeight) {
        return pWidth >= 3 && pHeight >= 3;
    }
}
