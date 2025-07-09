package com.Infinity.Nexus.Greenhouse.utils;

import com.Infinity.Nexus.Greenhouse.block.ModBlocksGreenhouse;
import com.Infinity.Nexus.Greenhouse.block.custom.Greenhouse;
import com.Infinity.Nexus.Greenhouse.block.entity.GreenhouseBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

import java.util.*;

public class GetInteriorArea {
    public static int MAX_AIR_BLOCKS = 1500;
    private static final int MAX_SEARCH_RADIUS = 35;

    public static class InteriorAreaResult {
        public final boolean isSealed;
        public final boolean hasVillager;
        public final Set<BlockPos> interiorBlocks;
        public final List<BlockPos> plants;
        public final Set<BlockPos> farmlands;

        public InteriorAreaResult(boolean isSealed, boolean hasVillager, Set<BlockPos> interiorBlocks, List<BlockPos> plants, Set<BlockPos> farmlands) {
            this.isSealed = isSealed;
            this.hasVillager = hasVillager;
            this.interiorBlocks = interiorBlocks;
            this.plants = plants;
            this.farmlands = farmlands;
        }
    }

    public static InteriorAreaResult computeInteriorArea(Level level, BlockPos start, int maxBlocks, boolean self) {
        MAX_AIR_BLOCKS = maxBlocks;
        boolean hasVillager = false;
        Set<BlockPos> visited = new HashSet<>();
        Set<BlockPos> farmlands = new HashSet<>();
        Queue<BlockPos> queue = new LinkedList<>();
        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            BlockPos current = queue.poll();

            for (Direction dir : Direction.values()) {
                BlockPos neighbor = current.relative(dir);

                if (visited.contains(neighbor)) continue;
                if (!level.isLoaded(neighbor)) return new InteriorAreaResult(false, hasVillager, visited, new ArrayList<>(), farmlands);

                int distance = Math.max(Math.max(Math.abs(start.getX() - neighbor.getX()),
                                Math.abs(start.getY() - neighbor.getY())),
                        Math.abs(start.getZ() - neighbor.getZ()));
                if (distance > MAX_SEARCH_RADIUS) continue;

                BlockState state = level.getBlockState(neighbor);
                if (isInteriorBlock(state)) {
                    queue.add(neighbor);
                    visited.add(neighbor);
                    if (visited.size() > MAX_AIR_BLOCKS)
                        return new InteriorAreaResult(false, hasVillager, visited, new ArrayList<>(), farmlands);
                }else if(state.getBlock() instanceof FarmBlock
                        || state.getBlock() instanceof SoulSandBlock
                        || state.is(Blocks.DIRT)
                        || state.is(Blocks.GRASS_BLOCK)){
                    farmlands.add(neighbor);
                }
            }
        }

        AABB bounds = getBoundingBox(farmlands, visited);
        List<ItemEntity> items = new ArrayList<>();
        boolean isGreenhouse = level.getBlockEntity(start) instanceof GreenhouseBlockEntity;
        hasVillager = !level.getEntitiesOfClass(Entity.class, bounds, e -> {
            if(e instanceof Villager villager){
                BlockPos villagerBlockPos = e.blockPosition();
                return  (farmlands.contains(villagerBlockPos)) && villager.getVillagerData().getProfession() == VillagerProfession.FARMER;
            }
            if(!(e instanceof ItemEntity item && isGreenhouse && self)){
                return false;
            }
            if(item.hasPickUpDelay()){
                return false;
            }
            item.setPickUpDelay(10);
            items.add(item);
            return false;
        }).isEmpty();

        if(level.getBlockEntity(start) instanceof GreenhouseBlockEntity greenhouse && self){
            greenhouse.collectItems(items);
        }

        List<BlockPos> plants = visited.stream().filter(plant ->
                !(level.getBlockState(plant).isAir()
                        && level.getBlockState(plant).is(Blocks.WATER)
                        && level.getBlockState(plant).is(ModBlocksGreenhouse.GREENHOUSE)
                )
        ).toList();

        return new InteriorAreaResult(true, hasVillager, visited, plants, farmlands);
    }


    private static AABB getBoundingBox(Set<BlockPos> positions1, Set<BlockPos> positions2) {
        positions1.addAll(positions2);
        if (positions1.isEmpty()) {
            return new AABB(0, 0, 0, 0, 0, 0);
        }

        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int minZ = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;
        int maxZ = Integer.MIN_VALUE;

        for (BlockPos pos : positions1) {
            minX = Math.min(minX, pos.getX());
            minY = Math.min(minY, pos.getY());
            minZ = Math.min(minZ, pos.getZ());
            maxX = Math.max(maxX, pos.getX());
            maxY = Math.max(maxY, pos.getY());
            maxZ = Math.max(maxZ, pos.getZ());
        }

        return new AABB(
                minX-1, minY - 5, minZ-1,
                maxX+1, maxY + 5, maxZ+1
        );
    }
    public static int getArea(Level level, BlockPos start, int maxBlocks) {
        InteriorAreaResult result = computeInteriorArea(level, start, maxBlocks, false);
        return result.isSealed ? result.interiorBlocks.size() : 0;
    }
    private static boolean isInteriorBlock(BlockState state) {
        Block block = state.getBlock();
        return state.isAir()
                || block instanceof CactusBlock
                || block instanceof SugarCaneBlock
                || block instanceof BushBlock
                || block instanceof VineBlock
                || block instanceof CoralPlantBlock
                || block instanceof BeehiveBlock
                || (block instanceof BonemealableBlock && block != Blocks.GRASS_BLOCK)
                || block.equals(Blocks.MELON)
                || block.equals(Blocks.PUMPKIN)
                || block.equals(Blocks.CAVE_VINES)
                || block instanceof LiquidBlock
                || block == ModBlocksGreenhouse.GREENHOUSE.get();
    }
}