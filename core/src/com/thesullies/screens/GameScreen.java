package com.thesullies.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.thesullies.Assets;
import com.thesullies.StickmanGame;
import com.thesullies.characters.World;
import com.thesullies.characters.WorldRenderer;

import static com.badlogic.gdx.Input.Keys.DOWN;
import static com.badlogic.gdx.Input.Keys.LEFT;
import static com.badlogic.gdx.Input.Keys.RIGHT;
import static com.badlogic.gdx.Input.Keys.UP;

/**
 * Created by kosullivan on 04/01/2017.
 */

public class GameScreen extends ScreenAdapter {

    private final StickmanGame game;
    static final int GAME_READY = 0;
    static final int GAME_RUNNING = 1;
    static final int GAME_PAUSED = 2;
    static final int GAME_LEVEL_END = 3;
    static final int GAME_OVER = 4;

    int state;

    private final World.WorldListener worldListener;
    private final World world;
    private final WorldRenderer worldRenderer;


    public GameScreen(StickmanGame game) {
        this.game = game;

        this.state = GAME_READY;


        this.worldListener = new World.WorldListener() {
            @Override
            public void jump() {
                //Assets.playSound(Assets.jumpSound);
            }

            @Override
            public void highJump() {
                //Assets.playSound(Assets.highJumpSound);
            }

            @Override
            public void hit() {
                //Assets.playSound(Assets.hitSound);
            }

            @Override
            public void coin() {
                //Assets.playSound(Assets.coinSound);
            }
        };
        this.world = new World(worldListener);
        this.worldRenderer = new WorldRenderer(game.batcher, world);

        //Gdx.app.log(Stickman.LOG_STICKMAN, String.format("mapWidth=%d, mapHeight=%d, tilePixelWidth=%d, tilePixelHeight=%d ", mapWidth,mapHeight,tilePixelWidth,tilePixelHeight));
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw();
    }

    public void update(float deltaTime) {
        if (deltaTime > 0.1f) deltaTime = 0.1f;
        worldRenderer.update(deltaTime);
    }



    public void draw() {
        Gdx.gl.glClearColor(0.6f, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        worldRenderer.render();
    }



    @Override
    public void pause() {
        if (state == GAME_RUNNING) state = GAME_PAUSED;
    }

}
