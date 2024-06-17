package com.cmdpro.datanessence;

import com.cmdpro.datanessence.block.ExoticEssencePoint;
import com.cmdpro.datanessence.init.*;
import com.cmdpro.datanessence.postprocessors.ProgressionProcessor;
import com.cmdpro.datanessence.renderers.*;
import com.cmdpro.datanessence.screen.EssenceBurnerMenu;
import com.cmdpro.datanessence.screen.EssenceBurnerScreen;
import com.cmdpro.datanessence.screen.FabricatorScreen;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import team.lodestar.lodestone.systems.postprocess.PostProcessHandler;
import team.lodestar.lodestone.systems.postprocess.PostProcessor;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = DataNEssence.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityInit.FABRICATOR.get(), FabricatorRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityInit.ESSENCE_POINT.get(), EssencePointRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityInit.LUNAR_ESSENCE_POINT.get(), LunarEssencePointRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityInit.NATURAL_ESSENCE_POINT.get(), NaturalEssencePointRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityInit.EXOTIC_ESSENCE_POINT.get(), ExoticEssencePointRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityInit.ITEM_POINT.get(), ItemPointRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityInit.FLUID_POINT.get(), FluidPointRenderer::new);
    }

    @SubscribeEvent
    public static void doSetup(FMLClientSetupEvent event) {
        MenuScreens.register(MenuInit.FABRICATORMENU.get(), FabricatorScreen::new);
        MenuScreens.register(MenuInit.ESSENCEBURNERMENU.get(), EssenceBurnerScreen::new);
        progressionProcessor = new ProgressionProcessor();
        progressionProcessor.setActive(false);
        PostProcessHandler.addInstance(progressionProcessor);
    }
    public static PostProcessor progressionProcessor;
    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {

    }
}
