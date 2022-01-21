package com.YTrollman.CableTiers.blockentity;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.gui.TieredRequesterScreen;
import com.YTrollman.CableTiers.node.TieredRequesterNetworkNode;
import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.world.level.block.state.BlockState;

public class TieredRequesterBlockEntity extends TieredBlockEntity<TieredRequesterNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, TieredRequesterBlockEntity> TYPE = IType.createParameter();
    public static final BlockEntitySynchronizationParameter<Integer, TieredRequesterBlockEntity> AMOUNT = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.INT, 0, t -> t.getNode().getAmount(), (t, v) -> t.getNode().setAmount(v), (initial, p) -> Minecraft.getInstance().tell(() -> {
        if (Minecraft.getInstance().screen instanceof TieredRequesterScreen) {
            ((TieredRequesterScreen) Minecraft.getInstance().screen).getTextField().setValue(String.valueOf(p));
        }
    }));
    public static final BlockEntitySynchronizationParameter<Boolean, TieredRequesterBlockEntity> MISSING = new BlockEntitySynchronizationParameter<>(EntityDataSerializers.BOOLEAN, false, tileRequester -> tileRequester.getNode().isMissingItems(), (tileRequester, aBoolean) -> {
    });

    public TieredRequesterBlockEntity(CableTier tier, BlockPos pos, BlockState state) {
        super(ContentType.REQUESTER, tier, pos, state);

        this.dataManager.addWatchedParameter(TYPE);
        this.dataManager.addWatchedParameter(AMOUNT);
        this.dataManager.addParameter(MISSING);
    }
}
