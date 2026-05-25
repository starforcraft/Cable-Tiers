package com.ultramega.cabletiers.common.autocrafting.autocrafter;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.support.AbstractAdvancedFilterScreen;

import com.refinedmods.refinedstorage.common.api.autocrafting.PatternOutputRenderingScreen;
import com.refinedmods.refinedstorage.common.autocrafting.PatternSlot;
import com.refinedmods.refinedstorage.common.autocrafting.autocrafter.AutocrafterContainerMenu;
import com.refinedmods.refinedstorage.common.support.AbstractBaseScreen;
import com.refinedmods.refinedstorage.common.support.tooltip.HelpClientTooltipComponent;
import com.refinedmods.refinedstorage.common.support.widget.History;
import com.refinedmods.refinedstorage.common.support.widget.SearchFieldWidget;
import com.refinedmods.refinedstorage.common.support.widget.TextMarquee;

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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.lwjgl.glfw.GLFW;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createIdentifier;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;
import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslationAsHeading;
import static java.util.Objects.requireNonNull;
import static net.minecraft.client.renderer.RenderPipelines.GUI_TEXTURED;

public class TieredAutocrafterScreen extends AbstractBaseScreen<TieredAutocrafterContainerMenu>
    implements AutocrafterContainerMenu.Listener, PatternOutputRenderingScreen {
    private static final List<ClientTooltipComponent> EMPTY_PATTERN_SLOT = List.of(ClientTooltipComponent.create(
        createTranslationAsHeading("gui", "autocrafter.empty_pattern_slot").getVisualOrderText()
    ));

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
    private EditBox nameField;
    @Nullable
    private Button editButton;
    private boolean editName;

    public TieredAutocrafterScreen(final TieredAutocrafterContainerMenu menu,
                                   final Inventory playerInventory,
                                   final Component title,
                                   final CableTiers tier) {
        // TODO: refractor
        super(menu, playerInventory, new TextMarquee(title, getTitleMaxWidth(menu)), tier != CableTiers.CREATIVE ? 210 : 176, switch (tier) {
            case ELITE -> 155;
            case ULTRA -> 191;
            case MEGA, CREATIVE -> 227;
        });
        this.playerInventory = playerInventory;
        this.tier = tier;

        switch (tier) {
            case ELITE:
                this.inventoryLabelY = 42 + 18;
                break;
            case ULTRA:
                this.inventoryLabelY = 42 + 18 * 3;
                break;
            case MEGA, CREATIVE:
                this.inventoryLabelY = 42 + 18 * 5;
                break;
        }
    }

    @Override
    public void extractBackground(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float partialTicks) {
        super.extractBackground(graphics, mouseX, mouseY, partialTicks);
        if (this.editName) {
            graphics.blitSprite(GUI_TEXTURED, NAME_BACKGROUND, this.leftPos + 7, this.topPos + 5, 162, 12);
        }
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
    public void extractContents(final GuiGraphicsExtractor graphics, final int mouseX, final int mouseY, final float partialTicks) {
        super.extractContents(graphics, mouseX, mouseY, partialTicks);
        if (this.nameField != null && this.editName) {
            this.nameField.extractRenderState(graphics, mouseX, mouseY, partialTicks);
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
        return 210 - 41 - this.font.width(title);
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
    protected void extractTooltip(final GuiGraphicsExtractor graphics, final int x, final int y) {
        if (this.hoveredSlot instanceof PatternSlot patternSlot
            && !patternSlot.hasItem()
            && this.getMenu().getCarried().isEmpty()) {
            graphics.tooltip(this.font, EMPTY_PATTERN_SLOT, x, y, DefaultTooltipPositioner.INSTANCE, null);
            return;
        }
        final Component chainingTitle = getChainingTitle(this.getMenu());
        final int chainingTitleX = this.getChainingTitleX(chainingTitle);
        if (this.isHovering(chainingTitleX, this.titleLabelY, this.font.width(chainingTitle), this.font.lineHeight, x, y)
            && this.nameField != null && !this.nameField.isFocused()) {
            final Component chainingTooltip = this.getChainingTooltip();
            graphics.tooltip(this.font, List.of(HelpClientTooltipComponent.createAlwaysDisplayed(chainingTooltip)), x, y,
                DefaultTooltipPositioner.INSTANCE, null);
            return;
        }
        super.extractTooltip(graphics, x, y);
    }

    @Override
    protected Identifier getTexture() {
        return AbstractAdvancedFilterScreen.getTexture(this.tier);
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
