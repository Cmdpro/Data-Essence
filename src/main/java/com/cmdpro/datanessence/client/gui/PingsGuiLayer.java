package com.cmdpro.datanessence.client.gui;

import com.cmdpro.databank.rendering.ColorUtil;
import com.cmdpro.databank.shaders.PostShaderManager;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.data.pinging.PingableStructure;
import com.cmdpro.datanessence.data.pinging.StructurePing;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import org.joml.*;
import org.joml.Math;

import java.awt.*;
import java.util.HashMap;

public class PingsGuiLayer implements LayeredDraw.Layer {
    public static HashMap<StructurePing, Integer> pings = new HashMap<>();
    @Override
    public void render(GuiGraphics guiGraphics, DeltaTracker deltaTracker) {
        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        for (var i : pings.entrySet()) {
            StructurePing ping = i.getKey();
            Vec3 pos = ping.pos.getCenter();
            pos = new Vec3(pos.x, Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().y, pos.z);
            PingableStructure structure = ping.getPingableStructure();
            PingableStructure.PingableStructureIcon icon = structure.icon;

            int width = guiGraphics.guiWidth();
            int height = guiGraphics.guiHeight();

            Vec2 screenPos = worldPosToTexCoord(pos.toVector3f(), width, height);
            if (screenPos != null) {
                int x = (int) (screenPos.x) - 8;
                int y = (int) (screenPos.y) - 8;
                Color color1 = i.getKey().getPingableStructure().color1;
                Color color2 = i.getKey().getPingableStructure().color2;
                long gameTime = 0;
                if (Minecraft.getInstance().level != null) {
                    gameTime = Minecraft.getInstance().level.getGameTime();
                }
                Color color = ColorUtil.blendColors(color1, color2, (Math.sin((gameTime+Minecraft.getInstance().getTimer().getGameTimeDeltaTicks())/15f)+1f)/2f);
                int u = icon.u;
                int v = icon.v;
                ResourceLocation texture = icon.texture;
                if (!i.getKey().known) {
                    u = 0;
                    v = 0;
                    texture = DataNEssence.locate("textures/gui/structure_icons.png");
                }
                guiGraphics.setColor(color.getRed()/255f, color.getGreen()/255f, color.getBlue()/255f, 1.0F);
                guiGraphics.blit(texture, x, y, u, v, 16, 16);
            }
        }
        guiGraphics.setColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }
    public static Vec2 worldPosToTexCoord(Vector3f worldPos, int width, int height) {
        Matrix4f viewMat = PostShaderManager.viewStackMatrix;
        Matrix4f projMat = Minecraft.getInstance().gameRenderer.getProjectionMatrix(Minecraft.getInstance().options.fov().get());

        Vector3f localPos = new Vector3f(worldPos);
        localPos.sub(Minecraft.getInstance().gameRenderer.getMainCamera().getPosition().toVector3f());

        Vector4f pos = new Vector4f(localPos, 1);
        pos.mul(viewMat);
        pos.mul(projMat);
        if (pos.w <= 0) {
            return null;
        }
        pos.div(pos.w);

        return new Vec2((pos.x+1f)/2f*width, (1f-pos.y)/2f*height);
    }
}
