package com.YTrollman.CreativeCrafter.tileentity;

import com.YTrollman.CreativeCrafter.gui.CreativeCrafterScreen;
import com.YTrollman.CreativeCrafter.gui.dataparameter.CreativeCrafterTileDataParameterClientListener;
import com.YTrollman.CreativeCrafter.node.CreativeCrafterNetworkNode;
import com.YTrollman.CreativeCrafter.registry.ModTileEntityTypes;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.screen.BaseScreen;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CreativeCrafterTileEntity extends NetworkNodeTile<CreativeCrafterNetworkNode>
{
    public static final TileDataParameter<Integer, CreativeCrafterTileEntity> MODE = new TileDataParameter<>(DataSerializers.INT, CreativeCrafterNetworkNode.CrafterMode.IGNORE.ordinal(), t -> t.getNode().getMode().ordinal(), (t, v) -> t.getNode().setMode(CreativeCrafterNetworkNode.CrafterMode.getById(v)));
    private static final TileDataParameter<Boolean, CreativeCrafterTileEntity> HAS_ROOT = new TileDataParameter<>(DataSerializers.BOOLEAN, false, t -> t.getNode().getRootContainerNotSelf().isPresent(), null, (t, v) -> new CreativeCrafterTileDataParameterClientListener().onChanged(t, v));
    public static final TileDataParameter<Integer, CreativeCrafterTileEntity> VIEW_TYPE = new TileDataParameter<>(DataSerializers.INT, 0, t -> t.getNode().getViewType(), (t, v) -> {
        if (IGrid.isValidViewType(v)) {
            t.getNode().setViewType(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> trySortGrid(initial));
    public static final TileDataParameter<Integer, CreativeCrafterTileEntity> SORTING_DIRECTION = new TileDataParameter<>(DataSerializers.INT, 0, t -> t.getNode().getSortingDirection(), (t, v) -> {
        if (IGrid.isValidSortingDirection(v)) {
            t.getNode().setSortingDirection(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> trySortGrid(initial));
    public static final TileDataParameter<Integer, CreativeCrafterTileEntity> SORTING_TYPE = new TileDataParameter<>(DataSerializers.INT, 0, t -> t.getNode().getSortingType(), (t, v) -> {
        if (IGrid.isValidSortingType(v)) {
            t.getNode().setSortingType(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> trySortGrid(initial));
    public static final TileDataParameter<Integer, CreativeCrafterTileEntity> SEARCH_BOX_MODE = new TileDataParameter<>(DataSerializers.INT, 0, t -> t.getNode().getSearchBoxMode(), (t, v) -> {
        if (IGrid.isValidSearchBoxMode(v)) {
            t.getNode().setSearchBoxMode(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> BaseScreen.executeLater(CreativeCrafterScreen.class, grid -> grid.getSearchField().setMode(p)));
    public static final TileDataParameter<Integer, CreativeCrafterTileEntity> SIZE = new TileDataParameter<>(DataSerializers.INT, 0, t -> t.getNode().getSize(), (t, v) -> {
        if (IGrid.isValidSize(v)) {
            t.getNode().setSize(v);
            t.getNode().markDirty();
        }
    }, (initial, p) -> BaseScreen.executeLater(CreativeCrafterScreen.class, grid -> grid.resize(grid.getMinecraft(), grid.width, grid.height)));
    public static final TileDataParameter<Integer, CreativeCrafterTileEntity> TAB_SELECTED = new TileDataParameter<>(DataSerializers.INT, 0, t -> t.getNode().getTabSelected(), (t, v) -> {
        t.getNode().setTabSelected(v == t.getNode().getTabSelected() ? -1 : v);
        t.getNode().markDirty();
    }, (initial, p) -> BaseScreen.executeLater(CreativeCrafterScreen.class, grid -> grid.getView().sort()));
    public static final TileDataParameter<Integer, CreativeCrafterTileEntity> TAB_PAGE = new TileDataParameter<>(DataSerializers.INT, 0, t -> t.getNode().getTabPage(), (t, v) -> {
        if (v >= 0 && v <= t.getNode().getTotalTabPages()) {
            t.getNode().setTabPage(v);
            t.getNode().markDirty();
        }
    });

    public static void trySortGrid(boolean initial) {
        if (!initial) {
            BaseScreen.executeLater(CreativeCrafterScreen.class, grid -> grid.getView().sort());
        }
    }

    private final LazyOptional<IItemHandler> patternsCapability = LazyOptional.of(() -> getNode().getPatternItems());

    public CreativeCrafterTileEntity()
    {
        super(ModTileEntityTypes.CREATIVE_CRAFTER_TILE_ENTITY.get());

        dataManager.addWatchedParameter(VIEW_TYPE);
        dataManager.addWatchedParameter(SORTING_DIRECTION);
        dataManager.addWatchedParameter(SORTING_TYPE);
        dataManager.addWatchedParameter(SEARCH_BOX_MODE);
        dataManager.addWatchedParameter(SIZE);
        dataManager.addWatchedParameter(MODE);
        dataManager.addParameter(HAS_ROOT);
    }

    @Override
    @Nonnull
    public CreativeCrafterNetworkNode createNode(World world, BlockPos blockPos)
    {
        return new CreativeCrafterNetworkNode(world, blockPos);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY && direction != null && !direction.equals(this.getNode().getDirection())) {
            return patternsCapability.cast();
        }

        return super.getCapability(cap, direction);
    }
}
