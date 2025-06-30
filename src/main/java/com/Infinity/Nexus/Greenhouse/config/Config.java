package com.Infinity.Nexus.Greenhouse.config;

import com.Infinity.Nexus.Greenhouse.InfinityNexusGreenhouse;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.config.ModConfigEvent;
import net.neoforged.neoforge.common.ModConfigSpec;

import java.util.ArrayList;
import java.util.List;

@EventBusSubscriber(modid = InfinityNexusGreenhouse.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class Config
{
    //Instancia a Configuração
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    private static final ModConfigSpec.BooleanValue GREENHOUSE_COST_COMPONENT_DURABILITY = BUILDER
            .comment("Define se componente perderá durabilidade (se o componente não for um item duravel ele será consumido)")
            .define("greenhouse_cost_component_durability", true);

    private static final ModConfigSpec.BooleanValue GREENHOUSE_HARVEST_SEEDS = BUILDER
            .comment("Define se a Estufa vai recolher os sementes")
            .define("greenhouse_harvest_seeds", false);

    private static final ModConfigSpec.IntValue GREENHOUSE_ENERGY = BUILDER.comment("Define a quantidade de energia que a Estufa vai armazenar").defineInRange("greenhouse_energy_capacity", 150000, 1, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue GREENHOUSE_ENERGY_TRANSFER = BUILDER.comment("Define a quantidade de energia que a Estufa vai transferir").defineInRange("greenhouse_energy_transfer", 100000, 1, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue GREENHOUSE_FLUID_STORAGE_CAPACITY  = BUILDER.comment("Define a quantidade de liquido a Estufa pode armazenar").defineInRange("greenhouse_fluid_capacity", 10000, 1, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue GREENHOUSE_FUEL_MULTIPLIER = BUILDER.comment("Define o valor que a Estufa vai multiplicar a geração de combustível. Ex: Carvão == 1600 * greenhouse_fuel_multiplier = Energia que um caravão vai gerar").defineInRange("greenhouse_fuel_multiplier", 5, 1, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue GREENHOUSE_ENERGY_PER_OPERATION_BASE  = BUILDER.comment("Define a quantidade de energia que a Estufa consumirá por operação (a base para os calculos)").defineInRange("greenhouse_energy_per_operation", 100, 1, Integer.MAX_VALUE);
    private static final ModConfigSpec.IntValue GREENHOUSE_MINIMUM_TICK  = BUILDER.comment("Define a menor velocidade que a Estufa pode trabalhar").defineInRange("greenhouse_minimum_tick", 1, 1, Integer.MAX_VALUE);

    private static final ModConfigSpec.ConfigValue<List<? extends String>> LIST_OF_NON_FERTILIZABLE_BLOCKS = BUILDER
            .comment("Lista de Plantas que nao podem ser fertilizadas")
            .defineList("list_of_non_fertilizable_blocks",
                    List.of(
                            "minecraft:lilac",
                            "minecraft:sunflower",
                            "minecraft:peony",
                            "minecraft:rose_bush",
                            "minecraft:glow_lichen",
                            "minecraft:crimson_fungus",
                            "minecraft:warped_fungus"
                    ), o -> o instanceof String);

    private static final ModConfigSpec.ConfigValue<List<? extends Integer>> LIST_OF_MAX_GREENHOUSE_INTERIOR_BLOCKS = BUILDER
            .comment("Define o tamanho maximo da estufa para cada tier")
            .defineList("list_of_max_greenhouse_interior_blocks", List.of(50, 100, 200, 300, 400, 600, 800, 1000, 1200), o -> o instanceof Integer);
    public static final ModConfigSpec SPEC = BUILDER.build();


    //Cria as Variaveis de Configuração

    public static boolean greenhouse_harvest_seeds;
    public static boolean greenhouse_cost_component_durability;

    public static int greenhouse_energy_capacity;
    public static int greenhouse_energy_transfer;
    public static int greenhouse_fluid_storage_capacity;
    public static int greenhouse_fuel_multiplier;
    public static int greenhouse_energy_per_operation_base;
    public static int greenhouse_minimum_tick;

    public static List<String> list_of_non_fertilizable_blocks;
    public static List<Integer> list_of_max_greenhouse_interior_blocks;


    @SubscribeEvent
    static void onLoad(final ModConfigEvent event)
    {
        greenhouse_cost_component_durability = GREENHOUSE_COST_COMPONENT_DURABILITY.get();
        greenhouse_harvest_seeds = GREENHOUSE_HARVEST_SEEDS.get();

        greenhouse_energy_capacity = GREENHOUSE_ENERGY.get();
        greenhouse_energy_transfer = GREENHOUSE_ENERGY_TRANSFER.get();
        greenhouse_fluid_storage_capacity = GREENHOUSE_FLUID_STORAGE_CAPACITY.get();
        greenhouse_fuel_multiplier = GREENHOUSE_FUEL_MULTIPLIER.get();
        greenhouse_energy_per_operation_base = GREENHOUSE_ENERGY_PER_OPERATION_BASE.get();
        greenhouse_minimum_tick = GREENHOUSE_MINIMUM_TICK.get();

        list_of_non_fertilizable_blocks = new ArrayList<>(LIST_OF_NON_FERTILIZABLE_BLOCKS.get());
        list_of_max_greenhouse_interior_blocks = new ArrayList<>(LIST_OF_MAX_GREENHOUSE_INTERIOR_BLOCKS.get());

    }
}
