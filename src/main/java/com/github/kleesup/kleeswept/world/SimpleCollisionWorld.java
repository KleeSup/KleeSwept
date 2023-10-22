package com.github.kleesup.kleeswept.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.github.kleesup.kleeswept.KleeHelper;
import com.github.kleesup.kleeswept.KleeSweptDetection;
import com.github.kleesup.kleeswept.util.Single;
import com.github.kleesup.kleeswept.world.body.ISweptAABB;
import com.github.kleesup.kleeswept.world.chunk.AbstractChunkCollisionWorld;
import com.github.kleesup.kleeswept.world.chunk.EfficientChunkManager;

import java.util.*;

/**
 * A simple implementation of {@link AbstractChunkCollisionWorld} which handles simple collision detection on a chunked basis.
 * Note: All AABBs that will be tested against will be interpreted as 'static'.
 * If this is not wanted a custom implementation is required. The class is NOT Thread-Safe!
 * <br>Created on 13.09.2023</br>
 * @author KleeSup
 * @version 1.0
 * @since 1.0.1
 */
public class SimpleCollisionWorld<AABB extends ISweptAABB> extends AbstractChunkCollisionWorld<AABB> {

    private final Map<AABB, Rectangle> boundingBoxes = new HashMap<>();

    public SimpleCollisionWorld(int chunkSize) {
        super(chunkSize, new EfficientChunkManager<>());
    }

    @Override
    public void addBody(AABB aabb, Rectangle boundingBox) {
        KleeHelper.paramRequireNonNull(aabb, "AABB cannot be null!");
        KleeHelper.paramRequireNonNull(boundingBox, "Bounding box cannot be null!");
        if(boundingBoxes.containsKey(aabb))return;
        boundingBoxes.put(aabb, boundingBox);
        addToContainedChunks(aabb, boundingBox);
    }

    @Override
    public Rectangle removeBody(AABB aabb) {
        KleeHelper.paramRequireNonNull(aabb, "AABB cannot be null!");
        if(!contains(aabb))return null;
        Rectangle boundingBox = getOriginalBoundingBox(aabb);
        removeFromContainedChunks(aabb, boundingBox);
        return boundingBox;
    }

    @Override
    public boolean contains(AABB aabb) {
        return boundingBoxes.containsKey(aabb);
    }

    /**
     * Retrieves the bounding box of an AABB body.
     * Note: This is a copy of the internal original so any changes applied to it won't be applied to the world.
     * If manipulation is wanted, use {@link #update(ISweptAABB, Vector2, float, float, CollisionResponse)} or {@link #forceUpdate(ISweptAABB, float, float, float, float)}.
     * @param aabb The AABB to retrieve the body for.
     * @return The rectangle bounding box (copy) of the AABB.
     */
    @Override
    public Rectangle getBoundingBox(AABB aabb) {
        return getBoundingBox(aabb, new Rectangle());
    }
    public Rectangle getBoundingBox(AABB aabb, Rectangle copyTo) {
        validateAABB(aabb);
        if(copyTo == null)copyTo = new Rectangle();
        return copyTo.set(getOriginalBoundingBox(aabb));
    }

    /**
     * Doesn't make a copy of the actual bounding box.
     * @param aabb The AABB to get the original bounding box for.
     * @return The original bounding box.
     */
    private Rectangle getOriginalBoundingBox(AABB aabb){
        return boundingBoxes.get(aabb);
    }

    private void validateAABB(AABB aabb){
        KleeHelper.paramRequireNonNull(aabb, "AABB cannot  be null!");
        if(!contains(aabb))throw new IllegalArgumentException("The specified AABB is not contained in this world!");
    }

    @Override
    public void forceUpdate(AABB aabb, float goalX, float goalY, float width, float height){
        validateAABB(aabb);
        Rectangle boundingBox = getOriginalBoundingBox(aabb);
        //return if the AABB didn't move or change size
        if(goalX == boundingBox.x && goalY == boundingBox.y && width == boundingBox.width && height == boundingBox.height)return;
        //remove from all chunks
        removeFromContainedChunks(aabb, boundingBox);
        //change size & location
        boundingBox.set(goalX, goalY, width, height);
        //add new to all chunks
        addToContainedChunks(aabb, boundingBox);
    }
    @Override
    public void forceUpdate(AABB aabb, float goalX, float goalY){
        validateAABB(aabb);
        Rectangle boundingBox = getOriginalBoundingBox(aabb);
        forceUpdate(aabb,goalX,goalY,boundingBox.width,boundingBox.height);
    }

