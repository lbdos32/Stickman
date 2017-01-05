package com.thesullies.characters;

import com.badlogic.gdx.math.Vector2;

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
    private static final int STICKMAN_HORIZONTAL_SPEED = 20;
    private static final int STICKMAN_VERTICAL_JUMP = 20;


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


    float stateTime;

    public Stickman(float x, float y) {
        super(x, y, STICKMAN_WIDTH, STICKMAN_HEIGHT);
        state = STICKMAN_STATES.IDLE;
        stateTime = 0;
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
            if (!isFalling()) {
                state = STICKMAN_STATES.IDLE;
            }
            velocity.x = 0;
        }
        if (inputDirection.y == UP && (isJumpAllowed())) {
            velocity.y = STICKMAN_VERTICAL_JUMP;
            state = STICKMAN_STATES.JUMPING;
        } else if (inputDirection.y == DOWN) {
            velocity.y = -STICKMAN_VERTICAL_JUMP;
        } else {
            this.velocity.add(gravity);
            //velocity.y = 0;
        }
        this.position.add(this.velocity.x * deltaTime, this.velocity.y * deltaTime);

        if (this.position.x <= WorldRenderer.boundingRectStickman.getX())
            this.position.x = WorldRenderer.boundingRectStickman.getX();
        if (this.position.x > WorldRenderer.boundingRectStickman.getWidth())
            this.position.x = WorldRenderer.boundingRectStickman.getWidth();

        if (this.position.y <= WorldRenderer.boundingRectStickman.getY()) {
            this.position.y = WorldRenderer.boundingRectStickman.getY();
            this.velocity.y = 0;
            if (isJumping()) {
                state = STICKMAN_STATES.IDLE;
            }
        }

        if (this.position.y > WorldRenderer.boundingRectStickman.getHeight())
            this.position.y = WorldRenderer.boundingRectStickman.getHeight();

/*

        bounds.x = position.x - bounds.width / 2;
        bounds.y = position.y - bounds.height / 2;

        if (velocity.y > 0 && state != BOB_STATE_HIT) {
            if (state != BOB_STATE_JUMP) {
                state = BOB_STATE_JUMP;
                stateTime = 0;
            }
        }

        if (velocity.y < 0 && state != BOB_STATE_HIT) {
            if (state != BOB_STATE_FALL) {
                state = BOB_STATE_FALL;
                stateTime = 0;
            }
        }*/

        //if (position.x < 0) position.x = World.WORLD_WIDTH;
        //if (position.x > World.WORLD_WIDTH) position.x = 0;

        stateTime += deltaTime;
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
}
