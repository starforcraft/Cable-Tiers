package com.ultramega.cabletiers.common.advancedfilter;

import com.ultramega.cabletiers.common.mixin.ActionButtonInvoker;
import com.ultramega.cabletiers.common.packet.c2s.SetAdvancedFilterPacket;

import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;
import com.refinedmods.refinedstorage.common.support.AbstractBaseScreen;
import com.refinedmods.refinedstorage.common.support.ResourceSlotRendering;
import com.refinedmods.refinedstorage.common.support.Sprites;
import com.refinedmods.refinedstorage.common.support.amount.ActionButton;
import com.refinedmods.refinedstorage.common.support.containermenu.ResourceSlot;
import com.refinedmods.refinedstorage.common.support.widget.CheckboxWidget;
import com.refinedmods.refinedstorage.common.support.widget.CustomButton;
import com.refinedmods.refinedstorage.common.support.widget.ScrollbarWidget;
import com.refinedmods.refinedstorage.common.support.widget.SearchIconWidget;
import com.refinedmods.refinedstorage.common.util.ClientPlatformUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import static com.refinedmods.refinedstorage.common.support.Sprites.ICON_SIZE;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersTranslation;
import static net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED;

public class AdvancedFilterScreen extends AbstractBaseScreen<AdvancedFilterContainerMenu> {
    static final int ROW_HEIGHT = 18;
    static final int ADVANCED_TAG_HEIGHT = ROW_HEIGHT * 2;
    static final int RESOURCES_PER_ROW = 9;

    private static final Identifier TEXTURE = createCableTiersIdentifier("textures/gui/advanced_filter.png");
    private static final MutableComponent DONE = createCableTiersTranslation("gui", "advanced_filter.done");
    private static final WidgetSprites EXPAND_SPRITES = new WidgetSprites(
        createIdentifier("widget/expand"),
        createIdentifier("widget/expand_disabled"),
        createIdentifier("widget/expand_focused"),
        createIdentifier("widget/expand_disabled")
    );
    private static final WidgetSprites COLLAPSE_SPRITES = new WidgetSprites(
        createIdentifier("widget/collapse"),
        createIdentifier("widget/collapse_focused")
    );
    private static final MutableComponent EXPAND = createTranslation("gui", "pattern_grid.alternatives.expand");
    private static final Component SEARCH_HELP = createTranslation("gui", "pattern_grid.alternatives.search_help")
        .withStyle(ChatFormatting.GRAY);

    private static final int TAGS_DISPLAYED = 2;
    private static final int ROWS_PER_TAG = 2;
    private static final int INSET_WIDTH = 194;
    private static final int INSET_HEIGHT = 90;

    private static final int ACTION_BUTTON_HEIGHT = 20;
    private static final int ACTION_BUTTON_SPACING = 20;

    @Nullable
    private ScrollbarWidget scrollbar;
    @Nullable
    private EditBox searchField;

    @Nullable
    private final Screen parent;
    private final int slotIndex;

    @Nullable
    private final TagKey<?> selectedTagKey;

    private final List<CheckboxWidget> advancedTagCheckboxes = new ArrayList<>();
    private final List<Button> expandButtons = new ArrayList<>();

    public AdvancedFilterScreen(@Nullable final Screen parent,
                                final Inventory playerInventory,
                                final int slotIndex,
                                @Nullable final TagKey<?> selectedTagKey,
                                @Nullable final PlatformResourceKey selectedResource,
                                final Component title) {
        super(new AdvancedFilterContainerMenu(selectedResource), playerInventory, title, 223, 182);
        this.slotIndex = slotIndex;
        this.parent = parent;
        this.selectedTagKey = selectedTagKey;
    }

