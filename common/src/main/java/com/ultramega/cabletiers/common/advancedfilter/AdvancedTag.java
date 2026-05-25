package com.ultramega.cabletiers.common.advancedfilter;

import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.resources.Identifier;
import org.jspecify.annotations.Nullable;

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
        return this.mainSlots;
    }

    List<AdvancedTagSlot> getOverflowSlots() {
        return this.overflowSlots;
    }

    public ResourceTag getTag() {
        return this.tag;
    }

    Identifier getId() {
        return this.tag.key().location();
    }

    String getTranslationKey() {
        return this.translationKey;
    }

    List<PlatformResourceKey> getResources() {
        return this.tag.resources();
    }

    double getExpandPct() {
        return this.expandPct;
    }

    boolean expandOrCollapse() {
        if (this.expandCollapse != null) {
            this.expandCollapse = this.expandCollapse == ExpandCollapse.EXPAND ? ExpandCollapse.COLLAPSE : ExpandCollapse.EXPAND;
            this.expandCollapseElapsed = (int) (EXPAND_COLLAPSE_DELAY - this.expandCollapseElapsed);
            return this.expandCollapse == ExpandCollapse.EXPAND;
        }
        this.expandCollapse = this.expandPct > 0 ? ExpandCollapse.COLLAPSE : ExpandCollapse.EXPAND;
        return this.expandCollapse == ExpandCollapse.EXPAND;
    }

    void update() {
        if (this.expandCollapse == null) {
            return;
        }
        ++this.expandCollapseElapsed;
        if (this.expandCollapse == ExpandCollapse.EXPAND) {
            this.expandPct = (double) this.expandCollapseElapsed / EXPAND_COLLAPSE_DELAY;
            if (this.expandPct >= 1) {
                this.stopExpandCollapse();
            }
        } else {
            this.expandPct = 1 - (double) this.expandCollapseElapsed / EXPAND_COLLAPSE_DELAY;
            if (this.expandPct <= 0) {
                this.stopExpandCollapse();
            }
        }
    }

    boolean isVisible() {
        return this.visible;
    }

    void setVisible(final boolean visible) {
        this.visible = visible;
    }

    private void stopExpandCollapse() {
        this.expandPct = this.expandCollapse == ExpandCollapse.EXPAND ? 1 : 0;
        this.expandCollapseElapsed = 0;
        this.expandCollapse = null;
    }

    private enum ExpandCollapse {
        EXPAND,
        COLLAPSE
    }
}
