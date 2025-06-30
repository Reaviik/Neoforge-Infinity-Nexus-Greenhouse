package com.Infinity.Nexus.Greenhouse.datagen;

import com.Infinity.Nexus.Greenhouse.InfinityNexusGreenhouse;
import com.Infinity.Nexus.Greenhouse.block.ModBlocksGreenhouse;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementType;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.Optional;
import java.util.function.Consumer;

public class ModAdvancementProvider implements AdvancementProvider.AdvancementGenerator {

    @Override
    public void generate(HolderLookup.Provider provider, Consumer<AdvancementHolder> consumer, ExistingFileHelper existingFileHelper) {

        Advancement rootAdvancement = Advancement.Builder.advancement()
                .display(new DisplayInfo(new ItemStack(ModBlocksGreenhouse.GREENHOUSE.get()),
                        Component.literal("Infinity Nexus Greenhouse"), Component.translatable("advancement.infinity_nexus_greenhouse.start"),
                        Optional.of(ResourceLocation.fromNamespaceAndPath(InfinityNexusGreenhouse.MOD_ID, "textures/block/greenhouse.png")),
                        AdvancementType.TASK,
                        true, true, false))
                .addCriterion("has_greenhouse", InventoryChangeTrigger.TriggerInstance.hasItems(Items.AMETHYST_SHARD))
                .save(consumer, InfinityNexusGreenhouse.MOD_ID + "Greenhouse").value();
    }
}