    @Override
    protected void init() {
        super.init();
        this.advancedTagCheckboxes.clear();
        this.expandButtons.clear();

        final int x = this.getCheckboxStartX();
        for (int i = 0; i < this.getMenu().getAdvancedTags().size(); ++i) {
            this.addWidgetsForTags(i, x);
        }

        this.addConfirmButton(160, 155);

        this.scrollbar = new ScrollbarWidget(
            this.leftPos + 203,
            this.topPos + 59,
            ScrollbarWidget.Type.NORMAL,
            INSET_HEIGHT
        );
        final int overflowingRows = this.getMenu().getAdvancedTags().size() - TAGS_DISPLAYED;
        final int maxOffset = this.scrollbar.isSmoothScrolling()
            ? overflowingRows * ADVANCED_TAG_HEIGHT
            : overflowingRows * ROWS_PER_TAG;
        this.scrollbar.setMaxOffset(maxOffset);
        this.scrollbar.setEnabled(maxOffset > 0);
        this.scrollbar.setListener(value -> this.updateWidgets());
        this.addWidget(this.scrollbar);

        this.searchField = new EditBox(
            this.font,
            this.leftPos + 24,
            this.topPos + 46,
            193 - 6,
            this.font.lineHeight,
            Component.empty()
        );
        this.searchField.setBordered(false);
        this.searchField.setVisible(true);
        this.searchField.setCanLoseFocus(true);
        this.searchField.setFocused(false);
        this.searchField.setResponder(query -> this.getMenu().filter(query));
        this.addRenderableWidget(this.searchField);

        this.addRenderableWidget(new SearchIconWidget(
            this.leftPos + 7,
            this.topPos + 44,
            () -> SEARCH_HELP,
            this.searchField
        ));
    }

    private int getCheckboxStartX() {
        return this.leftPos + 8;
    }

    private int getCheckboxStartY() {
        return this.topPos + 59;
    }

    private int getAdvancedTagY(final int idx) {
        return this.getCheckboxStartY() + (ADVANCED_TAG_HEIGHT * idx);
    }

    private void addWidgetsForTags(final int idx, final int x) {
        final AdvancedTag advancedTag = this.getMenu().getAdvancedTags().get(idx);
        final int y = this.getAdvancedTagY(idx);
        final boolean hasTranslation = I18n.exists(advancedTag.getTranslationKey());
        final MutableComponent id = Component.literal(advancedTag.getId().toString());
        final CheckboxWidget advancedTagCheckbox = new CheckboxWidget(
            x + 2,
            y + (ROW_HEIGHT / 2) - (9 / 2),
            164 - 2 - 16 - 1 - 4,
            hasTranslation ? Component.translatable(advancedTag.getTranslationKey()) : id,
            this.font,
            this.selectedTagKey != null && this.selectedTagKey.location().equals(advancedTag.getId()),
            CheckboxWidget.Size.SMALL
        );
        advancedTagCheckbox.setOnPressed((checkbox, selected) -> {
            this.advancedTagCheckboxes.forEach(c -> c.setSelected(false));
            checkbox.setSelected(selected);
        });
        if (hasTranslation) {
            advancedTagCheckbox.setTooltip(Tooltip.create(id));
        }
        this.advancedTagCheckboxes.add(this.addWidget(advancedTagCheckbox));
        final CustomButton expandButton = new CustomButton(
            x + INSET_WIDTH - 16 - 1,
            y + 1,
            16,
            16,
            EXPAND_SPRITES,
            btn -> {
                final boolean expanding = advancedTag.expandOrCollapse();
                btn.setSprites(expanding ? COLLAPSE_SPRITES : EXPAND_SPRITES);
            },
            EXPAND
        );
        expandButton.active = advancedTag.getResources().size() > RESOURCES_PER_ROW;
        this.expandButtons.add(this.addWidget(expandButton));
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.getMenu().getAdvancedTags().forEach(AdvancedTag::update);
        this.updateWidgets();
    }

    @Override
    protected void renderResourceSlots(final GuiGraphicsExtractor graphics) {
        ResourceSlotRendering.render(graphics, this.getMenu().getFilterSlot());
    }

