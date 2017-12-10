package com.woodsho.absoluteplan.common;

import com.woodsho.absoluteplan.bean.PlanTask;

import java.util.Comparator;

/**
 * Created by hewuzhao on 17/12/10.
 */

public class PlanTaskComparator implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {
        if (o1 instanceof PlanTask && o2 instanceof PlanTask) {
            PlanTask task1 = (PlanTask) o1;
            PlanTask task2 = (PlanTask) o2;
            return task1.time < task2.time ? 1 : 0;
        }
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }
}
