package com.cmdpro.datanessence;

import com.cmdpro.datanessence.particle.EssenceSparkleParticle;
import com.cmdpro.datanessence.registry.*;
import com.cmdpro.datanessence.screen.InfuserScreen;
import com.cmdpro.datanessence.shaders.ProgressionShader;
import com.cmdpro.datanessence.renderers.*;
import com.cmdpro.datanessence.screen.EssenceBurnerScreen;
import com.cmdpro.datanessence.screen.FabricatorScreen;
import com.cmdpro.datanessence.shaders.system.ShaderInstance;
import com.cmdpro.datanessence.shaders.system.ShaderManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = DataNEssence.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
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
    public static void drawHUD(ForgeGui gui, GuiGraphics guiGraphics, float pt, int width, int height) {

    }
    public static final IGuiOverlay HUD = ClientModEvents::drawHUD;
    @SubscribeEvent
    public static void renderHUD(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("mod_hud", HUD);
    }
    @SubscribeEvent
    public static void doSetup(FMLClientSetupEvent event) {
        EntityRenderers.register(EntityRegistry.ESSENCE_BOMB.get(), ThrownItemRenderer::new);
        EntityRenderers.register(EntityRegistry.LUNAR_ESSENCE_BOMB.get(), ThrownItemRenderer::new);
        EntityRenderers.register(EntityRegistry.NATURAL_ESSENCE_BOMB.get(), ThrownItemRenderer::new);
        EntityRenderers.register(EntityRegistry.EXOTIC_ESSENCE_BOMB.get(), ThrownItemRenderer::new);
        EntityRenderers.register(EntityRegistry.BLACK_HOLE.get(), EmptyEntityRenderer::new);

        MenuScreens.register(MenuRegistry.FABRICATOR_MENU.get(), FabricatorScreen::new);
        MenuScreens.register(MenuRegistry.ESSENCE_BURNER_MENU.get(), EssenceBurnerScreen::new);
        MenuScreens.register(MenuRegistry.INFUSER_MENU.get(), InfuserScreen::new);
        progressionShader = new ProgressionShader();
        ShaderManager.addShader(progressionShader);
    }
    public static ShaderInstance progressionShader;
    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.ESSENCE_SPARKLE.get(),
                EssenceSparkleParticle.Provider::new);
    }
}
