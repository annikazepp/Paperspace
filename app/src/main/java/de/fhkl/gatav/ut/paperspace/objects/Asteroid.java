package de.fhkl.gatav.ut.paperspace.objects;



import android.graphics.Canvas;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import javax.microedition.khronos.opengles.GL10;

public class Asteroid extends Obstacle {
    // current rotation
    public float rotation = 0.0f;
    // rotation speed in deg/s
    public float angularVelocity = 0.0f;
    public float rotationAxis[] = {0.0f, 1.0f, 0.0f};

    private static float colorA[] = {0.46f, 0.22f, 0.0f};
    private static float colorB[] = {0.36f, 0.25f, 0.14f};

    private float currentColor[] = new float[3];

    //@formatter:off
    private static final float asteroid_vertices[] = {
            -0.002496f, -0.460835f, -0.010647f,
            0.355168f, -0.174845f, 0.271927f,
            -0.130904f, -0.235006f, 0.386904f,
            -0.444445f, -0.227330f, -0.014998f,
            -0.144050f, -0.203583f, -0.401802f,
            0.346584f, -0.163993f, -0.206828f,
            0.140334f, 0.209464f, 0.421334f,
            -0.359100f, 0.198179f, 0.262922f,
            -0.367392f, 0.212006f, -0.219682f,
            0.125974f, 0.215978f, -0.342880f,
            0.435548f, 0.244987f, 0.059355f,
            -0.008833f, 0.428096f, 0.104398f,
            0.207353f, -0.373424f, 0.153056f,
            0.133621f, -0.247119f, 0.380046f,
            -0.077924f, -0.411979f, 0.220093f,
            0.202472f, -0.363134f, -0.132030f,
            0.411389f, -0.190710f, 0.039447f,
            -0.337833f, -0.274653f, 0.218346f,
            -0.260720f, -0.412636f, -0.022130f,
            -0.345159f, -0.251920f, -0.250657f,
            -0.085405f, -0.390180f, -0.247295f,
            0.120711f, -0.223130f, -0.363495f,
            0.459743f, 0.048270f, -0.087234f,
            0.464842f, 0.045329f, 0.191515f,
            0.292911f, 0.014209f, 0.401127f,
            0.006544f, -0.015725f, 0.469222f,
            -0.286683f, -0.025639f, 0.376206f,
            -0.472227f, -0.017340f, 0.145325f,
            -0.476845f, -0.005225f, -0.142658f,
            -0.299316f, 0.000827f, -0.370762f,
            -0.009890f, 0.006725f, -0.441979f,
            0.279494f, 0.025119f, -0.330386f,
            0.339090f, 0.267239f, 0.278707f,
            -0.127215f, 0.234766f, 0.396782f,
            -0.427477f, 0.245295f, 0.025477f,
            -0.140595f, 0.247552f, -0.336225f,
            0.330688f, 0.268195f, -0.168793f,
            0.252952f, 0.394993f, 0.083174f,
            0.078337f, 0.382254f, 0.297354f,
            -0.215426f, 0.377437f, 0.204316f,
            -0.220648f, 0.381195f, -0.074202f,
            0.069410f, 0.383521f, -0.147035f
    };
    private static final short asteroid_triangles[] = {
            1-1, 13-1, 15-1,
            2-1, 13-1, 17-1,
            1-1, 15-1, 19-1,
            1-1, 19-1, 21-1,
            1-1, 21-1, 16-1,
            2-1, 17-1, 24-1,
            3-1, 14-1, 26-1,
            4-1, 18-1, 28-1,
            5-1, 20-1, 30-1,
            6-1, 22-1, 32-1,
            2-1, 24-1, 25-1,
            3-1, 26-1, 27-1,
            4-1, 28-1, 29-1,
            5-1, 30-1, 31-1,
            6-1, 32-1, 23-1,
            7-1, 33-1, 39-1,
            8-1, 34-1, 40-1,
            9-1, 35-1, 41-1,
            10-1, 36-1, 42-1,
            11-1, 37-1, 38-1,
            15-1, 14-1, 3-1,
            15-1, 13-1, 14-1,
            13-1, 2-1, 14-1,
            17-1, 16-1, 6-1,
            17-1, 13-1, 16-1,
            13-1, 1-1, 16-1,
            19-1, 18-1, 4-1,
            19-1, 15-1, 18-1,
            15-1, 3-1, 18-1,
            21-1, 20-1, 5-1,
            21-1, 19-1, 20-1,
            19-1, 4-1, 20-1,
            16-1, 22-1, 6-1,
            16-1, 21-1, 22-1,
            21-1, 5-1, 22-1,
            24-1, 23-1, 11-1,
            24-1, 17-1, 23-1,
            17-1, 6-1, 23-1,
            26-1, 25-1, 7-1,
            26-1, 14-1, 25-1,
            14-1, 2-1, 25-1,
            28-1, 27-1, 8-1,
            28-1, 18-1, 27-1,
            18-1, 3-1, 27-1,
            30-1, 29-1, 9-1,
            30-1, 20-1, 29-1,
            20-1, 4-1, 29-1,
            32-1, 31-1, 10-1,
            32-1, 22-1, 31-1,
            22-1, 5-1, 31-1,
            25-1, 33-1, 7-1,
            25-1, 24-1, 33-1,
            24-1, 11-1, 33-1,
            27-1, 34-1, 8-1,
            27-1, 26-1, 34-1,
            26-1, 7-1, 34-1,
            29-1, 35-1, 9-1,
            29-1, 28-1, 35-1,
            28-1, 8-1, 35-1,
            31-1, 36-1, 10-1,
            31-1, 30-1, 36-1,
            30-1, 9-1, 36-1,
            23-1, 37-1, 11-1,
            23-1, 32-1, 37-1,
            32-1, 10-1, 37-1,
            39-1, 38-1, 12-1,
            39-1, 33-1, 38-1,
            33-1, 11-1, 38-1,
            40-1, 39-1, 12-1,
            40-1, 34-1, 39-1,
            34-1, 7-1, 39-1,
            41-1, 40-1, 12-1,
            41-1, 35-1, 40-1,
            35-1, 8-1, 40-1,
            42-1, 41-1, 12-1,
            42-1, 36-1, 41-1,
            36-1, 9-1, 41-1,
            38-1, 42-1, 12-1,
            38-1, 37-1, 42-1,
            37-1, 10-1, 42-1
    };
    //@formatter:on

