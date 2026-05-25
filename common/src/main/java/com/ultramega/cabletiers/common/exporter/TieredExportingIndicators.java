package com.ultramega.cabletiers.common.exporter;

import com.ultramega.cabletiers.common.advancedfilter.AdvancedResourceContainerImpl;
import com.ultramega.cabletiers.common.utils.BiIntFunction;

import com.refinedmods.refinedstorage.common.support.exportingindicator.ExportingIndicator;
import com.refinedmods.refinedstorage.common.support.packet.s2c.ExportingIndicatorUpdatePacket;
import com.refinedmods.refinedstorage.common.support.packet.s2c.S2CPackets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;

public class TieredExportingIndicators {
    @Nullable
    private final AdvancedResourceContainerImpl filterContainer;
    private final List<ExportingIndicator> indicators;
    private final BiIntFunction<ExportingIndicator> indicatorProvider;
    private final boolean includeEmptySlots;

    public TieredExportingIndicators(final List<ExportingIndicator> indicators) {
        this.filterContainer = null;
        this.indicators = indicators;
        this.indicatorProvider = (i, j) -> ExportingIndicator.NONE;
        this.includeEmptySlots = false;
    }

    public TieredExportingIndicators(final AdvancedResourceContainerImpl filterContainer,
                                     final BiIntFunction<ExportingIndicator> indicatorProvider,
                                     final boolean includeEmptySlots) {
        this.filterContainer = filterContainer;
        this.indicators = new ArrayList<>();
        this.includeEmptySlots = includeEmptySlots;
        int j = 0;
        for (int i = 0; i < filterContainer.size(); ++i) {
            if (filterContainer.isEmpty(i) && !includeEmptySlots) {
                this.indicators.add(ExportingIndicator.NONE);
                continue;
            }
            final int fakeIndex = filterContainer.getFakeShowcaseIndex(i);
            this.indicators.add(indicatorProvider.apply(j, fakeIndex));
            j++;
        }
        this.indicatorProvider = indicatorProvider;
    }

    public void detectChanges(final ServerPlayer player) {
        if (this.filterContainer == null) {
            return;
        }
        int j = 0;
        List<ExportingIndicatorUpdatePacket.UpdatedIndicator> updatedIndicators = null;
        for (int i = 0; i < this.filterContainer.size(); ++i) {
            if (this.filterContainer.isEmpty(i) && !this.includeEmptySlots) {
                updatedIndicators = this.tryUpdateIndicator(i, ExportingIndicator.NONE, updatedIndicators);
                continue;
            }
            final int fakeIndex = this.filterContainer.getFakeShowcaseIndex(i);
            updatedIndicators = this.tryUpdateIndicator(i, this.indicatorProvider.apply(j, fakeIndex), updatedIndicators);
            j++;
        }
        if (updatedIndicators != null) {
            S2CPackets.sendExportingIndicatorUpdate(player, updatedIndicators);
        }
    }

    @Nullable
    private List<ExportingIndicatorUpdatePacket.UpdatedIndicator> tryUpdateIndicator(
        final int idx,
        final ExportingIndicator indicator,
        @Nullable final List<ExportingIndicatorUpdatePacket.UpdatedIndicator> updatedIndicators) {
        if (this.indicators.get(idx) == indicator) {
            return updatedIndicators;
        }
        final List<ExportingIndicatorUpdatePacket.UpdatedIndicator> result = updatedIndicators == null
            ? new ArrayList<>()
            : updatedIndicators;
        result.add(new ExportingIndicatorUpdatePacket.UpdatedIndicator(idx, indicator));
        this.set(idx, indicator);
        return result;
    }

    public List<ExportingIndicator> getAll() {
        return Collections.unmodifiableList(this.indicators);
    }

    public ExportingIndicator get(final int idx) {
        return this.indicators.get(idx);
    }

    public void set(final int idx, final ExportingIndicator indicator) {
        this.indicators.set(idx, indicator);
    }

    public int size() {
        return this.indicators.size();
    }
}
