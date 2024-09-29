package com.cmdpro.datanessence.multiblock.predicates.serializers;

import com.cmdpro.datanessence.api.multiblock.MultiblockPredicateSerializer;
import com.cmdpro.datanessence.multiblock.predicates.BlockstateMultiblockPredicate;
import com.cmdpro.datanessence.multiblock.predicates.TagMultiblockPredicate;
import com.google.gson.JsonObject;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public class TagMultiblockPredicateSerializer extends MultiblockPredicateSerializer<TagMultiblockPredicate> {
    public static TagKey<Block> getTagFromString(String str) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.tryParse(str));
    }
    public static final StreamCodec<RegistryFriendlyByteBuf, TagMultiblockPredicate> STREAM_CODEC = StreamCodec.of((pBuffer, pValue) -> {
        pBuffer.writeUtf(pValue.tag.location().toString());
    }, (pBuffer) -> {
        String tag = pBuffer.readUtf();
        return new TagMultiblockPredicate(getTagFromString(tag));
    });
    public static final MapCodec<TagMultiblockPredicate> CODEC = RecordCodecBuilder.mapCodec((instance) -> instance.group(
            TagKey.codec(Registries.BLOCK).fieldOf("tag").forGetter(page -> page.tag)
    ).apply(instance, TagMultiblockPredicate::new));
    @Override
    public MapCodec<TagMultiblockPredicate> getCodec() {
        return CODEC;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, TagMultiblockPredicate> getStreamCodec() {
        return STREAM_CODEC;
    }
}
