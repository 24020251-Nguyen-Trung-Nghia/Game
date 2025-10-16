package com.arkanoid;

public class BallHit {
    public final Bounds hitBounds;
    public double beforeHitDistance;
    public double xHit;
    public double yHit;
    public double correctedX;
    public double correctedY;
    public boolean inverseVx;
    public boolean inverseVy;

    public double getBeforeHitDistance() {
        return beforeHitDistance;
    }

    public BallHit(Bounds hitBounds) {
        this.hitBounds = hitBounds;
    }

    public boolean isInverseVy() {
        return inverseVy;
    }

    public boolean isInverseVx() {
        return inverseVx;
    }

    public Bounds getHitBounds() {
        return hitBounds;
    }

    public double getXHit() {
        return xHit;
    }

    public double getYHit() {
        return yHit;
    }

    public double getCorrectedX() {
        return correctedX;
    }

    public double getCorrectedY() {
        return correctedY;
    }
}
