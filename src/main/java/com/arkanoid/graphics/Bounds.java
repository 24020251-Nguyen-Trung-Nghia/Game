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
        // Increased +r bounds to simplify computation and focus only on ball center
        double minXr = minX - r, minYr = minY - r, maxXr = maxX + r, maxYr = maxY + r;
        double yHit = 0, xHit = 0;
        boolean hit = false, inverseVy = false, inverseVx = false;
        // Did the ball hit the bottom border?
        if (y0 >= maxYr && y1 <= maxYr) { // Means that the ball crossed the bottom line (while moving up)
            xHit = Helper.computeLineIntersectionX(-1, maxYr, 1, maxYr, x0, y0, x1, y1); // Where on X?
            hit = xHit >= minXr && xHit <= maxXr; // X condition for a hit
            if (hit) {
                yHit = maxYr;
                inverseVy = true;
            }
        }
        // If not, did it hit the top border?
        if (!hit && y0 <= minYr && y1 >= minYr) { // Means that the ball crossed the top line (while moving down)
            xHit = Helper.computeLineIntersectionX(-1, minYr, 1, minYr, x0, y0, x1, y1); // Where on X?
            hit = xHit >= minXr && xHit <= maxXr; // X condition for a hit
            if (hit) {
                yHit = minYr;
                inverseVy = true;
            }
        }
        // If not, did it hit the left border?
        if (!hit && x0 <= minXr && x1 >= minXr) { // Means that the ball crossed the left line (while moving to right)
            yHit = Helper.computeLineIntersectionY(minXr, 1, minXr, -1, x0, y0, x1, y1); // Where on Y?
            hit = yHit >= minYr && yHit <= maxYr; // Y condition for a hit
            if (hit) {
                xHit = minXr;
                inverseVx = true;
            }
        }
        // If not, did it hit the right border?
        if (!hit && x0 >= maxXr && x1 <= maxXr) { // Means that the ball crossed the right line (while moving to left)
            yHit = Helper.computeLineIntersectionY(maxXr, 1, maxXr, -1, x0, y0, x1, y1); // Where on Y?
            hit = yHit >= minYr && yHit <= maxYr; // Y condition for a hit
            if (hit) {
                xHit = maxXr;
                inverseVx = true;
            }
        }
        // If not, is the ball inside the bounds? (this may happen with the paddle moving quickly)
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
