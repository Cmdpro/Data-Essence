package EsetKalenko.Halcyon.block.auxiliary;

import EsetKalenko.Halcyon.api.essence.EssenceBlockEntity;
import EsetKalenko.Halcyon.api.essence.EssenceStorage;
import EsetKalenko.Halcyon.api.essence.EssenceType;
import EsetKalenko.Halcyon.api.essence.container.MultiEssenceContainer;
import EsetKalenko.Halcyon.api.util.BufferUtil;
import EsetKalenko.Halcyon.client.particle.CircleParticleOptions;
import EsetKalenko.Halcyon.client.particle.MoteParticleOptions;
import EsetKalenko.Halcyon.config.DataNEssenceConfig;
import EsetKalenko.Halcyon.registry.AttachmentTypeRegistry;
import EsetKalenko.Halcyon.registry.BlockEntityRegistry;
import EsetKalenko.Halcyon.registry.EssenceTypeRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

public class TwiningLanternBlockEntity extends BlockEntity implements EssenceBlockEntity {
    MultiEssenceContainer storage = new MultiEssenceContainer(
            List.of(
                    EssenceTypeRegistry.ESSENCE.get(),
                    EssenceTypeRegistry.LUNAR_ESSENCE.get(),
                    EssenceTypeRegistry.NATURAL_ESSENCE.get(),
                    EssenceTypeRegistry.EXOTIC_ESSENCE.get()
            ),
            5000
    );

    public int industrialTicksLeft, lunarTicksLeft, livingTicksLeft, primeTicksLeft;
    public BlockPos corner1, corner2;

    public TwiningLanternBlockEntity(BlockPos pos, BlockState blockState) {
        super(BlockEntityRegistry.TWINING_LANTERN.get(), pos, blockState);
        corner1 = new BlockPos(pos.getX()-15, pos.getY()-7, pos.getZ()-15);
        corner2 = new BlockPos(pos.getX()+15, pos.getY()+7, pos.getZ()+15);
    }

    @Override
    public EssenceStorage getStorage() {
        return storage;
    }

    public static void tick(Level world, BlockPos pos, BlockState state, TwiningLanternBlockEntity lantern) {
        if ( !world.isClientSide ) {

            BufferUtil.getEssenceFromBuffersBelow(lantern, lantern.getStorage().getSupportedEssenceTypes());

            for ( EssenceType tide : lantern.getStorage().getSupportedEssenceTypes() ) {
                if ( lantern.getFuel(tide) > 0 ) {
                    lantern.doLanternEffect(tide, lantern.worldPosition, world);
                } else {
                    if ( lantern.getStorage().getEssence(tide) >= 1 ) {
                        lantern.getStorage().removeEssence(tide, 1);
                        lantern.refuel(tide);
                    }
                }
            }

        } else {
            // colors
            Color[] lensingColors = new Color[] {
                    new Color(0x0d3764),
                    new Color(0x7550a0),
                    new Color(0x9cd0e4),
                    new Color(0xd8fffd)
            };

            // TODO select a color for this tick based on fuel levels
            // if we have no fuel, lensing crystal colors are solely used
            // if we have 1-20 of any fuels, those are prioritized based on equality to each other—if we have 20 industrial,
            // and only 4 lunar, then industrial is more likely to appear than lunar. lensing crystal colors is always an
            // option, though its likelihood decreases as the types of fuel, and their fullness, go up.

            // for now we just randomly pick through the lensing crystals
            int index = Mth.randomBetweenInclusive(world.random, 0, lensingColors.length-1);

            var flame = new CircleParticleOptions()
                    .setColor(lensingColors[index])
                    .setAdditive(true)
                    .setFriction(0f);

            var mote = new MoteParticleOptions() // only have a chance to spawn
                    .setColor(lensingColors[index])
                    .setAdditive(true)
                    .setFriction(0f)
                    .setGravity(0.3f);

            // spawning point for both particles
            var origin = pos.getBottomCenter().add(
                    Mth.nextDouble(world.random, -0.07, 0.07),
                    (state.getValue(TwiningLantern.HANGING)) ? 0.42d : 0.1d,
                    Mth.nextDouble(world.random, -0.07, 0.07));

            // flame
            world.addParticle(
                    flame,
                    origin.x,
                    origin.y,
                    origin.z,
                    Mth.nextDouble(world.random, -0.05, 0.05),
                    Mth.nextDouble(world.random, 0.01, 0.2),
                    Mth.nextDouble(world.random, -0.05, 0.05)
            );

            // mote
            if (world.random.nextInt() % 15 == 0)
                world.addParticle(
                        mote,
                        origin.x,
                        origin.y,
                        origin.z,
                        Mth.nextDouble(world.random, -0.4, 0.4),
                        Mth.nextDouble(world.random, 0.01, 0.2),
                        Mth.nextDouble(world.random, -0.4, 0.4)
                );
        }
    }

