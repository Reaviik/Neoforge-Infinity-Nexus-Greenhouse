package com.Infinity.Nexus.Greenhouse.compat.jei;

import com.Infinity.Nexus.Core.utils.GetResourceLocation;
import com.Infinity.Nexus.Greenhouse.InfinityNexusGreenhouse;
import com.Infinity.Nexus.Greenhouse.block.ModBlocksGreenhouse;
import com.Infinity.Nexus.Greenhouse.recipes.GreenhouseRecipe;
import com.Infinity.Nexus.Greenhouse.recipes.OutputWithChance;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GreenhouseCategory implements IRecipeCategory<GreenhouseRecipe> {

    public static final ResourceLocation UID = GetResourceLocation.withNamespaceAndPath(InfinityNexusGreenhouse.MOD_ID, "greenhouse");
    public static final ResourceLocation TEXTURE = GetResourceLocation.withNamespaceAndPath(InfinityNexusGreenhouse.MOD_ID, "textures/gui/jei/greenhouse_gui_jei.png");

    public static final RecipeType<GreenhouseRecipe> GREENHOUSE_TYPE = new RecipeType<>(UID, GreenhouseRecipe.class);

    private final IDrawable background;
    private final IDrawable icon;
    private final BlockRenderDispatcher blockRenderer;

    public GreenhouseCategory(IGuiHelper helper) {
        this.background = helper.createDrawable(TEXTURE, 0, 0, 176, 88);
        this.icon = helper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(ModBlocksGreenhouse.GREENHOUSE.get()));
        this.blockRenderer = Minecraft.getInstance().getBlockRenderer();
    }

    @Override
    public RecipeType<GreenhouseRecipe> getRecipeType() {
        return GREENHOUSE_TYPE;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("block.infinity_nexus_greenhouse.greenhouse");
    }

    @Override
    public IDrawable getBackground() {
        return this.background;
    }

    @Override
    public void draw(GreenhouseRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        Minecraft minecraft = Minecraft.getInstance();
        PoseStack poseStack = guiGraphics.pose();

        // Desenha o texto de energia
        poseStack.pushPose();
        poseStack.translate(6, 76, 0);
        guiGraphics.drawString(minecraft.font, recipe.getEnergy() + " FE", 6, 0, 0xFFFFFF, false);
        poseStack.popPose();

        // Renderiza a planta 3D
        renderPlant(recipe, poseStack, 66, 28, 20);
    }

    private void renderPlant(GreenhouseRecipe recipe, PoseStack poseStack, int x, int y, int size) {
        Block plantBlock = BuiltInRegistries.BLOCK.get(ResourceLocation.parse(recipe.getPlant()));
        BlockState farmLand = Blocks.FARMLAND.defaultBlockState();
        if (plantBlock != null) {
            BlockState plantState = plantBlock.defaultBlockState();

            long gameTime = Minecraft.getInstance().level.getGameTime();
            int animationStage = (int)((gameTime % 40) / 10);

            BlockState animatedState = createAnimatedState(plantState, Objects.requireNonNull(recipe.getProperties("property")), animationStage);
            poseStack.pushPose();
            poseStack.translate(x, y, 20);
            poseStack.scale(size, -size, size);
            poseStack.mulPose(Axis.XP.rotationDegrees(20));
            poseStack.mulPose(Axis.YP.rotationDegrees(225));
            MultiBufferSource.BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
            poseStack.pushPose();
            poseStack.translate(0, -1.5, 0);
            blockRenderer.renderSingleBlock(
                    farmLand,
                    poseStack,
                    bufferSource,
                    0xF000F0,
                    OverlayTexture.NO_OVERLAY
            );
            poseStack.popPose();

            poseStack.pushPose();
            poseStack.translate(0, -0.6, 0);//0
            blockRenderer.renderSingleBlock(
                    animatedState,
                    poseStack,
                    bufferSource,
                    0xF000F0,
                    OverlayTexture.NO_OVERLAY
            );
            poseStack.popPose();
            bufferSource.endBatch();
            poseStack.popPose();
        }
    }

    private BlockState createAnimatedState(BlockState originalState, String property, int stage) {
        String[] parts = property.split("=");
        if (parts.length != 2) return originalState;

        Property<?> blockProperty = originalState.getBlock().getStateDefinition().getProperty(parts[0]);
        if (blockProperty == null) return originalState;

        if (blockProperty instanceof IntegerProperty intProp) {
            int maxValue = intProp.getPossibleValues().stream().max(Integer::compare).orElse(0);
            int animatedValue = stage * (maxValue + 1) / 4;
            return originalState.setValue(intProp, Math.min(animatedValue, maxValue));
        }

        else if (blockProperty instanceof BooleanProperty boolProp) {
            return originalState.setValue(boolProp, stage % 2 == 0);
        }

        return originalState;
    }

    @Override
    public IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GreenhouseRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.CATALYST, 8, 29)
                .addIngredients(recipe.getTier());

        try {
            ItemStack input = BuiltInRegistries.ITEM.get(ResourceLocation.parse(recipe.getPlant())).getDefaultInstance();
            builder.addSlot(RecipeIngredientRole.INPUT, 16, 47).addItemStack(input);
        } catch (Exception ignored) {}

        int[] secondarySlotX = {80, 98, 116, 80, 98, 116, 80, 98};
        int[] secondarySlotY = {11, 11, 11, 29, 29, 29, 48, 48};

        List<OutputWithChance> sortedOutputs = new ArrayList<>(recipe.getOutputs());

        for (int i = 0; i < sortedOutputs.size() && i < secondarySlotX.length; i++) {
            OutputWithChance output = sortedOutputs.get(i);
            ItemStack stack = output.stack();
            ItemLore lore = new ItemLore(List.of(
                    Component.translatable("tooltip.infinity_nexus.chance").append("§e" + (int) (output.chance() * 100) + "%"))
            );
            stack.set(DataComponents.LORE, lore);
            builder.addSlot(RecipeIngredientRole.OUTPUT, secondarySlotX[i], secondarySlotY[i])
                    .addItemStack(stack);
        }
    }
}