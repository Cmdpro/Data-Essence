package com.cmdpro.datanessence.client.gui;

import com.cmdpro.databank.rendering.RenderHandler;
import com.cmdpro.databank.shaders.PostShaderManager;
import com.cmdpro.datanessence.data.pinging.PingableStructure;
import com.cmdpro.datanessence.networking.packet.s2c.PingStructures;
import com.eliotlash.mclib.math.functions.limit.Min;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.*;

import java.util.HashMap;

public class PingsGuiLayer implements LayeredDraw.Layer {
    public static HashMap<PingStructures.StructurePing, Integer> pings = new HashMap<>();
    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        for (var i : pings.entrySet()) {
            PingStructures.StructurePing ping = i.getKey();
            Vec3 pos = ping.pos.getCenter();
            pos = new Vec3(pos.x, Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().y, pos.z);
            PingableStructure structure = ping.getPingableStructure();
            PingableStructure.PingableStructureIcon icon = structure.icon;

            int width = guiGraphics.guiWidth();
            int height = guiGraphics.guiHeight();

            Vec2 screenPos = worldPosToTexCoord(pos.toVector3f());
            int x = (int)(screenPos.x*width) - 8;
            int y = (int)(screenPos.y*height) - 8;
            guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
            guiGraphics.blit(icon.texture, x, y, icon.u, icon.v, 16, 16);
        }
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }
    public static Vec2 worldPosToTexCoord(Vector3f worldPos) {
        Matrix4f viewMat = PostShaderManager.viewStackMatrix;
        Matrix4f projMat = RenderSystem.getProjectionMatrix();

        Vector3f localPos = new Vector3f(worldPos);
        localPos.sub(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().toVector3f());

        Vector4f pos = new Vector4f(localPos, 0);
        pos.mul(viewMat);
        pos.mul(projMat);
        //pos.div(pos.w);

        return new Vec2((pos.x+1f)/2f, (pos.y+1f)/2f);
    }
}
