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
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.World;
import com.thesullies.Assets;
import com.thesullies.maps.Constants;
import com.thesullies.maps.MapBodyBuilder;

import static com.badlogic.gdx.Input.Keys.DOWN;
import static com.badlogic.gdx.Input.Keys.LEFT;
import static com.badlogic.gdx.Input.Keys.RIGHT;
import static com.badlogic.gdx.Input.Keys.UP;

public class WorldRenderer {

    public static final int GAME_WIDTH = 120;
    public static final int GAME_HEIGHT = 80;


    StickmanWorld stickmanWorld;
    public static OrthographicCamera guiCam;
    SpriteBatch batch;
    SpriteBatch debugBatch;
    private OrthogonalTiledMapRenderer mapRenderer;
    private ShapeRenderer shapeRenderer;

    Vector2 controlStickNeutral;
    Vector2 controlStickJump;

    private final int displayWidth;
    private final int displayHeight;

    private int constrolStickRadius;
    private int constrolStickNeutralRadius;
    Vector2 inputDirection;
    public boolean jumpPressed;
    Rectangle boundingRectCamera;
    public static Rectangle boundingRectStickman;
    BitmapFont font = null;
    public static boolean debugOutput = true;
    BitmapFont debugFont = null;

    private final Rectangle rectLeftControl;
    private final Rectangle rectRightControl;
    private final Rectangle rectUpControl;
    private final Rectangle rectDownControl;
    private final Rectangle rectJumpControl;


    public static float mapCellSize;

    Matrix4 debugMatrix;
    Box2DDebugRenderer debugRenderer;


    public WorldRenderer(SpriteBatch batch, StickmanWorld stickmanWorld, TiledMap map) {
        this.stickmanWorld = stickmanWorld;

        guiCam = new OrthographicCamera();
        guiCam.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);
        guiCam.position.set(GAME_WIDTH / 2, GAME_HEIGHT / 2, 0);
        guiCam.zoom = 1.0f;
        guiCam.update();

        this.displayWidth = Gdx.graphics.getWidth();
        this.displayHeight = Gdx.graphics.getHeight();
        this.shapeRenderer = new ShapeRenderer();

        controlStickNeutral = new Vector2(displayWidth / 10, displayHeight / 6);
        controlStickJump = new Vector2(displayWidth - displayWidth / 10, displayHeight / 6);
        constrolStickRadius = displayWidth / 10;
        constrolStickNeutralRadius = displayWidth / 30;

        this.rectLeftControl = new Rectangle(displayWidth / 10 - constrolStickRadius, displayHeight / 6, constrolStickRadius, constrolStickRadius);
        this.rectRightControl = new Rectangle(displayWidth / 10 + constrolStickRadius, displayHeight / 6, constrolStickRadius, constrolStickRadius);
        this.rectUpControl = new Rectangle(displayWidth / 10, constrolStickRadius + displayHeight / 6, constrolStickRadius, constrolStickRadius);
        this.rectDownControl = new Rectangle(displayWidth / 10, displayHeight / 6 - constrolStickRadius, constrolStickRadius, constrolStickRadius);
        this.rectJumpControl = new Rectangle(displayWidth - displayWidth / 10, displayHeight / 6, constrolStickRadius, constrolStickRadius);

        this.mapRenderer = new OrthogonalTiledMapRenderer(map, Assets.unitScale);
        this.shapeRenderer = new ShapeRenderer();

        //this.cam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        //this.cam.position.set(FRUSTUM_WIDTH / 2, FRUSTUM_HEIGHT / 2, 0);
        this.batch = batch; // /new SpriteBatch();
        debugBatch = new SpriteBatch();

        inputDirection = new Vector2();

