package com.ultramega.creativecrafter.registry;

import com.refinedmods.refinedstorage.container.factory.BlockEntityContainerFactory;
import com.ultramega.creativecrafter.CreativeCrafter;
import com.ultramega.creativecrafter.blockentity.CreativeCrafterBlockEntity;
import com.ultramega.creativecrafter.container.CreativeCrafterContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModContainerMenus {
    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, CreativeCrafter.MOD_ID);

    public static final RegistryObject<MenuType<CreativeCrafterContainerMenu>> CREATIVE_CRAFTER_CONTAINER = CONTAINER_TYPES.register("creative_crafter", () -> IForgeMenuType.create(new BlockEntityContainerFactory<CreativeCrafterContainerMenu, CreativeCrafterBlockEntity>((windowId, inv, blockEntity) -> new CreativeCrafterContainerMenu(blockEntity, inv.player, windowId))));
}
