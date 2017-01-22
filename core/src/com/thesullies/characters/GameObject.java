package com.thesullies.characters;

/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/


import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;

public class GameObject {

    /**
     * This is the position in the game world, not in the physics engine.
     * TODO - do we need to keep both, why not just convert from physics position to game world position dynamically?
     */
    public final Vector2 position;
    protected float stateTime;
    public Body physicsBody;


    public void die() {
        StickmanWorld.characterManager.remove(this);
    }


    public GameObject(float x, float y, float width, float height) {
        this.position = new Vector2(x, y);
    }
}
