package com.ultramega.cabletiers.common.autocrafting.sidedinput;

import com.ultramega.cabletiers.common.packet.c2s.SetSidedResourcesOnPatternGridBlockPacket;
import com.ultramega.cabletiers.common.utils.ResourceSlotRendering;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridContainerMenu;
import com.refinedmods.refinedstorage.common.autocrafting.patterngrid.PatternGridScreen;
import com.refinedmods.refinedstorage.common.grid.AutocraftableResourceHint;
import com.refinedmods.refinedstorage.common.grid.screen.AbstractGridScreen;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlot;
import com.refinedmods.refinedstorage.common.support.widget.ScrollbarWidget;
import com.refinedmods.refinedstorage.common.util.ClientPlatformUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedInputUtil.isProcessingInputSlot;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersTranslation;
import static net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED;

public class SidedInputScreen extends Screen {
    private static final Identifier TEXTURE = createCableTiersIdentifier("textures/gui/sided_input.png");
    private static final Identifier SLOT_TEXTURE = createIdentifier("slot");
    private static final Identifier DIRECTION_BUTTON = createIdentifier("widget/side_button/base");
    private static final Identifier DIRECTION_BUTTON_HOVERED = createIdentifier("widget/side_button/hovered");
    private static final Identifier DIRECTION_BUTTON_OVERLAY = createIdentifier("widget/side_button/hover_overlay");

    private static final MutableComponent NONE = createCableTiersTranslation("gui", "omni_side_pattern_grid.none");
    private static final MutableComponent DOWN = createCableTiersTranslation("gui", "omni_side_pattern_grid.down");
    private static final MutableComponent UP = createCableTiersTranslation("gui", "omni_side_pattern_grid.up");
    private static final MutableComponent NORTH = createCableTiersTranslation("gui", "omni_side_pattern_grid.north");
    private static final MutableComponent SOUTH = createCableTiersTranslation("gui", "omni_side_pattern_grid.south");
    private static final MutableComponent WEST = createCableTiersTranslation("gui", "omni_side_pattern_grid.west");
    private static final MutableComponent EAST = createCableTiersTranslation("gui", "omni_side_pattern_grid.east");

    private static final int DIRECTION_SIZE = 14;
    private static final int INDIVIDUAL_SLOT_SIZE = 18;

    private final PatternGridContainerMenu gridContainerMenu;
    private final Component gridTitle;
    private final List<ResourceSlot> processingInputSlots = new ArrayList<>();
    @Nullable
    private List<Component> tooltip = new ArrayList<>();
    private final int imageWidth;
    private final int imageHeight;

    @Nullable
    private ScrollbarWidget scrollbar;

    @Nullable
    private final Direction @Nullable [] directions;
    private int hoveringDirectionRow;
    private int hoveringDirectionColumn;

    private int leftPos;
    private int topPos;

    public SidedInputScreen(final PatternGridContainerMenu gridContainerMenu, final Component gridTitle, final List<Optional<SidedResourceAmount>> sidedResources) {
        super(Component.empty());
        this.gridContainerMenu = gridContainerMenu;
        this.gridTitle = gridTitle;

        for (int i = 0; i < gridContainerMenu.getResourceSlots().size(); ++i) {
            final ResourceSlot slot = gridContainerMenu.getResourceSlots().get(i);
            if (!isProcessingInputSlot(slot)) {
                continue;
            }

            this.processingInputSlots.add(slot);
        }

        this.directions = new Direction[this.processingInputSlots.size()];

        int i = 0;
        for (final Optional<SidedResourceAmount> resource : sidedResources) {
            if (resource.isPresent() && i < this.directions.length) {
                this.directions[i] = resource.get().inputDirection().orElse(null);
            }
            i++;
        }

        this.imageWidth = 140;
        this.imageHeight = 99;
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (this.width - this.imageWidth) / 2;
        this.topPos = (this.height - this.imageHeight) / 2;

        this.scrollbar = this.createScrollbar();
        this.updateScrollbarMaxOffset();
        this.addRenderableWidget(this.scrollbar);
    }

