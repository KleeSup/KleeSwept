package com.github.kleesup.kleeswept.world;

import com.badlogic.gdx.math.Rectangle;

/**
 * <br>Created on 13.09.2023</br>
 *
 * @author KleeSup
 * @version 1.0
 * @since 1.0.1
 */
public interface CollisionWorld<AABB extends ISweptAABB> {

    /**
     * Adds a new AABB body into the world.
     * @param aabb The AABB to add.
     * @param boundingBox The bounding box of the AABB.
     */
    void addBody(AABB aabb, Rectangle boundingBox);

    /**
     * Adds a new AABB body into the world.
     * @param aabb The AABB to add.
     * @param bbX The x position of the AABB's bounding box.
     * @param bbY The y position of the AABB's bounding box.
     * @param bbWidth The width of the AABB's bounding box.
     * @param bbHeight The height of the AABB's bounding box.
     */
    default void addBody(AABB aabb, float bbX, float bbY, float bbWidth, float bbHeight){
        addBody(aabb, new Rectangle(bbX, bbY, bbWidth, bbHeight));
    }

    /**
     * Removes an AABB from this world.
     * @param aabb The AABB to remove.
     * @return The bounding box of the AABB.
     */
    Rectangle removeBody(AABB aabb);

    /**
     * Retrieves the bounding box of an AABB body.
     * @param aabb The AABB to retrieve the body for.
     * @return The rectangle bounding box (copy) of the AABB.
     */
    Rectangle getBoundingBox(AABB aabb);

    /**
     * Calculates collisions between the current position of the AABB and the goal position.
     * After that, the AABB is moved in the world to the goal position (or the one that is possible).
     * @param aabb
     * @param goalX
     * @param goalY
     * @return
     */
    SweptResponse move(AABB aabb, float goalX, float goalY);

    /**
     * Calculates collisions between the current position of the AABB and the goal position.
     * The difference to {@link #move(ISweptAABB, float, float)} is that the AABB will not actually be moved.
     * @param aabb
     * @param goalX
     * @param goalY
     * @return
     */
    SweptResponse simulate(AABB aabb, float goalX, float goalY);

}
