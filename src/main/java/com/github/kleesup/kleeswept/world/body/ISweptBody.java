package com.github.kleesup.kleeswept.world.body;

import com.github.kleesup.kleeswept.world.CollisionResponse;

/**
 * A custom world body interface to implement freely.
 * <br>Created on 13.09.2023</br>
 * @author KleeSup
 * @version 1.3
 * @since 1.0.1
 */
public interface ISweptBody {

    /**
     * Before a collision is checked, calculated and put into the iteration list, this method will be called.
     * Therefore, if there are any objects that should never generate a collision response (i.e. static vs static),
     * this should be changed.
     * @param other The other body this body <i>might</i> collide with.
     * @return Whether the collision should be checked in the first place.
     */
    default boolean checkCollision(ISweptBody other){
        return true;
    }

    /**
     * A method which can be implemented to decide whether some collisions against some bodies should be resolved or not.
     * @param other The other body. Note that this object is the collision is tested AGAINST.
     * @param collision The collision that has been found.
     * @return {@code true} if the collision should be resolved, {@code false} otherwise. (default is {@code true}).
     */
    default boolean resolveCollision(ISweptBody other, CollisionResponse.Collision collision){
        return true;
    }

}
