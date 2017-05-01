package com.thesullies;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.thesullies.screens.GameManager;
import com.thesullies.screens.StickmanScreenAdapter;

/**
 * Created by kosullivan on 04/01/2017.
 */

public class StickmanGame extends Game {

    public static final String LOG_STICKMAN = "STICKMAN";
    // used by all screens
    public SpriteBatch batcher;

    static GameManager gameManager;

    @Override
    public void create() {
        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        gameManager = new GameManager(this);
        batcher = new SpriteBatch();
        Assets.load();
        switchScreen(gameManager.getGameStartScreen());
    }


    @Override
    public void render() {
        super.render();
        if (Gdx.input.isTouched()) {

            if (getScreen().equals(gameManager.getGameStartScreen())) {
                // On Start Screen, when touched go to game screen.
                gameManager.getGamePlayingScreen().restartGame();
                setScreen(gameManager.getGamePlayingScreen());
            } else if (getScreen().equals(gameManager.getLevelCompleteScreen())) {
                if (gameManager.getLevelCompleteScreen().isReadyToProgress()) {
                    setScreen(gameManager.getGamePlayingScreen());
                    gameManager.getGamePlayingScreen().nextLevel();
                }
            } else if (getScreen().equals(gameManager.getGameOverScreen())) {
                // On GameOver Screen, when touched restart the game
                switchScreen(gameManager.getGameStartScreen());
            }
        }
    }


    private void switchScreen(StickmanScreenAdapter screen) {
        screen.init(gameManager);
        setScreen(screen);
    }

    public void setGameOverScreen() { switchScreen(gameManager.getGameOverScreen());}

    public void setLevelCompleteScreen() {
        switchScreen(gameManager.getLevelCompleteScreen());
    }

}
