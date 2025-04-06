package com.ultramega.cabletiers.common.advancedfilter;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.utils.ModCodecs;

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
import javax.annotation.Nullable;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.tags.TagKey;

public class AdvancedResourceContainerImpl extends ResourceContainerImpl {
    private static final String TAG_FILTER = "tf";
    private static final String TAG_FILTER_INDICES = "tfi";

    private ResourceTag[] filterTags;
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
        Arrays.fill(fakeShowcaseIndices, -1);
    }

    public void setListener(final Consumer<Integer> listener) {
        this.listener = listener;
    }

    @Override
    public void set(final int index, final ResourceAmount resourceAmount) {
        super.set(index, resourceAmount);

        if (listener != null) {
            listener.accept(index);
        }
    }

    @Override
    public void remove(final int index) {
        super.remove(index);

        fakeSlots[index] = null;
        fakeStartIndices[index] = 0;
        fakeShowcaseIndices[index] = -1;
        if (listener != null) {
            listener.accept(index);
        }
    }

    @Override
    @Nullable
    public ResourceAmount get(final int index) {
        return fakeSlots[index] != null ? fakeSlots[index] : super.get(index);
    }

    public void updateFakeSlot(final int index, final ResourceTag tag) {
        if (fakeShowcaseIndices[index] == -1) {
            fakeShowcaseIndices[index] = fakeStartIndices[index];
        }

        int showcasedIndex = ++fakeShowcaseIndices[index];
        if (showcasedIndex >= tag.resources().size()) {
            fakeShowcaseIndices[index] = 0;
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
        if (fakeShowcaseIndices[index] == -1) {
            fakeShowcaseIndices[index] = fakeStartIndices[index];
        }

        return fakeShowcaseIndices[index];
    }

    public void setFilterTag(final int index, @Nullable final ResourceTag resourceTag) {
        filterTags[index] = resourceTag;
    }

    public @Nullable TagKey<?> getFilterTag(final int index) {
        return filterTags[index] != null ? filterTags[index].key() : null;
    }

    public List<ResourceTag> getFilterTagsWithNull() {
        return new ArrayList<>(Arrays.asList(filterTags));
    }

    public List<ResourceTag> getFilterTags() {
        final List<ResourceTag> tags = new ArrayList<>();

        for (int i = 0; i < size(); ++i) {
            final PlatformResourceKey slot = getResource(i);
            if (slot == null) {
                continue;
            }

            tags.add(filterTags[i] != null ? filterTags[i] : null);
        }

        return tags;
    }

    public Set<TagKey<?>> getUniqueFilterTags() {
        final Set<TagKey<?>> uniqueTags = new HashSet<>();

        for (int i = 0; i < size(); ++i) {
            final PlatformResourceKey slot = getResource(i);
            if (slot == null) {
                continue;
            }

            uniqueTags.add(filterTags[i] != null ? filterTags[i].key() : null);
        }

        return uniqueTags;
    }

    public void setFake(final int index, @Nullable final ResourceAmount resourceAmount) {
        fakeSlots[index] = resourceAmount;
    }

    public void resetFakeFilters() {
        fakeSlots = new ResourceAmount[fakeSlots.length];
        fakeStartIndices = new int[fakeSlots.length];
        fakeShowcaseIndices = new int[fakeSlots.length];
        Arrays.fill(fakeShowcaseIndices, -1);
    }

    @Override
    public CompoundTag toTag(final HolderLookup.Provider provider) {
        final CompoundTag tag = super.toTag(provider);
        tag.put(TAG_FILTER, filterTagsToTag());
        tag.putIntArray(TAG_FILTER_INDICES, fakeStartIndices);
        return tag;
    }

    @Override
    public void fromTag(final CompoundTag tag, final HolderLookup.Provider provider) {
        super.fromTag(tag, provider);
        this.filterTags = filterTagsFromTag(tag);
        this.fakeStartIndices = tag.getIntArray(TAG_FILTER_INDICES);
    }

    private ListTag filterTagsToTag() {
        final ListTag listTag = new ListTag();
        for (final ResourceTag resourceTag : getFilterTagsWithNull()) {
            if (resourceTag != null) {
                listTag.add(ModCodecs.RESOURCE_TAG_CODEC.encode(resourceTag, NbtOps.INSTANCE, new CompoundTag()).getOrThrow());
            } else {
                listTag.add(new CompoundTag());
            }
        }

        return listTag;
    }

    private ResourceTag[] filterTagsFromTag(final CompoundTag tag) {
        final ListTag listTag = tag.getList(TAG_FILTER, ListTag.TAG_COMPOUND);
        final ResourceTag[] tags = new ResourceTag[listTag.size()];
        for (int i = 0; i < listTag.size(); i++) {
            tags[i] = ModCodecs.RESOURCE_TAG_CODEC.parse(NbtOps.INSTANCE, listTag.getCompound(i)).result().orElse(null);
        }

        return tags;
    }

    public static AdvancedResourceContainerImpl createForFilter(final CableTiers tier) {
        return createForFilter(tier.getFilterSlotsCount());
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
