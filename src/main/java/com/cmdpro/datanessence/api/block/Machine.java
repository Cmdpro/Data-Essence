package com.cmdpro.datanessence.api.block;

import net.minecraft.core.Direction;

import java.util.List;

/**
 * For methods and behaviours common to all machines.
 */
public interface Machine {

    /**
     * Which sides can automation access this block's input slots from?
     * @return List of allowed Directions for automated input
     */
    default List<Direction> getValidInputDirections() {
        return List.of(Direction.DOWN);
    }
}
