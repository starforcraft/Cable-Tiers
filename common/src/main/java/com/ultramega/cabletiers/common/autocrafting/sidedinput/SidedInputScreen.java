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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ItemStack;
import org.lwjgl.opengl.GL11;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersTranslation;
import static com.ultramega.cabletiers.common.utils.SidedInputUtil.isProcessingInputSlot;
import static net.minecraft.client.gui.screens.inventory.AbstractContainerScreen.renderSlotHighlight;

public class SidedInputScreen extends Screen {
    private static final ResourceLocation TEXTURE = createCableTiersIdentifier("textures/gui/sided_input.png");
    private static final ResourceLocation SLOT_TEXTURE = createIdentifier("slot");
    private static final ResourceLocation DIRECTION_BUTTON = createIdentifier("widget/side_button/base");
    private static final ResourceLocation DIRECTION_BUTTON_HOVERED = createIdentifier("widget/side_button/hovered");
    private static final ResourceLocation DIRECTION_BUTTON_OVERLAY = createIdentifier("widget/side_button/hover_overlay");

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
    private final Direction[] directions;
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

        this.directions = new Direction[processingInputSlots.size()];

        int i = 0;
        for (final Optional<SidedResourceAmount> resource : sidedResources) {
            if (resource.isPresent() && i < directions.length) {
                directions[i] = resource.get().inputDirection().orElse(null);
            }
            i++;
        }

