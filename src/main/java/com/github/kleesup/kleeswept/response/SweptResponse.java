package com.github.kleesup.kleeswept.response;

import com.github.kleesup.kleeswept.SweptAABB;

/**
 * A simple container for a collision swept response between two AABBs.
 * Note that a returned response instance doesn't necessarily mean that there was a collision as {@link #isHit} can still be {@code false}.
 * <br>Created on 17.04.2023</br>
 * @author KleeSup
 * @version 1.0
 * @since 1.0.0
 */
public class SweptResponse {

    /** The AABB that was used for the collision. */
    public SweptAABB aabb;
    /** The AABB which may have been collided upon. */
    public SweptAABB other;
    /** Whether there was a collision. */
    public boolean isHit;
    /** The calculated hit-time. */
    public float time;
    /** The x-coordinate of the point at which the collision occurred. */
    public float x;
    /** The y-coordinate of the point at which the collision occurred. */
    public float y;
    /** Determines which side was hit horizontally (1 = right; -1 = left; 0 = none) */
    public float normalX;
    /** Determines which side was hit vertically (1 = top; -1 = bottom; 0 = none) */
    public float normalY;

    public void reset(){
        aabb = null;
        other = null;
        isHit = false;
        normalX = 0;
        normalY = 0;
    }

}
