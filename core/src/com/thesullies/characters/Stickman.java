package com.thesullies.characters;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.Array;
import com.thesullies.Assets;
import com.thesullies.maps.Constants;
import com.badlogic.gdx.physics.box2d.World;

import static com.badlogic.gdx.Input.Keys.UP;
import static com.badlogic.gdx.Input.Keys.DOWN;
import static com.badlogic.gdx.Input.Keys.LEFT;
import static com.badlogic.gdx.Input.Keys.RIGHT;

/**
 * Created by kosullivan on 04/01/2017.
 */
public class Stickman extends DynamicGameObject {

    public static final int STICKMAN_WIDTH = 12;
    public static final int STICKMAN_HEIGHT = 12;
    private static final int STICKMAN_HORIZONTAL_SPEED = 30;
    private static final int STICKMAN_VERTICAL_JUMP = 30;
    private final Body physicsBody;

    ShapeRenderer debugShapeRenderer = null;


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

    Vector2 gravity = new Vector2(0, -1);
    Rectangle stickmanHitBox;


    float stateTime;

    public Stickman(float x, float y, World world) {
        super(x, y, STICKMAN_WIDTH, STICKMAN_HEIGHT);
        state = STICKMAN_STATES.IDLE;
        stateTime = 0;
        stickmanHitBox = new Rectangle(3, 0, 6, STICKMAN_HEIGHT - 3);
        debugShapeRenderer = new ShapeRenderer();
        debugShapeRenderer.setAutoShapeType(true);

        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        bodyDef.position.set((this.position.x + STICKMAN_WIDTH / 2) /
                        Constants.PHYSICS_PIXELS_TO_METERS,
                (this.position.y + STICKMAN_HEIGHT / 2) / Constants.PHYSICS_PIXELS_TO_METERS);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(STICKMAN_WIDTH / 2 / Constants.PHYSICS_PIXELS_TO_METERS, STICKMAN_HEIGHT
                / 2 / Constants.PHYSICS_PIXELS_TO_METERS);
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.1f;
        this.physicsBody = world.createBody(bodyDef);
        //this.physicsBody.setLinearDamping(1);
        this.physicsBody.applyLinearImpulse(100f,0f,this.position.x+STICKMAN_WIDTH/2, this.position.y+STICKMAN_HEIGHT/2, true); //setLinearVelocity(100f,0f);
        shape.dispose();
    }


    public void render(SpriteBatch batch) {
        TextureRegion keyFrame;

        boolean flip = this.velocity.x < 0;
        switch (this.state) {
            case RUNNING:

                keyFrame = Assets.runAnimation.getKeyFrame(this.stateTime, true);
                batch.draw(keyFrame, flip ? this.position.x + Stickman.STICKMAN_WIDTH : this.position.x, this.position.y, flip ? -Stickman.STICKMAN_WIDTH : Stickman.STICKMAN_WIDTH, Stickman.STICKMAN_HEIGHT);
                break;


            case IDLE:
            default:
                keyFrame = Assets.idleAnimation.getKeyFrame(this.stateTime, true);
                batch.draw(keyFrame, flip ? this.position.x + Stickman.STICKMAN_WIDTH : this.position.x, this.position.y, flip ? -Stickman.STICKMAN_WIDTH : Stickman.STICKMAN_WIDTH, Stickman.STICKMAN_HEIGHT);
        }

        if (WorldRenderer.debugOutput) {
            batch.end();

            debugShapeRenderer.setProjectionMatrix(WorldRenderer.guiCam.combined);
            debugShapeRenderer.begin();
            debugShapeRenderer.rect(position.x + stickmanHitBox.getX(), position.y + stickmanHitBox.y, stickmanHitBox.getWidth(), stickmanHitBox.getHeight());
            debugShapeRenderer.end();
            batch.begin();
        }
    }


    public void updateBOX2D(float deltaTime, Vector2 inputDirection) {

        this.position.x = physicsBody.getPosition().x * Constants.PHYSICS_PIXELS_TO_METERS;
        this.position.y = physicsBody.getPosition().y * Constants.PHYSICS_PIXELS_TO_METERS;

        checkPositionLimits();
    }
    public void update(float deltaTime, Vector2 inputDirection) {
        //velocity.add(World.gravity.x * deltaTime, World.gravity.y * deltaTime);
        //position.add(velocity.x * deltaTime, velocity.y * deltaTime);


        if (inputDirection.x == LEFT) {
            velocity.x = -STICKMAN_HORIZONTAL_SPEED;
            if (!isFalling()) {
                state = STICKMAN_STATES.RUNNING;
            }
        } else if (inputDirection.x == RIGHT) {
            velocity.x = STICKMAN_HORIZONTAL_SPEED;
            if (!isFalling()) {
                state = STICKMAN_STATES.RUNNING;
            }
        } else {
            if (!isFalling() && !isJumping()) {
                state = STICKMAN_STATES.IDLE;
            }
            velocity.x = 0;
        }

        boolean isTouchingPlatform = isTouchingPlatform();

        if (inputDirection.y == UP && (isJumpAllowed())) {
            velocity.y = STICKMAN_VERTICAL_JUMP;
            state = STICKMAN_STATES.JUMPING;
        } else if (inputDirection.y == DOWN) {
            velocity.y = -STICKMAN_VERTICAL_JUMP;
        } else {
            if (!isTouchingPlatform)
                this.velocity.add(gravity);
        }
        if (this.velocity.y < 0)
            state = STICKMAN_STATES.FALLING;

        if (isTouchingPlatform && isFalling()) {
            stopFalling();
        }
        this.position.add(this.velocity.x * deltaTime, this.velocity.y * deltaTime);

        checkPositionLimits();


        stateTime += deltaTime;
    }

    /**
     * Make sure the stickman can't go off the map.
     */
    private void checkPositionLimits() {
        if (this.position.x <= WorldRenderer.boundingRectStickman.getX())
            this.physicsBody.getPosition().x = WorldRenderer.boundingRectStickman.getX();
        if (this.position.x > WorldRenderer.boundingRectStickman.getWidth())
            this.physicsBody.getPosition().x = WorldRenderer.boundingRectStickman.getWidth();

        if (this.position.y <= WorldRenderer.boundingRectStickman.getY()) {
            this.physicsBody.setTransform(this.physicsBody.getPosition().x, WorldRenderer.boundingRectStickman.getY(), 0);
            stopFalling();
        }

        if (this.position.y > WorldRenderer.boundingRectStickman.getHeight())
            this.physicsBody.getPosition().y = WorldRenderer.boundingRectStickman.getHeight();
    }

    private boolean isTouchingPlatform() {


        Array<TiledMapTileLayer.Cell> cells = getTiles(this.position, stickmanHitBox);
        if (cells == null)
            return false;
        for (TiledMapTileLayer.Cell cell : cells) {
            Boolean isPlatform = (Boolean) cell.getTile().getProperties().get(Constants.PROPERTY_PLATFORM);
            if (isPlatform != null && isPlatform == true)
                return true;
        }
        return false;
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

        TiledMapTileLayer layer = (TiledMapTileLayer) Assets.map.getLayers().get(0);

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
}
