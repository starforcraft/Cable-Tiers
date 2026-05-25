package com.ultramega.cabletiers.common.autocrafting.autocrafter;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.support.AbstractAdvancedFilterScreen;

import com.refinedmods.refinedstorage.common.api.autocrafting.PatternOutputRenderingScreen;
import com.refinedmods.refinedstorage.common.autocrafting.PatternRendering;
import com.refinedmods.refinedstorage.common.autocrafting.PatternSlot;
import com.refinedmods.refinedstorage.common.autocrafting.autocrafter.AutocrafterContainerMenu;
import com.refinedmods.refinedstorage.common.support.AbstractBaseScreen;
import com.refinedmods.refinedstorage.common.support.tooltip.HelpClientTooltipComponent;
import com.refinedmods.refinedstorage.common.support.widget.History;
import com.refinedmods.refinedstorage.common.support.widget.ScrollbarWidget;
import com.refinedmods.refinedstorage.common.support.widget.SearchFieldWidget;
import com.refinedmods.refinedstorage.common.support.widget.TextMarquee;
import com.refinedmods.refinedstorage.common.util.ClientPlatformUtil;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.DefaultTooltipPositioner;
import net.minecraft.client.input.CharacterEvent;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslationAsHeading;
import static com.ultramega.cabletiers.common.autocrafting.autocrafter.TieredAutocrafterContainerMenu.getPatternViewportHeight;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersIdentifier;
import static java.util.Objects.requireNonNull;
import static net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED;

