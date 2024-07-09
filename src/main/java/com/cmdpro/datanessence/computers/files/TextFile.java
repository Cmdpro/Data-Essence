package com.cmdpro.datanessence.computers.files;

import com.cmdpro.datanessence.computers.ComputerFile;
import com.cmdpro.datanessence.computers.ComputerFileType;
import com.cmdpro.datanessence.registry.ComputerFileTypeRegistry;
import net.minecraft.network.chat.Component;

public class TextFile extends ComputerFile {
    public TextFile(Component text) {
        this.text = text;
    }
    public Component text;
    @Override
    public ComputerFileType getType() {
        return ComputerFileTypeRegistry.TEXT.get();
    }
}