        TiledMapTileLayer layer = (TiledMapTileLayer) map.getLayers().get(Constants.MAP_LAYER_PLATFORM);
        mapCellSize = layer.getTileHeight();
        boundingRectCamera = new Rectangle(WorldRenderer.GAME_WIDTH / 2, WorldRenderer.GAME_HEIGHT / 2, layer.getWidth() * layer.getTileWidth() * Assets.unitScale - WorldRenderer.GAME_WIDTH / 2, layer.getHeight() * layer.getTileHeight() * Assets.unitScale - WorldRenderer.GAME_HEIGHT / 2);

        boundingRectStickman = new Rectangle(0, 0, layer.getWidth() * layer.getTileWidth() * Assets.unitScale, layer.getHeight() * layer.getTileHeight() * Assets.unitScale);

        font = new BitmapFont();
        font.setColor(Color.GREEN);
        font.getData().setScale(0.5f);
        debugFont = new BitmapFont();
        debugFont.setColor(Color.WHITE);
        debugFont.getData().setScale(2.0f);

        debugRenderer = new Box2DDebugRenderer();

        // Load the data from the ObjectLayer in the map
        MapBodyBuilder.buildShapes(map, mapCellSize * Assets.unitScale * 2, stickmanWorld.physicsWorld);
    }


    /**
     * Called each game loop to get input from the user and update the position of the stickman
     * and all game components.
     *
     * @param deltaTime
     */
    public void update(float deltaTime) {
        getUserInput();
    }


    /**
     * Detect where on the screen the user has pressed and if this corresponds to up/down/left/right movement.
     */
    private void getUserInput() {

        this.inputDirection.x = -1;
        this.inputDirection.y = -1;
        this.jumpPressed = false;
        if (Gdx.input.isTouched()) {
            //guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

            for (int i = 0; i < 10; i++) {
                if (Gdx.input.isTouched(i)) {
                    int inputY = displayHeight - Gdx.input.getY(i);
                    int inputX = Gdx.input.getX(i);
                    checkInput(inputX, inputY);
                }
            }
        }
    }

    private void checkInput(int inputX, int inputY) {
        if (rectDownControl.contains(inputX, inputY))
            inputDirection.y = DOWN;
        else if (rectUpControl.contains(inputX, inputY))
            inputDirection.y = UP;

        if (rectLeftControl.contains(inputX, inputY))
            inputDirection.x = LEFT;
        else if (rectRightControl.contains(inputX, inputY))
            inputDirection.x = RIGHT;

        if (rectJumpControl.contains(inputX, inputY))
            jumpPressed = true;
    }


    public void render() {

        updateCam();
        batch.setProjectionMatrix(guiCam.combined);
        debugMatrix = batch.getProjectionMatrix().cpy().scale(Constants.PHYSICS_PIXELS_TO_METERS,
                Constants.PHYSICS_PIXELS_TO_METERS, 0);
        renderMap();
        renderInputControls();
        renderObjects();
        renderDebug();
        debugRenderer.render(this.stickmanWorld.physicsWorld, debugMatrix);
    }

    private void renderDebug() {
        if (debugOutput) {
            debugBatch.begin();
            debugFont.draw(debugBatch, stickmanWorld.stickman.toString(), 1, displayHeight - 20);
            debugBatch.end();
        }
    }

    private void updateCam() {
        guiCam.position.x = stickmanWorld.stickman.position.x;
        guiCam.position.y = stickmanWorld.stickman.position.y;

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


    public void renderObjects() {
        batch.enableBlending();
        batch.begin();
        renderStickman();
        renderCoins();
        font.draw(batch, "Stickman go --->", 1, GAME_HEIGHT - 4); //displayHeight-20);
        batch.end();
    }

    private void renderInputControls() {
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(this.controlStickNeutral.x, controlStickNeutral.y, constrolStickNeutralRadius);
        drawJumpController();

        drawRightController();
        drawUpController();
        drawLeftController();
        drawDownController();


        shapeRenderer.end();

    }

    private void drawJumpController() {
//        shapeRenderer.circle(this.controlStickJump.x, controlStickJump.y, constrolStickNeutralRadius);
        if (this.jumpPressed)
            shapeRenderer.setColor(Color.BLUE);
        else
            shapeRenderer.setColor(Color.WHITE);

        shapeRenderer.rect(rectJumpControl.x, rectJumpControl.y, rectJumpControl.width, rectJumpControl.height);

    }


    private void drawDownController() {
        if (inputDirection.y == DOWN)
            shapeRenderer.setColor(Color.BLUE);
        else
            shapeRenderer.setColor(Color.WHITE);
        //shapeRenderer.arc(this.controlStickNeutral.x, controlStickNeutral.y, constrolStickRadius, 270 - 15, 30);
        shapeRenderer.rect(rectDownControl.x, rectDownControl.y, rectDownControl.width, rectDownControl.height);
    }

    private void drawLeftController() {
        if (inputDirection.x == LEFT)
            shapeRenderer.setColor(Color.BLUE);
        else
            shapeRenderer.setColor(Color.WHITE);
        //shapeRenderer.arc(this.controlStickNeutral.x, controlStickNeutral.y, constrolStickRadius, 180 - 15, 30);
        shapeRenderer.rect(rectLeftControl.x, rectLeftControl.y, rectLeftControl.width, rectLeftControl.height);
    }

    private void drawUpController() {
        if (inputDirection.y == UP)
            shapeRenderer.setColor(Color.BLUE);
        else
            shapeRenderer.setColor(Color.WHITE);
//        shapeRenderer.arc(this.controlStickNeutral.x, controlStickNeutral.y, constrolStickRadius, 90 - 15, 30);
        shapeRenderer.rect(rectUpControl.x, rectUpControl.y, rectUpControl.width, rectUpControl.height);
    }

    private void drawRightController() {
        if (inputDirection.x == RIGHT)
            shapeRenderer.setColor(Color.BLUE);
        else
            shapeRenderer.setColor(Color.WHITE);
//        shapeRenderer.arc(this.controlStickNeutral.x, controlStickNeutral.y, constrolStickRadius, 360 - 15, 30);
        shapeRenderer.rect(rectRightControl.x, rectRightControl.y, rectRightControl.width, rectRightControl.height);

    }

    private void renderMap() {
        mapRenderer.setView(guiCam);
        mapRenderer.render();
    }

    private void renderStickman() {
        stickmanWorld.stickman.render(this.batch);
    }

    private void renderCoins() {
        for (Coin coin : stickmanWorld.coins) {
            coin.render(this.batch);
        }
    }
/*
    private void renderItems () {
		int len = world.springs.size();
		for (int i = 0; i < len; i++) {
			Spring spring = world.springs.get(i);
			batch.draw(Assets.spring, spring.position.x - 0.5f, spring.position.y - 0.5f, 1, 1);
		}

		len = world.coins.size();
		for (int i = 0; i < len; i++) {
			Coin coin = world.coins.get(i);
			TextureRegion keyFrame = Assets.coinAnim.getKeyFrame(coin.stateTime, Animation.ANIMATION_LOOPING);
			batch.draw(keyFrame, coin.position.x - 0.5f, coin.position.y - 0.5f, 1, 1);
		}
	}

	private void renderSquirrels () {
		int len = world.squirrels.size();
		for (int i = 0; i < len; i++) {
			Squirrel squirrel = world.squirrels.get(i);
			TextureRegion keyFrame = Assets.squirrelFly.getKeyFrame(squirrel.stateTime, Animation.ANIMATION_LOOPING);
			float side = squirrel.velocity.x < 0 ? -1 : 1;
			if (side < 0)
				batch.draw(keyFrame, squirrel.position.x + 0.5f, squirrel.position.y - 0.5f, side * 1, 1);
			else
				batch.draw(keyFrame, squirrel.position.x - 0.5f, squirrel.position.y - 0.5f, side * 1, 1);
		}
	}

	private void renderCastle () {
		Castle castle = world.castle;
		batch.draw(Assets.castle, castle.position.x - 1, castle.position.y - 1, 2, 2);
	}*/
}
