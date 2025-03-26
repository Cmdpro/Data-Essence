package com.cmdpro.datanessence.api.node;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.List;

class NodeGraphSerialization {
    protected static class SerializationEdge {
        public BlockPos source;
        public BlockPos target;
        public SerializationEdge(BlockPos source, BlockPos target) {
            this.source = source;
            this.target = target;
        }
        protected static final Codec<SerializationEdge> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                BlockPos.CODEC.fieldOf("source").forGetter((edge) -> edge.source),
                BlockPos.CODEC.fieldOf("target").forGetter((edge) -> edge.target)
        ).apply(instance, SerializationEdge::new));
    }
    protected static class SerializationGraph {
        protected static final Codec<SerializationGraph> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
                BlockPos.CODEC.listOf().fieldOf("vertices").forGetter((graph) -> graph.vertices),
                SerializationEdge.CODEC.listOf().fieldOf("edges").forGetter((graph) -> graph.edges)
        ).apply(instance, SerializationGraph::new));
        public SerializationGraph(List<BlockPos> vertices, List<SerializationEdge> edges) {
            this.vertices = vertices;
            this.edges = edges;
        }
        public List<BlockPos> vertices;
        public List<SerializationEdge> edges;
        public static DefaultDirectedGraph<BlockPos, DefaultEdge> toGraph(SerializationGraph graphSerialization) {
            DefaultDirectedGraph<BlockPos, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
            for (BlockPos i : graphSerialization.vertices) {
                graph.addVertex(i);
            }
            for (SerializationEdge i : graphSerialization.edges) {
                graph.addEdge(i.source, i.target);
            }
            return graph;
        }
        public static SerializationGraph fromGraph(DefaultDirectedGraph<BlockPos, DefaultEdge> graph) {
            List<BlockPos> vertices = new ArrayList<>(graph.vertexSet());
            List<SerializationEdge> edges = graph.edgeSet().stream().map((edge) -> new SerializationEdge(graph.getEdgeSource(edge), graph.getEdgeTarget(edge))).toList();
            return new SerializationGraph(vertices, edges);
        }
    }
}
