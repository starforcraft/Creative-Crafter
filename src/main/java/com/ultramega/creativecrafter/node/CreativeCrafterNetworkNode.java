package com.ultramega.creativecrafter.node;

import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPattern;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternContainer;
import com.refinedmods.refinedstorage.api.autocrafting.ICraftingPatternProvider;
import com.refinedmods.refinedstorage.api.network.INetwork;
import com.refinedmods.refinedstorage.api.network.node.INetworkNode;
import com.refinedmods.refinedstorage.apiimpl.API;
import com.refinedmods.refinedstorage.apiimpl.network.node.ConnectivityStateChangeCause;
import com.refinedmods.refinedstorage.apiimpl.network.node.CrafterNetworkNode;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.validator.PatternItemValidator;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.util.LevelUtils;
import com.refinedmods.refinedstorage.util.StackUtils;
import com.ultramega.creativecrafter.CreativeCrafter;
import com.ultramega.creativecrafter.config.CreativeCrafterConfig;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class CreativeCrafterNetworkNode extends NetworkNode implements ICraftingPatternContainer {
    public static final ResourceLocation ID = new ResourceLocation(CreativeCrafter.MOD_ID, "creative_crafter");

    private static final Component DEFAULT_NAME = Component.translatable("gui.creativecrafter.creative_crafter");

    private static final String NBT_DISPLAY_NAME = "DisplayName";
    private static final String NBT_UUID = "CrafterUuid";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_LOCKED = "Locked";
    private static final String NBT_WAS_POWERED = "WasPowered";

    private final List<ICraftingPattern> patterns = new ArrayList<>();

    // Used to prevent infinite recursion on getRootContainer() when there's e.g. two crafters facing each other.
    private boolean visited = false;
    private final BaseItemHandler patternsInventory = new BaseItemHandler(CreativeCrafter.SIZE) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Nonnull
        @Override
        public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
            if (!stacks.get(slot).isEmpty()) {
                return stack;
            }

            return super.insertItem(slot, stack, simulate);
        }
    }.addValidator(new PatternItemValidator(level))
            .addListener(new NetworkNodeInventoryListener(this))
            .addListener((handler, slot, reading) -> {
                if (!reading) {
                    if (!level.isClientSide) {
                        invalidate();
                    }

                    if (network != null) {
                        network.getCraftingManager().invalidate();
                    }
                }
            });

    private CrafterNetworkNode.CrafterMode mode = CrafterNetworkNode.CrafterMode.IGNORE;
    private boolean locked = false;
    private boolean wasPowered;
    @Nullable
    private Component displayName;
    @Nullable
    private UUID uuid = null;

    public CreativeCrafterNetworkNode(Level level, BlockPos pos) {
        super(level, pos);
    }

    private void invalidate() {
        patterns.clear();

        for (int i = 0; i < patternsInventory.getSlots(); ++i) {
            ItemStack patternStack = patternsInventory.getStackInSlot(i);

            if (!patternStack.isEmpty()) {
                ICraftingPattern pattern = ((ICraftingPatternProvider) patternStack.getItem()).create(level, patternStack, this);

                if (pattern.isValid()) {
                    patterns.add(pattern);
                }
            }
        }
    }

    @Override
    public int getEnergyUsage() {
        return CreativeCrafterConfig.CREATIVE_CRAFTER_RF_CONSUME.get();
    }

    @Override
    public void update() {
        super.update();

        if (ticks == 1)
            invalidate();

        if (mode == CrafterNetworkNode.CrafterMode.PULSE_INSERTS_NEXT_SET && level.isLoaded(pos)) {
            if (level.hasNeighborSignal(pos)) {
                this.wasPowered = true;

                markDirty();
            } else if (wasPowered) {
                this.wasPowered = false;
                this.locked     = false;

                markDirty();
            }
        }
    }

    @Override
    protected void onConnectedStateChange(INetwork network, boolean state, ConnectivityStateChangeCause cause) {
        super.onConnectedStateChange(network, state, cause);

        network.getCraftingManager().invalidate();
    }

    @Override
    public void onDisconnected(INetwork network) {
        super.onDisconnected(network);

        network.getCraftingManager().getTasks().stream()
                .filter(task -> task.getPattern().getContainer().getPosition().equals(pos))
                .forEach(task -> network.getCraftingManager().cancel(task.getId()));
    }

    @Override
    public void onDirectionChanged(Direction direction) {
        super.onDirectionChanged(direction);

        if (network != null)
            network.getCraftingManager().invalidate();
    }

    @Override
    public void read(CompoundTag tag) {
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
            mode = CrafterNetworkNode.CrafterMode.getById(tag.getInt(NBT_MODE));
        }

        if (tag.contains(NBT_LOCKED)) {
            locked = tag.getBoolean(NBT_LOCKED);
        }

        if (tag.contains(NBT_WAS_POWERED)) {
            wasPowered = tag.getBoolean(NBT_WAS_POWERED);
        }
    }

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public CompoundTag write(CompoundTag tag) {
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
    public int getUpdateInterval() {
        return 0;
    }

    @Override
    public int getMaximumSuccessfulCraftingUpdates() {
        return 60000;
    }

    @Nullable
    @Override
    public IItemHandler getConnectedInventory() {
        ICraftingPatternContainer proxy = getRootContainer();
        if (proxy == null)
            return null;

        return LevelUtils.getItemHandler(proxy.getFacingBlockEntity(), proxy.getDirection().getOpposite());
    }

    @Nullable
    @Override
    public IFluidHandler getConnectedFluidInventory() {
        ICraftingPatternContainer proxy = getRootContainer();
        if (proxy == null)
            return null;

        return LevelUtils.getFluidHandler(proxy.getFacingBlockEntity(), proxy.getDirection().getOpposite());
    }

    @Nullable
    @Override
    public BlockEntity getConnectedBlockEntity() {
        ICraftingPatternContainer proxy = getRootContainer();
        if (proxy == null)
            return null;

        return proxy.getFacingBlockEntity();
    }

    @Override
    public List<ICraftingPattern> getPatterns() {
        return patterns;
    }

    @Nullable
    @Override
    public IItemHandlerModifiable getPatternInventory() {
        return patternsInventory;
    }

    @Nullable
    @Override
    public Component getCustomName() {
        return ICraftingPatternContainer.super.getCustomName();
    }


    @Override
    public Component getName() {
        ICraftingPatternContainer root = getRootContainer();
        if (root != null) {
            Component displayNameOfRoot = root.getCustomName();
            if (displayNameOfRoot != null) {
                return displayNameOfRoot;
            }
        }

        BlockEntity facing = getConnectedBlockEntity();

        if (facing instanceof Nameable nameable && nameable.getName() != null) {
            return nameable.getName();
        }

        if (facing != null) {
            return Component.translatable(level.getBlockState(facing.getBlockPos()).getBlock().getDescriptionId());
        }

        return DEFAULT_NAME;
    }

    @Nullable
    public Component getDisplayName() {
        return displayName;
    }


    public void setDisplayName(Component displayName) {
        this.displayName = displayName;
    }

    @Override
    public BlockPos getPosition() {
        return pos;
    }

    public CrafterNetworkNode.CrafterMode getMode() {
        return mode;
    }

    public void setMode(CrafterNetworkNode.CrafterMode mode) {
        this.mode       = mode;
        this.wasPowered = false;
        this.locked     = false;

        markDirty();
    }

    public IItemHandler getPatternItems() {
        return patternsInventory;
    }

    @Nullable
    @Override
    public IItemHandler getDrops() {
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

    public Optional<ICraftingPatternContainer> getRootContainerNotSelf() {
        ICraftingPatternContainer root = getRootContainer();

        if (root != null && root != this)
            return Optional.of(root);

        return Optional.empty();
    }

    @Override
    public UUID getUuid() {
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID();
            markDirty();
        }
        return this.uuid;
    }

    @Override
    public boolean isLocked() {
        Optional<ICraftingPatternContainer> root = getRootContainerNotSelf();
        if (root.isPresent())
            return root.get().isLocked();

        return switch (mode) {
            case SIGNAL_LOCKS_AUTOCRAFTING -> level.hasNeighborSignal(pos);
            case SIGNAL_UNLOCKS_AUTOCRAFTING -> !level.hasNeighborSignal(pos);
            case PULSE_INSERTS_NEXT_SET -> locked;
            default -> false;
        };
    }

    @Override
    public void unlock() {
        locked = false;
    }

    @Override
    public void onUsedForProcessing() {
        Optional<ICraftingPatternContainer> root = getRootContainerNotSelf();
        if (root.isPresent()) {
            root.get().onUsedForProcessing();

            return;
        }

        if (mode == CrafterNetworkNode.CrafterMode.PULSE_INSERTS_NEXT_SET) {
            this.locked = true;

            markDirty();
        }
    }
}