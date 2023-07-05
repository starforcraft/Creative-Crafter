package com.ultramega.creativecrafter.registry;

import com.ultramega.creativecrafter.CreativeCrafter;
import com.refinedmods.refinedstorage.item.blockitem.BaseBlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CreativeCrafter.MOD_ID);

    public static final RegistryObject<Item> CREATIVE_CRAFTER = ITEMS.register("creative_crafter", () -> new BaseBlockItem(ModBlocks.CREATIVE_CRAFTER.get(), new Item.Properties()));
}