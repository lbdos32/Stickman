package com.thesullies.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
//import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.thesullies.Assets;
import com.thesullies.Stickman;

import static com.badlogic.gdx.Input.Keys.DOWN;
import static com.badlogic.gdx.Input.Keys.LEFT;
import static com.badlogic.gdx.Input.Keys.RIGHT;
import static com.badlogic.gdx.Input.Keys.UP;

/**
 * Created by kosullivan on 04/01/2017.
 */

public class GameScreen extends ScreenAdapter {

    private final Stickman game;
    static final int GAME_READY = 0;
    static final int GAME_RUNNING = 1;
    static final int GAME_PAUSED = 2;
    static final int GAME_LEVEL_END = 3;
    static final int GAME_OVER = 4;
    private final OrthogonalTiledMapRenderer renderer;
    private final int displayWidth;
    private final int displayHeight;
    private final ShapeRenderer shapeRenderer;
    int state;
    private final int GAME_WIDTH = 60;
    private final int GAME_HEIGHT = 40;
    private int constrolStickRadius;
    private int constrolStickNeutralRadius;

    OrthographicCamera guiCam;
    Vector3 touchPoint;
    Vector2 inputDirection;

    Vector2 controlStickNeutral;

    public GameScreen(Stickman game) {
        this.game = game;

        this.state = GAME_READY;
        guiCam = new OrthographicCamera();
        guiCam.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);
        guiCam.position.x = GAME_WIDTH / 2;
        guiCam.position.y = GAME_HEIGHT / 2;
        guiCam.update();
        touchPoint = new Vector3();
        inputDirection = new Vector2();
        this.renderer = new OrthogonalTiledMapRenderer(Assets.map, Assets.unitScale);

        this.displayWidth = Gdx.graphics.getWidth();
        this.displayHeight = Gdx.graphics.getHeight();
        this.shapeRenderer = new ShapeRenderer();

