import java.awt.*;

import javax.swing.*;

import java.util.List;
import java.util.ArrayList;

/**
 * @title Quadtree Collision Detection
 * @subtitle Assignment: PS-2
 * @Author Nathan Giffard
 * @class Dartmouth CS 10, Winter 2023
 * @date January 30th, 2023
 * @description Using a quadtree for collision detection
 */
public class CollisionGUI extends DrawingGUI {
    private static final int width=800, height=600;		// size of the universe

    private List<Blob> blobs;						// all the blobs
    private List<Blob> colliders;	// the blobs who collided at this step
    private char blobType = 'b';						// what type of blob to create
    private char collisionHandler = 'c';				// when there's a collision, 'c'olor them, or 'd'estroy them
    private int delay = 100;							// timer control

    private int blobRadius = 10;                    //radius for blobs

    public CollisionGUI() {
        super("super-collider", width, height);

        blobs = new ArrayList<Blob>();
        colliders = new ArrayList<>();

        // Timer drives the animation.
        startTimer();
    }

    /**
     * Adds a blob of the current blobType at the location
     *
     */
    private void add(int x, int y) {
        if (blobType=='b') {
            blobs.add(new Bouncer(x,y,width,height));
        }
        else if (blobType=='w') {
            blobs.add(new Wanderer(x,y));
        }
        else {
            System.err.println("Unknown blob type "+blobType);
        }
    }

    /**
     * DrawingGUI method, here creating a new blob
     */
    public void handleMousePress(int x, int y) {
        add(x,y);
        repaint();
    }

    /**
     * DrawingGUI method
     */
    public void handleKeyPress(char k) {
        if (k == 'f') { // faster
            if (delay>1) delay /= 2;
            setTimerDelay(delay);
            System.out.println("delay:"+delay);
        }
        else if (k == 's') { // slower
            delay *= 2;
            setTimerDelay(delay);
            System.out.println("delay:"+delay);
        }
        else if (k == 'r') { // add some new blobs at random positions
            blobRadius = 10;
            for (int i=0; i<10; i++) {
                add((int)(width*Math.random()), (int)(height*Math.random()));
                repaint();
            }
        }
        else if (k == 'c' || k == 'd') { // control how collisions are handled
            collisionHandler = k;
            System.out.println("collision:"+k);
        }
        else if (k == '0'){
            testCase0();
        }
        else if (k == '1'){
            testCase1();
        }
        else { // set the type for new blobs
            blobType = k;
        }
    }

    /**
     * DrawingGUI method, here drawing all the blobs and then re-drawing the colliders in red
     */
    public void draw(Graphics g) {
        // Ask all the blobs to draw themselves.
        for(Blob b : blobs){
            g.fillOval((int)b.getX(),(int)b.getY(),blobRadius,blobRadius);
        }
        // Ask the colliders to draw themselves in red.
        if(collisionHandler == 'c' && !colliders.isEmpty()) {
            for (Blob b : colliders) {
                g.setColor(Color.RED);
                g.fillOval((int) b.getX(), (int) b.getY(), blobRadius, blobRadius);
            }
        }
    }

    /**
     * Sets colliders to include all blobs in contact with another blob
     */
    private void findColliders() {
        //collision Handler must be c to recolor
        if (collisionHandler == 'c') {
            // Create the tree
            PointQuadtree<Blob> collision = new PointQuadtree<Blob>(blobs.get(0), 0, 0, width, height);
            for (int x = 1; x < blobs.size(); x++) {
                collision.insert(blobs.get(x));
            }
            // Compare blobs pairwise to find collisions
            for (Blob a : blobs) {
                for (Blob b : blobs) {
                    // If there is overlap in position, add them to the temp list of blobs
                    if(Geometry.pointInCircle(a.getX(),a.getY(),b.getX(),b.getY(),blobRadius) && !a.equals(b)) {
                        List<Blob> temp = collision.findInCircle(b.getX(), b.getY(), blobRadius);
                        //add colliding blobs to colliders list
                        for (Blob blob : temp) {
                            colliders.add(blob);
                        }
                    }
                }
            }
        }
    }

    /**
     * DrawingGUI method, here moving all the blobs and checking for collisions
     */
    public void handleTimer() {
        // Ask all the blobs to move themselves.
        for (Blob blob : blobs) {
            blob.step();
        }
        // Check for collisions
        if (blobs.size() > 0) {
            findColliders();
            //if collisionHandler == 'd',
            // delete colliding
            if (collisionHandler == 'd') {
                blobs.removeAll(colliders);
                colliders = new ArrayList<Blob>();
            }
        }
        // Now update the drawing
        repaint();
    }
    /**
     * Test case #0:
     * creates 5 very large random dots that move quickly and will bump into each other
     */
    private void testCase0(){
        blobs = new ArrayList<Blob>();
        colliders = new ArrayList<>();
        collisionHandler = 'c';
        blobRadius = 75;
        delay = 10;
        setTimerDelay(delay);

        for (int i=0; i<10; i++) {
            add((int)(width*Math.random()), (int)(height*Math.random()));
            repaint();
        }
    }

    /**
     * Test case #1:
     * creates 2 very small random dots that move slowly and will not collide
     */
    private void testCase1(){
        blobs = new ArrayList<Blob>();
        colliders = new ArrayList<>();
        collisionHandler = 'c';
        blobRadius = 5;
        delay = 50;
        setTimerDelay(delay);

        for (int i=0; i<3; i++) {
            add((int)(width*Math.random()), (int)(height*Math.random()));
            repaint();
        }
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new CollisionGUI();
            }
        });
    }
}