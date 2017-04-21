/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.thesullies.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.FPSLogger;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.TimeUtils;
import com.thesullies.Assets;
import com.thesullies.InputController;
import com.thesullies.maps.Constants;
import com.thesullies.maps.LevelMapManager;
import com.thesullies.maps.MapBodyBuilder;


/**
 * This class is responsible for
 * <p>
 * - displaying all the platform graphics onscreen,
 * - displaying Stickman, coins, food baddies etc.
 * - displaying score & timecountdown.
 * - Getting user input
 */
public class WorldRenderer {

    private static final FPSLogger fpsLogger = new FPSLogger();
    public static final int GAME_WIDTH = 120;
    public static final int GAME_HEIGHT = 80;

    StickmanWorld stickmanWorld;
    public static OrthographicCamera guiCam;
    SpriteBatch batch;
    SpriteBatch debugBatch;

    SpriteBatch gameStatusBatch;
    BitmapFont gameStatusFont;

    private OrthogonalTiledMapRenderer mapRenderer;


    BitmapFont font = null;
    public static boolean debugOutput = true;
    BitmapFont debugFont = null;

    Matrix4 debugMatrix;
    Box2DDebugRenderer debugRenderer;

    InputController inputController;

    private int displayWidth, displayHeight;

    // used for FPS calculation
    private long lastTimeCounted;
    private float sinceChange;
    private float frameRate;

    public WorldRenderer(SpriteBatch batch, StickmanWorld stickmanWorld, TiledMap map) {
        this.stickmanWorld = stickmanWorld;

        this.displayWidth = Gdx.graphics.getWidth();
        this.displayHeight = Gdx.graphics.getHeight();


        guiCam = new OrthographicCamera();
        guiCam.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);
        guiCam.position.set(GAME_WIDTH / 2, GAME_HEIGHT / 2, 0);
        guiCam.zoom = 1.0f;
        guiCam.update();

        this.mapRenderer = new OrthogonalTiledMapRenderer(map, LevelMapManager.MAP_UNIT_SCALE);

        this.batch = batch;
        debugBatch = new SpriteBatch();
        gameStatusBatch = new SpriteBatch();


        font = new BitmapFont();
        font.setColor(Color.GREEN);
        font.getData().setScale(0.5f);
        debugFont = new BitmapFont();
        debugFont.setColor(Color.WHITE);
        debugFont.getData().setScale(2.0f);

        this.gameStatusFont = new BitmapFont();
        this.gameStatusFont.setColor(Color.WHITE);
        this.gameStatusFont.getData().setScale(3.0f);


        debugRenderer = new Box2DDebugRenderer();

        inputController = new InputController(displayWidth, displayHeight);
    }


    public void render(Stickman stickman, Rectangle boundingRectCamera) {

        fpsLogger.log();

        long delta = TimeUtils.timeSinceMillis(lastTimeCounted);
        lastTimeCounted = TimeUtils.millis();
        sinceChange += delta;
        if(sinceChange >= 1000) {
            sinceChange = 0;
            frameRate = Gdx.graphics.getFramesPerSecond();
        }

        updateCam(stickman, boundingRectCamera);
        batch.setProjectionMatrix(guiCam.combined);
        debugMatrix = batch.getProjectionMatrix().cpy().scale(Constants.PHYSICS_PIXELS_TO_METERS,
                Constants.PHYSICS_PIXELS_TO_METERS, 0);
        renderMap();
        inputController.renderInputControls();
        renderObjects(stickman);
        renderStatus(stickman);
        renderDebug(stickman);
        debugRenderer.render(this.stickmanWorld.physicsWorld, debugMatrix);
    }

    private void renderStatus(Stickman stickman) {
        this.gameStatusBatch.begin();
        this.gameStatusFont.draw(this.gameStatusBatch, String.format("Level: %d", this.stickmanWorld.level), displayWidth - 500, displayHeight - 20);

        this.gameStatusFont.draw(this.gameStatusBatch, String.format("Time:  %d", this.secondsSinceLevelStart()), displayWidth - 500, displayHeight - 60);
        if (this.stickmanWorld.coins.size() > 0)
            this.gameStatusFont.draw(this.gameStatusBatch, String.format("Remaining coins: %d", this.stickmanWorld.coins.size()), displayWidth - 500, displayHeight - 100);
        else
            this.gameStatusFont.draw(this.gameStatusBatch, String.format("Find the exit!"), displayWidth - 500, displayHeight - 100);
        this.gameStatusBatch.end();
    }

    private int secondsSinceLevelStart() {
        long now = System.currentTimeMillis();
        return (int) ((now - this.stickmanWorld.levelTimeStart) / 1000);
    }

    private void renderDebug(Stickman stickman) {
        if (debugOutput) {
            debugBatch.begin();
            debugFont.draw(debugBatch, stickman.toString(), 1, displayHeight - 20);
            debugFont.draw(debugBatch, "fps " + frameRate, 3, displayHeight - 60);
            debugBatch.end();
        }
    }

    /**
     * The camera will follow the stickman around.
     *
     * @param stickman
     */
    private void updateCam(Stickman stickman, Rectangle boundingRectCamera) {
        guiCam.position.x = stickman.position.x;
        guiCam.position.y = stickman.position.y;

        if (guiCam.position.x <= boundingRectCamera.getX())
            guiCam.position.x = boundingRectCamera.getX();
        if (guiCam.position.y <= boundingRectCamera.getY())
            guiCam.position.y = boundingRectCamera.getY();
        if (guiCam.position.x > boundingRectCamera.getWidth())
            guiCam.position.x = boundingRectCamera.getWidth();
        if (guiCam.position.y > boundingRectCamera.getHeight())
            guiCam.position.y = boundingRectCamera.getHeight();

        guiCam.update();
    }


    public void renderObjects(Stickman stickman) {
        batch.enableBlending();
        batch.begin();
        renderStickman(stickman);
        renderCoins();
        renderDynamicGameObjects();
        font.draw(batch, "Stickman go --->", 1, GAME_HEIGHT - 4); //displayHeight-20);
        batch.end();
    }


    private void renderMap() {
        mapRenderer.setView(guiCam);
        mapRenderer.render();
    }

    private void renderStickman(Stickman stickman) {
        stickman.render(this.batch);
    }

    private void renderCoins() {
        for (Coin coin : stickmanWorld.coins) {
            coin.render(this.batch);
        }
    }

    private void renderDynamicGameObjects() {
        for (DynamicGameObject gameObject : stickmanWorld.dynamicGameObjects) {
            gameObject.render(this.batch);
        }
    }

}
