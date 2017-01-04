package com.thesullies.characters;

/**
 * Created by kosullivan on 04/01/2017.
 */

public class Stickman extends DynamicGameObject {

    public static final int BOB_STATE_JUMP = 0;
    public static final int BOB_STATE_FALL = 1;
    public static final int BOB_STATE_HIT = 2;

    public static final int STICKMAN_WIDTH = 12;
    public static final int STICKMAN_HEIGHT = 12;


    int state;
    float stateTime;

    public Stickman (float x, float y) {
        super(x, y, STICKMAN_WIDTH, STICKMAN_HEIGHT);
        state = BOB_STATE_FALL;
        stateTime = 0;
    }

    public void update (float deltaTime) {
        velocity.add(World.gravity.x * deltaTime, World.gravity.y * deltaTime);
        position.add(velocity.x * deltaTime, velocity.y * deltaTime);
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
        }

        //if (position.x < 0) position.x = World.WORLD_WIDTH;
        //if (position.x > World.WORLD_WIDTH) position.x = 0;

        stateTime += deltaTime;
    }}
