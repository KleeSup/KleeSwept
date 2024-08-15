package com.github.kleesup.kleeswept.util;

import com.badlogic.gdx.math.Vector2;
import com.github.kleesup.kleeswept.world.CollisionResponse;
import com.github.kleesup.kleeswept.world.CollisionWorld;
import com.github.kleesup.kleeswept.world.body.ISweptBody;

import java.util.Comparator;

/**
 * Class used to sort collisions. Can be filled with extra information if {@link #needFullInfo} is set to {@code true}.
 * @author KleeSup
 * @since 1.1
 * @version 1.0
 */
public abstract class CollisionSorter<Body extends ISweptBody> implements Comparator<CollisionResponse.Collision> {

    public static <Body extends ISweptBody> CollisionSorter<Body> buildSmallestTime() {
        return new CollisionSorter<Body>(false) {
            @Override
            public int compare(CollisionResponse.Collision o1, CollisionResponse.Collision o2) {
                return Float.compare(o1.hitTime, o2.hitTime);
            }
        };
    }


    public static <Body extends ISweptBody> CollisionSorter<Body> buildSmallestTimeOrVelocity() {
        return new CollisionSorter<Body>(true) {
            @Override
            public int compare(CollisionResponse.Collision o1, CollisionResponse.Collision o2) {
                if (o1.hitTime == o2.hitTime) {
                    if (displacement.x > displacement.y) {
                        if (o1.normalX != 0 && o2.normalX == 0) {
                            return -1;
                        } else if (o1.normalX == 0 && o2.normalX != 0) {
                            return 1;
                        } else {
                            return 0;
                        }
                    } else if (displacement.x < displacement.y) {
                        if (o1.normalY != 0 && o2.normalY == 0) {
                            return -1;
                        } else if (o1.normalY == 0 && o2.normalY != 0) {
                            return 1;
                        } else {
                            return 0;
                        }
                    } else {
                        return 0;
                    }
                }
                return Float.compare(o1.hitTime, o2.hitTime);
            }
        };
    }

    protected CollisionWorld<Body> world;
    protected Body body;
    protected Vector2 displacement;
    protected float newWidth, newHeight;
    private final boolean needFullInfo;
    public CollisionSorter(boolean needFullInfo){
        this.needFullInfo = needFullInfo;
    }

    public void set(CollisionWorld<Body> world, Body body, Vector2 displacement, float newWidth, float newHeight){
        this.world = world;
        this.body = body;
        this.displacement = displacement;
        this.newWidth = newWidth;
        this.newHeight = newHeight;
    }

    public boolean needFullInfo() {
        return needFullInfo;
    }
}
