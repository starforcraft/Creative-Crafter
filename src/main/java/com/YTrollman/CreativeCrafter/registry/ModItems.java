package com.YTrollman.CreativeCrafter.registry;

import com.YTrollman.CreativeCrafter.CreativeCrafter;
import com.refinedmods.refinedstorage.item.blockitem.BaseBlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, CreativeCrafter.MOD_ID);

    public static final RegistryObject<Item> CREATIVE_CRAFTER_ITEM = ITEMS.register("creative_crafter", () -> new BaseBlockItem(ModBlocks.CREATIVE_CRAFTER.get(), new Item.Properties().tab(ModBlocks.CREATIVE_CRAFTER_GROUP)));
}