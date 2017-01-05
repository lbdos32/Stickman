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
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.thesullies.Assets;

import static com.badlogic.gdx.Input.Keys.DOWN;
import static com.badlogic.gdx.Input.Keys.LEFT;
import static com.badlogic.gdx.Input.Keys.RIGHT;
import static com.badlogic.gdx.Input.Keys.UP;

public class WorldRenderer {

    public static final int GAME_WIDTH = 120;
    public static final int GAME_HEIGHT = 80;


    World world;
    OrthographicCamera guiCam;
    SpriteBatch batch;
    SpriteBatch debugBatch;
    private OrthogonalTiledMapRenderer mapRenderer;
    private ShapeRenderer shapeRenderer;

    Vector2 controlStickNeutral;

    private final int displayWidth;
    private final int displayHeight;

    private int constrolStickRadius;
    private int constrolStickNeutralRadius;
    Vector2 inputDirection;
    Rectangle boundingRectCamera;
    public static Rectangle boundingRectStickman;
    BitmapFont font = null;
    boolean debugOutput = true;
    BitmapFont debugFont = null;

    public WorldRenderer(SpriteBatch batch, World world) {
        this.world = world;

        guiCam = new OrthographicCamera();
        guiCam.setToOrtho(false, GAME_WIDTH, GAME_HEIGHT);
        guiCam.position.set(GAME_WIDTH / 2, GAME_HEIGHT / 2, 0);
        guiCam.zoom = 1.0f;
        guiCam.update();

        this.displayWidth = Gdx.graphics.getWidth();
        this.displayHeight = Gdx.graphics.getHeight();
        this.shapeRenderer = new ShapeRenderer();

        controlStickNeutral = new Vector2(displayWidth / 10, displayHeight / 6);
        constrolStickRadius = displayWidth / 10;
        constrolStickNeutralRadius = displayWidth / 30;

        this.mapRenderer = new OrthogonalTiledMapRenderer(Assets.map, Assets.unitScale);
        this.shapeRenderer = new ShapeRenderer();

        //this.cam = new OrthographicCamera(FRUSTUM_WIDTH, FRUSTUM_HEIGHT);
        //this.cam.position.set(FRUSTUM_WIDTH / 2, FRUSTUM_HEIGHT / 2, 0);
        this.batch = batch; // /new SpriteBatch();
        debugBatch = new SpriteBatch();

        inputDirection = new Vector2();

        TiledMapTileLayer layer = (TiledMapTileLayer) Assets.map.getLayers().get(0);
        boundingRectCamera = new Rectangle(WorldRenderer.GAME_WIDTH / 2, WorldRenderer.GAME_HEIGHT / 2, layer.getWidth() * layer.getTileWidth() * Assets.unitScale - WorldRenderer.GAME_WIDTH / 2, layer.getHeight() * layer.getTileHeight() * Assets.unitScale - WorldRenderer.GAME_HEIGHT / 2);

        boundingRectStickman = new Rectangle(0, 0, layer.getWidth() * layer.getTileWidth() * Assets.unitScale, layer.getHeight() * layer.getTileHeight() * Assets.unitScale);

        font = new BitmapFont();
        font.setColor(Color.GREEN);
        font.getData().setScale(0.5f);
        debugFont = new BitmapFont();
        debugFont.setColor(Color.WHITE);
        debugFont.getData().setScale(5.0f);
    }


