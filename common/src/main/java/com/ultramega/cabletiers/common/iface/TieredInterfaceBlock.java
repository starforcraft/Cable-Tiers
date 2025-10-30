package com.ultramega.cabletiers.common.iface;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.registry.BlockEntities;

import com.refinedmods.refinedstorage.common.api.support.HelpTooltipComponent;
import com.refinedmods.refinedstorage.common.content.BlockConstants;
import com.refinedmods.refinedstorage.common.support.AbstractBaseBlock;
import com.refinedmods.refinedstorage.common.support.AbstractBlockEntityTicker;
import com.refinedmods.refinedstorage.common.support.NetworkNodeBlockItem;
import com.refinedmods.refinedstorage.common.support.network.NetworkNodeBlockEntityTicker;

import java.util.Optional;
import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.createTranslation;
import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersTranslation;

public class TieredInterfaceBlock extends AbstractBaseBlock implements EntityBlock {
    public static final BooleanProperty ACTIVE = BooleanProperty.create("active");

    private static final Component HELP_1 = createTranslation("item", "interface.help");
    private static final Component HELP_2 = createCableTiersTranslation("item", "tiered_interface.help");
    private final AbstractBlockEntityTicker<TieredInterfaceBlockEntity> ticker;

    private final CableTiers tier;

    public TieredInterfaceBlock(final CableTiers tier) {
        super(BlockConstants.PROPERTIES);
        this.tier = tier;
        this.ticker = new NetworkNodeBlockEntityTicker<>(() -> BlockEntities.INSTANCE.getTieredInterfaces(tier), ACTIVE);
    }

    @Override
    protected BlockState getDefaultState() {
        return super.getDefaultState().setValue(ACTIVE, false);
    }

    @Override
    protected void createBlockStateDefinition(final StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(ACTIVE);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return new TieredInterfaceBlockEntity(tier, pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(final Level level,
                                                                  final BlockState blockState,
                                                                  final BlockEntityType<T> type) {
        return ticker.get(level, type);
    }

    public BlockItem createBlockItem() {
        return new NetworkNodeBlockItem(this, null) {
            @Override
            public Optional<TooltipComponent> getTooltipImage(final ItemStack stack) {
                return Optional.of(new HelpTooltipComponent(Component.literal(
                    HELP_1.getString()
                        + " " + String.format(HELP_2.getString(), tier.getTransferQuotaMultiplier(CableType.INTERFACE) + "x", tier.getInterfaceSlotsCount())
                )));
            }
        };
    }
}