        this.imageWidth = 140;
        this.imageHeight = 99;
    }

    @Override
    protected void init() {
        super.init();

        this.leftPos = (width - imageWidth) / 2;
        this.topPos = (height - imageHeight) / 2;

        this.scrollbar = createScrollbar();
        updateScrollbarMaxOffset();
        this.addRenderableWidget(scrollbar);
    }

    @Override
    public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTick) {
        super.render(graphics, mouseX, mouseY, partialTick);

        graphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);

        final int startY = topPos + 6;
        // include the edge so we get the item counts properly
        final int endY = topPos + 6 + 88 + 1;
        graphics.enableScissor(leftPos - 50, startY, leftPos + imageWidth + 50, endY);
        renderSlotsAndDirections(graphics, mouseX, mouseY);
        graphics.disableScissor();

        if (scrollbar != null) {
            scrollbar.render(graphics, mouseX, mouseY, partialTick);
        }

        renderHoveredResourceTooltip(graphics, mouseX, mouseY);
    }

    @Override
    public void renderBackground(final GuiGraphics guiGraphics, final int mouseX, final int mouseY, final float partialTick) {
        super.renderTransparentBackground(guiGraphics);
    }

    private void renderSlotsAndDirections(final GuiGraphics graphics, final int mouseX, final int mouseY) {
        final int xx = leftPos + 5;
        final int startY = topPos + 9 - INDIVIDUAL_SLOT_SIZE;
        final int endY = topPos + 9 + (INDIVIDUAL_SLOT_SIZE * 4);

        boolean anyHovered = false;

        for (int i = 0; i < processingInputSlots.size(); ++i) {
            final ResourceSlot slot = processingInputSlots.get(i);
            if (directions == null || directions.length <= i) {
                return;
            }

            final int yy = getYForScrollbarOffset(i);
            if (yy < startY || yy > endY) {
                continue;
            }
            final int slotX = xx + 1;
            final int slotY = yy + 1;

            final boolean hovering = isHovering(slotX, slotY, INDIVIDUAL_SLOT_SIZE - 2, INDIVIDUAL_SLOT_SIZE - 2, mouseX, mouseY);

            graphics.blitSprite(SLOT_TEXTURE, xx, yy, INDIVIDUAL_SLOT_SIZE, INDIVIDUAL_SLOT_SIZE);

            if (slot.getResource() != null && gridContainerMenu.getRepository().isSticky(slot.getResource())) {
                AbstractGridScreen.renderSlotBackground(
                    graphics,
                    slotX,
                    slotY,
                    false,
                    AutocraftableResourceHint.AUTOCRAFTABLE.getColor()
                );
            }
            final List<Component> resourceTooltips = ResourceSlotRendering.render(graphics, slot, slotX, slotY);
            if (hovering) {
                anyHovered = true;
                renderSlotHighlight(graphics, slotX, slotY, 0);
                this.tooltip = resourceTooltips;
            }

            // j = -1 is the "none/X" pseudo-direction; j = 0, ..., DIRS.length-1 are the real directions
            final @Nullable Direction selectedDirection = directions[i];
            for (int j = -1; j < Direction.values().length; j++) {
                final Direction direction = j == -1 ? null : Direction.values()[j];
                final Component name = direction == null ? NONE : getDirectionName(direction);
                final String shortName = direction == null ? "X" : name.getString().substring(0, 1);

                final int x = slotX + 4 + (DIRECTION_SIZE * (j + 2));
                final int y = slotY + 1;

                final boolean isEmpty = slot.isEmpty();
                final boolean isHovered = isHovering(x + 1, y + 1, DIRECTION_SIZE - 2, DIRECTION_SIZE - 2, mouseX, mouseY);
                final boolean isClicked = selectedDirection == null ? j == -1 : j == selectedDirection.ordinal();

                if (renderDirectionButton(graphics, x, y, shortName, isEmpty, isHovered, isClicked)) {
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

    private boolean renderDirectionButton(final GuiGraphics graphics,
                                          final int x,
                                          final int y,
                                          final String text,
                                          final boolean isEmpty,
                                          final boolean isHovered,
                                          final boolean isClicked) {
        graphics.blitSprite((!isEmpty && (isHovered || isClicked)) ? DIRECTION_BUTTON_HOVERED : DIRECTION_BUTTON, x, y, DIRECTION_SIZE, DIRECTION_SIZE);

        final int textWidth = font.width(text);
        final int textX = x + (DIRECTION_SIZE - textWidth) / 2;
        final int textY = y + (DIRECTION_SIZE - font.lineHeight) / 2 + 1;
        graphics.drawString(font, text, textX, textY, 0xFFFFFFFF);

        if (isHovered) {
            RenderSystem.enableBlend();
            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.5F);
            graphics.blitSprite(DIRECTION_BUTTON_OVERLAY, x, y, DIRECTION_SIZE, DIRECTION_SIZE);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.disableBlend();

            return true;
        }
        return false;
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int button) {
        final boolean clickedScrollbar = scrollbar != null && scrollbar.mouseClicked(mouseX, mouseY, button);
        if (clickedScrollbar) {
            return true;
        }

        if (directions != null && hoveringDirectionRow != -1 && hoveringDirectionColumn != -2) {
            playClickSound();
            directions[hoveringDirectionRow] = hoveringDirectionColumn != -1 ? Direction.values()[hoveringDirectionColumn] : null;
            setSidedInputPatternData();
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }

    public void mouseMoved(final double mouseX, final double mouseY) {
        if (scrollbar != null) {
            scrollbar.mouseMoved(mouseX, mouseY);
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        return (scrollbar != null && scrollbar.mouseReleased(mouseX, mouseY, button)) || super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double scrollX, final double scrollY) {
        return (scrollbar != null && scrollbar.mouseScrolled(mouseX, mouseY, scrollX, scrollY)) || super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private int getYForScrollbarOffset(final int i) {
        final int scrollbarOffset = scrollbar != null ? (int) scrollbar.getOffset() : 0;
        final int scrollbarOffsetCorrected = scrollbar != null && scrollbar.isSmoothScrolling()
            ? scrollbarOffset
            : scrollbarOffset * INDIVIDUAL_SLOT_SIZE;

        return (topPos + 5)
            + (i * INDIVIDUAL_SLOT_SIZE)
            - scrollbarOffsetCorrected;
    }

    private void updateScrollbarMaxOffset() {
        if (scrollbar == null) {
            return;
        }

        int maxFilledSlots = 0;
        int maxLastFilledSlot = 0;

        for (int i = 0; i < processingInputSlots.size(); ++i) {
            final ResourceSlot resourceSlot = processingInputSlots.get(i);
            if (resourceSlot.isEmpty()) {
                continue;
            }

            maxFilledSlots++;
            maxLastFilledSlot = i;
        }

        final int maxOffset = Math.max(maxFilledSlots - 4, maxLastFilledSlot - 4);
        final int maxOffsetCorrected = scrollbar.isSmoothScrolling()
            ? maxOffset * INDIVIDUAL_SLOT_SIZE
            : maxOffset;

        scrollbar.setMaxOffset(maxOffsetCorrected);
        scrollbar.setEnabled(maxOffsetCorrected > 0);
    }

    private ScrollbarWidget createScrollbar() {
        final ScrollbarWidget s = new ScrollbarWidget(
            leftPos + 127,
            topPos + 6,
            ScrollbarWidget.Type.SMALL,
            88
        );
        s.setListener(offset -> onScrollbarChanged((int) offset));
        return s;
    }

    private void onScrollbarChanged(final int offset) {
        final int scrollbarOffset = (scrollbar != null && scrollbar.isSmoothScrolling())
            ? offset
            : offset * INDIVIDUAL_SLOT_SIZE;
        for (int i = 0; i < processingInputSlots.size(); ++i) {
            final int slotY = topPos
                + 3
                + 10
                + (i * INDIVIDUAL_SLOT_SIZE)
                - scrollbarOffset
                - topPos;
            Platform.INSTANCE.setSlotY(processingInputSlots.get(i), slotY);
        }
    }

    private void setSidedInputPatternData() {
        if (directions == null) {
            return;
        }

        final List<Optional<ResourceAmount>> resources = new ArrayList<>();
        for (final ResourceSlot slot : processingInputSlots) {
            if (slot.getResource() != null) {
                resources.add(Optional.of(new ResourceAmount(slot.getResource(), slot.getAmount())));
            } else {
                resources.add(Optional.empty());
            }
        }

        final List<Optional<Direction>> inputSides = Arrays.stream(directions)
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

    private void renderHoveredResourceTooltip(final GuiGraphics graphics,
                                              final int mouseX,
                                              final int mouseY) {
        if (tooltip == null) {
            return;
        }

        final List<ClientTooltipComponent> processedLines = Platform.INSTANCE.processTooltipComponents(
            ItemStack.EMPTY,
            graphics,
            mouseX,
            Optional.empty(),
            this.tooltip
        );
        Platform.INSTANCE.renderTooltip(graphics, processedLines, mouseX, mouseY);
    }

    @Override
    public void onClose() {
        final Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) {
            mc.setScreen(new PatternGridScreen(gridContainerMenu, mc.player.getInventory(), gridTitle));
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
