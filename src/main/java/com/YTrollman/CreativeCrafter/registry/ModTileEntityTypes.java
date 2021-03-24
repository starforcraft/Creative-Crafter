package com.YTrollman.CreativeCrafter.registry;

import com.YTrollman.CreativeCrafter.CreativeCrafter;
import com.YTrollman.CreativeCrafter.tileentity.CreativeCrafterTileEntity;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ModTileEntityTypes {

    public static final DeferredRegister<TileEntityType<?>> TILE_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, CreativeCrafter.MOD_ID);

    public static final RegistryObject<TileEntityType<CreativeCrafterTileEntity>> CREATIVE_CRAFTER_TILE_ENTITY = TILE_ENTITY_TYPES.register("creative_crafter", () -> TileEntityType.Builder
            .of(() -> new CreativeCrafterTileEntity(), ModBlocks.CREATIVE_CRAFTER.get())
            .build(null));

}