        controlStickNeutral = new Vector2(displayWidth / 10, displayHeight / 6);
        constrolStickRadius = displayWidth / 10;
        constrolStickNeutralRadius = displayWidth / 30;

    }

    @Override
    public void render(float delta) {
        update(delta);
        draw();
    }

    public void update(float deltaTime) {
        if (deltaTime > 0.1f) deltaTime = 0.1f;

        updateInputDirection();

        updateMapCameraPosition();
/*
        switch (state) {
            case GAME_READY:
                updateReady();
                break;
            case GAME_RUNNING:
                updateRunning(deltaTime);
                break;
            case GAME_PAUSED:
                updatePaused();
                break;
            case GAME_LEVEL_END:
                updateLevelEnd();
                break;
            case GAME_OVER:
                updateGameOver();
                break;
        }*/
    }

    private void updateMapCameraPosition() {
        if (inputDirection.x == LEFT) {
            guiCam.position.x -= 1;
        } else if (inputDirection.x == RIGHT) {
            guiCam.position.x += 1;
        }
        if (inputDirection.y == UP) {
            guiCam.position.y += 1;
        } else if (inputDirection.y == DOWN) {
            guiCam.position.y -= 1;
        }
        if (guiCam.position.x <= GAME_WIDTH / 2)
            guiCam.position.x = GAME_WIDTH / 2;
        if (guiCam.position.y <= GAME_HEIGHT / 2)
            guiCam.position.y = GAME_HEIGHT / 2;
        guiCam.update();


    }


    /**
     * Detect where on the screen the user has pressed and if this corresponds to up/down/left/right movement
     */
    private void updateInputDirection() {

        inputDirection.x = -1;
        inputDirection.y = -1;
        if (Gdx.input.isTouched()) {
            guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
            int inputY = displayHeight - Gdx.input.getY();
            //Gdx.app.log(Stickman.LOG_STICKMAN, String.format("touched=(x=%d, y=%d, adjustedY=%d) ", Gdx.input.getX(), Gdx.input.getY(), inputY));
            //Gdx.app.log(Stickman.LOG_STICKMAN, String.format("touched=(x=%f, y=%f) " , touchPoint.x, touchPoint.y));

            if (Gdx.input.getX() < (this.controlStickNeutral.x - constrolStickNeutralRadius) && Gdx.input.getX() > 0) {
                //Gdx.app.log(Stickman.LOG_STICKMAN, String.format("touched LEFT=(x=%d, y=%d) ", Gdx.input.getX(), Gdx.input.getY()));
                inputDirection.x = LEFT;
            } else if (Gdx.input.getX() > (this.controlStickNeutral.x + constrolStickNeutralRadius) && Gdx.input.getX() < this.controlStickNeutral.x + constrolStickRadius) {
                //Gdx.app.log(Stickman.LOG_STICKMAN, String.format("touched RIGHT=(x=%d, y=%d) ", Gdx.input.getX(), Gdx.input.getY()));
                inputDirection.x = RIGHT;
            }


            if (inputY < (this.controlStickNeutral.y - constrolStickNeutralRadius) && inputY >0 ) {
                //Gdx.app.log(Stickman.LOG_STICKMAN, String.format("touched DOWN=(x=%d, y=%d) ", Gdx.input.getX(),inputY));
                inputDirection.y = DOWN;
            } else if (inputY > (this.controlStickNeutral.y + constrolStickNeutralRadius) && inputY < (this.controlStickNeutral.y + constrolStickRadius)) {
                //Gdx.app.log(Stickman.LOG_STICKMAN, String.format("touched UP=(x=%d, y=%d) ", Gdx.input.getX(), inputY));
                inputDirection.y = UP;
            }
        }
    }


    public void draw() {
        GL20 gl = Gdx.gl;
        gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        renderer.render();

        guiCam.update();
        game.batcher.setProjectionMatrix(guiCam.combined);
        game.batcher.enableBlending();
        game.batcher.begin();
        renderer.setView(guiCam);
        renderer.render();

        shapeRenderer.setColor(Color.RED);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(this.controlStickNeutral.x, controlStickNeutral.y, constrolStickNeutralRadius);
        drawRightController();
        drawUpController();
        drawLeftController();
        drawDownController();
        shapeRenderer.end();
/*
        switch (state) {
            case GAME_READY:
                presentReady();
                break;
            case GAME_RUNNING:
                presentRunning();
                break;
            case GAME_PAUSED:
                presentPaused();
                break;
            case GAME_LEVEL_END:
                presentLevelEnd();
                break;
            case GAME_OVER:
                presentGameOver();
                break;
        }*/
        game.batcher.end();
    }

    private void drawDownController() {
        if (inputDirection.y == DOWN)
            shapeRenderer.setColor(Color.BLUE);
        else
            shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.arc(this.controlStickNeutral.x, controlStickNeutral.y, constrolStickRadius, 270 - 15, 30);
    }

    private void drawLeftController() {
        if (inputDirection.x == LEFT)
            shapeRenderer.setColor(Color.BLUE);
        else
            shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.arc(this.controlStickNeutral.x, controlStickNeutral.y, constrolStickRadius, 180 - 15, 30);
    }

    private void drawUpController() {
        if (inputDirection.y == UP)
            shapeRenderer.setColor(Color.BLUE);
        else
            shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.arc(this.controlStickNeutral.x, controlStickNeutral.y, constrolStickRadius, 90 - 15, 30);
    }

    private void drawRightController() {
        if (inputDirection.x == RIGHT)
            shapeRenderer.setColor(Color.BLUE);
        else
            shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.arc(this.controlStickNeutral.x, controlStickNeutral.y, constrolStickRadius, 360 - 15, 30);
    }

    @Override
    public void pause() {
        if (state == GAME_RUNNING) state = GAME_PAUSED;
    }

}
