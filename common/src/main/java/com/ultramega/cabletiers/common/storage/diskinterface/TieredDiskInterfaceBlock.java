package com.ultramega.cabletiers.common.storage.diskinterface;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.registry.BlockEntities;
import com.ultramega.cabletiers.common.registry.Blocks;
import com.ultramega.cabletiers.common.storage.AdvancedStorageTransferNetworkNode;
import com.ultramega.cabletiers.common.storage.TieredDiskContainerBlockEntityTicker;
import com.ultramega.cabletiers.common.utils.BlockEntityTierProvider;

import com.refinedmods.refinedstorage.common.api.support.HelpTooltipComponent;
import com.refinedmods.refinedstorage.common.content.BlockColorMap;
import com.refinedmods.refinedstorage.common.content.BlockConstants;
import com.refinedmods.refinedstorage.common.support.AbstractActiveColoredDirectionalBlock;
import com.refinedmods.refinedstorage.common.support.BaseBlockItem;
import com.refinedmods.refinedstorage.common.support.BlockItemProvider;
import com.refinedmods.refinedstorage.common.support.NetworkNodeBlockItem;
import com.refinedmods.refinedstorage.common.support.direction.BiDirection;
import com.refinedmods.refinedstorage.common.support.direction.BiDirectionType;
import com.refinedmods.refinedstorage.common.support.direction.DirectionType;

import java.util.Optional;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
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

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersTranslation;

public class TieredDiskInterfaceBlock extends AbstractActiveColoredDirectionalBlock<BiDirection, TieredDiskInterfaceBlock, BaseBlockItem>
    implements EntityBlock, BlockItemProvider<BaseBlockItem> {
    private static final Component HELP_1 = createTranslation("item", "disk_interface.help");
    private static final Component HELP_2 = createCableTiersTranslation("item", "tiered_disk_interface.help");
    private static final Component HELP_3 = createCableTiersTranslation("item", "tiered_cable.help.stack_upgrade");
    private final TieredDiskContainerBlockEntityTicker<AdvancedStorageTransferNetworkNode, AbstractTieredDiskInterfaceBlockEntity> ticker;

    private final CableTiers tier;
    private final BlockEntityTierProvider<AbstractTieredDiskInterfaceBlockEntity> blockEntityProvider;

    public TieredDiskInterfaceBlock(final DyeColor color,
                                    final MutableComponent name,
                                    final CableTiers tier,
                                    final BlockEntityTierProvider<AbstractTieredDiskInterfaceBlockEntity> blockEntityProvider) {
        super(BlockConstants.PROPERTIES, color, name);
        this.tier = tier;
        this.blockEntityProvider = blockEntityProvider;
        this.ticker = new TieredDiskContainerBlockEntityTicker<>(() -> BlockEntities.INSTANCE.getTieredDiskInterfaces(tier), ACTIVE);
    }

    @Override
    protected DirectionType<BiDirection> getDirectionType() {
        return BiDirectionType.INSTANCE;
    }

    @Nullable
    @Override
    public <O extends BlockEntity> BlockEntityTicker<O> getTicker(final Level level,
                                                                  final BlockState blockState,
                                                                  final BlockEntityType<O> type) {
        return ticker.get(level, type);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(final BlockPos blockPos, final BlockState blockState) {
        return blockEntityProvider.create(tier, blockPos, blockState);
    }

    @Override
    public BlockColorMap<TieredDiskInterfaceBlock, BaseBlockItem> getBlockColorMap() {
        return Blocks.INSTANCE.getTieredDiskInterfaces(tier);
    }

    @Override
    public BaseBlockItem createBlockItem() {
        return new NetworkNodeBlockItem(this, null) {
            @Override
            public Optional<TooltipComponent> getTooltipImage(final ItemStack stack) {
                return Optional.of(new HelpTooltipComponent(Component.literal(
                    HELP_1.getString()
                        + " " + String.format(HELP_2.getString(), tier.getFilterSlotsCount())
                        + (tier != CableTiers.ELITE ? " " + HELP_3.getString() : "")
                )));
            }
        };
    }

    @Override
    public boolean canAlwaysConnect() {
        return true;
    }
}
