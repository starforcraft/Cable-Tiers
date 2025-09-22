package com.ultramega.cabletiers.common.importer;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.registry.BlockEntities;
import com.ultramega.cabletiers.common.registry.Blocks;
import com.ultramega.cabletiers.common.utils.BlockEntityTierProvider;

import com.refinedmods.refinedstorage.common.api.support.HelpTooltipComponent;
import com.refinedmods.refinedstorage.common.content.BlockColorMap;
import com.refinedmods.refinedstorage.common.support.AbstractBlockEntityTicker;
import com.refinedmods.refinedstorage.common.support.AbstractDirectionalCableBlock;
import com.refinedmods.refinedstorage.common.support.BaseBlockItem;
import com.refinedmods.refinedstorage.common.support.BlockItemProvider;
import com.refinedmods.refinedstorage.common.support.ColorableBlock;
import com.refinedmods.refinedstorage.common.support.DirectionalCableBlockShapes;
import com.refinedmods.refinedstorage.common.support.NetworkNodeBlockItem;
import com.refinedmods.refinedstorage.common.support.network.NetworkNodeBlockEntityTicker;

import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.VoxelShape;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersTranslation;

public class TieredImporterBlock extends AbstractDirectionalCableBlock implements
    ColorableBlock<TieredImporterBlock, BaseBlockItem>, EntityBlock, BlockItemProvider<BaseBlockItem> {
    private static final Component HELP_1 = createTranslation("item", "importer.help");
    private static final Component HELP_2 = createCableTiersTranslation("item", "tiered_cable.help");
    private static final Component HELP_3 = createCableTiersTranslation("item", "tiered_cable.help.stack_upgrade");
    private static final ConcurrentHashMap<DirectionalCacheShapeCacheKey, VoxelShape> SHAPE_CACHE = new ConcurrentHashMap<>();
    private final AbstractBlockEntityTicker<AbstractTieredImporterBlockEntity> ticker;

    private final DyeColor color;
    private final MutableComponent name;
    private final CableTiers tier;
    private final BlockEntityTierProvider<AbstractTieredImporterBlockEntity> blockEntityProvider;

    public TieredImporterBlock(final DyeColor color,
                               final MutableComponent name,
                               final CableTiers tier,
                               final BlockEntityTierProvider<AbstractTieredImporterBlockEntity> blockEntityProvider) {
        super(SHAPE_CACHE);
        this.color = color;
        this.name = name;
        this.tier = tier;
        this.blockEntityProvider = blockEntityProvider;
        this.ticker = new NetworkNodeBlockEntityTicker<>(() -> BlockEntities.INSTANCE.getTieredImporters(tier));
    }

    @Override
    public DyeColor getColor() {
        return color;
    }

    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return blockEntityProvider.create(tier, pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(final Level level,
                                                                  final BlockState blockState,
                                                                  final BlockEntityType<T> type) {
        return ticker.get(level, type);
    }

    @Override
    public BlockColorMap<TieredImporterBlock, BaseBlockItem> getBlockColorMap() {
        return Blocks.INSTANCE.getTieredImporters(tier);
    }

    @Override
    protected VoxelShape getExtensionShape(final Direction direction) {
        return switch (direction) {
            case NORTH -> DirectionalCableBlockShapes.IMPORTER_NORTH;
            case EAST -> DirectionalCableBlockShapes.IMPORTER_EAST;
            case SOUTH -> DirectionalCableBlockShapes.IMPORTER_SOUTH;
            case WEST -> DirectionalCableBlockShapes.IMPORTER_WEST;
            case UP -> DirectionalCableBlockShapes.IMPORTER_UP;
            case DOWN -> DirectionalCableBlockShapes.IMPORTER_DOWN;
        };
    }

    @Override
    public MutableComponent getName() {
        return name;
    }

    @Override
    public BaseBlockItem createBlockItem() {
        return new NetworkNodeBlockItem(this, null) {
            @Override
            public Optional<TooltipComponent> getTooltipImage(final ItemStack stack) {
                return Optional.of(new HelpTooltipComponent(Component.literal(
                    HELP_1.getString()
                    + " " + String.format(HELP_2.getString(), tier.getSpeed(CableType.IMPORTER) + "x", tier.getFilterSlotsCount())
                    + (tier != CableTiers.ELITE ? " " + HELP_3.getString() : "")
                )));
            }
        };
    }
}
