package com.cmdpro.datanessence.screen.datatablet.pages.serializers;

import com.cmdpro.datanessence.screen.datatablet.PageSerializer;
import com.cmdpro.datanessence.screen.datatablet.pages.ItemPage;
import com.cmdpro.datanessence.screen.datatablet.pages.MultiblockPage;
import com.google.gson.JsonObject;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;

public class MultiblockPageSerializer extends PageSerializer<MultiblockPage> {
    public static final MultiblockPageSerializer INSTANCE = new MultiblockPageSerializer();
    @Override
    public MultiblockPage fromJson(JsonObject json) {
        return new MultiblockPage(ResourceLocation.tryParse(GsonHelper.getAsString(json, "multiblock")));
    }
    @Override
    public MultiblockPage fromNetwork(FriendlyByteBuf buf) {
        ResourceLocation multiblock = buf.readResourceLocation();
        return new MultiblockPage(multiblock);
    }

    @Override
    public void toNetwork(MultiblockPage page, FriendlyByteBuf buf) {
        buf.writeResourceLocation(page.multiblock);
    }
}
