package com.thesullies.maps;

/**
 * Created by kosullivan on 05/01/2017.
 */

public class Constants {

    public static final float PHYSICS_PIXELS_TO_METERS = 10f;
    public static final float PHYSICS_GRAVITY = -9.8f;

    // Map Properties

    /**
     * If an object has this property then stickman can move through it
     */
    public static final String PROPERTY_SENSOR = "sensor";
    /**
     * A door should have this property so that stickman can go through it to exit the level
     */
    public static final String PROPERTY_DOOR = "exit";
    /**
     * Objects with this property will be added as a coin graphic and stickman can collect them
     */
    public static final String PROPERTY_COIN = "coin";
    /**
     * Objects with this property will cause death if user hits it
     */
    public static final String PROPERTY_DEATH = "death";
    /**
     * If an object has this property, a willow the wisp particle animation will be played in its location
     */
    public static final String PROPERTY_WILLOW_THE_WISP = "willowTheWisp";


    public static final String MAP_LAYER_OBJECTS = "Objects";
    public static final String MAP_LAYER_PLATFORM = "PlatformTiles";

    public static final String LOG_TAG = "STICKMAN";

}
