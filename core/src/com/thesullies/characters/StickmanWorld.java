package com.thesullies.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.thesullies.Assets;
import com.thesullies.maps.Box2DContactListener;
import com.thesullies.maps.Constants;
import com.thesullies.maps.MapBodyBuilder;

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
    public static List<Coin> coins;


    public StickmanWorld(WorldListener listener) {
        this.physicsWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0, Constants.PHYSICS_GRAVITY), true);
        this.physicsWorld.setContactListener(new Box2DContactListener());
        this.stickman = new Stickman(WorldRenderer.GAME_WIDTH / 2, WorldRenderer.GAME_HEIGHT, this.physicsWorld);
        this.coins = new ArrayList<Coin>();
        this.coins.add(new Coin(70,20,this.physicsWorld));
        this.listener = listener;
        this.rand = new Random();
        this.state = WORLD_STATE_RUNNING;
    }

    public void update(float deltaTime, Vector2 inputDirection, boolean jumpPressed) {
        this.physicsWorld.step(1f/60f, 6, 2);

        updateStickman(deltaTime, inputDirection, jumpPressed);
        updateCoins(deltaTime);
    }

    private void updateCoins(float deltaTime) {
        for (Coin coin : this.coins) {
            coin.update(deltaTime);
        }
    }

    private void updateStickman(float deltaTime, Vector2 inputDirection, boolean jumpPressed) {
        stickman.update(deltaTime, inputDirection, jumpPressed);
    }

}
