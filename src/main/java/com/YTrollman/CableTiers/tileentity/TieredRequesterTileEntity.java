package com.YTrollman.CableTiers.tileentity;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.gui.TieredRequesterScreen;
import com.YTrollman.CableTiers.node.TieredRequesterNetworkNode;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.data.TileDataParameter;
import net.minecraft.client.Minecraft;
import net.minecraft.network.datasync.DataSerializers;

public class TieredRequesterTileEntity extends TieredTileEntity<TieredRequesterNetworkNode> {
    public static final TileDataParameter<Integer, TieredRequesterTileEntity> TYPE = IType.createParameter();
    public static final TileDataParameter<Integer, TieredRequesterTileEntity> AMOUNT = new TileDataParameter<>(DataSerializers.INT, 0, t -> t.getNode().getAmount(), (t, v) -> t.getNode().setAmount(v), (initial, p) -> Minecraft.getInstance().tell(() -> {
        if (Minecraft.getInstance().screen instanceof TieredRequesterScreen) {
            ((TieredRequesterScreen) Minecraft.getInstance().screen).getTextField().setValue(String.valueOf(p));
        }
    }));
    public static final TileDataParameter<Boolean, TieredRequesterTileEntity> MISSING = new TileDataParameter<>(DataSerializers.BOOLEAN, false, tileRequester -> tileRequester.getNode().isMissingItems(), (tileRequester, aBoolean) -> {
    });

    public TieredRequesterTileEntity(CableTier tier) {
        super(ContentType.REQUESTER, tier);

        this.dataManager.addWatchedParameter(TYPE);
        this.dataManager.addWatchedParameter(AMOUNT);
        this.dataManager.addParameter(MISSING);
    }
}
