package com.ultramega.cabletiers.fabric.support.render;

import com.refinedmods.refinedstorage.fabric.support.render.EmissiveTransform;

import java.util.Set;
import java.util.function.Supplier;

import net.fabricmc.fabric.api.renderer.v1.model.ForwardingBakedModel;
import net.fabricmc.fabric.api.renderer.v1.render.RenderContext;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;

/**
 * Exact copy of {@link com.refinedmods.refinedstorage.fabric.support.render.EmissiveBakedModel}
 */
class EmissiveBakedModel extends ForwardingBakedModel {
    private final EmissiveTransform transform;

    EmissiveBakedModel(final BakedModel wrapped, final Set<ResourceLocation> emissiveSprites) {
        this.wrapped = wrapped;
        this.transform = new EmissiveTransform(emissiveSprites);
    }

    @Override
    public void emitBlockQuads(final BlockAndTintGetter blockView,
                               final BlockState state,
                               final BlockPos pos,
                               final Supplier<RandomSource> randomSupplier,
                               final RenderContext context) {
        context.pushTransform(transform);
        wrapped.emitBlockQuads(blockView, state, pos, randomSupplier, context);
        context.popTransform();
    }

    @Override
    public void emitItemQuads(final ItemStack stack,
                              final Supplier<RandomSource> randomSupplier,
                              final RenderContext context) {
        context.pushTransform(transform);
        wrapped.emitItemQuads(stack, randomSupplier, context);
        context.popTransform();
    }

    @Override
    public boolean isVanillaAdapter() {
        return false;
    }
}
