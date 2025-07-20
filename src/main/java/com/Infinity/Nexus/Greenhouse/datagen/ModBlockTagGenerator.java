package com.Infinity.Nexus.Greenhouse.datagen;

import com.Infinity.Nexus.Greenhouse.InfinityNexusGreenhouse;
import com.Infinity.Nexus.Greenhouse.block.ModBlocksGreenhouse;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class ModBlockTagGenerator extends BlockTagsProvider {
    public ModBlockTagGenerator(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
        super(output, lookupProvider, InfinityNexusGreenhouse.MOD_ID, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {

        this.tag(BlockTags.MINEABLE_WITH_PICKAXE)
            .add(ModBlocksGreenhouse.GREENHOUSE.get());


        this.tag(BlockTags.NEEDS_STONE_TOOL)
            .add(ModBlocksGreenhouse.GREENHOUSE.get());


    }

    @Override
    public String getName() {
        return "Block Tags";
    }
}
