package util;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static util.SumFormatter.calculateUserDelivery;

public class SumFormatterTest {

    @Test
    public void calculateDelivery() {
        assertEquals(createPrice(2.5), calculateUserDelivery(2.5, 0));
        assertEquals(createPrice(2.5), calculateUserDelivery(2.5, 1));
        assertEquals(createPrice(1.25), calculateUserDelivery(2.5, 2));
        assertEquals(createPrice(0.84), calculateUserDelivery(2.5, 3));
        assertEquals(createPrice(0.63), calculateUserDelivery(2.5, 4));
        assertEquals(createPrice(0.5), calculateUserDelivery(2.5, 5));
        assertEquals(createPrice(0.42), calculateUserDelivery(2.5, 6));
        assertEquals(createPrice(0.36), calculateUserDelivery(2.5, 7));
        assertEquals(createPrice(0.32), calculateUserDelivery(2.5, 8));
        assertEquals(createPrice(0.28), calculateUserDelivery(2.5, 9));
        assertEquals(createPrice(0.25), calculateUserDelivery(2.5, 10));
        assertEquals(createPrice(0.23), calculateUserDelivery(2.5, 11));
    }

    private BigDecimal createPrice(double val) {
        return new BigDecimal(val).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

}
