/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.administrator.duchess;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.opengl.GLES20;

/**
 * draw a board fan
 */
public class BoardFan {

    private final String vertexShaderCode =
            // This matrix member variable provides a hook to manipulate
            // the coordinates of the objects that use this vertex shader
            "uniform mat4 uMVPMatrix;" +
            "attribute vec4 vPosition;" +
            "void main() {" +
            // The matrix must be included as a modifier of gl_Position.
            // Note that the uMVPMatrix factor *must be first* in order
            // for the matrix multiplication product to be correct.
            "  gl_Position = uMVPMatrix * vPosition;" +
            "}";

    private final String fragmentShaderCode =
            "precision mediump float;" +
            "uniform vec4 vColor;" +
            "void main() {" +
            "  gl_FragColor = vColor;" +
            "}";

    private final FloatBuffer vertexBuffer;
    private final ShortBuffer drawListBuffer;
    private final ShortBuffer drawListBuffer2;
    private final int mProgram;
    private int mPositionHandle;
    private int mColorHandle;
    private int mMVPMatrixHandle;

    // number of coordinates per vertex in this array
    static final int COORDS_PER_VERTEX = 3;
    public static float squareCoords[] = {
            0.050f,     0.088f, 0.0f,       // 0
            -0.050f,    0.088f, 0.0f,       // 1
            0.114f,     0.192f, 0.0f,       // 2
            0.052f,     0.173f, 0.0f,       // 3
            -0.052f,    0.173f, 0.0f,       // 4
            -0.114f,    0.192f, 0.0f,       // 5
            0.169f,     0.290f, 0.0f,       // 6
            0.114f,     0.276f, 0.0f,       // 7
            0.054f,     0.266f, 0.0f,       // 8
            -0.054f,    0.266f, 0.0f,       // 9
            -0.114f,    0.276f, 0.0f,       // 10
            -0.169f,    0.290f, 0.0f,       // 11
            0.195f,     0.385f, 0.0f,       // 12
            0.125f,     0.371f, 0.0f,       // 13
            0.056f,     0.359f, 0.0f,       // 14
            -0.056f,    0.359f, 0.0f,       // 15
            -0.125f,    0.371f, 0.0f,       // 16
            -0.195f,    0.385f, 0.0f,       // 17
            0.220f,     0.453f, 0.0f,       // 18
            0.135f,     0.453f, 0.0f,       // 19
            0.058f,     0.453f, 0.0f,       // 20
            -0.058f,    0.453f, 0.0f,       // 21
            -0.135f,    0.453f, 0.0f,       // 22
            -0.220f,    0.453f, 0.0f,       // 23
            0.242f,     0.500f, 0.0f,       // 24
            0.145f,     0.525f, 0.0f,       // 25
            0.060f,     0.545f, 0.0f,       // 26
            -0.060f,    0.545f, 0.0f,       // 27
            -0.145f,    0.525f, 0.0f,       // 28
            -0.242f,    0.500f, 0.0f,       // 29
            0.278f,     0.560f, 0.0f,       // 30
            0.175f,     0.610f, 0.0f,       // 31
            0.062f,     0.638f, 0.0f,       // 32
            -0.062f,    0.638f, 0.0f,       // 33
            -0.175f,    0.610f, 0.0f,       // 34
            -0.278f,    0.560f, 0.0f,       // 35

    };

    private final short drawBoardOrder[] = {
            0,1,3,1,3,4,    //124
            5,4,9,5,9,10,   //
            2,3,7,3,7,8,    //122
            6,7,12,7,12,13, //20
            8,9,14,9,14,15,  //18
            10,11,16,11,16,17,   //16
            13,14,19,14,19,20,  //14
            15,16,21,16,21,22,  //12
            18,19,24,19,24,25,  //10
            20,21,26,21,26,27,  //8
            22,23,28,23,28,29,  //6
            25,26,31,26,31,32,  //4
            27,28,33,28,33,34  //2
    };
    /*private final short drawOrder[] = {
            0,1,                                        //1st row
            0,3, 1,4,                                   //1st col
            2,3, 3,4, 4,5,                              //2nd row
            2,7, 3,8, 4,9, 5,10,                        //2nd col
            6,7, 7,8, 8,9, 9,10, 10,11, 6,12,           //3rd row
            7,13, 8,14, 9,15, 10,16, 11,17,             //3rd col
            12,13, 13,14, 14,15, 15,16, 16,17,          //4th row
            12,18, 13,19, 14,20, 15,21, 16,22, 17,23,   //4th col
            18,19, 19,20, 20,21, 21,22, 22,23,          //5th row
            18,24, 19,25, 20,26, 21,27, 22,28, 23,29,   //5th col
            24,25, 25,26, 26,27, 27,28, 28,29,          //6th row
            24,30, 25,31, 26,32, 27,33, 28,34, 29,35,   //6th col
            30,31, 31,32, 32,33, 33,34, 34,35

    };*/
    private final short drawOrder[] = {
             6,12,18,24,30,31,32,33,34,35,29,23,17,11
    };