    private void updateWidgets() {
        final ScrollbarWidget theScrollbar = this.scrollbar;
        if (theScrollbar == null) {
            return;
        }
        double totalHeight = 0;
        int totalRows = 0;
        final int scrollbarOffset = (int) theScrollbar.getOffset();
        int y = this.getAdvancedTagY(0)
            - (theScrollbar.isSmoothScrolling() ? scrollbarOffset : scrollbarOffset * ROW_HEIGHT);
        for (int i = 0; i < this.getMenu().getAdvancedTags().size(); ++i) {
            final AdvancedTag advancedTag = this.getMenu().getAdvancedTags().get(i);
            final CheckboxWidget advancedTagCheckbox = this.advancedTagCheckboxes.get(i);
            final Button expandButton = this.expandButtons.get(i);

            if (!advancedTag.isVisible()) {
                advancedTagCheckbox.visible = false;
                expandButton.visible = false;
                this.updateAdvancedTagSlots(advancedTag.getMainSlots(), y, 0, false);
                this.updateAdvancedTagSlots(advancedTag.getOverflowSlots(), y, 1, false);
                continue;
            }

            totalRows += ROWS_PER_TAG;
            final int overflowRows = getOverflowRows(advancedTag);
            totalRows += (int) (overflowRows * advancedTag.getExpandPct());
            final int height = ADVANCED_TAG_HEIGHT
                + (int) (overflowRows * ROW_HEIGHT * advancedTag.getExpandPct());

            this.updateAdvancedTagCheckbox(advancedTagCheckbox, y);
            this.updateExpandButton(expandButton, y);
            this.updateAdvancedTagSlots(advancedTag.getMainSlots(), y, 0, true);
            this.updateAdvancedTagSlots(advancedTag.getOverflowSlots(), y, 1, advancedTag.getExpandPct() > 0);

            totalHeight += height;
            y += height;
        }
        final double maxOffset = theScrollbar.isSmoothScrolling()
            ? totalHeight - (ADVANCED_TAG_HEIGHT * TAGS_DISPLAYED)
            : totalRows - 1 - (ROWS_PER_TAG * TAGS_DISPLAYED);
        theScrollbar.setMaxOffset(maxOffset);
        theScrollbar.setEnabled(maxOffset > 0);
    }

    private void updateAdvancedTagCheckbox(final CheckboxWidget advancedTagCheckbox, final int y) {
        advancedTagCheckbox.setY(y + (ROW_HEIGHT / 2) - (9 / 2));
        advancedTagCheckbox.visible = advancedTagCheckbox.getY() >= this.getCheckboxStartY() - advancedTagCheckbox.getHeight()
            && advancedTagCheckbox.getY() < this.getCheckboxStartY() + INSET_HEIGHT;
    }

    private void updateExpandButton(final Button expandButton, final int y) {
        expandButton.setY(y + 1);
        expandButton.visible = expandButton.getY() >= this.getCheckboxStartY() - expandButton.getHeight()
            && expandButton.getY() < this.getCheckboxStartY() + INSET_HEIGHT;
    }

    private void updateAdvancedTagSlots(final List<AdvancedTagSlot> slots,
                                        final int y,
                                        final int rowOffset,
                                        final boolean visible) {
        for (int i = 0; i < slots.size(); i++) {
            final int row = (i / RESOURCES_PER_ROW) + rowOffset;
            final AdvancedTagSlot resourceSlot = slots.get(i);
            Platform.INSTANCE.setSlotY(
                resourceSlot,
                (y + ROW_HEIGHT + (row * 18) + 1) - this.topPos
            );
            resourceSlot.setActive((resourceSlot.y + this.topPos) >= this.getCheckboxStartY() - 18
                && (resourceSlot.y + this.topPos) < this.getCheckboxStartY() + INSET_HEIGHT
                && visible);
        }
    }

    private static int getOverflowRows(final AdvancedTag advancedTag) {
        return Math.ceilDiv(
            advancedTag.getResources().size() - RESOURCES_PER_ROW,
            RESOURCES_PER_ROW
        );
    }

