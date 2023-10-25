package com.github.kleesup.kleeswept.util;

import com.badlogic.gdx.math.Vector2;
import com.github.kleesup.kleeswept.world.CollisionResponse;
import com.github.kleesup.kleeswept.world.body.ISweptBody;

import java.util.Comparator;

/**
 * A builder class for creating {@link Comparator} objects to compare collisions when they occur.
 * This is needed to resolve collisions the best or wanted way.
 * <br>Created on 22.10.2023</br>
 * @author KleeSup
 * @version 1.0
 * @since 1.0.3
 */
public interface CollisionComparatorBuilder<Body extends ISweptBody> {

    Comparator<CollisionResponse.Collision> build(Body body, Vector2 displacement, float newWidth, float newHeight);

}
