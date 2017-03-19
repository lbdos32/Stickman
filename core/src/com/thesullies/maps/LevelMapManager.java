package com.thesullies.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.World;
import com.thesullies.characters.WorldRenderer;

/**
 * This class is responsible for holding the TiledMap object and extracting the
 * full map size, & cell size.
 * <p>
 * Created by kosullivan on 06/02/2017.
 */
public class LevelMapManager {

    private TmxMapLoader mapLoader = new TmxMapLoader();
    private TiledMap levelMap;
    private Rectangle boundingRectCamera;

    /**
     * This is a bounding rectangle around the whole level, so we know where it starts/stops
     */
    public Rectangle levelBoundingRectStickman;
    public static float MAP_UNIT_SCALE = 1 / 8f;


    /**
     * Load the map & the physics objects defined in the map.
     *
     * @param level
     */
    public void loadLevel(int level, World physicsWorld) throws MapException {

        loadMap(level);

        TiledMapTileLayer layer = (TiledMapTileLayer) this.levelMap.getLayers().get(Constants.MAP_LAYER_PLATFORM);
        float mapCellSize = layer.getTileHeight();

        boundingRectCamera = new Rectangle(
                WorldRenderer.GAME_WIDTH / 2,
                WorldRenderer.GAME_HEIGHT / 2,
                layer.getWidth() * layer.getTileWidth() * LevelMapManager.MAP_UNIT_SCALE - WorldRenderer.GAME_WIDTH / 2,
                layer.getHeight() * layer.getTileHeight() * LevelMapManager.MAP_UNIT_SCALE - WorldRenderer.GAME_HEIGHT / 2);

        levelBoundingRectStickman = new Rectangle(0, 0, layer.getWidth() * layer.getTileWidth() * LevelMapManager.MAP_UNIT_SCALE, layer.getHeight() * layer.getTileHeight() * LevelMapManager.MAP_UNIT_SCALE);

        // Load the data from the ObjectLayer in the map
        MapBodyBuilder.buildShapes(this.levelMap, mapCellSize * LevelMapManager.MAP_UNIT_SCALE * 2, physicsWorld);
    }

    public void loadMap(int level) throws MapException {
        String levelFileName = String.format("levels/level_%02d.tmx", level);
        Gdx.app.debug(Constants.LOG_TAG, String.format("Loading map '%s'", levelFileName));
        this.levelMap = mapLoader.load(levelFileName);
        if (this.levelMap == null) {
            throw new MapException(String.format("Unable to load the map from file %s", levelFileName), levelFileName);
        }
        if (this.levelMap.getLayers().get(Constants.MAP_LAYER_PLATFORM) == null) {
            throw new MapException(String.format("The map is missing layer %s", Constants.MAP_LAYER_PLATFORM), levelFileName);
        }
        if (this.levelMap.getLayers().get(Constants.MAP_LAYER_OBJECTS) == null)
            throw new MapException(String.format("The map is missing layer %s", Constants.MAP_LAYER_OBJECTS), levelFileName);
    }
    
    public TiledMap getLevelMap() {
        return levelMap;
    }

    public Rectangle getBoundingRectCamera() {
        return boundingRectCamera;
    }

}

