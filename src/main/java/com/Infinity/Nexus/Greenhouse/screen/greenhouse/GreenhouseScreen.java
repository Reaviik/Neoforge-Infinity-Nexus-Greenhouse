package com.Infinity.Nexus.Greenhouse.screen.greenhouse;

import com.Infinity.Nexus.Core.items.ModItems;
import com.Infinity.Nexus.Core.renderer.EnergyInfoArea;
import com.Infinity.Nexus.Core.renderer.FluidTankRenderer;
import com.Infinity.Nexus.Core.renderer.InfoArea;
import com.Infinity.Nexus.Core.renderer.RenderScreenTooltips;
import com.Infinity.Nexus.Core.utils.MouseUtil;
import com.Infinity.Nexus.Greenhouse.InfinityNexusGreenhouse;
import com.Infinity.Nexus.Greenhouse.block.entity.GreenhouseBlockEntity;
import com.Infinity.Nexus.Greenhouse.config.Config;
import com.Infinity.Nexus.Greenhouse.networking.ModMessages;
import com.Infinity.Nexus.Greenhouse.networking.packet.AreaVisualizerC2SPacket;
import com.Infinity.Nexus.Greenhouse.utils.ModUtilsGreenhouse;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.List;
import java.util.Optional;

public class GreenhouseScreen extends AbstractContainerScreen<GreenhouseMenu> {
    private static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(InfinityNexusGreenhouse.MOD_ID, "textures/gui/greenhouse_gui.png");
    private static final ResourceLocation ARROW = ResourceLocation.fromNamespaceAndPath(InfinityNexusGreenhouse.MOD_ID, "textures/gui/greenhouse_arrow.png");
    private static final ResourceLocation TEXTURE_SLOTS = ResourceLocation.fromNamespaceAndPath(InfinityNexusGreenhouse.MOD_ID, "textures/gui/greenhouse_slots_gui.png");

    private EnergyInfoArea energyInfoArea;
    private FluidTankRenderer fluidRenderer;
    private Button areaVisualizerButton;

    public GreenhouseScreen(GreenhouseMenu pMenu, Inventory pPlayerInventory, Component pTitle) {
        super(pMenu, pPlayerInventory, pTitle);
    }
    @Override
    protected void init() {
        super.init();
        this.inventoryLabelY = 10000;
        this.titleLabelY = 10000;
        assignEnergyInfoArea();
        assignFluidTank();

        this.areaVisualizerButton = addRenderableWidget(
                Button.builder(
                                Component.literal(" "),
                                this::handleExampleButton)
                        .bounds(this.leftPos +152, this.topPos -10, 8, 9)
                        .tooltip(Tooltip.create(Component.translatable("gui.infinity_nexus_greenhouse.show_area")))
                        .build());
        this.areaVisualizerButton.setAlpha(0.0F);
    }

    private void handleExampleButton(AbstractButton button) {
        if (menu.blockEntity != null && menu.blockEntity.getLevel() != null && menu.blockEntity.getLevel().isClientSide()) {
            ModMessages.sendToServer(new AreaVisualizerC2SPacket(menu.blockEntity.getBlockPos()));
        }
    }

    private void assignEnergyInfoArea() {
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;
        energyInfoArea = new EnergyInfoArea(x + 159, y + 6, menu.getEnergyStorage());
    }
    private void assignFluidTank() {
        fluidRenderer = new FluidTankRenderer(GreenhouseBlockEntity.getInputFluidCapacity(), true, 6, 62);
    }


    @Override
    protected void renderLabels(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY) {
        pGuiGraphics.drawString(this.font,this.playerInventoryTitle,8,74,0XFFFFFF);
        pGuiGraphics.drawString(this.font,this.title,8,-9,0XFFFFFF);
        pGuiGraphics.drawString(this.font,Component.translatable("gui.infinity_nexus_greenhouse.owner").append("§b").getString() + menu.getBlockEntity().setOwnerName(),80,74,0XFFFFFF);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        renderEnergyAreaTooltips(pGuiGraphics,pMouseX,pMouseY, x, y);
        renderFluidAreaTooltips(pGuiGraphics,pMouseX,pMouseY, x+89, y, menu.blockEntity.getFluidInInputTank(), 62,6, fluidRenderer);
        renderTooltips(pGuiGraphics,pMouseX,pMouseY, x, y);

        InfoArea.draw(pGuiGraphics);
        super.renderLabels(pGuiGraphics, pMouseX, pMouseY);
    }

