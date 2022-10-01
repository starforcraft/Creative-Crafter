package com.YTrollman.CreativeCrafter.registry;

import com.YTrollman.CreativeCrafter.CreativeCrafter;
import com.YTrollman.CreativeCrafter.blocks.CreativeCrafterBlock;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import javax.annotation.Nonnull;

public class ModBlocks {

    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, CreativeCrafter.MOD_ID);

    public static final RegistryObject<CreativeCrafterBlock> CREATIVE_CRAFTER = BLOCKS.register("creative_crafter", () -> new CreativeCrafterBlock());

    public static final CreativeModeTab CREATIVE_CRAFTER_GROUP = (new CreativeModeTab(CreativeCrafter.MOD_ID) {

        @Override
        @Nonnull
        @OnlyIn(Dist.CLIENT)
        public ItemStack makeIcon() {
            return new ItemStack(ModItems.CREATIVE_CRAFTER_ITEM.get());
        }
    });
}