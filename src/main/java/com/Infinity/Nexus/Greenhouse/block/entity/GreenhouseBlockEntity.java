package com.Infinity.Nexus.Greenhouse.block.entity;

import com.Infinity.Nexus.Core.block.entity.common.SetMachineLevel;
import com.Infinity.Nexus.Core.block.entity.common.SetUpgradeLevel;
import com.Infinity.Nexus.Core.component.CoreDataComponents;
import com.Infinity.Nexus.Core.fakePlayer.IFFakePlayer;
import com.Infinity.Nexus.Core.itemStackHandler.RestrictedItemStackHandler;
import com.Infinity.Nexus.Core.items.ModItems;
import com.Infinity.Nexus.Core.utils.*;
import com.Infinity.Nexus.Greenhouse.block.custom.Greenhouse;
import com.Infinity.Nexus.Greenhouse.config.Config;
import com.Infinity.Nexus.Greenhouse.item.ModItemsGreenhouse;
import com.Infinity.Nexus.Greenhouse.recipes.GreenhouseRecipe;
import com.Infinity.Nexus.Greenhouse.recipes.GreenhouseRecipeInput;
import com.Infinity.Nexus.Greenhouse.recipes.ModRecipes;
import com.Infinity.Nexus.Greenhouse.screen.greenhouse.GreenhouseMenu;
import com.Infinity.Nexus.Greenhouse.utils.GetInteriorArea;
import com.Infinity.Nexus.Greenhouse.utils.ModUtilsGreenhouse;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.RandomSource;
import net.minecraft.world.*;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.AirItem;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.templates.FluidTank;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.List;

public class GreenhouseBlockEntity extends BlockEntity implements MenuProvider {
    // Constantes para slots
    private static final int[] OUTPUT_SLOTS = {0, 1, 2, 3, 4, 5, 6, 7, 8};
    private static final int[] UPGRADE_SLOTS = {9, 10, 11, 12};
    private static final int COMPONENT_SLOT = 13;
    private static final int LINK_SLOT = 15;
    private static final int HOE_SLOT = 14;
    private static final int FUEL_SLOT = 16;
    private static final int FERTILIZER_SLOT = 17;
    private static final int SEED_SLOT = 18;

    // Constantes para índices de dados
    private static final int DATA_PROGRESS = 0;
    private static final int DATA_MAX_PROGRESS = 1;
    private static final int DATA_VERIFY = 2;
    private static final int DATA_MAX_VERIFY = 3;
    private static final int DATA_STRUCTURE = 4;
    private static final int DATA_REDSTONE = 5;
    private static final int DATA_CRAFTING = 6;
    private static final int DATA_SLOT_FREE = 7;
    private static final int DATA_COMPONENT = 8;
    private static final int DATA_ENERGY = 9;
    private static final int DATA_RECIPE = 10;
    private static final int DATA_LINK_X = 11;
    private static final int DATA_LINK_Y = 12;
    private static final int DATA_LINK_Z = 13;
    private static final int DATA_LINK_FACE = 14;
    private static final int DATA_SIZE = 15;
    private static final int DATA_LIGHT = 16;
    private static final int DATA_PLANTS = 17;
    private static final int DATA_VILLAGER = 18;
    private String owner;
    private int delay = 1200;

    // Configurações
    private static final int ENERGY_CAPACITY = Config.greenhouse_energy_capacity;
    private static final int ENERGY_TRANSFER = Config.greenhouse_energy_transfer;
    private static final int FLUID_STORAGE_CAPACITY = Config.greenhouse_fluid_storage_capacity;
    private static final int MAX_CROPS_BASE = 1;
    private static final int PROGRESS_DEFAULT = 120;
    private static final int LIT_OFFSET = 9;


    // Sistemas internos
    private final RestrictedItemStackHandler itemHandler = createItemHandler();
    private final ModEnergyStorage energyStorage = createEnergyStorage();
    private final FluidTank fluidStorage = createFluidTank();
    private final GreenhouseContainerData containerData = new GreenhouseContainerData();

