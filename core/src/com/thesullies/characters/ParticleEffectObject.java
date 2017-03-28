package com.thesullies.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * Created by kosullivan on 28/03/2017.
 */

class ParticleEffectObject extends DynamicGameObject {

    ParticleEffect effect;

    public ParticleEffectObject(float x, float y, float width, float height, String effectFileName) {
        super(x, y, width, height);
        loadParticleEffect(effectFileName);
    }

    public void loadParticleEffect(String effectFileName) {
        effect = new ParticleEffect();
        effect.load(Gdx.files.internal("particleEffects/" + effectFileName), Gdx.files.internal("particleEffects"));
        effect.setPosition(this.position.x, this.position.y);
        effect.start();
    }

    @Override
    public void update(float deltaTime) {
        this.effect.update(deltaTime);
    }

    @Override
    public void render(SpriteBatch batch) {
        this.effect.draw(batch);
    }
}
