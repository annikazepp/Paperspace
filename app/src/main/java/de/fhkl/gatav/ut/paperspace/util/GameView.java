package de.fhkl.gatav.ut.paperspace.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import de.fhkl.gatav.ut.paperspace.R;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {

    private SurfaceHolder surfaceHolder;
    private Context context = getContext();

    // Steuerung
    private int steuerungJoystickPointerId = -1;
    private int directionJoystickPointerId = -1;
    private final Joystick steuerungJoystick;
    private final Joystick directionJoystick;

    // DISPLAY
    public static double screenWidth, screenHeight;

    // GAME
    private final GameLoop gameLoop;
    private final GameContent gameContent;

    // Hintergrund, Leben und Score
    private final Bitmap backgroundBitmap;
    private final Bitmap healthBitmap;
    private final Paint scorePaint;
    private static final int TEXT_SIZE = 70; //TODO WERT?

    public GameView(Context context) {
        super(context);

        SurfaceHolder surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        // Background, Leben und Score
        backgroundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        healthBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart);

        scorePaint = new Paint();
        scorePaint.setColor(Color.rgb(8,22,64)); //TODO Farbe anpassen
        scorePaint.setTextSize(TEXT_SIZE);

        // Joysticks initialisieren
        steuerungJoystick = new Joystick(265,900,130,70);
        directionJoystick = new Joystick(1920-150,900,130,70);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        gameLoop = new GameLoop(this, surfaceHolder);
        gameContent = new GameContent(context, steuerungJoystick, directionJoystick);

        setFocusable(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Handle touch event actions
        int action = event.getAction();
        int actionCode = action & MotionEvent.ACTION_MASK;
        int pointerIdIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
        int pointerId = event.getPointerId(pointerIdIndex);
        float x = event.getX(pointerIdIndex);
        float y = event.getY(pointerIdIndex);
        int pointerCount = event.getPointerCount();

        switch(actionCode) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                if (steuerungJoystick.isPressed(x, y)) {
                     /* movementJoystick is pressed in this event ->
                            store pointer id and setIsPressed(true) */
                    steuerungJoystickPointerId = pointerId;
                    steuerungJoystick.setIsPressed(true);
                } else if (directionJoystick.isPressed(x, y)) {
                     /* directionJoystick is pressed in this event ->
                            store pointer id and setIsPressed(true) */
                    directionJoystickPointerId = pointerId;
                    directionJoystick.setIsPressed(true);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (steuerungJoystickPointerId == pointerId) {
                    // joystick pointer was let go off -> setIsPressed(false) and resetActuator()
                    steuerungJoystick.setIsPressed(false);
                    steuerungJoystick.resetActuator();
                    steuerungJoystickPointerId = -1;
                } else if (directionJoystickPointerId == pointerId) {
                    // joystick pointer was let go off -> setIsPressed(false) and resetActuator()
                    directionJoystick.setIsPressed(false);
                    directionJoystick.resetActuator();
                    directionJoystickPointerId = -1;
                }
                break;
        }
        for (int iPointerIndex = 0; iPointerIndex < pointerCount; iPointerIndex++) {
            if (steuerungJoystick.getIsPressed() && event.getPointerId(iPointerIndex) == steuerungJoystickPointerId) {
                // movementJoystick was pressed previously and is now moved
                steuerungJoystick.setActuator(event.getX(iPointerIndex), event.getY(iPointerIndex));
            } else if (directionJoystick.getIsPressed() && event.getPointerId(iPointerIndex) == directionJoystickPointerId) {
                // directionJoystick was pressed previously and is now moved
                directionJoystick.setActuator(event.getX(iPointerIndex), event.getY(iPointerIndex));
            }
        }
        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        Log.d("Game.java", "surfaceCreated()");
        gameLoop.startLoop();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // NICHT BENÖTIGT
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        gameLoop.stopLoop();
    }

    public void pause() {
        gameLoop.stopLoop();
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        if(canvas !=null) {
            // Hintergrundbild zeichnen
            canvas.drawBitmap(backgroundBitmap, null, new RectF(0, 0, getWidth(), getHeight()), null);

            // GameContent - Spielinhalte zeichnen
            gameContent.draw(canvas);

            // Lebenssymbole zeichnen
            int life = gameContent.getHealth();
            for(int i = life; i>=1; i--){
                canvas.drawBitmap(healthBitmap,
                        (float) (screenWidth - (healthBitmap.getWidth() +5) * i),40,null);
            }

            // Score TODO Koordinaten anpassen?
            canvas.drawText("Score: " + gameContent.getScore(),  (scorePaint.getTextScaleX() + 15) ,85, scorePaint);

            // Joystick zeichnen
            steuerungJoystick.draw(canvas);
            directionJoystick.draw(canvas);
        }
    }

    public void update() {
        if(gameContent!= null){
            gameContent.update();
        }

        if(gameContent.getHealth()==0) {
            directionJoystick.resetActuator();
            steuerungJoystick.resetActuator();
            startGameOverActivity();
        }
    }

    protected void startGameOverActivity() {
        // Startet GameOver Activity und übermittelt den Score
        Intent gameOverIntent = new Intent(context, GameOver.class);
        gameOverIntent.putExtra("score", gameContent.getScore());
        context.startActivity(gameOverIntent);
        ((Activity) context).finish();
    }
}