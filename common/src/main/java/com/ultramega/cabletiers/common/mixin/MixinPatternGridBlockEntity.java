package com.ultramega.cabletiers.common.mixin;

import com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedInputPatternState;
import com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedResourceAmount;
import com.ultramega.cabletiers.common.registry.DataComponents;
import com.ultramega.cabletiers.common.utils.SidedInput;

import com.refinedmods.refinedstorage.common.autocrafting.PatternState;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridBlockEntity;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternType;
import com.refinedmods.refinedstorage.common.grid.AbstractGridBlockEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PatternGridBlockEntity.class)
public abstract class MixinPatternGridBlockEntity extends AbstractGridBlockEntity implements SidedInput {
    @Unique
    private static final String TAG_SIDED_RESOURCES = "sided_resources";

    @Unique
    private List<Optional<SidedResourceAmount>> cabletiers$sidedResources = new ArrayList<>();

    protected MixinPatternGridBlockEntity(final BlockEntityType<? extends AbstractGridBlockEntity> type,
                                          final BlockPos pos,
                                          final BlockState state,
                                          final long energyUsage) {
        super(type, pos, state, energyUsage);
    }

    @Inject(method = "createProcessingPattern", at = @At("RETURN"), cancellable = true)
    private void createProcessingPattern(final CallbackInfoReturnable<ItemStack> cir) {
        final ItemStack pattern = cir.getReturnValue();
        pattern.set(DataComponents.INSTANCE.getSidedInputPatternState(), new SidedInputPatternState(cabletiers$sidedResources));
        cir.setReturnValue(pattern);
    }

    @Inject(method = "saveAdditional", at = @At("TAIL"))
    private void saveAdditional(final CompoundTag tag, final HolderLookup.Provider provider, final CallbackInfo ci) {
        tag.put(TAG_SIDED_RESOURCES, SidedResourceAmount.OPTIONAL_LIST_CODEC.encode(cabletiers$sidedResources, NbtOps.INSTANCE, new CompoundTag()).getOrThrow());
    }

    @Inject(method = "loadAdditional", at = @At("TAIL"))
    private void loadAdditional(final CompoundTag tag, final HolderLookup.Provider provider, final CallbackInfo ci) {
        cabletiers$sidedResources.clear();

        if (tag.contains(TAG_SIDED_RESOURCES)) {
            this.cabletiers$sidedResources = SidedResourceAmount.OPTIONAL_LIST_CODEC.parse(NbtOps.INSTANCE, tag.getCompound(TAG_SIDED_RESOURCES)).result().orElseThrow();
        }
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
        setChanged();
    }

    @Unique
    @Override
    public List<Optional<SidedResourceAmount>> cabletiers$getSidedResources() {
        return cabletiers$sidedResources;
    }
}
