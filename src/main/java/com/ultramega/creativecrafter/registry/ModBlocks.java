package com.ultramega.creativecrafter.registry;

import com.ultramega.creativecrafter.CreativeCrafter;
import com.ultramega.creativecrafter.blocks.CreativeCrafterBlock;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CreativeCrafter.MOD_ID);

    public static final RegistryObject<CreativeCrafterBlock> CREATIVE_CRAFTER = BLOCKS.register("creative_crafter", CreativeCrafterBlock::new);
}