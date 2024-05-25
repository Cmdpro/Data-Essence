package com.cmdpro.datanessence.init;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.minigames.TestMinigame;
import com.cmdpro.datanessence.minigames.TestMinigameCreator;
import com.cmdpro.datanessence.screen.databank.Minigame;
import com.cmdpro.datanessence.screen.databank.MinigameCreator;
import com.cmdpro.datanessence.screen.databank.MinigameSerializer;
import com.cmdpro.datanessence.screen.datatablet.PageSerializer;
import com.cmdpro.datanessence.screen.datatablet.pages.serializers.CraftingPageSerializer;
import com.cmdpro.datanessence.screen.datatablet.pages.serializers.TextPageSerializer;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec2;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import org.joml.Vector2i;

import java.util.function.Supplier;

public class MinigameInit {
    public static final DeferredRegister<MinigameSerializer> MINIGAME_TYPES = DeferredRegister.create(new ResourceLocation(DataNEssence.MOD_ID, "minigames"), DataNEssence.MOD_ID);

    public static final RegistryObject<MinigameSerializer> TESTMINIGAME = register("test", () -> new TestMinigameCreator.TestMinigameSerializer());
    private static <T extends MinigameSerializer> RegistryObject<T> register(final String name, final Supplier<T> item) {
        return MINIGAME_TYPES.register(name, item);
    }
}
