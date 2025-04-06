package com.ultramega.cabletiers.common.utils;

import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;

import javax.annotation.Nullable;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;

public interface TagFiltering {
    void setTagFilter(int index, @Nullable ResourceTag resourceTag);

    @Nullable TagKey<?> getTagFilter(int index);

    void resetFakeFilters();

    void sendFilterTagsToClient(ServerPlayer player);

    void setOnChanged(Runnable onChanged);

    void setInContainerMenu(boolean inContainerMenu);
}
