package com.YTrollman.CableTiers.blocks;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.node.TieredNetworkNode;
import com.YTrollman.CableTiers.blockentity.TieredBlockEntity;
import com.refinedmods.refinedstorage.block.NetworkNodeBlock;
import com.refinedmods.refinedstorage.container.factory.BlockEntityMenuProvider;
import com.refinedmods.refinedstorage.util.BlockUtils;
import com.refinedmods.refinedstorage.util.NetworkUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;

public abstract class TieredNetworkBlock<T extends TieredBlockEntity<N>, N extends TieredNetworkNode<N>> extends NetworkNodeBlock {

    private final ContentType<? extends TieredNetworkBlock<T, N>, T, ?, N> contentType;
    private final CableTier tier;

    protected TieredNetworkBlock(ContentType<? extends TieredNetworkBlock<T, N>, T, ?, N> contentType, CableTier tier) {
        super(BlockUtils.DEFAULT_ROCK_PROPERTIES);
        this.contentType = contentType;
        this.tier = tier;
    }

    public ContentType<? extends TieredNetworkBlock<T, N>, T, ?, N> getContentType() {
        return contentType;
    }

    public CableTier getTier() {
        return tier;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return contentType.getBlockEntityType(tier).create(pos, state);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (!level.isClientSide) {
            return NetworkUtils.attemptModify (
                    level,
                    pos,
                    player,
                    () -> NetworkHooks.openGui(
                            (ServerPlayer) player,
                            new BlockEntityMenuProvider<T>(
                                    new TranslatableComponent(getDescriptionId()),
                                    (tile, windowId, inventory, p) -> contentType.createContainer(windowId, p, tile),
                                    pos
                            ),
                            pos
                    )
            );
        }

        return InteractionResult.SUCCESS;
    }
}
