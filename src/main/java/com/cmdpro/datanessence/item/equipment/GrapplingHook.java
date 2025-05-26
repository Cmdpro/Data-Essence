package com.cmdpro.datanessence.item.equipment;

import com.cmdpro.databank.rendering.ColorUtil;
import com.cmdpro.datanessence.DataNEssence;
import com.cmdpro.datanessence.api.item.ItemEssenceContainer;
import com.cmdpro.datanessence.networking.ModMessages;
import com.cmdpro.datanessence.networking.packet.s2c.GrapplingHookSync;
import com.cmdpro.datanessence.registry.AttachmentTypeRegistry;
import com.cmdpro.datanessence.registry.DataComponentRegistry;
import net.minecraft.client.DeltaTracker;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.awt.*;
import java.util.List;
import java.util.Optional;

public class GrapplingHook extends Item {
    public static ResourceLocation FUEL_ESSENCE_TYPE = DataNEssence.locate("essence");
    public static float ESSENCE_COST = 5f;
    public GrapplingHook(Properties pProperties) {
        super(pProperties.component(DataComponentRegistry.ESSENCE_STORAGE, new ItemEssenceContainer(List.of(FUEL_ESSENCE_TYPE), 1000)));
    }
    public static Color getTransitionColor(Player player, DeltaTracker partialTick) {
        boolean shouldTrans = player.isHolding(GrapplingHook::isTrans);
        Color color = null;
        if (shouldTrans) {
            Color blue = new Color(0xff6ab3fc);
            Color white = new Color(0xffffffff);
            Color pink = new Color(0xfffc92bb);
            int transitionTicks = 200;
            float transitionProgress = (player.level().getGameTime() % transitionTicks)+partialTick.getGameTimeDeltaPartialTick(true);
            transitionProgress = transitionProgress/(transitionTicks/4f);
            if (transitionProgress <= 1f) {
                color = ColorUtil.blendColors(blue, white, transitionProgress);
            } else if (transitionProgress <= 2f) {
                color = ColorUtil.blendColors(white, pink, transitionProgress-1f);
            } else if (transitionProgress <= 3f) {
                color = ColorUtil.blendColors(pink, white, transitionProgress-2f);
            } else if (transitionProgress <= 4f) {
                color = ColorUtil.blendColors(white, blue, transitionProgress-3f);
            }
        }
        return color;
    }
    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pUsedHand) {
        ItemStack stack = pPlayer.getItemInHand(pUsedHand);
        boolean success = pPlayer.getData(AttachmentTypeRegistry.GRAPPLING_HOOK_DATA).isPresent() || ItemEssenceContainer.getEssence(stack, FUEL_ESSENCE_TYPE) >= 5;
        if (!pLevel.isClientSide) {
            if (pPlayer.getData(AttachmentTypeRegistry.GRAPPLING_HOOK_DATA).isPresent()) {
                pPlayer.setData(AttachmentTypeRegistry.GRAPPLING_HOOK_DATA, Optional.empty());
                ModMessages.sendToPlayersTrackingEntityAndSelf(new GrapplingHookSync(pPlayer.getId(), null), (ServerPlayer) pPlayer);
            } else {
                if (ItemEssenceContainer.getEssence(stack, FUEL_ESSENCE_TYPE) >= ESSENCE_COST) {
                    ItemEssenceContainer.removeEssence(stack, FUEL_ESSENCE_TYPE, ESSENCE_COST);
                    HitResult hit = pPlayer.pick(35, 0, false);
                    if (hit.getType() != HitResult.Type.MISS) {
                        Vec3 pos = hit.getLocation();
                        double distance = pos.distanceTo(pPlayer.position());
                        GrapplingHookData data = new GrapplingHookData(pos, distance);
                        pPlayer.setData(AttachmentTypeRegistry.GRAPPLING_HOOK_DATA, Optional.of(data));
                        ModMessages.sendToPlayersTrackingEntityAndSelf(new GrapplingHookSync(pPlayer.getId(), data), (ServerPlayer) pPlayer);
                    }
                }
            }
        }
        if (success) {
            return InteractionResultHolder.sidedSuccess(stack, pLevel.isClientSide);
        }
        return super.use(pLevel, pPlayer, pUsedHand);
    }
    public static boolean isTrans(ItemStack stack) {
        return stack.getItem() instanceof GrapplingHook && stack.getHoverName().getString().equalsIgnoreCase("trans tether");
    }
    public static class GrapplingHookData {
        public Vec3 pos;
        public double distance;
        public GrapplingHookData(Vec3 pos, double distance) {
            this.pos = pos;
            this.distance = distance;
        }
    }
}
