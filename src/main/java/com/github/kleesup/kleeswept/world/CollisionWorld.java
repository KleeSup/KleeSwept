package com.github.kleesup.kleeswept.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.github.kleesup.kleeswept.world.body.ISweptAABB;

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
     * Checks whether an AABB is contained in this world.
     * @param aabb The AABB to check for.
     * @return {@code true} if the AABB is contained in this world, {@code false} otherwise.
     */
    boolean contains(AABB aabb);

    /**
     * Retrieves the bounding box of an AABB body.
     * @param aabb The AABB to retrieve the body for.
     * @return The rectangle bounding box (copy) of the AABB.
     */
    Rectangle getBoundingBox(AABB aabb);

    /**
     * Calculates collisions between the current position of the AABB and the goal position.
     * After that, the AABB is moved in the world to the goal position (or the one that is possible).
     * @param aabb The AABB to update.
     * @param displacement The displacement of the AABB.
     * @param width The new width.
     * @param height The new height.
     * @param writeTo An already existing SweptResponse object that can be written to.
     *                Therefore, no object creation will be needed but rather this object will be overridden.
     *                If set to {@code null} a new object will be created.
     * @return The result of the update.
     */
    CollisionResponse update(AABB aabb, Vector2 displacement, float width, float height, CollisionResponse writeTo);
    default CollisionResponse update(AABB aabb, Vector2 displacement, float width, float height){
        return update(aabb,displacement,width,height,null);
    }

    /**
     * Calculates collisions between the current position of the AABB and the goal position.
     * After that, the AABB is moved in the world to the goal position (or the one that is possible).
     * @param aabb The AABB to update.
     * @param displacement The displacement of the AABB.
     * @return The result of the update.
     */
    CollisionResponse update(AABB aabb, Vector2 displacement, CollisionResponse response);
    default CollisionResponse update(AABB aabb, Vector2 displacement){
        return update(aabb,displacement,null);
    }

    /**
     * Calculates collisions between the current position of the AABB and the goal position.
     * The difference to {@link #update(ISweptAABB, Vector2)} is that the AABB will not actually be moved.
     * @param aabb The AABB to simulate for.
     * @param displacement The displacement of the AABB.
     * @param width The new simulated width.
     * @param height The new simulated height.
     * @return The result of the simulation.
     */
    CollisionResponse simulate(AABB aabb, Vector2 displacement, float width, float height, CollisionResponse writeTo);
    default CollisionResponse simulate(AABB aabb, Vector2 displacement, float width, float height){
        return simulate(aabb,displacement,width,height,null);
    }

    /**
     * Calculates collisions between the current position of the AABB and the goal position.
     * The difference to {@link #update(ISweptAABB, Vector2)} is that the AABB will not actually be moved.
     * @param aabb The AABB to simulate for.
     * @param displacement The displacement of the AABB.
     * @return The result of the simulation.
     */
    CollisionResponse simulate(AABB aabb, Vector2 displacement, CollisionResponse writeTo);
    default CollisionResponse simulate(AABB aabb, Vector2 displacement){
        return simulate(aabb,displacement,null);
    }

    /**
     * Forces a teleport and/or resize of the specified AABB in the world.
     * Note that this will not check for collisions and will completely ignore any sort of potential moving into other AABBs.
     * @param aabb The AABB to force an update on.
     * @param goalX The wanted x-position.
     * @param goalY The wanted y-position.
     * @param width The new width of the AABB.
     * @param height The new height of the AABB.
     */
    void forceUpdate(AABB aabb, float goalX, float goalY, float width, float height);
    void forceUpdate(AABB aabb, float goalX, float goalY);

}
