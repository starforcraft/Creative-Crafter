package com.YTrollman.CreativeCrafter.tileentity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.YTrollman.CreativeCrafter.gui.dataparameter.CreativeCrafterTileDataParameterClientListener;
import com.YTrollman.CreativeCrafter.node.CreativeCrafterNetworkNode;
import com.YTrollman.CreativeCrafter.registry.ModTileEntityTypes;
import com.refinedmods.refinedstorage.tile.NetworkNodeTile;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;

import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class CreativeCrafterTileEntity extends NetworkNodeTile<CreativeCrafterNetworkNode>
{
    public static final TileDataParameter<Integer, CreativeCrafterTileEntity> MODE = new TileDataParameter<>(DataSerializers.INT, CreativeCrafterNetworkNode.CrafterMode.IGNORE.ordinal(), t -> t.getNode().getMode().ordinal(), (t, v) -> t.getNode().setMode(CreativeCrafterNetworkNode.CrafterMode.getById(v)));
    private static final TileDataParameter<Boolean, CreativeCrafterTileEntity> HAS_ROOT = new TileDataParameter<>(DataSerializers.BOOLEAN, false, t -> t.getNode().getRootContainerNotSelf().isPresent(), null, (t, v) -> new CreativeCrafterTileDataParameterClientListener().onChanged(t, v));

    private final LazyOptional<IItemHandler> patternsCapability = LazyOptional.of(() -> getNode().getPatternItems());

    public CreativeCrafterTileEntity()
    {
        super(ModTileEntityTypes.CREATIVE_CRAFTER_TILE_ENTITY.get());
        
        dataManager.addWatchedParameter(MODE);
        dataManager.addParameter(HAS_ROOT);
    }

    @Override
    public CreativeCrafterNetworkNode createNode(World world, BlockPos blockPos)
    {
        return new CreativeCrafterNetworkNode(world, blockPos);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction)
    {
        if(cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            if(direction != null && !direction.equals(this.getNode().getDirection()))
                return patternsCapability.cast();
        return super.getCapability(cap, direction);
    }
}
