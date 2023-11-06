package com.ultramega.creativecrafter.registry;

import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationManager;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.ultramega.creativecrafter.CreativeCrafter;
import com.ultramega.creativecrafter.blockentity.CreativeCrafterBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CreativeCrafter.MOD_ID);

    public static final RegistryObject<BlockEntityType<CreativeCrafterBlockEntity>> CREATIVE_CRAFTER_TILE_ENTITY = BLOCK_ENTITIES.register("creative_crafter", () -> registerSynchronizationParameters(CreativeCrafterBlockEntity.SPEC, BlockEntityType.Builder
            .of(CreativeCrafterBlockEntity::new, ModBlocks.CREATIVE_CRAFTER.get())
            .build(null)));

    private static <T extends BlockEntity> BlockEntityType<T> registerSynchronizationParameters(BlockEntitySynchronizationSpec spec, BlockEntityType<T> t) {
        spec.getParameters().forEach(BlockEntitySynchronizationManager::registerParameter);
        return t;
    }
}
