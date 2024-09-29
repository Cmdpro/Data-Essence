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
}
