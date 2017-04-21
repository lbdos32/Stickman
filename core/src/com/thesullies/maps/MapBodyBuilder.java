package com.thesullies.maps;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.EllipseShapeBuilder;
import com.badlogic.gdx.maps.Map;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.CircleMapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.PolylineMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.objects.TextureMapObject;
import com.badlogic.gdx.math.Circle;
import com.badlogic.gdx.math.Ellipse;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.thesullies.characters.Coin;
import com.thesullies.characters.FrogSpawn;
import com.thesullies.characters.StickmanWorld;
import com.thesullies.characters.WillowTheWisp;

/**
 * Creates the box2d physical elements from the Tiled map object loaded.
 * <p>The physics elements are in a platform layer called Objects (Constants.MAP_LAYER_OBJECTS)</p>
 */
public class MapBodyBuilder {

    // The pixels per tile. If your tiles are 16x16, this is set to 16f
    private static float ppt = 0;

    public static Array<Body> buildShapes(Map map, float pixels, World world) {
        ppt = pixels;
        MapObjects objects = map.getLayers().get(Constants.MAP_LAYER_OBJECTS).getObjects();

        Array<Body> bodies = new Array<Body>();

        for (MapObject object : objects) {

            if (object instanceof TextureMapObject) {
                continue;
            }

            Shape shape;
            if (object instanceof RectangleMapObject) {
                shape = getRectangle((RectangleMapObject) object);

                if (MapBodyBuilder.isCoin(object)) {
                    Coin coin = new Coin(
                            MapBodyBuilder.getCenterXOfRectangle((RectangleMapObject) object),
                            MapBodyBuilder.getCenterYOfRectangle((RectangleMapObject) object),
                            world);
                    StickmanWorld.coins.add(coin);
                    continue;
                } else if (MapBodyBuilder.isWillowTheWisp(object)) {
                    WillowTheWisp wtw = new WillowTheWisp(
                            MapBodyBuilder.getCenterXOfRectangle((RectangleMapObject) object),
                            MapBodyBuilder.getBottomOfRectangle((RectangleMapObject) object),
                            world);
                    StickmanWorld.dynamicGameObjects.add(wtw);
                    continue;
                } else if (MapBodyBuilder.isFrogSpawn(object)) {
                    FrogSpawn fs = new FrogSpawn(
                            MapBodyBuilder.getCenterXOfRectangle((RectangleMapObject) object),
                            MapBodyBuilder.getBottomOfRectangle((RectangleMapObject) object),
                            world);
                    StickmanWorld.dynamicGameObjects.add(fs);
                    continue;
                }
            } else if (object instanceof PolygonMapObject) {
                shape = getPolygon((PolygonMapObject) object);
            } else if (object instanceof PolylineMapObject) {
                shape = getPolyline((PolylineMapObject) object);
            } else if (object instanceof CircleMapObject) {
                shape = getCircle((CircleMapObject) object);
            } else if (object instanceof EllipseMapObject) {
                shape = getEllipse((EllipseMapObject) object);
            } else {
                continue;
            }


            BodyDef bd = new BodyDef();
            bd.type = BodyDef.BodyType.StaticBody;
            Body body = world.createBody(bd);
            body.setUserData(object);

            Fixture fixture = body.createFixture(shape, 1);
            Object sensor = object.getProperties().get(Constants.PROPERTY_SENSOR);
            if (isDeath(object) ||
                    isDoor(object) ||
                    isWillowTheWisp(object) ||
                    isFrogSpawn(object) ||
                    (sensor != null && sensor instanceof Boolean && ((Boolean) sensor) == true)) {
                fixture.setSensor(true);
            }
            if (isDoor(object)) {
                WillowTheWisp wtw = new WillowTheWisp(
                        MapBodyBuilder.getCenterXOfRectangle((RectangleMapObject) object),
                        MapBodyBuilder.getBottomOfRectangle((RectangleMapObject) object),
                        world);
                StickmanWorld.dynamicGameObjects.add(wtw);
            }

            bodies.add(body);

            shape.dispose();
        }
        return bodies;
    }

    private static float getCenterXOfRectangle(RectangleMapObject object) {
        return (object.getRectangle().getX() / ppt) + (object.getRectangle().getWidth() / 2) / ppt;
    }

