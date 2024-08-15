package com.github.kleesup.kleeswept.world;

import com.badlogic.gdx.utils.Pool;
import com.github.kleesup.kleeswept.world.body.ISweptBody;

import java.util.*;

/**
 * The object that is returned when a collision test was done.
 * <br>Created on 13.09.2023</br>
 * @author KleeSup
 * @version 1.3
 * @since 1.0.1
 */
public class CollisionResponse {

    /** The AABB that was tested **/
    public ISweptBody body;
    /** The next best goal position-x (original x + {@link #updatedDisplacementX}) **/
    public float bestGoalX;
    /** The next best goal position-y (original y + {@link #updatedDisplacementY}) **/
    public float bestGoalY;
    /** The updated displacement x (already calculated into {@link #bestGoalX}). **/
    public float updatedDisplacementX;
    /** The updated displacement y (already calculated into {@link #bestGoalY}). **/
    public float updatedDisplacementY;
    private final List<Collision> collisions = new LinkedList<>();

    /**
     * Clears the object so it can be reused.
     */
    public void clear(){
        collisions.clear();
        body = null;
    }

    public List<Collision> getCollisions() {
        return collisions;
    }

    /**
     * The single collision record for the AABB vs a targeted AABB.
     */
    public static class Collision implements Pool.Poolable {
        /** The targeted AABB which was tested against **/
        public ISweptBody target;
        /** Whether that AABB is currently overlapping the targeted **/
        public boolean isOverlapping;
        /**
         *  Interpretation:
         *  1.0f -> target AABB was hit on the right side.
         *  -1.0f -> target AABB was hit on the left side.
         *  0.0f -> target AABB wasn't hit on left or right, it's probably a vertical hit.
         */
        public byte normalX;
        /**
         *  Interpretation:
         *  1.0f -> target AABB was hit on the top side.
         *  -1.0f -> target AABB was hit on the bottom side.
         *  0.0f -> target AABB wasn't hit on top or bottom, it's probably a horizontal hit.
         */
        public byte normalY;

        /**
         * A number between 0 and 1 that determines how much the colliding AABB needs to move out of another AABB.
         */
        public float hitTime;

        /**
         * Whether this collision was resolved.
         */
        public boolean resolved;

        public Collision(ISweptBody target, boolean isOverlapping, byte normalX, byte normalY, float hitTime) {
            this.target = target;
            this.isOverlapping = isOverlapping;
            this.normalX = normalX;
            this.normalY = normalY;
            this.hitTime = hitTime;
        }

        public Collision() {
        }

        public Collision set(ISweptBody target, boolean overlap, byte nx, byte ny, float ht, boolean res){
            this.target = target;
            this.isOverlapping = overlap;
            this.normalX = nx;
            this.normalY = ny;
            this.hitTime = ht;
            this.resolved = res;
            return this;
        }

        public boolean wasHitHorizontally(){
            return wasHitRight() || wasHitLeft();
        }
        public boolean wasHitRight(){
            return normalX == 1;
        }
        public boolean wasHitLeft(){
            return normalX == -1;
        }

        public boolean wasHitVertically(){
            return wasHitTop() || wasHitBottom();
        }
        public boolean wasHitTop(){
            return normalY == 1;
        }
        public boolean wasHitBottom(){
            return normalY == -1;
        }

        @Override
        public void reset() {
            target = null;
            isOverlapping = false;
            normalX = 0;
            normalY = 0;
            hitTime = 0;
        }
    }


}
