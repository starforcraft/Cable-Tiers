package com.ultramega.cabletiers.common.advancedfilter;

import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.getTagTranslationKey;

public class AdvancedTag {
    private static final long EXPAND_COLLAPSE_DELAY = 10;

    private final ResourceTag tag;
    private final String translationKey;
    private final List<AdvancedTagSlot> mainSlots = new ArrayList<>();
    private final List<AdvancedTagSlot> overflowSlots = new ArrayList<>();
    private boolean visible = true;
    private double expandPct;
    private int expandCollapseElapsed;
    @Nullable
    private ExpandCollapse expandCollapse;

    AdvancedTag(final ResourceTag tag) {
        this.tag = tag;
        this.translationKey = getTagTranslationKey(tag.key());
    }

    List<AdvancedTagSlot> getMainSlots() {
        return mainSlots;
    }

    List<AdvancedTagSlot> getOverflowSlots() {
        return overflowSlots;
    }

    public ResourceTag getTag() {
        return tag;
    }

    ResourceLocation getId() {
        return tag.key().location();
    }

    String getTranslationKey() {
        return translationKey;
    }

    List<PlatformResourceKey> getResources() {
        return tag.resources();
    }

    double getExpandPct() {
        return expandPct;
    }

    boolean expandOrCollapse() {
        if (expandCollapse != null) {
            expandCollapse = expandCollapse == ExpandCollapse.EXPAND ? ExpandCollapse.COLLAPSE : ExpandCollapse.EXPAND;
            expandCollapseElapsed = (int) (EXPAND_COLLAPSE_DELAY - expandCollapseElapsed);
            return expandCollapse == ExpandCollapse.EXPAND;
        }
        expandCollapse = expandPct > 0 ? ExpandCollapse.COLLAPSE : ExpandCollapse.EXPAND;
        return expandCollapse == ExpandCollapse.EXPAND;
    }

    void update() {
        if (expandCollapse == null) {
            return;
        }
        ++expandCollapseElapsed;
        if (expandCollapse == ExpandCollapse.EXPAND) {
            expandPct = (double) expandCollapseElapsed / EXPAND_COLLAPSE_DELAY;
            if (expandPct >= 1) {
                stopExpandCollapse();
            }
        } else {
            expandPct = 1 - (double) expandCollapseElapsed / EXPAND_COLLAPSE_DELAY;
            if (expandPct <= 0) {
                stopExpandCollapse();
            }
        }
    }

    boolean isVisible() {
        return visible;
    }

    void setVisible(final boolean visible) {
        this.visible = visible;
    }

    private void stopExpandCollapse() {
        expandPct = expandCollapse == ExpandCollapse.EXPAND ? 1 : 0;
        expandCollapseElapsed = 0;
        expandCollapse = null;
    }

    private enum ExpandCollapse {
        EXPAND,
        COLLAPSE
    }
}
