package com.Infinity.Nexus.Greenhouse.utils;

import com.Infinity.Nexus.Core.fakePlayer.IFFakePlayer;
import com.Infinity.Nexus.Greenhouse.config.Config;
import com.Infinity.Nexus.Greenhouse.item.ModItemsGreenhouse;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import java.util.*;
import java.util.stream.Collectors;

public class ModUtilsGreenhouse {

    private static IFFakePlayer player;

    public static List<ItemStack> getDrop(BlockState stack, Level level, BlockPos pos, ItemStack pickaxe) {
        if (player == null) {
            player = new IFFakePlayer((ServerLevel) level);
        }
        List<ItemStack> drops = new ArrayList<>(Block.getDrops(stack, (ServerLevel) level, pos, null, player, pickaxe));
        return drops;
    }
    public static boolean hasUpgrade(ItemStackHandler itemHandler, int[] upgradeSlots, Item upgrade) {
        for (int upgradeSlot : upgradeSlots) {
            ItemStack stack = itemHandler.getStackInSlot(upgradeSlot);
            if (stack.getItem() == upgrade) {
                return true;
            }
        }
        return false;
    }

    public static int notifyOwner(BlockPos pPos, Level level, String owner, int delay) {
        if (delay <= 0 && owner != null) {
            Player player = getOwnerName(level, owner);
            if (player != null) {
                int x = pPos.getX();
                int y = pPos.getY();
                int z = pPos.getZ();

                ResourceKey<Level> blockDimension = level.dimension();

                String dimensionName = blockDimension.location().toString();

                MutableComponent message = Component.translatable("chat.infinity_nexus_greenhouse.greenhouse_is_full")
                        .append(" [TP]")
                        .withStyle(style -> style
                                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,
                                        "/infinitynexusgreenhouse tp " + player.getName().getString() + " '" + dimensionName + "' " + (x + 0.5) + " " + (y + 1) + " " + (z + 0.5) + " dezanove")
                                )

                        );
                player.displayClientMessage(message, false);
                return delay = 1200;
            }
        } else {
            return delay = delay-1;
        }
        return delay;
    }

    public static Player getOwnerName(Level level, String owner){
        return level.getPlayerByUUID(UUID.fromString(owner));
    }

    public static void showInfo(ServerLevel serverLevel, BlockPos pPos, ServerPlayer player, int maxBlocks) {
        Set<BlockPos> area = GetInteriorArea.computeInteriorArea(serverLevel, pPos, maxBlocks, false).interiorBlocks;

        // Count blocks efficiently using merge
        Map<String, Integer> blockCounts = new HashMap<>();
        for (BlockPos pos : area) {
            // Visual particle effect
            serverLevel.sendParticles(
                    player,
                    ParticleTypes.END_ROD,
                    true,
                    pos.getX() + 0.5,
                    pos.getY() + 0.5,
                    pos.getZ() + 0.5,
                    1,
                    0, 0, 0,
                    0
            );

            // Count blocks
            String blockName = serverLevel.getBlockState(pos).getBlock().getName().getString();
            blockCounts.merge(blockName, 1, Integer::sum);
        }

        // Sort blocks by count (descending) and alphabetically
        List<Map.Entry<String, Integer>> sortedBlocks = blockCounts.entrySet()
                .stream()
                .sorted(
                        Map.Entry.<String, Integer>comparingByValue().reversed()
                                .thenComparing(Map.Entry.comparingByKey())
                )
                .collect(Collectors.toList());

        // Send sorted results to player
        if (sortedBlocks.isEmpty()) {
            player.sendSystemMessage(Component.literal("§cNo blocks found in the area!"));
        } else {
            player.sendSystemMessage(Component.literal("§6=== Greenhouse Block Count ==="));
            player.sendSystemMessage(Component.literal("§7- §eArea: §b" + area.size() +"/"+ maxBlocks));
            sortedBlocks.forEach(entry -> {
                player.sendSystemMessage(
                        Component.literal("§7- §e" + entry.getKey() + "§7: §b" + entry.getValue())
                );
            });
        }
    }

    public static int getAreaByTier(int tier) {
        List<Integer> tierArea = Config.list_of_max_greenhouse_interior_blocks;
        if(tier < 0 || tier >= tierArea.size()){
            return 0;
        }
        return tierArea.get(tier);
    }
}
