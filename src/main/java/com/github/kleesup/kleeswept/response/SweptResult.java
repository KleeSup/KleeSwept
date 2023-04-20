package com.github.kleesup.kleeswept.response;

import com.github.kleesup.kleeswept.AABB;
import com.github.kleesup.kleeswept.KleeHelper;
import com.github.kleesup.kleeswept.KleeSweptDetection;

/**
 * A class that is returned by methods that calculate a swept collision by using a sum-AABB (methods contained in {@link KleeSweptDetection}).
 * <br>Created on 17.04.2023</br>
 * @author KleeSup
 * @version 1.0
 * @since 1.0.0
 */
public class SweptResult extends SimpleSweptResult {

    /** The stored sum of both colliders, saved for further collision response. */
    public AABB sumAABB;

    /** Represents the hit position with the {@link #sumAABB}. */
    public float sumX, sumY;

    /** Determines if the {@link #x} and {@link #y} where fixed and no longer represent {@link #sumX} and {@link #sumY}. */
    public boolean isHitPosFixed;

    @Override
    public void reset() {
        super.reset();
        sumAABB = null;
        isHitPosFixed = false;
    }
}
