package com.Infinity.Nexus.Greenhouse.datagen.loot;

import com.Infinity.Nexus.Greenhouse.block.ModBlocksGreenhouse;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    public ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        dropSelf(ModBlocksGreenhouse.GREENHOUSE.get());
    }

    @Override
    protected Iterable<Block> getKnownBlocks() {
        return ModBlocksGreenhouse.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
