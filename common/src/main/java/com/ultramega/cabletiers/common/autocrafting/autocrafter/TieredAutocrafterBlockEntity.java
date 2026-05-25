package com.ultramega.cabletiers.common.autocrafting.autocrafter;

import com.ultramega.cabletiers.common.CableTiers;
import com.ultramega.cabletiers.common.CableType;
import com.ultramega.cabletiers.common.Platform;
import com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedInputPatternState;
import com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedInputRouting;
import com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedResourceAmount;
import com.ultramega.cabletiers.common.registry.BlockEntities;
import com.ultramega.cabletiers.common.registry.DataComponents;
import com.ultramega.cabletiers.common.utils.ContentNames;

import com.refinedmods.refinedstorage.api.autocrafting.Pattern;
import com.refinedmods.refinedstorage.api.autocrafting.task.ExternalPatternSink;
import com.refinedmods.refinedstorage.api.autocrafting.task.ExternalPatternSink.Result;
import com.refinedmods.refinedstorage.api.autocrafting.task.ExternalPatternSinkId;
import com.refinedmods.refinedstorage.api.autocrafting.task.StepBehavior;
import com.refinedmods.refinedstorage.api.autocrafting.task.Task;
import com.refinedmods.refinedstorage.api.autocrafting.task.TaskImpl;
import com.refinedmods.refinedstorage.api.autocrafting.task.TaskSnapshot;
import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.autocrafting.AutocraftingNetworkComponent;
import com.refinedmods.refinedstorage.api.network.autocrafting.PatternProvider;
import com.refinedmods.refinedstorage.api.network.autocrafting.PatternProviderExternalPatternSink;
import com.refinedmods.refinedstorage.api.network.impl.node.patternprovider.PatternProviderListener;
import com.refinedmods.refinedstorage.api.network.node.importer.ImporterTransferStrategy;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.autocrafting.PlatformPatternProviderExternalPatternSink;
import com.refinedmods.refinedstorage.common.api.support.network.InWorldNetworkNodeContainer;
import com.refinedmods.refinedstorage.common.autocrafting.PatternInventory;
import com.refinedmods.refinedstorage.common.autocrafting.autocrafter.AutocrafterBlockEntity;
import com.refinedmods.refinedstorage.common.autocrafting.autocrafter.AutocrafterData;
import com.refinedmods.refinedstorage.common.autocrafting.autocrafter.AutocrafterExternalPatternSinkDetails;
import com.refinedmods.refinedstorage.common.autocrafting.autocrafter.LockMode;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.common.support.AbstractDirectionalBlock;
import com.refinedmods.refinedstorage.common.support.FilteredContainer;
import com.refinedmods.refinedstorage.common.support.containermenu.ExtendedMenuProvider;
import com.refinedmods.refinedstorage.common.support.network.AbstractBaseNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeContainer;
import com.refinedmods.refinedstorage.common.upgrade.UpgradeDestinations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamEncoder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Containers;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.refinedmods.refinedstorage.common.support.AbstractDirectionalBlock.tryExtractDirection;
import static com.ultramega.cabletiers.common.autocrafting.sidedinput.SidedInputRouting.resourcesMatchIgnoringIndex;
import static com.ultramega.cabletiers.common.importer.AbstractTieredImporterBlockEntity.createStrategy;

