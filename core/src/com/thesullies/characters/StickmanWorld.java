package com.thesullies.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.badlogic.gdx.physics.box2d.World;
import com.thesullies.Assets;
import com.thesullies.maps.Box2DContactListener;
import com.thesullies.maps.Constants;
import com.thesullies.maps.LevelMapManager;
import com.thesullies.screens.GamePlayingScreen;

/**
 * This class manages the game when playing the levels.
 * It contains both the physics world and the rendered world.
 * The Physics World  contains the physics entities, platforms, exits, coin shapes etc.
 * The Rendered World is responsible for drawing these entities.
 * <p>
 * Created by kosullivan on 04/01/2017.
 */
public class StickmanWorld {

    // Kees list of characters to add/remove from the game
    static CharacterManager characterManager = new CharacterManager();

    private Stickman stickman;

    public static List<Coin> coins;
    int level = 0;
    private SpriteBatch batcher;

    long levelTimeStart;

    // Physics Box2d World
    World physicsWorld;
    public WorldRenderer renderedWorld;
    private LevelMapManager levelMapManager;


    public StickmanWorld(SpriteBatch batcher) {
        this.batcher = batcher;
        levelMapManager = new LevelMapManager();
        startNewLevel();
    }

    private void startNewLevel() {
        this.coins = new ArrayList<Coin>();
        this.physicsWorld = new com.badlogic.gdx.physics.box2d.World(new Vector2(0, Constants.PHYSICS_GRAVITY), true);
        this.physicsWorld.setContactListener(new Box2DContactListener());
        try {
            levelMapManager.loadLevel(this.level, this.physicsWorld);
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.renderedWorld = new WorldRenderer(this.batcher, this, this.levelMapManager.getLevelMap());
        this.stickman = new Stickman(WorldRenderer.GAME_WIDTH / 2, WorldRenderer.GAME_HEIGHT, this.physicsWorld, this.levelMapManager);
        this.levelTimeStart = System.currentTimeMillis();
        Assets.materialise.play(1.0f);
    }


    public void update(float deltaTime) {

        renderedWorld.inputController.getUserInput();

        if (characterManager.entitiesToRemove.size() > 0)
            Assets.coinCollect.play(0.55f);
        for (GameObject entity : characterManager.entitiesToRemove) {
            if (entity instanceof Coin) {
                this.coins.remove(entity);
            }
            this.physicsWorld.destroyBody(entity.physicsBody);
        }
        characterManager.entitiesToRemove.clear();

        this.physicsWorld.step(1f / 60f, 6, 2);

        updateStickman(deltaTime, renderedWorld.inputController.inputDirection, renderedWorld.inputController.jumpPressed);
        updateCoins(deltaTime);

        if (this.stickman.isTouchingDoor() ) { //&& this.coins.size()==0) {
            showLevelCompleteScreen();
        }
    }

    public void showLevelCompleteScreen() {
        GamePlayingScreen.game.setLevelCompleteScreen();
    }

    public void gotoNextLevel() {
        level++;
        startNewLevel();
    }

    private void updateCoins(float deltaTime) {
        for (Coin coin : this.coins) {
            coin.update(deltaTime);
        }
    }

    private void updateStickman(float deltaTime, Vector2 inputDirection, boolean jumpPressed) {
        stickman.update(deltaTime, inputDirection, jumpPressed);
    }

    /**
     * Get the Rendered World to redisplay itself.
     */
    public void render() {
        Gdx.gl.glClearColor(0.6f, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.renderedWorld.render(stickman, levelMapManager.getBoundingRectCamera());
    }
}
