package com.ultramega.creativecrafter.registry;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class RegistryHandler {
    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModCreativeTabs.TABS.register(bus);
        ModItems.ITEMS.register(bus);
        ModBlocks.BLOCKS.register(bus);
        ModContainerMenus.CONTAINER_TYPES.register(bus);
        ModBlockEntities.BLOCK_ENTITIES.register(bus);
    }
}
