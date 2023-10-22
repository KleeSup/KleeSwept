package com.github.kleesup.kleeswept;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.github.kleesup.kleeswept.world.CollisionResponse;
import com.github.kleesup.kleeswept.world.SimpleCollisionWorld;
import com.github.kleesup.kleeswept.world.body.SweptAABB;

/**
 * <br>Created on 22.10.2023</br>
 *
 * @author KleeSup
 * @version 1.0
 * @since 1.0.1
 */
public class Setup {

    public static void howToSetup(){
        int chunkSize = 32;

        //create a new world instance with a fixed chunk size
        SimpleCollisionWorld<SweptAABB> world = new SimpleCollisionWorld<>(chunkSize);

        //initialize world objects
        SweptAABB player = new SweptAABB();
        SweptAABB obstacle = new SweptAABB();

        //add them to the world with their bounding box
        world.addBody(player, new Rectangle(0,0,10,10));
        world.addBody(obstacle, new Rectangle(5,0,10,10));

        //retrieving a world objects bounding box
        Rectangle playerBoundingBox = world.getBoundingBox(player);
        Rectangle obstacleBoundingBox = world.getBoundingBox(obstacle);

        CollisionResponse response;
        Vector2 displacement = new Vector2(1, 2);
        //updating moves the body in the world and retrieves a collision response
        response = world.update(player, displacement);
        //simulating doesn't actually move the body in the world but still checks for collisions
        response = world.simulate(player, displacement);
        //forceUpdate forces a teleport/resize in the world
        world.forceUpdate(player, playerBoundingBox.x + displacement.x, playerBoundingBox.y + displacement.y);

    }

}
