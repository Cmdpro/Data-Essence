package com.cmdpro.datanessence;

import com.cmdpro.databank.music.MusicSystem;
import com.cmdpro.databank.rendering.ColorUtil;
import com.cmdpro.databank.shaders.PostShaderInstance;
import com.cmdpro.databank.shaders.PostShaderManager;
import com.cmdpro.datanessence.entity.AncientSentinel;
import com.cmdpro.datanessence.entity.EssenceSlashProjectile;
import com.cmdpro.datanessence.fluid.Genderfluid;
import com.cmdpro.datanessence.item.blockitem.*;
import com.cmdpro.datanessence.particle.*;
import com.cmdpro.datanessence.registry.*;
import com.cmdpro.datanessence.renderers.block.*;
import com.cmdpro.datanessence.renderers.entity.AncientSentinelRenderer;
import com.cmdpro.datanessence.renderers.entity.EmptyEntityRenderer;
import com.cmdpro.datanessence.renderers.entity.EssenceSlashRenderer;
import com.cmdpro.datanessence.renderers.item.*;
import com.cmdpro.datanessence.renderers.layer.HornsLayer;
import com.cmdpro.datanessence.renderers.layer.TailLayer;
import com.cmdpro.datanessence.renderers.layer.WingsLayer;
import com.cmdpro.datanessence.screen.*;
import com.cmdpro.datanessence.shaders.GenderEuphoriaShader;
import com.cmdpro.datanessence.shaders.ProgressionShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.JukeboxPlayable;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.JukeboxSongs;
import net.minecraft.world.item.component.ItemContainerContents;
import net.minecraft.world.level.block.entity.JukeboxBlockEntity;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.event.RegisterParticleProvidersEvent;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.items.ComponentItemHandler;
import net.neoforged.neoforge.items.IItemHandler;
import org.joml.SimplexNoise;

