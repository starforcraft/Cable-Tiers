package com.ultramega.cabletiers.neoforge.capability;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;
import com.refinedmods.refinedstorage.mekanism.ChemicalResource;
import com.refinedmods.refinedstorage.mekanism.ChemicalResourceType;
import com.refinedmods.refinedstorage.mekanism.ResourceContainerChemicalHandlerAdapter;

import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;

/**
 * Check for the slot max stack size instead of the interface export limit
 */
public class ImprovedResourceContainerChemicalHandlerAdapter extends ResourceContainerChemicalHandlerAdapter {
    private final ResourceContainer container;

    public ImprovedResourceContainerChemicalHandlerAdapter(final ResourceContainer container) {
        super(container);
        this.container = container;
    }

    @Override
    public void setChemicalInTank(final int tank, final ChemicalStack chemicalStack) {
        if (chemicalStack.isEmpty()) {
            container.remove(tank);
        } else {
            final ChemicalResource resource = ChemicalResource.ofChemicalStack(chemicalStack);
            final long amount = Math.min(chemicalStack.getAmount(),
                Math.max(this.container.getMaxAmount(resource), ChemicalResourceType.INSTANCE.getInterfaceExportLimit()));
            container.set(tank, new ResourceAmount(resource, amount));
        }
    }

    @Override
    public long getChemicalTankCapacity(final int tank) {
        final ResourceKey resource = container.getResource(tank);
        if (resource == null || resource instanceof ChemicalResource) {
            return Math.max(this.container.getMaxAmount(resource == null ? ChemicalResource.ofChemicalStack(ChemicalStack.EMPTY) : resource),
                ChemicalResourceType.INSTANCE.getInterfaceExportLimit());
        }
        return 0;
    }

    @Override
    public ChemicalStack insertChemical(final int tank, final ChemicalStack chemicalStack, final Action action) {
        final ResourceAmount currentResource = container.get(tank);
        if (currentResource == null) {
            return insertChemicalInEmptyTank(tank, chemicalStack, action);
        } else if (currentResource.resource() instanceof ChemicalResource(Chemical otherChemical)
            && otherChemical == chemicalStack.getChemical()) {
            return insertChemicalInFilledTank(tank, chemicalStack, action, currentResource);
        }
        return chemicalStack;
    }

    private ChemicalStack insertChemicalInFilledTank(final int tank,
                                                     final ChemicalStack chemicalStack,
                                                     final Action action,
                                                     final ResourceAmount currentResource) {
        final long currentAmount = currentResource.amount();
        final long toInsert = Math.min(
            chemicalStack.getAmount(),
            Math.max(this.container.getMaxAmount(currentResource.resource()), ChemicalResourceType.INSTANCE.getInterfaceExportLimit()) - currentAmount
        );
        if (toInsert <= 0) {
            return chemicalStack;
        }
        if (action == Action.EXECUTE) {
            container.set(tank, new ResourceAmount(currentResource.resource(), currentAmount + toInsert));
        }
        final long remainder = chemicalStack.getAmount() - toInsert;
        if (remainder <= 0) {
            return ChemicalStack.EMPTY;
        }
        return new ChemicalStack(chemicalStack.getChemical(), remainder);
    }

    private ChemicalStack insertChemicalInEmptyTank(final int tank,
                                                    final ChemicalStack chemicalStack,
                                                    final Action action) {
        final ChemicalResource resource = ChemicalResource.ofChemicalStack(chemicalStack);
        final long toInsert = Math.min(
            chemicalStack.getAmount(),
            Math.max(this.container.getMaxAmount(resource), ChemicalResourceType.INSTANCE.getInterfaceExportLimit())
        );
        if (action == Action.EXECUTE) {
            container.set(tank, new ResourceAmount(resource, toInsert));
        }
        final long remainder = chemicalStack.getAmount() - toInsert;
        if (remainder <= 0) {
            return ChemicalStack.EMPTY;
        }
        return new ChemicalStack(chemicalStack.getChemical(), remainder);
    }
}
