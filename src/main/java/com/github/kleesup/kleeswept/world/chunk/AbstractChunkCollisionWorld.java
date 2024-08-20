package com.github.kleesup.kleeswept.world.chunk;

import com.badlogic.gdx.math.Rectangle;
import com.github.kleesup.kleeswept.KleeHelper;
import com.github.kleesup.kleeswept.world.CollisionWorld;
import com.github.kleesup.kleeswept.world.body.ISweptBody;

import java.util.function.BiConsumer;

/**
 * An implementation of {@link CollisionWorld} which offers a chunk cache {@link IChunkManager}.
 * <br>Created on 13.09.2023</br>
 * @author KleeSup
 * @version 1.2
 * @since 1.0.1
 */
public abstract class AbstractChunkCollisionWorld<Body extends ISweptBody> implements CollisionWorld<Body> {

    protected final IChunkManager<Body> chunkManager;

    protected final int chunkSize;
    protected final float invChunkSize;
    protected AbstractChunkCollisionWorld(int chunkSize, IChunkManager<Body> chunkManager){
        if(chunkSize < 1)throw new IllegalArgumentException("The chunk size cannot be smaller than 1!");
        this.chunkSize = chunkSize;
        this.chunkManager = chunkManager;
        this.invChunkSize = 1f / chunkSize;
    }

    /**
     * Adds an AABB to all chunks containing its bounding box.
     * @param body The AABB to add.
     * @param rectangle The bounding box of the AABB.
     */
    protected void addToContainedChunks(Body body, Rectangle rectangle){
        forContainingChunk(rectangle, (chunkX, chunkY) -> chunkManager.addBody(chunkX, chunkY, body));
    }

    /**
     * Removes an AABB from all chunks containing its bounding box.
     * @param body The AABB to remove.
     * @param rectangle The bounding box of the AABB.
     */
    protected void removeFromContainedChunks(Body body, Rectangle rectangle){
        forContainingChunk(rectangle, (chunkX, chunkY) -> chunkManager.removeBody(chunkX, chunkY, body));
    }

    /**
     * Loops through all chunks the AABBs bounding box takes space in.
     * @param rectangle The bounding box of the AABB.
     * @param coordinateConsumer The action that should be performed for each chunk.
     */
    protected void forContainingChunk(Rectangle rectangle, BiConsumer<Integer, Integer> coordinateConsumer){
        if(coordinateConsumer == null)return;
        int chunksX = KleeHelper.chunkFloor((rectangle.x + rectangle.width) * invChunkSize);
        int chunksY = KleeHelper.chunkFloor((rectangle.y + rectangle.height) * invChunkSize);;
        int startChunkX = KleeHelper.chunkFloor(rectangle.x * invChunkSize);
        int startChunkY = KleeHelper.chunkFloor(rectangle.y * invChunkSize);
        //in case the hole rectangle is only in one chunk
        if(startChunkX == chunksX && startChunkY == chunksY){
            coordinateConsumer.accept(startChunkX, startChunkY);
            return;
        }
        for(int x = startChunkX; x <= chunksX; x++){
            for(int y = startChunkY; y <= chunksY; y++){
                coordinateConsumer.accept(x,y);
            }
        }

    }

    /**
     * Checks whether a rectangles dimension only take up one chunk (the chunk it is currently in).
     * @param rectangle The rectangle to check for.
     * @return Whether it is contained in only one chunk.
     */
    protected boolean containedInOneChunk(Rectangle rectangle){
        int minX = KleeHelper.chunkFloor(rectangle.x * invChunkSize);
        int minY = KleeHelper.chunkFloor(rectangle.y * invChunkSize);
        int maxX = KleeHelper.chunkFloor((rectangle.x + rectangle.width) * invChunkSize);
        int maxY = KleeHelper.chunkFloor((rectangle.y + rectangle.height) * invChunkSize);
        return minX == maxX && minY == maxY;
    }


}
