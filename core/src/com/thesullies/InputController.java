package com.thesullies;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;


/**
 * Class responsible for displaying the input controls on screen and getting the input touch events from the user.
 * <p>
 * Created by kosullivan on 06/02/2017.
 */
public class InputController {

    public static final int DOWN = 1;
    public static final int UP = -1;
    public static final int LEFT = -1;
    public static final int RIGHT = 1;

    private ShapeRenderer shapeRenderer;
    private final Rectangle rectLeftControl;
    private final Rectangle rectRightControl;
    private final Rectangle rectUpControl;
    private final Rectangle rectDownControl;
    private final Rectangle rectJumpControl;

    Vector2 controlStickNeutral;
    Vector2 controlStickJump;
    private int constrolStickRadius;
    private int constrolStickNeutralRadius;


    public Vector2 inputDirection;
    public boolean jumpPressed;

    private int displayHeight;

    public InputController(int displayWidth, int displayHeight) {
        this.shapeRenderer = new ShapeRenderer();

        this.displayHeight = displayHeight;


        controlStickNeutral = new Vector2(displayWidth / 10, displayHeight / 6);
        controlStickJump = new Vector2(displayWidth - displayWidth / 10, displayHeight / 6);
        constrolStickRadius = displayWidth / 10;
        constrolStickNeutralRadius = displayWidth / 30;

        inputDirection = new Vector2();

        this.rectLeftControl = new Rectangle(displayWidth / 10 - constrolStickRadius, displayHeight / 6, constrolStickRadius, constrolStickRadius);
        this.rectRightControl = new Rectangle(displayWidth / 10 + constrolStickRadius, displayHeight / 6, constrolStickRadius, constrolStickRadius);
        this.rectUpControl = new Rectangle(displayWidth / 10, constrolStickRadius + displayHeight / 6, constrolStickRadius, constrolStickRadius);
        this.rectDownControl = new Rectangle(displayWidth / 10, displayHeight / 6 - constrolStickRadius, constrolStickRadius, constrolStickRadius);
        this.rectJumpControl = new Rectangle(displayWidth - displayWidth / 10, displayHeight / 6, constrolStickRadius, constrolStickRadius);

    }

    /**
     * Detect where on the screen the user has pressed and if this corresponds to up/down/left/right movement.
     */
    public void getUserInput() {

        this.inputDirection.x = 0;
        this.inputDirection.y = 0;
        this.jumpPressed = false;
        if (Gdx.input.isTouched()) {
            //guiCam.unproject(touchPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0));

            for (int i = 0; i < 10; i++) {
                if (Gdx.input.isTouched(i)) {
                    int inputY = displayHeight - Gdx.input.getY(i);
                    int inputX = Gdx.input.getX(i);
                    this.checkInput(inputX, inputY);
                }
            }
        }
    }


    public void checkInput(int inputX, int inputY) {
        if (rectDownControl.contains(inputX, inputY))
            inputDirection.y = DOWN;
        else if (rectUpControl.contains(inputX, inputY))
            inputDirection.y = UP;

        if (rectLeftControl.contains(inputX, inputY))
            inputDirection.x = LEFT;
        else if (rectRightControl.contains(inputX, inputY))
            inputDirection.x = RIGHT;

        if (rectJumpControl.contains(inputX, inputY))
            jumpPressed = true;
    }

    public void renderInputControls() {
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        //shapeRenderer.circle(this.controlStickNeutral.x, controlStickNeutral.y, constrolStickNeutralRadius);
        drawJumpController();
        drawRightController();
        //drawUpController();
        drawLeftController();
        //drawDownController();
        shapeRenderer.end();
    }

    private void drawJumpController() {
        if (this.jumpPressed)
            shapeRenderer.setColor(Color.BLUE);
        else
            shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.circle(rectJumpControl.x + rectJumpControl.width/2, rectJumpControl.y+rectJumpControl.height/2, rectJumpControl.height/2);
        shapeRenderer.circle(rectJumpControl.x + rectJumpControl.width/2, rectJumpControl.y+rectJumpControl.height/2, rectJumpControl.height/2.1f);
        shapeRenderer.circle(rectJumpControl.x + rectJumpControl.width/2, rectJumpControl.y+rectJumpControl.height/2, rectJumpControl.height/2.2f);
//        shapeRenderer.rect(rectJumpControl.x, rectJumpControl.y, rectJumpControl.width, rectJumpControl.height);

    }

    private void drawDownController() {
        if (inputDirection.y == DOWN)
            shapeRenderer.setColor(Color.BLUE);
        else
            shapeRenderer.setColor(Color.WHITE);
        //shapeRenderer.rect(rectDownControl.x, rectDownControl.y, rectDownControl.width, rectDownControl.height);
        shapeRenderer.circle(rectDownControl.x + rectDownControl.width/2, rectDownControl.y+rectDownControl.height/2, rectDownControl.height/2);
    }

    private void drawLeftController() {
        if (inputDirection.x == LEFT)
            shapeRenderer.setColor(Color.BLUE);
        else
            shapeRenderer.setColor(Color.WHITE);
//        shapeRenderer.rect(rectLeftControl.x, rectLeftControl.y, rectLeftControl.width, rectLeftControl.height);
        shapeRenderer.circle(rectLeftControl.x + rectLeftControl.width/2, rectLeftControl.y+rectLeftControl.height/2, rectLeftControl.height/2);
        shapeRenderer.circle(rectLeftControl.x + rectLeftControl.width/2, rectLeftControl.y+rectLeftControl.height/2, rectLeftControl.height/2.1f);
        shapeRenderer.circle(rectLeftControl.x + rectLeftControl.width/2, rectLeftControl.y+rectLeftControl.height/2, rectLeftControl.height/2.2f);
    }

    private void drawUpController() {
        if (inputDirection.y == UP)
            shapeRenderer.setColor(Color.BLUE);
        else
            shapeRenderer.setColor(Color.WHITE);
//        shapeRenderer.rect(rectUpControl.x, rectUpControl.y, rectUpControl.width, rectUpControl.height);
        shapeRenderer.circle(rectUpControl.x + rectUpControl.width/2, rectUpControl.y+rectUpControl.height/2, rectUpControl.height/2);
    }

    private void drawRightController() {
        if (inputDirection.x == RIGHT)
            shapeRenderer.setColor(Color.BLUE);
        else
            shapeRenderer.setColor(Color.WHITE);
//        shapeRenderer.rect(rectRightControl.x, rectRightControl.y, rectRightControl.width, rectRightControl.height);
        shapeRenderer.circle(rectRightControl.x + rectRightControl.width/2, rectRightControl.y+rectRightControl.height/2, rectRightControl.height/2);
        shapeRenderer.circle(rectRightControl.x + rectRightControl.width/2, rectRightControl.y+rectRightControl.height/2, rectRightControl.height/2.1f);
        shapeRenderer.circle(rectRightControl.x + rectRightControl.width/2, rectRightControl.y+rectRightControl.height/2, rectRightControl.height/2.2f);
    }

}
