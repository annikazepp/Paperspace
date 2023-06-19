package de.fhkl.gatav.ut.paperspace.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import de.fhkl.gatav.ut.paperspace.R;

// Darstellung Oberfläche
public class SpaceView extends SurfaceView implements SurfaceHolder.Callback, Runnable {

    //SurffaceHolder to manage a surface for drawing graphics or controlling the format of the surface
    private SurfaceHolder surfaceHolder;

    private Thread gameThread;
    private boolean isRunning = false;

    private Bitmap backgroundBitmap;
    private Bitmap healthBitmap;
    private GameContent gameContent;


    //Zustände
    private boolean gameOver = false;



    //Constructor
    public SpaceView(Context context){
        super(context);

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);

        gameContent = new GameContent(context);

        backgroundBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        healthBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart);

    }

    // wird aufgerufen, wenn Surface erstellt wird
    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        startGameLoop();
    }

    //TODO
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        // NICHT BENÖTIGT
    }



    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        stopGameLoop();
    }


    public void startGameLoop(){
        isRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void stopGameLoop(){
        isRunning = false;
        try {
            gameThread.join();
        }catch (InterruptedException e){
            e.printStackTrace();
        }
    }

    void updateContent(){
        if(gameContent!= null){
            gameContent.update();
        }

    }


    void drawContent(Canvas c){
        if(c !=null) {
            // Hintergrund zeichnen
            c.drawBitmap(backgroundBitmap, null, new RectF(0, 0, c.getWidth(), c.getHeight()), null);

            // GameContent (Player, Asteroiden) - Spielinhalte zeichnen
            gameContent.draw(c);

            // Leben
           int life = gameContent.getHealthSpaceShip();
            for(int i = life; i>=1; i--){
                c.drawBitmap(healthBitmap,
                        gameContent.getGameWidth() - (healthBitmap.getWidth() +5) * i ,40,null);
            }
            //TODO GameOver hier gut?
            if (life == 0){
                gameOver = true;
            }





        }
        }

        //TODO dauert zu lange bis GameOver angezeigt wird
    private void startGameOverActivity() {
        Intent gameOverIntent = new Intent(getContext(), GameOver.class);
        getContext().startActivity(gameOverIntent);
    }


    @Override
    public void run() {

        while(isRunning) {
            Canvas canvas = null;
            try{
                canvas = surfaceHolder.lockCanvas();
                synchronized (surfaceHolder){
                    updateContent();
                    drawContent(canvas);
                }
            }finally {
                if(canvas!=null){
                    surfaceHolder.unlockCanvasAndPost(canvas);
                }
            }

            if(gameContent!=null && gameOver) {
                startGameOverActivity(); //TODO ÜBERGANG VON GAME ZU GAMEOVER
                //stopGameLoop();
             }


        }






    }









}

