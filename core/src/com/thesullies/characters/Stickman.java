package com.thesullies.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.thesullies.Assets;
import com.thesullies.InputController;
import com.thesullies.maps.Constants;
import com.badlogic.gdx.physics.box2d.World;
import com.thesullies.maps.LevelMapManager;
import com.thesullies.maps.MapBodyBuilder;

/**
 * Created by kosullivan on 04/01/2017.
 */
public class Stickman extends DynamicGameObject {

    public static final int STICKMAN_SPRITE_WIDTH = 12;
    public static final int STICKMAN_SPRITE_HEIGHT = 12;

    public static final int STICKMAN_HITBOX_WIDTH = 2;
    public static final int STICKMAN_HITBOX_HEIGHT = 5;

    public static final int STICKMAN_SPAWN_X = 10;
    public static final int STICKMAN_SPAWN_Y = 100;


    private static final float MAX_LINEAR_VELOCITY_X = 3.5f;
    private static final float JUMP_IMPULSE = 2f;
    private static final int STICKMAN_LINEAR_DAMPING = 2;
    private static final float STICKMAN_GRAVITY_SCALE = 2f;
    // If velocity is lower than this, then force stop
    private static final float STICKMAN_LOW_VELOCITY_THRESHOLD = 1f;


    ShapeRenderer debugShapeRenderer = null;
    private int touchingPlatformCount = 0;
    /**
     * This is set to true in the handleCollision() method, if touching object with attribute door
     */
    private boolean touchingDoor = false;
    /**
     * This is set to true in the handleCollision() method, if touching object with attribute death
     */
    private boolean touchingDeath = false;
    /**
     * True if moving left - used to flip the bitmap image depending on direction of movement.
     */
    private boolean movingLeft;

    public enum STICKMAN_STATES {
        IDLE("IDLE"),
        FALLING("FALLING"),
        JUMPING("JUMPING");
        private final String name;

        private STICKMAN_STATES(String s) {
            name = s;
        }

        public boolean equalsName(String otherName) {
            return (otherName == null) ? false : name.equals(otherName);
        }

        public String toString() {
            return this.name;
        }
    }

    STICKMAN_STATES state;

    Vector2 gravity = new Vector2(0, -2);
    Rectangle stickmanHitBox;
    /**
     * Holds data on the current level the stickman is playing in
     */
    LevelMapManager levelMapManager;


    /**
     *
     * @param x - where to start the stickman in the world
     * @param y - where to start the stickman in the world
     * @param world - Box2D physics world
     * @param levelMapManager - copy of the levelMapManager
     */
    public Stickman(float x, float y, World world, LevelMapManager levelMapManager) {
        super(x, y, STICKMAN_SPRITE_WIDTH, STICKMAN_SPRITE_HEIGHT);
        state = STICKMAN_STATES.IDLE;
        stateTime = 0;
        stickmanHitBox = new Rectangle(3, 0, 6, STICKMAN_SPRITE_HEIGHT - 3);

        debugShapeRenderer = new ShapeRenderer();
        debugShapeRenderer.setAutoShapeType(true);
        this.levelMapManager = levelMapManager;

        createPhysicalBody(world);
    }

    private void createPhysicalBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(
                (STICKMAN_SPAWN_X + STICKMAN_SPRITE_WIDTH / 2) / Constants.PHYSICS_PIXELS_TO_METERS,
                (STICKMAN_SPAWN_Y + STICKMAN_SPRITE_HEIGHT / 2) / Constants.PHYSICS_PIXELS_TO_METERS);
        bodyDef.fixedRotation = true;

        this.physicsBody = world.createBody(bodyDef);
        this.physicsBody.setLinearDamping(STICKMAN_LINEAR_DAMPING);
        this.physicsBody.setGravityScale(STICKMAN_GRAVITY_SCALE);
        this.physicsBody.setUserData(this);

        PolygonShape bodyRectangleShape = new PolygonShape();
        bodyRectangleShape.setAsBox(STICKMAN_HITBOX_WIDTH / Constants.PHYSICS_PIXELS_TO_METERS, STICKMAN_HITBOX_HEIGHT / Constants.PHYSICS_PIXELS_TO_METERS);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = bodyRectangleShape;
        fixtureDef.density = 1f;
        this.physicsBody.createFixture(fixtureDef);
        bodyRectangleShape.dispose();