    private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per vertex

    float color[] = { 0.2f, 0.709803922f, 0.898039216f, 1.0f };
    float color2[] = { 1f, 1f, 1f, 1.0f };

    /**
     * Sets up the drawing object data for use in an OpenGL ES context.
     */
    public BoardFan() {
        // initialize vertex byte buffer for shape coordinates
        ByteBuffer bb = ByteBuffer.allocateDirect(
        // (# of coordinate values * 4 bytes per float)
                squareCoords.length * 4);
        bb.order(ByteOrder.nativeOrder());
        vertexBuffer = bb.asFloatBuffer();
        vertexBuffer.put(squareCoords);
        vertexBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawOrder.length * 2);
        dlb.order(ByteOrder.nativeOrder());
        drawListBuffer = dlb.asShortBuffer();
        drawListBuffer.put(drawOrder);
        drawListBuffer.position(0);

        // initialize byte buffer for the draw list
        ByteBuffer dlb2 = ByteBuffer.allocateDirect(
                // (# of coordinate values * 2 bytes per short)
                drawBoardOrder.length * 2);
        dlb2.order(ByteOrder.nativeOrder());
        drawListBuffer2 = dlb2.asShortBuffer();
        drawListBuffer2.put(drawBoardOrder);
        drawListBuffer2.position(0);

        // prepare shaders and OpenGL program
        int vertexShader = GLRenderer.loadShader(
                GLES20.GL_VERTEX_SHADER,
                vertexShaderCode);
        int fragmentShader = GLRenderer.loadShader(
                GLES20.GL_FRAGMENT_SHADER,
                fragmentShaderCode);

        mProgram = GLES20.glCreateProgram();             // create empty OpenGL Program
        GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
        GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
        GLES20.glLinkProgram(mProgram);                  // create OpenGL program executables
    }

    /**
     * Encapsulates the OpenGL ES instructions for drawing this shape.
     *
     * @param mvpMatrix - The Model View Project matrix in which to draw
     * this shape.
     */
    public void drawBoard(float[] mvpMatrix) {
        // Add program to OpenGL environment
        GLES20.glUseProgram(mProgram);

        // get handle to vertex shader's vPosition member
        mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");

        // Enable a handle to the triangle vertices
        GLES20.glEnableVertexAttribArray(mPositionHandle);

        // Prepare the triangle coordinate data
        GLES20.glVertexAttribPointer(
                mPositionHandle, COORDS_PER_VERTEX,
                GLES20.GL_FLOAT, false,
                vertexStride, vertexBuffer);

        // get handle to fragment shader's vColor member
        mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");

        // Set color for drawing the triangle
        GLES20.glUniform4fv(mColorHandle, 1, color, 0);

        // get handle to shape's transformation matrix
        mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
        GLRenderer.checkGlError("glGetUniformLocation");

        // Apply the projection and view transformation
        GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
        GLRenderer.checkGlError("glUniformMatrix4fv");

        GLES20.glLineWidth(5);
        // Draw the square
        GLES20.glDrawElements(
                GLES20.GL_LINE_STRIP, drawOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

        GLES20.glUniform4fv(mColorHandle, 1, color2, 0);
        GLES20.glDrawElements(
                GLES20.GL_TRIANGLES, drawBoardOrder.length,
                GLES20.GL_UNSIGNED_SHORT, drawListBuffer2);

        // Disable vertex array
        GLES20.glDisableVertexAttribArray(mPositionHandle);
    }


}