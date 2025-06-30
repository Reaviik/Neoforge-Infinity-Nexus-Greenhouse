package com.Infinity.Nexus.Greenhouse.compat.jade;

import com.Infinity.Nexus.Greenhouse.InfinityNexusGreenhouse;
import com.Infinity.Nexus.Greenhouse.block.entity.GreenhouseBlockEntity;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import snownee.jade.api.BlockAccessor;
import snownee.jade.api.IBlockComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.config.IPluginConfig;

public enum GreenhouseOwner implements IBlockComponentProvider {
    INSTANCE;

    private static final ResourceLocation UID = ResourceLocation.fromNamespaceAndPath(InfinityNexusGreenhouse.MOD_ID, "greenhouse_owner");

    @Override
    public void appendTooltip(ITooltip iTooltip, BlockAccessor blockAccessor, IPluginConfig iPluginConfig) {
        if (blockAccessor.getBlockEntity() instanceof GreenhouseBlockEntity greenhouse) {
            iTooltip.add(Component.translatable("gui.infinity_nexus_greenhouse.owner").append(greenhouse.getOwner()));
            iTooltip.add(Component.translatable("gui.infinity_nexus_greenhouse.level").append("§3"+greenhouse.getTier()));
            iTooltip.add(Component.translatable("gui.infinity_nexus_greenhouse.link").append(greenhouse.getHasLink()));
        }
    }

    @Override
    public ResourceLocation getUid() {
        return UID;
    }
}