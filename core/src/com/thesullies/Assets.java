package com.thesullies;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

/**
 * Created by kosullivan on 04/01/2017.
 */

public class Assets {
    private final static float ANIMATION_SPEED = 1f / 15f;
    public static Animation<TextureRegion> idleAnimation;
    public static Animation<TextureRegion> attack_1_Animation;
    public static Animation<TextureRegion> runAnimation;
    public static Animation<TextureRegion> coinAnimation;
    public static TiledMap map = null;
    public static float unitScale = 1 / 8f;

    public static void load() {
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

        loadCoinAnimation();

        loadMap();
    }

    private static void loadCoinAnimation() {
        TextureRegion[] coinFrames = new TextureRegion[4];
        for (int i = 0; i < 4; i++) {
            String fileName = "elements/coin/coin_0" + i + ".png";
            coinFrames[i] = new TextureRegion(new Texture(fileName));
        }
        coinAnimation = new Animation(ANIMATION_SPEED, coinFrames);
    }

    private static void loadMap() {
        map = new TmxMapLoader().load("levels/level_00.tmx");
    }
}