    private void renderEnergyAreaTooltips(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, int x, int y) {
        if(isMouseAboveArea(pMouseX, pMouseY, x, y, 159,  6, 6, 62)) {
            pGuiGraphics.renderTooltip(this.font, energyInfoArea.getTooltips(), Optional.empty(), pMouseX - x, pMouseY - y);
        }
    }
    private void renderTooltips(GuiGraphics pGuiGraphics, int pMouseX, int pMouseY, int x, int y) {
        if(Screen.hasShiftDown()) {
            if (isMouseAboveArea(pMouseX, pMouseY, x, y, -11, 10, 16, 52)) {
                RenderScreenTooltips.renderUpgradeSlotTooltipAndItems(this.font, pGuiGraphics, pMouseX, pMouseY, x, y);
            }else if (isMouseAboveArea(pMouseX, pMouseY, x, y, 7, 28, 17, 17)) {
                RenderScreenTooltips.renderComponentSlotTooltipAndItems(this.font, pGuiGraphics, pMouseX, pMouseY, x, y);
            }else if (isMouseAboveArea(pMouseX, pMouseY, x, y, 25, 28, 17, 17)) {
                //TODO Fertilizer  Slot
                List<Component> structureTooltip = List.of(Component.translatable("tooltip.infinity_nexus_greenhouse.fertilizer_slot_tooltip"));
                RenderScreenTooltips.renderTooltipArea(this.font, pGuiGraphics, structureTooltip, pMouseX, pMouseY, x, y);
            }else if (isMouseAboveArea(pMouseX, pMouseY, x, y, 79, 10, 53, 53)) {
                //TODO Output Slot
                List<Component> outputTooltip = List.of(Component.translatable("tooltip.infinity_nexus_greenhouse.output_slot_tooltip"));
                RenderScreenTooltips.renderTooltipArea(this.font, pGuiGraphics, outputTooltip, pMouseX, pMouseY, x, y);
            }else if (isMouseAboveArea(pMouseX, pMouseY, x, y, 43, 10, 17, 17)) {
                //TODO Hoe Slot
                List<Component> enchantTooltip = List.of(Component.translatable("tooltip.infinity_nexus_greenhouse.hoe_slot_tooltip"));
                RenderScreenTooltips.renderTooltipArea(this.font, pGuiGraphics, enchantTooltip, pMouseX, pMouseY, x, y);
            }else if (isMouseAboveArea(pMouseX, pMouseY, x, y, 43, 28, 17, 17)) {
                //TODO Linking Slot
                List<Component> linkingTooltip = List.of(Component.translatable("tooltip.infinity_nexus_greenhouse.linking_slot_tooltip"));
                RenderScreenTooltips.renderTooltipArea(this.font, pGuiGraphics, linkingTooltip, pMouseX, pMouseY, x, y);
            }else if (isMouseAboveArea(pMouseX, pMouseY, x, y, 43, 46, 17, 17)) {
                //TODO Fuel Slot
                List<Component> fuelTooltip = List.of(Component.translatable("tooltip.infinity_nexus_greenhouse.fuel_slot_tooltip"));
                RenderScreenTooltips.renderTooltipArea(this.font, pGuiGraphics, fuelTooltip, pMouseX, pMouseY, x, y);
            }
        }
        if (isMouseAboveArea(pMouseX, pMouseY, x, y, 135,  6, 6, 62)) {
            List<Component> progressTooltip = List.of(Component.literal((int)(((double) menu.getScaledProgress() / 62 ) * 100) + "%"));
            pGuiGraphics.renderTooltip(font, progressTooltip, Optional.empty(), pMouseX - x, pMouseY - y);
        }else if (isMouseAboveArea(pMouseX, pMouseY, x, y, 135+8,  6, 6, 62)) {
            List<Component> fuelTooltip = List.of(Component.translatable("gui.infinity_nexus_greenhouse.light").append(" = " + menu.getData(16)));
            pGuiGraphics.renderTooltip(font, fuelTooltip, Optional.empty(), pMouseX - x, pMouseY - y);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float pPartialTick, int pMouseX, int pMouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, TEXTURE);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        guiGraphics.blit(TEXTURE, x + 2, y-14, 2, 167, 174, 64);
        guiGraphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight);

        if(Screen.hasShiftDown()){
            guiGraphics.blit(TEXTURE_SLOTS, x, y, 0, 0, imageWidth, imageHeight);
            RenderScreenTooltips.renderComponentSlotTooltip(guiGraphics, TEXTURE, x - 15, y + 10, 193, 84, 18, 131);
            RenderScreenTooltips.renderComponentSlotTooltip(guiGraphics, TEXTURE_SLOTS, x - 15, y + 10, 193, 84, 18, 131);
        }else{
            RenderScreenTooltips.renderComponentSlotTooltip(guiGraphics, TEXTURE, x - 3, y + 10, 193, 84, 4, 131);
        }

