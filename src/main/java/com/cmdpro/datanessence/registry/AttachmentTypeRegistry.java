package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.misc.BlockPosNetworks;
import com.cmdpro.datanessence.block.technical.StructureProtectorBlockEntity;
import com.cmdpro.datanessence.item.equipment.GrapplingHook;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;

import java.util.*;
import java.util.function.Supplier;

public class AttachmentTypeRegistry {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES = DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES,
            DataNEssence.MOD_ID);
    public static final Supplier<AttachmentType<ArrayList<StructureProtectorBlockEntity>>> STRUCTURE_CONTROLLERS =
            register("structure_controllers", () -> AttachmentType.builder(() -> new ArrayList<StructureProtectorBlockEntity>()).build());

    public static final Supplier<AttachmentType<BlockPosNetworks>> ESSENCE_NODE_NETWORKS =
            register("essence_node_networks", () -> AttachmentType.builder(() -> new BlockPosNetworks(new DefaultDirectedGraph<>(DefaultEdge.class))).serialize(BlockPosNetworks.CODEC).build());
    public static final Supplier<AttachmentType<BlockPosNetworks>> CAPABILITY_NODE_NETWORKS =
            register("capability_node_networks", () -> AttachmentType.builder(() -> new BlockPosNetworks(new DefaultDirectedGraph<>(DefaultEdge.class))).serialize(BlockPosNetworks.CODEC).build());
    public static final Supplier<AttachmentType<BlockPosNetworks>> ENDER_PEARL_NETWORKS =
            register("ender_pearl_networks", () -> AttachmentType.builder(() -> new BlockPosNetworks(new DefaultDirectedGraph<>(DefaultEdge.class))).serialize(BlockPosNetworks.CODEC).build());


    public static final Supplier<AttachmentType<Integer>> TIER =
            register("tier", () -> AttachmentType.builder(() -> 0).serialize(Codec.INT).copyOnDeath().build());
    public static final Supplier<AttachmentType<Optional<BlockEntity>>> LINK_FROM =
            register("link_from", () -> AttachmentType.builder(() -> Optional.ofNullable((BlockEntity)null)).build());
    public static final Supplier<AttachmentType<Optional<GrapplingHook.GrapplingHookData>>> GRAPPLING_HOOK_DATA =
            register("grappling_hook_data", () -> AttachmentType.builder(() -> Optional.ofNullable((GrapplingHook.GrapplingHookData)null)).build());
    public static final Supplier<AttachmentType<Optional<StructureProtectorBlockEntity>>> BINDING_STRUCTURE_CONTROLLER =
            register("binding_structure_controller", () -> AttachmentType.builder(() -> Optional.ofNullable((StructureProtectorBlockEntity)null)).build());
    public static final Supplier<AttachmentType<HashMap<ResourceLocation, Boolean>>> UNLOCKED_ESSENCES =
            register("unlocked_essences", () -> AttachmentType.builder(() -> new HashMap<ResourceLocation, Boolean>()).serialize(Codec.unboundedMap(ResourceLocation.CODEC, Codec.BOOL).xmap(HashMap::new, HashMap::new)).copyOnDeath().build());
    public static final Supplier<AttachmentType<Boolean>> HAS_HORNS =
            register("has_horns", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).copyOnDeath().build());
    public static final Supplier<AttachmentType<Boolean>> HAS_TAIL =
            register("has_tail", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).copyOnDeath().build());
    public static final Supplier<AttachmentType<Boolean>> HAS_WINGS =
            register("has_wings", () -> AttachmentType.builder(() -> false).serialize(Codec.BOOL).copyOnDeath().build());
    public static final Supplier<AttachmentType<ArrayList<ResourceLocation>>> UNLOCKED =
            register("unlocked", () -> AttachmentType.builder(() -> new ArrayList<ResourceLocation>()).serialize(
                    ResourceLocation.CODEC.listOf().xmap(ArrayList::new, (a) -> a.stream().toList())).copyOnDeath().build());
    public static final Supplier<AttachmentType<HashMap<ResourceLocation, Integer>>> INCOMPLETE_STAGES =
            register("incomplete_stages", () -> AttachmentType.builder(() -> new HashMap<ResourceLocation, Integer>()).serialize(
                    Codec.unboundedMap(ResourceLocation.CODEC, Codec.INT).xmap(HashMap::new, (a) -> a)).copyOnDeath().build());

    private static <T extends AttachmentType<?>> Supplier<T> register(final String name, final Supplier<T> attachment) {
        return ATTACHMENT_TYPES.register(name, attachment);
    }
}