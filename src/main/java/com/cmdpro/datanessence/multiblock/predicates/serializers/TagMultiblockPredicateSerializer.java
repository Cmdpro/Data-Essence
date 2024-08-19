package com.cmdpro.datanessence.multiblock.predicates.serializers;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.multiblock.MultiblockPredicateSerializer;
import com.cmdpro.datanessence.multiblock.predicates.BlockstateMultiblockPredicate;
import com.cmdpro.datanessence.multiblock.predicates.TagMultiblockPredicate;
import com.google.gson.JsonObject;
import net.minecraft.commands.arguments.blocks.BlockStateParser;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.Tags;

public class TagMultiblockPredicateSerializer extends MultiblockPredicateSerializer<TagMultiblockPredicate> {
    @Override
    public TagMultiblockPredicate fromNetwork(FriendlyByteBuf buf) {
        String tag = buf.readUtf();
        return new TagMultiblockPredicate(getTagFromString(tag));
    }

    @Override
    public void toNetwork(FriendlyByteBuf buf, TagMultiblockPredicate predicate) {
        buf.writeUtf(predicate.tag.location().toString());
    }

    @Override
    public TagMultiblockPredicate fromJson(JsonObject obj) {
        String tag = obj.get("tag").getAsString();
        return new TagMultiblockPredicate(getTagFromString(tag));
    }
    public TagKey<Block> getTagFromString(String str) {
        return TagKey.create(Registries.BLOCK, ResourceLocation.tryParse(str));
    }
}
