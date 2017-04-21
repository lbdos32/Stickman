package com.thesullies.characters;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by kosullivan on 16/01/2017.
 */

public class FrogSpawn extends ParticleEffectObject {


    private static final float COIN_SPRITE_WIDTH = 5;
    private static final float COIN_SPRITE_HEIGHT = 5;

    public FrogSpawn(float x, float y, World world) {
        super(x + COIN_SPRITE_WIDTH / 2,
                y + COIN_SPRITE_HEIGHT / 2,
                COIN_SPRITE_WIDTH,
                COIN_SPRITE_HEIGHT,
                "frogSpawn.pe");
        stateTime = 0;
    }

    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
    }

    @Override
    public void render(SpriteBatch batch) {
        super.render(batch);
    }


}
