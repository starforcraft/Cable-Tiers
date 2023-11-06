package com.ultramega.cabletiers.blockentity;

import com.refinedmods.refinedstorage.blockentity.config.IType;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationParameter;
import com.refinedmods.refinedstorage.blockentity.data.BlockEntitySynchronizationSpec;
import com.ultramega.cabletiers.CableTier;
import com.ultramega.cabletiers.CableTiers;
import com.ultramega.cabletiers.ContentType;
import com.ultramega.cabletiers.node.TieredRequesterNetworkNode;
import com.ultramega.cabletiers.screen.TieredRequesterScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.state.BlockState;

public class TieredRequesterBlockEntity extends TieredBlockEntity<TieredRequesterNetworkNode> {
    public static final BlockEntitySynchronizationParameter<Integer, TieredRequesterBlockEntity> TYPE = IType.createParameter(new ResourceLocation(CableTiers.MOD_ID, "tiered_requester_type"));
    public static final BlockEntitySynchronizationParameter<Integer, TieredRequesterBlockEntity> AMOUNT = new BlockEntitySynchronizationParameter<>(new ResourceLocation(CableTiers.MOD_ID, "tiered_requester_amount"), EntityDataSerializers.INT, 0, t -> t.getNode().getAmount(), (t, v) -> t.getNode().setAmount(v), (initial, p) -> Minecraft.getInstance().tell(() -> {
        if (Minecraft.getInstance().screen instanceof TieredRequesterScreen screen) {
            screen.getTextField().setValue(String.valueOf(p));
        }
    }));
    public static final BlockEntitySynchronizationParameter<Boolean, TieredRequesterBlockEntity> MISSING = new BlockEntitySynchronizationParameter<>(new ResourceLocation(CableTiers.MOD_ID, "tiered_requester_missing"), EntityDataSerializers.BOOLEAN, false, tileRequester -> tileRequester.getNode().isMissingItems(), (tileRequester, aBoolean) -> {
    });

    public static BlockEntitySynchronizationSpec SPEC = BlockEntitySynchronizationSpec.builder()
            .addWatchedParameter(REDSTONE_MODE)
            .addWatchedParameter(TYPE)
            .addWatchedParameter(AMOUNT)
            .addWatchedParameter(MISSING)
            .build();

    public TieredRequesterBlockEntity(CableTier tier, BlockPos pos, BlockState state) {
        super(ContentType.REQUESTER, tier, pos, state, SPEC);
    }
}
