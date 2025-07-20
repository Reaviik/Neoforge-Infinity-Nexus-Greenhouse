package com.Infinity.Nexus.Greenhouse.datagen;


import com.Infinity.Nexus.Greenhouse.InfinityNexusGreenhouse;
import com.Infinity.Nexus.Greenhouse.item.ModItemsGreenhouse;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(PackOutput output, ExistingFileHelper existingFileHelper) {
        super(output, InfinityNexusGreenhouse.MOD_ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        basicItem(ModItemsGreenhouse.FERTILIZER_UPGRADE.get());
        basicItem(ModItemsGreenhouse.IRRIGATE_UPGRADE.get());
        basicItem(ModItemsGreenhouse.PLANTER_UPGRADE.get());
        basicItem(ModItemsGreenhouse.COLLECTOR_UPGRADE.get());
        basicItem(ModItemsGreenhouse.SECONDARY_OUTPUT_UPGRADE.get());
    }
}
