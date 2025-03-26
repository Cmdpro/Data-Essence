package com.cmdpro.datanessence.api.node;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import org.jgrapht.Graph;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class EssenceNodeNetworks {
    public DefaultDirectedGraph<BlockPos, DefaultEdge> graph;
    public DijkstraShortestPath<BlockPos, DefaultEdge> path;
    public static final Codec<EssenceNodeNetworks> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
        NodeGraphSerialization.SerializationGraph.CODEC.fieldOf("graph").xmap(NodeGraphSerialization.SerializationGraph::toGraph, NodeGraphSerialization.SerializationGraph::fromGraph).forGetter((networks) -> networks.graph)
    ).apply(instance, EssenceNodeNetworks::new));
    public EssenceNodeNetworks(DefaultDirectedGraph<BlockPos, DefaultEdge> graph) {
        this.graph = graph;
        path = new DijkstraShortestPath<>(graph);
    }
    public void updatePath() {
    }
}
