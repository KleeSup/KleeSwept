package com.github.kleesup.kleeswept;


import com.github.kleesup.kleeswept.impl.SimpleAABB;
import com.github.kleesup.kleeswept.response.SweptResult;

/**
 *
 * <br>Created on 17.04.2023</br>
 * @author KleeSup
 * @version 1.0
 * @since 1.0.0
 */
public class KleeSweptTest {

    public static void main(String[] args) {


    }

    private void doc(){
        //create two AABBs, you can either use SimpleAABB for standalone or RectangleAABB which uses Rectangle from LibGDX
        AABB player = new SimpleAABB(0,0,2,2);
        AABB collider = new SimpleAABB(4,0,3,3);

        //now we define the goal where the player wants to move at.
        //NOTE: always calculate movements based of the CENTER of the AABB as this is where the library calculates collisions.
        //in this example, we move by +10 units on the x-axis.
        float goalX = player.getCenterX() + 10;
        float goalY = player.getCenterY();

        //now we can get our result. It contains various information of our collision.
        SweptResult result = KleeSweptDetection.checkAABBvsAABB(player, collider, goalX, goalY);
        System.out.println(result.isHit);
    }

}