    @Override
    public void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float partialTicks) {
        super.extractContents(graphics, mouseX, mouseY, partialTicks);
        if (this.scrollbar != null) {
            this.scrollbar.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);
        final int x = this.getCheckboxStartX();
        final int y = this.getCheckboxStartY();
        graphics.enableScissor(x, y, x + INSET_WIDTH, y + INSET_HEIGHT);
        int currentY = y - ((this.scrollbar != null ? (int) this.scrollbar.getOffset() : 0)
            * (this.scrollbar != null && this.scrollbar.isSmoothScrolling() ? 1 : ROW_HEIGHT));
        for (int i = 0; i < this.getMenu().getAdvancedTags().size(); ++i) {
            currentY += this.renderAdvancedTagBackground(graphics, mouseX, mouseY, i, y, x, currentY);
        }
        this.renderAdvancedTagMainSlots(graphics, mouseX, mouseY);
        this.advancedTagCheckboxes.forEach(c -> c.extractRenderState(graphics, mouseX, mouseY, partialTicks));
        this.expandButtons.forEach(c -> c.extractRenderState(graphics, mouseX, mouseY, partialTicks));
        graphics.disableScissor();
    }

    private int renderAdvancedTagBackground(final GuiGraphicsExtractor graphics,
                                            final int mouseX,
                                            final int mouseY,
                                            final int i,
                                            final int startY,
                                            final int x,
                                            final int y) {
        final AdvancedTag advancedTag = this.getMenu().getAdvancedTags().get(i);
        if (!advancedTag.isVisible()) {
            return 0;
        }
        final int height = ADVANCED_TAG_HEIGHT + (int) (getOverflowRows(advancedTag) * ROW_HEIGHT * advancedTag.getExpandPct());
        final boolean backgroundVisible = y >= startY - height && y < startY + INSET_HEIGHT;
        if (i % 2 == 0 && backgroundVisible) {
            graphics.fill(x, y, x + INSET_WIDTH, y + height, 0xFFC6C6C6);
        }
        final int mainSlotsY = y + ROW_HEIGHT;
        this.renderMainSlotsBackground(graphics, startY, x, mainSlotsY, advancedTag);
        final int overflowSlotsY = y + (ROW_HEIGHT * 2);
        return ADVANCED_TAG_HEIGHT + this.renderOverflowSlotsBackground(
            graphics,
            mouseX,
            mouseY,
            startY,
            x,
            overflowSlotsY,
            advancedTag
        );
    }

    private void renderMainSlotsBackground(final GuiGraphicsExtractor graphics,
                                           final int startY,
                                           final int x,
                                           final int y,
                                           final AdvancedTag advancedTag) {
        if (y >= startY - ROW_HEIGHT && y < startY + INSET_HEIGHT) {
            for (int col = 0; col < Math.min(advancedTag.getResources().size(), RESOURCES_PER_ROW); ++col) {
                final int slotX = x + 1 + (col * 18);
                graphics.blitSprite(GUI_TEXTURED, Sprites.SLOT, slotX, y, 18, 18);
            }
        }
    }

    private int renderOverflowSlotsBackground(final GuiGraphicsExtractor graphics,
                                              final int mouseX,
                                              final int mouseY,
                                              final int startY,
                                              final int x,
                                              final int y,
                                              final AdvancedTag advancedTag) {
        final int rows = getOverflowRows(advancedTag);
        final int height = (int) (rows * ROW_HEIGHT * advancedTag.getExpandPct());
        if (height == 0) {
            return 0;
        }
        graphics.enableScissor(x, y, x + (18 * RESOURCES_PER_ROW), y + height);
        for (int row = 0; row < rows; ++row) {
            final int rowY = y + (ROW_HEIGHT * row);
            final boolean visible = rowY >= startY - ROW_HEIGHT && rowY < startY + INSET_HEIGHT;
            if (!visible) {
                continue;
            }
            for (int col = 0; col < RESOURCES_PER_ROW; ++col) {
                final int idx = RESOURCES_PER_ROW + (row * RESOURCES_PER_ROW) + col;
                if (idx >= advancedTag.getResources().size()) {
                    break;
                }
                final int slotX = x + 1 + (col * 18);
                graphics.blitSprite(GUI_TEXTURED, Sprites.SLOT, slotX, rowY, 18, 18);
            }
        }
        this.renderSlots(advancedTag.getOverflowSlots(), graphics, mouseX, mouseY);
        graphics.disableScissor();
        return height;
    }

    private void renderAdvancedTagMainSlots(final GuiGraphicsExtractor graphics,
                                            final int mouseX,
                                            final int mouseY) {
        for (final AdvancedTag advancedTag : this.getMenu().getAdvancedTags()) {
            this.renderSlots(advancedTag.getMainSlots(), graphics, mouseX, mouseY);
        }
    }

    private void renderSlots(final List<AdvancedTagSlot> slots,
                             final GuiGraphicsExtractor graphics,
                             final int mouseX,
                             final int mouseY) {
        graphics.pose().pushMatrix();
        graphics.pose().translate(this.leftPos, this.topPos);
        for (final ResourceSlot resourceSlot : slots) {
            this.renderSlot(graphics, mouseX, mouseY, resourceSlot);
        }
        graphics.pose().popMatrix();
    }

    private void renderSlot(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY,
                            final ResourceSlot resourceSlot) {
        if (!resourceSlot.isActive()) {
            return;
        }
        final boolean hovering = this.isHovering(resourceSlot.x, resourceSlot.y, 16, 16, mouseX, mouseY);
        if (hovering) {
            ClientPlatformUtil.renderSlotHighlightBack(graphics, resourceSlot.x, resourceSlot.y);
        }
        ResourceSlotRendering.render(graphics, resourceSlot);
        if (hovering) {
            ClientPlatformUtil.renderSlotHighlightFront(graphics, resourceSlot.x, resourceSlot.y);
        }
    }

    @Override
    protected void extractLabels(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY) {
        graphics.text(this.font, this.title, this.titleLabelX, this.titleLabelY, -12566464, false);
    }

    private void addConfirmButton(final int x, final int y) {
        final int width = this.font.width(DONE) + ACTION_BUTTON_SPACING + ICON_SIZE;
        final ActionButton button = ActionButtonInvoker.init(
            this.leftPos + x,
            this.topPos + y,
            width,
            ACTION_BUTTON_HEIGHT,
            DONE,
            btn -> this.tryConfirmAndCloseToParent()
        );
        this.addRenderableWidget(button);
    }

    @Override
    public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
        return (this.scrollbar != null && this.scrollbar.mouseClicked(event, doubleClick)) || super.mouseClicked(event, doubleClick);
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
    public boolean mouseScrolled(final double x, final double y, final double scrollX, final double scrollY) {
        final boolean didScrollbar = this.isOverTagArea(x, y)
            && this.scrollbar != null
            && this.scrollbar.mouseScrolled(x, y, scrollX, scrollY);

        return didScrollbar || super.mouseScrolled(x, y, scrollX, scrollY);
    }

    private boolean isOverTagArea(final double mouseX, final double mouseY) {
        return mouseX >= this.getCheckboxStartX()
            && mouseX < this.getCheckboxStartX() + INSET_WIDTH
            && mouseY >= this.getCheckboxStartY()
            && mouseY < this.getCheckboxStartY() + INSET_HEIGHT;
    }

    @Override
    public boolean charTyped(final CharacterEvent event) {
        return (this.searchField != null && this.searchField.charTyped(event))
            || super.charTyped(event);
    }

    private boolean tryConfirmAndCloseToParent() {
        if (this.parent != null) {
            Minecraft.getInstance().setScreen(this.parent);

            // Send update data to server
            final OptionalInt selectedIndex = IntStream.range(0, this.advancedTagCheckboxes.size())
                .filter(i -> this.advancedTagCheckboxes.get(i).isSelected())
                .findFirst();

            if (selectedIndex.isPresent()) {
                final ResourceTag resourceTag = this.getMenu().getAdvancedTags().get(selectedIndex.getAsInt()).getTag();
                Platform.INSTANCE.sendPacketToServer(new SetAdvancedFilterPacket(this.slotIndex, Optional.of(resourceTag)));
            } else {
                Platform.INSTANCE.sendPacketToServer(new SetAdvancedFilterPacket(this.slotIndex, Optional.empty()));
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(final KeyEvent event) {
        if (this.tryClose(event.key())) {
            return true;
        }
        if (this.searchField != null
            && (this.searchField.keyPressed(event) || this.searchField.canConsumeInput())) {
            return true;
        }

        return super.keyPressed(event);
    }

    private boolean tryClose(final int key) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            this.close();
            return true;
        }
        return false;
    }

    private void close() {
        if (!this.tryConfirmAndCloseToParent()) {
            this.onClose();
        }
    }

    @Override
    protected Identifier getTexture() {
        return TEXTURE;
    }
}
