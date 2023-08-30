package com.ultramega.cabletiers.blockentity;

import com.refinedmods.refinedstorage.blockentity.config.IComparable;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.ultramega.cabletiers.CableTier;
import com.ultramega.cabletiers.CableTiers;
import com.ultramega.cabletiers.ContentType;
import com.ultramega.cabletiers.node.TieredInterfaceNetworkNode;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TieredInterfaceBlockEntity extends TieredBlockEntity<TieredInterfaceNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, TieredInterfaceBlockEntity> COMPARE = IComparable.createParameter(new ResourceLocation(CableTiers.MOD_ID, "tiered_interface_compare"));

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
            .addWatchedParameter(REDSTONE_MODE)
            .addWatchedParameter(COMPARE)
            .build();

    private final LazyOptional<IItemHandler> itemsCapability = LazyOptional.of(() -> getNode().getItems());

    public TieredInterfaceBlockEntity(CableTier tier, BlockPos pos, BlockState state) {
        super(ContentType.INTERFACE, tier, pos, state, SPEC);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction direction) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemsCapability.cast();
        }

        return super.getCapability(cap, direction);
    }
}