public class TieredAutocrafterBlockEntity extends AbstractBaseNetworkNodeContainerBlockEntity<ExtendedPatternProviderNetworkNode>
    implements ExtendedMenuProvider<AutocrafterData>, StepBehavior, PatternProviderExternalPatternSink, PatternProviderListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(TieredAutocrafterBlockEntity.class);

    private static final int MAX_CHAINED_AUTOCRAFTERS = 8;
    private static final String TAG_UPGRADES = "upgr";
    private static final String TAG_PATTERNS = "patterns";
    private static final String TAG_LOCK_MODE = "lm";
    private static final String TAG_PRIORITY = "pri";
    private static final String TAG_TASKS = "tasks";
    private static final String TAG_VISIBLE_TO_THE_AUTOCRAFTER_MANAGER = "vaum";
    private static final String TAG_IMPORT_MODE = "im";
    private static final String TAG_LOCKED = "locked";
    private static final String TAG_WAS_POWERED = "wp";
    private static final String TAG_ID = "auid";

    private final PatternInventory patternContainer;
    private final UpgradeContainer upgradeContainer;
    private LockMode lockMode = LockMode.NEVER;
    private ImportMode importMode = ImportMode.DONT_IMPORT;
    private boolean visibleToTheAutocrafterManager = true;
    private int ticks;
    private int steps;
    private int tickRate;
    @Nullable
    private final PlatformPatternProviderExternalPatternSink[] sinks = new PlatformPatternProviderExternalPatternSink[Direction.values().length];
    @Nullable
    private ExternalPatternSinkId id;
    @Nullable
    private AutocrafterExternalPatternSinkDetails lazyDetails;
    private boolean wasPowered;
    private boolean locked;

    private final CableTiers tier;

    public TieredAutocrafterBlockEntity(final CableTiers tier, final BlockPos pos, final BlockState state) {
        super(
            BlockEntities.INSTANCE.getTieredAutocrafters(tier),
            pos,
            state,
            new ExtendedPatternProviderNetworkNode(Platform.getConfig().getTieredAutocrafters().getEnergyUsage(tier), tier.getAutocrafterPatternSlotCount())
        );
        this.tier = tier;
        this.steps = getSteps(0, tier);
        this.tickRate = getTickRate(0, tier);
        this.patternContainer = new PatternInventory(tier.getAutocrafterPatternSlotCount(), this::getLevel) {
            @Override
            public void setChanged() {
                super.setChanged();
                final long upgradeEnergyUsage = TieredAutocrafterBlockEntity.this.upgradeContainer.getEnergyUsage();
                final long baseEnergyUsage = Platform.getConfig().getTieredAutocrafters().getEnergyUsage(tier);
                final long patternEnergyUsage = TieredAutocrafterBlockEntity.this.patternContainer.getEnergyUsage();
                TieredAutocrafterBlockEntity.this.mainNetworkNode.setEnergyUsage(baseEnergyUsage + patternEnergyUsage + upgradeEnergyUsage);
                TieredAutocrafterBlockEntity.this.setChanged();
            }
        };
        this.upgradeContainer = new UpgradeContainer(UpgradeDestinations.AUTOCRAFTER, (c, upgradeEnergyUsage) -> {
            final long baseEnergyUsage = Platform.getConfig().getTieredAutocrafters().getEnergyUsage(tier);
            final long patternEnergyUsage = this.patternContainer.getEnergyUsage();
            this.mainNetworkNode.setEnergyUsage(baseEnergyUsage + patternEnergyUsage + upgradeEnergyUsage);
            final int amountOfSpeedUpgrades = c.getAmount(Items.INSTANCE.getSpeedUpgrade());
            this.tickRate = getTickRate(amountOfSpeedUpgrades, tier);
            this.steps = getSteps(amountOfSpeedUpgrades, tier);
        }, this::setChanged);
        this.patternContainer.setListener(this::onPatternChanged);
        this.mainNetworkNode.setStepBehavior(this);
        this.mainNetworkNode.setDetailsProvider(this::getOrLoadDetails);
        this.mainNetworkNode.setSink(this);
        this.mainNetworkNode.setListener(this);
        this.mainNetworkNode.onAddedIntoContainer(new AutocrafterParentContainer(this));
    }

    @Override
    protected InWorldNetworkNodeContainer createMainContainer(final ExtendedPatternProviderNetworkNode networkNode) {
        return new AutocrafterNetworkNodeContainer(
            this,
            networkNode,
            "main",
            new AutocrafterConnectionStrategy(this::getBlockState, this.getBlockPos())
        );
    }

    FilteredContainer getPatternContainer() {
        return this.patternContainer;
    }

    UpgradeContainer getUpgradeContainer() {
        return this.upgradeContainer;
    }

    private boolean isPartOfChain() {
        return this.getChainingRoot() != this;
    }

    // If there is another autocrafter next to us, that is pointing in our direction,
    // and we are not part of a chain, we are the head of the chain
    private boolean isHeadOfChain() {
        if (this.level == null || this.isPartOfChain()) {
            return false;
        }
        for (final Direction direction : Direction.values()) {
            final BlockPos pos = this.getBlockPos().relative(direction);
            if (!this.level.isLoaded(pos)) {
                continue;
            }
            final BlockEntity neighbor = this.level.getBlockEntity(pos);
            if (neighbor instanceof TieredAutocrafterBlockEntity neighborAutocrafter) {
                final Direction neighborDirection = tryExtractDirection(neighborAutocrafter.getBlockState());
                if (neighborDirection == direction.getOpposite()) {
                    return true;
                }
            }
        }
        return false;
    }

    private TieredAutocrafterBlockEntity getChainingRoot() {
        return this.getChainingRoot(0, this);
    }

    private TieredAutocrafterBlockEntity getChainingRoot(final int depth, final TieredAutocrafterBlockEntity origin) {
        final Direction direction = tryExtractDirection(this.getBlockState());
        if (this.level == null || direction == null || depth >= MAX_CHAINED_AUTOCRAFTERS) {
            return origin;
        }
        final BlockEntity neighbor = this.getConnectedMachine();
        if (!(neighbor instanceof TieredAutocrafterBlockEntity neighborCrafter)) {
            return this;
        }
        return neighborCrafter.getChainingRoot(depth + 1, origin);
    }

    @Nullable
    private BlockEntity getConnectedMachine() {
        final Direction direction = tryExtractDirection(this.getBlockState());
        if (this.level == null || direction == null) {
            return null;
        }
        final BlockPos neighborPos = this.getBlockPos().relative(direction);
        if (!this.level.isLoaded(neighborPos)) {
            return null;
        }
        return this.level.getBlockEntity(neighborPos);
    }

    @Override
    public Component getName() {
        final TieredAutocrafterBlockEntity root = this.getChainingRoot();
        if (root == this) {
            return this.doGetName();
        }
        return root.getName();
    }

    private Component doGetName() {
        final Component customName = this.getCustomName();
        if (customName != null) {
            return customName;
        }
        final BlockEntity connectedMachine = this.getConnectedMachine();
        // We don't handle autocrafters here, as autocrafters are also nameable, and we could have infinite recursion.
        if (connectedMachine instanceof Nameable nameable
            && !(connectedMachine instanceof AutocrafterBlockEntity)
            && !(connectedMachine instanceof TieredAutocrafterBlockEntity)) {
            return nameable.getName();
        } else if (connectedMachine != null) {
            return connectedMachine.getBlockState().getBlock().getName();
        }
        return ContentNames.getContentName(this.tier, CableType.AUTOCRAFTER);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(final int syncId, final Inventory inventory, final Player player) {
        return new TieredAutocrafterContainerMenu(syncId, inventory, this, this.tier);
    }

    @Override
    public AutocrafterData getMenuData() {
        return new AutocrafterData(this.isPartOfChain(), this.isHeadOfChain(), this.locked);
    }

    @Override
    public StreamEncoder<RegistryFriendlyByteBuf, AutocrafterData> getMenuCodec() {
        return AutocrafterData.STREAM_CODEC;
    }

    private List<TaskSnapshot> collectTaskSnapshots() {
        final List<TaskSnapshot> snapshots = new ArrayList<>();
        for (final Task task : this.mainNetworkNode.getTasks()) {
            if (task instanceof TaskImpl taskImpl) {
                try {
                    snapshots.add(taskImpl.createSnapshot());
                } catch (final Exception e) {
                    LOGGER.error("Error while creating snapshot for task {} {}", task.getResource(), task.getAmount(), e);
                }
            }
        }
        return snapshots;
    }

    @Override
    public void saveAdditional(final ValueOutput output) {
        super.saveAdditional(output);
        output.store(TAG_PATTERNS, ItemContainerContents.CODEC, ItemContainerContents.fromItems(this.patternContainer.getItems()));
        output.store(TAG_UPGRADES, ItemContainerContents.CODEC, ItemContainerContents.fromItems(this.upgradeContainer.getItems()));
        output.store(TAG_TASKS, TaskSnapshotCodecs.LIST_CODEC, this.collectTaskSnapshots());
        output.putBoolean(TAG_LOCKED, this.locked);
        output.putBoolean(TAG_WAS_POWERED, this.wasPowered);
        if (this.id != null) {
            output.store(TAG_ID, UUIDUtil.CODEC, this.id.id());
        }
    }

    @Override
    public void loadAdditional(final ValueInput input) {
        input.read(TAG_PATTERNS, ItemContainerContents.CODEC)
            .ifPresent(contents -> contents.copyInto(this.patternContainer.getItems()));
        input.read(TAG_UPGRADES, ItemContainerContents.CODEC).ifPresent(this.upgradeContainer::load);
        input.read(TAG_TASKS, TaskSnapshotCodecs.LIST_CODEC)
            .ifPresent(snapshots ->
                snapshots.forEach(snapshot -> this.mainNetworkNode.addTask(new TaskImpl(snapshot))));
        this.locked = input.getBooleanOr(TAG_LOCKED, false);
        this.wasPowered = input.getBooleanOr(TAG_WAS_POWERED, false);
        if (this.level != null && !this.level.isClientSide()) {
            this.onPatternChanged();
        }
        this.id = new ExternalPatternSinkId(input.read(TAG_ID, UUIDUtil.CODEC).orElseGet(UUID::randomUUID));
        this.mainNetworkNode.setId(this.id);
        super.loadAdditional(input);
    }

    @Override
    public void writeConfiguration(final ValueOutput output) {
        super.writeConfiguration(output);
        output.putInt(TAG_LOCK_MODE, LockModeSettings.getLockMode(this.lockMode));
        output.putInt(TAG_IMPORT_MODE, ImportModeSettings.getImportMode(this.importMode));
        output.putInt(TAG_PRIORITY, this.mainNetworkNode.getPriority());
        output.putBoolean(TAG_VISIBLE_TO_THE_AUTOCRAFTER_MANAGER, this.visibleToTheAutocrafterManager);
    }

    @Override
    public void readConfiguration(final ValueInput input) {
        super.readConfiguration(input);
        this.lockMode = input.getInt(TAG_LOCK_MODE)
            .map(LockModeSettings::getLockMode)
            .orElse(LockMode.NEVER);
        this.importMode = input.getInt(TAG_IMPORT_MODE)
            .map(ImportModeSettings::getImportMode)
            .orElse(ImportMode.DONT_IMPORT);
        input.getInt(TAG_PRIORITY).ifPresent(this.mainNetworkNode::setPriority);
        this.visibleToTheAutocrafterManager = input.getBooleanOr(TAG_VISIBLE_TO_THE_AUTOCRAFTER_MANAGER, true);
    }

    @Override
    protected boolean hasRedstoneMode() {
        return false;
    }

    @Override
    public List<ItemStack> getUpgrades() {
        return this.upgradeContainer.getUpgrades();
    }

    @Override
    public boolean addUpgrade(final ItemStack upgradeStack) {
        return this.upgradeContainer.addUpgrade(upgradeStack);
    }

    @Override
    public void preRemoveSideEffects(final BlockPos pos, final BlockState state) {
        super.preRemoveSideEffects(pos, state);
        if (this.level != null) {
            final NonNullList<ItemStack> drops = NonNullList.create();
            drops.addAll(this.upgradeContainer.getDrops());
            for (int i = 0; i < this.patternContainer.getContainerSize(); ++i) {
                drops.add(this.patternContainer.getItem(i));
            }
            Containers.dropContents(this.level, pos, drops);
        }
    }

    void setCustomName(final String name) {
        if (this.isPartOfChain()) {
            return;
        }
        this.setCustomName(name.trim().isBlank() ? null : Component.literal(name));
        this.setChanged();
    }

    LockMode getLockMode() {
        return this.lockMode;
    }

    void setLockMode(final LockMode lockMode) {
        this.lockMode = lockMode;
        this.locked = false;
        this.wasPowered = false;
        this.setChanged();
    }

    int getPriority() {
        return this.mainNetworkNode.getPriority();
    }

    void setPriority(final int priority) {
        this.mainNetworkNode.setPriority(priority);
        this.setChanged();
    }

    boolean isVisibleToTheAutocrafterManager() {
        return this.visibleToTheAutocrafterManager;
    }

    void setVisibleToTheAutocrafterManager(final boolean visibleToTheAutocrafterManager) {
        this.visibleToTheAutocrafterManager = visibleToTheAutocrafterManager;
        this.setChanged();
    }

    ImportMode getImportMode() {
        return this.importMode;
    }

    void setImportMode(final ImportMode importMode) {
        this.importMode = importMode;
        this.setChanged();
    }

    @Override
    public void setLevel(final Level level) {
        super.setLevel(level);
        if (!level.isClientSide()) {
            this.onPatternChanged();
        }
    }

    @Override
    protected void initialize(final ServerLevel level, final Direction direction) {
        super.initialize(level, direction);
        if (this.id == null) {
            this.id = ExternalPatternSinkId.create();
            this.mainNetworkNode.setId(this.id);
        }
        this.invalidateDetails();
        for (int i = 0; i < Direction.values().length; i++) {
            final Direction incomingDirection = Direction.values()[i];
            final BlockPos sourcePosition = this.worldPosition.relative(direction);
            this.sinks[i] = com.refinedmods.refinedstorage.common.Platform.INSTANCE.getPatternProviderExternalPatternSinkFactory()
                .create(level, sourcePosition, incomingDirection);
        }
        this.mainNetworkNode.setImportMode(this::getImportMode);
        final ImporterTransferStrategy strategy = createStrategy(level, direction, this.worldPosition, this.upgradeContainer);
        LOGGER.debug("Initialized autocrafter at {} with strategy {}", this.worldPosition, strategy);
        this.mainNetworkNode.setTransferStrategy(strategy);
    }

    private void onPatternChanged() {
        if (this.level == null) {
            return;
        }
        for (int i = 0; i < this.patternContainer.getContainerSize(); ++i) {
            final Pattern pattern = RefinedStorageApi.INSTANCE.getPattern(this.patternContainer.getItem(i), this.level)
                .orElse(null);
            this.mainNetworkNode.tryUpdatePattern(i, pattern);
            this.mainNetworkNode.updatePatternOutputFilter(i, pattern);
        }
    }

    @Override
    public void doWork() {
        for (int i = 0; i < this.tier.getSpeed(CableType.AUTOCRAFTER); i++) {
            super.doWork();
        }
        if (this.mainNetworkNode.isActive()) {
            this.ticks++;
        }
        this.updateLocked();
    }

    private void updateLocked() {
        final boolean newLocked = this.calculateLocked();
        if (newLocked != this.locked) {
            this.setLocked(newLocked);
        }
    }

    private void setLocked(final boolean locked) {
        this.locked = locked;
        this.setChanged();
    }

    boolean isLocked() {
        return this.locked;
    }

    private boolean calculateLocked() {
        if (this.level == null) {
            return false;
        }
        return switch (this.lockMode) {
            case NEVER -> false;
            case LOCK_UNTIL_REDSTONE_PULSE_RECEIVED -> this.isLockedInPulseMode();
            case LOCK_UNTIL_CONNECTED_MACHINE_IS_EMPTY -> !Arrays.stream(this.sinks)
                .filter(Objects::nonNull)
                .allMatch(PlatformPatternProviderExternalPatternSink::isEmpty);
            case LOCK_UNTIL_ALL_OUTPUTS_ARE_RECEIVED -> this.locked;
            case LOCK_UNTIL_HIGH_REDSTONE_SIGNAL -> !this.level.hasNeighborSignal(this.worldPosition);
            case LOCK_UNTIL_LOW_REDSTONE_SIGNAL -> this.level.hasNeighborSignal(this.worldPosition);
        };
    }

    private boolean isLockedInPulseMode() {
        if (this.level != null && this.level.hasNeighborSignal(this.worldPosition)) {
            this.wasPowered = true;
            this.setChanged();
        } else if (this.wasPowered) {
            this.wasPowered = false;
            this.setChanged();
            return false;
        }
        return this.locked;
    }

    @Override
    public boolean canStep(final Pattern pattern) {
        final PatternProvider provider = this.lookupProvider(pattern);
        if (provider == null) {
            return false;
        }
        if (provider == this.mainNetworkNode) {
            if (this.tickRate == 0) {
                return true;
            }
            return this.mainNetworkNode.isActive() && this.ticks % this.tickRate == 0;
        }
        return provider.canStep(pattern);
    }

    @Override
    public int getSteps(final Pattern pattern) {
        final PatternProvider provider = this.lookupProvider(pattern);
        if (provider == null) {
            return 0;
        }
        if (provider == this.mainNetworkNode) {
            return this.steps;
        }
        return provider.getSteps(pattern);
    }

    private static int getSteps(final int amountOfSpeedUpgrades, final CableTiers tier) {
        final int speed = switch (amountOfSpeedUpgrades) {
            case 1 -> 2;
            case 2 -> 3;
            case 3 -> 4;
            case 4 -> 5;
            default -> 1;
        };

        return speed * tier.getSpeed(CableType.AUTOCRAFTER);
    }

    private static int getTickRate(final int amountOfSpeedUpgrades, final CableTiers tier) {
        final int speed = switch (amountOfSpeedUpgrades) {
            case 0 -> 10;
            case 1 -> 8;
            case 2 -> 6;
            case 3 -> 4;
            case 4 -> 2;
            default -> 0;
        };

        return Math.max(0, speed / tier.getSpeed(CableType.AUTOCRAFTER));
    }

    @Nullable
    private PatternProvider lookupProvider(final Pattern pattern) {
        final Network network = this.mainNetworkNode.getNetwork();
        if (network == null) {
            return null;
        }
        return network.getComponent(AutocraftingNetworkComponent.class).getProviderByPattern(pattern);
    }

    @Override
    protected boolean doesBlockStateChangeWarrantNetworkNodeUpdate(final BlockState oldBlockState,
                                                                   final BlockState newBlockState) {
        return AbstractDirectionalBlock.didDirectionChange(oldBlockState, newBlockState);
    }

    private void invalidateDetails() {
        this.lazyDetails = null;
    }

    @Nullable
    private AutocrafterExternalPatternSinkDetails getOrLoadDetails() {
        if (this.lazyDetails != null) {
            return this.lazyDetails;
        }
        this.lazyDetails = this.loadDetails();
        return this.lazyDetails;
    }

    @Nullable
    private AutocrafterExternalPatternSinkDetails loadDetails() {
        if (!(this.level instanceof ServerLevel serverLevel)) {
            return null;
        }
        final Direction direction = tryExtractDirection(this.getBlockState());
        if (direction == null) {
            return null;
        }
        final TieredAutocrafterBlockEntity root = this.getChainingRoot();
        final BlockEntity connectedMachine = root.getConnectedMachine();
        if (connectedMachine == null) {
            return null;
        }
        return this.loadDetails(serverLevel, connectedMachine, direction);
    }

    @Nullable
    private AutocrafterExternalPatternSinkDetails loadDetails(final ServerLevel serverLevel, final BlockEntity connectedMachine, final Direction direction) {
        final BlockState connectedMachineState = connectedMachine.getBlockState();
        final Player fakePlayer = this.getFakePlayer(serverLevel);
        final ItemStack connectedMachineStack = com.refinedmods.refinedstorage.common.Platform.INSTANCE.getBlockAsItemStack(
            connectedMachineState.getBlock(),
            connectedMachineState,
            direction.getOpposite(),
            serverLevel,
            connectedMachine.getBlockPos(),
            fakePlayer
        );
        if (connectedMachineStack.isEmpty()) {
            return null;
        }
        return new AutocrafterExternalPatternSinkDetails(this.getName().getString(), connectedMachineStack);
    }

    @Override
    public Result insertAll(final Collection<ResourceAmount> resources, final Action action) {
        final TieredAutocrafterBlockEntity root = this.getChainingRoot();
        if (root != this) {
            return root.insertAll(resources, action);
        }
        if (Arrays.stream(this.sinks).allMatch(Objects::isNull)) {
            return Result.SKIPPED;
        }
        if (this.locked) {
            return Result.LOCKED;
        }

        final Direction baseDirection = tryExtractDirection(this.getBlockState());
        return findResult(this.sinks, this.patternContainer, baseDirection, resources, action, this::updateLockedAfterAccept);
    }

    public static Result findResult(final PlatformPatternProviderExternalPatternSink[] sinks,
                                    final FilteredContainer patternContainer,
                                    @Nullable final Direction baseDirection,
                                    final Collection<ResourceAmount> resources,
                                    final Action action,
                                    final BiConsumer<Action, Result> afterAccept) {
        @Nullable
        final SidedInputPatternState sidedInputState = findSidedInputPatternState(patternContainer, resources.stream().toList());
        return SidedInputRouting.findResult(sinks, baseDirection, resources, action, afterAccept, sidedInputState);
    }

    @Nullable
    public static SidedInputPatternState findSidedInputPatternState(final FilteredContainer patternContainer, final List<ResourceAmount> resources) {
        for (int i = 0; i < patternContainer.getContainerSize(); i++) {
            final ItemStack pattern = patternContainer.getItem(i);
            final SidedInputPatternState sidedInputState = pattern.get(DataComponents.INSTANCE.getSidedInputPatternState());
            if (sidedInputState == null) {
                continue;
            }

            final List<SidedResourceAmount> sidedResources = sidedInputState.sidedResources().stream()
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();
            if (!resourcesMatchIgnoringIndex(sidedResources, resources)) {
                continue;
            }

            return sidedInputState;
        }

        return null;
    }

    private void updateLockedAfterAccept(final Action action, final ExternalPatternSink.Result result) {
        // If we are using speed upgrades, we will try multiple insertions (steps) in 1 tick.
        // That is too late, however, if we only update the locked state every tick.
        if (result == ExternalPatternSink.Result.ACCEPTED
            && action == Action.EXECUTE
            && this.lockMode == LockMode.LOCK_UNTIL_CONNECTED_MACHINE_IS_EMPTY) {
            this.updateLocked();
        }
        if (result == ExternalPatternSink.Result.ACCEPTED
            && action == Action.EXECUTE
            && (this.lockMode == LockMode.LOCK_UNTIL_REDSTONE_PULSE_RECEIVED
            || this.lockMode == LockMode.LOCK_UNTIL_ALL_OUTPUTS_ARE_RECEIVED)) {
            this.setLocked(true);
        }
    }

    @Override
    public void receivedExternalIteration() {
        final TieredAutocrafterBlockEntity root = this.getChainingRoot();
        if (root != this) {
            root.receivedExternalIteration();
            return;
        }
        if (this.lockMode == LockMode.LOCK_UNTIL_ALL_OUTPUTS_ARE_RECEIVED && this.locked) {
            this.setLocked(false);
        }
    }

    public void completedOrCancelledTask(final Task task) {
        this.mainNetworkNode.removeRequestedResourceFilter(task);
    }
}
