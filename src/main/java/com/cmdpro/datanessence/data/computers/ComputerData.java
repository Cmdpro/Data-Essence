package com.cmdpro.datanessence.data.computers;

import com.cmdpro.datanessence.api.computer.ComputerFile;

import java.util.List;

public class ComputerData {
    public List<ComputerFile> files;
    public ComputerData(List<ComputerFile> files) {
        this.files = files;
    }
}
