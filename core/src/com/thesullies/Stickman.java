package com.thesullies;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.thesullies.screens.GameScreen;

/**
 * Created by kosullivan on 04/01/2017.
 */

public class Stickman extends Game {

    public static final String LOG_STICKMAN = "STICKMAN";
    // used by all screens
    public SpriteBatch batcher;

    @Override
    public void create() {
        batcher = new SpriteBatch();
        //Settings.load();
        Assets.load();
        setScreen(new GameScreen(this));
    }

    @Override
    public void render() {
        super.render();
    }

}
