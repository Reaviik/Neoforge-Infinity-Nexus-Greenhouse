package com.Infinity.Nexus.Greenhouse.command;

import com.Infinity.Nexus.Core.utils.GetResourceLocation;
import com.Infinity.Nexus.Greenhouse.InfinityNexusGreenhouse;
import com.Infinity.Nexus.Greenhouse.component.GreenhouseDataComponents;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.util.List;
import java.util.Set;

public class GreenhouseTP {
    public GreenhouseTP(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("infinitynexusgreenhouse")
                .then(Commands.literal("tp")
                        .then(Commands.argument("player", EntityArgument.player())
                                .then(Commands.argument("level", StringArgumentType.string()) // Nome da dimensão
                                        .then(Commands.argument("x", DoubleArgumentType.doubleArg())
                                                .then(Commands.argument("y", DoubleArgumentType.doubleArg())
                                                        .then(Commands.argument("z", DoubleArgumentType.doubleArg())
                                                                .then(Commands.argument("pit", StringArgumentType.string())
                                                                      .executes(this::execute)
                                                                )
                                                        )
                                                )
                                        )
                                )
                        )
                )
                .then(Commands.argument("givedebughoe", StringArgumentType.string())
                        .requires(source -> source.hasPermission(2))
                        .executes(this::giveDebugStick)
                )
        );
    }
    private int giveDebugStick(CommandContext<CommandSourceStack> context) {
        ServerLevel serverLevel = context.getSource().getLevel();
        Vec3 pos = context.getSource().getPosition();
        ItemStack stack = new ItemStack(Items.GOLDEN_HOE);
        ItemLore lore = new ItemLore(List.of(Component.literal("§bGreenhouse Debug Hoe")));
        stack.set(GreenhouseDataComponents.DEBUG_HOE, 1);
        stack.set(DataComponents.LORE, lore);
        ItemEntity axeEditor = new ItemEntity(serverLevel,pos.x, pos.y, pos.z, stack);
        serverLevel.addFreshEntity(axeEditor);
        return 1;
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        try {
            ServerPlayer player = EntityArgument.getPlayer(context, "player");
            String levelName = StringArgumentType.getString(context, "level");
            String pit = StringArgumentType.getString(context, "pit");
            double x = DoubleArgumentType.getDouble(context, "x");
            double y = DoubleArgumentType.getDouble(context, "y");
            double z = DoubleArgumentType.getDouble(context, "z");

            if (!pit.equals("dezanove")) {
                context.getSource().sendFailure(Component.translatable(InfinityNexusGreenhouse.MESSAGE).append(Component.translatable("chat.infinity_nexus_greenhouse.tp_player_error")));
                return 0;
            }

            ResourceKey<Level> dimensionKey = ResourceKey.create(Registries.DIMENSION, GetResourceLocation.parse(levelName));
            ServerLevel targetWorld = player.getServer().getLevel(dimensionKey);

            if (targetWorld == null) {
                context.getSource().sendFailure(Component.translatable(InfinityNexusGreenhouse.MESSAGE).append(Component.translatable("chat.infinity_nexus_greenhouse.tp_dimension_error").append(levelName)));
                return 0;
            }

            player.teleportTo(targetWorld, x, y, z, Set.of(), player.getYRot(), player.getXRot());
            context.getSource().sendSuccess(() -> Component.translatable(InfinityNexusGreenhouse.MESSAGE).append(Component.translatable("chat.infinity_nexus_greenhouse.tp_player_success")), true);
            return 1;
        } catch (Exception e) {
            context.getSource().sendFailure(Component.translatable(InfinityNexusGreenhouse.MESSAGE).append(Component.translatable("chat.infinity_nexus_greenhouse.tp_player_error_0")));
            e.printStackTrace();
            return 0;
        }
    }
}
