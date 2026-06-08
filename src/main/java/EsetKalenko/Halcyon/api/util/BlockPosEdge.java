package EsetKalenko.Halcyon.api.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record BlockPosEdge(BlockPos source, BlockPos target) {
    public static final Codec<BlockPosEdge> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            BlockPos.CODEC.fieldOf("source").forGetter((edge) -> edge.source),
            BlockPos.CODEC.fieldOf("target").forGetter((edge) -> edge.target)
    ).apply(instance, BlockPosEdge::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockPosEdge> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, BlockPosEdge::source,
            BlockPos.STREAM_CODEC, BlockPosEdge::target,
            BlockPosEdge::new
    );

    public double distSqr() {
        return this.source.distSqr(this.target);
    }
}
