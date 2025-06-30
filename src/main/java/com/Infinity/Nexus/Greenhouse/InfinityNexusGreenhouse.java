package com.Infinity.Nexus.Greenhouse;

import com.Infinity.Nexus.Core.component.CoreDataComponents;
import com.Infinity.Nexus.Greenhouse.block.ModBlocksGreenhouse;
import com.Infinity.Nexus.Greenhouse.block.entity.ModBlockEntities;
import com.Infinity.Nexus.Greenhouse.component.GreenhouseDataComponents;
import com.Infinity.Nexus.Greenhouse.config.Config;
import com.Infinity.Nexus.Greenhouse.item.ModItemsGreenhouse;
import com.Infinity.Nexus.Greenhouse.networking.ModMessages;
import com.Infinity.Nexus.Greenhouse.recipes.ModRecipes;
import com.Infinity.Nexus.Greenhouse.screen.ModMenuTypes;
import com.Infinity.Nexus.Greenhouse.screen.greenhouse.GreenhouseScreen;
import com.Infinity.Nexus.Greenhouse.tab.ModTab;
import com.mojang.logging.LogUtils;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import org.slf4j.Logger;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(InfinityNexusGreenhouse.MOD_ID)
public class InfinityNexusGreenhouse
{
    public static final String MOD_ID = "infinity_nexus_greenhouse";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final String MESSAGE = "§f[§4I§5n§9fi§3ni§bty§f]: §e";


    public InfinityNexusGreenhouse(IEventBus modEventBus, ModContainer modContainer) {

        ModBlocksGreenhouse.register(modEventBus);
        ModItemsGreenhouse.register(modEventBus);

        ModTab.register(modEventBus);
        ModRecipes.register(modEventBus);

        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);

        GreenhouseDataComponents.DATA_COMPONENT_TYPES.register(modEventBus);

        modEventBus.register(ModMessages.class);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::registerScreens);


        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // ModMessages.register();
        });
    }
    private void registerScreens(RegisterMenuScreensEvent event) {
        event.register(ModMenuTypes.GREENHOUSE_MENU.get(), GreenhouseScreen::new);
    }
    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MOD_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            ItemBlockRenderTypes.setRenderLayer(ModBlocksGreenhouse.GREENHOUSE.get(), RenderType.translucent());
        }
    }
    private void setup(final FMLCommonSetupEvent event)
    {
        LOGGER.info("   §4_____§5_   __§9__________§3_   ______§b_______  __");
        LOGGER.info("  §4/_  _§5/ | / §9/ ____/  _§3/ | / /  _§b/_  __| \\/ /");
        LOGGER.info("   §4/ /§5/  |/ §9/ /_   / /§3/  |/ // /  §b/ /   \\  / ");
        LOGGER.info(" §4_/ /§5/ /|  §9/ __/ _/ /§3/ /|  // /  §b/ /    / /  ");
        LOGGER.info("§4/___§5/_/ |_§9/_/   /___§3/_/ |_/___/ §b/_/    /_/   ");
        LOGGER.info("§b     Infinity Nexus Greenhouse");

    }
}
