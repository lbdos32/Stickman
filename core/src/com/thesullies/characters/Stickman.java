package com.thesullies.characters;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.thesullies.Assets;
import com.thesullies.maps.Constants;
import com.badlogic.gdx.physics.box2d.World;
import com.thesullies.maps.MapBodyBuilder;

import static com.badlogic.gdx.Input.Keys.DOWN;
import static com.badlogic.gdx.Input.Keys.LEFT;
import static com.badlogic.gdx.Input.Keys.RIGHT;

/**
 * Created by kosullivan on 04/01/2017.
 */
public class Stickman extends DynamicGameObject {

    public static final int STICKMAN_SPRITE_WIDTH = 12;
    public static final int STICKMAN_SPRITE_HEIGHT = 12;

    public static final int STICKMAN_HITBOX_WIDTH = 2;
    public static final int STICKMAN_HITBOX_HEIGHT = 5;

    private static final int STICKMAN_HORIZONTAL_SPEED = 40;
    private static final int STICKMAN_VERTICAL_JUMP = 60;
    private static final int MAX_LINEAR_VELOCITY_X = 5;
    private static final float JUMP_IMPULSE = 2f;
    private static final int STICKMAN_LINEAR_DAMPING = 2;


    ShapeRenderer debugShapeRenderer = null;
    private boolean touchingPlatform = false;


    public enum STICKMAN_STATES {
        IDLE("IDLE"),
        RUNNING("RUNNING"),
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


    public Stickman(float x, float y, World world) {
        super(x, y, STICKMAN_SPRITE_WIDTH, STICKMAN_SPRITE_HEIGHT);
        state = STICKMAN_STATES.IDLE;
        stateTime = 0;
        stickmanHitBox = new Rectangle(3, 0, 6, STICKMAN_SPRITE_HEIGHT - 3);

        debugShapeRenderer = new ShapeRenderer();
        debugShapeRenderer.setAutoShapeType(true);

        createPhysicalBody(world);
    }

    private void createPhysicalBody(World world) {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set(
                (50 + STICKMAN_SPRITE_WIDTH / 2) / Constants.PHYSICS_PIXELS_TO_METERS,
                (20 + STICKMAN_SPRITE_HEIGHT / 2) / Constants.PHYSICS_PIXELS_TO_METERS);
        bodyDef.fixedRotation = true;

        this.physicsBody = world.createBody(bodyDef);
        this.physicsBody.setLinearDamping(STICKMAN_LINEAR_DAMPING);
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

        boolean flip = this.physicsBody.getLinearVelocity().x < 0;
        switch (this.state) {
            case RUNNING:

                keyFrame = Assets.runAnimation.getKeyFrame(this.stateTime, true);
                batch.draw(keyFrame, flip ? this.position.x + Stickman.STICKMAN_SPRITE_WIDTH : this.position.x, this.position.y, flip ? -Stickman.STICKMAN_SPRITE_WIDTH : Stickman.STICKMAN_SPRITE_WIDTH, Stickman.STICKMAN_SPRITE_HEIGHT);
                break;


            case IDLE:
            default:
                keyFrame = Assets.idleAnimation.getKeyFrame(this.stateTime, true);
                batch.draw(keyFrame, flip ? this.position.x + Stickman.STICKMAN_SPRITE_WIDTH : this.position.x, this.position.y, flip ? -Stickman.STICKMAN_SPRITE_WIDTH : Stickman.STICKMAN_SPRITE_WIDTH, Stickman.STICKMAN_SPRITE_HEIGHT);
        }
    }


