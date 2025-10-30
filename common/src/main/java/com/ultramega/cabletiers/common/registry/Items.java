package com.ultramega.cabletiers.common.registry;

import com.refinedmods.refinedstorage.common.support.BaseBlockItem;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

public final class Items {
    public static final Items INSTANCE = new Items();

    private final List<Supplier<BaseBlockItem>> allTieredImporters = new ArrayList<>();
    private final List<Supplier<BaseBlockItem>> allTieredExporters = new ArrayList<>();
    private final List<Supplier<BaseBlockItem>> allTieredDestructors = new ArrayList<>();
    private final List<Supplier<BaseBlockItem>> allTieredConstructors = new ArrayList<>();
    private final List<Supplier<BaseBlockItem>> allTieredDiskInterfaces = new ArrayList<>();
    private final List<Supplier<BaseBlockItem>> allTieredAutocrafters = new ArrayList<>();
    private final List<Supplier<BaseBlockItem>> allTieredInterfaces = new ArrayList<>();

    private Items() {
    }

    public void addTieredImporter(final Supplier<BaseBlockItem> supplier) {
        allTieredImporters.add(supplier);
    }

    public List<Supplier<BaseBlockItem>> getTieredImporters() {
        return Collections.unmodifiableList(allTieredImporters);
    }

    public void addTieredExporter(final Supplier<BaseBlockItem> supplier) {
        allTieredExporters.add(supplier);
    }

    public List<Supplier<BaseBlockItem>> getTieredExporters() {
        return Collections.unmodifiableList(allTieredExporters);
    }

    public void addTieredDestructor(final Supplier<BaseBlockItem> supplier) {
        allTieredDestructors.add(supplier);
    }

    public List<Supplier<BaseBlockItem>> getTieredDestructors() {
        return Collections.unmodifiableList(allTieredDestructors);
    }

    public void addTieredConstructor(final Supplier<BaseBlockItem> supplier) {
        allTieredConstructors.add(supplier);
    }

    public List<Supplier<BaseBlockItem>> getTieredConstructors() {
        return Collections.unmodifiableList(allTieredConstructors);
    }

    public void addTieredDiskInterface(final Supplier<BaseBlockItem> supplier) {
        allTieredDiskInterfaces.add(supplier);
    }

    public List<Supplier<BaseBlockItem>> getTieredDiskInterfaces() {
        return Collections.unmodifiableList(allTieredDiskInterfaces);
    }

    public void addTieredAutocrafters(final Supplier<BaseBlockItem> supplier) {
        allTieredAutocrafters.add(supplier);
    }

    public List<Supplier<BaseBlockItem>> getTieredAutocrafters() {
        return Collections.unmodifiableList(allTieredAutocrafters);
    }

    public void addTieredInterfaces(final Supplier<BaseBlockItem> supplier) {
        allTieredInterfaces.add(supplier);
    }

    public List<Supplier<BaseBlockItem>> getTieredInterfaces() {
        return Collections.unmodifiableList(allTieredInterfaces);
    }
}
