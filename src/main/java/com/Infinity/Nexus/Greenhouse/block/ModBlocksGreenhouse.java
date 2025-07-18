package com.Infinity.Nexus.Greenhouse.block;

import com.Infinity.Nexus.Greenhouse.InfinityNexusGreenhouse;
import com.Infinity.Nexus.Greenhouse.block.custom.Greenhouse;
import com.Infinity.Nexus.Greenhouse.item.ModItemsGreenhouse;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModBlocksGreenhouse {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(InfinityNexusGreenhouse.MOD_ID);
    public static final DeferredBlock<Block> GREENHOUSE = registerBlock("greenhouse", () ->
            new Greenhouse(BlockBehaviour.Properties.ofFullCopy(Blocks.IRON_BLOCK).requiresCorrectToolForDrops().strength(1.0f, 6.0f)
                    .lightLevel((state) -> state.getValue(Greenhouse.LIT) >= 8 ? 2 : 0).noOcclusion().mapColor(MapColor.METAL)));



    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItemsGreenhouse.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}