package com.YTrollman.CreativeCrafter.registry;

import com.YTrollman.CreativeCrafter.CreativeCrafter;
import com.YTrollman.CreativeCrafter.blockentity.CreativeCrafterBlockEntity;
import com.YTrollman.CreativeCrafter.container.CreativeCrafterContainerMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModContainers {

    public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, CreativeCrafter.MOD_ID);

    public static final RegistryObject<MenuType<CreativeCrafterContainerMenu>> CREATIVE_CRAFTER_CONTAINER = CONTAINER_TYPES.register("creative_crafter", () -> IForgeMenuType.create((windowId, inv, data) -> {
        BlockPos pos = data.readBlockPos();
        BlockEntity te = inv.player.getCommandSenderWorld().getBlockEntity(pos);
        if(!(te instanceof CreativeCrafterBlockEntity)) {
            CreativeCrafter.LOGGER.error("Wrong type of tile entity (expected CreativeCrafterBlockEntity)!");
            return null;
        }
        return new CreativeCrafterContainerMenu(windowId, inv.player, (CreativeCrafterBlockEntity) te);
    }));
}
