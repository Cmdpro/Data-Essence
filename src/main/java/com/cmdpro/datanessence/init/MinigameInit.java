package com.cmdpro.datanessence.init;

import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.minigames.TestMinigame;
import com.cmdpro.datanessence.screen.databank.Minigame;
import com.cmdpro.datanessence.screen.databank.MinigameCreator;
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
    public static final DeferredRegister<MinigameCreator> MINIGAME_TYPES = DeferredRegister.create(new ResourceLocation(DataNEssence.MOD_ID, "minigames"), DataNEssence.MOD_ID);

    public static final RegistryObject<MinigameCreator> TESTMINIGAME = register("test", () -> new MinigameCreator<TestMinigame>() {

        @Override
        public TestMinigame fromJson(JsonObject json) {
            return createNew(json.get("playerX").getAsInt(), json.get("playerY").getAsInt());
        }

        @Override
        public TestMinigame fromNetwork(FriendlyByteBuf buf) {
            int playerX = buf.readInt();
            int playerY = buf.readInt();
            return createNew(playerX, playerY);
        }

        @Override
        public void toNetwork(TestMinigame page, FriendlyByteBuf buf) {
            buf.writeInt(page.playerPos.x);
            buf.writeInt(page.playerPos.y);
        }

        public TestMinigame createNew(int playerX, int playerY) {
            TestMinigame minigame = new TestMinigame(new Vector2i(playerX, playerY));
            return minigame;
        }
    });
    private static <T extends MinigameCreator> RegistryObject<T> register(final String name, final Supplier<T> item) {
        return MINIGAME_TYPES.register(name, item);
    }
}
