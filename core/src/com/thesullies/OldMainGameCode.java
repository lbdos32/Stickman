package com.thesullies;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Logger;

import static com.badlogic.gdx.controllers.ControlType.button;

public class OldMainGameCode extends ApplicationAdapter {
    public static final String LOG_STICKMAN = "STICKMAN";
    SpriteBatch batch;
    Texture img;
    float elapsedTime;
    public Animation<TextureRegion> idleAnimation;
    public Animation<TextureRegion> attack_1_Animation;
    public Animation<TextureRegion> runAnimation;
    private final static float ANIMATION_SPEED = 1f / 15f;
    private static final int STICKMAN_WIDTH = 4;
    private static final int STICKMAN_HEIGHT = 4;
    private static final int STICKMAN_SPRITE_WIDTH = 744;
    private static final int STICKMAN_SPRITE_HEIGHT = 745;
    OrthogonalTiledMapRenderer renderer = null;
    OrthographicCamera camera = null;
    TiledMap map = null;
    CameraController controller;
    GestureDetector gestureDetector;

    StickmanCharacter stickman;

    private boolean debug = true;
    private ShapeRenderer debugRenderer;

    BitmapFont font = null;

    @Override
    public void create() {

        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Gdx.app.debug(LOG_STICKMAN, String.format("Create World"));

        font = new BitmapFont();
        font.setColor(Color.RED);

        batch = new SpriteBatch();
        //img = new Texture("badlogi.jpg");
        TextureRegion[] idleFrames = new TextureRegion[8];
        for (int i = 0; i < 8; i++) {
            String fileName = "charactersprites/idle_0" + i + ".png";
            idleFrames[i] = new TextureRegion(new Texture(fileName));
        }
        idleAnimation = new Animation(ANIMATION_SPEED, idleFrames);

        TextureRegion[] attackFrames = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            String fileName = "charactersprites/attack_1_0" + i + ".png";
            attackFrames[i] = new TextureRegion(new Texture(fileName));
        }
        attack_1_Animation = new Animation(ANIMATION_SPEED, attackFrames);