    //temporary fields which can be reused for less object heap.
    private final Rectangle _moveArea = new Rectangle();
    private final Rectangle _goalRect = new Rectangle();
    private final Vector2 _displacement = new Vector2();
    private final Vector2 _normal = new Vector2();
    private final Rectangle _sum = new Rectangle();
    private final Single<Float> _hitTime = new Single<>(0f);
    private final Vector2 _rayHit = new Vector2();
    private final Set<AABB> _alreadyLooped = new HashSet<>();

    @Override
    public CollisionResponse update(AABB aabb, Vector2 displacement, float width, float height, CollisionResponse writeTo) {
        //simulate collision to find the best possible spot
        CollisionResponse response = simulate(aabb,displacement,width,height,writeTo);
        //update the AABB in the world
        forceUpdate(aabb, response.bestGoalX, response.bestGoalY, width, height);
        return response;
    }

    @Override
    public CollisionResponse update(AABB aabb, Vector2 displacement, CollisionResponse response) {
        validateAABB(aabb);
        Rectangle rectangle = getOriginalBoundingBox(aabb);
        return update(aabb, displacement, rectangle.width, rectangle.height, response);
    }

    @Override
    public CollisionResponse simulate(AABB aabb, Vector2 displacement, float width, float height, CollisionResponse writeTo) {
        validateAABB(aabb);
        if(writeTo == null)writeTo = new CollisionResponse();
        writeTo.clear();
        writeTo.aabb = aabb;
        Rectangle rectangle = getOriginalBoundingBox(aabb);
        if(displacement == null){
            _displacement.set(0,0);
        }else{
            _displacement.set(displacement);
        }
        Rectangle goalRect = _goalRect.set(rectangle.x + _displacement.x, rectangle.y + _displacement.y,  width, height);

        //set the displacement
        _displacement.set(displacement);

        //define the area the rectangle will move in
        Rectangle holeMovementArea = _moveArea.set(rectangle).merge(goalRect);
        _alreadyLooped.clear();
        //making the response object final, so it can be used in the lambda statement
        final CollisionResponse finalizedResponse = writeTo;
        //loop chunks in the area from start to goal position
        forContainingChunk(holeMovementArea, (chunkX, chunkY) -> {
            Set<AABB> aabbs = chunkManager.getBodies(chunkX,chunkY);
            //for all AABBs in the chunk
            for(AABB target : aabbs){
                if(target.equals(aabb))continue;
                if(!_alreadyLooped.add(target))continue; //skip if the AABB was already been tested
                Rectangle other = getOriginalBoundingBox(target);
                //now collision gets checked
                boolean hit = KleeSweptDetection.checkDynamicVsStatic(rectangle, other, _displacement, _normal.setZero(), _sum, _rayHit.setZero(), _hitTime);
                if(hit)finalizedResponse.getCollisions().add(new CollisionResponse.Collision(target, true, _normal.x, _normal.y,_hitTime.get()));
            }
        });

        //sorting collisions for smallest collision time, if it is the same -> sort for highest velocity axis
        finalizedResponse.getCollisions().sort(new Comparator<CollisionResponse.Collision>() {
            @Override
            public int compare(CollisionResponse.Collision c1, CollisionResponse.Collision c2) {
                if(c1.hitTime == c2.hitTime){
                    if(_displacement.x > _displacement.y){
                        if(c1.normalX != 0 && c2.normalX == 0){
                            return -1;
                        }else if(c1.normalX == 0 && c2.normalX != 0){
                            return 1;
                        }else{
                            return 0;
                        }
                    }else if(_displacement.x < _displacement.y){
                        if(c1.normalY != 0 && c2.normalY == 0){
                            return -1;
                        }else if(c1.normalY == 0 && c2.normalY != 0){
                            return 1;
                        }else{
                            return 0;
                        }
                    }else{
                        return 0;
                    }
                }else{
                    return Float.compare(c1.hitTime, c2.hitTime);
                }
            }
        });

        //resolving collisions
        for(CollisionResponse.Collision collision : finalizedResponse.getCollisions()){
            if(!collision.isHit)continue;
            _displacement.x += collision.normalX * Math.abs(_displacement.x) * (1-collision.hitTime);
            _displacement.y += collision.normalY * Math.abs(_displacement.y) * (1-collision.hitTime);
        }

        //finally, write the best goal position into the response
        finalizedResponse.bestGoalX = rectangle.x + _displacement.x;
        finalizedResponse.bestGoalY = rectangle.y + _displacement.y;
        finalizedResponse.updatedDisplacementX = _displacement.x;
        finalizedResponse.updatedDisplacementY = _displacement.y;
        return finalizedResponse;
    }

    @Override
    public CollisionResponse simulate(AABB aabb, Vector2 displacement, CollisionResponse writeTo) {
        validateAABB(aabb);
        Rectangle rectangle = getOriginalBoundingBox(aabb);
        return simulate(aabb,displacement,rectangle.width,rectangle.height,writeTo);
    }


}
