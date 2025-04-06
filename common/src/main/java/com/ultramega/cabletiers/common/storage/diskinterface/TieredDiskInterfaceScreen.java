package com.ultramega.cabletiers.common.storage.diskinterface;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.support.AbstractAdvancedFilterScreen;

import com.refinedmods.refinedstorage.api.network.impl.node.storagetransfer.StorageTransferMode;
import com.refinedmods.refinedstorage.common.storage.FilterModeSideButtonWidget;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.widget.FuzzyModeSideButtonWidget;
import com.refinedmods.refinedstorage.common.support.widget.RedstoneModeSideButtonWidget;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;

public class TieredDiskInterfaceScreen extends AbstractAdvancedFilterScreen<TieredDiskInterfaceContainerMenu> {
    private static final ResourceLocation ELITE_TEXTURE = createCableTiersIdentifier("textures/gui/elite_disk_interface.png");
    private static final ResourceLocation ULTRA_TEXTURE = createCableTiersIdentifier("textures/gui/ultra_disk_interface.png");
    private static final ResourceLocation MEGA_TEXTURE = createCableTiersIdentifier("textures/gui/mega_disk_interface.png");

    private static final MutableComponent IN_TEXT = createTranslation("gui", "disk_interface.in");
    private static final MutableComponent OUT_TEXT = createTranslation("gui", "disk_interface.out");

    public TieredDiskInterfaceScreen(final TieredDiskInterfaceContainerMenu menu,
                                     final Inventory playerInventory,
                                     final Component title,
                                     final CableTiers tier) {
        super(menu, playerInventory, title, tier);

        this.inventoryLabelY = 117 + getYIncrease(tier);
        this.imageHeight = 211 + getYIncrease(tier);
    }

    @Override
    protected void init() {
        super.init();
        addSideButton(new RedstoneModeSideButtonWidget(getMenu().getProperty(PropertyTypes.REDSTONE_MODE)));
        addSideButton(new TransferModeSideButtonWidget(
            getMenu().getProperty(DiskInterfacePropertyTypes.TRANSFER_MODE)
        ));
        addSideButton(new FilterModeSideButtonWidget(
            getMenu().getProperty(PropertyTypes.FILTER_MODE),
            createTranslation("gui", "disk_interface.filter_mode.allow.help"),
            createTranslation("gui", "disk_interface.filter_mode.block.help")
        ));
        addSideButton(new FuzzyModeSideButtonWidget(
            getMenu().getProperty(PropertyTypes.FUZZY_MODE),
            () -> getMenu().getProperty(DiskInterfacePropertyTypes.TRANSFER_MODE).getValue()
                == StorageTransferMode.EXTRACT_FROM_NETWORK
                ? FuzzyModeSideButtonWidget.Type.EXTRACTING_STORAGE_NETWORK
                : FuzzyModeSideButtonWidget.Type.EXTRACTING_SOURCE
        ));
    }

    @Override
    protected void renderLabels(final GuiGraphics graphics, final int x, final int y) {
        super.renderLabels(graphics, x, y);
        graphics.drawString(font, IN_TEXT, 43, 45 + getYIncrease(tier), 4210752, false);
        graphics.drawString(font, OUT_TEXT, 115, 45 + getYIncrease(tier), 4210752, false);
    }

    @Override
    protected void renderBg(final GuiGraphics graphics, final float delta, final int mouseX, final int mouseY) {
        final int x = (width - imageWidth) / 2;
        final int y = (height - imageHeight) / 2;
        final int textureSize = tier == CableTiers.ELITE ? 256 : 512;
        graphics.blit(getTexture(), x, y, 0, 0, imageWidth, imageHeight, textureSize, textureSize);
        renderResourceSlots(graphics);
    }

    @Override
    protected ResourceLocation getTexture() {
        return switch (tier) {
            case ELITE -> ELITE_TEXTURE;
            case ULTRA -> ULTRA_TEXTURE;
            case MEGA, CREATIVE -> MEGA_TEXTURE;
        };
    }

    @Override
    protected boolean hasUpgrades() {
        return tier != CableTiers.CREATIVE;
    }

    public static int getYIncrease(final CableTiers tier) {
        return ((tier.getFilterSlotsCount() / 9 - 1) * 18);
    }
}
