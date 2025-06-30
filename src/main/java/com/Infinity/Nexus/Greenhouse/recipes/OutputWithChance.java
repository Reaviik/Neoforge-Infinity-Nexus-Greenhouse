package com.Infinity.Nexus.Greenhouse.recipes;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

public record OutputWithChance(ItemStack stack, float chance) {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final Codec<OutputWithChance> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("item").xmap(
                    location -> {
                        if (!BuiltInRegistries.ITEM.containsKey(location)) {
                            LOGGER.error("Item não encontrado no registro: {}", location);
                            return ItemStack.EMPTY;
                        }
                        return new ItemStack(BuiltInRegistries.ITEM.get(location));
                    },
                    stack -> {
                        if (stack.isEmpty()) {
                            LOGGER.error("Tentativa de serializar ItemStack vazio");
                            return ResourceLocation.fromNamespaceAndPath("minecraft", "air");
                        }
                        return BuiltInRegistries.ITEM.getKey(stack.getItem());
                    }
            ).forGetter(OutputWithChance::stack),
            Codec.FLOAT.fieldOf("chance").orElse(1.0f).forGetter(OutputWithChance::chance),
            Codec.INT.optionalFieldOf("count", 1).forGetter(stack -> stack.stack().getCount())
    ).apply(instance, (item, chance, count) -> {
        if (item.isEmpty()) {
            LOGGER.error("Tentativa de criar OutputWithChance com ItemStack vazio");
            // Retorna um item padrão (ar) para evitar crash, mas você deve corrigir suas receitas
            return new OutputWithChance(new ItemStack(BuiltInRegistries.ITEM.get(ResourceLocation.fromNamespaceAndPath("minecraft", "air"))), 0);
        }

        ItemStack stack = item.copy();
        stack.setCount(count);
        return new OutputWithChance(stack, chance);
    }));

    // Validação adicional
    public OutputWithChance {
        if (stack.isEmpty()) {
            throw new IllegalArgumentException("ItemStack não pode ser vazio em OutputWithChance");
        }
        if (chance < 0 || chance > 1) {
            throw new IllegalArgumentException("Chance deve estar entre 0 e 1");
        }
    }
}