    private static float getCenterYOfRectangle(RectangleMapObject object) {
        return (object.getRectangle().getY() + object.getRectangle().getHeight() / 2) / ppt;
    }

    private static float getBottomOfRectangle(RectangleMapObject object) {
        return (object.getRectangle().getY()) / ppt;
    }


    private static PolygonShape getRectangle(RectangleMapObject rectangleObject) {
        Rectangle rectangle = rectangleObject.getRectangle();
        PolygonShape polygon = new PolygonShape();
        Vector2 size = new Vector2((rectangle.x + rectangle.width * 0.5f) / ppt / Constants.PHYSICS_PIXELS_TO_METERS,
                (rectangle.y + rectangle.height * 0.5f) / ppt / Constants.PHYSICS_PIXELS_TO_METERS);
        polygon.setAsBox(rectangle.width * 0.5f / ppt / Constants.PHYSICS_PIXELS_TO_METERS,
                rectangle.height * 0.5f / ppt / Constants.PHYSICS_PIXELS_TO_METERS,
                size,
                0.0f);
        return polygon;
    }

    private static CircleShape getCircle(CircleMapObject circleObject) {
        Circle circle = circleObject.getCircle();
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(circle.radius / ppt / Constants.PHYSICS_PIXELS_TO_METERS);
        circleShape.setPosition(new Vector2(circle.x / ppt / Constants.PHYSICS_PIXELS_TO_METERS, circle.y / ppt / Constants.PHYSICS_PIXELS_TO_METERS));
        return circleShape;
    }

    private static Shape getEllipse(EllipseMapObject ellipseObject) {

        Ellipse ellipse = ellipseObject.getEllipse();
        //CircleShape
        CircleShape circleShape = new CircleShape();
        circleShape.setRadius(ellipse.width / 2 / ppt / Constants.PHYSICS_PIXELS_TO_METERS);
        circleShape.setPosition(new Vector2((ellipse.x + ellipse.width / 2) / ppt / Constants.PHYSICS_PIXELS_TO_METERS, (ellipse.y + ellipse.height / 2) / ppt / Constants.PHYSICS_PIXELS_TO_METERS));
        return circleShape;
    }

    private static PolygonShape getPolygon(PolygonMapObject polygonObject) {
        PolygonShape polygon = new PolygonShape();
        float[] vertices = polygonObject.getPolygon().getTransformedVertices();

        float[] worldVertices = new float[vertices.length];

        for (int i = 0; i < vertices.length; ++i) {
            worldVertices[i] = vertices[i] / ppt / Constants.PHYSICS_PIXELS_TO_METERS;
        }

        polygon.set(worldVertices);
        return polygon;
    }

    private static ChainShape getPolyline(PolylineMapObject polylineObject) {
        float[] vertices = polylineObject.getPolyline().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length / 2];

        for (int i = 0; i < vertices.length / 2; ++i) {
            worldVertices[i] = new Vector2();
            worldVertices[i].x = vertices[i * 2] / ppt / Constants.PHYSICS_PIXELS_TO_METERS;
            worldVertices[i].y = vertices[i * 2 + 1] / ppt / Constants.PHYSICS_PIXELS_TO_METERS;
        }

        ChainShape chain = new ChainShape();
        chain.createChain(worldVertices);
        return chain;
    }

    public static boolean isDoor(MapObject userData) {

        if (userData.getProperties().get(Constants.PROPERTY_DOOR) != null) {
            return true;
        }

        return false;
    }

    public static boolean isCoin(MapObject userData) {
        if (userData.getProperties().get(Constants.PROPERTY_COIN) != null) {
            return true;
        }
        return false;
    }

    public static boolean isDeath(MapObject userData) {
        if (userData.getProperties().get(Constants.PROPERTY_DEATH) != null) {
            return true;
        }
        return false;
    }

    public static boolean isWillowTheWisp(MapObject userData) {
        if (userData.getProperties().get(Constants.PROPERTY_WILLOW_THE_WISP) != null) {
            return true;
        }
        return false;
    }

    public static boolean isFrogSpawn(MapObject userData) {
        if (userData.getProperties().get(Constants.PROPERTY_FROG_SPAWN) != null) {
            return true;
        }
        return false;
    }
}
