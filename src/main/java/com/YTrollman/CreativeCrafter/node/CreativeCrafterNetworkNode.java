package com.YTrollman.CreativeCrafter.node;

import com.YTrollman.CreativeCrafter.CreativeCrafter;
import com.YTrollman.CreativeCrafter.config.CreativeCrafterConfig;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.validator.PatternItemValidator;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.util.LevelUtils;
import com.refinedmods.refinedstorage.util.StackUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Nameable;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;
import net.minecraftforge.items.wrapper.CombinedInvWrapper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Collectors;

public class CreativeCrafterNetworkNode extends NetworkNode implements ICraftingPatternContainer
{
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

    private final BaseItemHandler patternsInventory = new BaseItemHandler(CreativeCrafter.SIZE)
    {
        @Override
        public int getSlotLimit(int slot)
        {
            return 1;
        }
    }
            .addValidator(new PatternItemValidator(level))
            .addListener(new NetworkNodeInventoryListener(this))
            .addListener((handler, slot, reading) ->
            {
                if (!reading)
                {
                    if (!level.isClientSide)
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
    private Component displayName;

    @Nullable
    private UUID uuid = null;

    public static final ResourceLocation ID = new ResourceLocation(CreativeCrafter.MOD_ID, "creative_crafter");

    private static final Component DEFAULT_NAME = new TranslatableComponent("gui.creativecrafter.creative_crafter");

    public CreativeCrafterNetworkNode(Level level, BlockPos pos)
    {
        super(level, pos);
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

        ICraftingPattern pattern = ((ICraftingPatternProvider) patternStack.getItem()).create(level, patternStack, this);
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

        if (mode == CrafterMode.PULSE_INSERTS_NEXT_SET && level.isLoaded(pos))
        {
            if (level.hasNeighborSignal(pos))
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
    public void read(CompoundTag tag)
    {
        super.read(tag);

        StackUtils.readItems(patternsInventory, 0, tag);

        invalidate();

        if (tag.contains(NBT_DISPLAY_NAME)) {
            displayName = Component.Serializer.fromJson(tag.getString(NBT_DISPLAY_NAME));
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
    }

    @Override
    public ResourceLocation getId()
    {
        return ID;
    }

    @Override
    public CompoundTag write(CompoundTag tag)
    {
        super.write(tag);

        StackUtils.writeItems(patternsInventory, 0, tag);

        if (displayName != null) {
            tag.putString(NBT_DISPLAY_NAME, Component.Serializer.toJson(displayName));
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

        return LevelUtils.getItemHandler(proxy.getFacingBlockEntity(), proxy.getDirection().getOpposite());
    }

    @Nullable
    @Override
    public IFluidHandler getConnectedFluidInventory()
    {
        ICraftingPatternContainer proxy = getRootContainer();
        if(proxy == null)
            return null;

        return LevelUtils.getFluidHandler(proxy.getFacingBlockEntity(), proxy.getDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity getConnectedBlockEntity()
    {
        ICraftingPatternContainer proxy = getRootContainer();
        if(proxy == null)
            return null;

        return proxy.getFacingBlockEntity();
    }

    @Nullable
    @Override
    public BlockEntity getFacingBlockEntity()
    {
        BlockPos facingPos = pos.relative(getDirection());
        if (!level.isLoaded(facingPos))
            return null;

        return level.getBlockEntity(facingPos);
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
    public Component getName()
    {
        if (displayName != null)
            return displayName;

        BlockEntity facing = getConnectedBlockEntity();

        if (facing instanceof Nameable && ((Nameable) facing).getName() != null)
            return ((Nameable) facing).getName();

        if (facing != null)
            return new TranslatableComponent(level.getBlockState(facing.getBlockPos()).getBlock().getDescriptionId());

        return DEFAULT_NAME;
    }

    public void setDisplayName(Component displayName)
    {
        this.displayName = displayName;
    }

    @Nullable
    public Component getDisplayName()
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

        INetworkNode facing = API.instance().getNetworkNodeManager((ServerLevel) level).getNode(pos.relative(getDirection()));
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
                return level.hasNeighborSignal(pos);
            case SIGNAL_UNLOCKS_AUTOCRAFTING:
                return !level.hasNeighborSignal(pos);
            case PULSE_INSERTS_NEXT_SET:
                return locked;
            default:
                return false;
        }
    }

    @Override
    public void unlock()
    {
        locked=false;
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
}