package com.YTrollman.CableTiers.node;

import com.YTrollman.CableTiers.CableTier;
import com.YTrollman.CableTiers.ContentType;
import com.YTrollman.CableTiers.config.CableConfig;
import com.YTrollman.CableTiers.tileentity.TieredDestructorTileEntity;
import com.refinedmods.refinedstorage.RS;
import com.refinedmods.refinedstorage.api.util.Action;
import com.refinedmods.refinedstorage.api.util.IComparer;
import com.refinedmods.refinedstorage.apiimpl.network.node.NetworkNode;
import com.refinedmods.refinedstorage.inventory.fluid.FluidInventory;
import com.refinedmods.refinedstorage.inventory.item.BaseItemHandler;
import com.refinedmods.refinedstorage.inventory.item.UpgradeItemHandler;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeFluidInventoryListener;
import com.refinedmods.refinedstorage.inventory.listener.NetworkNodeInventoryListener;
import com.refinedmods.refinedstorage.item.UpgradeItem;
import com.refinedmods.refinedstorage.tile.config.IComparable;
import com.refinedmods.refinedstorage.tile.config.IType;
import com.refinedmods.refinedstorage.tile.config.IWhitelistBlacklist;
import com.refinedmods.refinedstorage.util.StackUtils;
import com.refinedmods.refinedstorage.util.WorldUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.EntityPredicates;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import java.util.ArrayList;
import java.util.List;

public class TieredDestructorNetworkNode extends NetworkNode implements IComparable, IWhitelistBlacklist, IType {

    private static final String NBT_COMPARE = "Compare";
    private static final String NBT_MODE = "Mode";
    private static final String NBT_TYPE = "Type";
    private static final String NBT_PICKUP = "Pickup";
    private static final String NBT_FLUID_FILTERS = "FluidFilters";

    private static final int BASE_SPEED = 20;

    private final CableTier tier;
    private final ResourceLocation id;

    private final BaseItemHandler itemFilters;
    private final FluidInventory fluidFilters;

    private final UpgradeItemHandler upgrades;

    private int compare = IComparer.COMPARE_NBT;
    private int mode = IWhitelistBlacklist.BLACKLIST;
    private int type = IType.ITEMS;
    private boolean pickupItem = false;
    private ItemStack tool;

    public TieredDestructorNetworkNode(World world, BlockPos pos, CableTier tier) {
        super(world, pos);
        this.tier = tier;
        this.id = ContentType.DESTRUCTOR.getId(tier);
        this.itemFilters = new BaseItemHandler(tier.getSlots()).addListener(new NetworkNodeInventoryListener(this));
        this.fluidFilters = new FluidInventory(tier.getSlots()).addListener(new NetworkNodeFluidInventoryListener(this));
        this.upgrades = (UpgradeItemHandler) new UpgradeItemHandler(
                4,
                tier == CableTier.CREATIVE ? new UpgradeItem.Type[] {} : new UpgradeItem.Type[] { UpgradeItem.Type.SILK_TOUCH, UpgradeItem.Type.FORTUNE_1, UpgradeItem.Type.FORTUNE_2, UpgradeItem.Type.FORTUNE_3 })
                .addListener(new NetworkNodeInventoryListener(this))
                .addListener((handler, slot, reading) -> tool = createTool());
        this.tool = createTool();
    }

    @Override
    public int getEnergyUsage() {
        // TODO: change energy cost for higher tiers (wasn't implemented before)
        return 4 * (RS.SERVER_CONFIG.getDestructor().getUsage() + upgrades.getEnergyUsage());
    }

    @Override
    public void update() {
        super.update();

        if (!canUpdate() || !world.isLoaded(pos) || !world.isLoaded(pos.relative(getDirection()))) {
            return;
        }

        if (tier != CableTier.CREATIVE) {
            int baseSpeed = BASE_SPEED / getSpeedMultiplier();
            int speed = Math.max(1, upgrades.getSpeed(baseSpeed, 4));
            if (speed > 1 && ticks % speed != 0) {
                return;
            }
        }

        if (type == IType.ITEMS) {
            if (pickupItem) {
                pickupItems();
            } else {
                breakBlock();
            }
        } else if (type == IType.FLUIDS) {
            breakFluid();
        }
    }

    private int getSpeedMultiplier() {
        switch (tier) {
            case ELITE:
                return CableConfig.ELITE_DESTRUCTOR_SPEED.get();
            case ULTRA:
                return CableConfig.ULTRA_DESTRUCTOR_SPEED.get();
            default:
                throw new RuntimeException("illegal tier " + tier);
        }
    }

