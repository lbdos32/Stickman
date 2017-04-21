package com.thesullies.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.thesullies.StickmanGame;
import com.thesullies.characters.StickmanWorld;

/**
 * Created by kosullivan on 04/01/2017.
 */

public class GamePlayingScreen extends ScreenAdapter {

    public static StickmanGame game;
    static final int GAME_READY = 0;
    static final int GAME_RUNNING = 1;
    static final int GAME_PAUSED = 2;
    static final int GAME_LEVEL_END = 3;
    static final int GAME_OVER = 4;

    int state;

    private final StickmanWorld stickmanWorld;

    public GamePlayingScreen(StickmanGame game) {
        this.game = game;
        this.state = GAME_READY;
        this.stickmanWorld = new StickmanWorld(game.batcher);
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw();
    }

    public void update(float deltaTime) {
        if (deltaTime > 0.1f) deltaTime = 0.1f;
        stickmanWorld.update(deltaTime);
    }


    public void draw() {
        stickmanWorld.render();
    }


    @Override
    public void pause() {
        if (state == GAME_RUNNING) state = GAME_PAUSED;
    }

    public void restartGame() {
        this.stickmanWorld.restartGame();
    }

    public void nextLevel() {
        this.stickmanWorld.gotoNextLevel();
    }
}
