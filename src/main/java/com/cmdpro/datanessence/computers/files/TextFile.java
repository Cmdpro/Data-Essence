package com.cmdpro.datanessence.computers.files;

import com.cmdpro.datanessence.api.computer.ComputerFile;
import com.cmdpro.datanessence.api.computer.ComputerFileType;
import com.cmdpro.datanessence.registry.ComputerFileTypeRegistry;
import net.minecraft.network.chat.Component;

public class TextFile extends ComputerFile {
    public TextFile(Component text, boolean rtl) {
        this.text = text;
        this.rtl = rtl;
    }
    public Component text;
    public boolean rtl;
    @Override
    public ComputerFileType getType() {
        return ComputerFileTypeRegistry.TEXT.get();
    }
}
