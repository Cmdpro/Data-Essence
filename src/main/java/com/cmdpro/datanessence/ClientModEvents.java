package com.cmdpro.datanessence;

import com.cmdpro.datanessence.entity.AncientSentinel;
import com.cmdpro.datanessence.entity.EssenceSlashProjectile;
import com.cmdpro.datanessence.particle.*;
import com.cmdpro.datanessence.registry.*;
import com.cmdpro.datanessence.renderers.block.*;
import com.cmdpro.datanessence.renderers.entity.AncientSentinelRenderer;
import com.cmdpro.datanessence.renderers.entity.EmptyEntityRenderer;
import com.cmdpro.datanessence.renderers.entity.EssenceSlashRenderer;
import com.cmdpro.datanessence.renderers.layer.HornsLayer;
import com.cmdpro.datanessence.renderers.layer.TailLayer;
import com.cmdpro.datanessence.renderers.layer.WingsLayer;
import com.cmdpro.datanessence.screen.*;
import com.cmdpro.datanessence.shaders.ProgressionShader;
import com.cmdpro.datanessence.shaders.system.ShaderInstance;
import com.cmdpro.datanessence.shaders.system.ShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
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
        event.registerBlockEntityRenderer(BlockEntityRegistry.CHARGER.get(), ChargerRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.LASER_EMITTER.get(), LaserEmitterRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.AUTO_FABRICATOR.get(), AutoFabricatorRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.FLUID_TANK.get(), FluidTankRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.ESSENCE_BATTERY.get(), EssenceBatteryRenderer::new);
    }
    @SubscribeEvent
    public static void addLayers(EntityRenderersEvent.AddLayers event) {
        for (PlayerSkin.Model i : event.getSkins()) {
            LivingEntityRenderer skin = event.getSkin(i);
            skin.addLayer(new WingsLayer(skin, event.getEntityModels()));
            skin.addLayer(new HornsLayer(skin, event.getEntityModels()));
            skin.addLayer(new TailLayer(skin, event.getEntityModels()));
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
        EntityRenderers.register(EntityRegistry.ANCIENT_SENTINEL_PROJECTILE.get(), EmptyEntityRenderer::new);
        EntityRenderers.register(EntityRegistry.ESSENCE_SLASH_PROJECTILE.get(), EssenceSlashRenderer::new);

        event.enqueueWork(() -> {
            ClientHooks.registerLayerDefinition(WingsLayer.wingsLocation, WingsLayer.WingsModel::createLayer);
            ClientHooks.registerLayerDefinition(HornsLayer.hornsLocation, HornsLayer.HornsModel::createLayer);
            ClientHooks.registerLayerDefinition(TailLayer.tailLocation, TailLayer.TailModel::createLayer);
        });

        progressionShader = new ProgressionShader();
        ShaderManager.addShader(progressionShader);


    }
    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(MenuRegistry.FABRICATOR_MENU.get(), FabricatorScreen::new);
        event.register(MenuRegistry.ESSENCE_BURNER_MENU.get(), EssenceBurnerScreen::new);
        event.register(MenuRegistry.INFUSER_MENU.get(), InfuserScreen::new);
        event.register(MenuRegistry.CHARGER_MENU.get(), ChargerScreen::new);
        event.register(MenuRegistry.LASER_EMITTER_MENU.get(), LaserEmitterScreen::new);
        event.register(MenuRegistry.AUTO_FABRICATOR_MENU.get(), AutoFabricatorScreen::new);
        event.register(MenuRegistry.FLUID_BOTTLER_MENU.get(), FluidBottlerScreen::new);
        event.register(MenuRegistry.ENTROPIC_PROCESSOR_MENU.get(), EntropicProcessorScreen::new);
        event.register(MenuRegistry.ESSENCE_FURNACE_MENU.get(), EssenceFurnaceScreen::new);
        event.register(MenuRegistry.SYNTHESIS_CHAMBER_MENU.get(), SynthesisChamberScreen::new);
        event.register(MenuRegistry.FLUID_MIXER_MENU.get(), FluidMixerScreen::new);
        event.register(MenuRegistry.ENTICING_LURE_MENU.get(), EnticingLureScreen::new);
    }
    public static ShaderInstance progressionShader;
    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.ESSENCE_SPARKLE.get(),
                EssenceSparkleParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.CIRCLE.get(),
                CircleParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.CIRCLE_ADDITIVE.get(),
                CircleParticleAdditive.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.RHOMBUS.get(),
                RhombusParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.SMALL_CIRCLE.get(),
                SmallCircleParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.MOTE.get(),
                MoteParticle.Provider::new);
    }
}
