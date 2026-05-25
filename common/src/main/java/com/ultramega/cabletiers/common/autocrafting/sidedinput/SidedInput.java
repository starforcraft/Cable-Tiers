package com.ultramega.cabletiers.common.autocrafting.sidedinput;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;

import java.util.List;
import java.util.Optional;

import org.jspecify.annotations.Nullable;

public interface SidedInput {
    void cabletiers$setSidedResources(List<Optional<SidedResourceAmount>> sidedResources);

    void cabletiers$replaceSidedResource(@Nullable ResourceAmount resource, int index);

    List<Optional<SidedResourceAmount>> cabletiers$getSidedResources();
}
