package com.cmdpro.datanessence.api.util.client;

import com.cmdpro.datanessence.ClientModEvents;
import com.cmdpro.datanessence.screen.datatablet.Entry;
import com.cmdpro.datanessence.toasts.CriticalDataToast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.chunk.SectionRenderDispatcher;

public class ClientProgressionUtil {
    public static void unlockedCriticalData(Entry entry) {
        Minecraft.getInstance().getToasts().addToast(new CriticalDataToast(entry));
        ClientProgressionUtil.progressionShader();
    }

    public static void progressionShader() {
        ClientModEvents.progressionShader.time = 0;
        ClientModEvents.progressionShader.setActive(true);
    }

    public static void updateWorld() {
        for (SectionRenderDispatcher.RenderSection i : Minecraft.getInstance().levelRenderer.viewArea.sections) {
            i.setDirty(false);
        }
    }
}
