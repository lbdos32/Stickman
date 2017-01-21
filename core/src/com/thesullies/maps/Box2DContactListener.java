package com.thesullies.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.thesullies.characters.Coin;
import com.thesullies.characters.GameObject;
import com.thesullies.characters.Stickman;
import com.thesullies.characters.StickmanWorld;

/**
 * Created by kosullivan on 16/01/2017.
 */

public class Box2DContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Gdx.app.log(Constants.LOG_TAG, "Contact start between " + contact.getFixtureA() + " and " + contact.getFixtureB());
        handleCollision(contact, true);
    }

    @Override
    public void endContact(Contact contact) {
        Gdx.app.log(Constants.LOG_TAG, "Contact end between " + contact.getFixtureA() + " and " + contact.getFixtureB());
        handleCollision(contact, false);
    }

    private void handleCollision(Contact contact, boolean contactStart) {
        Fixture fA = contact.getFixtureA();
        Fixture fB = contact.getFixtureB();

        Object fixtureUserData = null;
        Body deleteBody;
        if (fA.getBody().getUserData() != null) {
            fixtureUserData = fA.getBody().getUserData();
            if (fixtureUserData instanceof Stickman) {
                if (((Stickman) fixtureUserData).handleCollision(contact, fA, fB, contactStart) == true) {

                    if (fB.getBody().getUserData() != null && fB.getBody().getUserData() instanceof GameObject) {
                        ((GameObject) fB.getBody().getUserData()).die();
                    }
                }
                return;
            } else if (fixtureUserData instanceof Coin) {
                Gdx.app.debug(Constants.LOG_TAG, "Touching COIN: contactStart=" + contactStart);
            }
        }

        if (fB.getBody().getUserData() != null) {
            fixtureUserData = fB.getBody().getUserData();
            if (fixtureUserData instanceof Stickman) {
                if (((Stickman) fixtureUserData).handleCollision(contact, fB, fA, contactStart) == true) {
                    if (fA.getBody().getUserData() != null && fA.getBody().getUserData() instanceof GameObject) {
                        ((GameObject) fA.getBody().getUserData()).die();
                    }
                }
                return;
            } else if (fixtureUserData instanceof Coin) {
                Gdx.app.debug(Constants.LOG_TAG, "Touching COIN: contactStart=" + contactStart);
            }
        }
    }


    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        //you can leave this empty
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        //you can leave this empty
    }
}
