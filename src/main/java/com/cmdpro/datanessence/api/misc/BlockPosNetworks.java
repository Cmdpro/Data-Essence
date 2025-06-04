package com.cmdpro.datanessence.api.misc;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

public class BlockPosNetworks {
    public DefaultDirectedGraph<BlockPos, DefaultEdge> graph;
    public DijkstraShortestPath<BlockPos, DefaultEdge> path;
    public static final Codec<BlockPosNetworks> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
        BlockPosGraphSerialization.SerializationGraph.CODEC.fieldOf("graph").xmap(BlockPosGraphSerialization.SerializationGraph::toGraph, BlockPosGraphSerialization.SerializationGraph::fromGraph).forGetter((networks) -> networks.graph)
    ).apply(instance, BlockPosNetworks::new));
    public BlockPosNetworks(DefaultDirectedGraph<BlockPos, DefaultEdge> graph) {
        this.graph = graph;
        path = new DijkstraShortestPath<>(graph);
    }
}
