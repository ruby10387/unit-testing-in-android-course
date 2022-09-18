package com.techyourchance.unittestingfundamentals.exercise1;

import static org.hamcrest.CoreMatchers.is;

import org.hamcrest.MatcherAssert;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class NegativeNumberValidatorTest {
    NegativeNumberValidator SUT;

    @Before
    public void setup() {
        SUT = new NegativeNumberValidator();
    }

    @Test
    public void test1() {
        boolean result = SUT.isNegative(1);
//        MatcherAssert.assertThat(result, is(false)); //The replacement of Assert.assertThat
        Assert.assertThat(result, is(false));
    }

    @Test
    public void test2() {
        boolean result = SUT.isNegative(0);
        Assert.assertThat(result, is(false));
    }

    @Test
    public void test3() {
        boolean result = SUT.isNegative(-1);
        Assert.assertThat(result, is(true));
    }
}