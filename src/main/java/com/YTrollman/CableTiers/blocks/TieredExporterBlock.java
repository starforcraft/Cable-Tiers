package com.YTrollman.CableTiers.blocks;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredExporterNetworkNode;
import com.YTrollman.CableTiers.tileentity.TieredExporterTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

public class TieredExporterBlock extends TieredCableBlock<TieredExporterTileEntity, TieredExporterNetworkNode> {

    private static final VoxelShape LINE_NORTH_1 = box(6.0, 6.0, 0.0, 10.0, 10.0, 2.0);
    private static final VoxelShape LINE_NORTH_2 = box(5.0, 5.0, 2.0, 11.0, 11.0, 4.0);
    private static final VoxelShape LINE_NORTH_3 = box(3.0, 3.0, 4.0, 13.0, 13.0, 6.0);
    private static final VoxelShape LINE_NORTH = VoxelShapes.or(LINE_NORTH_1, LINE_NORTH_2, LINE_NORTH_3);
    private static final VoxelShape LINE_EAST_1 = box(14.0, 6.0, 6.0, 16.0, 10.0, 10.0);
    private static final VoxelShape LINE_EAST_2 = box(12.0, 5.0, 5.0, 14.0, 11.0, 11.0);
    private static final VoxelShape LINE_EAST_3 = box(10.0, 3.0, 3.0, 12.0, 13.0, 13.0);
    private static final VoxelShape LINE_EAST = VoxelShapes.or(LINE_EAST_1, LINE_EAST_2, LINE_EAST_3);
    private static final VoxelShape LINE_SOUTH_1 = box(6.0, 6.0, 14.0, 10.0, 10.0, 16.0);
    private static final VoxelShape LINE_SOUTH_2 = box(5.0, 5.0, 12.0, 11.0, 11.0, 14.0);
    private static final VoxelShape LINE_SOUTH_3 = box(3.0, 3.0, 10.0, 13.0, 13.0, 12.0);
    private static final VoxelShape LINE_SOUTH = VoxelShapes.or(LINE_SOUTH_1, LINE_SOUTH_2, LINE_SOUTH_3);
    private static final VoxelShape LINE_WEST_1 = box(0.0, 6.0, 6.0, 2.0, 10.0, 10.0);
    private static final VoxelShape LINE_WEST_2 = box(2.0, 5.0, 5.0, 4.0, 11.0, 11.0);
    private static final VoxelShape LINE_WEST_3 = box(4.0, 3.0, 3.0, 6.0, 13.0, 13.0);
    private static final VoxelShape LINE_WEST = VoxelShapes.or(LINE_WEST_1, LINE_WEST_2, LINE_WEST_3);
    private static final VoxelShape LINE_UP_1 = box(6.0, 14.0, 6.0, 10.0, 16.0, 10.0);
    private static final VoxelShape LINE_UP_2 = box(5.0, 12.0, 5.0, 11.0, 14.0, 11.0);
    private static final VoxelShape LINE_UP_3 = box(3.0, 10.0, 3.0, 13.0, 12.0, 13.0);
    private static final VoxelShape LINE_UP = VoxelShapes.or(LINE_UP_1, LINE_UP_2, LINE_UP_3);
    private static final VoxelShape LINE_DOWN_1 = box(6.0, 0.0, 6.0, 10.0, 2.0, 10.0);
    private static final VoxelShape LINE_DOWN_2 = box(5.0, 2.0, 5.0, 11.0, 4.0, 11.0);
    private static final VoxelShape LINE_DOWN_3 = box(3.0, 4.0, 3.0, 13.0, 6.0, 13.0);
    private static final VoxelShape LINE_DOWN = VoxelShapes.or(LINE_DOWN_1, LINE_DOWN_2, LINE_DOWN_3);

    public TieredExporterBlock(CableTier tier) {
        super(ContentType.EXPORTER, tier);
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
