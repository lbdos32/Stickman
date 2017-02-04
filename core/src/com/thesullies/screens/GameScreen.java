package com.thesullies.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.thesullies.StickmanGame;
import com.thesullies.characters.StickmanWorld;
import com.thesullies.characters.WorldRenderer;
import com.thesullies.maps.Constants;

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

    private final StickmanWorld stickmanWorld;

    public GameScreen(StickmanGame game) {
        this.game = game;
        this.state = GAME_READY;
        this.stickmanWorld = new StickmanWorld(game.batcher);
    }

    @Override
    public void render(float delta) {
        update(delta);
        // Step the physics simulation forward at a rate of 60hz

        draw();
    }

    public void update(float deltaTime) {
        if (deltaTime > 0.1f) deltaTime = 0.1f;
        stickmanWorld.update(deltaTime);
    }


    public void draw() {
        Gdx.gl.glClearColor(0.6f, 0, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stickmanWorld.worldRenderer.render();
    }


    @Override
    public void pause() {
        if (state == GAME_RUNNING) state = GAME_PAUSED;
    }

}
