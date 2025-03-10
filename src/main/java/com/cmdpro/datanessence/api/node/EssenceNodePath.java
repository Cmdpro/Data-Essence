package com.cmdpro.datanessence.api.node;

import com.cmdpro.datanessence.api.node.block.BaseEssencePointBlockEntity;
import net.minecraft.core.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class EssenceNodePath {
    public BaseEssencePointBlockEntity start;
    public NodePathEnd[] ends;
    public EssenceNodePath(BaseEssencePointBlockEntity start, NodePathEnd[] ends) {
        this.start = start;
        this.ends = ends;
    }
    public static void updatePaths(BaseEssencePointBlockEntity tryStart) {
        if (tryStart.linkFrom.isEmpty()) {
            tryStart.path = calculate(tryStart);
        } else {
            for (BlockPos i : tryStart.linkFrom) {
                if (tryStart.getLevel().getBlockEntity(i) instanceof BaseEssencePointBlockEntity ent) {
                    updatePaths(ent);
                }
            }
        }
    }
    public static EssenceNodePath calculate(BaseEssencePointBlockEntity start) {
        List<BaseEssencePointBlockEntity> alreadyVisited = new ArrayList<>();
        BaseEssencePointBlockEntity node = start;
        alreadyVisited.add(node);
        List<NodePathEnd> ends = getEnds(node, alreadyVisited);
        EssenceNodePath path = new EssenceNodePath(start, ends.toArray(new NodePathEnd[0]));
        return path;
    }
    public static List<NodePathEnd> getEnds(BaseEssencePointBlockEntity node, List<BaseEssencePointBlockEntity> alreadyVisited) {
        List<NodePathEnd> ends = new ArrayList<>();
        for (BlockPos i : node.link) {
            if (node.getLevel().getBlockEntity(i) instanceof BaseEssencePointBlockEntity ent) {
                if (!alreadyVisited.contains(ent)) {
                    alreadyVisited.add(ent);
                    if (ent.link.isEmpty()) {
                        ends.add(new NodePathEnd(ent, alreadyVisited.toArray(new BaseEssencePointBlockEntity[0])));
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