public class TieredAutocrafterScreen extends AbstractBaseScreen<TieredAutocrafterContainerMenu>
    implements AutocrafterContainerMenu.Listener, PatternOutputRenderingScreen {
    private static final Identifier ULTRA_MEGA_CREATIVE_TEXTURE = createCableTiersIdentifier("textures/gui/ultra_mega_creative_autocrafter.png");
    private static final Identifier ROW_SPRITE = createIdentifier("grid/row");
    private static final List<ClientTooltipComponent> EMPTY_PATTERN_SLOT = List.of(ClientTooltipComponent.create(
        createTranslationAsHeading("gui", "autocrafter.empty_pattern_slot").getVisualOrderText()
    ));

    private static final int ROW_SIZE = 18;
    private static final int COLUMNS = 9;
    private static final int SCROLLBAR_X = 174;
    private static final int SCROLLBAR_Y = 20;
    private static final int PATTERN_VIEWPORT_X = 7;
    private static final int PATTERN_VIEWPORT_Y = 19;
    private static final int PATTERN_ROW_WIDTH = 162;

    private static final Component CHAINED = createTranslation("gui", "autocrafter.chained");
    private static final Component CHAINED_HELP = createTranslation("gui", "autocrafter.chained.help");
    private static final Component CHAINED_HEAD_HELP = createTranslation("gui", "autocrafter.chained.head_help");
    private static final Component NOT_CHAINED = createTranslation("gui", "autocrafter.not_chained");
    private static final Component NOT_CHAINED_HELP = createTranslation("gui", "autocrafter.not_chained.help");
    private static final Component EDIT = createTranslation("gui", "autocrafter.edit_name");
    private static final Component CURRENTLY_LOCKED = createTranslation("gui", "autocrafter.currently_locked");

    private static final Identifier NAME_BACKGROUND = createIdentifier("widget/autocrafter_name");
    private static final List<String> CRAFTER_NAME_HISTORY = new ArrayList<>();

    private final Inventory playerInventory;
    private final CableTiers tier;

    @Nullable
    private LockModeSideButtonWidget lockModeSideButtonWidget;

    @Nullable
    private ScrollbarWidget scrollbar;
    @Nullable
    private EditBox nameField;
    @Nullable
    private Button editButton;
    private boolean editName;

    public TieredAutocrafterScreen(final TieredAutocrafterContainerMenu menu,
                                   final Inventory playerInventory,
                                   final Component title,
                                   final CableTiers tier) {
        super(menu, playerInventory, new TextMarquee(title, getTitleMaxWidth(menu)), getImageWidth(tier), getImageHeight(tier));
        this.playerInventory = playerInventory;
        this.tier = tier;
        this.inventoryLabelY = getInventoryLabelY(tier);
    }

    @Override
    protected void init() {
        super.init();
        this.getMenu().setListener(this);

        this.tryAddLockModeSideButton();
        this.addSideButton(new AutocrafterPrioritySideButtonWidget(
            this.getMenu().getProperty(AutocrafterPropertyTypes.PRIORITY),
            this.playerInventory,
            this
        ));
        this.addSideButton(new VisibleToTheAutocrafterManagerSideButtonWidget(
            this.getMenu().getProperty(AutocrafterPropertyTypes.VISIBLE_TO_THE_AUTOCRAFTER_MANAGER)
        ));
        this.addSideButton(new AutocrafterImportModeSideButtonWidget(
            this.getMenu().getProperty(AutocrafterPropertyTypes.IMPORT_MODE)
        ));

        if (this.tier != CableTiers.ELITE) {
            this.scrollbar = new ScrollbarWidget(
                this.leftPos + SCROLLBAR_X,
                this.topPos + SCROLLBAR_Y,
                ScrollbarWidget.Type.NORMAL,
                getPatternViewportHeight() - 2
            );
            this.scrollbar.setListener(offset -> this.scrollbarChanged());
            this.updateScrollbarMaxOffset();
            this.addWidget(this.scrollbar);
        }

        this.nameField = new SearchFieldWidget(
            this.font,
            this.leftPos + 8 + 1,
            this.topPos + 6 + 1,
            159 - 6,
            new History(CRAFTER_NAME_HISTORY)
        );
        this.nameField.setValue(this.title.getString());
        this.nameField.setBordered(false);
        this.nameField.setCanLoseFocus(false);
        this.addWidget(this.nameField);

        this.editButton = this.addRenderableWidget(Button.builder(EDIT, button -> this.setEditName(true))
            .pos(this.getEditButtonX(), this.topPos + this.titleLabelY - 3)
            .size(getEditButtonWidth(), 14)
            .build());
        this.editButton.active = this.getMenu().canChangeName();

        this.setEditName(false);
    }

    @Override
    public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);
        if (this.tier != CableTiers.ELITE) {
            this.renderRows(graphics, mouseX, mouseY);
        }

        if (this.editName) {
            graphics.blitSprite(GUI_TEXTURED, NAME_BACKGROUND, this.leftPos + 7, this.topPos + 5, 162, 12);
        }
    }

    @Override
    public void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float partialTicks) {
        super.extractContents(graphics, mouseX, mouseY, partialTicks);
        if (this.scrollbar != null) {
            this.scrollbar.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        }

        if (this.nameField != null && this.editName) {
            this.nameField.extractRenderState(graphics, mouseX, mouseY, partialTicks);
        }
    }

    private void renderRows(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY) {
        final int x = this.leftPos;
        final int y = this.topPos;

        final int scissorX1 = x + PATTERN_VIEWPORT_X;
        final int scissorY1 = y + PATTERN_VIEWPORT_Y + 1;
        final int scissorX2 = scissorX1 + PATTERN_ROW_WIDTH;
        final int scissorY2 = scissorY1 + getPatternViewportHeight() - 2;

        graphics.enableScissor(scissorX1, scissorY1, scissorX2, scissorY2);

        this.renderRows(graphics, mouseX, mouseY, x, y, PATTERN_VIEWPORT_Y, TieredAutocrafterContainerMenu.PATTERN_VISIBLE_ROWS);

        graphics.disableScissor();
    }

    private void renderRows(final GuiGraphicsExtractor graphics,
                            final int mouseX,
                            final int mouseY,
                            final int x,
                            final int y,
                            final int topHeight,
                            final int rows) {
        final int totalRows = this.getMenu().getPatternTotalRows();
        for (int row = 0; row < Math.max(totalRows, rows); ++row) {
            final int rowX = x + PATTERN_VIEWPORT_X;
            final int rowY = y + topHeight + (row * ROW_SIZE) - this.getScrollbarOffset();

            final boolean isOutOfFrame = rowY < y + topHeight - ROW_SIZE || rowY > y + topHeight + (ROW_SIZE * rows);
            if (isOutOfFrame) {
                continue;
            }

            this.renderRow(graphics, mouseX, mouseY, rowX, rowY, row);
        }
    }

    private void renderRow(final GuiGraphicsExtractor graphics,
                           final int mouseX,
                           final int mouseY,
                           final int rowX,
                           final int rowY,
                           final int row) {
        graphics.blitSprite(GUI_TEXTURED, ROW_SPRITE, rowX, rowY, PATTERN_ROW_WIDTH, ROW_SIZE);
        for (int column = 0; column < COLUMNS; ++column) {
            this.renderCell(graphics, mouseX, mouseY, rowX, rowY, (row * COLUMNS) + column, column);
        }
    }

    private void renderCell(final GuiGraphicsExtractor graphics,
                            final int mouseX,
                            final int mouseY,
                            final int rowX,
                            final int rowY,
                            final int idx,
                            final int column) {
        if (this.menu.getPatternSlots().size() <= idx) {
            return;
        }

        final Slot slot = this.menu.getPatternSlots().get(idx);
        final int slotX = rowX + 1 + (column * ROW_SIZE);
        final int slotY = rowY + 1;

        final ItemStack stack = slot.container.getItem(idx);
        this.renderSlot(graphics, mouseX, mouseY, stack, slotX, slotY);
    }

    private void renderSlot(final GuiGraphicsExtractor graphics,
                            final int mouseX,
                            final int mouseY,
                            final ItemStack stack,
                            final int slotX,
                            final int slotY) {
        final boolean hovering = mouseX >= slotX
            && mouseY >= slotY
            && mouseX <= slotX + 16
            && mouseY <= slotY + 16;
        final boolean interact = hovering && this.isMouseInPatternViewport(mouseX, mouseY);
        if (interact) {
            ClientPlatformUtil.renderSlotHighlightBack(graphics, slotX, slotY);
        }
        PatternRendering.getOutput(stack).ifPresent(output -> {
            graphics.item(output, slotX, slotY);
        });
        if (interact) {
            ClientPlatformUtil.renderSlotHighlightFront(graphics, slotX, slotY);
        }
    }

    @Override
    protected void extractSlot(final GuiGraphicsExtractor graphics,
                               final Slot slot,
                               final int mouseX,
                               final int mouseY) {
        if (this.tier == CableTiers.ELITE || !(slot instanceof PatternSlot)) {
            super.extractSlot(graphics, slot, mouseX, mouseY);
        }
    }

    @Override
    protected void extractLabels(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY) {
        if (this.editName) {
            this.renderPlayerInventoryTitle(graphics);
            return;
        }
        super.extractLabels(graphics, mouseX, mouseY);
        final Component title = getChainingTitle(this.menu);
        graphics.text(this.font, title, this.getChainingTitleX(title), this.titleLabelY, -12566464, false);
    }

    @Override
    protected void extractTooltip(final GuiGraphicsExtractor graphics, final int x, final int y) {
        if (this.hoveredSlot instanceof PatternSlot patternSlot
            && !patternSlot.hasItem()
            && this.getMenu().getCarried().isEmpty()
            && this.isMouseInPatternViewport(x, y)) {
            graphics.tooltip(this.font, EMPTY_PATTERN_SLOT, x, y, DefaultTooltipPositioner.INSTANCE, null);
            return;
        }
        final Component chainingTitle = getChainingTitle(this.getMenu());
        final int chainingTitleX = this.getChainingTitleX(chainingTitle);
        if (this.isHovering(chainingTitleX, this.titleLabelY, this.font.width(chainingTitle), this.font.lineHeight, x, y)
            && this.nameField != null && !this.nameField.isFocused()) {
            graphics.tooltip(this.font, List.of(HelpClientTooltipComponent.createAlwaysDisplayed(this.getChainingTooltip())), x, y,
                DefaultTooltipPositioner.INSTANCE, null);
            return;
        }
        super.extractTooltip(graphics, x, y);
    }

    private static int getImageWidth(final CableTiers tier) {
        return tier == CableTiers.CREATIVE ? 193 : tier == CableTiers.ELITE ? 210 : 227;
    }

    private static int getImageHeight(final CableTiers tier) {
        return switch (tier) {
            case ELITE -> 155;
            case ULTRA, MEGA, CREATIVE -> 227;
        };
    }

    private static int getInventoryLabelY(final CableTiers tier) {
        return switch (tier) {
            case ELITE -> 42 + 18;
            case ULTRA, MEGA, CREATIVE -> 42 + 18 * 5;
        };
    }

    private static int getTitleMaxWidth(final TieredAutocrafterContainerMenu menu) {
        final int chainingTitleWidth = Minecraft.getInstance().font.width(getChainingTitle(menu));
        final int editButtonWidth = getEditButtonWidth();
        return TITLE_MAX_WIDTH - chainingTitleWidth - editButtonWidth - 10;
    }

    private int getEditButtonX() {
        return this.leftPos + this.titleLabelX + this.titleMarquee.getEffectiveWidth(this.font) + 2;
    }

    private static int getEditButtonWidth() {
        return Minecraft.getInstance().font.width(EDIT) + 8;
    }

    private static Component getChainingTitle(final TieredAutocrafterContainerMenu menu) {
        return (menu.isPartOfChain() || menu.isHeadOfChain()) ? CHAINED : NOT_CHAINED;
    }

    private Component getChainingTooltip() {
        if (!this.getMenu().isPartOfChain() && !this.getMenu().isHeadOfChain()) {
            return NOT_CHAINED_HELP;
        }
        return this.getMenu().isHeadOfChain() ? CHAINED_HEAD_HELP : CHAINED_HELP;
    }

    private void tryAddLockModeSideButton() {
        if (this.getMenu().isPartOfChain()) {
            return;
        }
        this.lockModeSideButtonWidget = new LockModeSideButtonWidget(
            this.getMenu().getProperty(AutocrafterPropertyTypes.LOCK_MODE)
        );
        this.lockedChanged(this.getMenu().isLocked());
        this.addSideButton(this.lockModeSideButtonWidget);
    }

    private void setEditName(final boolean editName) {
        this.editName = editName;
        if (this.nameField != null) {
            this.nameField.visible = editName;
            this.nameField.setFocused(editName);
            this.nameField.setCanLoseFocus(!editName);
            if (editName) {
                this.setFocused(this.nameField);
            } else {
                this.setFocused(null);
            }
        }
        if (this.editButton != null) {
            this.editButton.visible = !editName;
        }
    }

    private int getChainingTitleX(final Component title) {
        return 210 - 41 + (this.tier != CableTiers.ELITE ? 18 : 0) - this.font.width(title);
    }

    @Override
    public boolean charTyped(final CharacterEvent event) {
        if (this.nameField != null && this.editName && this.nameField.charTyped(event)) {
            return true;
        }
        return super.charTyped(event);
    }

    @Override
    public boolean keyPressed(final KeyEvent event) {
        if (this.nameField != null && this.editName) {
            if (this.nameField.keyPressed(event)) {
                return true;
            }
            if (this.nameField.isFocused() && this.saveOrCancel(event.key())) {
                return true;
            }
        }
        return super.keyPressed(event);
    }

    @Override
    public boolean mouseClicked(final MouseButtonEvent event, final boolean doubleClick) {
        if (this.scrollbar != null && this.scrollbar.mouseClicked(event, doubleClick)) {
            return true;
        }

        if (this.hoveredSlot instanceof PatternSlot && !this.isMouseInPatternViewport(event.x(), event.y())) {
            return false;
        }

        return super.mouseClicked(event, doubleClick);
    }

    @Override
    public void mouseMoved(final double mx, final double my) {
        if (this.scrollbar != null) {
            this.scrollbar.mouseMoved(mx, my);
        }
        super.mouseMoved(mx, my);
    }

    @Override
    public boolean mouseReleased(final MouseButtonEvent event) {
        if (this.scrollbar != null && this.scrollbar.mouseReleased(event)) {
            return true;
        }
        return super.mouseReleased(event);
    }

    @Override
    public boolean mouseScrolled(final double x, final double y, final double scrollX, final double scrollY) {
        final boolean didScrollbar = this.scrollbar != null
            && !this.minecraft.hasShiftDown()
            && !ClientPlatformUtil.isCommandOrControlDown()
            && this.scrollbar.mouseScrolled(x, y, scrollX, scrollY);

        return didScrollbar || super.mouseScrolled(x, y, scrollX, scrollY);
    }

    private void scrollbarChanged() {
        this.getMenu().setPatternScrollOffset(this.getScrollbarOffset());
    }

    private int getScrollbarOffset() {
        if (this.scrollbar == null) {
            return 0;
        }

        final int scrollbarOffset = (int) this.scrollbar.getOffset();
        if (!this.scrollbar.isSmoothScrolling()) {
            return scrollbarOffset * ROW_SIZE;
        }

        return scrollbarOffset;
    }

    private void updateScrollbarMaxOffset() {
        if (this.scrollbar == null) {
            return;
        }

        final int totalRows = this.getMenu().getPatternTotalRows();
        final int rowsExcludingVisibleOnes = totalRows - TieredAutocrafterContainerMenu.PATTERN_VISIBLE_ROWS;
        final int maxOffset = this.scrollbar.isSmoothScrolling()
            ? Math.max(0, rowsExcludingVisibleOnes * ROW_SIZE)
            : Math.max(0, rowsExcludingVisibleOnes);

        this.scrollbar.setMaxOffset(maxOffset);
        this.scrollbar.setEnabled(totalRows > TieredAutocrafterContainerMenu.PATTERN_VISIBLE_ROWS);
        this.scrollbarChanged();
    }

    private boolean isMouseInPatternViewport(final double mouseX, final double mouseY) {
        final int x = this.leftPos + PATTERN_VIEWPORT_X;
        final int y = this.topPos + PATTERN_VIEWPORT_Y;
        final int width = COLUMNS * ROW_SIZE;
        final int height = getPatternViewportHeight() - 2;

        return mouseX >= x
            && mouseX < x + width
            && mouseY >= y
            && mouseY < y + height;
    }

    private boolean saveOrCancel(final int key) {
        if ((key == GLFW.GLFW_KEY_ENTER || key == GLFW.GLFW_KEY_KP_ENTER)) {
            this.getMenu().changeName(requireNonNull(this.nameField).getValue());
            this.setEditName(false);
            return true;
        } else if (key == GLFW.GLFW_KEY_ESCAPE) {
            this.setEditName(false);
            requireNonNull(this.nameField).setValue(this.titleMarquee.getText().getString());
            return true;
        }
        return false;
    }

    @Override
    protected Identifier getTexture() {
        if (this.tier == CableTiers.ELITE) {
            return AbstractAdvancedFilterScreen.getTexture(this.tier);
        } else {
            return ULTRA_MEGA_CREATIVE_TEXTURE;
        }
    }

    @Override
    public void nameChanged(final Component name) {
        this.titleMarquee.setText(name);
        if (this.nameField != null) {
            this.nameField.setValue(name.getString());
        }
        if (this.editButton != null) {
            this.editButton.setX(this.getEditButtonX());
        }
    }

    @Override
    public void lockedChanged(final boolean locked) {
        if (this.lockModeSideButtonWidget == null) {
            return;
        }
        if (locked) {
            this.lockModeSideButtonWidget.setWarning(CURRENTLY_LOCKED);
            return;
        }
        this.lockModeSideButtonWidget.setWarning(null);
    }

    @Override
    public boolean canDisplayOutput(final ItemStack stack) {
        return this.getMenu().containsPattern(stack);
    }
}
