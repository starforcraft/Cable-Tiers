package com.YTrollman.CableTiers.blocks;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredConstructorNetworkNode;
import com.YTrollman.CableTiers.tileentity.TieredConstructorTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

public class TieredConstructorBlock extends TieredCableBlock<TieredConstructorTileEntity, TieredConstructorNetworkNode> {
    private static final VoxelShape HEAD_NORTH = VoxelShapes.or(box(2, 2, 0, 14, 14, 2), HOLDER_NORTH);
    private static final VoxelShape HEAD_EAST = VoxelShapes.or(box(14, 2, 2, 16, 14, 14), HOLDER_EAST);
    private static final VoxelShape HEAD_SOUTH = VoxelShapes.or(box(2, 2, 14, 14, 14, 16), HOLDER_SOUTH);
    private static final VoxelShape HEAD_WEST = VoxelShapes.or(box(0, 2, 2, 2, 14, 14), HOLDER_WEST);
    private static final VoxelShape HEAD_DOWN = VoxelShapes.or(box(2, 0, 2, 14, 2, 14), HOLDER_DOWN);
    private static final VoxelShape HEAD_UP = VoxelShapes.or(box(2, 14, 2, 14, 16, 14), HOLDER_UP);

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
                return VoxelShapes.empty();
        }
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }
}
