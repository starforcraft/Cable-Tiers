package com.ultramega.cabletiers.common.utils;

import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import org.jspecify.annotations.Nullable;

public interface TagFiltering {
    void setTagFilter(int index, @Nullable ResourceTag resourceTag);

    @Nullable TagKey<?> getTagFilter(int index);

    void resetFakeFilters();

    void sendFilterTagsToClient(ServerPlayer player);

    void setOnChanged(Runnable onChanged);

    void setInContainerMenu(boolean inContainerMenu);
}
