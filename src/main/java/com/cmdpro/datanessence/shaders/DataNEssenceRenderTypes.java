package com.cmdpro.datanessence.shaders;

import com.cmdpro.databank.rendering.RenderTypeHandler;
import com.cmdpro.databank.rendering.ShaderTypeHandler;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;

import java.util.OptionalDouble;

public class DataNEssenceRenderTypes extends RenderType {
    public DataNEssenceRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }
    public static final RenderStateShard.ShaderStateShard WARPING_POINT_SHADER = new RenderStateShard.ShaderStateShard(DataNEssenceCoreShaders::getWarpingPoint);
    public static final RenderStateShard.ShaderStateShard WIRES_SHADER = new RenderStateShard.ShaderStateShard(DataNEssenceCoreShaders::getWires);
    public static final RenderType WARPING_POINT = RenderTypeHandler.registerRenderType(create("warping_point",
            DefaultVertexFormat.POSITION,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            RenderType.CompositeState.builder().setShaderState(WARPING_POINT_SHADER).createCompositeState(false)), false);
    public static final RenderType WIRES = RenderTypeHandler.registerRenderType(create("wires",
           DefaultVertexFormat.POSITION_COLOR_NORMAL,
           VertexFormat.Mode.LINES,
            1536,
            false,
            false,
           RenderType.CompositeState.builder()
                    .setShaderState(WIRES_SHADER)
                    .setLineState(new RenderStateShard.LineStateShard(OptionalDouble.empty()))
                    .setLayeringState(RenderStateShard.VIEW_OFFSET_Z_LAYERING)
                    .setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY)
                    .setOutputState(RenderStateShard.ITEM_ENTITY_TARGET)
                    .setWriteMaskState(RenderStateShard.COLOR_DEPTH_WRITE)
                    .setCullState(RenderStateShard.NO_CULL)
                    .createCompositeState(false)), false);

}
