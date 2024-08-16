package com.github.kleesup.kleeswept.world.chunk;

import com.badlogic.gdx.utils.LongMap;
import com.github.kleesup.kleeswept.KleeHelper;
import com.github.kleesup.kleeswept.world.body.ISweptBody;

import java.util.*;
import java.util.function.Function;

/**
 * An implementation of {@link IChunkManager} that manages chunks in a {@link HashMap}.
 * The key of the map is represented by a long which contains both the chunkX and chunkY paired into it.
 * For reference see: {@link KleeHelper#pairLong(int, int)}.
 * This method reduces object heap as it is not necessary to create a wrapper object for the chunk coordinates (e.g. {@link com.badlogic.gdx.math.Vector2}).
 * <br>Created on 13.09.2023</br>
 * @author KleeSup
 * @version 1.1
 * @since 1.0.1
 */
public class EfficientChunkManager<Body extends ISweptBody> implements IChunkManager<Body> {

    private final Function<Long, Set<Body>> builderFunc = pair -> new HashSet<>();

    private final LongMap<Set<Body>> chunks = new LongMap<>();

    @Override
    public Set<Body> getBodies(int chunkX, int chunkY) {
        long pair = KleeHelper.pairLong(chunkX, chunkY);
        return chunks.get(pair, Collections.EMPTY_SET);
    }

    @Override
    public void addBody(int chunkX, int chunkY, Body aabb) {
        KleeHelper.paramRequireNonNull(aabb, "Body cannot be null!");
        long pair = KleeHelper.pairLong(chunkX,chunkY);
        Set<Body> bodies = computeIfAbsent(pair);
        bodies.add(aabb);
    }

    private Set<Body> computeIfAbsent(long key){
        Set<Body> bodies = chunks.get(key);
        if(bodies == null){
            chunks.put(key, bodies = new HashSet<>());
        }
        return bodies;
    }

    @Override
    public void removeBody(int chunkX, int chunkY, Body aabb) {
        getBodies(chunkX,chunkY).remove(aabb);
    }
}
