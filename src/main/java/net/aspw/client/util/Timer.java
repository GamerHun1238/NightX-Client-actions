package net.aspw.client.util;

import net.minecraft.util.MathHelper;

/**
 * The type Timer.
 */
public final class Timer {

    /* fields */
    private long lastMS;
    private long previousTime;

    /**
     * Instantiates a new Timer.
     */
    /* constructors */
    public Timer() {
        this.lastMS = 0L;
        this.previousTime = -1L;
    }

    /**
     * Sleep boolean.
     *
     * @param time the time
     * @return the boolean
     */
    /* methods */
    public boolean sleep(long time) {
        if (time() >= time) {
            reset();
            return true;
        }

        return false;
    }

    /**
     * Check boolean.
     *
     * @param milliseconds the milliseconds
     * @return the boolean
     */
    public boolean check(float milliseconds) {
        return System.currentTimeMillis() - previousTime >= milliseconds;
    }

    /**
     * Delay boolean.
     *
     * @param milliseconds the milliseconds
     * @return the boolean
     */
    public boolean delay(double milliseconds) {
        return MathHelper.clamp_float(getCurrentMS() - lastMS, 0, (float) milliseconds) >= milliseconds;
    }

    /**
     * Reset.
     */
    public void reset() {
        this.previousTime = System.currentTimeMillis();
        this.lastMS = getCurrentMS();
    }

    /**
     * Time long.
     *
     * @return the long
     */
    public long time() {
        return System.nanoTime() / 1000000L - lastMS;
    }

    /**
     * Gets current ms.
     *
     * @return the current ms
     */
    public long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

    /**
     * Gets last delay.
     *
     * @return the last delay
     */
    public double getLastDelay() {
        return getCurrentMS() - getLastMS();
    }

    /**
     * Gets last ms.
     *
     * @return the last ms
     */
    public long getLastMS() {
        return lastMS;
    }
}