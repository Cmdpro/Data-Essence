package com.cmdpro.datanessence.api;

import com.cmdpro.datanessence.moddata.PlayerModData;
import com.cmdpro.datanessence.moddata.PlayerModDataProvider;
import com.cmdpro.datanessence.screen.datatablet.Page;
import com.cmdpro.datanessence.screen.datatablet.PageSerializer;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.IForgeRegistry;
import org.joml.Math;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class DataNEssenceUtil {
    public static Supplier<IForgeRegistry<PageSerializer>> PAGE_TYPE_REGISTRY = null;
    public static boolean playerHasEntry(Player player, String entry) {
        if (entry != null) {
            return player.getCapability(PlayerModDataProvider.PLAYER_MODDATA).resolve().get().getUnlocked().contains(ResourceLocation.tryParse(entry));
        }
        return false;
    }
    public static void drawLine(ParticleOptions particle, Vec3 point1, Vec3 point2, Level level, double space) {
        double distance = point1.distanceTo(point2);
        Vec3 vector = point2.subtract(point1).normalize().multiply(space, space, space);
        double length = 0;
        for (Vec3 point = point1; length < distance; point = point.add(vector)) {
            ((ServerLevel)level).sendParticles(particle, point.x, point.y, point.z, 1, 0, 0, 0, 0);
            length += space;
        }
    }
    public static void updatePlayerData(Player player) {
        player.getCapability(PlayerModDataProvider.PLAYER_MODDATA).ifPresent(data -> {
            data.updateData((ServerPlayer)player);
        });
    }
    public static void transferEssence(EssenceContainer from, EssenceContainer to, float amount) {
        if (from.getMaxEssence() > 0 && to.getMaxEssence() > 0) {
            float trueAmount = Math.clamp(0, to.getMaxEssence()-to.getEssence(), Math.min(from.getEssence(), amount));
            from.setEssence(from.getEssence()-trueAmount);
            to.setEssence(to.getEssence()+trueAmount);
        }
    }
    public static void transferLunarEssence(EssenceContainer from, EssenceContainer to, float amount) {
        if (from.getMaxLunarEssence() > 0 && to.getMaxLunarEssence() > 0) {
            float trueAmount = Math.clamp(0, to.getMaxLunarEssence()-to.getLunarEssence(), Math.min(from.getLunarEssence(), amount));
            from.setLunarEssence(from.getLunarEssence()-trueAmount);
            to.setLunarEssence(to.getLunarEssence()+trueAmount);
        }
    }
    public static void transferNaturalEssence(EssenceContainer from, EssenceContainer to, float amount) {
        if (from.getMaxNaturalEssence() > 0 && to.getMaxNaturalEssence() > 0) {
            float trueAmount = Math.clamp(0, to.getMaxNaturalEssence()-to.getNaturalEssence(), Math.min(from.getNaturalEssence(), amount));
            from.setNaturalEssence(from.getNaturalEssence()-trueAmount);
            to.setNaturalEssence(to.getNaturalEssence()+trueAmount);
        }
    }
    public static void transferExoticEssence(EssenceContainer from, EssenceContainer to, float amount) {
        if (from.getMaxExoticEssence() > 0 && to.getMaxExoticEssence() > 0) {
            float trueAmount = Math.clamp(0, to.getMaxExoticEssence()-to.getExoticEssence(), Math.min(from.getExoticEssence(), amount));
            from.setExoticEssence(from.getExoticEssence()-trueAmount);
            to.setExoticEssence(to.getExoticEssence()+trueAmount);
        }
    }
}
