package com.ultramega.cabletiers.common.constructordestructor;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.registry.BlockEntities;
import com.ultramega.cabletiers.common.registry.Blocks;
import com.ultramega.cabletiers.common.utils.BlockEntityTierProvider;

import com.refinedmods.refinedstorage.common.api.support.HelpTooltipComponent;
import com.refinedmods.refinedstorage.common.constructordestructor.AbstractConstructorDestructorBlock;
import com.refinedmods.refinedstorage.common.content.BlockColorMap;
import com.refinedmods.refinedstorage.common.support.BaseBlockItem;
import com.refinedmods.refinedstorage.common.support.BlockItemProvider;
import com.refinedmods.refinedstorage.common.support.NetworkNodeBlockItem;
import com.refinedmods.refinedstorage.common.support.network.NetworkNodeBlockEntityTicker;
import com.refinedmods.refinedstorage.common.util.IdentifierUtil;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jspecify.annotations.Nullable;

import static com.ultramega.cabletiers.common.utils.CableTiersIdentifierUtil.createCableTiersTranslation;

public class TieredDestructorBlock extends AbstractConstructorDestructorBlock<TieredDestructorBlock, AbstractTieredDestructorBlockEntity, BaseBlockItem>
    implements BlockItemProvider<BaseBlockItem> {
    private static final Component HELP_1 = IdentifierUtil.createTranslation("item", "destructor.help");
    private static final Component HELP_2 = createCableTiersTranslation("item", "tiered_cable.help");

    private final Identifier id;
    private final CableTiers tier;
    private final BlockEntityTierProvider<AbstractTieredDestructorBlockEntity> blockEntityProvider;

    public TieredDestructorBlock(final Identifier id,
                                 final DyeColor color,
                                 final MutableComponent name,
                                 final CableTiers tier,
                                 final BlockEntityTierProvider<AbstractTieredDestructorBlockEntity> blockEntityProvider) {
        super(id, color, name, new NetworkNodeBlockEntityTicker<>(
            () -> BlockEntities.INSTANCE.getTieredDestructors(tier),
            ACTIVE
        ));
        this.id = id;
        this.tier = tier;
        this.blockEntityProvider = blockEntityProvider;
    }

    @Override
    public BlockColorMap<TieredDestructorBlock, BaseBlockItem> getBlockColorMap() {
        return Blocks.INSTANCE.getTieredDestructors(this.tier);
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(final BlockPos pos, final BlockState state) {
        return this.blockEntityProvider.create(this.tier, pos, state);
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
                                Component.literal(tier.getSpeed(CableType.DESTRUCTOR) + "x"),
                                Component.literal(String.valueOf(tier.getFilterSlotCount()))
                            ))
                    )
                );
            }
        };
    }

    @Override
    protected boolean shouldChangedStateKeepBlockEntity(final BlockState oldState) {
        return oldState.getBlock() instanceof TieredDestructorBlock;
    }
}
