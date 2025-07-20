package com.Infinity.Nexus.Greenhouse.item.custom;

import com.Infinity.Nexus.Core.component.CoreDataComponents;
import com.Infinity.Nexus.Core.items.ModItems;
import com.Infinity.Nexus.Core.items.custom.UpgradeItem;
import com.Infinity.Nexus.Greenhouse.item.ModItemsGreenhouse;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class GreenhouseUpgradeItem extends UpgradeItem {
    public GreenhouseUpgradeItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext tooltip, List<Component> components, TooltipFlag flag) {
        if (Screen.hasShiftDown()) {
            if (stack.getItem() == ModItemsGreenhouse.FERTILIZER_UPGRADE.get()) {
                components.add(Component.translatable("tooltip.infinity_nexus_greenhouse.fertilizer_upgrade"));
                components.add(Component.translatable("tooltip.infinity_nexus_greenhouse.upgrade_no_additional_speed"));
            } else if (stack.getItem() == ModItemsGreenhouse.IRRIGATE_UPGRADE.get()) {
                components.add(Component.translatable("tooltip.infinity_nexus_greenhouse.irrigate_upgrade"));
                components.add(Component.translatable("tooltip.infinity_nexus_greenhouse.upgrade_no_additional_speed"));
            } else if (stack.getItem() == ModItemsGreenhouse.PLANTER_UPGRADE.get()) {
                components.add(Component.translatable("tooltip.infinity_nexus_greenhouse.planter_upgrade"));
                components.add(Component.translatable("tooltip.infinity_nexus_greenhouse.upgrade_no_additional_speed"));
            } else if (stack.getItem() == ModItemsGreenhouse.COLLECTOR_UPGRADE.get()) {
                components.add(Component.translatable("tooltip.infinity_nexus_greenhouse.collector_upgrade"));
                components.add(Component.translatable("tooltip.infinity_nexus_greenhouse.upgrade_no_additional_speed"));
            } else if (stack.getItem() == ModItemsGreenhouse.SECONDARY_OUTPUT_UPGRADE.get()) {
                components.add(Component.translatable("tooltip.infinity_nexus_greenhouse.secondary_output_upgrade"));
                components.add(Component.translatable("tooltip.infinity_nexus_greenhouse.upgrade_no_additional_speed"));
            }
        }

        super.appendHoverText(stack, tooltip, components, flag);
    }
}
