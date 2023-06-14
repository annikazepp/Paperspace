package de.fhkl.gatav.ut.paperspace.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.fhkl.gatav.ut.paperspace.objects.Comet;

public class GameView extends SurfaceView implements Runnable {

    private Thread gameThread;
    private SurfaceHolder surfaceHolder;
    private volatile boolean playing;
    private boolean paused = true;
    private Canvas canvas;
    private Paint paint;
    private float shipX;
    private float shipY;
    private int screenWidth;
    private int screenHeight;
    private Path shipPath;
    private List<Comet> comets;

    public GameView(Context context) {
        super(context);
        surfaceHolder = getHolder();
        paint = new Paint();
        shipPath = new Path();

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;

        shipX = screenWidth / 2;
        shipY = screenHeight / 2;

        shipPath.moveTo(shipX, shipY - 50); // top point of the ship
        shipPath.lineTo(shipX - 25, shipY + 25); // bottom left point of the ship
        shipPath.lineTo(shipX + 25, shipY + 25); // bottom right point of the ship
        shipPath.close();

        comets = new ArrayList<>();
        initializeComets();
    }

    private void initializeComets() {
        Random random = new Random();
        for (int i = 0; i < 5; i++) {
            int x = random.nextInt(screenWidth);
            int y = random.nextInt(screenHeight);
            float speed = random.nextFloat() * 5 + 1;
            comets.add(new Comet(x, y, speed));
        }
    }

    @Override
    public void run() {
        while (playing) {
            if (!paused) {
                update();
                draw();
            }
        }
    }

    private void update() {
        // Update spaceship logic here
        // Update comet logic here
        for (Comet comet : comets) {
            comet.update(screenWidth, screenHeight);
        }
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);

            paint.setColor(Color.WHITE);
            canvas.drawPath(shipPath, paint);

            paint.setColor(Color.RED);
            for (Comet comet : comets) {
                canvas.drawCircle(comet.getX(), comet.getY(), comet.getSize(), paint);
            }

            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                // Touch released, stop moving
                paused = true;
                break;
            case MotionEvent.ACTION_DOWN:
                // Touch detected, start moving
                paused = false;
                break;
        }
        return true;
    }
}

