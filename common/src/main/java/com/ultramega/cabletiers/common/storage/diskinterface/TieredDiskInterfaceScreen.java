package com.ultramega.cabletiers.common.storage.diskinterface;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.mixin.AbstractContainerScreenAccessor;
import com.ultramega.cabletiers.common.support.AbstractAdvancedFilterScreen;

import com.refinedmods.refinedstorage.api.network.impl.node.storagetransfer.StorageTransferMode;
import com.refinedmods.refinedstorage.common.storage.FilterModeSideButtonWidget;
import com.refinedmods.refinedstorage.common.support.containermenu.PropertyTypes;
import com.refinedmods.refinedstorage.common.support.widget.FuzzyModeSideButtonWidget;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;
import static com.ultramega.cabletiers.common.storage.diskinterface.TieredDiskInterfaceContainerMenu.getYIncrease;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;
import static net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED;

public class TieredDiskInterfaceScreen extends AbstractAdvancedFilterScreen<TieredDiskInterfaceContainerMenu> {
    private static final Identifier ELITE_TEXTURE = createCableTiersIdentifier("textures/gui/elite_disk_interface.png");
    private static final Identifier ULTRA_TEXTURE = createCableTiersIdentifier("textures/gui/ultra_disk_interface.png");
    private static final Identifier MEGA_TEXTURE = createCableTiersIdentifier("textures/gui/mega_disk_interface.png");

    private static final MutableComponent IN_TEXT = createTranslation("gui", "disk_interface.in");
    private static final MutableComponent OUT_TEXT = createTranslation("gui", "disk_interface.out");

    public TieredDiskInterfaceScreen(final TieredDiskInterfaceContainerMenu menu,
                                     final Inventory playerInventory,
                                     final Component title,
                                     final CableTiers tier) {
        super(menu, playerInventory, title, tier, tier != CableTiers.CREATIVE);

        this.inventoryLabelY = 117 + getYIncrease(tier);
        ((AbstractContainerScreenAccessor) this).cabletiers$setImageHeight(211 + getYIncrease(tier));
    }

    @Override
    protected void init() {
        super.init();
        this.addSideButton(new TransferModeSideButtonWidget(
            this.getMenu().getProperty(DiskInterfacePropertyTypes.TRANSFER_MODE)
        ));
        this.addSideButton(new FilterModeSideButtonWidget(
            this.getMenu().getProperty(PropertyTypes.FILTER_MODE),
            createTranslation("gui", "disk_interface.filter_mode.allow.help"),
            createTranslation("gui", "disk_interface.filter_mode.block.help")
        ));
        this.addSideButton(new FuzzyModeSideButtonWidget(
            this.getMenu().getProperty(PropertyTypes.FUZZY_MODE),
            () -> this.getMenu().getProperty(DiskInterfacePropertyTypes.TRANSFER_MODE).getValue()
                == StorageTransferMode.EXTRACT_FROM_NETWORK
                ? FuzzyModeSideButtonWidget.Type.EXTRACTING_STORAGE_NETWORK
                : FuzzyModeSideButtonWidget.Type.EXTRACTING_SOURCE
        ));
    }

    @Override
    protected void extractLabels(final GuiGraphicsExtractor graphics, final int x, final int y) {
        super.extractLabels(graphics, x, y);
        graphics.text(this.font, IN_TEXT, 43, 45 + getYIncrease(this.tier), -12566464, false);
        graphics.text(this.font, OUT_TEXT, 115, 45 + getYIncrease(this.tier), -12566464, false);
    }

    @Override
    public void extractDefaultBackground(final GuiGraphicsExtractor graphics) {
        final int x = (this.width - this.imageWidth) / 2;
        final int y = (this.height - this.imageHeight) / 2;
        final int textureSize = this.tier == CableTiers.ELITE ? 256 : 512;
        graphics.blit(GUI_TEXTURED, this.getTexture(), x, y, 0, 0, this.imageWidth, this.imageHeight, textureSize, textureSize);
        this.renderResourceSlots(graphics);
    }

    @Override
    protected Identifier getTexture() {
        return switch (this.tier) {
            case ELITE -> ELITE_TEXTURE;
            case ULTRA -> ULTRA_TEXTURE;
            case MEGA, CREATIVE -> MEGA_TEXTURE;
        };
    }
}
