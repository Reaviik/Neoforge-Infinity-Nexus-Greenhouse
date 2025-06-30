package com.Infinity.Nexus.Greenhouse.screen.greenhouse;


import com.Infinity.Nexus.Core.itemStackHandler.RestrictedItemStackHandler;
import com.Infinity.Nexus.Core.screen.BaseAbstractContainerMenu;
import com.Infinity.Nexus.Core.slots.*;
import com.Infinity.Nexus.Greenhouse.block.ModBlocksGreenhouse;
import com.Infinity.Nexus.Greenhouse.block.entity.GreenhouseBlockEntity;
import com.Infinity.Nexus.Greenhouse.screen.ModMenuTypes;
import com.Infinity.Nexus.Greenhouse.slots.FertilizerSlot;
import com.Infinity.Nexus.Greenhouse.slots.HoeSlot;
import com.Infinity.Nexus.Greenhouse.slots.RecipeSlot;
import com.Infinity.Nexus.Greenhouse.slots.SeedSlot;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.energy.IEnergyStorage;

public class GreenhouseMenu extends BaseAbstractContainerMenu {
    public final GreenhouseBlockEntity blockEntity;
    private final Level level;
    private IEnergyStorage energyStorage;
    private final ContainerData data;
    private static final int slots = 19;

    public GreenhouseMenu(int pContainerId, Inventory inv, FriendlyByteBuf extraData) {
        this(pContainerId, inv, (GreenhouseBlockEntity) inv.player.level().getBlockEntity(extraData.readBlockPos()), new SimpleContainerData(19), new RestrictedItemStackHandler(slots));
    }

    public GreenhouseMenu(int pContainerId, Inventory inv, GreenhouseBlockEntity entity, ContainerData data, RestrictedItemStackHandler iItemHandler) {
        super(ModMenuTypes.GREENHOUSE_MENU.get(), pContainerId, slots);
        checkContainerSize(inv, slots);
        blockEntity = entity;
        energyStorage = blockEntity.getEnergyStorage();
        level = inv.player.level();
        this.data = data;

        addPlayerInventory(inv);
        addPlayerHotbar(inv);


        this.addSlot(new ResultSlot(iItemHandler, 0, 80, 11));
        this.addSlot(new ResultSlot(iItemHandler, 1, 98, 11));
        this.addSlot(new ResultSlot(iItemHandler, 2, 116, 11));
        this.addSlot(new ResultSlot(iItemHandler, 3, 80, 29));
        this.addSlot(new ResultSlot(iItemHandler, 4, 98, 29));
        this.addSlot(new ResultSlot(iItemHandler, 5, 116, 29));
        this.addSlot(new ResultSlot(iItemHandler, 6, 80, 47));
        this.addSlot(new ResultSlot(iItemHandler, 7, 98, 47));

        this.addSlot(new RecipeSlot(iItemHandler, 8, 116, 47));

        this.addSlot(new UpgradeSlot(iItemHandler, 9, -11, 11));
        this.addSlot(new UpgradeSlot(iItemHandler, 10, -11, 23));
        this.addSlot(new UpgradeSlot(iItemHandler, 11, -11, 35));
        this.addSlot(new UpgradeSlot(iItemHandler, 12, -11, 47));

        this.addSlot(new ComponentSlot(iItemHandler, 13, 8, 29));

        this.addSlot(new HoeSlot(iItemHandler, 14, 44, 11));
        this.addSlot(new LinkSlot(iItemHandler, 15, 44, 29));
        this.addSlot(new FuelSlot(iItemHandler, 16, 44, 47));
        this.addSlot(new FertilizerSlot(iItemHandler, 17, 26, 29));
        this.addSlot(new SeedSlot(iItemHandler, 18, 26, 11));

        addDataSlots(data);
    }

    public int getData(int index){
        return data.get(index);
    }

    public boolean isCrafting() {
        return data.get(0) > 0 && data.get(7) > 0 && data.get(8) > 0 && data.get(9) > 0;
    }

    public int getScaledProgress() {
        int progress = this.data.get(0);
        int maxProgress = this.data.get(1);  // Max Progress
        int progressArrowSize = 62;
        return maxProgress != 0 && progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }
    public int getLightLevel() {
        int progress = this.data.get(16);
        int maxProgress = 15;
        int progressArrowSize = 62;
        return progress != 0 ? progress * progressArrowSize / maxProgress : 0;
    }


    public GreenhouseBlockEntity getBlockEntity(){
        return blockEntity;
    }
    public IEnergyStorage getEnergyStorage() {
        return energyStorage;
    }

    @Override
    public boolean stillValid(Player pPlayer) {
        return stillValid(ContainerLevelAccess.create(this.getBlockEntity().getLevel(), this.getBlockEntity().getBlockPos()),
                pPlayer, ModBlocksGreenhouse.GREENHOUSE.get());
    }

    public int[] getDisplayInfo() {
        return new int[] { data.get(5), data.get(8), data.get(9), data.get(4), data.get(7), data.get(10), data.get(17), data.get(16), data.get(15), data.get(18) };
    }
}