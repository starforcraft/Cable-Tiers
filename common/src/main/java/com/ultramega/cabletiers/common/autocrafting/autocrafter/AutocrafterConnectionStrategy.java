package com.ultramega.cabletiers.common.autocrafting.autocrafter;

import com.refinedmods.refinedstorage.common.api.support.network.ConnectionSink;
import com.refinedmods.refinedstorage.common.support.network.ColoredConnectionStrategy;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.block.state.BlockState;

import static com.refinedmods.refinedstorage.common.support.AbstractDirectionalBlock.tryExtractDirection;

/**
 * Nearly exact copy of {@link com.refinedmods.refinedstorage.common.autocrafting.autocrafter.AutocrafterConnectionStrategy}
 */
class AutocrafterConnectionStrategy extends ColoredConnectionStrategy {
    AutocrafterConnectionStrategy(final Supplier<BlockState> blockStateProvider, final BlockPos origin) {
        super(blockStateProvider, origin);
    }

    @Override
    public void addOutgoingConnections(final ConnectionSink sink) {
        final Direction myDirection = tryExtractDirection(blockStateProvider.get());
        if (myDirection == null) {
            super.addOutgoingConnections(sink);
            return;
        }
        for (final Direction direction : Direction.values()) {
            if (direction == myDirection) {
                sink.tryConnectInSameDimension(origin.relative(direction), direction.getOpposite(), TieredAutocrafterBlock.class);
            } else {
                sink.tryConnectInSameDimension(origin.relative(direction), direction.getOpposite());
            }
        }
    }

    @Override
    public boolean canAcceptIncomingConnection(final Direction incomingDirection, final BlockState connectingState) {
        if (!colorsAllowConnecting(connectingState)) {
            return false;
        }
        final Direction myDirection = tryExtractDirection(blockStateProvider.get());
        if (myDirection != null) {
            return myDirection != incomingDirection || connectingState.getBlock() instanceof TieredAutocrafterBlock;
        }
        return true;
    }
}
