package com.cmdpro.datanessence.minigames;

import com.cmdpro.datanessence.init.MinigameInit;
import com.cmdpro.datanessence.screen.DataBankScreen;
import com.cmdpro.datanessence.screen.databank.Minigame;
import com.cmdpro.datanessence.screen.databank.MinigameCreator;
import com.cmdpro.datanessence.screen.databank.MinigameSerializer;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.FriendlyByteBuf;
import org.joml.Vector2i;

public class TestMinigameCreator extends MinigameCreator<TestMinigame> {
    public Vector2i playerPos;
    public TestMinigameCreator(int playerX, int playerY) {
        playerPos = new Vector2i(playerX, playerY);
    }
    @Override
    public TestMinigame createMinigame() {
        return new TestMinigame(playerPos);
    }

    @Override
    public MinigameSerializer getSerializer() {
        return MinigameInit.TESTMINIGAME.get();
    }

    public static class TestMinigameSerializer extends MinigameSerializer<TestMinigameCreator> {
        @Override
        public TestMinigameCreator fromJson(JsonObject json) {
            return new TestMinigameCreator(json.get("playerX").getAsInt(), json.get("playerY").getAsInt());
        }

        @Override
        public TestMinigameCreator fromNetwork(FriendlyByteBuf buf) {
            int playerX = buf.readInt();
            int playerY = buf.readInt();
            return new TestMinigameCreator(playerX, playerY);
        }

        @Override
        public void toNetwork(TestMinigameCreator creator, FriendlyByteBuf buf) {
            buf.writeInt(creator.playerPos.x);
            buf.writeInt(creator.playerPos.y);
        }
    }
}
