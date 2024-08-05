package com.cmdpro.datanessence.registry;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.computers.ComputerFileType;
import com.cmdpro.datanessence.computers.files.types.TextFileType;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ComputerFileTypeRegistry {
    public static final DeferredRegister<ComputerFileType> COMPUTER_FILE_TYPES = DeferredRegister.create(ResourceLocation.fromNamespaceAndPath(DataNEssence.MOD_ID, "computer_file_types"), DataNEssence.MOD_ID);

    public static final Supplier<ComputerFileType> TEXT = register("text", () -> new TextFileType());
    private static <T extends ComputerFileType> Supplier<T> register(final String name, final Supplier<T> item) {
        return COMPUTER_FILE_TYPES.register(name, item);
    }
}
