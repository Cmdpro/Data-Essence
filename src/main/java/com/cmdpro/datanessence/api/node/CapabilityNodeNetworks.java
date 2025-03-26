package com.cmdpro.datanessence.api.node;

import com.cmdpro.datanessence.api.node.block.BaseCapabilityPointBlockEntity;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.ArrayList;
import java.util.List;

public class CapabilityNodeNetworks {
    public DefaultDirectedGraph<BlockPos, DefaultEdge> graph;
    public DijkstraShortestPath<BlockPos, DefaultEdge> path;
    public static final Codec<CapabilityNodeNetworks> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            NodeGraphSerialization.SerializationGraph.CODEC.fieldOf("graph").xmap(NodeGraphSerialization.SerializationGraph::toGraph, NodeGraphSerialization.SerializationGraph::fromGraph).forGetter((networks) -> networks.graph)
    ).apply(instance, CapabilityNodeNetworks::new));
    public CapabilityNodeNetworks(DefaultDirectedGraph<BlockPos, DefaultEdge> graph) {
        this.graph = graph;
        path = new DijkstraShortestPath<>(graph);
    }
}
