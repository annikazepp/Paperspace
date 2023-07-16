package de.fhkl.gatav.ut.paperspace.powerUps;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import java.util.Random;

import de.fhkl.gatav.ut.paperspace.R;
import de.fhkl.gatav.ut.paperspace.objects.Circle;

public class PowerUps extends Circle {

    public enum PowerUp{
        HEART,
        GHOST(5 * 1000L),
        X2SCORE(20 * 1000L),
        STRONG_SHOT(10 * 1000L);

        private long duration;
        PowerUp(long duration) {
            this.duration = duration;
        }
        PowerUp(){
        }

        public long getDuration() {
            return duration;
        }

        /**
         * Pick a random value of the PowerUp enum.
         *
         * @return a random PowerUp.
         */
        public static PowerUp getRandomValue() {
            Random random = new Random();
            PowerUp[] values = PowerUp.values();
            return values[random.nextInt(values.length)];
        }
    }

    private PowerUp powerUp;

    public PowerUp getPowerUp() {
        return powerUp;
    }

    private Context context;

    private long creationTime; // Zeitstempel der Erstellung
    private long expirationTime = 6000L; // TODO Dauer 6sek

    public long getCreationTime() {
        return creationTime;
    }

    public long getExpirationTime() {
        return expirationTime;
    }


    public PowerUps(Context context, double positionX, double positionY) {
        super(context, Color.BLUE, positionX, positionY);

        this.creationTime = System.currentTimeMillis();

        this.context = context;
        this.powerUp = PowerUp.getRandomValue();

        loadBitmap();
    }


    private void loadBitmap() {
        switch (powerUp) {
            case GHOST:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.item_ghost); //TODO BITMAP ANPASSEN
                break;
            case HEART:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart);
                break;
            case STRONG_SHOT:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.item_powerball);
                break;
            case X2SCORE:
                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.itemx2);
                break;
        }
            setRadius(bitmap.getHeight() / 2.0);
    }



    @Override
    public void update() {

    }
}
