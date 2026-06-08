package EsetKalenko.Halcyon.api.util;

import com.jgalgo.alg.shortestpath.NegativeCycleException;
import com.jgalgo.alg.shortestpath.ShortestPathSingleSource;
import com.jgalgo.alg.shortestpath.ShortestPathSingleSourceDijkstra;
import com.jgalgo.graph.EdgeSet;
import com.jgalgo.graph.Graph;
import com.jgalgo.graph.NoSuchEdgeException;
import com.jgalgo.graph.NoSuchVertexException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;

public class BlockPosGraph {
    private final Graph<BlockPos, BlockPosEdge> inner;
    private long version;

    public static final Codec<BlockPosGraph> CODEC = RecordCodecBuilder.create((instance) -> instance.group(
            BlockPos.CODEC.listOf().fieldOf("vertices").forGetter((graph) -> new ArrayList<>(graph.vertices())),
            BlockPosEdge.CODEC.listOf().fieldOf("edges").forGetter((graph) -> new ArrayList<>(graph.edges()))
    ).apply(instance, BlockPosGraph::new));

    public static final StreamCodec<RegistryFriendlyByteBuf, BlockPosGraph> STREAM_CODEC = new StreamCodec<>() {
        @Override
        public @NotNull BlockPosGraph decode(RegistryFriendlyByteBuf buf) {
            var graph = new BlockPosGraph();

            var numVertices = buf.readVarInt();
            graph.inner.ensureVertexCapacity(numVertices);
            for (int i = 0; i < numVertices; i++) {
                graph.addVertex(buf.readBlockPos());
            }

            var numEdges = buf.readVarInt();
            graph.inner.ensureEdgeCapacity(numEdges);
            var vertices = graph.inner.indexGraphVerticesMap();
            for (int i = 0; i < numEdges; i++) {
                var source = buf.readVarInt();
                var target = buf.readVarInt();
                graph.addEdge(vertices.indexToId(source), vertices.indexToId(target));
            }

            return graph;
        }

        @Override
        public void encode(@NotNull RegistryFriendlyByteBuf buf, @NotNull BlockPosGraph graph) {
            buf.writeVarInt(graph.vertices().size());
            for (var vertex : graph.vertices()) {
                buf.writeBlockPos(vertex);
            }

            buf.writeVarInt(graph.edges().size());
            var vertices = graph.inner.indexGraphVerticesMap();
            for (var edge : graph.edges()) {
                buf.writeVarInt(vertices.idToIndex(edge.source()));
                buf.writeVarInt(vertices.idToIndex(edge.target()));
            }
        }
    };

    public BlockPosGraph() {
        this.inner = Graph.newDirected();
    }

    public BlockPosGraph(Collection<BlockPos> vertices, Collection<BlockPosEdge> edges) {
        this();
        this.addVertices(vertices);
        for (var edge : edges) {
            this.addEdge(edge);
        }
        this.version = 0;
    }

    /**
     * Returns the number of changes made to this graph since creation.
     * Can be used for caching.
     *
     * @return Number of changes made to this graph since creation
     */
    public long getVersion() {
        return version;
    }

    /**
     * Get the set of all vertices of the graph.
     *
     * <p>
     * Each vertex in the graph is identified by a unique non null hashable object and the returned set is a set of all
     * these identifiers.
     *
     * <p>
     * The Graph object does not expose an explicit method to get the number of vertices, but it can accessed using this
     * method by {@code g.vertices().size()}.
     *
     * @return a set containing all vertices of the graph
     */
    public Set<BlockPos> vertices() {
        return this.inner.vertices();
    }

    /**
     * Get the set of all edges of the graph.
     *
     * <p>
     * Each edge in the graph is identified by a unique non null hashable object, and the returned set is a set of all
     * these identifiers.
     *
     * <p>
     * The Graph object does not expose an explicit method to get the number of edges, but it can accessed using this
     * method by {@code g.edges().size()}.
     *
     * @return a set containing all edges of the graph
     */
    public Set<BlockPosEdge> edges() {
        return this.inner.edges();
    }

    /**
     * Add a new edge to the graph.
     *
     * <p>
     * If the graph does not support parallel edges, and an edge between {@code source} and {@code target} already
     * exists, an exception will be raised. If the graph does not support self edges, and {@code source} and
     * {@code target} are the same vertex, an exception will be raised.
     *
     * @param  source                   the source vertex
     * @param  target                   the target vertex
     * @throws IllegalArgumentException if an edge between {@code source} and {@code target} already exists
     * @throws NoSuchVertexException    if {@code source} or {@code target} are not valid vertex identifiers
     */
    public void addEdge(BlockPos source, BlockPos target) {
        this.inner.addEdge(source, target, new BlockPosEdge(source, target));
        this.version++;
    }

    /**
     * Add a new edge to the graph.
     *
     * <p>
     * If the graph does not support parallel edges, and an edge between {@code source} and {@code target} already
     * exists, an exception will be raised. If the graph does not support self edges, and {@code source} and
     * {@code target} are the same vertex, an exception will be raised.
     *
     * @param  edge                     the new edge
     * @throws IllegalArgumentException if an edge with the same source and target between already exists
     * @throws NullPointerException     if {@code edge} is {@code null}
     * @throws NoSuchVertexException    if the source or target are not valid vertex identifiers
     */
    public void addEdge(BlockPosEdge edge) {
        this.inner.addEdge(edge.source(), edge.target(), edge);
        this.version++;
    }

    /**
     * Add a new vertex to the graph.
     *
     * @param  vertex                   new vertex
     * @throws IllegalArgumentException if {@code vertex} is already in the graph
     * @throws NullPointerException     if {@code vertex} is {@code null}
     */
    public void addVertex(BlockPos vertex) {
        this.inner.addVertex(vertex);
        this.version++;
    }

