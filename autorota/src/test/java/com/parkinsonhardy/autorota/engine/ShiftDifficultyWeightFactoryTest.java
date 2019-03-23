package com.parkinsonhardy.autorota.engine;

import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ShiftDifficultyWeightFactoryTest {

    private RotaSolution emptySolution = new RotaSolution(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

    @Test
    public void testBothOnWeekend() {
        DateTime saturday = DateTime.parse("2019-03-09");
        Shift shiftA = new Shift(0, "Test", saturday, saturday);
        Shift shiftB = new Shift(1, "Test", saturday.plusDays(1), saturday.plusDays(1));

        ShiftDifficultyWeightFactory shiftDifficultyWeightFactory = new ShiftDifficultyWeightFactory();
        ShiftDifficultyWeight aWeight = shiftDifficultyWeightFactory.createSorterWeight(emptySolution, shiftA);
        ShiftDifficultyWeight bWeight = shiftDifficultyWeightFactory.createSorterWeight(emptySolution, shiftB);

        // order unchanged
        Assert.assertEquals(aWeight.getDifficulty(), bWeight.getDifficulty());
    }

    @Test
    public void testOneOnWeekend() {
        DateTime saturday = DateTime.parse("2019-03-09");
        Shift shiftA = new Shift(0, "Test", saturday, saturday);
        Shift shiftB = new Shift(1, "Test", saturday.plusDays(2), saturday.plusDays(2));
        ShiftDifficultyWeightFactory shiftDifficultyWeightFactory = new ShiftDifficultyWeightFactory();
        ShiftDifficultyWeight aWeight = shiftDifficultyWeightFactory.createSorterWeight(emptySolution, shiftA);
        ShiftDifficultyWeight bWeight = shiftDifficultyWeightFactory.createSorterWeight(emptySolution, shiftB);
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
        Shift shiftA = new Shift(0, "Test", saturday, saturday);
        Shift shiftB = new Shift(1, "Test", saturday.plusDays(2), saturday.plusDays(2));
        Shift shiftC = new Shift(2, "Test", saturday.plusDays(3), saturday.plusDays(3));
        Shift shiftD = new Shift(3, "Test", saturday.plusDays(7), saturday.plusDays(7));
        Shift shiftE = new Shift(4, "Test", saturday.plusDays(8), saturday.plusDays(8));

        ShiftDifficultyWeightFactory shiftDifficultyWeightFactory = new ShiftDifficultyWeightFactory();
        ShiftDifficultyWeight aWeight = shiftDifficultyWeightFactory.createSorterWeight(emptySolution, shiftA);
        ShiftDifficultyWeight bWeight = shiftDifficultyWeightFactory.createSorterWeight(emptySolution, shiftB);
        ShiftDifficultyWeight cWeight = shiftDifficultyWeightFactory.createSorterWeight(emptySolution, shiftC);
        ShiftDifficultyWeight dWeight = shiftDifficultyWeightFactory.createSorterWeight(emptySolution, shiftD);
        ShiftDifficultyWeight eWeight = shiftDifficultyWeightFactory.createSorterWeight(emptySolution, shiftE);
        List<ShiftDifficultyWeight> shifts = new ArrayList<>();
        shifts.add(aWeight);
        shifts.add(bWeight);
        shifts.add(cWeight);
        shifts.add(dWeight);
        shifts.add(eWeight);
        shifts.sort(ShiftDifficultyWeight::compareTo);

        // expected order
        Assert.assertEquals(shiftC.getShiftId(), shifts.get(0).getShiftId());
        Assert.assertEquals(shiftB.getShiftId(), shifts.get(1).getShiftId());
        Assert.assertEquals(shiftE.getShiftId(), shifts.get(2).getShiftId());
        Assert.assertEquals(shiftD.getShiftId(), shifts.get(3).getShiftId());
        Assert.assertEquals(shiftA.getShiftId(), shifts.get(4).getShiftId());
    }

}