    private static FloatBuffer asteroidVerticesBuffer;
    private static ShortBuffer asteroidTrianglesBuffer;

    private static boolean buffersInitialized = false;

    public Asteroid() {
        randomizeRotationAxis();
        randomizeColor();

        if(!buffersInitialized) {
            // Initialize buffers
            ByteBuffer asteroidVerticesBB = ByteBuffer.allocateDirect(asteroid_vertices.length * 4);
            asteroidVerticesBB.order(ByteOrder.nativeOrder());
            asteroidVerticesBuffer = asteroidVerticesBB.asFloatBuffer();
            asteroidVerticesBuffer.put(asteroid_vertices);
            asteroidVerticesBuffer.position(0);

            ByteBuffer asteroidTrianglesBB = ByteBuffer.allocateDirect(asteroid_triangles.length * 2);
            asteroidTrianglesBB.order(ByteOrder.nativeOrder());
            asteroidTrianglesBuffer = asteroidTrianglesBB.asShortBuffer();
            asteroidTrianglesBuffer.put(asteroid_triangles);
            asteroidTrianglesBuffer.position(0);

            buffersInitialized = true;
        }
    }

    @Override
    public void draw(Canvas canvas) {
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glPushMatrix();
        {
            gl.glMultMatrixf(transformationMatrix, 0);
            gl.glScalef(scale, scale, scale);

            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

            gl.glLineWidth(1.0f);

            gl.glRotatef(rotation, rotationAxis[0], rotationAxis[1], rotationAxis[2]);
            gl.glVertexPointer(3, GL10.GL_FLOAT, 0, asteroidVerticesBuffer);
            gl.glColor4f(currentColor[0], currentColor[1], currentColor[2], 0);
            for (int i = 0; i < (asteroid_triangles.length / 3); i++) {
                asteroidTrianglesBuffer.position(3 * i);
                gl.glDrawElements(GL10.GL_LINE_LOOP, 3, GL10.GL_UNSIGNED_SHORT, asteroidTrianglesBuffer);
            }
            asteroidTrianglesBuffer.position(0);

            gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        }
        gl.glPopMatrix();
    }

    @Override
    public void update(float fracSec) {
        updatePosition(fracSec);
        rotation += fracSec * angularVelocity;
    }

    public void randomizeRotationAxis() {
        rotationAxis[0] = (float) Math.random();
        rotationAxis[1] = (float) Math.random();
        rotationAxis[2] = (float) Math.random();
        //normalize(rotationAxis);
    }

    public void randomizeColor() {
        // shades of brown
        float factor = (float) Math.random();
        currentColor[0] = factor * colorA[0] + (1.0f - factor) * colorB[0];
        currentColor[1] = factor * colorA[1] + (1.0f - factor) * colorB[1];
        currentColor[2] = factor * colorA[2] + (1.0f - factor) * colorB[2];
    }

}

