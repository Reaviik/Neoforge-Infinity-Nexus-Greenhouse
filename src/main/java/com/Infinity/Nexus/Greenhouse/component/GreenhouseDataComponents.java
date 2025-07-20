package com.Infinity.Nexus.Greenhouse.component;

import com.Infinity.Nexus.Greenhouse.InfinityNexusGreenhouse;
import com.mojang.serialization.Codec;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.UnaryOperator;

/*
*
* Credits
* https://github.com/InnovativeOnlineIndustries/Industrial-Foregoing/blob/1.21/src/main/java/com/buuz135/industrial/api/IMachineSettings.java#L25
*
*
*/
public class GreenhouseDataComponents {
    public static final DeferredRegister<DataComponentType<?>> DATA_COMPONENT_TYPES = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, InfinityNexusGreenhouse.MOD_ID);

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Integer>> DEBUG_HOE = register("greenhouse_debug_hoe",  op -> op.persistent(Codec.INT));

    private static <T>DeferredHolder<DataComponentType<?>, DataComponentType<T>> register(String name, UnaryOperator<DataComponentType.Builder<T>> builderOperator) {
        return DATA_COMPONENT_TYPES.register(name, () -> builderOperator.apply(DataComponentType.builder()).build());
    }

    public static void register(IEventBus eventBus) {
        DATA_COMPONENT_TYPES.register(eventBus);
    }
}