        TextureRegion[] runFrames = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            String fileName = "charactersprites/run_0" + i + ".png";
            runFrames[i] = new TextureRegion(new Texture(fileName));
        }
        runAnimation = new Animation(ANIMATION_SPEED, runFrames);



        camera = new OrthographicCamera();
        camera.setToOrtho(false, 30, 20);
        camera.position.x=15;
        camera.position.y=10;
        camera.update();

        loadMap();

        stickman = new StickmanCharacter();
        stickman.position.set(0, 0);
        StickmanCharacter.WIDTH = STICKMAN_WIDTH; //1 / 16f * regions[0].getRegionWidth();
        StickmanCharacter.HEIGHT = STICKMAN_HEIGHT; //1 / 16f * regions[0].getRegionHeight();
        //koalaTexture = new Texture("data/maps/tiled/super-koalio/koalio.png");
        //TextureRegion[] regions = TextureRegion.split(koalaTexture, 18, 26)[0];
        //stand = new Animation(0, regions[0]);
        //jump = new Animation(0, regions[1]);
        //walk = new Animation(0.15f, regions[2], regions[3], regions[4]);
        //walk.setPlayMode(Animation.PlayMode.LOOP_PINGPONG);


        controller = new CameraController();
        gestureDetector = new GestureDetector(20, 0.5f, 2, 0.15f, controller);
        Gdx.input.setInputProcessor(gestureDetector);

        /*Gdx.input.setInputProcessor(new InputAdapter() {
            @Override
            public boolean touchDown (int x, int y, int pointer, int button) {
                // your touch down code here
                Gdx.app.debug(LOG_STICKMAN, String.format("touchDown x=%d, y=%d, pointer=%d, button=%d",x,y,pointer,button));
                return true; // return true to indicate the event was handled
            }

            @Override
            public boolean touchUp (int x, int y, int pointer, int button) {
                // your touch up code here
                Gdx.app.debug(LOG_STICKMAN, String.format("touchUp x=%d, y=%d, pointer=%d, button=%d",x,y,pointer,button));
                return true; // return true to indicate the event was handled
            }
        });*/


        debugRenderer = new ShapeRenderer();

        resetWorld();
    }

    private void loadMap() {
        float unitScale = 1 / 16f;
        map = new TmxMapLoader().load("levels/level_02.tmx");

        renderer = new OrthogonalTiledMapRenderer(map, unitScale);
    }


    private void resetWorld() {
        //camera.position.x = 400;

    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

// get the delta time
        float deltaTime = Gdx.graphics.getDeltaTime();
        updateStickman(deltaTime);


        updateWorld();

        controller.update();
        //camera.position.x = stickman.position.x;
        camera.update();
        batch.setProjectionMatrix(camera.combined);
        renderer.setView(camera);
        renderer.render();

        renderStickman(deltaTime);
        //drawWorld();

        if (debug) renderDebug();
        Gdx.app.log("GestureDetectorTest", String.format("camera.zoom = %f, camera.postition=(x=%f, y=%f z=%f) " , camera.zoom, camera.position.x, camera.position.y, camera.position.z));

    }

    public void updateWorld() {
        elapsedTime += Gdx.graphics.getDeltaTime();
        float deltaTime = Gdx.graphics.getDeltaTime();
    }

    private void updateStickman (float deltaTime) {
        if (deltaTime == 0) return;

        if (deltaTime > 0.1f)
            deltaTime = 0.1f;

        stickman.stateTime += deltaTime;
    }
    private void renderStickman (float deltaTime) {
        // based on the koala state, get the animation frame
        TextureRegion frame = null;
        switch (stickman.state) {
//            case Standing:
//                frame = stand.getKeyFrame(koala.stateTime);
//                break;
            case Walking:
                frame = idleAnimation.getKeyFrame(stickman.stateTime, true);
                break;
//            case Jumping:
//                frame = jump.getKeyFrame(koala.stateTime);
//                break;
        }

        // draw the koala, depending on the current velocity
        // on the x-axis, draw the koala facing either right
        // or left
        //Batch batch = renderer.getBatch();
        batch.begin();
        if (stickman.facesRight) {
            batch.draw(frame, stickman.position.x, stickman.position.y, stickman.WIDTH, stickman.HEIGHT);
        } else {
            batch.draw(frame, stickman.position.x + stickman.WIDTH, stickman.position.y, -stickman.WIDTH, stickman.HEIGHT);
        }
        batch.end();
    }

    public void drawWorld() {


        //camera.update();
        //batch.setProjectionMatrix(camera.combined);

        batch.begin();
        //batch.draw(img, 200, 10);
        batch.draw(idleAnimation.getKeyFrame(elapsedTime, true), 0, 0, STICKMAN_WIDTH, STICKMAN_HEIGHT);

        boolean flip = false;
        batch.draw(attack_1_Animation.getKeyFrame(elapsedTime, true), flip ? 300f + STICKMAN_WIDTH : 300f, 0f, flip ? -STICKMAN_WIDTH : STICKMAN_WIDTH, STICKMAN_HEIGHT);
        flip = true;
        batch.draw(attack_1_Animation.getKeyFrame(elapsedTime, true), flip ? 600f + STICKMAN_WIDTH : 600f, 0f, flip ? -STICKMAN_WIDTH : STICKMAN_WIDTH, STICKMAN_HEIGHT);


        flip = false;
        batch.draw(runAnimation.getKeyFrame(elapsedTime, true), flip ? 900f + STICKMAN_WIDTH : 900f, 0f, flip ? -STICKMAN_WIDTH : STICKMAN_WIDTH, STICKMAN_HEIGHT);
        flip = true;
        batch.draw(runAnimation.getKeyFrame(elapsedTime, true), flip ? 1200f + STICKMAN_WIDTH : 1200f, 0f, flip ? -STICKMAN_WIDTH : STICKMAN_WIDTH, STICKMAN_HEIGHT);

        //batch.draw(runAnimation.getKeyFrame(elapsedTime,true),900,0,STICKMAN_WIDTH,STICKMAN_HEIGHT);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        img.dispose();
    }

    private void renderDebug () {
        debugRenderer.setProjectionMatrix(camera.combined);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);

        debugRenderer.setColor(Color.RED);
        //debugRenderer.rect(koala.position.x, koala.position.y, Koala.WIDTH, Koala.HEIGHT);

        debugRenderer.setColor(Color.YELLOW);
        TiledMapTileLayer layer = (TiledMapTileLayer)map.getLayers().get(0);
        for (int y = 0; y <= layer.getHeight(); y++) {
            for (int x = 0; x <= layer.getWidth(); x++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null) {
                    if (camera.frustum.boundsInFrustum(x + 0.5f, y + 0.5f, 0, 1, 1, 0))
                        debugRenderer.rect(x, y, 1, 1);
                }
            }
        }
        debugRenderer.end();


        batch.begin();
        font.draw(batch, "Hello World!", 10, 10);
        batch.end();


    }

    static enum GameState {
        Start, Running, GameOver
    }


    class CameraController implements GestureDetector.GestureListener {
        float velX, velY;
        boolean flinging = false;
        float initialScale = 1;

        public boolean touchDown (float x, float y, int pointer, int button) {
            flinging = false;
            initialScale = camera.zoom;
            Gdx.app.log("GestureDetectorTest", "camera.zoom =  " + camera.zoom);
            return false;
        }

        @Override
        public boolean tap (float x, float y, int count, int button) {
            Gdx.app.log("GestureDetectorTest", "tap at " + x + ", " + y + ", count: " + count);
            return false;
        }

        @Override
        public boolean longPress (float x, float y) {
            Gdx.app.log("GestureDetectorTest", "long press at " + x + ", " + y);
            return false;
        }

        @Override
        public boolean fling (float velocityX, float velocityY, int button) {
            Gdx.app.log("GestureDetectorTest", "fling " + velocityX + ", " + velocityY);
            flinging = true;
            velX = camera.zoom * velocityX * 0.01f;
            velY = camera.zoom * velocityY * 0.01f;
            return false;
        }

        @Override
        public boolean pan (float x, float y, float deltaX, float deltaY) {
            // Gdx.app.log("GestureDetectorTest", "pan at " + x + ", " + y);
            camera.position.add(-deltaX * camera.zoom/50, deltaY * camera.zoom/50, 0);
            return false;
        }

        @Override
        public boolean panStop (float x, float y, int pointer, int button) {
            Gdx.app.log("GestureDetectorTest", "pan stop at " + x + ", " + y);
            return false;
        }

        @Override
        public boolean zoom (float originalDistance, float currentDistance) {
            float ratio = originalDistance / currentDistance;
            camera.zoom = initialScale * ratio;
            System.out.println(camera.zoom);
            return false;
        }

        @Override
        public boolean pinch (Vector2 initialFirstPointer, Vector2 initialSecondPointer, Vector2 firstPointer, Vector2 secondPointer) {
            return false;
        }

        public void update () {
            if (flinging) {
                velX *= 0.98f;
                velY *= 0.98f;
                camera.position.add(-velX * Gdx.graphics.getDeltaTime(), velY * Gdx.graphics.getDeltaTime(), 0);
                if (Math.abs(velX) < 0.01f) velX = 0;
                if (Math.abs(velY) < 0.01f) velY = 0;
            }
        }

        @Override
        public void pinchStop () {
        }
    }


    /** The player character, has state and state time, */
    static class StickmanCharacter {
        static float WIDTH;
        static float HEIGHT;
        static float MAX_VELOCITY = 10f;
        static float JUMP_VELOCITY = 40f;
        static float DAMPING = 0.87f;

        enum State {
            Standing, Walking, Jumping
        }

        final Vector2 position = new Vector2();
        final Vector2 velocity = new Vector2();
        State state = State.Walking;
        float stateTime = 0;
        boolean facesRight = true;
        boolean grounded = false;
    }

}
