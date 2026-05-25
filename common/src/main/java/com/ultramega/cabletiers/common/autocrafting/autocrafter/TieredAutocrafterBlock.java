package com.ultramega.cabletiers.common.autocrafting.autocrafter;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.registry.BlockEntities;
import com.ultramega.cabletiers.common.registry.Blocks;

import com.refinedmods.refinedstorage.common.api.support.HelpTooltipComponent;
import com.refinedmods.refinedstorage.common.content.BlockColorMap;
import com.refinedmods.refinedstorage.common.content.BlockProperties;
import com.refinedmods.refinedstorage.common.support.AbstractActiveColoredDirectionalBlock;
import com.refinedmods.refinedstorage.common.support.AbstractBlockEntityTicker;
import com.refinedmods.refinedstorage.common.support.BaseBlockItem;
import com.refinedmods.refinedstorage.common.support.BlockItemProvider;
import com.refinedmods.refinedstorage.common.support.NetworkNodeBlockItem;
import com.refinedmods.refinedstorage.common.support.direction.DefaultDirectionType;
import com.refinedmods.refinedstorage.common.support.direction.DirectionType;
import com.refinedmods.refinedstorage.common.support.network.NetworkNodeBlockEntityTicker;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersTranslation;

public class TieredAutocrafterBlock extends AbstractActiveColoredDirectionalBlock<Direction, TieredAutocrafterBlock, BaseBlockItem>
    implements EntityBlock, BlockItemProvider<BaseBlockItem> {
    private static final Component HELP_1 = createTranslation("item", "autocrafter.help");
    private static final Component HELP_2 = createCableTiersTranslation("item", "tiered_autocrafter.help");
    private final AbstractBlockEntityTicker<TieredAutocrafterBlockEntity> ticker;

    private final Identifier id;
    private final CableTiers tier;

    public TieredAutocrafterBlock(final Identifier id, final DyeColor color, final MutableComponent name, final CableTiers tier) {
        super(BlockProperties.stone(id), color, name);
        this.id = id;
        this.tier = tier;
        this.ticker = new NetworkNodeBlockEntityTicker<>(() -> BlockEntities.INSTANCE.getTieredAutocrafters(tier), ACTIVE);
    }

    @Override
    protected DirectionType<Direction> getDirectionType() {
        return DefaultDirectionType.FACE_CLICKED;
    }

    @Nullable
    @Override
    public <O extends BlockEntity> BlockEntityTicker<O> getTicker(final Level level,
                                                                  final BlockState blockState,
                                                                  final BlockEntityType<O> type) {
        return this.ticker.get(level, type);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new TieredAutocrafterBlockEntity(this.tier, pos, state);
    }

    @Override
    public BlockColorMap<TieredAutocrafterBlock, BaseBlockItem> getBlockColorMap() {
        return Blocks.INSTANCE.getTieredAutocrafters(this.tier);
    }

    @Override
    public BaseBlockItem createBlockItem() {
        return new NetworkNodeBlockItem(this.id, this, null) {
            @Override
            public Optional<TooltipComponent> getTooltipImage(final ItemStack stack) {
                return Optional.of(new HelpTooltipComponent(
                    Component.empty()
                        .append(HELP_1)
                        .append(Component.literal(" "))
                        .append(Component.translatable(
                            HELP_2.getString(),
                            Component.literal(tier.getSpeed(CableType.AUTOCRAFTER) + "x"),
                            Component.literal(String.valueOf(tier.getAutocrafterPatternSlotCount()))
                        ))
                    )
                );
            }
        };
    }

    @Override
    public boolean canAlwaysConnect() {
        return true;
    }

    @Override
    protected boolean shouldChangedStateKeepBlockEntity(final BlockState oldState) {
        return oldState.getBlock() instanceof TieredAutocrafterBlock;
    }
}
