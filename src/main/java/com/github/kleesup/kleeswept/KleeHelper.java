package com.github.kleesup.kleeswept;

import com.badlogic.gdx.math.Polygon;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


/**
 * Simple utility class.
 * <br>Created on 17.04.2023</br>
 * @author KleeSup
 * @version 1.5
 * @since 1.0.0
 */
public final class KleeHelper {

    private KleeHelper(){}

    /**
     * Throws a {@link IllegalArgumentException} when the specified object is {@code null}.
     * @param param The object to check for.
     * @param msg The message that is printed with the {@link IllegalArgumentException} if the object occurs to be {@code null}.
     * @throws IllegalArgumentException thrown when the specified object is {@code null}.
     */
    public static void paramRequireNonNull(Object param, String msg){
        if(param == null)throw new IllegalArgumentException(msg);
    }

    /**
     * Calculates the magnitude between two points.
     * @param x The x-coordinate of the start-position.
     * @param y The y-coordinate of the start-position.
     * @param goalX The x-coordinate of the goal-position.
     * @param goalY The y-coordinate of the goal-position.
     * @param magWriteTo A temporary point object where the magnitude can be written into (Useful when less object creation is desired).
     * @return The calculated magnitude between the two points.
     */
    public static Vector2 calculateMagnitude(float x, float y, float goalX, float goalY, Vector2 magWriteTo){
        Vector2 point = (magWriteTo != null ? magWriteTo : new Vector2()).set(goalX,goalY);
        point.sub(x,y);
        return point;
    }
    public static Vector2 calculateMagnitude(float x, float y, float goalX, float goalY){
        return calculateMagnitude(x,y,goalX,goalY,null);
    }

    /**
     * Calculates a summed AABB which represents an AABB centered at the center of the second AABB and the width and height of both combined.
     * @param aabb The AABB from which the sizes are added to the second.
     * @param other The second AABB which sets the position.
     * @param sumWriteTo A temp AABB object where the outcome can be written into (Useful when less object creation is desired).
     * @return The sum of both AABB sizes with the center located at the second AABBs center.
     */
    public static Rectangle calculateSumAABB(Rectangle aabb, Rectangle other, Rectangle sumWriteTo){
        paramRequireNonNull(aabb, "AABB cannot be null!");
        paramRequireNonNull(other, "Second AABB cannot be null!");
        if(sumWriteTo == null)sumWriteTo = new Rectangle();
        //calculating the sum of both AABBs by adding their width and height together.
        float width = aabb.getWidth() + other.getWidth();
        float height = aabb.getHeight() + other.getHeight();
        sumWriteTo.setSize(width, height);
        sumWriteTo.setCenter(KleeSweptDetection.getCenterX(other), KleeSweptDetection.getCenterY(other));
        return sumWriteTo;
    }
    public static Rectangle calculateSumAABB(Rectangle aabb, Rectangle other){
        return calculateSumAABB(aabb, other, null);
    }

    /**
     * Pairs two integers into a long.
     * @param x The first integer of the pair.
     * @param y The second integer of the pair.
     * @return The pair stored into a long.
     */
    public static long pairLong(int x, int y){
        return ((x & 0xFFFFFFFFL) | (y & 0xFFFFFFFFL) << 32);
    }

    /**
     * Sets a polygon to fit a rectangle. This polygon is given and needs at least 4 vertexes!
     * @param polygon The polygon to modify.
     * @param x1 First corner x-value.
     * @param y1 First corner y-value.
     * @param x2 Second corner x-value.
     * @param y2 Second corner y-value.
     * @param x3 Third corner x-value.
     * @param y3 Third corner y-value.
     * @param x4 Fourth corner x-value.
     * @param y4 Fourth corner y-value.
     */
    public static void setPolygonRect(Polygon polygon, float x1, float y1, float x2, float y2, float x3, float y3,
                                      float x4, float y4){
        polygon.getVertices()[0] = x1;
        polygon.getVertices()[1] = y1;
        polygon.getVertices()[2] = x2;
        polygon.getVertices()[3] = y2;
        polygon.getVertices()[4] = x3;
        polygon.getVertices()[5] = y3;
        polygon.getVertices()[6] = x4;
        polygon.getVertices()[7] = y4;
    }

    /**
     * See {@link #setPolygonRect(Polygon, float, float, float, float, float, float, float, float)}.
     * @param polygon The polygon to modify (needs at least 4 vertexes).
     * @param rectangle The rectangle to write into the polygon.
     */
    public static void setPolygonRect(Polygon polygon, Rectangle rectangle){
       setPolygonRect(polygon,
               rectangle.x, rectangle.y,
               rectangle.x + rectangle.width, rectangle.y,
               rectangle.x, rectangle.y + rectangle.height,
               rectangle.x + rectangle.width, rectangle.y + rectangle.height
       );
    }

    /**
     * Creates a polygon that contains two rectangles and the space between them.
     * This polygon will have and need 6 vertexes.
     * The rectangle A represents the one of the smaller x-value and B the one with the higher x-value.
     * If this condition isn't met, they are swapped. After that, the outside points which are always parts
     * get added (being A bottom-left, top-left and B bottom-right and top-right). Depending on the position of
     * the second rectangle, the connecting points are added.
     * @param a The first rectangle (smaller x).
     * @param b The second rectangle (higher x).
     * @param goal The polygon to write to. Note: the polygon object needs at least 6 vertexes!
     * @return The created polygon.
     */
    public static Polygon createMovementPolygon(Rectangle a, Rectangle b, Polygon goal){
        if(a.x > b.x){ //need A to be the one in the smaller position, if not swap them
            Rectangle swap = a;
            a = b;
            b = swap;
        }
        float[] vert = goal.getVertices();
        if(b.y > a.y){ //top-right
            vert[0] = a.x; vert[1] = a.y;
            vert[2] = a.x + a.width; vert[3] = a.y;
            vert[4] = b.x + b.width; vert[5] = b.y;
            vert[6] = b.x + b.width; vert[7] = b.y + b.height;
            vert[8] = b.x; vert[9] = b.y + b.height;
            vert[10] = a.x; vert[11] = a.y + a.height;
        }else{ //bottom-right
            vert[0] = a.x; vert[1] = a.y;
            vert[2] = b.x; vert[3] = b.y;
            vert[4] = b.x + b.width; vert[5] = b.y;
            vert[6] = b.x + b.width; vert[7] = b.y + b.height;
            vert[8] = a.x + a.width; vert[9] = a.y + a.height;
            vert[10] = a.x; vert[11] = a.y + a.height;
        }
        goal.setVertices(vert);
        return goal;
    }

    /**
     * Floors a value to a int value. This means:
     * <l>
     *     <li>Number {@code 0} will return {@code 0}</li>
     *     <li>Numbers bigger than {@code 0} will return their int value (1.5 -> 1, 1.9 -> 1, 5.3421 -> 5)</li>
     *     <li>Numbers smaller than {@code 0} will return their int value (and minus 1 if they have a decimal point,
     *     therefore -1.5 -> -2 but -3 -> -3)</li>
     * </l>
     * @param input The input value to floor.
     * @return The transformed int value.
     */
    public static int chunkFloor(float input){
        return input >= 0 ? (int) input : (input % 1 == 0 ? (int) input : (int) input - 1);
    }

}
