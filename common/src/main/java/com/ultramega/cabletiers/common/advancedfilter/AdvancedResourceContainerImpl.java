package com.ultramega.cabletiers.common.advancedfilter;

import com.ultramega.cabletiers.common.CableTiers;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceFactory;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;
import com.refinedmods.refinedstorage.common.support.resource.ResourceContainerImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.ToLongFunction;

import net.minecraft.tags.TagKey;
import org.jspecify.annotations.Nullable;

public class AdvancedResourceContainerImpl extends ResourceContainerImpl {
    private static final String TAG_FILTER = "tf";
    private static final String TAG_FILTER_INDICES = "tfi";

    @Nullable
    private ResourceTag[] filterTags;
    @Nullable
    private ResourceAmount[] fakeSlots;
    private int[] fakeStartIndices;
    private int[] fakeShowcaseIndices;

    @Nullable
    private Consumer<Integer> listener;

    public AdvancedResourceContainerImpl(final int size,
                                         final ToLongFunction<ResourceKey> maxAmountProvider,
                                         final ResourceFactory primaryResourceFactory,
                                         final Set<ResourceFactory> alternativeResourceFactories) {
        super(size, maxAmountProvider, primaryResourceFactory, alternativeResourceFactories);
        this.filterTags = new ResourceTag[size];
        this.fakeSlots = new ResourceAmount[size];
        this.fakeStartIndices = new int[size];
        this.fakeShowcaseIndices = new int[size];
        Arrays.fill(this.fakeShowcaseIndices, -1);
    }

    public void setListener(final Consumer<Integer> listener) {
        this.listener = listener;
    }

    @Override
    public void set(final int index, final ResourceAmount resourceAmount) {
        super.set(index, resourceAmount);

        if (this.listener != null) {
            this.listener.accept(index);
        }
    }

    @Override
    public void remove(final int index) {
        super.remove(index);

        this.fakeSlots[index] = null;
        this.fakeStartIndices[index] = 0;
        this.fakeShowcaseIndices[index] = -1;
        if (this.listener != null) {
            this.listener.accept(index);
        }
    }

    @Override
    @Nullable
    public ResourceAmount get(final int index) {
        return this.fakeSlots[index] != null ? this.fakeSlots[index] : super.get(index);
    }

    public void updateFakeSlot(final int index, final ResourceTag tag) {
        if (this.fakeShowcaseIndices[index] == -1) {
            this.fakeShowcaseIndices[index] = this.fakeStartIndices[index];
        }

        int showcasedIndex = ++this.fakeShowcaseIndices[index];
        if (showcasedIndex >= tag.resources().size()) {
            this.fakeShowcaseIndices[index] = 0;
            showcasedIndex = 0;
        }

        this.setFake(index, new ResourceAmount(tag.resources().get(showcasedIndex), 1));
    }

    public void setFakeStartIndex(final int index, final int fakeStartIndex) {
        this.fakeStartIndices[index] = fakeStartIndex; //TODO: WHY THE FUCK IS THIS NOT SAVED??
    }

    public void setFakeShowcaseIndex(final int index, final int fakeShowcaseIndex) {
        this.fakeShowcaseIndices[index] = fakeShowcaseIndex;
    }

    public int getFakeShowcaseIndex(final int index) {
        if (this.fakeShowcaseIndices[index] == -1) {
            this.fakeShowcaseIndices[index] = this.fakeStartIndices[index];
        }

        return this.fakeShowcaseIndices[index];
    }

    public void setFilterTag(final int index, @Nullable final ResourceTag resourceTag) {
        this.filterTags[index] = resourceTag;
    }

    public @Nullable TagKey<?> getFilterTag(final int index) {
        return this.filterTags[index] != null ? this.filterTags[index].key() : null;
    }

    public List<@Nullable ResourceTag> getFilterTagsWithNull() {
        return new ArrayList<>(Arrays.asList(this.filterTags));
    }

    public List<ResourceTag> getFilterTags() {
        final List<ResourceTag> tags = new ArrayList<>();

        for (int i = 0; i < this.size(); ++i) {
            final PlatformResourceKey slot = this.getResource(i);
            if (slot == null) {
                continue;
            }

            final ResourceTag filterTag = this.filterTags[i];
            tags.add(filterTag);
        }

        return tags;
    }

    public Set<TagKey<?>> getUniqueFilterTags() {
        final Set<TagKey<?>> uniqueTags = new HashSet<>();

        for (int i = 0; i < this.size(); ++i) {
            final PlatformResourceKey slot = this.getResource(i);
            if (slot == null) {
                continue;
            }

            final ResourceTag filterTag = this.filterTags[i];
            uniqueTags.add(filterTag != null ? filterTag.key() : null);
        }

        return uniqueTags;
    }

    public void setFake(final int index, @Nullable final ResourceAmount resourceAmount) {
        this.fakeSlots[index] = resourceAmount;
    }

    public void resetFakeFilters() {
        this.fakeSlots = new ResourceAmount[this.fakeSlots.length];
        this.fakeStartIndices = new int[this.fakeSlots.length];
        this.fakeShowcaseIndices = new int[this.fakeSlots.length];
        Arrays.fill(this.fakeShowcaseIndices, -1);
    }

    public void load(final TagResourceContainerContents contents) {
        super.load(contents.resourceContents());
        this.filterTags = contents.filterTags()
            .stream()
            .map(optional -> optional.orElse(null))
            .toArray(ResourceTag[]::new);
        this.fakeStartIndices = contents.fakeStartIndices()
            .stream()
            .mapToInt(Integer::intValue)
            .toArray();
    }

    public List<Integer> getFakeStartIndices() {
        return Arrays.stream(this.fakeStartIndices).boxed().toList();
    }

    public static AdvancedResourceContainerImpl createForFilter(final CableTiers tier) {
        return createForFilter(tier.getFilterSlotCount());
    }

    public static AdvancedResourceContainerImpl createForFilter(final int size) {
        return new AdvancedResourceContainerImpl(
            size,
            resource -> Long.MAX_VALUE,
            RefinedStorageApi.INSTANCE.getItemResourceFactory(),
            RefinedStorageApi.INSTANCE.getAlternativeResourceFactories()
        );
    }
}
