package com.arkanoid.graphics;

import com.arkanoid.utils.BallHit;
import com.arkanoid.utils.Helper;

public class Bounds {
    public double x;
    public double y;
    public double width;
    public double height;
    public double minX;
    public double minY;
    public double maxX;
    public double maxY;
    public double centerX;
    public double centerY;


    // ******************** Constructors **************************************
    public Bounds() {
        this(0, 0, 0, 0);
    }

    public Bounds(final double width, final double height) {
        this(0, 0, width, height);
    }

    public Bounds(final double x, final double y, final double width, final double height) {
        set(x, y, width, height);
    }


    // ******************** Methods *******************************************
    public void set(final Bounds bounds) {
        set(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void set(final double x, final double y, final double width, final double height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.minX = x;
        this.minY = y;
        this.maxX = x + width;
        this.maxY = y + height;
        this.centerX = x + width * 0.5;
        this.centerY = y + height * 0.5;
    }

    public boolean contains(final double x, final double y) {
        return x >= minX && x <= maxX && y >= minY && y <= maxY;
    }

    public boolean intersects(final Bounds other) {
        return other.minX <= maxX && minX <= other.maxX && other.minY <= maxY && minY <= other.maxY;
    }

    public BallHit computeBallHit(double x0, double y0, double x1, double y1, double r) {
        // Mở rộng bounds với bán kính bóng
        double minXr = minX - r, minYr = minY - r, maxXr = maxX + r, maxYr = maxY + r;
        double yHit = 0, xHit = 0;
        boolean hit = false, inverseVy = false, inverseVx = false;
        // KIỂM TRA VA CHẠM VỚI ĐÁY (BOTTOM)
        if (y0 >= maxYr && y1 <= maxYr) {
            xHit = Helper.computeLineIntersectionX(-1, maxYr, 1, maxYr, x0, y0, x1, y1);
            hit = xHit >= minXr && xHit <= maxXr; // Giao điểm X có thuộc bounds?
            if (hit) {
                yHit = maxYr;
                inverseVy = true;
            }
        }
        // KIỂM TRA VA CHẠM VỚI ĐỈNH (TOP)
        if (!hit && y0 <= minYr && y1 >= minYr) {
            xHit = Helper.computeLineIntersectionX(-1, minYr, 1, minYr, x0, y0, x1, y1);
            hit = xHit >= minXr && xHit <= maxXr;
            if (hit) {
                yHit = minYr;
                inverseVy = true;
            }
        }
        // KIỂM TRA VA CHẠM VỚI TRÁI (LEFT) - chỉ nếu chưa va chạm
        if (!hit && x0 <= minXr && x1 >= minXr) {
            yHit = Helper.computeLineIntersectionY(minXr, 1, minXr, -1, x0, y0, x1, y1);
            hit = yHit >= minYr && yHit <= maxYr;
            if (hit) {
                xHit = minXr;
                inverseVx = true;
            }
        }
        // KIỂM TRA VA CHẠM VỚI PHẢI (RIGHT)
        if (!hit && x0 >= maxXr && x1 <= maxXr) { // Means that the ball crossed the right line (while moving to left)
            yHit = Helper.computeLineIntersectionY(maxXr, 1, maxXr, -1, x0, y0, x1, y1); // Where on Y?
            hit = yHit >= minYr && yHit <= maxYr; // Y condition for a hit
            if (hit) {
                xHit = maxXr;
                inverseVx = true;
            }
        }
        // Có thể xảy ra khi paddle di chuyển quá nhanh và nuốT bóng
        if (!hit && contains(x1, y1)) {
            hit = true;
            xHit = Helper.computeLineIntersectionX(-1, minYr, 1, minYr, x0, y0, x1, y1); // Where on X?
            yHit = minYr;
            //x0 = x1 = xHit;
            //y0 = y1 = yHit;
            inverseVy = true;
        }
        if (!hit)
            return null;
        // TẠO ĐỐI TƯỢNG BallHit CHỨA THÔNG TIN VA CHẠM
        BallHit ballHit = new BallHit(this);
        ballHit.xHit = xHit;
        ballHit.yHit = yHit;
        ballHit.inverseVx = inverseVx;
        ballHit.inverseVy = inverseVy;
        ballHit.beforeHitDistance = Helper.distance(x0, y0, xHit, yHit);
        double afterHitDistance = Helper.distance(xHit, yHit, x1, y1);
        ballHit.correctedX = inverseVx ? xHit - (x1 - xHit) * afterHitDistance : x1;
        ballHit.correctedY = inverseVy ? yHit - (y1 - yHit) * afterHitDistance : y1;
        return ballHit;
    }
}
