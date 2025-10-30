package com.ultramega.cabletiers.common.advancedfilter;

import com.ultramega.cabletiers.common.mixin.InvokerActionButton;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.IntStream;
import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Inventory;
import org.lwjgl.glfw.GLFW;

import static com.refinedmods.refinedstorage.common.support.Sprites.ICON_SIZE;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersTranslation;

public class AdvancedFilterScreen extends AbstractBaseScreen<AdvancedFilterContainerMenu> {
    static final int ROW_HEIGHT = 18;
    static final int ADVANCED_TAG_HEIGHT = ROW_HEIGHT * 2;
    static final int RESOURCES_PER_ROW = 9;

    private static final ResourceLocation TEXTURE = createCableTiersIdentifier("textures/gui/advanced_filter.png");
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
        super(new AdvancedFilterContainerMenu(selectedResource), playerInventory, title);
        this.slotIndex = slotIndex;
        this.parent = parent;
        this.selectedTagKey = selectedTagKey;
        this.imageWidth = 223;
        this.imageHeight = 182;
    }

    @Override
    protected void init() {
        super.init();
        advancedTagCheckboxes.clear();
        expandButtons.clear();

        final int x = getCheckboxStartX();
        for (int i = 0; i < getMenu().getAdvancedTags().size(); ++i) {
            addWidgetsForTags(i, x);
        }

        addConfirmButton(160, 155);

        scrollbar = new ScrollbarWidget(
            leftPos + 203,
            topPos + 59,
            ScrollbarWidget.Type.NORMAL,
            INSET_HEIGHT
        );
        final int overflowingRows = getMenu().getAdvancedTags().size() - TAGS_DISPLAYED;
        final int maxOffset = scrollbar.isSmoothScrolling()
            ? overflowingRows * ADVANCED_TAG_HEIGHT
            : overflowingRows * ROWS_PER_TAG;
        scrollbar.setMaxOffset(maxOffset);
        scrollbar.setEnabled(maxOffset > 0);
        scrollbar.setListener(value -> updateWidgets());
        addWidget(scrollbar);

        searchField = new EditBox(
            font,
            leftPos + 24,
            topPos + 46,
            193 - 6,
            font.lineHeight,
            Component.empty()
        );
        searchField.setBordered(false);
        searchField.setVisible(true);
        searchField.setCanLoseFocus(true);
        searchField.setFocused(false);
        searchField.setResponder(query -> getMenu().filter(query));
        addRenderableWidget(searchField);

        addRenderableWidget(new SearchIconWidget(
            leftPos + 7,
            topPos + 44,
            () -> SEARCH_HELP,
            searchField
        ));
    }

    private int getCheckboxStartX() {
        return leftPos + 8;
    }

    private int getCheckboxStartY() {
        return topPos + 59;
    }

    private int getAdvancedTagY(final int idx) {
        return getCheckboxStartY() + (ADVANCED_TAG_HEIGHT * idx);
    }

    private void addWidgetsForTags(final int idx, final int x) {
        final AdvancedTag advancedTag = getMenu().getAdvancedTags().get(idx);
        final int y = getAdvancedTagY(idx);
        final boolean hasTranslation = I18n.exists(advancedTag.getTranslationKey());
        final MutableComponent id = Component.literal(advancedTag.getId().toString());
        final CheckboxWidget advancedTagCheckbox = new CheckboxWidget(
            x + 2,
            y + (ROW_HEIGHT / 2) - (9 / 2),
            164 - 2 - 16 - 1 - 4,
            hasTranslation ? Component.translatable(advancedTag.getTranslationKey()) : id,
            font,
            selectedTagKey != null && selectedTagKey.location().equals(advancedTag.getId()),
            CheckboxWidget.Size.SMALL
        );
        advancedTagCheckbox.setOnPressed((checkbox, selected) -> {
            advancedTagCheckboxes.forEach(c -> c.setSelected(false));
            checkbox.setSelected(selected);
        });
        if (hasTranslation) {
            advancedTagCheckbox.setTooltip(Tooltip.create(id));
        }
        advancedTagCheckboxes.add(addWidget(advancedTagCheckbox));
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
        expandButtons.add(addWidget(expandButton));
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        getMenu().getAdvancedTags().forEach(AdvancedTag::update);
        updateWidgets();
    }

    @Override
    protected void renderResourceSlots(final GuiGraphics graphics) {
        ResourceSlotRendering.render(graphics, getMenu().getFilterSlot(), leftPos, topPos);
    }

    private void updateWidgets() {
        final ScrollbarWidget theScrollbar = scrollbar;
        if (theScrollbar == null) {
            return;
        }
        double totalHeight = 0;
        int totalRows = 0;
        final int scrollbarOffset = (int) theScrollbar.getOffset();
        int y = getAdvancedTagY(0)
            - (theScrollbar.isSmoothScrolling() ? scrollbarOffset : scrollbarOffset * ROW_HEIGHT);
        for (int i = 0; i < getMenu().getAdvancedTags().size(); ++i) {
            final AdvancedTag advancedTag = getMenu().getAdvancedTags().get(i);
            final CheckboxWidget advancedTagCheckbox = advancedTagCheckboxes.get(i);
            final Button expandButton = expandButtons.get(i);

            if (!advancedTag.isVisible()) {
                advancedTagCheckbox.visible = false;
                expandButton.visible = false;
                updateAdvancedTagSlots(advancedTag.getMainSlots(), y, 0, false);
                updateAdvancedTagSlots(advancedTag.getOverflowSlots(), y, 1, false);
                continue;
            }

            totalRows += ROWS_PER_TAG;
            final int overflowRows = getOverflowRows(advancedTag);
            totalRows += (int) (overflowRows * advancedTag.getExpandPct());
            final int height = ADVANCED_TAG_HEIGHT
                + (int) (overflowRows * ROW_HEIGHT * advancedTag.getExpandPct());

            updateAdvancedTagCheckbox(advancedTagCheckbox, y);
            updateExpandButton(expandButton, y);
            updateAdvancedTagSlots(advancedTag.getMainSlots(), y, 0, true);
            updateAdvancedTagSlots(advancedTag.getOverflowSlots(), y, 1, advancedTag.getExpandPct() > 0);

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
        advancedTagCheckbox.visible = advancedTagCheckbox.getY() >= getCheckboxStartY() - advancedTagCheckbox.getHeight()
            && advancedTagCheckbox.getY() < getCheckboxStartY() + INSET_HEIGHT;
    }

    private void updateExpandButton(final Button expandButton, final int y) {
        expandButton.setY(y + 1);
        expandButton.visible = expandButton.getY() >= getCheckboxStartY() - expandButton.getHeight()
            && expandButton.getY() < getCheckboxStartY() + INSET_HEIGHT;
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
                (y + ROW_HEIGHT + (row * 18) + 1) - topPos
            );
            resourceSlot.setActive((resourceSlot.y + topPos) >= getCheckboxStartY() - 18
                && (resourceSlot.y + topPos) < getCheckboxStartY() + INSET_HEIGHT
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
    public void render(final GuiGraphics graphics, final int mouseX, final int mouseY, final float partialTicks) {
        super.render(graphics, mouseX, mouseY, partialTicks);
        if (scrollbar != null) {
            scrollbar.render(graphics, mouseX, mouseY, partialTicks);
        }
    }

    @Override
    protected void renderBg(final GuiGraphics graphics, final float delta, final int mouseX, final int mouseY) {
        super.renderBg(graphics, delta, mouseX, mouseY);
        final int x = getCheckboxStartX();
        final int y = getCheckboxStartY();
        graphics.enableScissor(x, y, x + INSET_WIDTH, y + INSET_HEIGHT);
        int currentY = y - ((scrollbar != null ? (int) scrollbar.getOffset() : 0)
            * (scrollbar != null && scrollbar.isSmoothScrolling() ? 1 : ROW_HEIGHT));
        for (int i = 0; i < getMenu().getAdvancedTags().size(); ++i) {
            currentY += renderAdvancedTagBackground(graphics, mouseX, mouseY, i, y, x, currentY);
        }
        renderAdvancedTagMainSlots(graphics, mouseX, mouseY);
        advancedTagCheckboxes.forEach(c -> c.render(graphics, mouseX, mouseY, delta));
        expandButtons.forEach(c -> c.render(graphics, mouseX, mouseY, delta));
        graphics.disableScissor();
    }

    private int renderAdvancedTagBackground(
        final GuiGraphics graphics,
        final int mouseX,
        final int mouseY,
        final int i,
        final int startY,
        final int x,
        final int y
    ) {
        final AdvancedTag advancedTag = getMenu().getAdvancedTags().get(i);
        if (!advancedTag.isVisible()) {
            return 0;
        }
        final int height = ADVANCED_TAG_HEIGHT
            + (int) (getOverflowRows(advancedTag) * ROW_HEIGHT * advancedTag.getExpandPct());
        final boolean backgroundVisible = y >= startY - height && y < startY + INSET_HEIGHT;
        if (i % 2 == 0 && backgroundVisible) {
            graphics.fill(
                x,
                y,
                x + INSET_WIDTH,
                y + height,
                0,
                0xFFC6C6C6
            );
        }
        final int mainSlotsY = y + ROW_HEIGHT;
        renderMainSlotsBackground(graphics, startY, x, mainSlotsY, advancedTag);
        final int overflowSlotsY = y + (ROW_HEIGHT * 2);
        return ADVANCED_TAG_HEIGHT + renderOverflowSlotsBackground(
            graphics,
            mouseX,
            mouseY,
            startY,
            x,
            overflowSlotsY,
            advancedTag
        );
    }

    private void renderMainSlotsBackground(
        final GuiGraphics graphics,
        final int startY,
        final int x,
        final int y,
        final AdvancedTag advancedTag
    ) {
        if (y >= startY - ROW_HEIGHT && y < startY + INSET_HEIGHT) {
            for (int col = 0; col < Math.min(advancedTag.getResources().size(), RESOURCES_PER_ROW); ++col) {
                final int slotX = x + 1 + (col * 18);
                graphics.blitSprite(Sprites.SLOT, slotX, y, 18, 18);
            }
        }
    }

    private int renderOverflowSlotsBackground(final GuiGraphics graphics,
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
                graphics.blitSprite(Sprites.SLOT, slotX, rowY, 18, 18);
            }
        }
        renderSlots(advancedTag.getOverflowSlots(), graphics, mouseX, mouseY);
        graphics.disableScissor();
        return height;
    }

    private void renderAdvancedTagMainSlots(final GuiGraphics graphics,
                                            final int mouseX,
                                            final int mouseY) {
        for (final AdvancedTag advancedTag : getMenu().getAdvancedTags()) {
            renderSlots(advancedTag.getMainSlots(), graphics, mouseX, mouseY);
        }
    }

    private void renderSlots(final List<AdvancedTagSlot> slots,
                             final GuiGraphics graphics,
                             final int mouseX,
                             final int mouseY) {
        for (final ResourceSlot resourceSlot : slots) {
            if (resourceSlot.isActive()) {
                ResourceSlotRendering.render(graphics, resourceSlot, leftPos, topPos);
                if (isHovering(resourceSlot.x, resourceSlot.y, 16, 16, mouseX, mouseY)) {
                    renderSlotHighlight(graphics, leftPos + resourceSlot.x, topPos + resourceSlot.y, 0);
                }
            }
        }
    }

    @Override
    protected void renderLabels(final GuiGraphics graphics, final int mouseX, final int mouseY) {
        graphics.drawString(font, title, titleLabelX, titleLabelY, 4210752, false);
    }

    private void addConfirmButton(final int x, final int y) {
        final int width = font.width(DONE) + ACTION_BUTTON_SPACING + ICON_SIZE;
        final ActionButton button = InvokerActionButton.init(
            leftPos + x,
            topPos + y,
            width,
            ACTION_BUTTON_HEIGHT,
            DONE,
            btn -> tryConfirmAndCloseToParent()
        );
        addRenderableWidget(button);
    }

    @Override
    public boolean mouseClicked(final double mouseX, final double mouseY, final int clickedButton) {
        if (scrollbar != null && scrollbar.mouseClicked(mouseX, mouseY, clickedButton)) {
            return true;
        }

        return super.mouseClicked(mouseX, mouseY, clickedButton);
    }

    @Override
    public void mouseMoved(final double mouseX, final double mouseY) {
        if (scrollbar != null) {
            scrollbar.mouseMoved(mouseX, mouseY);
        }
        super.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean mouseReleased(final double mouseX, final double mouseY, final int button) {
        if (scrollbar != null && scrollbar.mouseReleased(mouseX, mouseY, button)) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(final double mouseX, final double mouseY, final double scrollX, final double scrollY) {
        final boolean didScrollbar = isOverTagArea(mouseX, mouseY)
            && scrollbar != null
            && scrollbar.mouseScrolled(mouseX, mouseY, scrollX, scrollY);

        return didScrollbar || super.mouseScrolled(mouseX, mouseY, scrollX, scrollY);
    }

    private boolean isOverTagArea(final double mouseX, final double mouseY) {
        return mouseX >= getCheckboxStartX()
            && mouseX < getCheckboxStartX() + INSET_WIDTH
            && mouseY >= getCheckboxStartY()
            && mouseY < getCheckboxStartY() + INSET_HEIGHT;
    }

    @Override
    public boolean charTyped(final char unknown1, final int unknown2) {
        return (searchField != null && searchField.charTyped(unknown1, unknown2))
            || super.charTyped(unknown1, unknown2);
    }

    private boolean tryConfirmAndCloseToParent() {
        if (parent != null) {
            Minecraft.getInstance().setScreen(parent);

            // Send update data to server
            final OptionalInt selectedIndex = IntStream.range(0, advancedTagCheckboxes.size())
                .filter(i -> advancedTagCheckboxes.get(i).isSelected())
                .findFirst();

            if (selectedIndex.isPresent()) {
                final ResourceTag resourceTag = getMenu().getAdvancedTags().get(selectedIndex.getAsInt()).getTag();
                Platform.INSTANCE.sendPacketToServer(new SetAdvancedFilterPacket(slotIndex, Optional.of(resourceTag)));
            } else {
                Platform.INSTANCE.sendPacketToServer(new SetAdvancedFilterPacket(slotIndex, Optional.empty()));
            }

            return true;
        }
        return false;
    }

    @Override
    public boolean keyPressed(final int key, final int scanCode, final int modifiers) {
        if (tryClose(key)) {
            return true;
        }
        if (searchField != null
            && (searchField.keyPressed(key, scanCode, modifiers) || searchField.canConsumeInput())) {
            return true;
        }

        return super.keyPressed(key, scanCode, modifiers);
    }

    private boolean tryClose(final int key) {
        if (key == GLFW.GLFW_KEY_ESCAPE) {
            close();
            return true;
        }
        return false;
    }

    private void close() {
        if (!tryConfirmAndCloseToParent()) {
            onClose();
        }
    }

    @Override
    protected ResourceLocation getTexture() {
        return TEXTURE;
    }
}
