package com.thesullies.screens;

import com.badlogic.gdx.ScreenAdapter;
import com.thesullies.StickmanGame;

/**
 * This class is responsible for returning the correct GameScreen - which will include the
 * GameStart screen, each level, Game Over screen, High Score Screen etc..
 * Created by kosullivan on 06/02/2017.
 */
public class GameManager {

    private GameOverScreen gameOverScreen = null;
    private GamePlayingScreen gamePlayingScreen = null;
    private StickmanGame stickmanGame = null;
    private GameStartScreen gameStartScreen;
    private LevelCompleteScreen levelCompleteScreen;

    public GameManager(StickmanGame stickmanGame) {
        this.stickmanGame = stickmanGame;
    }

    public LevelCompleteScreen getLevelCompleteScreen() {

        if (levelCompleteScreen==null) {
            levelCompleteScreen = new LevelCompleteScreen(this);
        }
        return levelCompleteScreen;
    }

    public ScreenAdapter getGameStartScreen() {

        if (gameStartScreen==null) {
            gameStartScreen = new GameStartScreen(this);
        }
        return gameStartScreen;

    }

    public GamePlayingScreen getGamePlayingScreen() {

        if (gamePlayingScreen==null) {
            gamePlayingScreen = new GamePlayingScreen(this.stickmanGame);
        }
        return gamePlayingScreen;
    }

    public ScreenAdapter getGameOverScreen() {
        if (gameOverScreen==null) {
            gameOverScreen = new GameOverScreen(this);
        }
        return gameOverScreen;
    }

    public ScreenAdapter getHighScoreScreen() {
        return null;
    }
}
