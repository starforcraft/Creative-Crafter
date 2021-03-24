package com.YTrollman.CreativeCrafter.registry;

import com.YTrollman.CreativeCrafter.CreativeCrafter;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.item.blockitem.BaseBlockItem;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CreativeCrafter.MOD_ID);

    public static final RegistryObject<Item> CREATIVE_CRAFTER_ITEM = ITEMS.register("creative_crafter", () -> new BaseBlockItem(ModBlocks.CREATIVE_CRAFTER.get(), new Item.Properties().tab(RS.MAIN_GROUP)));
}