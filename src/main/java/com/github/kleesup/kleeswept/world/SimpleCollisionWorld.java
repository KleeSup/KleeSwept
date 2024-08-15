package com.github.kleesup.kleeswept.world;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Pool;
import com.github.kleesup.kleeswept.KleeHelper;
import com.github.kleesup.kleeswept.KleeSweptDetection;
import com.github.kleesup.kleeswept.util.BytePair;
import com.github.kleesup.kleeswept.util.CollisionSorter;
import com.github.kleesup.kleeswept.util.Single;
import com.github.kleesup.kleeswept.world.body.ISweptBody;
import com.github.kleesup.kleeswept.world.chunk.AbstractChunkCollisionWorld;
import com.github.kleesup.kleeswept.world.chunk.EfficientChunkManager;

import java.util.*;

/**
 * A simple implementation of {@link AbstractChunkCollisionWorld} which handles simple collision detection on a chunked basis.
 * Note: All AABBs that will be tested against will be interpreted as 'static'.
 * If this is not wanted a custom implementation is required. The class is NOT Thread-Safe!
 * <br>Created on 13.09.2023</br>
 * @author KleeSup
 * @version 1.4
 * @since 1.0.1
 */
public class SimpleCollisionWorld<Body extends ISweptBody> extends AbstractChunkCollisionWorld<Body> {

    private final Map<Body, Rectangle> boundingBoxes = new IdentityHashMap<>();
    private final Pool<CollisionResponse.Collision> poolCollisions;
    private CollisionSorter<Body> sorter;
    private boolean sort = true;

    public SimpleCollisionWorld(int chunkSize) {
        super(chunkSize, new EfficientChunkManager<>());
        //sorting collisions for smallest collision time, if it is the same -> sort for highest velocity axis
        setDefaultSorter();
        //build pool
        this.poolCollisions = new Pool<CollisionResponse.Collision>() {
            @Override
            protected CollisionResponse.Collision newObject() {
                return new CollisionResponse.Collision();
            }
        };
        this.poolCollisions.fill(5);
    }

    @Override
    public void addBody(Body body, Rectangle boundingBox) {
        KleeHelper.paramRequireNonNull(body, "Body cannot be null!");
        KleeHelper.paramRequireNonNull(boundingBox, "Bounding box cannot be null!");
        if(boundingBoxes.containsKey(body))return;
        Rectangle bb = new Rectangle(boundingBox); //copy to own box to avoid errors.
        boundingBoxes.put(body, bb);
        addToContainedChunks(body, bb);
    }

    @Override
    public void addBody(Body body, float bbX, float bbY, float bbWidth, float bbHeight) {
        KleeHelper.paramRequireNonNull(body, "Body cannot be null!");
        if(boundingBoxes.containsKey(body))return;
        Rectangle bb = new Rectangle(bbX,bbY,bbWidth,bbHeight);
        boundingBoxes.put(body, bb);
        addToContainedChunks(body, bb);
    }

    @Override
    public Rectangle removeBody(Body body) {
        KleeHelper.paramRequireNonNull(body, "Body cannot be null!");
        if(!contains(body))return null;
        Rectangle boundingBox = getOriginalBoundingBox(body);
        removeFromContainedChunks(body, boundingBox);
        return boundingBox;
    }

    @Override
    public boolean contains(Body body) {
        return boundingBoxes.containsKey(body);
    }

    /**
     * Retrieves the bounding box of an AABB body.
     * Note: This is a copy of the internal original so any changes applied to it won't be applied to the world.
     * If manipulation is wanted, use {@link #update(ISweptBody, Vector2, float, float, CollisionResponse)} or {@link #forceUpdate(ISweptBody, float, float, float, float)}.
     * @param body The AABB to retrieve the body for.
     * @return The rectangle bounding box (copy) of the AABB.
     */
    @Override
    public Rectangle getBoundingBox(Body body) {
        return getBoundingBox(body, new Rectangle());
    }
    @Override
    public Rectangle getBoundingBox(Body body, Rectangle copyTo) {
        validateAABB(body);
        return copyTo.set(getOriginalBoundingBox(body));
    }

    /**
     * Doesn't make a copy of the actual bounding box.
     * @param body The AABB to get the original bounding box for.
     * @return The original bounding box.
     */
    private Rectangle getOriginalBoundingBox(Body body){
        return boundingBoxes.get(body);
    }

    private void validateAABB(Body body){
        KleeHelper.paramRequireNonNull(body, "Body cannot  be null!");
        if(!contains(body))throw new IllegalArgumentException("The specified Body is not contained in this world!");
    }

    @Override
    public void forceUpdate(Body body, float goalX, float goalY, float width, float height){
        validateAABB(body);
        Rectangle boundingBox = getOriginalBoundingBox(body);
        //return if the AABB didn't move or change size
        if(goalX == boundingBox.x && goalY == boundingBox.y && width == boundingBox.width && height == boundingBox.height)return;
        //remove from all chunks
        removeFromContainedChunks(body, boundingBox);
        //change size & location
        boundingBox.set(goalX, goalY, width, height);
        //add new to all chunks
        addToContainedChunks(body, boundingBox);
    }
    @Override
    public void forceUpdate(Body body, float goalX, float goalY){
        validateAABB(body);
        Rectangle boundingBox = getOriginalBoundingBox(body);
        forceUpdate(body,goalX,goalY,boundingBox.width,boundingBox.height);
    }

    //temporary fields which can be reused for less object heap.
    private final Rectangle _moveArea = new Rectangle();
    private final Rectangle _goalRect = new Rectangle();
    private final Vector2 _displacement = new Vector2();
    private final BytePair _normal = new BytePair();
    private final Rectangle _sum = new Rectangle();
    private final Single<Float> _hitTime = new Single<>(0f);
    private final Vector2 _rayHit = new Vector2();
    private final Set<Body> _alreadyLooped = new HashSet<>();
    private final ArrayList<CollisionResponse.Collision> copyList = new ArrayList<>();

