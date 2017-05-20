package com.thesullies.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import com.thesullies.characters.WorldRenderer;

/**
 * Created by kosullivan on 04/01/2017.
 */

public class LevelCompleteScreen extends StickmanScreenAdapter {

    private final GlyphLayout glyphLayout;
    BitmapFont font;
    GameManager gameManager;
    private long showStartTime;

    public LevelCompleteScreen(GameManager gameManager) {

        this.gameManager = gameManager;

        this.font = new BitmapFont();
        this.font.setColor(Color.WHITE);
        this.font.getData().setScale(4.0f);

        this.glyphLayout = new GlyphLayout();
    }

    /**
     * Level Complete screen is being
     */
    @Override
    public void show() {
        showStartTime = System.currentTimeMillis();
    }

    @Override
    public void init(GameManager gameManager) {

    }

    @Override
    public void render(float delta) {
        update(delta);
        draw();
    }

    public void update(float deltaTime) {
        if (deltaTime > 0.1f) deltaTime = 0.1f;
    }


    public void draw() {
        Gdx.gl.glClearColor(0.2f,0.6f,0.6f,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        this.gameManager.stickmanGame.batcher.begin();

        this.glyphLayout.setText(font, "Level Complete", Color.BLUE, Gdx.graphics.getWidth(), Align.center, true);
        this.font.draw(
                this.gameManager.stickmanGame.batcher,
                glyphLayout,0, (Gdx.graphics.getHeight()/2-this.glyphLayout.height/2));
        this.glyphLayout.setText(font, "Touch for next level", Color.BLUE, Gdx.graphics.getWidth(), Align.center, true);
        this.font.draw(
                this.gameManager.stickmanGame.batcher,
                this.glyphLayout,0, (Gdx.graphics.getHeight()/2-this.glyphLayout.height/2)+100);
        this.gameManager.stickmanGame.batcher.end();
    }

    public boolean isReadyToProgress() {
        return true;

    }

    @Override
    public void pause() {
        //if (state == GAME_RUNNING) state = GAME_PAUSED;
    }


}
