package com.YTrollman.CableTiers.blocks;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredImporterNetworkNode;
import com.YTrollman.CableTiers.tileentity.TieredImporterTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

public class TieredImporterBlock extends TieredCableBlock<TieredImporterTileEntity, TieredImporterNetworkNode> {

    private static final VoxelShape LINE_NORTH_1 = box(6, 6, 4, 10, 10, 6);
    private static final VoxelShape LINE_NORTH_2 = box(5, 5, 2, 11, 11, 4);
    private static final VoxelShape LINE_NORTH_3 = box(3, 3, 0, 13, 13, 2);
    private static final VoxelShape LINE_NORTH = VoxelShapes.or(LINE_NORTH_1, LINE_NORTH_2, LINE_NORTH_3);
    private static final VoxelShape LINE_EAST_1 = box(10, 6, 6, 12, 10, 10);
    private static final VoxelShape LINE_EAST_2 = box(12, 5, 5, 14, 11, 11);
    private static final VoxelShape LINE_EAST_3 = box(14, 3, 3, 16, 13, 13);
    private static final VoxelShape LINE_EAST = VoxelShapes.or(LINE_EAST_1, LINE_EAST_2, LINE_EAST_3);
    private static final VoxelShape LINE_SOUTH_1 = box(6, 6, 10, 10, 10, 12);
    private static final VoxelShape LINE_SOUTH_2 = box(5, 5, 12, 11, 11, 14);
    private static final VoxelShape LINE_SOUTH_3 = box(3, 3, 14, 13, 13, 16);
    private static final VoxelShape LINE_SOUTH = VoxelShapes.or(LINE_SOUTH_1, LINE_SOUTH_2, LINE_SOUTH_3);
    private static final VoxelShape LINE_WEST_1 = box(4, 6, 6, 6, 10, 10);
    private static final VoxelShape LINE_WEST_2 = box(2, 5, 5, 4, 11, 11);
    private static final VoxelShape LINE_WEST_3 = box(0, 3, 3, 2, 13, 13);
    private static final VoxelShape LINE_WEST = VoxelShapes.or(LINE_WEST_1, LINE_WEST_2, LINE_WEST_3);
    private static final VoxelShape LINE_UP_1 = box(6, 10, 6, 10, 12, 10);
    private static final VoxelShape LINE_UP_2 = box(5, 12, 5, 11, 14, 11);
    private static final VoxelShape LINE_UP_3 = box(3, 14, 3, 13, 16, 13);
    private static final VoxelShape LINE_UP = VoxelShapes.or(LINE_UP_1, LINE_UP_2, LINE_UP_3);
    private static final VoxelShape LINE_DOWN_1 = box(6, 4, 6, 10, 6, 10);
    private static final VoxelShape LINE_DOWN_2 = box(5, 2, 5, 11, 4, 11);
    private static final VoxelShape LINE_DOWN_3 = box(3, 0, 3, 13, 2, 13);
    private static final VoxelShape LINE_DOWN = VoxelShapes.or(LINE_DOWN_1, LINE_DOWN_2, LINE_DOWN_3);

    public TieredImporterBlock(CableTier tier) {
        super(ContentType.IMPORTER, tier);
    }

    @Override
    protected VoxelShape getHeadShape(BlockState state) {
        switch (state.getValue(getDirection().getProperty())) {
            case UP:
                return LINE_UP;
            case DOWN:
                return LINE_DOWN;
            case NORTH:
                return LINE_NORTH;
            case SOUTH:
                return LINE_SOUTH;
            case EAST:
                return LINE_EAST;
            case WEST:
                return LINE_WEST;
            default:
                return VoxelShapes.empty();
        }
    }
}
