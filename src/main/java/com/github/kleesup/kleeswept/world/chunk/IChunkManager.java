package com.github.kleesup.kleeswept.world.chunk;

import com.github.kleesup.kleeswept.world.body.ISweptAABB;

import java.util.Set;

/**
 * An interface for basic chunk management.
 * <br>Created on 13.09.2023</br>
 * @author KleeSup
 * @version 1.0
 * @since 1.0.1
 */
public interface IChunkManager<AABB extends ISweptAABB> {

    Set<AABB> getBodies(int chunkX, int chunkY);

    void addBody(int chunkX, int chunkY, AABB aabb);

    void removeBody(int chunkX, int chunkY, AABB aabb);

}
