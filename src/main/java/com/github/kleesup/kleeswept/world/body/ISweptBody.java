package com.github.kleesup.kleeswept.world.body;

import com.github.kleesup.kleeswept.world.CollisionResponse;

/**
 * A custom world body interface to implement freely.
 * <br>Created on 13.09.2023</br>
 * @author KleeSup
 * @version 1.1
 * @since 1.0.1
 */
public interface ISweptBody {

    /**
     * A method which can be implemented to decide whether some collisions against some bodies should be resolved or not.
     * @param other The other body. Note that this object is the one CAUSING the collision.
     * @param collision The collision that has been found.
     * @return {@code true} if the collision should be resolved, {@code false} otherwise. (default is {@code true}).
     */
    default boolean resolveCollision(ISweptBody other, CollisionResponse.Collision collision){
        return true;
    }

}
