package com.cmdpro.datanessence.api.node;

import com.cmdpro.datanessence.api.node.block.BaseCapabilityPointBlockEntity;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class CapabilityNodePath {
    public BaseCapabilityPointBlockEntity start;
    public BaseCapabilityPointBlockEntity[] ends;
    public CapabilityNodePath(BaseCapabilityPointBlockEntity start, BaseCapabilityPointBlockEntity[] ends) {
        this.start = start;
        this.ends = ends;
    }
    public static void updatePaths(BaseCapabilityPointBlockEntity tryStart) {
        if (tryStart.linkFrom.isEmpty()) {
            tryStart.path = calculate(tryStart);
        } else {
            for (BlockPos i : tryStart.linkFrom) {
                if (tryStart.getLevel().getBlockEntity(i) instanceof BaseCapabilityPointBlockEntity ent) {
                    updatePaths(ent);
                }
            }
        }
    }
    public static CapabilityNodePath calculate(BaseCapabilityPointBlockEntity start) {
        List<BaseCapabilityPointBlockEntity> alreadyVisited = new ArrayList<>();
        BaseCapabilityPointBlockEntity node = start;
        alreadyVisited.add(node);
        List<BaseCapabilityPointBlockEntity> ends = getEnds(node, alreadyVisited);
        CapabilityNodePath path = new CapabilityNodePath(start, ends.toArray(new BaseCapabilityPointBlockEntity[0]));
        return path;
    }
    public static List<BaseCapabilityPointBlockEntity> getEnds(BaseCapabilityPointBlockEntity node, List<BaseCapabilityPointBlockEntity> alreadyVisited) {
        List<BaseCapabilityPointBlockEntity> ends = new ArrayList<>();
        for (BlockPos i : node.link) {
            if (node.getLevel().getBlockEntity(i) instanceof BaseCapabilityPointBlockEntity ent) {
                if (!alreadyVisited.contains(ent)) {
                    alreadyVisited.add(ent);
                    if (ent.link.isEmpty()) {
                        ends.add(ent);
                    } else {
                        ends.addAll(getEnds(ent, alreadyVisited));
                    }
                } else {
                    break;
                }
            } else {
                break;
            }
        }
        return ends;
    }
}