    @Override
    public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float a) {
        super.extractBackground(graphics, mouseX, mouseY, a);
        graphics.blit(GUI_TEXTURED, TEXTURE, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
    }

    @Override
    public void extractRenderState(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float partialTick) {
        super.extractRenderState(graphics, mouseX, mouseY, partialTick);

        graphics.enableScissor(this.leftPos - 50, this.getSlotsViewportTop(), this.leftPos + this.imageWidth + 50, this.getSlotsViewportBottom());
        this.extractSlotsAndDirections(graphics, mouseX, mouseY);
        graphics.disableScissor();

        if (this.scrollbar != null) {
            this.scrollbar.extractRenderState(graphics, mouseX, mouseY, partialTick);
        }

        this.renderHoveredResourceTooltip(graphics, mouseX, mouseY);
    }

    private void extractSlotsAndDirections(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY) {
        final int xx = this.leftPos + 5;
        final int viewportTop = this.getSlotsViewportTop();
        final int viewportBottom = this.getSlotsViewportBottom();
        final boolean mouseInsideViewport = this.isInsideSlotsViewport(mouseX, mouseY);

        boolean anyHovered = false;

        for (int i = 0; i < this.processingInputSlots.size(); ++i) {
            final ResourceSlot slot = this.processingInputSlots.get(i);
            if (this.directions == null || this.directions.length <= i) {
                return;
            }

            final int yy = this.getYForScrollbarOffset(i);
            if (yy + INDIVIDUAL_SLOT_SIZE <= viewportTop || yy >= viewportBottom) {
                continue;
            }
            final int slotX = xx + 1;
            final int slotY = yy + 1;

            final boolean hovering = mouseInsideViewport && this.isHovering(slotX, slotY, INDIVIDUAL_SLOT_SIZE - 2, INDIVIDUAL_SLOT_SIZE - 2, mouseX, mouseY);

            graphics.blitSprite(GUI_TEXTURED, SLOT_TEXTURE, xx, yy, INDIVIDUAL_SLOT_SIZE, INDIVIDUAL_SLOT_SIZE);

            if (slot.getResource() != null && this.gridContainerMenu.getRepository().isSticky(slot.getResource())) {
                AbstractGridScreen.renderSlotBackground(graphics, slotX, slotY, false, AutocraftableResourceHint.AUTOCRAFTABLE.getColor());
            }
            if (hovering) {
                ClientPlatformUtil.renderSlotHighlightBack(graphics, slotX, slotY);
            }
            final List<Component> resourceTooltips = ResourceSlotRendering.render(graphics, slot, slotX, slotY);
            if (hovering) {
                anyHovered = true;
                ClientPlatformUtil.renderSlotHighlightFront(graphics, slotX, slotY);
                this.tooltip = resourceTooltips;
            }

            // j = -1 is the "none/X" pseudo-direction; j = 0, ..., DIRS.length-1 are the real directions
            final @Nullable Direction selectedDirection = this.directions[i];
            for (int j = -1; j < Direction.values().length; j++) {
                final Direction direction = j == -1 ? null : Direction.values()[j];
                final Component name = direction == null ? NONE : getDirectionName(direction);
                final String shortName = direction == null ? "X" : name.getString().substring(0, 1);

                final int x = slotX + 4 + (DIRECTION_SIZE * (j + 2));
                final int y = slotY + 1;

                final boolean isEmpty = slot.isEmpty();
                final boolean isHovered = mouseInsideViewport && this.isHovering(x + 1, y + 1, DIRECTION_SIZE - 2, DIRECTION_SIZE - 2, mouseX, mouseY);
                final boolean isClicked = selectedDirection == null ? j == -1 : j == selectedDirection.ordinal();

                if (this.renderDirectionButton(graphics, x, y, shortName, isEmpty, isHovered, isClicked)) {
                    anyHovered = true;
                    this.tooltip = List.of(name);
                    this.hoveringDirectionRow = i;
                    this.hoveringDirectionColumn = j;
                }
            }
        }

        if (!anyHovered) {
            this.tooltip = null;
            this.hoveringDirectionRow = -1;
            this.hoveringDirectionColumn = -2;
        }
    }

    private boolean renderDirectionButton(final GuiGraphicsExtractor graphics,
                                          final int x,
                                          final int y,
                                          final String text,
                                          final boolean isEmpty,
                                          final boolean isHovered,
                                          final boolean isClicked) {
        graphics.blitSprite(GUI_TEXTURED, (!isEmpty && (isHovered || isClicked)) ? DIRECTION_BUTTON_HOVERED : DIRECTION_BUTTON, x, y, DIRECTION_SIZE, DIRECTION_SIZE);

        final int textWidth = this.font.width(text);
        final int textX = x + (DIRECTION_SIZE - textWidth) / 2;
        final int textY = y + (DIRECTION_SIZE - this.font.lineHeight) / 2 + 1;
        graphics.text(this.font, text, textX, textY, 0xFFFFFFFF);

        if (isHovered) {
            graphics.blitSprite(GUI_TEXTURED, DIRECTION_BUTTON_OVERLAY, x, y, DIRECTION_SIZE, DIRECTION_SIZE, 0.5F);

            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
        final boolean clickedScrollbar = this.scrollbar != null && this.scrollbar.mouseClicked(event, doubleClick);
        if (clickedScrollbar) {
            return true;
        }

        if (this.directions != null && this.hoveringDirectionRow != -1 && this.hoveringDirectionColumn != -2) {
            this.playClickSound();
            this.directions[this.hoveringDirectionRow] = this.hoveringDirectionColumn != -1 ? Direction.values()[this.hoveringDirectionColumn] : null;
            this.setSidedInputPatternData();
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void mouseMoved(final double mouseX, final double mouseY) {
        if (this.scrollbar != null) {
            this.scrollbar.mouseMoved(mouseX, mouseY);
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(final MouseButtonEvent event) {
        return (this.scrollbar != null && this.scrollbar.mouseReleased(event)) || super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double scrollX, final double scrollY) {
        return (this.scrollbar != null && this.scrollbar.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) || super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private int getSlotsViewportTop() {
        return this.topPos + 6;
    }

    private int getSlotsViewportBottom() {
        // Keep the extra pixel so item-count decorations on the last visible row do not get cut off
        return this.getSlotsViewportTop() + 88 + 1;
    }

    private boolean isInsideSlotsViewport(final double mouseX, final double mouseY) {
        return mouseX >= this.leftPos - 50
            && mouseX < this.leftPos + this.imageWidth + 50
            && mouseY >= this.getSlotsViewportTop()
            && mouseY < this.getSlotsViewportBottom();
    }

    private int getYForScrollbarOffset(final int i) {
        final int scrollbarOffset = this.scrollbar != null ? (int) this.scrollbar.getOffset() : 0;
        final int scrollbarOffsetCorrected = this.scrollbar != null && this.scrollbar.isSmoothScrolling()
            ? scrollbarOffset
            : scrollbarOffset * INDIVIDUAL_SLOT_SIZE;

        return (this.topPos + 5)
            + (i * INDIVIDUAL_SLOT_SIZE)
            - scrollbarOffsetCorrected;
    }

    private void updateScrollbarMaxOffset() {
        if (this.scrollbar == null) {
            return;
        }

        int maxFilledSlots = 0;
        int maxLastFilledSlot = 0;

        for (int i = 0; i < this.processingInputSlots.size(); ++i) {
            final ResourceSlot resourceSlot = this.processingInputSlots.get(i);
            if (resourceSlot.isEmpty()) {
                continue;
            }

            maxFilledSlots++;
            maxLastFilledSlot = i;
        }

        final int maxOffset = Math.max(maxFilledSlots - 4, maxLastFilledSlot - 4);
        final int maxOffsetCorrected = this.scrollbar.isSmoothScrolling()
            ? maxOffset * INDIVIDUAL_SLOT_SIZE
            : maxOffset;

        this.scrollbar.setMaxOffset(maxOffsetCorrected);
        this.scrollbar.setEnabled(maxOffsetCorrected > 0);
    }

    private ScrollbarWidget createScrollbar() {
        final ScrollbarWidget s = new ScrollbarWidget(
            this.leftPos + 127,
            this.topPos + 6,
            ScrollbarWidget.Type.SMALL,
            88
        );
        s.setListener(offset -> this.onScrollbarChanged((int) offset));
        return s;
    }

    private void onScrollbarChanged(final int offset) {
        final int scrollbarOffset = (this.scrollbar != null && this.scrollbar.isSmoothScrolling())
            ? offset
            : offset * INDIVIDUAL_SLOT_SIZE;
        for (int i = 0; i < this.processingInputSlots.size(); ++i) {
            final int slotY = this.topPos
                + 3
                + 10
                + (i * INDIVIDUAL_SLOT_SIZE)
                - scrollbarOffset
                - this.topPos;
            Platform.INSTANCE.setSlotY(this.processingInputSlots.get(i), slotY);
        }
    }

    private void setSidedInputPatternData() {
        if (this.directions == null) {
            return;
        }

        final List<Optional<ResourceAmount>> resources = new ArrayList<>();
        for (final ResourceSlot slot : this.processingInputSlots) {
            if (slot.getResource() != null) {
                resources.add(Optional.of(new ResourceAmount(slot.getResource(), slot.getAmount())));
            } else {
                resources.add(Optional.empty());
            }
        }

        final List<Optional<Direction>> inputSides = Arrays.stream(this.directions)
            .map(Optional::ofNullable)
            .toList();

        final List<Optional<SidedResourceAmount>> sidedResources = new ArrayList<>();
        for (int i = 0; i < resources.size(); ++i) {
            final Optional<ResourceAmount> resource = resources.get(i);
            final Optional<Direction> direction = inputSides.get(i);
            if (resource.isPresent()) {
                sidedResources.add(Optional.of(new SidedResourceAmount(resource.get(), direction)));
            } else {
                sidedResources.add(Optional.empty());
            }
        }

        Platform.INSTANCE.sendPacketToServer(new SetSidedResourcesOnPatternGridBlockPacket(sidedResources));
    }

    private void renderHoveredResourceTooltip(final GuiGraphicsExtractor graphics,
                                              final int mouseX,
                                              final int mouseY) {
        if (this.tooltip == null || this.tooltip.isEmpty()) {
            return;
        }

        final List<ClientTooltipComponent> processedLines = Platform.INSTANCE.processTooltipComponents(
            ItemStack.EMPTY,
            graphics,
            mouseX,
            Optional.empty(),
            this.tooltip
        );
        graphics.tooltip(this.font, processedLines, mouseX, mouseY, DefaultTooltipPositioner.INSTANCE, null);
    }

    @Override
    public void onClose() {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.setScreen(new PatternGridScreen(this.gridContainerMenu, mc.player.getInventory(), this.gridTitle));
        }
    }

    private void playClickSound() {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0f));
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }

    private boolean isHovering(final int x, final int y, final int width, final int height, final double mouseX, final double mouseY) {
        return mouseX >= (double) (x - 1)
            && mouseX < (double) (x + width + 1)
            && mouseY >= (double) (y - 1)
            && mouseY < (double) (y + height + 1);
    }

    public static MutableComponent getDirectionName(final Direction direction) {
        return switch (direction) {
            case DOWN -> DOWN;
            case UP -> UP;
            case NORTH -> NORTH;
            case SOUTH -> SOUTH;
            case WEST -> WEST;
            case EAST -> EAST;
        };
    }
}
