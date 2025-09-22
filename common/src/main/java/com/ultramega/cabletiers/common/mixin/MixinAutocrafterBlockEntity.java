package com.ultramega.cabletiers.common.mixin;

import com.refinedmods.refinedstorage.api.autocrafting.task.ExternalPatternSink;
import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.impl.node.AbstractNetworkNode;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.autocrafting.PlatformPatternProviderExternalPatternSink;
import com.refinedmods.refinedstorage.common.autocrafting.PatternInventory;
import com.refinedmods.refinedstorage.common.autocrafting.autocrafter.AutocrafterBlockEntity;
import com.refinedmods.refinedstorage.common.autocrafting.autocrafter.LockMode;
import com.refinedmods.refinedstorage.common.support.network.AbstractBaseNetworkNodeContainerBlockEntity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.refinedmods.refinedstorage.common.support.AbstractDirectionalBlock.tryExtractDirection;
import static com.ultramega.cabletiers.common.autocrafting.autocrafter.TieredAutocrafterBlockEntity.findResult;

@Mixin(AutocrafterBlockEntity.class)
public abstract class MixinAutocrafterBlockEntity extends AbstractBaseNetworkNodeContainerBlockEntity {
    @Shadow(remap = false)
    private LockMode lockMode;
    @Shadow(remap = false)
    @Final
    private PatternInventory patternContainer;
    @Shadow(remap = false)
    private boolean locked;

    @Unique
    private final PlatformPatternProviderExternalPatternSink[] cabletiers$sinks = new PlatformPatternProviderExternalPatternSink[Direction.values().length];

    private MixinAutocrafterBlockEntity(final BlockEntityType type,
                                          final BlockPos pos,
                                          final BlockState state,
                                          final AbstractNetworkNode networkNode) {
        super(type, pos, state, networkNode);
    }

    @Inject(method = "initialize", at = @At("TAIL"))
    private void initialize(final ServerLevel level, final Direction direction, final CallbackInfo ci) {
        for (int i = 0; i < Direction.values().length; i++) {
            final Direction incomingDirection = Direction.values()[i];

            final BlockPos sourcePosition = worldPosition.relative(direction);
            this.cabletiers$sinks[i] = RefinedStorageApi.INSTANCE.getPatternProviderExternalPatternSinkFactory()
                .create(level, sourcePosition, incomingDirection);
        }
    }

    @Inject(method = "calculateLocked", at = @At("RETURN"), cancellable = true, remap = false)
    private void calculateLocked(final CallbackInfoReturnable<Boolean> cir) {
        if (this.lockMode == LockMode.LOCK_UNTIL_CONNECTED_MACHINE_IS_EMPTY) {
            cir.setReturnValue(
                !Arrays.stream(cabletiers$sinks)
                    .filter(Objects::nonNull)
                    .allMatch(PlatformPatternProviderExternalPatternSink::isEmpty)
            );
        }
    }

    /**
     * @author Ultramega
     * @reason Add support for Sided Input
     */
    @Overwrite(remap = false)
    public ExternalPatternSink.Result accept(final Collection<ResourceAmount> resources, final Action action) {
        final AutocrafterBlockEntity root = getChainingRoot();
        if (root != (Object) this) {
            return root.accept(resources, action);
        }
        if (Arrays.stream(cabletiers$sinks).allMatch(Objects::isNull)) {
            return ExternalPatternSink.Result.SKIPPED;
        }
        if (locked) {
            return ExternalPatternSink.Result.LOCKED;
        }

        final Direction baseDirection = tryExtractDirection(getBlockState());

        return findResult(cabletiers$sinks, patternContainer, baseDirection, resources, action, this::updateLockedAfterAccept);
    }

    @Shadow(remap = false)
    protected abstract AutocrafterBlockEntity getChainingRoot();

    @Shadow(remap = false)
    protected abstract void updateLockedAfterAccept(Action action, ExternalPatternSink.Result result);
}
