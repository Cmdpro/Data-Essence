package EsetKalenko.Halcyon.networking.packet.s2c;

import EsetKalenko.Halcyon.Halcyon;
import EsetKalenko.Halcyon.client.StructureSongs;
import EsetKalenko.Halcyon.networking.Message;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Map;

public record StructureMusicSync(ResourceLocation music, boolean schedule) implements Message {
    @Override
    public void handleClient(Minecraft minecraft, Player player, IPayloadContext ctx) {
        if (minecraft.level == null) return;
        var soundRegistry = minecraft.level.registryAccess().registry(Registries.SOUND_EVENT);
        if (soundRegistry.isEmpty()) return;

        if (music == null) {
            for (StructureSongs.StructureMusic song : StructureSongs.STRUCTURE_SONGS.values()) {
                song.stop();
            }
        }

        var song = StructureSongs.getSong( soundRegistry.get().get(music) );

        if (schedule) {
            song.start();
        } else {
            song.stop();
        }
    }

    public static void write(RegistryFriendlyByteBuf pBuffer, StructureMusicSync obj) {
        pBuffer.writeResourceLocation(obj.music);
        pBuffer.writeBoolean(obj.schedule);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static final Type<StructureMusicSync> TYPE = new Type<>(Halcyon.locate("structure_music_sync"));

    public static StructureMusicSync read(RegistryFriendlyByteBuf buf) {
        ResourceLocation music = buf.readResourceLocation();
        boolean schedule = buf.readBoolean();
        return new StructureMusicSync(music, schedule);
    }

}