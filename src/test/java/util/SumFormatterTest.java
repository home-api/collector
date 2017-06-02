package util;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class SumFormatterTest {

    @Test
    public void calculateDelivery() {
        assertEquals(createBigDecimal(2.5), SumFormatter.calculateUserDelivery(2.5, 1));
        assertEquals(createBigDecimal(1.25), SumFormatter.calculateUserDelivery(2.5, 2));
        assertEquals(createBigDecimal(0.84), SumFormatter.calculateUserDelivery(2.5, 3));
        assertEquals(createBigDecimal(0.63), SumFormatter.calculateUserDelivery(2.5, 4));
        assertEquals(createBigDecimal(0.5), SumFormatter.calculateUserDelivery(2.5, 5));
        assertEquals(createBigDecimal(0.42), SumFormatter.calculateUserDelivery(2.5, 6));
        assertEquals(createBigDecimal(0.36), SumFormatter.calculateUserDelivery(2.5, 7));
        assertEquals(createBigDecimal(0.32), SumFormatter.calculateUserDelivery(2.5, 8));
        assertEquals(createBigDecimal(0.28), SumFormatter.calculateUserDelivery(2.5, 9));
        assertEquals(createBigDecimal(0.25), SumFormatter.calculateUserDelivery(2.5, 10));
        assertEquals(createBigDecimal(0.23), SumFormatter.calculateUserDelivery(2.5, 11));
    }

    private BigDecimal createBigDecimal(double val) {
        return new BigDecimal(val).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

}