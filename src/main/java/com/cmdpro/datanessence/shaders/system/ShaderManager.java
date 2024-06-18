package com.cmdpro.datanessence.shaders.system;

import java.util.ArrayList;
import java.util.List;

public class ShaderManager {
    public static List<ShaderInstance> instances = new ArrayList<>();
    public static void addShader(ShaderInstance instance) {
        instances.add(instance);
    }
}
