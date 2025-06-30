package com.Infinity.Nexus.Greenhouse.events;

import com.Infinity.Nexus.Greenhouse.InfinityNexusGreenhouse;
import com.Infinity.Nexus.Greenhouse.command.GreenhouseTP;
import com.Infinity.Nexus.Greenhouse.component.GreenhouseDataComponents;
import com.Infinity.Nexus.Greenhouse.config.Config;
import com.Infinity.Nexus.Greenhouse.utils.ModUtilsGreenhouse;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.server.command.ConfigCommand;


@EventBusSubscriber(modid = InfinityNexusGreenhouse.MOD_ID)
public class Listeners {
    @SubscribeEvent
    public static void onCommandRegister(RegisterCommandsEvent event) {
        new GreenhouseTP(event.getDispatcher());
        ConfigCommand.register(event.getDispatcher());
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        ItemStack stack = player.getMainHandItem();
        if (!stack.is(Items.GOLDEN_HOE) || !stack.has(GreenhouseDataComponents.DEBUG_HOE)) {
            return;
        }
        if (!(event.getLevel() instanceof ServerLevel serverLevel)) {
            return;
        }

        event.setCanceled(true);
        BlockPos pos = event.getPos();
        ModUtilsGreenhouse.showInfo(serverLevel, pos, player, ModUtilsGreenhouse.getAreaByTier(8));
    }
}