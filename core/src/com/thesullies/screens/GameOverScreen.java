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

/**
 * Created by kosullivan on 04/01/2017.
 */

public class GameOverScreen extends StickmanScreenAdapter {

    private OrthographicCamera guiCam;
    BitmapFont font;
    GameManager gameManager;
    GlyphLayout glyphLayout;


    float red = 0.9f, green = 0.0f, blue = 0.0f;

    float textYOffset = 0f;


    /**
     * This is used to scale the text up in size
     */
    float textScale = 0.1f;

    /**
     * boolean will store true or false. If true, "press to start" will be displayed on screen.
     */
    boolean blinkStatus = true;
    /**
     * Keep a counter of how long the "press to start" has been on or off
     */
    float blinkTimeCounter;

    public static final int TEXT_DROP_SPEED = 10;
    public static final float TEXT_SCALE_SPEED = 0.2f;
    public static final float TEXT_SCALE_TARGET = 5f;
    public static final float BLINK_TIME_SECONDS = 1f;

    public GameOverScreen(GameManager gameManager) {
        init(gameManager);
    }

    @Override
    public void init(GameManager gameManager) {
        this.gameManager = gameManager;

        if (this.glyphLayout == null) {
            this.glyphLayout = new GlyphLayout();
        }

        if (this.font == null) {
            this.font = new BitmapFont();
            this.font.setColor(Color.WHITE);
        }

        this.textYOffset = Gdx.graphics.getHeight() / 2;
        this.textScale = 0.1f;

        red = 0.9f;
        green = 0.0f;
        blue = 0.0f;
    }

    @Override
    public void render(float delta) {
        update(delta);
        draw();
    }

    public void update(float deltaTime) {
        if (deltaTime > 0.1f) deltaTime = 0.1f;

        /**
         * While the text scale is smaller than the target scale, keep increasing it.
         */
        if (this.textScale < TEXT_SCALE_TARGET) {
            this.textScale += TEXT_SCALE_SPEED;
            this.font.getData().setScale(this.textScale);
        }

        /**
         * If the text is at the target size, then start the counter for the blinking text
         */
        if (isTextAtTargetSize())
            this.blinkTimeCounter += deltaTime;
        /**
         * If the blink counter is longer than the blink time, then reset the counter and reverse the true/false value
         */
        if (this.blinkTimeCounter > BLINK_TIME_SECONDS) {
            this.blinkTimeCounter = 0;

            /**
             * This ! (called a NOT operator) will reverse the true/false value.
             */
            this.blinkStatus = !this.blinkStatus;
        }

        green += 0.005f;
        if (green >= 1)
            green = 1;

        blue += 0.005;
        if (green >= 1)
            green = 1;
        //if (textYOffset>Gdx.graphics.getHeight()/2)
        //    textYOffset-=TEXT_DROP_SPEED;

    }


    public void draw() {

        Gdx.gl.glClearColor(red, green, blue, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        this.gameManager.stickmanGame.batcher.begin();

        this.glyphLayout.setText(font, "Ha ha ha GAME OVER", Color.BLUE, Gdx.graphics.getWidth(), Align.center, true);
        float yPos = this.textYOffset;// + Gdx.graphics.getHeight()/2-this.glyphLayout.height/2;
        this.font.draw(
                this.gameManager.stickmanGame.batcher,
                glyphLayout, 0, yPos);

        /**
         * Only draw the "touch screen to start" text if blinkStatus is true AND the text is at the target size.
         */
        if (this.blinkStatus && isTextAtTargetSize()) {
            this.glyphLayout.setText(font, "Touch screen to restart", Color.WHITE, Gdx.graphics.getWidth(), Align.center, true);
            yPos = this.textYOffset - (30 * this.textScale); // + (Gdx.graphics.getHeight()/2-this.glyphLayout.height/2)
            this.font.draw(
                    this.gameManager.stickmanGame.batcher,
                    glyphLayout, 0, yPos);
        }
        this.gameManager.stickmanGame.batcher.end();
    }

    private boolean isTextAtTargetSize() {
        return this.textScale >= TEXT_SCALE_TARGET;
    }

}
