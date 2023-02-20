import java.util.ArrayList;
import java.util.List;

/**
 * @title A point quadtree
 * @subtitle Assignment: PS-2
 * @Author Nathan Giffard
 * @class Dartmouth CS 10, Winter 2023
 * @date January 30th, 2023
 * @description Stores an element at a 2D position, with children at the subdivided quadrants.
 */
public class PointQuadtree<E extends Point2D> {
    private E point;                            // the point anchoring this node
    private int x1, y1;                            // upper-left corner of the region
    private int x2, y2;                            // bottom-right corner of the region
    private int prevX1, prevY1;                    // previous upper-left corner of last searched region
    private int prevX2, prevY2;                    // previous bottom-right corner of last searched region
    private PointQuadtree<E> c1, c2, c3, c4;    // children
    private ArrayList<E> allNodes = new ArrayList<E>(); // keep track of points added

    /**
     * Initializes a leaf quadtree, holding the point in the rectangle
     */
    public PointQuadtree(E point, int x1, int y1, int x2, int y2) {
        this.point = point;
        this.x1 = x1;
        this.y1 = y1;
        this.x2 = x2;
        this.y2 = y2;
    }

    // Getters

    public E getPoint() {
        return point;
    }

    public int getX1() {
        return x1;
    }

    public int getY1() {
        return y1;
    }

    public int getX2() {
        return x2;
    }

    public int getY2() {
        return y2;
    }

    /**
     * Returns the child (if any) at the given quadrant, 1-4
     *
     * @param quadrant 1 through 4
     */
    public PointQuadtree<E> getChild(int quadrant) {
        if (quadrant == 1) return c1;
        if (quadrant == 2) return c2;
        if (quadrant == 3) return c3;
        if (quadrant == 4) return c4;
        return null;
    }

    /**
     * Returns whether there is a child at the given quadrant, 1-4
     *
     * @param quadrant 1 through 4
     */
    public boolean hasChild(int quadrant) {
        return (quadrant == 1 && c1 != null) ||
                (quadrant == 2 && c2 != null) ||
                (quadrant == 3 && c3 != null) ||
                (quadrant == 4 && c4 != null);
    }

    /**
     * Inserts the point into the tree
     *
     * @param p2 point to add
     */
    public void insert(E p2) {
        int x = (int) p2.getX(); // x coordinate of p2
        int y = (int) p2.getY(); // y coordinate of p2
        int xRoot = (int) point.getX(); // x coordinate of root
        int yRoot = (int) point.getY(); // y coordinate of root

        //  If (x,y) is to the right of the root point
        if (x >= xRoot) {
            // If (x,y) is in quadrant 1
            if (y <= yRoot) {
                // If the quadrant already has a child,
                // then insert the point in quadrant 1
                if (hasChild(1)) {
                    c1.insert(p2);
                }
                // If the quadrant does not have a child,
                // then create a new child with point p2
                // and the bounds of the quadrant
                else {
                    c1 = new PointQuadtree<E>(p2, xRoot, getY1(), getX2(), yRoot); //bounds of quadrant 1
                }
            }
            //If (x,y) is in quadrant 4
            else {
                // If the quadrant already has a child,
                // then insert the point in quadrant 4
                if (hasChild(4)) {
                    c4.insert(p2);
                }
                // If the quadrant does not have a child,
                // then create a new child with point p2
                // and the bounds of the quadrant
                else {
                    c4 = new PointQuadtree<E>(p2, xRoot, yRoot, getX2(), getY2()); //bounds of quadrant 4
                }
            }
        }

        //  If (x,y) is to the left of the root point
        else {
            // If (x,y) is in quadrant 2
            if (y <= yRoot) {
                // If the quadrant already has a child,
                // then insert the point in quadrant 2
                if (hasChild(2)) {
                    c2.insert(p2);
                }
                // If the quadrant does not have a child,
                // then create a new child with point p2
                // and the bounds of the quadrant
                else {
                    c2 = new PointQuadtree<E>(p2, getX1(), getY1(), xRoot, yRoot); //bounds of quadrant 2

                }
            }
            //If (x,y) is in quadrant 3
            else {
                // If the quadrant already has a child,
                // then insert the point in quadrant 3
                if (hasChild(3)) {
                    c3.insert(p2);
                }
                // If the quadrant does not have a child,
                // then create a new child with point p2
                // and the bounds of the quadrant
                else {
                    c3 = new PointQuadtree<E>(p2, getX1(), yRoot, xRoot, getY2()); //bounds of quadrant 3
                }
            }
        }
    }

    /**
     * Finds the number of points in the quadtree (including its descendants)
     */
    public int size() {
        return allNodes.size(); // return the size of the tree using the allNodes helper method
    }

    /**
     * Builds a list of all the points in the quadtree (including its descendants)
     */
    public List<E> allPoints() {
        //add the starting point to the arraylist
        allNodes.add(point);
        //search through children in each quadrant
        for (int x = 1; x <= 4; x++) {
            //if the point has a child,
            // set the point to the child
            // and call allPoints on that new point to add to the list of nodes
            if (hasChild(x)) {
                point = getChild(x).getPoint(); //set point to child
                allPoints(); //recursively call allPoints
            }
        }
        return allNodes; //return resulting list
    }

    /**
     * Uses the quadtree to find all points within the circle
     *
     * @param cx circle center x
     * @param cy circle center y
     * @param cr circle radius
     * @return the points in the circle (and the qt's rectangle)
     */
    public List<E> findInCircle(double cx, double cy, double cr) {
        List<E> found = new ArrayList<>();
        found = circleHelper(found, cx, cy, cr); //call helper method
        //System.out.println("Points found...");
        //System.out.println(found.toString());
        return found;
    }

    /**
     * Helper method to find all points within the circle
     *
     * @param list list of points in the circle
     * @param cx   circle center x
     * @param cy   circle center y
     * @param cr   circle radius
     * @return the points in the circle (and the qt's rectangle)
     */
    private List<E> circleHelper(List<E> list, double cx, double cy, double cr) {
        boolean foundRect = false; //keep track of whether the circle intersects w/ the rectangle

            //if the circle and rectangle overlap and if the point lies in the circle,
            // then add the point to the list
            if (Geometry.circleIntersectsRectangle(cx, cy, cr, x1, y1, x2, y2)) {
                foundRect = true;
                if (Geometry.pointInCircle(point.getX(), point.getY(), cx, cy, cr)) {
                    list.add(point);
                }
            }
            //search children of the point recursively
            if(foundRect == true) {
                for (int x = 1; x <= 4; x++) {
                    if (hasChild(x)) {
                        getChild(x).circleHelper(list, cx, cy, cr);
                    }
                }
            }
        return list;
    }
}