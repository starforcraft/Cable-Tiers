package com.ultramega.cabletiers.blocks;

import com.ultramega.cabletiers.CableTier;
import com.ultramega.cabletiers.ContentType;
import com.ultramega.cabletiers.blockentity.TieredConstructorBlockEntity;
import com.ultramega.cabletiers.node.TieredConstructorNetworkNode;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class TieredConstructorBlock extends TieredCableBlock<TieredConstructorBlockEntity, TieredConstructorNetworkNode> {

    private static final VoxelShape HEAD_NORTH = Shapes.or(box(2, 2, 0, 14, 14, 2), HOLDER_NORTH);
    private static final VoxelShape HEAD_EAST = Shapes.or(box(14, 2, 2, 16, 14, 14), HOLDER_EAST);
    private static final VoxelShape HEAD_SOUTH = Shapes.or(box(2, 2, 14, 14, 14, 16), HOLDER_SOUTH);
    private static final VoxelShape HEAD_WEST = Shapes.or(box(0, 2, 2, 2, 14, 14), HOLDER_WEST);
    private static final VoxelShape HEAD_DOWN = Shapes.or(box(2, 0, 2, 14, 2, 14), HOLDER_DOWN);
    private static final VoxelShape HEAD_UP = Shapes.or(box(2, 14, 2, 14, 16, 14), HOLDER_UP);

    public TieredConstructorBlock(CableTier tier) {
        super(ContentType.CONSTRUCTOR, tier);
    }

    @Override
    protected VoxelShape getHeadShape(BlockState state) {
        switch (state.getValue(getDirection().getProperty())) {
            case NORTH:
                return HEAD_NORTH;
            case EAST:
                return HEAD_EAST;
            case SOUTH:
                return HEAD_SOUTH;
            case WEST:
                return HEAD_WEST;
            case UP:
                return HEAD_UP;
            case DOWN:
                return HEAD_DOWN;
            default:
                return Shapes.empty();
        }
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