    @Override
    public CollisionResponse update(Body body, Vector2 displacement, float width, float height, CollisionResponse writeTo) {
        //simulate collision to find the best possible spot
        CollisionResponse response = simulate(body,displacement,width,height,writeTo);
        //update the AABB in the world
        forceUpdate(body, response.bestGoalX, response.bestGoalY, width, height);
        return response;
    }

    @Override
    public CollisionResponse update(Body body, Vector2 displacement, CollisionResponse response) {
        validateAABB(body);
        Rectangle rectangle = getOriginalBoundingBox(body);
        return update(body, displacement, rectangle.width, rectangle.height, response);
    }

    @Override
    public CollisionResponse simulate(Body body, Vector2 displacement, float width, float height, CollisionResponse writeTo) {
        validateAABB(body);
        if(writeTo == null)writeTo = new CollisionResponse();
        writeTo.clear();
        writeTo.body = body;
        Rectangle rectangle = getOriginalBoundingBox(body);
        //set the displacement
        if(displacement == null){
            _displacement.set(0,0);
        }else{
            _displacement.set(displacement);
        }
        Rectangle goalRect = _goalRect.set(rectangle.x + _displacement.x, rectangle.y + _displacement.y,  width, height);

        //define the area the rectangle will move in
        Rectangle holeMovementArea = _moveArea.set(rectangle).merge(goalRect);
        _alreadyLooped.clear();
        //making the response object final, so it can be used in the lambda statement
        final CollisionResponse finalizedResponse = writeTo;
        //loop chunks in the area from start to goal position
        forContainingChunk(holeMovementArea, (chunkX, chunkY) -> {
            Set<Body> bodies = chunkManager.getBodies(chunkX,chunkY);
            //for all AABBs in the chunk
            for(Body target : bodies){
                if(target.equals(body))continue;
                if(!_alreadyLooped.add(target))continue; //skip if the AABB was already been tested
                if(!body.checkCollision(target))continue; //skip if calculation isn't wanted
                Rectangle other = getOriginalBoundingBox(target);
                //now collision gets checked
                boolean hit = KleeSweptDetection.checkDynamicVsStatic(rectangle, other, _displacement, _normal.setZero(), _sum, _rayHit.setZero(), _hitTime);
                if(hit)finalizedResponse.getCollisions().add(poolCollisions.obtain().set(target, goalRect.overlaps(other), _normal.x, _normal.y,_hitTime.get(), false));
            }
        });

        //sorting collisions
        if(sort){
            if(sorter.needFullInfo())sorter.set(this,body,_displacement,width,height);
            finalizedResponse.getCollisions().sort(sorter);
        }

        copyList.addAll(finalizedResponse.getCollisions()); //copy to separate to avoid ConcurrentModificationException
        //resolving collisions
        for(CollisionResponse.Collision collision : copyList){
            Rectangle other = getOriginalBoundingBox((Body) collision.target);
            boolean isHit = KleeSweptDetection.checkDynamicVsStatic(rectangle, other, _displacement, _normal.setZero(), _sum, _rayHit.setZero(), _hitTime);
            if(!isHit){
                finalizedResponse.getCollisions().remove(collision);
                continue;
            }
            collision.normalX = _normal.x;
            collision.normalY = _normal.y;
            collision.hitTime = _hitTime.get();
            if(body.resolveCollision(collision.target, collision)){
                _displacement.x += collision.normalX * Math.abs(_displacement.x) * (1-collision.hitTime);
                _displacement.y += collision.normalY * Math.abs(_displacement.y) * (1-collision.hitTime);
                collision.resolved = true;
            }
        }
        copyList.clear();

        //finally, write the best goal position into the response
        finalizedResponse.bestGoalX = rectangle.x + _displacement.x;
        finalizedResponse.bestGoalY = rectangle.y + _displacement.y;
        finalizedResponse.updatedDisplacementX = _displacement.x;
        finalizedResponse.updatedDisplacementY = _displacement.y;
        return finalizedResponse;
    }

    @Override
    public CollisionResponse simulate(Body body, Vector2 displacement, CollisionResponse writeTo) {
        validateAABB(body);
        Rectangle rectangle = getOriginalBoundingBox(body);
        return simulate(body,displacement,rectangle.width,rectangle.height,writeTo);
    }

    /**
     * Sets the current comparator builder used for collision resolution.
     * @param sorter The sorter to set.
     */
    public void setSorter(CollisionSorter<Body> sorter) {
        this.sorter = sorter;
    }

    /**
     * Enables or disables sorting of collisions. If some sort of tile world is used, this should be {@code true} to
     * avoid problems. If not this can be set to {@code false} to spare iteration time.
     * @param enabled Whether sorting should be enabled.
     */
    public void setSort(boolean enabled) {
        this.sort = enabled;
    }

    /**
     * Sets the sorter back to the default sorter.
     */
    public void setDefaultSorter() {
        this.sorter = CollisionSorter.buildSmallestTimeOrVelocity();
    }

    /**
     * Frees the given response object by freeing all collision objects and then clearing the response object.
     * This method should be called after any collision checks have been done.
     * @param response The response object to free.
     */
    public void free(CollisionResponse response){
        for(CollisionResponse.Collision collision : response.getCollisions()){
            free(collision);
        }
        response.clear();
    }

    /**
     * Frees a collision into the pool.
     * @param collision The collision to free.
     */
    public void free(CollisionResponse.Collision collision){
        poolCollisions.free(collision);
    }



}
