package com.cmdpro.datanessence.api;

import com.cmdpro.datanessence.moddata.PlayerModData;
import com.cmdpro.datanessence.moddata.PlayerModDataProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Supplier;

public class DataNEssenceUtil {
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
}