import java.awt.*;
import java.util.Optional;

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
        event.registerBlockEntityRenderer(BlockEntityRegistry.ENTROPIC_PROCESSOR.get(), EntropicProcessorRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.FLUID_MIXER.get(), FluidMixerRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.AREKKO.get(), ArekkoRenderer::new);
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
            ClientHooks.registerLayerDefinition(AncientSentinelRenderer.ancientSentinelLocation, AncientSentinelRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(AutoFabricatorRenderer.modelLocation, AutoFabricatorRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(AutoFabricatorItemRenderer.autoFabricatorLocation, AutoFabricatorItemRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(ChargerRenderer.chargerLocation, ChargerRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(ChargerItemRenderer.chargerLocation, ChargerItemRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(EssencePointItemRenderer.modelLocation, EssencePointItemRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(EssencePointRenderer.modelLocation, EssencePointRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(LunarEssencePointItemRenderer.modelLocation, LunarEssencePointItemRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(LunarEssencePointRenderer.modelLocation, LunarEssencePointRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(NaturalEssencePointItemRenderer.modelLocation, NaturalEssencePointItemRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(NaturalEssencePointRenderer.modelLocation, NaturalEssencePointRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(ExoticEssencePointItemRenderer.modelLocation, ExoticEssencePointItemRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(ExoticEssencePointRenderer.modelLocation, ExoticEssencePointRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(ItemPointItemRenderer.modelLocation, ItemPointItemRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(ItemPointRenderer.modelLocation, ItemPointRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(FluidPointItemRenderer.modelLocation, FluidPointItemRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(FluidPointRenderer.modelLocation, FluidPointRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(FabricatorItemRenderer.modelLocation, FabricatorItemRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(FabricatorRenderer.modelLocation, FabricatorRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(InfuserItemRenderer.modelLocation, InfuserItemRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(InfuserRenderer.modelLocation, InfuserRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(EntropicProcessorRenderer.modelLocation, EntropicProcessorRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(EntropicProcessorItemRenderer.modelLocation, EntropicProcessorItemRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(FluidMixerRenderer.modelLocation, FluidMixerRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(FluidMixerItemRenderer.modelLocation, FluidMixerItemRenderer.Model::createLayer);
            ClientHooks.registerLayerDefinition(ArekkoRenderer.modelLocation, ArekkoRenderer.Model::createLayer);
        });

        event.enqueueWork(() -> {
            ItemProperties.register(ItemRegistry.MUSIC_DISC_PLAYER.get(), ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "playing"), (stack, level, entity, seed) -> {
                if (stack.has(DataComponentRegistry.PLAYING_MUSIC)) {
                    return 1;
                }
                return 0;
            });
        });

        progressionShader = new ProgressionShader();
        PostShaderManager.addShader(progressionShader);
        genderEuphoriaShader = new GenderEuphoriaShader();
        PostShaderManager.addShader(genderEuphoriaShader);
    }
    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(Genderfluid.EXTENSIONS, FluidRegistry.GENDERFLUID_TYPE.get());

        event.registerItem(AutoFabricatorItem.extensions(), BlockRegistry.AUTO_FABRICATOR.get().asItem());
        event.registerItem(ChargerItem.extensions(), BlockRegistry.CHARGER.get().asItem());
        event.registerItem(EntropicProcessorItem.extensions(), BlockRegistry.ENTROPIC_PROCESSOR.get().asItem());
        event.registerItem(EssencePointItem.extensions(), BlockRegistry.ESSENCE_POINT.get().asItem());
        event.registerItem(ExoticEssencePointItem.extensions(), BlockRegistry.EXOTIC_ESSENCE_POINT.get().asItem());
        event.registerItem(FabricatorItem.extensions(), BlockRegistry.FABRICATOR.get().asItem());
        event.registerItem(FluidPointItem.extensions(), BlockRegistry.FLUID_POINT.get().asItem());
        event.registerItem(InfuserItem.extensions(), BlockRegistry.INFUSER.get().asItem());
        event.registerItem(ItemPointItem.extensions(), BlockRegistry.ITEM_POINT.get().asItem());
        event.registerItem(LunarEssencePointItem.extensions(), BlockRegistry.LUNAR_ESSENCE_POINT.get().asItem());
        event.registerItem(NaturalEssencePointItem.extensions(), BlockRegistry.NATURAL_ESSENCE_POINT.get().asItem());
        event.registerItem(FluidMixerItem.extensions(), BlockRegistry.FLUID_MIXER.get().asItem());
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
        event.register(MenuRegistry.MINERAL_PURIFICATION_CHAMBER_MENU.get(), MineralPurificationChamberScreen::new);
        event.register(MenuRegistry.ITEM_FILTER_MENU.get(), ItemFilterScreen::new);
        event.register(MenuRegistry.MUSIC_DISC_PLAYER_MENU.get(), MusicDiscPlayerScreen::new);
        event.register(MenuRegistry.FILTER_NODE_UPGRADE_MENU.get(), FilterNodeUpgradeScreen::new);
        event.register(MenuRegistry.METAL_SHAPER_MENU.get(), MetalShaperScreen::new);
    }
    public static PostShaderInstance progressionShader;
    public static PostShaderInstance genderEuphoriaShader;
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
    @SubscribeEvent
    public static void registerBlockColorHandlers(RegisterColorHandlersEvent.Block event) {
        event.register((pState, pLevel, pPos, pTintIndex) -> {
            if (pPos != null) {
                return ColorUtil.blendColors(new Color(0x0CB3FF), new Color(0xFF49C1), (SimplexNoise.noise(pPos.getX()/25f, pPos.getY()/25f, pPos.getZ()/25f)+1f)/2f).getRGB();
            } else {
                float blendAmount = Minecraft.getInstance().levelRenderer.getTicks()+Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
                return ColorUtil.blendColors(new Color(0x0CB3FF), new Color(0xFF49C1), (float) (Math.sin(blendAmount/(360f/5f))+1f)/2f).getRGB();
            }
        }, BlockRegistry.SPIRE_GLASS.get());
        event.register((pState, pLevel, pPos, pTintIndex) -> {
            if (pPos != null) {
                return Color.getHSBColor(((SimplexNoise.noise(pPos.getX()/25f, pPos.getY()/25f, pPos.getZ()/25f)+1f)/2f), 1f, 1f).getRGB();
            } else {
                float blendAmount = Minecraft.getInstance().levelRenderer.getTicks()+Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
                return Color.getHSBColor((float) (Math.sin(blendAmount/(360f/5f))+1f)/2f, 1f, 1f).getRGB();
            }
        }, BlockRegistry.CRYSTALLINE_LEAVES.get());
    }
    @SubscribeEvent
    public static void registerItemColorHandlers(RegisterColorHandlersEvent.Item event) {
        event.register((pStack, pTintIndex) -> {
            float blendAmount = Minecraft.getInstance().levelRenderer.getTicks()+Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
            return ColorUtil.blendColors(new Color(0x0CB3FF), new Color(0xFF49C1), (float) (Math.sin(blendAmount/(360f/5f))+1f)/2f).getRGB();
        }, BlockRegistry.SPIRE_GLASS.get());
        event.register((pStack, pTintIndex) -> {
            float blendAmount = Minecraft.getInstance().levelRenderer.getTicks()+Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
            return Color.getHSBColor((float) (Math.sin(blendAmount/(360f/5f))+1f)/2f, 1f, 1f).getRGB();
        }, BlockRegistry.CRYSTALLINE_LEAVES.get());
    }
}
