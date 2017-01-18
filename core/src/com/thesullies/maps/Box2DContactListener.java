package com.thesullies.maps;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.thesullies.characters.Stickman;

/**
 * Created by kosullivan on 16/01/2017.
 */

public class Box2DContactListener implements ContactListener {


    @Override
    public void beginContact(Contact contact) {

        handleCollision(contact, true);


        //Gdx.app.log(Constants.LOG_TAG, "Contact between " + fixtureA + " and " + fixtureB);
    }
    @Override
    public void endContact(Contact contact) {
        handleCollision(contact, false);
    }

    private void handleCollision(Contact contact, boolean contactStart) {
        Fixture fA = contact.getFixtureA();
        Fixture fB = contact.getFixtureB();

        Object fixtureUserData = null;
        if (fA.getBody().getUserData()!=null)
            fixtureUserData = fA.getBody().getUserData();
        if (fixtureUserData instanceof Stickman) {
            ((Stickman)fixtureUserData).handleCollision(contact, fB, contactStart);
            return;
        }
        if (fB.getBody().getUserData()!=null)
            fixtureUserData = fB.getBody().getUserData();
        if (fixtureUserData instanceof Stickman) {
            ((Stickman)fixtureUserData).handleCollision(contact, fA, contactStart);
            return;
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
