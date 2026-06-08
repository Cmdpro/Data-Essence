package EsetKalenko.Halcyon.api.misc;

import EsetKalenko.Halcyon.api.util.BlockPosEdge;
import EsetKalenko.Halcyon.api.util.BlockPosGraph;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.ArrayList;
import java.util.List;

public class BlockPosGraphSerialization {
    public static class SerializationGraph {
        public List<BlockPos> vertices;
        public List<BlockPosEdge> edges;

        public static final Codec<SerializationGraph> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                BlockPos.CODEC.listOf().fieldOf("vertices").forGetter((graph) -> graph.vertices),
                BlockPosEdge.CODEC.listOf().fieldOf("edges").forGetter((graph) -> graph.edges)
        ).apply(instance, SerializationGraph::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, SerializationGraph> STREAM_CODEC = StreamCodec.composite(
                BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()), graph -> graph.vertices,
                BlockPosEdge.STREAM_CODEC.apply(ByteBufCodecs.list()), graph -> graph.edges,
                SerializationGraph::new
        );

        public SerializationGraph(List<BlockPos> vertices, List<BlockPosEdge> edges) {
            this.vertices = vertices;
            this.edges = edges;
        }

        public static BlockPosGraph toGraph(SerializationGraph graphSerialization) {
            BlockPosGraph graph = new BlockPosGraph();
            graph.addVertices(graphSerialization.vertices);
            for (BlockPosEdge i : graphSerialization.edges) {
                graph.addEdge(i);
            }
            return graph;
        }
        public static SerializationGraph fromGraph(BlockPosGraph graph) {
            List<BlockPos> vertices = new ArrayList<>(graph.vertices());
            List<BlockPosEdge> edges = new ArrayList<>(graph.edges());
            return new SerializationGraph(vertices, edges);
        }
    }
}
