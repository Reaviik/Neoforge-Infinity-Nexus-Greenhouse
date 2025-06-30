package com.Infinity.Nexus.Greenhouse.tab;

import com.Infinity.Nexus.Greenhouse.InfinityNexusGreenhouse;
import com.Infinity.Nexus.Greenhouse.block.ModBlocksGreenhouse;
import com.Infinity.Nexus.Greenhouse.item.ModItemsGreenhouse;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModTab {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, InfinityNexusGreenhouse.MOD_ID);
    public static final Supplier<CreativeModeTab> INFINITY_TAB_GREENHOUSE = CREATIVE_MODE_TABS.register("infinity_nexus_greenhouse",
            //Tab Icon
            () -> CreativeModeTab.builder().icon(() -> new ItemStack(ModBlocksGreenhouse.GREENHOUSE.get()))
                    .title(Component.translatable("itemGroup.infinity_nexus_greenhouse"))
                    .displayItems((pParameters, pOutput) -> {
                        //-------------------------//-------------------------//
                        //Machines
                        pOutput.accept(new ItemStack(ModBlocksGreenhouse.GREENHOUSE.get()));
                        //-------------------------//-------------------------//
                        //Items
                        pOutput.accept(new ItemStack(ModItemsGreenhouse.FERTILIZER_UPGRADE.get()));
                        pOutput.accept(new ItemStack(ModItemsGreenhouse.IRRIGATE_UPGRADE.get()));
                        pOutput.accept(new ItemStack(ModItemsGreenhouse.PLANTER_UPGRADE.get()));
                        pOutput.accept(new ItemStack(ModItemsGreenhouse.COLLECTOR_UPGRADE.get()));
                        //-------------------------//-------------------------//

                    })
                    .build());
    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
