package com.YTrollman.CreativeCrafter.blocks;

import com.YTrollman.CreativeCrafter.CreativeCrafter;
import com.YTrollman.CreativeCrafter.tileentity.CreativeCrafterTileEntity;
import com.refinedmods.refinedstorage.api.network.grid.GridFactoryType;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.api.network.grid.IGridFactory;
import com.refinedmods.refinedstorage.inventory.player.PlayerSlot;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class CreativeCrafterBlockFactory implements IGridFactory {
    public static final ResourceLocation ID = new ResourceLocation(CreativeCrafter.MOD_ID, "creative_crafter");

    @Override
    @Nullable
    public IGrid createFromStack(PlayerEntity player, ItemStack stack, PlayerSlot slotId) {
        return null;
    }

    @Override
    @Nullable
    public IGrid createFromBlock(PlayerEntity player, BlockPos pos) {
        TileEntity tile = getRelevantTile(player.getCommandSenderWorld(), pos);

        if (tile instanceof CreativeCrafterTileEntity) {
            return ((CreativeCrafterTileEntity) tile).getNode();
        }

        return null;
    }

    @Nullable
    @Override
    public TileEntity getRelevantTile(World world, BlockPos pos) {
        return world.getBlockEntity(pos);
    }

    @Override
    public GridFactoryType getType() {
        return GridFactoryType.BLOCK;
    }
}