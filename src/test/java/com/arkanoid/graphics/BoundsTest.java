package com.arkanoid.graphics;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import com.arkanoid.graphics.Bounds;
import com.arkanoid.utils.BallHit;

class BoundsTest {

    @Test
    void testContains() {
        Bounds b = new Bounds(10, 10, 20, 20);
        assertTrue(b.contains(15, 15));
        assertFalse(b.contains(5, 5));
    }

    @Test
    void testIntersects() {
        Bounds b1 = new Bounds(0, 0, 10, 10);
        Bounds b2 = new Bounds(5, 5, 10, 10);
        Bounds b3 = new Bounds(20, 20, 5, 5);

        assertTrue(b1.intersects(b2));
        assertFalse(b1.intersects(b3));
    }

    @Test
    void testComputeBallHitTop() {
        Bounds b = new Bounds(0, 0, 10, 10);
        double x0 = 5, y0 = -5, x1 = 5, y1 = 5; // bóng di chuyển từ trên xuống
        double r = 1;

        BallHit hit = b.computeBallHit(x0, y0, x1, y1, r);
        assertNotNull(hit);
        assertTrue(hit.inverseVy); // va chạm với top
        assertEquals(b.minY - r, hit.yHit, 0.001);
    }

    @Test
    void testComputeBallHitNone() {
        Bounds b = new Bounds(0, 0, 10, 10);
        double x0 = -5, y0 = -5, x1 = -1, y1 = -1; // bóng đi xa không va chạm
        double r = 1;

        BallHit hit = b.computeBallHit(x0, y0, x1, y1, r);
        assertNotNull(hit);

    }
}

