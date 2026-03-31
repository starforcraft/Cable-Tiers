package com.ultramega.cabletiers.common.iface;

import com.ultramega.cabletiers.common.iface.externalstorage.TieredInterfaceExternalStorageProvider;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

public class TieredInterfaceProxyExternalStorageProvider implements TieredInterfaceExternalStorageProvider {
    private final Level level;
    private final BlockPos pos;

    public TieredInterfaceProxyExternalStorageProvider(final Level level, final BlockPos pos) {
        this.level = level;
        this.pos = pos;
    }

    private Optional<TieredInterfaceBlockEntity> tryGetInterface() {
        if (level.getBlockEntity(pos) instanceof TieredInterfaceBlockEntity blockEntity) {
            return Optional.of(blockEntity);
        }
        return Optional.empty();
    }

    private Optional<TieredInterfaceExternalStorageProvider> tryGetProvider() {
        return tryGetInterface().map(TieredInterfaceBlockEntity::getExternalStorageProvider);
    }

    @Override
    public long extract(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        return tryGetProvider().map(provider -> provider.extract(resource, amount, action, actor)).orElse(0L);
    }

    @Override
    public long insert(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        return tryGetProvider().map(provider -> provider.insert(resource, amount, action, actor)).orElse(0L);
    }

    @Override
    public Iterator<ResourceAmount> iterator() {
        return tryGetProvider().map(TieredInterfaceExternalStorageProvider::iterator).orElse(Collections.emptyIterator());
    }

    @Override
    @Nullable
    public TieredInterfaceNetworkNode getInterface() {
        return tryGetInterface().map(TieredInterfaceBlockEntity::getInterface).orElse(null);
    }
}
