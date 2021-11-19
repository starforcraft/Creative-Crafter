package com.YTrollman.CreativeCrafter.registry;

import com.YTrollman.CreativeCrafter.CreativeCrafter;
import com.YTrollman.CreativeCrafter.blocks.CreativeCrafterBlock;
import net.minecraft.block.Block;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CreativeCrafter.MOD_ID);

    public static final RegistryObject<CreativeCrafterBlock> CREATIVE_CRAFTER = BLOCKS.register("creative_crafter", () -> new CreativeCrafterBlock());
    
    public static final ItemGroup CREATIVE_CRAFTER_GROUP = (new ItemGroup(CreativeCrafter.MOD_ID) {

        @Override
        @Nonnull
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.CREATIVE_CRAFTER_ITEM.get());
        }
    });
}