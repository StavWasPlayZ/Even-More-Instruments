package com.cstav.evenmoreinstruments.item.crafting;

import com.cstav.evenmoreinstruments.EMIMain;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModRecipeSerializers {

    public static final DeferredRegister<RecipeSerializer<?>> RECIPES = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, EMIMain.MODID);
    public static void register(final IEventBus bus) {
        RECIPES.register(bus);
    }

    public static final RegistryObject<RecipeSerializer<RecordCloningRecipe>> RECORD_CLONING = RECIPES.register(
        "crafting_special_recordcloning",
        () -> new SimpleCraftingRecipeSerializer<>(RecordCloningRecipe::new))
    ;

}