        renderProgressArrow(guiGraphics, x, y);
        energyInfoArea.render(guiGraphics);
        fluidRenderer.render(guiGraphics, x+151, y+6, menu.blockEntity.getFluidInInputTank());
    }

    private void renderProgressArrow(GuiGraphics guiGraphics, int x, int y) {
        if(menu.isCrafting()) {
            guiGraphics.blit(ARROW, x + 141, y + 68, 0, 62, -6, -menu.getScaledProgress());
        }
        guiGraphics.blit(ARROW, x + 149, y + 68, 0, 149, -6, -menu.getLightLevel());
    }


    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        renderBackground(guiGraphics, mouseX, mouseY, delta);
        super.render(guiGraphics, mouseX, mouseY, delta);
        renderTooltip(guiGraphics, mouseX, mouseY);

        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        // Dados de status
        int[] info = menu.getDisplayInfo();
        boolean[] status = {
                info[0] == 0,  // redstone
                info[1] == 1,  // component
                info[2] == 1,  // energy
                info[4] == 1,  // slot
                info[6] >= 1,  // plants
                info[8] >= 1,  // size
                info[7] >= 1,  // light
                info[9] == 1   // villager
        };

        // Itens dos ícones
        ItemStack[] icons = {
                new ItemStack(Blocks.LEVER),
                new ItemStack(ModItems.REDSTONE_COMPONENT.get()),
                new ItemStack(Items.REDSTONE),
                new ItemStack(Items.CHEST),
                new ItemStack(Items.WHEAT_SEEDS),
                new ItemStack(Items.OAK_SIGN),
                new ItemStack(Items.LIGHT),
                new ItemStack(Items.VILLAGER_SPAWN_EGG)
        };

        // Componentes de texto
        Component[] texts = {
                Component.translatable("gui.infinity_nexus_greenhouse.redstone"),
                Component.translatable("gui.infinity_nexus_greenhouse.component"),
                Component.translatable("gui.infinity_nexus_greenhouse.energy"),
                Component.translatable("gui.infinity_nexus_greenhouse.slot"),
                Component.translatable("gui.infinity_nexus_greenhouse.plants").append(" = " + info[6]),
                Component.translatable("gui.infinity_nexus_greenhouse.size").append(" = " + menu.getData(15) + "/" + ModUtilsGreenhouse.getAreaByTier(menu.blockEntity.getTier()-1)),
                Component.translatable("gui.infinity_nexus_greenhouse.light").append(" = " + menu.getData(16)),
                Component.translatable("gui.infinity_nexus_greenhouse.villager"),
        };

        // Renderiza linhas básicas de status
        for (int i = 0; i < texts.length; i++) {
            y = renderStatusLine(guiGraphics, x, y, status[i], texts[i], icons[i],
                    i == 0 ? (status[0] ? 0xFF0000 : 0x00FF00) : 0x00FF00);
        }

        // Status do bloco vinculado
        ItemStack linkedBlock = menu.getBlockEntity().getLikedBlock();
        boolean hasLink = linkedBlock != null && !linkedBlock.isEmpty();
        y = renderStatusLine(guiGraphics, x, y, hasLink,
                Component.literal(menu.getBlockEntity().getHasLink()),
                hasLink ? linkedBlock : new ItemStack(ModItems.LINKING_TOOL.get()),
                hasLink ? 0x00FF00 : 0xB6FF00);

        // Status de funcionamento
        boolean isWorking = status[0] && status[1] && status[2] &&
                status[3] && status[4] && info[6] > 1 && status[7] && info[8] > 4 && info[9] !=0;
        renderStatusLine(guiGraphics, x, y, isWorking,
                Component.translatable("gui.infinity_nexus_greenhouse.working"),
                new ItemStack(Items.CRAFTING_TABLE),
                isWorking ? 0x00FF00 : 0xFF0000);
    }

    private int renderStatusLine(GuiGraphics gui, int x, int y, boolean active,
                                 Component text, ItemStack icon, int color) {
        gui.drawString(this.font,
                active ? "§f]§a✅§f[ §e" + text.getString()
                        : "§f]§4❎§f[ §4" + text.getString(),
                x + 196, y, color);
        gui.renderFakeItem(icon, x + 178, y - 4);
        return y + 15;
    }

    private void renderFluidAreaTooltips(GuiGraphics guiGraphics, int pMouseX, int pMouseY, int x, int y,
                                         FluidStack stack, int offsetX, int offsetY, FluidTankRenderer renderer) {
        if(isMouseAboveArea(pMouseX, pMouseY, x, y, offsetX, offsetY, renderer)) {
            guiGraphics.renderTooltip(this.font, renderer.getTooltip(stack, TooltipFlag.Default.NORMAL),
                    Optional.empty(), pMouseX - x, pMouseY - y);
        }
    }
    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, FluidTankRenderer renderer) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, renderer.getWidth(), renderer.getHeight());
    }
    private boolean isMouseAboveArea(int pMouseX, int pMouseY, int x, int y, int offsetX, int offsetY, int width, int height) {
        return MouseUtil.isMouseOver(pMouseX, pMouseY, x + offsetX, y + offsetY, width, height);
    }
}