    public int getFuel(EssenceType tide) {
        if (tide == EssenceTypeRegistry.ESSENCE.get()) {
            return industrialTicksLeft;
        } else if (tide == EssenceTypeRegistry.LUNAR_ESSENCE.get()) {
            return lunarTicksLeft;
        } else if (tide == EssenceTypeRegistry.NATURAL_ESSENCE.get()) {
            return livingTicksLeft;
        } else if (tide == EssenceTypeRegistry.EXOTIC_ESSENCE.get()) {
            return primeTicksLeft;
        }
        return 0;
    }

    public void refuel(EssenceType tide) {
        if (tide == EssenceTypeRegistry.ESSENCE.get()) {
            industrialTicksLeft += DataNEssenceConfig.twiningLanternFuelRatio;
        } else if (tide == EssenceTypeRegistry.LUNAR_ESSENCE.get()) {
            lunarTicksLeft += DataNEssenceConfig.twiningLanternFuelRatio;
        } else if (tide == EssenceTypeRegistry.NATURAL_ESSENCE.get()) {
            livingTicksLeft += DataNEssenceConfig.twiningLanternFuelRatio;
        } else if (tide == EssenceTypeRegistry.EXOTIC_ESSENCE.get()) {
            primeTicksLeft += DataNEssenceConfig.twiningLanternFuelRatio;
        }
    }

    public void doLanternEffect(EssenceType tide, BlockPos pos, Level world) {
        if (tide == EssenceTypeRegistry.ESSENCE.get()) {
            // blocks mob spawns — this is done in a neo event.
            industrialTicksLeft--;
        } else if (tide == EssenceTypeRegistry.LUNAR_ESSENCE.get()) {
            lunarTicksLeft--;
            // TODO transformatively enhances function of the other three, does nothing alone
            // +industrial: makes it block ALL spawns
        } else if (tide == EssenceTypeRegistry.NATURAL_ESSENCE.get()) {
            livingTicksLeft--;
            // TODO accelerates plant and animal growth, maybe also breeds them?
        } else if (tide == EssenceTypeRegistry.EXOTIC_ESSENCE.get()) {
            primeTicksLeft--;
            // TODO ???
        }
    }

    @Override
    public void setLevel(Level world) {
        if (level != null) {
            if (!level.isClientSide) {
                level.getData(AttachmentTypeRegistry.TWINING_LANTERNS).remove(this);
            }
        }
        super.setLevel(world);
        if (!world.isClientSide) {
            if (!world.getData(AttachmentTypeRegistry.TWINING_LANTERNS).contains(this)) {
                world.getData(AttachmentTypeRegistry.TWINING_LANTERNS).add(this);
            }
        }
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        if (level != null) {
            if (!level.isClientSide) {
                level.getData(AttachmentTypeRegistry.TWINING_LANTERNS).remove(this);
            }
        }
    }

    @Override
    public void onDataPacket(Connection connection, ClientboundBlockEntityDataPacket pkt, HolderLookup.Provider registries) {
        CompoundTag tag = pkt.getTag();
        storage.fromNbt(tag);
        industrialTicksLeft = tag.getInt("IndustrialFuel");
        lunarTicksLeft = tag.getInt("LunarFuel");
        livingTicksLeft = tag.getInt("LivingFuel");
        primeTicksLeft = tag.getInt("PrimeFuel");
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider registries) {
        CompoundTag tag = new CompoundTag();
        tag.put("EssenceStorage", storage.toNbt());
        tag.putInt("IndustrialFuel", industrialTicksLeft);
        tag.putInt("LunarFuel", lunarTicksLeft);
        tag.putInt("LivingFuel", livingTicksLeft);
        tag.putInt("PrimeFuel", primeTicksLeft);
        return tag;
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries) {
        tag.put("EssenceStorage", storage.toNbt());
        tag.putInt("C1X", corner1.getX());
        tag.putInt("C1Y", corner1.getY());
        tag.putInt("C1Z", corner1.getZ());
        tag.putInt("C2X", corner2.getX());
        tag.putInt("C2Y", corner2.getY());
        tag.putInt("C2Z", corner2.getZ());
        tag.putInt("IndustrialFuel", industrialTicksLeft);
        tag.putInt("LunarFuel", lunarTicksLeft);
        tag.putInt("LivingFuel", livingTicksLeft);
        tag.putInt("PrimeFuel", primeTicksLeft);
        super.saveAdditional(tag, registries);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        storage.fromNbt(tag.getCompound("EssenceStorage"));
        corner1 = new BlockPos(
                tag.getInt("C1X"),
                tag.getInt("C1Y"),
                tag.getInt("C1Z")
        );
        corner2 = new BlockPos(
                tag.getInt("C2X"),
                tag.getInt("C2Y"),
                tag.getInt("C2Z")
        );
        industrialTicksLeft = tag.getInt("IndustrialFuel");
        lunarTicksLeft = tag.getInt("LunarFuel");
        livingTicksLeft = tag.getInt("LivingFuel");
        primeTicksLeft = tag.getInt("PrimeFuel");
    }
}
