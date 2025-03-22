package com.cmdpro.datanessence;

import java.time.LocalDateTime;
import java.time.Month;

public class SpecialConditionHandler {
    public static boolean isAprilFools() {
        LocalDateTime time = LocalDateTime.now();
        return time.getMonth() == Month.APRIL && time.getDayOfMonth() == 1;
    }
}
