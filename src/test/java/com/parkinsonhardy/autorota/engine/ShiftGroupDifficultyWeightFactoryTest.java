package com.parkinsonhardy.autorota.engine;

import com.parkinsonhardy.autorota.engine.planner.ShiftDifficultyWeight;
import com.parkinsonhardy.autorota.engine.planner.ShiftGroupDifficultyWeightFactory;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ShiftGroupDifficultyWeightFactoryTest {

    private RotaSolution emptySolution = new RotaSolution(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    @Test
    public void testBothOnWeekend() {
        DateTime saturday = DateTime.parse("2019-03-09");
        Shift shiftA = new Shift(0, "Test", saturday, saturday);
        Shift shiftB = new Shift(1, "Test", saturday.plusDays(1), saturday.plusDays(1));
        ShiftGroup shiftGroupA = new ShiftGroup(shiftA);
        ShiftGroup shiftGroupB = new ShiftGroup(shiftB);

        ShiftGroupDifficultyWeightFactory shiftDifficultyWeightFactory = new ShiftGroupDifficultyWeightFactory();
        ShiftDifficultyWeight aWeight = shiftDifficultyWeightFactory.createSorterWeight(emptySolution, shiftGroupA);
        ShiftDifficultyWeight bWeight = shiftDifficultyWeightFactory.createSorterWeight(emptySolution, shiftGroupB);

        // order unchanged
        Assert.assertEquals(aWeight.getDifficulty(), bWeight.getDifficulty());
    }

    @Test
    public void testOneOnWeekend() {
        DateTime saturday = DateTime.parse("2019-03-09");
        Shift shiftA = new Shift(0, "Test", saturday, saturday);
        Shift shiftB = new Shift(1, "Test", saturday.plusDays(2), saturday.plusDays(2));
        ShiftGroup shiftGroupA = new ShiftGroup(shiftA);
        ShiftGroup shiftGroupB = new ShiftGroup(shiftB);
        ShiftGroupDifficultyWeightFactory shiftDifficultyWeightFactory = new ShiftGroupDifficultyWeightFactory();
        ShiftDifficultyWeight aWeight = shiftDifficultyWeightFactory.createSorterWeight(emptySolution, shiftGroupA);
        ShiftDifficultyWeight bWeight = shiftDifficultyWeightFactory.createSorterWeight(emptySolution, shiftGroupB);
        List<ShiftDifficultyWeight> shifts = new ArrayList<>();
        shifts.add(aWeight);
        shifts.add(bWeight);
        shifts.sort(ShiftDifficultyWeight::compareTo);

        // b first
        Assert.assertEquals(shiftB.getShiftId(), shifts.get(0).getShiftId());
        Assert.assertEquals(shiftA.getShiftId(), shifts.get(1).getShiftId());
    }

    @Test
    public void testMultiple() {
        DateTime saturday = DateTime.parse("2019-03-09");
        ShiftGroup shiftGroupA = new ShiftGroup(new Shift(0, "Test", saturday, saturday));
        ShiftGroup shiftGroupB = new ShiftGroup(new Shift(1, "Test", saturday.plusDays(2), saturday.plusDays(2)));
        ShiftGroup shiftGroupC = new ShiftGroup(new Shift(2, "Test", saturday.plusDays(3), saturday.plusDays(3)));
        ShiftGroup shiftGroupD = new ShiftGroup(new Shift(3, "Test", saturday.plusDays(7), saturday.plusDays(7)));
        ShiftGroup shiftGroupE = new ShiftGroup(new Shift(4, "Test", saturday.plusDays(8), saturday.plusDays(8)));

        ShiftGroupDifficultyWeightFactory shiftDifficultyWeightFactory = new ShiftGroupDifficultyWeightFactory();
        ShiftDifficultyWeight aWeight = shiftDifficultyWeightFactory.createSorterWeight(emptySolution, shiftGroupA);
        ShiftDifficultyWeight bWeight = shiftDifficultyWeightFactory.createSorterWeight(emptySolution, shiftGroupB);
        ShiftDifficultyWeight cWeight = shiftDifficultyWeightFactory.createSorterWeight(emptySolution, shiftGroupC);
        ShiftDifficultyWeight dWeight = shiftDifficultyWeightFactory.createSorterWeight(emptySolution, shiftGroupD);
        ShiftDifficultyWeight eWeight = shiftDifficultyWeightFactory.createSorterWeight(emptySolution, shiftGroupE);
        List<ShiftDifficultyWeight> shifts = new ArrayList<>();
        shifts.add(aWeight);
        shifts.add(bWeight);
        shifts.add(cWeight);
        shifts.add(dWeight);
        shifts.add(eWeight);
        shifts.sort(ShiftDifficultyWeight::compareTo);

        // expected order
        Assert.assertEquals(shiftGroupC.getId(), shifts.get(0).getShiftId());
        Assert.assertEquals(shiftGroupB.getId(), shifts.get(1).getShiftId());
        Assert.assertEquals(shiftGroupE.getId(), shifts.get(2).getShiftId());
        Assert.assertEquals(shiftGroupD.getId(), shifts.get(3).getShiftId());
        Assert.assertEquals(shiftGroupA.getId(), shifts.get(4).getShiftId());
    }

}
