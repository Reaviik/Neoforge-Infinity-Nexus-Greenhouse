package com.Infinity.Nexus.Greenhouse.slots;

import com.Infinity.Nexus.Core.itemStackHandler.RestrictedItemStackHandler;
import com.Infinity.Nexus.Core.slots.RestrictiveSlot;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.HoeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.jetbrains.annotations.NotNull;

public class HoeSlot extends RestrictiveSlot {
    public HoeSlot(RestrictedItemStackHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition, 1);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return stack.is(ItemTags.HOES);
    }
}