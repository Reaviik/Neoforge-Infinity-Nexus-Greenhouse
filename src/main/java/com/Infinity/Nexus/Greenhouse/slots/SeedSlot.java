package com.Infinity.Nexus.Greenhouse.slots;

import com.Infinity.Nexus.Core.itemStackHandler.RestrictedItemStackHandler;
import com.Infinity.Nexus.Core.slots.RestrictiveSlot;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;

public class SeedSlot extends RestrictiveSlot {
    public SeedSlot(RestrictedItemStackHandler itemHandler, int index, int xPosition, int yPosition) {
        super(itemHandler, index, xPosition, yPosition, 1);
    }

    @Override
    public boolean mayPlace(@NotNull ItemStack stack) {
        return stack.is(Tags.Items.SEEDS) || stack.is(Tags.Items.CROPS);
    }
}