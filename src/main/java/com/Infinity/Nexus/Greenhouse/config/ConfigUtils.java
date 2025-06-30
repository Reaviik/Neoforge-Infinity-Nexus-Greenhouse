package com.Infinity.Nexus.Greenhouse.config;

import com.Infinity.Nexus.Core.items.ModItems;
import net.minecraft.world.item.ItemStack;

public class ConfigUtils {
    public static ItemStack getComponentByLevel(int level) {
        return switch (level) {
            case 1 -> ModItems.REDSTONE_COMPONENT.get().getDefaultInstance();
            case 2 -> ModItems.BASIC_COMPONENT.get().getDefaultInstance();
            case 3 -> ModItems.REINFORCED_COMPONENT.get().getDefaultInstance();
            case 4 -> ModItems.LOGIC_COMPONENT.get().getDefaultInstance();
            case 5 -> ModItems.ADVANCED_COMPONENT.get().getDefaultInstance();
            case 6 -> ModItems.REFINED_COMPONENT.get().getDefaultInstance();
            case 7 -> ModItems.INTEGRAL_COMPONENT.get().getDefaultInstance();
            case 8 -> ModItems.INFINITY_COMPONENT.get().getDefaultInstance();
            case 9 -> ModItems.ANCESTRAL_COMPONENT.get().getDefaultInstance();
            default -> ItemStack.EMPTY;
        };
    }
}
