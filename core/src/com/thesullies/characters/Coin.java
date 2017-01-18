package com.thesullies.characters;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.thesullies.Assets;
import com.thesullies.maps.Constants;

/**
 * Created by kosullivan on 16/01/2017.
 */

public class Coin extends DynamicGameObject {


    private static final float COIN_SPRITE_WIDTH = 5;
    private static final float COIN_SPRITE_HEIGHT = 5;

    public Coin(float x, float y, World world) {
        super(x+COIN_SPRITE_WIDTH/2, y+COIN_SPRITE_HEIGHT/2, COIN_SPRITE_WIDTH, COIN_SPRITE_HEIGHT);
        //state = Stickman.STICKMAN_STATES.IDLE;
        stateTime = 0;

        createPhysicalBody(x+COIN_SPRITE_WIDTH/2,y+COIN_SPRITE_HEIGHT/2,world);
    }

    private void createPhysicalBody(float x, float y,World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.KinematicBody;
        bodyDef.position.set(
                x / Constants.PHYSICS_PIXELS_TO_METERS,
                y / Constants.PHYSICS_PIXELS_TO_METERS);
        bodyDef.fixedRotation = true;

        this.physicsBody = world.createBody(bodyDef);
        this.physicsBody.setLinearDamping(1);
        this.physicsBody.setUserData(this);


        CircleShape circleGroundDetectorShape = new CircleShape();
        Vector2 vec = new Vector2(0f, 0f);
        circleGroundDetectorShape.setPosition(vec);
        circleGroundDetectorShape.setRadius(0.2f);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = circleGroundDetectorShape;
        fixtureDef.density = 1f;
        fixtureDef.isSensor = true;
        this.physicsBody.createFixture(fixtureDef);
    }

    public void update(float deltaTime) {
        stateTime += deltaTime;
        this.position.x = (physicsBody.getPosition().x * Constants.PHYSICS_PIXELS_TO_METERS);
        this.position.y = (physicsBody.getPosition().y * Constants.PHYSICS_PIXELS_TO_METERS);

    }

    public void render(SpriteBatch batch) {
        TextureRegion keyFrame;
        keyFrame = Assets.coinAnimation.getKeyFrame(this.stateTime, true);
        batch.draw(keyFrame,  this.position.x-COIN_SPRITE_WIDTH/2, this.position.y-COIN_SPRITE_HEIGHT/2,  COIN_SPRITE_WIDTH,  COIN_SPRITE_HEIGHT);
    }


}
