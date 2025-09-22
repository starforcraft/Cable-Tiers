package com.ultramega.cabletiers.common.utils;

import com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedResourceAmount;

import java.util.List;
import java.util.Optional;

public interface SidedInput {
    void cabletiers$setSidedResources(List<Optional<SidedResourceAmount>> sidedResources);

    void cabletiers$removeSidedResources(int index);

    List<Optional<SidedResourceAmount>> cabletiers$getSidedResources();
}
