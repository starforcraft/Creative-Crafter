package com.ultramega.creativecrafter.registry;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

public class RegistryHandler {

    public static void init() {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        ModContainerMenus.CONTAINER_TYPES.register(bus);
        ModBlockEntities.BLOCK_ENTITIES.register(bus);
        ModBlocks.BLOCKS.register(bus);
        ModItems.ITEMS.register(bus);
    }
}
