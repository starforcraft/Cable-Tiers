package com.YTrollman.CableTiers.blocks;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredDiskManipulatorNetworkNode;
import com.YTrollman.CableTiers.tileentity.TieredDiskManipulatorTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;

public class TieredDiskManipulatorBlock extends TieredCableBlock<TieredDiskManipulatorTileEntity, TieredDiskManipulatorNetworkNode> {

    public TieredDiskManipulatorBlock(CableTier tier) {
        super(ContentType.DISK_MANIPULATOR, tier);
    }

    @Override
    public boolean hasConnectedState() {
        return true;
    }

    @Override
    protected VoxelShape getHeadShape(BlockState state) {
        return VoxelShapes.box(0,0,0,1,1,1);
    }
}
