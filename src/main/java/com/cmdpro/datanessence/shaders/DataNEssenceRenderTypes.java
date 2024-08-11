package com.cmdpro.datanessence.shaders;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;

public class DataNEssenceRenderTypes extends RenderType {
    public DataNEssenceRenderTypes(String pName, VertexFormat pFormat, VertexFormat.Mode pMode, int pBufferSize, boolean pAffectsCrumbling, boolean pSortOnUpload, Runnable pSetupState, Runnable pClearState) {
        super(pName, pFormat, pMode, pBufferSize, pAffectsCrumbling, pSortOnUpload, pSetupState, pClearState);
    }
    public static final RenderType WARPING_POINT = create("warping_point",
            DefaultVertexFormat.POSITION,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            RenderType.CompositeState.builder().setShaderState(DataNEssenceCoreShaders.WARPINGPOINT.shard).createCompositeState(false));
    public static final RenderType HOLOGRAM_BLOCK = create("hologram_block",
            DefaultVertexFormat.BLOCK,
            VertexFormat.Mode.QUADS,
            4194304,
            true,
            false,
            RenderType.CompositeState.builder()
                    .setLightmapState(LIGHTMAP)
                    .setShaderState(DataNEssenceCoreShaders.HOLOGRAM_BLOCK.shard)
                    .setTextureState(BLOCK_SHEET_MIPPED)
                    .createCompositeState(true));
    public static final RenderType TRANSPARENT = create("transparent",
            DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP,
            VertexFormat.Mode.QUADS,
            256,
            false,
            false,
            RenderType.CompositeState.builder().setShaderState(RenderStateShard.POSITION_COLOR_TEX_LIGHTMAP_SHADER).createCompositeState(false));
}