    public void update(float deltaTime, Vector2 inputDirection, boolean jump) {

        this.position.x = (physicsBody.getPosition().x * Constants.PHYSICS_PIXELS_TO_METERS) - 6;
        this.position.y = (physicsBody.getPosition().y * Constants.PHYSICS_PIXELS_TO_METERS) - 5.5f;
        stateTime += deltaTime;

        if (inputDirection.x == LEFT) {
            if (this.physicsBody.getLinearVelocity().x > -MAX_LINEAR_VELOCITY_X) {
                Vector2 vec = new Vector2(-1f, 0f);
                this.physicsBody.applyLinearImpulse(vec, this.physicsBody.getWorldCenter(), true); //-1f,0f,this.position.x+STICKMAN_SPRITE_WIDTH/2, this.position.y+STICKMAN_SPRITE_HEIGHT/2, true); //setLinearVelocity(100f,0f);
                //velocity.x = -STICKMAN_HORIZONTAL_SPEED;
            }
            if (!isFalling()) {
                state = STICKMAN_STATES.RUNNING;
            }
        } else if (inputDirection.x == RIGHT) {
            //this.physicsBody.applyLinearImpulse(+1f,0f,this.position.x+STICKMAN_SPRITE_WIDTH/2, this.position.y+STICKMAN_SPRITE_HEIGHT/2, true); //setLinearVelocity(100f,0f);
            if (this.physicsBody.getLinearVelocity().x < MAX_LINEAR_VELOCITY_X) {
                Vector2 vec = new Vector2(1f, 0f);
                this.physicsBody.applyLinearImpulse(vec, this.physicsBody.getWorldCenter(), true); //-1f,0f,this.position.x+STICKMAN_SPRITE_WIDTH/2, this.position.y+STICKMAN_SPRITE_HEIGHT/2, true); //setLinearVelocity(100f,0f);
            }

            //velocity.x = STICKMAN_HORIZONTAL_SPEED;
            if (!isFalling()) {
                state = STICKMAN_STATES.RUNNING;
            }
        } else {
            if (!isFalling() && !isJumping()) {
                state = STICKMAN_STATES.IDLE;
            }
//            velocity.x = 0;
        }

        if (jump && isTouchingPlatform()) {
            Gdx.app.debug(Constants.LOG_TAG, "Jump!");
            Vector2 vec = new Vector2(0f, JUMP_IMPULSE);
            this.physicsBody.applyLinearImpulse(vec, this.physicsBody.getWorldCenter(), true); //-1f,0f,this.position.x+STICKMAN_SPRITE_WIDTH/2, this.position.y+STICKMAN_SPRITE_HEIGHT/2, true); //setLinearVelocity(100f,0f);
            //this.physicsBody.applyLinearImpulse(0f,-10f,this.position.x+STICKMAN_SPRITE_WIDTH/2, this.position.y+STICKMAN_SPRITE_HEIGHT/2, true); //setLinearVelocity(100f,0f);
        }

        if (this.physicsBody.getLinearVelocity().y < 0)
            state = STICKMAN_STATES.FALLING;
        else if (this.physicsBody.getLinearVelocity().y > 0)
            state = STICKMAN_STATES.JUMPING;

        checkPositionLimits();
    }

    /**
     * Make sure the stickman can't go off the map.
     */
    private void checkPositionLimits() {
        if (this.position.x <= WorldRenderer.boundingRectStickman.getX())
            this.physicsBody.getPosition().x = WorldRenderer.boundingRectStickman.getX();
        if (this.position.x > WorldRenderer.boundingRectStickman.getWidth())
            this.physicsBody.getPosition().x = WorldRenderer.boundingRectStickman.getWidth();

        if (this.position.y + STICKMAN_HITBOX_HEIGHT / 2 <= WorldRenderer.boundingRectStickman.getY()) {
            this.physicsBody.setTransform(this.physicsBody.getPosition().x, WorldRenderer.boundingRectStickman.getY() + STICKMAN_HITBOX_HEIGHT / 2 / Constants.PHYSICS_PIXELS_TO_METERS, 0);
//            /this.position.y = WorldRenderer.boundingRectStickman.getY()-STICKMAN_HITBOX_HEIGHT/2;
            stopFalling();
        }

        if (this.position.y > WorldRenderer.boundingRectStickman.getHeight())
            this.physicsBody.getPosition().y = WorldRenderer.boundingRectStickman.getHeight();
    }

    private boolean isTouchingPlatform() {
        return touchingPlatform;
    }


    private void stopFalling() {
        this.velocity.y = 0;

        if (isJumping() || isFalling()) {
            state = STICKMAN_STATES.IDLE;
        }
    }

