package com.thesullies.characters;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Random;

import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.thesullies.maps.Constants;

/**
 * Created by kosullivan on 04/01/2017.
 */

public class StickmanWorld {


    public interface WorldListener {
        public void jump();

        public void highJump();

        public void hit();

        public void coin();
    }

    public static final int WORLD_STATE_RUNNING = 0;
    public static final int WORLD_STATE_NEXT_LEVEL = 1;
    public static final int WORLD_STATE_GAME_OVER = 2;
    public static final Vector2 gravity = new Vector2(0, -12);

    public final Stickman stickman;
    private final Random rand;
    private final int state;
    private final WorldListener listener;
    // Physics Box2d World
    protected World physicsWorld;
    private final Box2DDebugRenderer debugRenderer;


    public StickmanWorld(WorldListener listener) {
        this.physicsWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0, Constants.PHYSICS_GRAVITY), true);
        this.debugRenderer = new Box2DDebugRenderer();

        this.stickman = new Stickman(WorldRenderer.GAME_WIDTH/2, WorldRenderer.GAME_HEIGHT, this.physicsWorld);
        this.listener = listener;
        this.rand = new Random();
        this.state = WORLD_STATE_RUNNING;
    }

    public void update(float deltaTime, Vector2 inputDirection) {
        this.physicsWorld.step(1f,6,2); //1f/60f, 6, 2);

        updateStickman(deltaTime, inputDirection);
        //updatePlatforms(deltaTime);
        //updateSquirrels(deltaTime);
        //updateCoins(deltaTime);
        //if (bob.state != Bob.BOB_STATE_HIT) checkCollisions();
        //checkGameOver();
    }

    private void updateStickman(float deltaTime,  Vector2 inputDirection) {
        //if (this.stickman.state != Stickman.BOB_STATE_HIT && this.stickman.position.y <= 0.5f)
        //    bob.hitPlatform();
        //if (this.stickman.state != Stickman.BOB_STATE_HIT)
        //    this.stickman.velocity.x = -accelX / 10 * Bob.BOB_MOVE_VELOCITY;
        stickman.update(deltaTime, inputDirection);
        //heightSoFar = Math.max(bob.position.y, heightSoFar);
    }

}