        CircleShape circleGroundDetectorShape = new CircleShape();
        Vector2 vec = new Vector2(0f, -0.5f);
        circleGroundDetectorShape.setPosition(vec);
        circleGroundDetectorShape.setRadius(0.15f);
        fixtureDef = new FixtureDef();
        fixtureDef.shape = circleGroundDetectorShape;
        fixtureDef.density = 1f;
        fixtureDef.isSensor = true;
        this.physicsBody.createFixture(fixtureDef);
        circleGroundDetectorShape.dispose();
    }

    public void render(SpriteBatch batch) {

        TextureRegion keyFrame;

        if (isMovingHorizontally()) {
            keyFrame = Assets.runAnimation.getKeyFrame(this.stateTime, true);
            batch.draw(keyFrame, movingLeft ? this.position.x + Stickman.STICKMAN_SPRITE_WIDTH : this.position.x, this.position.y, movingLeft ? -Stickman.STICKMAN_SPRITE_WIDTH : Stickman.STICKMAN_SPRITE_WIDTH, Stickman.STICKMAN_SPRITE_HEIGHT);
        } else {

            switch (this.state) {
                case IDLE:
                default:
                    keyFrame = Assets.idleAnimation.getKeyFrame(this.stateTime, true);
                    batch.draw(keyFrame, movingLeft ? this.position.x + Stickman.STICKMAN_SPRITE_WIDTH : this.position.x, this.position.y, movingLeft ? -Stickman.STICKMAN_SPRITE_WIDTH : Stickman.STICKMAN_SPRITE_WIDTH, Stickman.STICKMAN_SPRITE_HEIGHT);
            }
        }
    }


    public void update(float deltaTime, Vector2 inputDirection, boolean jump) {

        this.position.x = (physicsBody.getPosition().x * Constants.PHYSICS_PIXELS_TO_METERS) - 6;
        this.position.y = (physicsBody.getPosition().y * Constants.PHYSICS_PIXELS_TO_METERS) - 5.5f;
        stateTime += deltaTime;

        if (inputDirection.x == InputController.LEFT) {
            if (this.physicsBody.getLinearVelocity().x > -MAX_LINEAR_VELOCITY_X) {
                Vector2 vec = new Vector2(-1f, 0f);
                this.physicsBody.applyLinearImpulse(vec, this.physicsBody.getWorldCenter(), true);
            }
        } else if (inputDirection.x == InputController.RIGHT) {
            if (this.physicsBody.getLinearVelocity().x < MAX_LINEAR_VELOCITY_X) {
                Vector2 vec = new Vector2(1f, 0f);
                this.physicsBody.applyLinearImpulse(vec, this.physicsBody.getWorldCenter(), true);
            }
        } else {
/*
            if (isMovingHorizontally())
            {
                Gdx.app.debug(Constants.LOG_TAG, "STOPPING...");
                Vector2 vec = new Vector2(-this.physicsBody.getLinearVelocity().x, 0f);
                this.physicsBody.applyLinearImpulse(vec, this.physicsBody.getWorldCenter(), true);
            }*/

            if (!isFalling() && !isJumping()) {
                state = STICKMAN_STATES.IDLE;
            }
        }

        if (isMovingHorizontally()) {
            this.movingLeft = this.physicsBody.getLinearVelocity().x <0;
        }

        if (jump && isTouchingPlatform()) {
            Gdx.app.debug(Constants.LOG_TAG, "Jump!");
            Vector2 vec = new Vector2(0f, JUMP_IMPULSE);
            this.physicsBody.applyLinearImpulse(vec, this.physicsBody.getWorldCenter(), true);
        }

        if (this.physicsBody.getLinearVelocity().y < -0.01f)
            state = STICKMAN_STATES.FALLING;
        else if (this.physicsBody.getLinearVelocity().y > 0.01f)
            state = STICKMAN_STATES.JUMPING;
        else
            state = STICKMAN_STATES.IDLE;

        checkPositionLimits();
    }

    /**
     * the x value for linear velocity can be really really small, and still non zero. e.g. 0.00000001 - this means its not moving any noticible speed and we should update the visuals accordingly.
     * @return
     */
    private boolean isMovingHorizontally() {
        return this.physicsBody.getLinearVelocity().x!=0 &&
                (this.physicsBody.getLinearVelocity().x<-STICKMAN_LOW_VELOCITY_THRESHOLD ||
                this.physicsBody.getLinearVelocity().x>-STICKMAN_LOW_VELOCITY_THRESHOLD);
    }

    /**
     * Make sure the stickman can't go off the map.
     */
    private void checkPositionLimits() {
        if (this.position.x <= levelMapManager.levelBoundingRectStickman.getX())
            this.physicsBody.getPosition().x = levelMapManager.levelBoundingRectStickman.getX();
        if (this.position.x > levelMapManager.levelBoundingRectStickman.getWidth())
            this.physicsBody.getPosition().x = levelMapManager.levelBoundingRectStickman.getWidth();

        if (this.position.y + STICKMAN_HITBOX_HEIGHT / 2 <= levelMapManager.levelBoundingRectStickman.getY()) {
            this.physicsBody.setTransform(this.physicsBody.getPosition().x, levelMapManager.levelBoundingRectStickman.getY() + STICKMAN_HITBOX_HEIGHT / 2 / Constants.PHYSICS_PIXELS_TO_METERS, 0);
            stopFalling();
        }

        if (this.position.y > levelMapManager.levelBoundingRectStickman.getHeight())
            this.physicsBody.getPosition().y = levelMapManager.levelBoundingRectStickman.getHeight();
    }

    private boolean isTouchingPlatform() {
        return touchingPlatformCount>0;
    }


    private void stopFalling() {
        if (isJumping() || isFalling()) {
            state = STICKMAN_STATES.IDLE;
        }
    }

    private boolean isFalling() {
        return !isTouchingPlatform();
//        if (state.equals(STICKMAN_STATES.FALLING))
//            return true;
//        return false;
    }

    private boolean isJumping() {
        if (state.equals(STICKMAN_STATES.JUMPING))
            return true;
        return false;
    }



    /***
     * @param contact
     * @param stickmanFixture - the fixture for Stickman
     * @param otherFixture - the fixture that Stickman hit
     * @param contactStart - true for start of contact, false for end of contact
     * @return return true of the object Stickman collided with is to be removed from the map
     */
    public boolean handleCollision(Contact contact, Fixture stickmanFixture, Fixture otherFixture, boolean contactStart) {

        if (otherFixture.getBody().getUserData() != null) {
            if (otherFixture.getBody().getUserData() instanceof MapObject) {
                return handleCollisionMapObject(stickmanFixture, ((MapObject) otherFixture.getBody().getUserData()), contactStart);
            } else if (otherFixture.getBody().getUserData() instanceof Coin) {
                Gdx.app.debug(Constants.LOG_TAG, "Touching Coin: contactStart=" + contactStart);
                return true;
            }
        }
        return false;
    }

    private boolean handleCollisionMapObject(Fixture stickmanFixture, MapObject otherMapObject, boolean contactStart) {

        boolean deleteObject = false;
        if (MapBodyBuilder.isDoor(otherMapObject)) {
            Gdx.app.debug(Constants.LOG_TAG, "Touching Door: contactStart=" + contactStart);
            this.touchingDoor = true;
        } else if (MapBodyBuilder.isDeath(otherMapObject)) {
            Gdx.app.debug(Constants.LOG_TAG, "Touching Death: contactStart=" + contactStart);
            this.touchingDeath = true;
        } else {
            if (stickmanFixture.isSensor()) {
                if (contactStart)
                    touchingPlatformCount++;
                else
                    touchingPlatformCount--;

                Gdx.app.debug(Constants.LOG_TAG, "Touching Platform: contactStart=" + contactStart);
            } else {
                Gdx.app.debug(Constants.LOG_TAG, "Ignoring Touching Platform for non-sensor: contactStart=" + contactStart);
            }
        }
        return deleteObject;
    }


    @Override
    public String toString() {

        return String.format("State=%s, pos(%.2f, %.2f), physicsPos(%.2f, %.2f), linearVel(%.2f, %.2f), touchingPlatform=%s (%d) ",
                this.state.toString(),
                this.position.x, this.position.y,
                this.physicsBody.getPosition().x, this.physicsBody.getPosition().y,
                this.physicsBody.getLinearVelocity().x, this.physicsBody.getLinearVelocity().y,
                 this.isTouchingPlatform(), this.touchingPlatformCount);

    }

    public boolean isTouchingDoor() {
        return touchingDoor;
    }
    public boolean isTouchingDeath() {
        return touchingDeath;
    }

}
