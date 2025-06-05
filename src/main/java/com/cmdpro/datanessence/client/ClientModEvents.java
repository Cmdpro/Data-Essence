package com.cmdpro.datanessence.client;

import com.cmdpro.databank.ClientDatabankUtils;
import com.cmdpro.databank.DatabankUtils;
import com.cmdpro.databank.rendering.ColorUtil;
import com.cmdpro.databank.shaders.PostShaderInstance;
import com.cmdpro.databank.shaders.PostShaderManager;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.item.ItemDecorators;
import com.cmdpro.datanessence.api.item.ItemEssenceContainer;
import com.cmdpro.datanessence.api.util.client.AnimatedBlockItemUtil;
import com.cmdpro.datanessence.client.gui.PingsGuiLayer;
import com.cmdpro.datanessence.client.renderers.entity.LunarStrikeRenderer;
import com.cmdpro.datanessence.client.shaders.PingShader;
import com.cmdpro.datanessence.client.shaders.MachineOutputShader;
import com.cmdpro.datanessence.fluid.Genderfluid;
import com.cmdpro.datanessence.integration.DataNEssenceIntegration;
import com.cmdpro.datanessence.integration.mekanism.ChemicalNodeRenderer;
import com.cmdpro.datanessence.client.particle.*;
import com.cmdpro.datanessence.item.equipment.GrapplingHook;
import com.cmdpro.datanessence.registry.*;
import com.cmdpro.datanessence.client.renderers.block.*;
import com.cmdpro.datanessence.client.renderers.entity.AncientSentinelRenderer;
import com.cmdpro.datanessence.client.renderers.entity.EmptyEntityRenderer;
import com.cmdpro.datanessence.client.renderers.entity.EssenceSlashRenderer;
import com.cmdpro.datanessence.client.renderers.layer.HornsLayer;
import com.cmdpro.datanessence.client.renderers.layer.TailLayer;
import com.cmdpro.datanessence.client.renderers.layer.WingsLayer;
import com.cmdpro.datanessence.screen.*;
import com.cmdpro.datanessence.client.shaders.GenderEuphoriaShader;
import com.cmdpro.datanessence.client.shaders.ProgressionShader;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.renderer.item.ItemPropertyFunction;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.ClientHooks;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import org.joml.SimplexNoise;

import java.awt.*;

import static com.cmdpro.datanessence.integration.DataNEssenceIntegration.hasOpalescence;

@EventBusSubscriber(value = Dist.CLIENT, modid = DataNEssence.MOD_ID, bus = EventBusSubscriber.Bus.MOD)
public class ClientModEvents {
    @SubscribeEvent
    public static void registerGuiLayers(RegisterGuiLayersEvent event) {
        event.registerAboveAll(DataNEssence.locate("pings"), new PingsGuiLayer());
    }
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
        event.registerBlockEntityRenderer(BlockEntityRegistry.ENTICING_LURE.get(), EnticingLureRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.METAL_SHAPER.get(), MetalShaperRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.INDUSTRIAL_PLANT_SIPHON.get(), IndustrialPlantSiphonRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.CRYOCHAMBER.get(), CryochamberRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.CHEMICAL_NODE.get(), ChemicalNodeRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.DRYING_TABLE.get(), DryingTableRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.ESSENCE_BRIDGE.get(), EssenceBridgeRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.ENDER_PEARL_CAPTURE.get(), EnderPearlCaptureRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.ENDER_PEARL_RELAY.get(), EnderPearlRelayRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.ENDER_PEARL_DESTINATION.get(), EnderPearlDestinationRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.STRUCTURE_PROTECTOR.get(), StructureProtectorRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.ESSENCE_DERIVATION_SPIKE.get(), EssenceDerivationSpikeRenderer::new);
        event.registerBlockEntityRenderer(BlockEntityRegistry.LUNAR_ESSENCE_BATTERY.get(), LunarEssenceBatteryRenderer::new);
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
        EntityRenderers.register(EntityRegistry.LUNAR_STRIKE.get(), LunarStrikeRenderer::new);

        event.enqueueWork(() -> {
            ItemProperties.register(ItemRegistry.MUSIC_DISC_PLAYER.get(), DataNEssence.locate("playing"), (stack, level, entity, seed) -> {
                if (stack.has(DataComponentRegistry.PLAYING_MUSIC)) {
                    return 1;
                }
                return 0;
            });
            ItemProperties.register(ItemRegistry.GRAPPLING_HOOK.get(), DataNEssence.locate("charged"), chargedGrapplingHookProperty);
            ItemProperties.register(ItemRegistry.GRAPPLING_HOOK.get(), DataNEssence.locate("using"), usingGrapplingHookProperty);
            ItemProperties.register(ItemRegistry.TRANS_GRAPPLING_HOOK.get(), DataNEssence.locate("charged"), chargedGrapplingHookProperty);
            ItemProperties.register(ItemRegistry.TRANS_GRAPPLING_HOOK.get(), DataNEssence.locate("using"), usingGrapplingHookProperty);
        });

