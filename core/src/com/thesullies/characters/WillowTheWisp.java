package com.thesullies.characters;

import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.thesullies.Assets;
import com.thesullies.maps.Constants;

/**
 * Created by kosullivan on 16/01/2017.
 */

public class WillowTheWisp extends ParticleEffectObject {


    private static final float COIN_SPRITE_WIDTH = 5;
    private static final float COIN_SPRITE_HEIGHT = 5;

    public WillowTheWisp(float x, float y, World world) {
        super(x + COIN_SPRITE_WIDTH / 2, y + COIN_SPRITE_HEIGHT / 2, COIN_SPRITE_WIDTH, COIN_SPRITE_HEIGHT, "willowTheWisp.pe");
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