    private void pickupItems() {
        BlockPos front = pos.relative(getDirection());
        List<ItemEntity> droppedItems = new ArrayList<>();
        world.getChunkAt(front).getEntitiesOfClass(ItemEntity.class, new AxisAlignedBB(front), droppedItems, EntityPredicates.ENTITY_STILL_ALIVE);
        for (ItemEntity entity : droppedItems) {
            ItemStack droppedItem = entity.getItem();
            if (droppedItem.isEmpty() || !IWhitelistBlacklist.acceptsItem(itemFilters, mode, compare, droppedItem)) {
                continue;
            }

            ItemStack remaining = network.insertItemTracked(droppedItem.copy(), droppedItem.getCount());
            int inserted = droppedItem.getCount() - remaining.getCount();
            if (inserted > 0) {
                if (remaining.isEmpty()) {
                    entity.remove();
                } else {
                    entity.setItem(remaining);
                }

                if (tier != CableTier.CREATIVE) {
                    break;
                }
            }
        }
    }

    private void breakBlock() {
        BlockPos front = pos.relative(getDirection());
        BlockState frontBlockState = world.getBlockState(front);
        if (frontBlockState.getDestroySpeed(world, front) < 0) {
            return;
        }

        Block frontBlock = frontBlockState.getBlock();
        ItemStack frontStack = frontBlock.getPickBlock(
                frontBlockState,
                new BlockRayTraceResult(Vector3d.ZERO, getDirection().getOpposite(), front, false),
                world,
                front,
                WorldUtils.getFakePlayer((ServerWorld) world, getOwner())
        );
        if (frontStack.isEmpty() || !IWhitelistBlacklist.acceptsItem(itemFilters, mode, compare, frontStack)) {
            return;
        }

        List<ItemStack> drops = Block.getDrops(
                frontBlockState,
                (ServerWorld) world,
                front,
                world.getBlockEntity(front),
                WorldUtils.getFakePlayer((ServerWorld) world, getOwner()),
                tool
        );

        for (ItemStack drop : drops) {
            if (!network.insertItem(drop, drop.getCount(), Action.SIMULATE).isEmpty()) {
                return;
            }
        }

        BlockEvent.BreakEvent e = new BlockEvent.BreakEvent(world, front, frontBlockState, WorldUtils.getFakePlayer((ServerWorld) world, getOwner()));

        if (!MinecraftForge.EVENT_BUS.post(e)) {
            frontBlock.playerWillDestroy(world, front, frontBlockState, WorldUtils.getFakePlayer((ServerWorld) world, getOwner()));

            world.removeBlock(front, false);

            for (ItemStack drop : drops) {
                // We check if the controller isn't null here because when a destructor faces a node and removes it
                // it will essentially remove this block itself from the network without knowing
                if (network == null) {
                    InventoryHelper.dropItemStack(world, front.getX() + 0.5, front.getY() + 0.5, front.getZ() + 0.5, drop);
                } else {
                    ItemStack remainder = network.insertItemTracked(drop, drop.getCount());
                    if (!remainder.isEmpty()) {
                        throw new RuntimeException("could not insert dropped item stack into network");
                    }
                }
            }
        }
    }

    private void breakFluid() {
        BlockPos front = pos.relative(getDirection());
        BlockState frontBlockState = world.getBlockState(front);
        Block frontBlock = frontBlockState.getBlock();

        if (frontBlock instanceof FlowingFluidBlock) {
            // @Volatile: Logic from FlowingFluidBlock#pickupFluid
            if (frontBlockState.getValue(FlowingFluidBlock.LEVEL) == 0) {
                Fluid fluid = ((FlowingFluidBlock) frontBlock).getFluid();

                FluidStack result = new FluidStack(fluid, FluidAttributes.BUCKET_VOLUME);
                if (IWhitelistBlacklist.acceptsFluid(fluidFilters, mode, compare, result) && network.insertFluid(result, result.getAmount(), Action.SIMULATE).isEmpty()) {
                    world.setBlock(front, Blocks.AIR.defaultBlockState(), 11);

                    FluidStack remainder = network.insertFluidTracked(result, result.getAmount());
                    if (!remainder.isEmpty()) {
                        throw new RuntimeException("could not insert drained fluid stack into network");
                    }
                }
            }
        } else if (frontBlock instanceof IFluidBlock) {
            IFluidBlock fluidBlock = (IFluidBlock) frontBlock;
            if (fluidBlock.canDrain(world, front)) {
                FluidStack result = fluidBlock.drain(world, front, IFluidHandler.FluidAction.SIMULATE);
                if (IWhitelistBlacklist.acceptsFluid(fluidFilters, mode, compare, result) && network.insertFluid(result, result.getAmount(), Action.SIMULATE).isEmpty()) {
                    result = fluidBlock.drain(world, front, IFluidHandler.FluidAction.EXECUTE);

                    FluidStack remainder = network.insertFluidTracked(result, result.getAmount());
                    if (!remainder.isEmpty()) {
                        throw new RuntimeException("could not insert drained fluid stack into network");
                    }
                }
            }
        }
    }

