package com.cmdpro.datanessence.screen.databank;

import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.CraftingHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataBankTypeSerializer {
    public ResourceLocation[] read(ResourceLocation entryId, JsonObject json) {
        if (!json.has("ids")) {
            throw new JsonSyntaxException("Element ids missing in entry JSON for " + entryId.toString());
        }
        List<ResourceLocation> ids = new ArrayList<>();
        for (JsonElement i : json.getAsJsonArray("ids")) {
            ids.add(ResourceLocation.tryParse(i.getAsString()));
        }
        return ids.toArray(new ResourceLocation[0]);
    }
    @Nonnull
    public static ResourceLocation[] fromNetwork(FriendlyByteBuf buf) {
        ResourceLocation[] ids = buf.readList(FriendlyByteBuf::readResourceLocation).toArray(new ResourceLocation[0]);
        return ids;
    }
    public static void toNetwork(FriendlyByteBuf buf, ResourceLocation[] ids) {
        buf.writeCollection(Arrays.stream(ids).toList(), FriendlyByteBuf::writeResourceLocation);
    }
}