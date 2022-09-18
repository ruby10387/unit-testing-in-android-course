package com.techyourchance.unittestingfundamentals.exercise3;

import static org.hamcrest.CoreMatchers.is;

import com.techyourchance.unittestingfundamentals.example3.Interval;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class IntervalsAdjacencyDetectorTest {
    IntervalsAdjacencyDetector SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new IntervalsAdjacencyDetector();
    }

    //interval1 before interval2
    @Test
    public void isAdjacent_interval1BeforeInterval2_falseReturned() {
        Interval interval1 = new Interval(-1, 3);
        Interval interval2 = new Interval(6, 8);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(false));
    }
    //interval1 after interval2
    @Test
    public void isAdjacent_interval1AfterInterval2_falseReturned() {
        Interval interval1 = new Interval(-1, 3);
        Interval interval2 = new Interval(-5, -2);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(false));
    }
    //interval1 overlaps interval2 on start
    @Test
    public void isAdjacent_interval1OverlapsInterval2OnStart_falseReturned() {
        Interval interval1 = new Interval(-1, 6);
        Interval interval2 = new Interval(4, 8);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(false));
    }
    //interval1 overlaps interval2 on end
    @Test
    public void isAdjacent_interval1OverlapsInterval2OnEnd_falseReturned() {
        Interval interval1 = new Interval(-1, 6);
        Interval interval2 = new Interval(-4, 0);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(false));
    }
    //interval1 contains interval2
    @Test
    public void isAdjacent_interval1ContainsInterval2_falseReturned() {
        Interval interval1 = new Interval(-1, 6);
        Interval interval2 = new Interval(2, 5);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(false));
    }
    //interval1 contain within interval2
    @Test
    public void isAdjacent_interval1ContainWithinInterval2_falseReturned() {
        Interval interval1 = new Interval(-1, 6);
        Interval interval2 = new Interval(-4, 8);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(false));
    }
    //interval1 before and adjacent interval2
    @Test
    public void isAdjacent_interval1BeforeAndAdjacentInterval2_trueReturned() {
        Interval interval1 = new Interval(-1, 2);
        Interval interval2 = new Interval(2, 5);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(true));
    }
    //interval1 after and adjacent interval2
    @Test
    public void isAdjacent_interval1AfterAndAdjacentInterval2_trueReturned() {
        Interval interval1 = new Interval(-1, 2);
        Interval interval2 = new Interval(-4, -1);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(true));
    }

    //interval1 and interval2 are the same
    @Test
    public void isAdjacent_interval1EqualInterval2_falseReturned() {
        Interval interval1 = new Interval(-1, 0);
        Interval interval2 = new Interval(-1, 0);
        boolean result = SUT.isAdjacent(interval1, interval2);
        Assert.assertThat(result, is(false));
    }

}