package com.cmdpro.datanessence.computers;

import com.cmdpro.datanessence.api.DataNEssenceRegistries;
import com.cmdpro.datanessence.api.computer.ComputerFile;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public class ComputerData {
    public List<ComputerFile> files;
    public ComputerData(List<ComputerFile> files) {
        this.files = files;
    }
    public static void toNetwork(FriendlyByteBuf buf, ComputerData data) {
        buf.writeCollection(data.files, (a, b) -> {
            a.writeResourceLocation(DataNEssenceRegistries.COMPUTER_FILE_TYPES_REGISTRY.getKey(b.getType()));
            b.getType().toNetwork(a, b);
        });
    }
    public static ComputerData fromNetwork(FriendlyByteBuf buf) {
        List<ComputerFile> files = buf.readList((a) -> {
            ResourceLocation type = a.readResourceLocation();
            return DataNEssenceRegistries.COMPUTER_FILE_TYPES_REGISTRY.get(type).fromNetwork(a);
        });
        return new ComputerData(files);
    }
}
