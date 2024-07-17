package com.cmdpro.datanessence;

import com.cmdpro.datanessence.entity.AncientSentinel;
import com.cmdpro.datanessence.particle.EssenceSparkleParticle;
import com.cmdpro.datanessence.registry.*;
import com.cmdpro.datanessence.renderers.block.*;
import com.cmdpro.datanessence.renderers.entity.AncientSentinelRenderer;
import com.cmdpro.datanessence.renderers.entity.EmptyEntityRenderer;
import com.cmdpro.datanessence.renderers.layer.HornsLayer;
import com.cmdpro.datanessence.renderers.layer.WingsLayer;
import com.cmdpro.datanessence.screen.InfuserScreen;
import com.cmdpro.datanessence.shaders.ProgressionShader;
import com.cmdpro.datanessence.screen.EssenceBurnerScreen;
import com.cmdpro.datanessence.screen.FabricatorScreen;
import com.cmdpro.datanessence.shaders.system.ShaderInstance;
import com.cmdpro.datanessence.shaders.system.ShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.resources.PlayerSkin;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;

@EventBusSubscriber(value = Dist.CLIENT, modid = DataNEssence.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerRenderers(final EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(BlockEntityRegistry.FABRICATOR.get(), FabricatorRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.ESSENCE_POINT.get(), EssencePointRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.LUNAR_ESSENCE_POINT.get(), LunarEssencePointRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.NATURAL_ESSENCE_POINT.get(), NaturalEssencePointRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.EXOTIC_ESSENCE_POINT.get(), ExoticEssencePointRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.ITEM_POINT.get(), ItemPointRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.FLUID_POINT.get(), FluidPointRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.INFUSER.get(), InfuserRenderer::new);
    }
    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model i : event.getSkins()) {
            LivingEntityRenderer skin = event.getSkin(i);
            skin.addLayer(new WingsLayer(skin, event.getEntityModels()));
            skin.addLayer(new HornsLayer(skin, event.getEntityModels()));
        }
    }
    @SubscribeEvent
    public static void doSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(EntityRegistry.ESSENCE_BOMB.get(), ThrownItemRenderer::new);
        EntityRenderers.register(EntityRegistry.LUNAR_ESSENCE_BOMB.get(), ThrownItemRenderer::new);
        EntityRenderers.register(EntityRegistry.NATURAL_ESSENCE_BOMB.get(), ThrownItemRenderer::new);
        EntityRenderers.register(EntityRegistry.EXOTIC_ESSENCE_BOMB.get(), ThrownItemRenderer::new);
        EntityRenderers.register(EntityRegistry.BLACK_HOLE.get(), EmptyEntityRenderer::new);
        EntityRenderers.register(EntityRegistry.ANCIENT_SENTINEL.get(), AncientSentinelRenderer::new);

        event.enqueueWork(() -> {
            ClientHooks.registerLayerDefinition(WingsLayer.wingsLocation, WingsLayer.WingsModel::createLayer);
            ClientHooks.registerLayerDefinition(HornsLayer.hornsLocation, HornsLayer.HornsModel::createLayer);
        });

        progressionShader = new ProgressionShader();
        ShaderManager.addShader(progressionShader);


    }
    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MenuRegistry.FABRICATOR_MENU.get(), FabricatorScreen::new);
        event.register(MenuRegistry.ESSENCE_BURNER_MENU.get(), EssenceBurnerScreen::new);
        event.register(MenuRegistry.INFUSER_MENU.get(), InfuserScreen::new);
    }
    public static ShaderInstance progressionShader;
    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.ESSENCE_SPARKLE.get(),
                EssenceSparkleParticle.Provider::new);
    }
}
