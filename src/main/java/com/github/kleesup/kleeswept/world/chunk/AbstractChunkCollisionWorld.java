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
 * @version 1.0
 * @since 1.0.1
 */
public abstract class AbstractChunkCollisionWorld<Body extends ISweptBody> implements CollisionWorld<Body> {

    protected final IChunkManager<Body> chunkManager;

    protected final int chunkSize;
    protected AbstractChunkCollisionWorld(int chunkSize, IChunkManager<Body> chunkManager){
        this.chunkSize = chunkSize;
        this.chunkManager = chunkManager;
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
        KleeHelper.paramRequireNonNull(rectangle, "Rectangle cannot be null!");
        int minX = (int) rectangle.x;
        int minY = (int) rectangle.y;
        int maxX = (int) (rectangle.x + rectangle.width);
        int maxY = (int) (rectangle.y + rectangle.height);

        int chunksX = maxX / chunkSize;
        int chunksY = maxY / chunkSize;
        int startChunkX = minX / chunkSize;
        int startChunkY = minY / chunkSize;
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


}
