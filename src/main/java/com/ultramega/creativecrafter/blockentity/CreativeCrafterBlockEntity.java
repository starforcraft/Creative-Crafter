package com.ultramega.creativecrafter.blockentity;

import com.refinedmods.refinedstorage.apiimpl.network.node.CrafterNetworkNode;
import com.refinedmods.refinedstorage.blockentity.NetworkNodeBlockEntity;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.ultramega.creativecrafter.CreativeCrafter;
import com.ultramega.creativecrafter.gui.dataparameter.CreativeCrafterTileDataParameterClientListener;
import com.ultramega.creativecrafter.node.CreativeCrafterNetworkNode;
import com.ultramega.creativecrafter.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CreativeCrafterBlockEntity extends NetworkNodeBlockEntity<CreativeCrafterNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, CreativeCrafterBlockEntity> MODE = new BlockEntitySynchronizationParameter<>(new ResourceLocation(CreativeCrafter.MOD_ID, "creative_crafter_mode"), EntityDataSerializers.INT, CrafterNetworkNode.CrafterMode.IGNORE.ordinal(), t -> t.getNode().getMode().ordinal(), (t, v) -> t.getNode().setMode(CrafterNetworkNode.CrafterMode.getById(v)));
    private static final BlockEntitySynchronizationParameter<Boolean, CreativeCrafterBlockEntity> HAS_ROOT = new BlockEntitySynchronizationParameter<>(new ResourceLocation(CreativeCrafter.MOD_ID, "creative_crafter_has_root"), EntityDataSerializers.BOOLEAN, false, t -> t.getNode().getRootContainerNotSelf().isPresent(), null, (t, v) -> new CreativeCrafterTileDataParameterClientListener().onChanged(t, v));

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
            .addWatchedParameter(REDSTONE_MODE)
            .addWatchedParameter(MODE)
            .addParameter(HAS_ROOT)
            .build();

    private final LazyOptional<IItemHandler> patternsCapability = LazyOptional.of(() -> getNode().getPatternInventory());

    public CreativeCrafterBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CREATIVE_CRAFTER_TILE_ENTITY.get(), pos, state, SPEC, CreativeCrafterNetworkNode.class);
    }

    @Override
    @Nonnull
    public CreativeCrafterNetworkNode createNode(Level level, BlockPos blockPos) {
        return new CreativeCrafterNetworkNode(level, blockPos);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == ForgeCapabilities.ITEM_HANDLER && direction != null && !direction.equals(this.getNode().getDirection())) {
            return patternsCapability.cast();
        }

        return super.getCapability(cap, direction);
    }
}
