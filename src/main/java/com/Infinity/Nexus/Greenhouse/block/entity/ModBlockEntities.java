package com.Infinity.Nexus.Greenhouse.block.entity;

import com.Infinity.Nexus.Greenhouse.InfinityNexusGreenhouse;
import com.Infinity.Nexus.Greenhouse.block.ModBlocksGreenhouse;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlockEntities {
    public  static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, InfinityNexusGreenhouse.MOD_ID);

    public static final Supplier<BlockEntityType<GreenhouseBlockEntity>> GREENHOUSE_BE =
            BLOCK_ENTITY.register("greenhouse_block_entity", () -> BlockEntityType.Builder.of(
                    GreenhouseBlockEntity::new, ModBlocksGreenhouse.GREENHOUSE.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITY.register(eventBus);
    }
}
