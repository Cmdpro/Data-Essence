package com.cmdpro.datanessence.shaders;

import com.cmdpro.databank.rendering.ShaderTypeHandler;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;

public class DataNEssenceRenderTypes extends RenderType {
    public DataNEssenceRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }
    public static final RenderStateShard.ShaderStateShard WARPING_POINT_SHADER = new RenderStateShard.ShaderStateShard(DataNEssenceCoreShaders::getWarpingPoint);
    public static final RenderType WARPING_POINT = create("warping_point",
            DefaultVertexFormat.POSITION,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            RenderType.CompositeState.builder().setShaderState(WARPING_POINT_SHADER).createCompositeState(false));

}
