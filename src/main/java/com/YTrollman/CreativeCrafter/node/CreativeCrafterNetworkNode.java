package com.YTrollman.CreativeCrafter.node;

import com.YTrollman.CreativeCrafter.CreativeCrafter;
import com.YTrollman.CreativeCrafter.blocks.CreativeCrafterBlock;
import com.YTrollman.CreativeCrafter.config.CreativeCrafterConfig;
import com.YTrollman.CreativeCrafter.tileentity.CreativeCrafterTileEntity;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.grid.GridType;
import com.refinedmods.refinedstorage.api.network.grid.IGrid;
import com.refinedmods.refinedstorage.api.network.grid.IGridTab;
import com.refinedmods.refinedstorage.api.network.grid.handler.IFluidGridHandler;
import com.refinedmods.refinedstorage.api.network.grid.handler.IItemGridHandler;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCache;
import com.refinedmods.refinedstorage.api.storage.cache.IStorageCacheListener;
import com.refinedmods.refinedstorage.api.util.IFilter;
import com.refinedmods.refinedstorage.api.util.IStackList;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.apiimpl.storage.cache.listener.ItemGridStorageCacheListener;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.FilterItemHandler;
import com.refinedmods.refinedstorage.inventory.item.validator.PatternItemValidator;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.tile.data.TileDataManager;
import com.refinedmods.refinedstorage.util.StackUtils;
import com.refinedmods.refinedstorage.util.WorldUtils;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.CraftResultInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.INameable;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class CreativeCrafterNetworkNode extends NetworkNode implements ICraftingPatternContainer, IGrid
{
    private final List<IFilter> filters = new ArrayList<>();
    private final List<IGridTab> tabs = new ArrayList<>();
    private final FilterItemHandler filter = (FilterItemHandler) new FilterItemHandler(filters, tabs).addListener(new NetworkNodeInventoryListener(this));

    private int viewType;
    private int sortingDirection;
    private int sortingType;
    private int searchBoxMode;
    private int size;
    private int tabSelected;
    private int tabPage;

    public enum CrafterMode
    {
        IGNORE,
        SIGNAL_UNLOCKS_AUTOCRAFTING,
        SIGNAL_LOCKS_AUTOCRAFTING,
        PULSE_INSERTS_NEXT_SET;

        public static CrafterMode getById(int id)
        {
            if (id >= 0 && id < values().length)
                return values()[id];
            return IGNORE;
        }
    }

    private static final String NBT_DISPLAY_NAME = "DisplayName";
    private static final String NBT_UUID = "CrafterUuid";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_LOCKED = "Locked";
    private static final String NBT_WAS_POWERED = "WasPowered";

    private boolean readingInventory;

    private final BaseItemHandler patternsInventory = new BaseItemHandler(100)
        {
            @Override
            public int getSlotLimit(int slot)
            {
                return 1;
            }
        }
        .addValidator(new PatternItemValidator(world))
        .addListener(new NetworkNodeInventoryListener(this))
        .addListener((handler, slot, reading) ->
        {
            if (!reading)
            {
                if (!world.isClientSide)
                    invalidateSlot(slot);
                invalidateNextTick = true;
            }
        });

    private final ICraftingPattern[] patterns = new ICraftingPattern[patternsInventory.getSlots()];

    // Used to prevent infinite recursion on getRootContainer() when there's e.g. two crafters facing each other.
    private boolean visited = false;
    private boolean invalidateNextTick = false;

    private CrafterMode mode = CrafterMode.IGNORE;
    private boolean locked = false;
    private boolean wasPowered;

    @Nullable
    private ITextComponent displayName;

    @Nullable
    private UUID uuid = null;

    public static final ResourceLocation ID = new ResourceLocation(CreativeCrafter.MOD_ID, "creative_crafter");

    private static final ITextComponent DEFAULT_NAME = new TranslationTextComponent("gui.creativecrafter.creative_crafter");

    public CreativeCrafterNetworkNode(World world, BlockPos pos)
    {
    	super(world, pos);
        this.size = 0;
    }

    private void invalidate()
    {
        for(int slot = 0; slot < patternsInventory.getSlots(); ++slot)
            invalidateSlot(slot);
    }

    private void invalidateSlot(int slot)
    {
        patterns[slot] = null;

        ItemStack patternStack = patternsInventory.getStackInSlot(slot);
        if (patternStack.isEmpty())
            return;

        ICraftingPattern pattern = ((ICraftingPatternProvider) patternStack.getItem()).create(world, patternStack, this);
        if(pattern.isValid())
            patterns[slot] = pattern;
    }

    @Override
    public int getEnergyUsage()
    {
        return CreativeCrafterConfig.CREATIVE_CRAFTER_RF_CONSUME.get();
    }

    @Override
    public void update()
    {
        super.update();

        if (ticks == 1)
            invalidate();

        if (invalidateNextTick)
        {
            invalidateNextTick = false;
            if (network != null)
                network.getCraftingManager().invalidate();
        }

        if (mode == CrafterMode.PULSE_INSERTS_NEXT_SET && world.isLoaded(pos))
        {
            if (world.hasNeighborSignal(pos))
            {
                this.wasPowered = true;
                markDirty();
            }
            else if (wasPowered)
            {
                this.wasPowered = false;
                this.locked = false;
                markDirty();
            }
        }
    }

    @Override
    protected void onConnectedStateChange(INetwork network, boolean state, ConnectivityStateChangeCause cause)
    {
        super.onConnectedStateChange(network, state, cause);
        network.getCraftingManager().invalidate();
    }

    @Override
    public void onDisconnected(INetwork network)
    {
        super.onDisconnected(network);

        network.getCraftingManager().getTasks().stream()
                .filter(task -> task.getPattern().getContainer().getPosition().equals(pos))
                .forEach(task -> network.getCraftingManager().cancel(task.getId()));
    }

    @Override
    public void onDirectionChanged(Direction direction)
    {
        super.onDirectionChanged(direction);

        if(network != null)
            network.getCraftingManager().invalidate();
    }

    @Override
    public void read(CompoundNBT tag)
    {
        super.read(tag);

        readingInventory = true;

        StackUtils.readItems(patternsInventory, 0, tag);
        StackUtils.readItems(filter, 1, tag);

        invalidate();

        if (tag.contains(NBT_DISPLAY_NAME)) {
            displayName = ITextComponent.Serializer.fromJson(tag.getString(NBT_DISPLAY_NAME));
        }

        if (tag.hasUUID(NBT_UUID)) {
            uuid = tag.getUUID(NBT_UUID);
        }

        if (tag.contains(NBT_MODE)) {
            mode = CrafterMode.getById(tag.getInt(NBT_MODE));
        }

        if (tag.contains(NBT_LOCKED)) {
            locked = tag.getBoolean(NBT_LOCKED);
        }

        if (tag.contains(NBT_WAS_POWERED)) {
            wasPowered = tag.getBoolean(NBT_WAS_POWERED);
        }

        readingInventory = false;
    }

    @Override
    public void readConfiguration(CompoundNBT tag) {
        super.readConfiguration(tag);

        if (tag.contains("Size")) {
            this.size = tag.getInt("Size");
        }
    }

    @Override
    public ResourceLocation getId()
    {
        return ID;
    }

    @Override
    public CompoundNBT write(CompoundNBT tag)
    {
        super.write(tag);

        StackUtils.writeItems(patternsInventory, 0, tag);
        StackUtils.writeItems(filter, 1, tag);

        if (displayName != null) {
            tag.putString(NBT_DISPLAY_NAME, ITextComponent.Serializer.toJson(displayName));
        }

        if (uuid != null) {
            tag.putUUID(NBT_UUID, uuid);
        }

        tag.putInt(NBT_MODE, mode.ordinal());
        tag.putBoolean(NBT_LOCKED, locked);
        tag.putBoolean(NBT_WAS_POWERED, wasPowered);

        return tag;
    }

    @Override
    public CompoundNBT writeConfiguration(CompoundNBT tag) {
        super.writeConfiguration(tag);
        tag.putInt("Size", this.size);
        return tag;
    }

    @Override
    public int getUpdateInterval()
    {
    	return 0;
    }

    @Override
    public int getMaximumSuccessfulCraftingUpdates()
    {
    	return CreativeCrafterConfig.CREATIVE_CRAFTER_SPEED.get();
    }

    @Nullable
    @Override
    public IItemHandler getConnectedInventory()
    {
        ICraftingPatternContainer proxy = getRootContainer();
        if(proxy == null)
            return null;

        return WorldUtils.getItemHandler(proxy.getFacingTile(), proxy.getDirection().getOpposite());
    }

    @Nullable
    @Override
    public IFluidHandler getConnectedFluidInventory()
    {
        ICraftingPatternContainer proxy = getRootContainer();
        if(proxy == null)
            return null;

        return WorldUtils.getFluidHandler(proxy.getFacingTile(), proxy.getDirection().getOpposite());
    }

    @Nullable
    @Override
    public TileEntity getConnectedTile()
    {
        ICraftingPatternContainer proxy = getRootContainer();
        if(proxy == null)
            return null;

        return proxy.getFacingTile();
    }

    @Nullable
    @Override
    public TileEntity getFacingTile()
    {
        BlockPos facingPos = pos.relative(getDirection());
        if (!world.isLoaded(facingPos))
            return null;

        return world.getBlockEntity(facingPos);
    }

    @Override
    public List<ICraftingPattern> getPatterns()
    {
        return Arrays.stream(patterns)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }

    @Nullable
    @Override
    public IItemHandlerModifiable getPatternInventory()
    {
        return patternsInventory;
    }

    @Override
    public ITextComponent getName()
    {
        if (displayName != null)
            return displayName;

        TileEntity facing = getConnectedTile();

        if (facing instanceof INameable && ((INameable) facing).getName() != null)
            return ((INameable) facing).getName();

        if (facing != null)
            return new TranslationTextComponent(world.getBlockState(facing.getBlockPos()).getBlock().getDescriptionId());

        return DEFAULT_NAME;
    }

    public void setDisplayName(ITextComponent displayName)
    {
        this.displayName = displayName;
    }

    @Nullable
    public ITextComponent getDisplayName()
    {
        return displayName;
    }

    @Override
    public BlockPos getPosition()
    {
        return pos;
    }

    public CrafterMode getMode()
    {
        return mode;
    }

    public void setMode(CrafterMode mode)
    {
        this.mode = mode;
        this.wasPowered = false;
        this.locked = false;

        markDirty();
    }

    public IItemHandler getPatternItems()
    {
        return patternsInventory;
    }

    @Nullable
    @Override
    public IItemHandler getDrops()
    {
        return new CombinedInvWrapper(patternsInventory);
    }

    @Nullable
    @Override
    public ICraftingPatternContainer getRootContainer() {
        if (visited)
            return null;

        INetworkNode facing = API.instance().getNetworkNodeManager((ServerWorld) world).getNode(pos.relative(getDirection()));
        if (!(facing instanceof ICraftingPatternContainer) || facing.getNetwork() != network)
            return this;

        visited = true;
        ICraftingPatternContainer facingContainer = ((ICraftingPatternContainer) facing).getRootContainer();
        visited = false;

        return facingContainer;
    }

    public Optional<ICraftingPatternContainer> getRootContainerNotSelf()
    {
        ICraftingPatternContainer root = getRootContainer();

        if (root != null && root != this)
            return Optional.of(root);

        return Optional.empty();
    }

    @Override
    public UUID getUuid()
    {
        if(this.uuid == null)
        {
            this.uuid = UUID.randomUUID();
            markDirty();
        }
        return this.uuid;
    }

    @Override
    public boolean isLocked()
    {
        Optional<ICraftingPatternContainer> root = getRootContainerNotSelf();
        if (root.isPresent())
            return root.get().isLocked();

        switch (mode)
        {
            case SIGNAL_LOCKS_AUTOCRAFTING:
                return world.hasNeighborSignal(pos);
            case SIGNAL_UNLOCKS_AUTOCRAFTING:
                return !world.hasNeighborSignal(pos);
            case PULSE_INSERTS_NEXT_SET:
                return locked;
            default:
                return false;
        }
    }

    @Override
    public void unlock()
    {
        locked = false;
    }

    @Override
    public void onUsedForProcessing()
    {
        Optional<ICraftingPatternContainer> root = getRootContainerNotSelf();
        if (root.isPresent())
        {
            root.get().onUsedForProcessing();
        }
        else if (mode == CrafterMode.PULSE_INSERTS_NEXT_SET)
        {
            this.locked = true;
            markDirty();
        }
    }

    @Override
    public void onSizeChanged(int size) {
        TileDataManager.setParameter(CreativeCrafterTileEntity.SIZE, size);
    }

    @Override
    public void onTabSelectionChanged(int tab) {
        TileDataManager.setParameter(CreativeCrafterTileEntity.TAB_SELECTED, tab);
    }

    @Override
    public void onTabPageChanged(int page) {
        if (page >= 0 && page <= getTotalTabPages()) {
            TileDataManager.setParameter(CreativeCrafterTileEntity.TAB_PAGE, page);
        }
    }

    @Override
    public List<IFilter> getFilters() {
        return filters;
    }

    @Override
    public List<IGridTab> getTabs() {
        return tabs;
    }

    @Override
    public IItemHandlerModifiable getFilter() {
        return filter;
    }

    @Nullable
    @Override
    public CraftingInventory getCraftingMatrix() {
        return null;
    }

    @Nullable
    @Override
    public CraftResultInventory getCraftingResult() {
        return null;
    }

    @Override
    public void onCraftingMatrixChanged() {
        if (!readingInventory) {
            markDirty();
        }
    }

    @Override
    public void onCrafted(PlayerEntity playerEntity, @Nullable IStackList<ItemStack> iStackList, @Nullable IStackList<ItemStack> iStackList1) {

    }

    @Override
    public void onClear(PlayerEntity playerEntity) {

    }

    @Override
    public void onCraftedShift(PlayerEntity playerEntity) {

    }

    @Override
    public void onRecipeTransfer(PlayerEntity playerEntity, ItemStack[][] itemStacks) {

    }

    @Override
    public void onClosed(PlayerEntity playerEntity) {
        // NO OP
    }

    @Override
    public boolean isGridActive() {
        BlockState state = world.getBlockState(pos);

        if (state.getBlock() instanceof CreativeCrafterBlock) {
            return state.getValue(NetworkNodeBlock.CONNECTED);
        }

        return false;
    }

    @Override
    public int getSlotId() {
        return -1;
    }

    @Override
    public GridType getGridType() {
        return GridType.NORMAL;
    }

    @Override
    public IStorageCacheListener createListener(ServerPlayerEntity player) {
        return new ItemGridStorageCacheListener(player, network);
    }

    @Nullable
    @Override
    public IStorageCache getStorageCache() {
        if (network != null) {
            return network.getItemStorageCache();
        }

        return null;
    }

    @Nullable
    @Override
    public IItemGridHandler getItemHandler() {
        return network != null ? network.getItemGridHandler() : null;
    }

    @Nullable
    @Override
    public IFluidGridHandler getFluidHandler() {
        return network != null ? network.getFluidGridHandler() : null;
    }

    @Override
    public ITextComponent getTitle() {
        return new TranslationTextComponent("gui.creativecrafter.creative_crafter");
    }

    public int getViewType() {
        return this.world.isClientSide ? CreativeCrafterTileEntity.VIEW_TYPE.getValue() : this.viewType;
    }

    public int getSortingDirection() {
        return this.world.isClientSide ? CreativeCrafterTileEntity.SORTING_DIRECTION.getValue() : this.sortingDirection;
    }

    public int getSortingType() {
        return this.world.isClientSide ? CreativeCrafterTileEntity.SORTING_TYPE.getValue() : this.sortingType;
    }

    public int getSearchBoxMode() {
        return this.world.isClientSide ? CreativeCrafterTileEntity.SEARCH_BOX_MODE.getValue() : this.searchBoxMode;
    }

    public int getSize() {
        return this.world.isClientSide ? CreativeCrafterTileEntity.SIZE.getValue() : this.size;
    }

    @Override
    public void onViewTypeChanged(int type) {
        TileDataManager.setParameter(CreativeCrafterTileEntity.VIEW_TYPE, type);
    }

    @Override
    public void onSortingTypeChanged(int type) {
        TileDataManager.setParameter(CreativeCrafterTileEntity.SORTING_TYPE, type);
    }

    @Override
    public void onSortingDirectionChanged(int direction) {
        TileDataManager.setParameter(CreativeCrafterTileEntity.SORTING_DIRECTION, direction);
    }

    @Override
    public void onSearchBoxModeChanged(int searchBoxMode) {
        TileDataManager.setParameter(CreativeCrafterTileEntity.SEARCH_BOX_MODE, searchBoxMode);
    }

    public int getTabSelected() {
        return this.world.isClientSide ? CreativeCrafterTileEntity.TAB_SELECTED.getValue() : this.tabSelected;
    }

    public int getTabPage() {
        return this.world.isClientSide ? CreativeCrafterTileEntity.TAB_PAGE.getValue() : Math.min(this.tabPage, this.getTotalTabPages());
    }

    public int getTotalTabPages() {
        return (int)Math.floor(((float)Math.max(0, this.tabs.size() - 1) / 5.0F));
    }

    public void setViewType(int viewType) {
        this.viewType = viewType;
    }

    public void setSortingDirection(int sortingDirection) {
        this.sortingDirection = sortingDirection;
    }

    public void setSortingType(int sortingType) {
        this.sortingType = sortingType;
    }

    public void setSearchBoxMode(int searchBoxMode) {
        this.searchBoxMode = searchBoxMode;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public void setTabSelected(int tabSelected) {
        this.tabSelected = tabSelected;
    }

    public void setTabPage(int page) {
        this.tabPage = page;
    }
}