    /**
     * Called each game loop to get input from the user and update the position of the stickman
     * and all game components.
     *
     * @param deltaTime
     */
    public void update(float deltaTime) {

        getUserInput();

        world.update(deltaTime, inputDirection);

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


    /**
     * Detect where on the screen the user has pressed and if this corresponds to up/down/left/right movement.
     */
    private void getUserInput() {

        inputDirection.x = -1;
        inputDirection.y = -1;
        if (Gdx.input.isTouched()) {
            //guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));
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


            if (inputY < (this.controlStickNeutral.y - constrolStickNeutralRadius) && inputY > 0) {
                //Gdx.app.log(Stickman.LOG_STICKMAN, String.format("touched DOWN=(x=%d, y=%d) ", Gdx.input.getX(),inputY));
                inputDirection.y = DOWN;
            } else if (inputY > (this.controlStickNeutral.y + constrolStickNeutralRadius) && inputY < (this.controlStickNeutral.y + constrolStickRadius)) {
                //Gdx.app.log(Stickman.LOG_STICKMAN, String.format("touched UP=(x=%d, y=%d) ", Gdx.input.getX(), inputY));
                inputDirection.y = UP;
            }
        }
    }


    public void render() {
        //if (world.stickman.position.y > guiCam.position.y)
        //    guiCam.position.y = world.stickman.position.y;

        updateCam();

        batch.setProjectionMatrix(guiCam.combined);
        renderMap();
        renderInputControls();
        renderObjects();
        renderDebug();
    }

    private void renderDebug() {
        if (debugOutput) {

            debugBatch.begin();
            debugFont.draw(debugBatch, String.format("Stickman state=%s, pos(%.2f, %.2f), vel=(%.2f, %.2f) ", world.stickman.state.toString(), world.stickman.position.x, world.stickman.position.y, world.stickman.velocity.x, world.stickman.velocity.y), 1, displayHeight - 20);
            debugBatch.end();

        }
    }

    private void updateCam() {
        guiCam.position.x = world.stickman.position.x;
        guiCam.position.y = world.stickman.position.y;

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
        font.draw(batch, "Stickman go --->", 1, GAME_HEIGHT - 4); //displayHeight-20);
        batch.end();
    }

    private void renderInputControls() {
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.circle(this.controlStickNeutral.x, controlStickNeutral.y, constrolStickNeutralRadius);
        drawRightController();
        drawUpController();
        drawLeftController();
        drawDownController();
        shapeRenderer.end();

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

    private void renderMap() {
        mapRenderer.setView(guiCam);
        mapRenderer.render();
    }

    private void renderStickman() {
        TextureRegion keyFrame;



        /*switch (world.stickman.state) {

		case Bob.BOB_STATE_FALL:
			keyFrame = Assets.bobFall.getKeyFrame(world.stickman.stateTime, Animation.ANIMATION_LOOPING);
			break;
		case Bob.BOB_STATE_JUMP:
			keyFrame = Assets.bobJump.getKeyFrame(world.stickman.stateTime, Animation.ANIMATION_LOOPING);
			break;
		case Bob.BOB_STATE_HIT:
		default:
			keyFrame = Assets.bobHit;
		}*/


        boolean flip = world.stickman.velocity.x < 0;
        switch (world.stickman.state) {
            case RUNNING:

                keyFrame = Assets.runAnimation.getKeyFrame(world.stickman.stateTime, true);
                batch.draw(keyFrame, flip ? world.stickman.position.x + Stickman.STICKMAN_WIDTH : world.stickman.position.x, world.stickman.position.y, flip ? -Stickman.STICKMAN_WIDTH : Stickman.STICKMAN_WIDTH, Stickman.STICKMAN_HEIGHT);
                break;


            case IDLE:
            default:
                keyFrame = Assets.idleAnimation.getKeyFrame(world.stickman.stateTime, true);
                batch.draw(keyFrame, flip ? world.stickman.position.x + Stickman.STICKMAN_WIDTH : world.stickman.position.x, world.stickman.position.y, flip ? -Stickman.STICKMAN_WIDTH : Stickman.STICKMAN_WIDTH, Stickman.STICKMAN_HEIGHT);

        }

        //batch.draw(keyFrame, 500, 200);

        //batch.draw(keyFrame, 10, 10);
        //batch.draw(keyFrame, 0, 0);

    }
/*
    private void renderPlatforms () {
		int len = world.platforms.size();
		for (int i = 0; i < len; i++) {
			Platform platform = world.platforms.get(i);
			TextureRegion keyFrame = Assets.platform;
			if (platform.state == Platform.PLATFORM_STATE_PULVERIZING) {
				keyFrame = Assets.brakingPlatform.getKeyFrame(platform.stateTime, Animation.ANIMATION_NONLOOPING);
			}

			batch.draw(keyFrame, platform.position.x - 1, platform.position.y - 0.25f, 2, 0.5f);
		}
	}

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