    private ItemStack createTool() {
        ItemStack newTool = new ItemStack(tier == CableTier.CREATIVE ? Items.NETHERITE_PICKAXE : Items.DIAMOND_PICKAXE);

        if (upgrades.hasUpgrade(UpgradeItem.Type.SILK_TOUCH)) {
            newTool.enchant(Enchantments.SILK_TOUCH, 1);
        } else if (upgrades.hasUpgrade(UpgradeItem.Type.FORTUNE_3)) {
            newTool.enchant(Enchantments.BLOCK_FORTUNE, 3);
        } else if (upgrades.hasUpgrade(UpgradeItem.Type.FORTUNE_2)) {
            newTool.enchant(Enchantments.BLOCK_FORTUNE, 2);
        } else if (upgrades.hasUpgrade(UpgradeItem.Type.FORTUNE_1)) {
            newTool.enchant(Enchantments.BLOCK_FORTUNE, 1);
        }

        return newTool;
    }

    @Override
    public int getCompare() {
        return compare;
    }

    @Override
    public void setCompare(int compare) {
        this.compare = compare;
        markDirty();
    }

    @Override
    public int getWhitelistBlacklistMode() {
        return mode;
    }

    @Override
    public void setWhitelistBlacklistMode(int mode) {
        this.mode = mode;
        markDirty();
    }

    @Override
    public void read(CompoundNBT tag) {
        super.read(tag);
        StackUtils.readItems(upgrades, 1, tag);
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        StackUtils.writeItems(upgrades, 1, tag);
        return tag;
    }

    @Override
    public CompoundNBT writeConfiguration(CompoundNBT tag) {
        super.writeConfiguration(tag);
        tag.putInt(NBT_COMPARE, compare);
        tag.putInt(NBT_MODE, mode);
        tag.putInt(NBT_TYPE, type);
        tag.putBoolean(NBT_PICKUP, pickupItem);
        StackUtils.writeItems(itemFilters, 0, tag);
        tag.put(NBT_FLUID_FILTERS, fluidFilters.writeToNbt());
        return tag;
    }

    @Override
    public void readConfiguration(CompoundNBT tag) {
        super.readConfiguration(tag);
        if (tag.contains(NBT_COMPARE)) {
            compare = tag.getInt(NBT_COMPARE);
        }
        if (tag.contains(NBT_MODE)) {
            mode = tag.getInt(NBT_MODE);
        }
        if (tag.contains(NBT_TYPE)) {
            type = tag.getInt(NBT_TYPE);
        }
        if (tag.contains(NBT_PICKUP)) {
            pickupItem = tag.getBoolean(NBT_PICKUP);
        }
        StackUtils.readItems(itemFilters, 0, tag);
        if (tag.contains(NBT_FLUID_FILTERS)) {
            fluidFilters.readFromNbt(tag.getCompound(NBT_FLUID_FILTERS));
        }
    }

    public IItemHandler getUpgrades() {
        return upgrades;
    }

    @Override
    public IItemHandler getDrops() {
        return getUpgrades();
    }

    @Override
    public int getType() {
        return world.isClientSide ? TieredDestructorTileEntity.TYPE.getValue() : type;
    }

    @Override
    public void setType(int type) {
        this.type = type;
        markDirty();
    }

    @Override
    public IItemHandlerModifiable getItemFilters() {
        return itemFilters;
    }

    @Override
    public FluidInventory getFluidFilters() {
        return fluidFilters;
    }

    public boolean isPickupItem() {
        return pickupItem;
    }

    public void setPickupItem(boolean pickupItem) {
        this.pickupItem = pickupItem;
    }
}