        progressionShader = new ProgressionShader();
        PostShaderManager.addShader(progressionShader);
        genderEuphoriaShader = new GenderEuphoriaShader();
        PostShaderManager.addShader(genderEuphoriaShader);
        machineOutputShader = new MachineOutputShader();
        PostShaderManager.addShader(machineOutputShader);
        machineOutputShader.setActive(true);
    }
    public static PostShaderInstance progressionShader;
    public static PostShaderInstance genderEuphoriaShader;
    public static PostShaderInstance machineOutputShader;
    public static final ItemPropertyFunction usingGrapplingHookProperty = (stack, level, entity, seed) -> {
        if (entity != null) {
            if (entity.getData(AttachmentTypeRegistry.GRAPPLING_HOOK_DATA).isPresent()) {
                return 1;
            }
        }
        return 0;
    };
    public static final ItemPropertyFunction chargedGrapplingHookProperty = (stack, level, entity, seed) -> {
        if (stack.has(DataComponentRegistry.ESSENCE_STORAGE) && ItemEssenceContainer.getEssence(stack, GrapplingHook.FUEL_ESSENCE_TYPE) >= GrapplingHook.ESSENCE_COST) {
            return 1;
        }
        return 0;
    };
    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerFluidType(Genderfluid.EXTENSIONS, FluidRegistry.GENDERFLUID_TYPE.get());

        event.registerItem(AnimatedBlockItemUtil.createBasicExtensions(DataNEssence.locate("textures/block/auto-fabricator.png"), DataNEssence.locate("auto-fabricator")), BlockRegistry.AUTO_FABRICATOR.get().asItem());
        event.registerItem(AnimatedBlockItemUtil.createBasicExtensions(DataNEssence.locate("textures/block/charger.png"), DataNEssence.locate("charger")), BlockRegistry.CHARGER.get().asItem());
        event.registerItem(AnimatedBlockItemUtil.createBasicExtensions(DataNEssence.locate("textures/block/entropic_processor.png"), DataNEssence.locate("entropic_processor")), BlockRegistry.ENTROPIC_PROCESSOR.get().asItem());
        event.registerItem(AnimatedBlockItemUtil.createBasicExtensions(DataNEssence.locate("textures/block/essence_point.png"), DataNEssence.locate("essence_point")), BlockRegistry.ESSENCE_POINT.get().asItem());
        event.registerItem(AnimatedBlockItemUtil.createBasicExtensions(DataNEssence.locate("textures/block/exotic_essence_point.png"), DataNEssence.locate("essence_point")), BlockRegistry.EXOTIC_ESSENCE_POINT.get().asItem());
        event.registerItem(AnimatedBlockItemUtil.createBasicExtensions(DataNEssence.locate("textures/block/fabricator.png"), DataNEssence.locate("fabricator")), BlockRegistry.FABRICATOR.get().asItem());
        event.registerItem(AnimatedBlockItemUtil.createBasicExtensions(DataNEssence.locate("textures/block/fluid_point.png"), DataNEssence.locate("essence_point")), BlockRegistry.FLUID_POINT.get().asItem());
        event.registerItem(AnimatedBlockItemUtil.createBasicExtensions(DataNEssence.locate("textures/block/infuser.png"), DataNEssence.locate("infuser")), BlockRegistry.INFUSER.get().asItem());
        event.registerItem(AnimatedBlockItemUtil.createBasicExtensions(DataNEssence.locate("textures/block/item_point.png"), DataNEssence.locate("essence_point")), BlockRegistry.ITEM_POINT.get().asItem());
        event.registerItem(AnimatedBlockItemUtil.createBasicExtensions(DataNEssence.locate("textures/block/lunar_essence_point.png"), DataNEssence.locate("essence_point")), BlockRegistry.LUNAR_ESSENCE_POINT.get().asItem());
        event.registerItem(AnimatedBlockItemUtil.createBasicExtensions(DataNEssence.locate("textures/block/natural_essence_point.png"), DataNEssence.locate("essence_point")), BlockRegistry.NATURAL_ESSENCE_POINT.get().asItem());
        event.registerItem(AnimatedBlockItemUtil.createBasicExtensions(DataNEssence.locate("textures/block/fluid_mixer.png"), DataNEssence.locate("fluid_mixer")), BlockRegistry.FLUID_MIXER.get().asItem());
        event.registerItem(AnimatedBlockItemUtil.createBasicExtensions(DataNEssence.locate("textures/block/metal_shaper.png"), DataNEssence.locate("metal_shaper")), BlockRegistry.METAL_SHAPER.get().asItem());
        event.registerItem(AnimatedBlockItemUtil.createBasicExtensions(DataNEssence.locate("textures/block/industrial_plant_siphon.png"), DataNEssence.locate("industrial_plant_siphon")), BlockRegistry.INDUSTRIAL_PLANT_SIPHON.get().asItem());
        event.registerItem(AnimatedBlockItemUtil.createBasicExtensions(DataNEssence.locate("textures/block/chemical_node.png"), DataNEssence.locate("essence_point")), BlockRegistry.CHEMICAL_NODE.get().asItem());
        event.registerItem(AnimatedBlockItemUtil.createBasicExtensions(DataNEssence.locate("textures/block/ender_pearl_capture.png"), DataNEssence.locate("ender_pearl_capture")), BlockRegistry.ENDER_PEARL_CAPTURE.get().asItem());
        event.registerItem(AnimatedBlockItemUtil.createBasicExtensions(DataNEssence.locate("textures/block/ender_pearl_relay.png"), DataNEssence.locate("ender_pearl_relay")), BlockRegistry.ENDER_PEARL_RELAY.get().asItem());
        event.registerItem(AnimatedBlockItemUtil.createBasicExtensions(DataNEssence.locate("textures/block/ender_pearl_destination.png"), DataNEssence.locate("ender_pearl_destination")), BlockRegistry.ENDER_PEARL_DESTINATION.get().asItem());
    }
    @SubscribeEvent
    public static void registerItemDecorations(RegisterItemDecorationsEvent event) {
        event.register(ItemRegistry.ESSENCE_SWORD.get(), ItemDecorators.essenceBarDecoration);
        event.register(ItemRegistry.ILLUMINATION_ROD.get(), ItemDecorators.essenceBarDecoration);
        event.register(ItemRegistry.PRIMITIVE_ANTI_GRAVITY_PACK.get(), ItemDecorators.essenceBarDecoration);
        event.register(ItemRegistry.REPULSION_ROD.get(), ItemDecorators.essenceBarDecoration);
        event.register(ItemRegistry.GRAPPLING_HOOK.get(), ItemDecorators.essenceBarDecoration);
        event.register(ItemRegistry.TRANS_GRAPPLING_HOOK.get(), ItemDecorators.essenceBarDecoration);
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
        event.register(MenuRegistry.INDUSTRIAL_PLANT_SIPHON_MENU.get(), IndustrialPlantSiphonScreen::new);
        event.register(MenuRegistry.MELTER_MENU.get(), MelterScreen::new);
        event.register(MenuRegistry.DRYING_TABLE_MENU.get(), DryingTableScreen::new);
    }
    @SubscribeEvent
    public static void registerParticleFactories(RegisterParticleProvidersEvent event) {
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.ESSENCE_SPARKLE.get(),
                EssenceSparkleParticle.Provider::new);
        Minecraft.getInstance().particleEngine.register(ParticleRegistry.CIRCLE.get(),
                CircleParticle.Provider::new);
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
                double x = pPos.getX();
                double y = pPos.getY();
                double z = pPos.getZ();

                // y position / 14 + {z pos / 42 + 0.5} + { 0.5sin(x pos / 14)}
                float hue = (float) ((y/14)+((z/42)+0.5+0.5*Math.sin(x/14)));
                return Color.getHSBColor( hue, 0.8f, 1f ).getRGB();
            } else {
                float blendAmount = Minecraft.getInstance().levelRenderer.getTicks()+Minecraft.getInstance().getTimer().getGameTimeDeltaTicks();
                return Color.getHSBColor((float) (Math.sin(blendAmount/(360f/5f))+1f)/2f, 0.8f, 1f).getRGB();
            }
        }, BlockRegistry.CRYSTALLINE_LEAVES.get());
        event.register((pState, pLevel, pPos, pTintIndex) -> {
            Block block = ClientDatabankUtils.getHiddenBlock(pState.getBlock());
            if (block != null) {
                return Minecraft.getInstance().getBlockColors().getColor(DatabankUtils.changeBlockType(pState, block), pLevel, pPos, pTintIndex);
            }
            return 0xFFFFFFFF;
        }, BlockRegistry.TETHERGRASS.get());
        if (hasOpalescence) {
            event.register((pState, pLevel, pPos, pTintIndex) -> {
                if (pPos != null) {
                    if (pTintIndex == 0) {
                        return DataNEssenceIntegration.OpalescenseIntegration.getOpalBlockColor(pState, pLevel, pPos, pTintIndex);
                    }
                }
                return 0xFFFFFFFF;
            }, BlockRegistry.TRAVERSITE_ROAD_OPAL.get(), BlockRegistry.TRAVERSITE_ROAD_SLAB_OPAL.get(), BlockRegistry.TRAVERSITE_ROAD_STAIRS_OPAL.get());
        }
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
        if (hasOpalescence) {
            event.register((stack, tintIndex) -> tintIndex == 0 ? DataNEssenceIntegration.OpalescenseIntegration.getOpalItemColor(stack, tintIndex) : 0xFFFFFFFF, BlockRegistry.TRAVERSITE_ROAD_OPAL.get(), BlockRegistry.TRAVERSITE_ROAD_SLAB_OPAL.get(), BlockRegistry.TRAVERSITE_ROAD_STAIRS_OPAL.get());
        }
    }
}