    public GreenhouseBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.GREENHOUSE_BE.get(), pPos, pBlockState);
    }

    // ============ Criação de Componentes ============

    private RestrictedItemStackHandler createItemHandler() {
        return new RestrictedItemStackHandler(19) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
                if (!level.isClientSide()) {
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                }
                if(slot == LINK_SLOT){
                    setLinked();
                }
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return switch (slot) {
                    case 0, 1, 2, 3, 4, 5, 6, 7, 8 -> true;
                    case 9, 10, 11, 12 -> ModUtils.isUpgrade(stack);
                    case 13 -> ModUtils.isComponent(stack);
                    case 14 -> stack.is(ItemTags.HOES);
                    case 15 -> stack.is(ModItems.LINKING_TOOL.get().asItem());
                    case 16 -> stack.getBurnTime(RecipeType.SMELTING) > 0;
                    case 17 -> stack.is(Tags.Items.FERTILIZERS);
                    case 18 -> stack.is(Tags.Items.SEEDS) || stack.is(Tags.Items.CROPS);
                    default -> super.isItemValid(slot, stack);
                };
            }

            @Override
            public @NotNull ItemStack extractItem(int slot, int amount, boolean simulate, boolean fromAutomation) {
                if (slot <= 8) {
                    return super.extractItem(slot, amount, simulate, false);
                }
                return super.extractItem(slot, amount, simulate, fromAutomation);
            }

            @Override
            public ItemStack insertItem(int slot, ItemStack stack, boolean simulate) {
                if (slot < 0) {
                    return stack;
                }
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    private ModEnergyStorage createEnergyStorage() {
        return new ModEnergyStorage(ENERGY_CAPACITY, ENERGY_TRANSFER) {
            @Override
            public void onEnergyChanged() {
                setChanged();
                if (level != null) {
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 4);
                }
            }
        };
    }

    private FluidTank createFluidTank() {
        return new FluidTank(FLUID_STORAGE_CAPACITY) {
            @Override
            public void onContentsChanged() {
                setChanged();
                if (level != null && !level.isClientSide()) {
                    level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
                }
            }

            @Override
            public boolean isFluidValid(FluidStack stack) {
                return true;
            }
        };
    }

    // ============ Lógica Principal ============

    public void tick(Level level, BlockPos pos, BlockState state) {
        if (level.isClientSide) {
            return;
        }
        FluidUtils.fillFluidToTank(fluidStorage, new FluidStack(Fluids.WATER, 10));

        int machineLevel = Math.max(getMachineLevel() - 1, 0);

        //Process
        if (!hasProgressFinished()) {
            containerData.set(DATA_CRAFTING, 0);
            increaseCraftingProgress();
            return;
        }
        resetProgress();

        //Redstone
        if (isRedstonePowered(pos)) {
            containerData.set(DATA_REDSTONE, 1);
            updateBlockState(state, pos, machineLevel);
            return;
        }
        containerData.set(DATA_REDSTONE, 0);

        //Componnet
        if (!hasComponent()) {
            containerData.set(DATA_COMPONENT, 0);
            updateBlockState(state, pos, machineLevel);
            return;
        }
        containerData.set(DATA_COMPONENT, 1);

        //Energy
        if (!hasEnoughEnergy(machineLevel)) {
            verifySolidFuel();
            containerData.set(DATA_ENERGY, 0);
            updateBlockState(state, pos, machineLevel);
            return;
        }
        containerData.set(DATA_ENERGY, 1);

        //Slot
        if (!hasEmptySlot()) {
            containerData.set(DATA_SLOT_FREE, 0);
            updateBlockState(state, pos, machineLevel);
            sendItems(level);
            this.delay = ModUtilsGreenhouse.notifyOwner(pos, level, owner, this.delay);
            return;
        }
        containerData.set(DATA_SLOT_FREE, 1);

        //Plants
        GetInteriorArea.InteriorAreaResult result = GetInteriorArea.computeInteriorArea(level, worldPosition, ModUtilsGreenhouse.getAreaByTier(machineLevel));
        List<BlockPos> plants = result.interiorBlocks.stream().filter(plant -> level.getBlockState(plant) != Blocks.AIR.defaultBlockState()).toList();
        containerData.set(DATA_PLANTS, plants.size() - 1);
        containerData.set(DATA_SIZE, result.interiorBlocks.size());
        containerData.set(DATA_STRUCTURE, result.isSealed ? 1 : 0);

        if (containerData.get(DATA_PLANTS) < 1) {
            updateBlockState(state, pos, machineLevel);
            return;
        }
        humidify(plants);

        //Light
        updateLightLevel(pos);
        if(containerData.get(DATA_LIGHT) < 4){
            updateBlockState(state, pos, machineLevel);
            return;
        }

        //Villager Farmer
        if(!result.hasVillager){
            containerData.set(DATA_VILLAGER, 0);
            updateBlockState(state, pos, machineLevel);
            return;
        }
        containerData.set(DATA_VILLAGER, 1);
        int max_progress = ProgressUtils.setMaxProgress(machineLevel, PROGRESS_DEFAULT, itemHandler, UPGRADE_SLOTS, Config.greenhouse_minimum_tick);
        containerData.set(DATA_CRAFTING, 1);
        containerData.set(DATA_MAX_PROGRESS, max_progress);

        if (!plants.isEmpty()) {
            containerData.set(DATA_RECIPE, 1);
            craftItem(result, plants);
            updateBlockState(state, pos, machineLevel + LIT_OFFSET);
            setChanged(level, pos, state);
            return;
        }
        containerData.set(DATA_RECIPE, 0);
    }

    private Optional<RecipeHolder<GreenhouseRecipe>> getCurrentRecipe(BlockState state, Level level) {
        ItemStack stack = this.itemHandler.getStackInSlot(COMPONENT_SLOT);
        String plantID = BuiltInRegistries.BLOCK.getKey(state.getBlock()).toString();
        return level.getRecipeManager().getRecipeFor(ModRecipes.GREENHOUSE_RECIPE_TYPE.get(), new GreenhouseRecipeInput(stack, plantID), level);
    }

    private void sendItems(Level level) {
        BlockPos pos = getLinkedPos();
        if(pos == null){
            ModUtils.ejectItemsWhePusher(worldPosition, UPGRADE_SLOTS, OUTPUT_SLOTS, itemHandler, level);
            return;
        }
        IItemHandler beItemHandler = ItemStackHandlerUtils.getBlockCapabilityItemHandler(level, pos, Direction.UP);
        if(beItemHandler == null) return;
        for (int slot : OUTPUT_SLOTS) {
            for (int i = 0; i < beItemHandler.getSlots(); i++) {
                if(ModUtils.canPlaceItemInContainer(itemHandler.getStackInSlot(slot), i, beItemHandler)) {
                    beItemHandler.insertItem(i, itemHandler.getStackInSlot(slot), false);
                    ItemStackHandlerUtils.extractItem(slot, itemHandler.getStackInSlot(slot).getCount(), false, itemHandler);
                }
            }
        }
    }

    // ============ Operações de Agricultura ============

    private void humidify(List<BlockPos> farmBlocks) {
        if (farmBlocks.isEmpty()) return;
        for (BlockPos pos : farmBlocks) {
            BlockState state = level.getBlockState(pos.below());
            if ((state.is(Blocks.DIRT) || state.is(Blocks.GRASS_BLOCK)) && itemHandler.getStackInSlot(HOE_SLOT).is(ItemTags.HOES)) {
                level.setBlock(pos.below(), Blocks.FARMLAND.defaultBlockState(), 3);
                continue;
            }

            if (fluidStorage.getFluidAmount() <= 0 && !ModUtilsGreenhouse.hasIrrigate(itemHandler, UPGRADE_SLOTS)) continue;
            if (state.hasProperty(FarmBlock.MOISTURE)) {
                int moisture = state.getValue(FarmBlock.MOISTURE);
                if (moisture < FarmBlock.MAX_MOISTURE) {
                    level.setBlock(pos.below(), state.setValue(FarmBlock.MOISTURE, moisture + 1), 3);
                    FluidUtils.drainFluidFromTank(fluidStorage, containerData.get(DATA_PLANTS));
                }
            }
        }
    }

    private void harvestCrops(GetInteriorArea.InteriorAreaResult result) {
        for (BlockPos pos : result.interiorBlocks) {
            BlockState state = level.getBlockState(pos);

            if(state.isAir()){
                continue;
            }

            Optional<RecipeHolder<GreenhouseRecipe>> recipe = getCurrentRecipe(state, level);
            if (recipe.isEmpty()) {
                continue;
            }

            int offset = recipe.get().value().size();
            state = level.getBlockState(pos.above(offset));
            Block block = state.getBlock();
            String plantId = recipe.get().value().getPlant();
            Block recipeBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(plantId));

            if (block != recipeBlock) {
                continue;
            }

            String requiredProperty = recipe.get().value().getProperties("property");
            String requiredReplace = recipe.get().value().getProperties("replace");
            String[] parts = requiredProperty.split("=");
            if (!requiredProperty.equals("none") && (requiredProperty.isEmpty() || parts.length != 2)) {
                continue;
            }

            Property<?> properties = state.getProperties().stream().filter(p -> p.getName().equals(parts[0])).findFirst().orElse(null);
            if(!requiredProperty.equals("none")) {
                if ((properties instanceof IntegerProperty integerProperty)) {
                    if (state.getValue(integerProperty) < Integer.parseInt(parts[1])) {
                        continue;
                    }
                }
            }

            List<ItemStack> drops = ModUtilsGreenhouse.getDrop(state, level, pos.above(offset), itemHandler.getStackInSlot(HOE_SLOT));
            processHarvestDrops(drops, recipe);
            extractEnergy(recipe);

            BlockState newState = state;
            for (Property<?> property : state.getProperties()) {
                if(property.getName().equals(parts[0])){
                    if (property instanceof IntegerProperty intProp) {
                        newState = state.setValue(intProp, 0);
                        break;
                    } else if (property instanceof BooleanProperty boolProp) {
                        newState = state.setValue(boolProp, false);
                        break;
                    }
                }
            }
            if(!requiredReplace.equals("none")){
                try {
                    newState = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(requiredReplace)).defaultBlockState();
                } catch (Exception e) {
                    continue;
                }
            }

            level.setBlock(pos.above(offset), newState, 3);
        }
    }

    private void processHarvestDrops(List<ItemStack> drops, Optional<RecipeHolder<GreenhouseRecipe>> recipe) {
        RandomSource chance = RandomSource.create();
        float rd = chance.nextFloat();
        float primaryChance = recipe.get().value().outputs().get(0).chance();
        if (drops == null || drops.isEmpty()) {

            if (rd <= primaryChance) {
                selfInsertItems(recipe.get().value().getResultItem(level.registryAccess()));
            }

            List<ItemStack> secondary = recipe.get().value().getRandomResults(rd);
            for (ItemStack secondaryStack : secondary) {
                selfInsertItems(secondaryStack);
            }
            return;
        }

        boolean foundOutputInDrops = false;

        for (ItemStack stack : drops) {
            if (!Config.greenhouse_harvest_seeds && (stack.is(Tags.Items.SEEDS) || stack.is(ItemTags.SAPLINGS))) {
                continue;
            }
            ItemStack output = recipe.get().value().getResultItem(level.registryAccess());
            if (output.is(stack.getItem())) {
                foundOutputInDrops = true;
            }

            if(rd <= primaryChance){
                selfInsertItems(stack);
            }

            List<ItemStack> secondary = recipe.get().value().getRandomResults(rd);
            for(ItemStack secondaryStack : secondary){
                selfInsertItems(secondaryStack);
            }
        }

        if (!foundOutputInDrops) {
            if (rd <= primaryChance) {
                selfInsertItems(recipe.get().value().getResultItem(level.registryAccess()));
            }
        }
    }

    private void selfInsertItems(ItemStack stack){
        verifySolidFuel();
        for(int slot : OUTPUT_SLOTS){
            if(ItemStackHandlerUtils.canInsertItemAndAmountIntoOutputSlot(stack.getItem(), stack.getCount(), slot, itemHandler)){
                ItemStackHandlerUtils.insertItem(slot, stack, false, itemHandler);
                if(slot == OUTPUT_SLOTS.length -1){
                    sendItems(level);
                }
                break;
            }
        }
    }

    private void processCrops(GetInteriorArea.InteriorAreaResult result, List<BlockPos> farmBlocks) {
        List<BlockPos> workingBlocks = new ArrayList<>(farmBlocks);
        harvestCrops(result);
        plantSeeds(result.interiorBlocks.stream().toList());
        RandomSource random = level.getRandom();
        int cropsProcessed = 0;
        int maxCropsProcessed = calculateMaxCropsToProcess();
        int attempts = 0;

        while (cropsProcessed < maxCropsProcessed && attempts < workingBlocks.size()) {
            BlockPos pos = getRandomFarmBlock(workingBlocks, random);
            BlockState state = level.getBlockState(pos);
            Block block = state.getBlock();

            if (itemHandler.getStackInSlot(FERTILIZER_SLOT).getCount() > 0 && hasFertilizerUpgrade()) {
                String blockID = BuiltInRegistries.BLOCK.getKey(block).toString();
                if(block.asItem().getDefaultInstance().is(ItemTags.SAPLINGS) || Config.list_of_non_fertilizable_blocks.contains(blockID)){
                    continue;
                }
                if (block instanceof BonemealableBlock growable && growable.isValidBonemealTarget(level, pos, state)) {
                    ItemStackHandlerUtils.extractItem(FERTILIZER_SLOT, 1, false, itemHandler);
                    growable.performBonemeal((ServerLevel) level, random, pos, state);
                    renderParticles(pos.below());
                    cropsProcessed++;
                }
            }
            attempts++;
        }
    }

    private void plantSeeds(List<BlockPos> farmBlocks) {
        IFFakePlayer fakePlayer = GetFakePlayer.get((ServerLevel) level);
        if(!ModUtilsGreenhouse.hasPlanter(itemHandler, UPGRADE_SLOTS)){
            return;
        }
        for (BlockPos pos : farmBlocks) {
            BlockState state = level.getBlockState(pos);
            if(state.isAir()){
                if(itemHandler.getStackInSlot(SEED_SLOT).isEmpty()){
                    break;
                }
                ItemStack seed = itemHandler.getStackInSlot(SEED_SLOT);
                fakePlayer.setItemInHand(InteractionHand.MAIN_HAND, seed.copy());
                fakePlayer.gameMode.useItemOn(fakePlayer, level, seed, InteractionHand.MAIN_HAND, new BlockHitResult(Vec3.atCenterOf(pos), Direction.UP, pos, false));
                if(!level.getBlockState(pos).isAir()) {
                    ItemStackHandlerUtils.extractItem(SEED_SLOT, 1, false, itemHandler);
                }
            }
        }
    }

    private boolean hasFertilizerUpgrade() {
        for (int slot : UPGRADE_SLOTS) {
            if (itemHandler.getStackInSlot(slot).is(ModItemsGreenhouse.FERTILIZER_UPGRADE.get())) {
                return true;
            }
        }
        return false;
    }

    private int calculateMaxCropsToProcess() {
        int maxCrops = MAX_CROPS_BASE + getMachineLevel() * 2;
        return maxCrops;
    }

    private BlockPos getRandomFarmBlock(List<BlockPos> farmBlocks, RandomSource random) {
        if (farmBlocks.isEmpty()) {
            return BlockPos.ZERO;
        }
        List<BlockPos> mutableBlocks = new ArrayList<>(farmBlocks);
        int index = random.nextInt(mutableBlocks.size());
        return mutableBlocks.get(index);
    }

    // ============ Operações de Energia ============

    private boolean hasEnoughEnergy(int machineLevel) {
        int baseEnergyCost = calculateBaseEnergyCost(machineLevel);
        int totalEnergyRequired = calculateTotalEnergyRequirement(machineLevel, baseEnergyCost);
        return energyStorage.getEnergyStored() >= totalEnergyRequired;
    }

    private int calculateBaseEnergyCost(int machineLevel) {
        return (machineLevel + 1) * Config.greenhouse_energy_per_operation_base;
    }

    private int calculateTotalEnergyRequirement(int machineLevel, int baseEnergyCost) {
        int speedFactor = Math.max(ModUtils.getSpeed(itemHandler, UPGRADE_SLOTS), 2) + machineLevel;
        int strengthBonus = ModUtils.getStrength(itemHandler, UPGRADE_SLOTS) * 10;
        return (baseEnergyCost * speedFactor) + strengthBonus;
    }

    private void extractEnergy(Optional<RecipeHolder<GreenhouseRecipe>> recipe) {
        EnergyUtils.extractEnergyFromRecipe(
                energyStorage,
                recipe.get().value().getEnergy(),
                getMachineLevel() + 1,
                containerData.get(DATA_MAX_PROGRESS),
                itemHandler,
                UPGRADE_SLOTS
        );
    }

    private void verifySolidFuel() {
        ItemStack fuelStack = itemHandler.getStackInSlot(FUEL_SLOT);
        int burnTime = fuelStack.getBurnTime(RecipeType.SMELTING) * Config.greenhouse_fuel_multiplier;

        if (burnTime > 1) {
            while (fuelStack.getCount() > 0 && energyStorage.getEnergyStored() + burnTime < energyStorage.getMaxEnergyStored()) {
                energyStorage.receiveEnergy(burnTime, false);
                ItemStackHandlerUtils.extractItem(FUEL_SLOT, 1, false, itemHandler);
            }
        }
    }

    // ============ Operações de Progresso ============

    private boolean hasProgressFinished() {
        return containerData.get(DATA_PROGRESS) >= containerData.get(DATA_MAX_PROGRESS);
    }

    private void increaseCraftingProgress() {
        containerData.increment(DATA_PROGRESS);
    }

    private void resetProgress() {
        containerData.set(DATA_PROGRESS, 0);
    }
    // ============ Operações de Componentes ============

    private void craftItem(GetInteriorArea.InteriorAreaResult result, List<BlockPos> farmBlocks) {
        ItemStack component = itemHandler.getStackInSlot(COMPONENT_SLOT);
        processCrops(result, farmBlocks);

        if (Config.greenhouse_cost_component_durability) {
            ModUtils.useComponent(component, level, getBlockPos());
        }

        if (ModUtils.getMuffler(itemHandler, UPGRADE_SLOTS) <= 0) {
            SoundUtils.playSound(level, getBlockPos(), SoundSource.BLOCKS, SoundEvents.COMPOSTER_READY,0.2f, 1.0f);
        }
    }

    private int getMachineLevel() {
        return ModUtils.getComponentLevel(itemHandler.getStackInSlot(COMPONENT_SLOT));
    }

    // ============ Operações de Estado ============

    private void updateBlockState(BlockState state, BlockPos pos, int litValue) {
        if (level != null && state.getValue(Greenhouse.LIT) != litValue) {
            level.setBlock(pos, state.setValue(Greenhouse.LIT, litValue), 3);
        }
    }

    private void updateLightLevel(BlockPos pos) {
        if (level != null) {
            containerData.set(DATA_LIGHT, level.getBrightness(LightLayer.BLOCK, pos));
        }
    }

    private boolean isRedstonePowered(BlockPos pos) {
        return level != null && level.hasNeighborSignal(pos);
    }

    private boolean hasComponent() {
        return ModUtils.isComponent(itemHandler.getStackInSlot(COMPONENT_SLOT));
    }

    private boolean hasEmptySlot() {
        for (int slot = 0; slot < OUTPUT_SLOTS.length - 1; slot++) {
            if (itemHandler.getStackInSlot(slot).isEmpty()) {
                return true;
            }
        }
        return false;
    }

    // ============ Operações de Renderização ============

    private void renderParticles(BlockPos pos) {
        if (level instanceof ServerLevel serverLevel) {
            double x = pos.getX() + 0.5;
            double y = pos.getY() + 1;
            double z = pos.getZ() + 0.5;
            serverLevel.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 5, 0, 0.1, 0, 0.1D);
        }
    }

    // ============ Operações de Inventário ============

    public void drops() {
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for (int i = 0; i < itemHandler.getSlots() - 1; i++) {
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(level, worldPosition, inventory);
    }

    // ============ Getters Públicos ============

    public IItemHandler getItemHandler(Direction direction) {
        return itemHandler;
    }
    public FluidTank getFluidHandler(Direction direction) {
        return fluidStorage;
    }
    public IEnergyStorage getEnergyStorage(@Nullable Direction direction) {
        return energyStorage;
    }
    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }
    public static long getInputFluidCapacity() {
        return FLUID_STORAGE_CAPACITY;
    }
    public FluidStack getFluidInInputTank() {
        return fluidStorage.getFluidInTank(0);
    }

    public String getHasLink() {
        if (containerData.get(DATA_LINK_X) != 0 ||
                containerData.get(DATA_LINK_Y) != 0 ||
                containerData.get(DATA_LINK_Z) != 0) {

            return Component.translatable("gui.infinity_nexus_greenhouse.link_on").getString() +
                    " X: " + containerData.get(DATA_LINK_X) +
                    ", Y: " + containerData.get(DATA_LINK_Y) +
                    ", Z: " + containerData.get(DATA_LINK_Z);
        }
        return Component.translatable("gui.infinity_nexus_greenhouse.link_off").getString();
    }

    public ItemStack getLikedBlock() {
        if (level == null) return ItemStack.EMPTY;
        BlockPos linkedPos = new BlockPos(
                containerData.get(DATA_LINK_X),
                containerData.get(DATA_LINK_Y),
                containerData.get(DATA_LINK_Z)
        );
        return new ItemStack(level.getBlockState(linkedPos).getBlock().asItem());
    }

    public BlockPos getLinkedPos() {
        ItemStack linkingTool = itemHandler.getStackInSlot(LINK_SLOT);
        if (linkingTool.isEmpty()) return null;
        if(linkingTool.has(CoreDataComponents.LINKINGTOOL_COORDS)) {
            if(getLikedBlock().isEmpty()){
                return null;
            }
            return linkingTool.get(CoreDataComponents.LINKINGTOOL_COORDS);
        }
        return null;
    }

    public void setLinked(){
        if(itemHandler.getStackInSlot(LINK_SLOT).has(CoreDataComponents.LINKINGTOOL_COORDS)){
            BlockPos cords = itemHandler.getStackInSlot(LINK_SLOT).get(CoreDataComponents.LINKINGTOOL_COORDS);
            containerData.set(DATA_LINK_X, cords.getX());
            containerData.set(DATA_LINK_Y, cords.getY());
            containerData.set(DATA_LINK_Z, cords.getZ());
        }else{
            containerData.set(DATA_LINK_X, 0);
            containerData.set(DATA_LINK_Y, 0);
            containerData.set(DATA_LINK_Z, 0);
        }
    }

    // ============ MenuProvider Implementation ============

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.infinity_nexus_greenhouse.greenhouse")
                .append(" LV " + getMachineLevel());
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new GreenhouseMenu(containerId, playerInventory, this, containerData, itemHandler);
    }

    // ============ NBT Operations ============

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);

        tag.put("inventory", itemHandler.serializeNBT(registries));
        tag.putInt("greenhouse.progress", containerData.get(DATA_PROGRESS));
        tag.putInt("greenhouse.energy", energyStorage.getEnergyStored());

        tag.putInt("greenhouse.hasStructure", containerData.get(DATA_STRUCTURE));

        tag.putInt("greenhouse.hasRedstoneSignal", containerData.get(DATA_REDSTONE));
        tag.putInt("greenhouse.stillCrafting", containerData.get(DATA_CRAFTING));
        tag.putInt("greenhouse.hasSlotFree", containerData.get(DATA_SLOT_FREE));
        tag.putInt("greenhouse.hasComponent", containerData.get(DATA_COMPONENT));
        tag.putInt("greenhouse.hasEnoughEnergy", containerData.get(DATA_ENERGY));
        tag.putInt("greenhouse.hasRecipe", containerData.get(DATA_RECIPE));

        tag.putInt("greenhouse.linkx", containerData.get(DATA_LINK_X));
        tag.putInt("greenhouse.linky", containerData.get(DATA_LINK_Y));
        tag.putInt("greenhouse.linkz", containerData.get(DATA_LINK_Z));
        tag.putInt("greenhouse.linkFace", containerData.get(DATA_LINK_FACE));

        tag.putInt("greenhouse.size", containerData.get(DATA_SIZE));
        tag.putInt("greenhouse.light", containerData.get(DATA_LIGHT));
        tag.putInt("greenhouse.plants", containerData.get(DATA_PLANTS));

        fluidStorage.writeToNBT(registries, tag);

        tag.putString("owner", owner == null ? "" : owner);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);

        itemHandler.deserializeNBT(registries, tag.getCompound("inventory"));
        containerData.set(DATA_PROGRESS, tag.getInt("greenhouse.progress"));
        energyStorage.setEnergy(tag.getInt("greenhouse.energy"));

        containerData.set(DATA_STRUCTURE, tag.getInt("greenhouse.hasStructure"));

        containerData.set(DATA_REDSTONE, tag.getInt("greenhouse.hasRedstoneSignal"));
        containerData.set(DATA_CRAFTING, tag.getInt("greenhouse.stillCrafting"));
        containerData.set(DATA_SLOT_FREE, tag.getInt("greenhouse.hasSlotFree"));
        containerData.set(DATA_COMPONENT, tag.getInt("greenhouse.hasComponent"));
        containerData.set(DATA_ENERGY, tag.getInt("greenhouse.hasEnoughEnergy"));
        containerData.set(DATA_RECIPE, tag.getInt("greenhouse.hasRecipe"));

        containerData.set(DATA_LINK_X, tag.getInt("greenhouse.linkx"));
        containerData.set(DATA_LINK_Y, tag.getInt("greenhouse.linky"));
        containerData.set(DATA_LINK_Z, tag.getInt("greenhouse.linkz"));
        containerData.set(DATA_LINK_FACE, tag.getInt("greenhouse.linkFace"));

        containerData.set(DATA_SIZE, tag.getInt("greenhouse.size"));
        containerData.set(DATA_LIGHT, tag.getInt("greenhouse.light"));
        containerData.set(DATA_PLANTS, tag.getInt("greenhouse.plants"));

        fluidStorage.readFromNBT(registries, tag);

        if (tag.getString("owner").equals("")) {
            owner = null;
        } else {
            owner = tag.getString("owner");
        }
    }

    // ============ Network Sync ============

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider lookupProvider) {
        super.onDataPacket(net, pkt, lookupProvider);
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        return saveWithFullMetadata(registries);
    }

    // ============ Upgrade Operations ============

    public void setMachineLevel(ItemStack itemStack, Player player) {
        SetMachineLevel.setMachineLevel(itemStack, player, this, COMPONENT_SLOT, itemHandler);
        resetVerify();
    }

    public void setUpgradeLevel(ItemStack itemStack, Player player) {
        SetUpgradeLevel.setUpgradeLevel(itemStack, player, this, UPGRADE_SLOTS, itemHandler);
        setChanged();
    }

    public void resetVerify() {
        containerData.set(DATA_VERIFY, containerData.get(DATA_MAX_VERIFY));
    }

    public void showArea(ServerPlayer player) {
        if(level == null || level.isClientSide()) {
            return;
        }
        if(level instanceof ServerLevel serverLevel){
            int tier = this.getMachineLevel() -1;
            GetInteriorArea.InteriorAreaResult result = GetInteriorArea.computeInteriorArea(level, worldPosition, ModUtilsGreenhouse.getAreaByTier(tier));
            Set<BlockPos> area = result.interiorBlocks;
            boolean isSealed = result.isSealed;

            System.out.println(isSealed);
            System.out.println(area.size());

            if (!isSealed || area == null) {
                return;
            }
            ModUtilsGreenhouse.showInfo(serverLevel, worldPosition, player, ModUtilsGreenhouse.getAreaByTier(tier));
        }
    }

    // ============ Inner Classes ============

    private class GreenhouseContainerData implements ContainerData {
        private final int[] data = new int[19];

        @Override
        public int get(int index) {
            return index >= 0 && index < data.length ? data[index] : 0;
        }

        @Override
        public void set(int index, int value) {
            if (index >= 0 && index < data.length) {
                data[index] = value;
            }
        }

        @Override
        public int getCount() {
            return data.length;
        }

        public void increment(int index) {
            if (index >= 0 && index < data.length) {
                data[index]++;
            }
        }
    }
    public void setOwner(Player player) {
        owner = player.getStringUUID();
        setChanged();
    }

    public String setOwnerName() {
        return ModUtilsGreenhouse.getOwnerName(this.level, owner).getName().getString();
    }

    //---------------------------------------Jade----------------------------------------//
    public String getOwner() {
        if (owner == null) {
            return "§4❎";
        }
        Player player = level.getPlayerByUUID(UUID.fromString(owner));
        Component displayName = player == null ? Component.empty() : player.getDisplayName();

        return "§e"+ displayName.getString();
    }

    public int getTier() {
        return getMachineLevel();
    }
}