    private boolean isFalling() {
        if (state.equals(STICKMAN_STATES.FALLING))
            return true;
        return false;

    }

    private boolean isJumping() {
        if (state.equals(STICKMAN_STATES.JUMPING))
            return true;
        return false;
    }

    /**
     * Is the character allowed to jump in current state.
     *
     * @return
     */
    private boolean isJumpAllowed() {
        if (state.equals(STICKMAN_STATES.IDLE) || state.equals(STICKMAN_STATES.RUNNING))
            return true;

        return false;
    }


    private Array<TiledMapTileLayer.Cell> getTiles(Vector2 position, Rectangle stickmanHitBox) {

        Array<TiledMapTileLayer.Cell> cells = null;

        TiledMapTileLayer layer = (TiledMapTileLayer) Assets.map.getLayers().get(Constants.MAP_LAYER_PLATFORM);

        int startX = (int) ((position.x + stickmanHitBox.x) / Assets.unitScale / WorldRenderer.mapCellSize);
        int startY = (int) ((position.y + stickmanHitBox.y) / Assets.unitScale / WorldRenderer.mapCellSize);
        int endX = (int) ((position.x + stickmanHitBox.width) / Assets.unitScale / WorldRenderer.mapCellSize);
        int endY = (int) ((position.x + stickmanHitBox.height) / Assets.unitScale / WorldRenderer.mapCellSize);


        for (int y = startY; y <= endY; y++) {
            for (int x = startX; x <= endX; x++) {
                TiledMapTileLayer.Cell cell = layer.getCell(x, y);
                if (cell != null) {
                    if (cells == null) {
                        cells = new Array<TiledMapTileLayer.Cell>();
                    }
                    cells.add(cell);
                }
            }
        }
        return cells;
    }

    /***
     * @param contact
     * @param otherFixture
     */
    public boolean handleCollision(Contact contact, Fixture stickmanFixture, Fixture otherFixture, boolean contactStart) {

        //if (stickmanFixture.getShape()
        if (otherFixture.getBody().getUserData() != null) {
            if (otherFixture.getBody().getUserData() instanceof MapObject) {
                return handleCollisionMapObject(stickmanFixture, ((MapObject) otherFixture.getBody().getUserData()), contactStart);
            }
            else if (otherFixture.getBody().getUserData() instanceof Coin) {
                Gdx.app.debug(Constants.LOG_TAG, "Touching Coin: contactStart=" + contactStart);
                return true;
            }
        }
        //if (otherFixture.getBody().getUserData()!=null) {
        //    if (otherFixture.getBody().getUserData() instanceof ) {
//
        //          }
        //    }
        return false;
    }

    private boolean handleCollisionMapObject(Fixture stickmanFixture, MapObject userData, boolean contactStart) {

        boolean deleteObject = false;
        if (MapBodyBuilder.isDoor(userData)) {
            Gdx.app.debug(Constants.LOG_TAG, "Touching Door: contactStart=" + contactStart);

            // Time to exit level ?!
        } else if (MapBodyBuilder.isCoin(userData)) {
            Gdx.app.debug(Constants.LOG_TAG, "Touching Coin: contactStart=" + contactStart);
            deleteObject = true;

        } else {
            //if (stickmanFixture.isSensor()) {
                this.touchingPlatform = contactStart;
                Gdx.app.debug(Constants.LOG_TAG, "Touching Platform: contactStart=" + contactStart);
            //} else {
           //     Gdx.app.debug(Constants.LOG_TAG, "Ignoring Touching Platform for non-sensor: contactStart=" + contactStart);
            //}

        }

        return deleteObject;
    }


    @Override
    public String toString() {

        return String.format("Stickman state=%s, pos(%.2f, %.2f), physicsPos(%.2f, %.2f), linearVel(%.2f, %.2f), vel(%.2f, %.2f), touchingPlatform=%s ",
                this.state.toString(),
                this.position.x, this.position.y,
                this.physicsBody.getPosition().x, this.physicsBody.getPosition().y,
                this.physicsBody.getLinearVelocity().x, this.physicsBody.getLinearVelocity().y,
                this.velocity.x, this.velocity.y, this.touchingPlatform);

    }

}
