package com.ultramega.cabletiers.common.mixin;

import com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedInput;
import com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedInputCodecs;
import com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedInputPatternState;
import com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedResourceAmount;
import com.ultramega.cabletiers.common.registry.DataComponents;

import com.refinedmods.refinedstorage.common.autocrafting.PatternState;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridBlockEntity;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternType;
import com.refinedmods.refinedstorage.common.grid.AbstractGridBlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PatternGridBlockEntity.class)
public abstract class PatternGridBlockEntityMixin extends AbstractGridBlockEntity implements SidedInput {
    @Unique
    private static final String TAG_SIDED_RESOURCES = "sided_resources";

    // This is only for server side
    @Unique
    private List<Optional<SidedResourceAmount>> cabletiers$sidedResources = new ArrayList<>();

    protected PatternGridBlockEntityMixin(final BlockEntityType<? extends AbstractGridBlockEntity> type,
                                          final BlockPos pos,
                                          final BlockState state,
                                          final long energyUsage) {
        super(type, pos, state, energyUsage);
    }

    @Inject(method = "createProcessingPattern", at = @At("RETURN"), cancellable = true)
    private void createProcessingPattern(final CallbackInfoReturnable<ItemStack> cir) {
        final ItemStack pattern = cir.getReturnValue();
        pattern.set(DataComponents.INSTANCE.getSidedInputPatternState(), new SidedInputPatternState(this.cabletiers$sidedResources));
        cir.setReturnValue(pattern);
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void saveAdditional(final ValueOutput output, final CallbackInfo ci) {
        output.store(TAG_SIDED_RESOURCES, SidedInputCodecs.SIDED_RESOURCE_OPTIONAL_LIST_CODEC, this.cabletiers$sidedResources);
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void loadAdditional(final ValueInput input, final CallbackInfo ci) {
        this.cabletiers$sidedResources.clear();

        input.read(TAG_SIDED_RESOURCES, SidedInputCodecs.SIDED_RESOURCE_OPTIONAL_LIST_CODEC)
            .ifPresent(sidedResources -> this.cabletiers$sidedResources = sidedResources);
    }

    @Inject(method = "copyPattern", at = @At("HEAD"))
    private void copyPattern(final ItemStack stack, final CallbackInfo ci) {
        final PatternState patternState = stack.get(com.refinedmods.refinedstorage.common.content.DataComponents.INSTANCE.getPatternState());
        if (patternState == null) {
            return;
        }

        if (patternState.type() != PatternType.PROCESSING) {
            return;
        }

        final SidedInputPatternState sidedInputState = stack.get(DataComponents.INSTANCE.getSidedInputPatternState());
        if (sidedInputState == null) {
            return;
        }

        this.cabletiers$sidedResources = sidedInputState.sidedResources();
    }

    @Unique
    @Override
    public void cabletiers$setSidedResources(final List<Optional<SidedResourceAmount>> sidedResources) {
        this.cabletiers$sidedResources = sidedResources;
        this.setChanged();
    }

    @Unique
    @Override
    public List<Optional<SidedResourceAmount>> cabletiers$getSidedResources() {
        return this.cabletiers$sidedResources;
    }
}
