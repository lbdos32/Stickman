package com.thesullies;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Logger;

import static com.badlogic.gdx.controllers.ControlType.button;

public class Stickman extends ApplicationAdapter {
    public static final String LOG_STICKMAN = "STICKMAN";
    SpriteBatch batch;
    Texture img;
    float elapsedTime;
    public Animation<TextureRegion> idleAnimation;
    public Animation<TextureRegion> attack_1_Animation;
    public Animation<TextureRegion> runAnimation;
    private final static float ANIMATION_SPEED = 1f / 15f;
    private static final int STICKMAN_WIDTH = 387;
    private static final int STICKMAN_HEIGHT = 387;
    private static final int STICKMAN_SPRITE_WIDTH = 744;
    private static final int STICKMAN_SPRITE_HEIGHT = 745;
    //OrthographicCamera camera;

    @Override
    public void create() {

        Gdx.app.setLogLevel(Application.LOG_DEBUG);
        Gdx.app.debug(LOG_STICKMAN, String.format("Create World"));
        batch = new SpriteBatch();
        //img = new Texture("badlogi.jpg");
        TextureRegion[] idleFrames = new TextureRegion[8];
        for (int i = 0; i < 8; i++) {
            idleFrames[i] = new TextureRegion(new Texture("charactersprites/idle_0" + i + ".png"));
        }
        idleAnimation = new Animation(ANIMATION_SPEED, idleFrames);

        TextureRegion[] attackFrames = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            attackFrames[i] = new TextureRegion(new Texture("charactersprites/attack_1_0" + i + ".png"));
        }
        attack_1_Animation = new Animation(ANIMATION_SPEED, attackFrames);

        TextureRegion[] runFrames = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            runFrames[i] = new TextureRegion(new Texture("charactersprites/run_0" + i + ".png"));
        }
        runAnimation = new Animation(ANIMATION_SPEED, runFrames);


        //camera = new OrthographicCamera();
        //camera.setToOrtho(false, 800, 480);


        Gdx.input.setInputProcessor(new InputAdapter() {
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
        });

        initStage();
        resetWorld();
    }
    Stage stage;
    TextButton button;
    TextButton.TextButtonStyle textButtonStyle;
    BitmapFont font;
    Skin skin;
    TextureAtlas buttonAtlas;
    private void initStage() {
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);
        font = new BitmapFont();
        skin = new Skin();
        buttonAtlas = new TextureAtlas(Gdx.files.internal("buttons/buttons.pack"));
        skin.addRegions(buttonAtlas);
        textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = font;
        textButtonStyle.up = skin.getDrawable("up-button");
        textButtonStyle.down = skin.getDrawable("down-button");
        textButtonStyle.checked = skin.getDrawable("checked-button");
        button = new TextButton("Button1", textButtonStyle);
        stage.addActor(button);
    }

    private void resetWorld() {
        //camera.position.x = 400;

    }

    @Override
    public void render() {
        super.render();
        stage.draw();
        /*
        Gdx.gl.glClearColor(1, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        updateWorld();
        drawWorld();*/
    }

    public void updateWorld() {
        elapsedTime += Gdx.graphics.getDeltaTime();
        float deltaTime = Gdx.graphics.getDeltaTime();
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



    static enum GameState {
        Start, Running, GameOver
    }
}
