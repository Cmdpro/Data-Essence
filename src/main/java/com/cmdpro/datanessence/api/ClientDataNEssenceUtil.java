package com.cmdpro.datanessence.api;

import com.cmdpro.datanessence.ClientModEvents;
import com.cmdpro.datanessence.hiddenblocks.HiddenBlock;
import com.cmdpro.datanessence.hiddenblocks.HiddenBlocksManager;
import com.cmdpro.datanessence.moddata.ClientPlayerUnlockedEntries;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import com.cmdpro.datanessence.toasts.CriticalDataToast;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.ChunkRenderDispatcher;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import team.lodestar.lodestone.handlers.RenderHandler;
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;

import java.awt.*;
import java.util.Map;

public class ClientDataNEssenceUtil {
    public static void drawSphere(VFXBuilders.WorldVFXBuilder builder, PoseStack stack, Color color, float alpha, float radius, int longs, int lats) {
        builder.setColor(color).setAlpha(alpha).setRenderType(LodestoneRenderTypeRegistry.TRANSPARENT_TEXTURE.applyWithModifierAndCache(new ResourceLocation("textures/misc/white.png"), b -> b.replaceVertexFormat(VertexFormat.Mode.TRIANGLES).setCullState(LodestoneRenderTypeRegistry.NO_CULL))).renderSphere(stack, radius, longs, lats);
    }
    public static BlockState getHiddenBlock(Block block) {
        for (HiddenBlock i : HiddenBlocksManager.blocks.values()) {
            if (i.originalBlock == null || i.hiddenAs == null || i.entry == null) {
                continue;
            }
            if (i.originalBlock.equals(block)) {
                if (!ClientPlayerUnlockedEntries.getUnlocked().contains(i.entry)) {
                    return i.hiddenAs;
                }
                break;
            }
        }
        return null;
    }
    public static void drawLine(ParticleOptions particle, Vec3 point1, Vec3 point2, Level level, double space) {
        double distance = point1.distanceTo(point2);
        Vec3 vector = point2.subtract(point1).normalize().multiply(space, space, space);
        double length = 0;
        for (Vec3 point = point1; length < distance; point = point.add(vector)) {
            level.addParticle(particle, point.x, point.y, point.z, 0, 0, 0);
            length += space;
        }
    }
    public static void unlockedCriticalData(Entry entry) {
        Minecraft.getInstance().getToasts().addToast(new CriticalDataToast(entry));
        ClientModEvents.progressionProcessor.setActive(true);
    }
    public static void updateWorld() {
        for (ChunkRenderDispatcher.RenderChunk i : Minecraft.getInstance().levelRenderer.viewArea.chunks) {
            i.setDirty(false);
        }
    }
}
