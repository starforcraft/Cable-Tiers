package com.ultramega.cabletiers.common.mixin;

import com.ultramega.cabletiers.common.iface.TieredInterfaceBlock;
import com.ultramega.cabletiers.common.iface.TieredInterfaceProxyExternalStorageProvider;

import com.refinedmods.refinedstorage.api.network.impl.node.externalstorage.ExternalStorageNetworkNode;
import com.refinedmods.refinedstorage.common.storage.externalstorage.AbstractExternalStorageBlockEntity;
import com.refinedmods.refinedstorage.common.support.AbstractCableLikeBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.refinedmods.refinedstorage.common.support.AbstractDirectionalBlock.tryExtractDirection;

@Mixin(AbstractExternalStorageBlockEntity.class)
public abstract class MixinAbstractExternalStorageBlockEntity extends AbstractCableLikeBlockEntity<ExternalStorageNetworkNode> {
    protected MixinAbstractExternalStorageBlockEntity(final BlockEntityType<?> type,
                                                      final BlockPos pos,
                                                      final BlockState state,
                                                      final ExternalStorageNetworkNode networkNode) {
        super(type, pos, state, networkNode);
    }

    @Inject(method = "loadStorage", at = @At("HEAD"), cancellable = true)
    private void loadStorage(final ServerLevel serverLevel, final CallbackInfo ci) {
        final Direction direction = tryExtractDirection(getBlockState());
        if (direction == null) {
            return;
        }
        final BlockPos sourcePosition = worldPosition.relative(direction);
        if (serverLevel.getBlockState(sourcePosition).getBlock() instanceof TieredInterfaceBlock) {
            mainNetworkNode.initialize(new TieredInterfaceProxyExternalStorageProvider(serverLevel, sourcePosition));
            ci.cancel();
        }
    }
}
