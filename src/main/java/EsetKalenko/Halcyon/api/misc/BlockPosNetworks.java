package EsetKalenko.Halcyon.api.misc;

import EsetKalenko.Halcyon.api.util.BlockPosGraph;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

public class BlockPosNetworks {
    public @NotNull BlockPosGraph graph;

    public static final Codec<BlockPosNetworks> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
        BlockPosGraph.CODEC.fieldOf("graph").forGetter((networks) -> networks.graph)
    ).apply(instance, BlockPosNetworks::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockPosNetworks> STREAM_CODEC = StreamCodec.composite(
            BlockPosGraph.STREAM_CODEC, network -> network.graph,
            BlockPosNetworks::new
    );

    public BlockPosNetworks(@NotNull BlockPosGraph graph) {
        this.graph = graph;
    }
}
