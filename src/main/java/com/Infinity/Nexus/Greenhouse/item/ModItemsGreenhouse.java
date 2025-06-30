package com.Infinity.Nexus.Greenhouse.item;

import com.Infinity.Nexus.Core.items.custom.ComponentItem;
import com.Infinity.Nexus.Core.items.custom.UpgradeItem;
import com.Infinity.Nexus.Greenhouse.InfinityNexusGreenhouse;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItemsGreenhouse {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(InfinityNexusGreenhouse.MOD_ID);

    public static final DeferredItem<Item> FERTILIZER_UPGRADE = ITEMS.register("fertilizer_upgrade",() ->
            new UpgradeItem(new Item.Properties().rarity(Rarity.COMMON).stacksTo(1)));
    public static final DeferredItem<Item> IRRIGATE_UPGRADE = ITEMS.register("irrigate_upgrade",() ->
            new UpgradeItem(new Item.Properties().rarity(Rarity.COMMON).stacksTo(1)));
    public static final DeferredItem<Item> PLANTER_UPGRADE = ITEMS.register("planter_upgrade",() ->
            new UpgradeItem(new Item.Properties().rarity(Rarity.COMMON).stacksTo(1)));
    public static final DeferredItem<Item> COLLECTOR_UPGRADE = ITEMS.register("collector_upgrade",() ->
            new UpgradeItem(new Item.Properties().rarity(Rarity.COMMON).stacksTo(1)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}