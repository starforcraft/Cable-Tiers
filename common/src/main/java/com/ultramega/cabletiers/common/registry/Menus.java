package com.ultramega.cabletiers.common.registry;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.autocrafting.autocrafter.TieredAutocrafterContainerMenu;
import com.ultramega.cabletiers.common.constructordestructor.TieredConstructorContainerMenu;
import com.ultramega.cabletiers.common.constructordestructor.TieredDestructorContainerMenu;
import com.ultramega.cabletiers.common.exporter.TieredExporterContainerMenu;
import com.ultramega.cabletiers.common.iface.TieredInterfaceContainerMenu;
import com.ultramega.cabletiers.common.importer.TieredImporterContainerMenu;
import com.ultramega.cabletiers.common.storage.diskinterface.TieredDiskInterfaceContainerMenu;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.world.inventory.MenuType;

import static java.util.Objects.requireNonNull;

public final class Menus {
    public static final Menus INSTANCE = new Menus();

    private final Map<CableTiers, Supplier<MenuType<TieredImporterContainerMenu>>> tieredImporters = new HashMap<>();
    private final Map<CableTiers, Supplier<MenuType<TieredExporterContainerMenu>>> tieredExporters = new HashMap<>();
    private final Map<CableTiers, Supplier<MenuType<TieredDestructorContainerMenu>>> tieredDestructors = new HashMap<>();
    private final Map<CableTiers, Supplier<MenuType<TieredConstructorContainerMenu>>> tieredConstructors = new HashMap<>();
    private final Map<CableTiers, Supplier<MenuType<TieredDiskInterfaceContainerMenu>>> tieredDiskInterfaces = new HashMap<>();
    private final Map<CableTiers, Supplier<MenuType<TieredAutocrafterContainerMenu>>> tieredAutocrafters = new HashMap<>();
    private final Map<CableTiers, Supplier<MenuType<TieredInterfaceContainerMenu>>> tieredInterfaces = new HashMap<>();

    public void setTieredImporters(final CableTiers tier,
                                   final Supplier<MenuType<TieredImporterContainerMenu>> supplier) {
        this.tieredImporters.put(tier, supplier);
    }

    public MenuType<TieredImporterContainerMenu> getTieredImporters(final CableTiers tier) {
        return requireNonNull(tieredImporters).get(tier).get();
    }

    public void setTieredExporters(final CableTiers tier,
                                   final Supplier<MenuType<TieredExporterContainerMenu>> supplier) {
        this.tieredExporters.put(tier, supplier);
    }

    public MenuType<TieredExporterContainerMenu> getTieredExporters(final CableTiers tier) {
        return requireNonNull(tieredExporters).get(tier).get();
    }

    public void setTieredDestructors(final CableTiers tier,
                                     final Supplier<MenuType<TieredDestructorContainerMenu>> supplier) {
        this.tieredDestructors.put(tier, supplier);
    }

    public MenuType<TieredDestructorContainerMenu> getTieredDestructors(final CableTiers tier) {
        return requireNonNull(tieredDestructors).get(tier).get();
    }

    public void setTieredConstructors(final CableTiers tier,
                                      final Supplier<MenuType<TieredConstructorContainerMenu>> supplier) {
        this.tieredConstructors.put(tier, supplier);
    }

    public MenuType<TieredConstructorContainerMenu> getTieredConstructors(final CableTiers tier) {
        return requireNonNull(tieredConstructors).get(tier).get();
    }

    public void setTieredDiskInterfaces(final CableTiers tier,
                                        final Supplier<MenuType<TieredDiskInterfaceContainerMenu>> supplier) {
        this.tieredDiskInterfaces.put(tier, supplier);
    }

    public MenuType<TieredDiskInterfaceContainerMenu> getTieredDiskInterfaces(final CableTiers tier) {
        return requireNonNull(tieredDiskInterfaces).get(tier).get();
    }

    public void setTieredAutocrafters(final CableTiers tier,
                                      final Supplier<MenuType<TieredAutocrafterContainerMenu>> supplier) {
        this.tieredAutocrafters.put(tier, supplier);
    }

    public MenuType<TieredAutocrafterContainerMenu> getTieredAutocrafters(final CableTiers tier) {
        return requireNonNull(tieredAutocrafters).get(tier).get();
    }

    public void setTieredInterfaces(final CableTiers tier,
                                    final Supplier<MenuType<TieredInterfaceContainerMenu>> supplier) {
        this.tieredInterfaces.put(tier, supplier);
    }

    public MenuType<TieredInterfaceContainerMenu> getTieredInterfaces(final CableTiers tier) {
        return requireNonNull(tieredInterfaces).get(tier).get();
    }
}
