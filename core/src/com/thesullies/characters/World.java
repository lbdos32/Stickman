package com.thesullies.characters;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.Random;



/**
 * Created by kosullivan on 04/01/2017.
 */

public class World {


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


    public World(WorldListener listener) {
        this.stickman = new Stickman(WorldRenderer.GAME_WIDTH/2, WorldRenderer.GAME_HEIGHT);
        this.listener = listener;
        this.rand = new Random();
        this.state = WORLD_STATE_RUNNING;
    }

    public void update(float deltaTime, Vector2 inputDirection) {
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
