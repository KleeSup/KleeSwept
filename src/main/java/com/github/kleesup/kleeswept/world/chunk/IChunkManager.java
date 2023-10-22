package com.github.kleesup.kleeswept.world.chunk;

import com.github.kleesup.kleeswept.world.body.ISweptBody;

import java.util.Set;

/**
 * An interface for basic chunk management.
 * <br>Created on 13.09.2023</br>
 * @author KleeSup
 * @version 1.0
 * @since 1.0.1
 */
public interface IChunkManager<Body extends ISweptBody> {

    Set<Body> getBodies(int chunkX, int chunkY);

    void addBody(int chunkX, int chunkY, Body body);

    void removeBody(int chunkX, int chunkY, Body body);

}
