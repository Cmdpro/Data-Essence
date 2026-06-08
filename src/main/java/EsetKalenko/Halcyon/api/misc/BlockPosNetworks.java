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
        BlockPosGraphSerialization.SerializationGraph.CODEC.fieldOf("graph").xmap(BlockPosGraphSerialization.SerializationGraph::toGraph, BlockPosGraphSerialization.SerializationGraph::fromGraph).forGetter((networks) -> networks.graph)
    ).apply(instance, BlockPosNetworks::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockPosNetworks> STREAM_CODEC = StreamCodec.composite(
            BlockPosGraphSerialization.SerializationGraph.STREAM_CODEC, (BlockPosNetworks network) -> BlockPosGraphSerialization.SerializationGraph.fromGraph(network.graph),
            ser -> new BlockPosNetworks(BlockPosGraphSerialization.SerializationGraph.toGraph(ser))
    );

    public BlockPosNetworks(@NotNull BlockPosGraph graph) {
        this.graph = graph;
    }
}