    /**
     * Add multiple vertices to the graph.
     *
     * @param  vertices                 new vertices
     * @throws IllegalArgumentException if {@code vertices} contains duplications or if any of the vertices is already
     *                                      in the graph
     * @throws NullPointerException     if {@code vertices} is {@code null} or if any of the vertices is {@code null}
     */
    public void addVertices(Collection<BlockPos> vertices) {
        this.inner.addVertices(vertices);
        this.version++;
    }

    /**
     * Get the edges whose target is {@code target}.
     *
     * <p>
     * The graph object does not expose an explicit method to get the (in) degree of a vertex, but it can accessed using
     * this method by {@code g.inEdges(vertex).size()}.
     *
     * @param  target                a target vertex
     * @return                       all the edges whose target is {@code target}
     * @throws NoSuchVertexException if {@code target} is not a valid vertex identifier
     */
    public EdgeSet<BlockPos, BlockPosEdge> inEdges(BlockPos target) {
        return this.inner.inEdges(target);
    }

    /**
     * Get the edges whose source is {@code target}.
     *
     * <p>
     * The graph object does not expose an explicit method to get the (out) degree of a vertex, but it can accessed
     * using this method by {@code g.outEdges(vertex).size()}.
     *
     * @param  source                a source vertex
     * @return                       all the edges whose target is {@code target}
     * @throws NoSuchVertexException if {@code target} is not a valid vertex identifier
     */
    public EdgeSet<BlockPos, BlockPosEdge> outEdges(BlockPos source) {
        return this.inner.outEdges(source);
    }

    /**
     * Check whether the graph contains an edge with the given source and target.
     *
     * <p>
     * If the graph is undirected, the method will return {@code true} if there is an edge whose end-points are
     * {@code source} and {@code target}, regardless of which is the source and which is the target.
     *
     * @param  source                the source vertex
     * @param  target                the target vertex
     * @return                       {@code true} if the graph contains an edge with the given source and target,
     *                               {@code false} otherwise
     * @throws NoSuchVertexException if {@code source} or {@code target} are not valid vertices identifiers
     */
    public boolean containsEdge(BlockPos source, BlockPos target) {
        return getEdge(source, target) != null;
    }

    /**
     * Get the edge whose source is {@code source} and target is {@code target}.
     *
     * <p>
     * If the graph is not directed, the return edge is an edge that its end-points are {@code source} and
     * {@code target}.
     *
     * <p>
     * In case there are multiple (parallel) edges between {@code source} and {@code target}, a single arbitrary one is
     * returned.
     *
     * @param  source                a source vertex
     * @param  target                a target vertex
     * @return                       id of the edge or {@code null} if no such edge exists
     * @throws NoSuchVertexException if {@code source} or {@code target} are not valid vertices identifiers
     */
    public BlockPosEdge getEdge(BlockPos source, BlockPos target) {
        return this.inner.getEdge(source, target);
    }

    /**
     * Remove a vertex and all its edges from the graph.
     *
     * @param  vertex                the vertex identifier to remove
     * @throws NoSuchVertexException if {@code vertex} is not a valid vertex identifier
     */
    public void removeVertex(BlockPos vertex) throws NoSuchVertexException {
        this.inner.removeVertex(vertex);
        this.version++;
    }

    /**
     * Remove an edge from the graph.
     *
     * @param  edge                the edge to remove
     * @throws NoSuchEdgeException if {@code edge} is not a valid edge identifier
     */
    public void removeEdge(BlockPosEdge edge) {
        this.inner.removeEdge(edge);
        this.version++;
    }

    /**
     * Remove all the edges of a vertex.
     *
     * @param  vertex                a vertex in the graph
     * @throws NoSuchVertexException if {@code vertex} is not a valid vertex identifier
     */
    public void removeEdgesOf(BlockPos vertex) {
        this.inner.removeEdgesOf(vertex);
        this.version++;
    }

    /**
     * Remove all edges whose source is {@code source}.
     *
     * @param  source                a vertex in the graph
     * @throws NoSuchVertexException if {@code source} is not a valid vertex identifier
     */
    public void removeOutEdgesOf(BlockPos source) {
        this.inner.removeOutEdgesOf(source);
        this.version++;
    }

    /**
     * Remove all edges whose target is {@code target}.
     *
     * @param  target                a vertex in the graph
     * @throws NoSuchVertexException if {@code target} is not a valid vertex identifier
     */
    public void removeInEdgesOf(BlockPos target) {
        this.inner.removeInEdgesOf(target);
        this.version++;
    }

    /**
     * Applies {@code consumer} to the inner graph then increments the version.
     * <br>
     * Users should avoid letting the reference escape the {@code consumer}.
     *
     * @param consumer the function that the inner graph is passed to.
     */
    public void unsafeWithInner(Consumer<Graph<BlockPos, BlockPosEdge>> consumer) {
        consumer.accept(this.inner);
        this.version++;
    }

    /**
     * Compute the shortest paths from a source to any other vertex in a graph.
     *
     * @return                        a result object containing the distances and shortest paths from the source to any
     *                                other vertex
     * @throws NegativeCycleException if a negative cycle is detected in the graph. If there is a negative cycle that is
     *                                    not reachable from the given source, it might not be detected, depending on
     *                                    the implementation
     */
    public ShortestPathSingleSource.Result<BlockPos, BlockPosEdge> getPaths(BlockPos vertex) {
        return new ShortestPathSingleSourceDijkstra().computeShortestPaths(this.inner, BlockPosEdge::distSqr, vertex);
    }
}
