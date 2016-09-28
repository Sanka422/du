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

import android.content.Context;
import android.graphics.Matrix;
import android.opengl.GLSurfaceView;
import android.util.Log;
import android.view.MotionEvent;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {

    private final GLRenderer mRenderer;

    public MyGLSurfaceView(Context context) {
        super(context);

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new GLRenderer(context);
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        //setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }

    private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
    private float mPreviousX;
    private float mPreviousY;

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        // MotionEvent reports input details from the touch screen
        // and other input controls. In this case, you are only
        // interested in events where the touch position changed.

        //get touch location
        float x = e.getX();
        float y = e.getY();

        //generalize to opengl's location
        x = (float) ((x / 710 -0.5)/0.5 *0.63 );
        y = (float) ((float) (y / 1130 -0.5)/0.499);

        switch (e.getAction()) {
            case MotionEvent.ACTION_UP:
                int m = mapBoard(x,y);
                switch (mRenderer.phase){
                    case Start:
                        Log.d("tap", String.valueOf(m));
                        if(m >=0){
                            if(mRenderer.board.own(mRenderer.board.board[m])) {
                                mRenderer.board.placeSelected = m;
                                mRenderer.board.calculateValidMove();
                                mRenderer.phase = GLRenderer.Phase.PieceSelected;
                            }
                        }
                        break;
                    case PieceSelected:
                        if(m>=0){
                            if(mRenderer.board.validMove.contains(m)){
                                mRenderer.board.piecePlaced = m;
                                mRenderer.phase = GLRenderer.Phase.PiecePlaced;
                            }else if(mRenderer.board.own(mRenderer.board.board[m])){
                                mRenderer.board.placeSelected = m;
                                mRenderer.board.calculateValidMove();
                                mRenderer.board.piecePlaced = -1;
                            }
                        }
                        break;
                    case PiecePlaced:
                        if(m>=0){
                            if(mRenderer.board.validMove.contains(m)){
                                mRenderer.board.piecePlaced = m;
                                mRenderer.phase = GLRenderer.Phase.PiecePlaced;
                            }else if(mRenderer.board.own(mRenderer.board.board[m])){
                                mRenderer.board.piecePlaced = -1;
                                mRenderer.board.placeSelected = m;
                                mRenderer.board.calculateValidMove();
                                mRenderer.phase = GLRenderer.Phase.PieceSelected;
                            }
                        }else if(m == -2){
                            mRenderer.board.makeMove();
                            if(mRenderer.board.promoteValid()){
                                mRenderer.phase = GLRenderer.Phase.Promote;
                            }else {
                                mRenderer.phase = GLRenderer.Phase.Start;
                                mRenderer.board.nextPlayer();
                            }
                            mRenderer.board.piecePlaced = -1;
                            mRenderer.board.placeSelected = -1;
                            mRenderer.board.validMove = null;
                        }
                        break;
                    case End:
                        break;
                    case Promote:
                        m = mapPromote(x,y);
                        Log.d("promote", String.valueOf(m));
                        switch (m){
                            case 10:
                                mRenderer.phase = GLRenderer.Phase.Start;
                                mRenderer.board.nextPlayer();
                                break;
                            case 0:
                                if(promote(0)){
                                    break;
                                }else{
                                    promote(4);
                                    break;
                                }
                            case 5:
                                if(promote(5)){
                                    break;
                                }else{
                                    promote(8);
                                    break;
                                }
                            case 1:
                                promote(1);
                                break;
                            case 6:
                                promote(1);
                                break;
                            case 7:
                                promote(7);
                                break;
                            case 3:
                                promote(3);
                                break;
                            case 9:
                                promote(9);
                                break;
                        }
                        break;
                }
                break;
        }

        mPreviousX = x;
        mPreviousY = y;
        return true;
    }

    private boolean promote(int x){
        if(mRenderer.board.pieceLocation[mRenderer.board.getCurrentPlayer()*14+x] == -1) {
            mRenderer.board.pieceLocation[mRenderer.board.board[157]] = -1;
            mRenderer.board.pieceLocation[mRenderer.board.getCurrentPlayer() * 14 + x] = 157;
            mRenderer.phase = GLRenderer.Phase.Start;
            mRenderer.board.nextPlayer();
            return true;
        }
        return false;
    }
    private int mapPromote(float x, float y) {
        int minP = 157; //the closest piece set to Vertex
        if(y>=0.8&&y<=0.9){
            if (x>=-0.55&&x<=-0.45){
                return 0;
            }else if(x>=-0.40&&x<=-0.30){
                return 5;
            }else if(x>=-0.25&&x<=-0.15){
                return 1;
            }else if(x>=-0.10&&x<=0.0){
                return 6;
            }else if(x>=0.05&&x<=0.15){
                return 7;
            }else if(x>=0.20&&x<=0.30){
                return 3;
            }else if(x>=0.35&&x<=0.45){
                return 9;
            }else if(x>=0.5&&x<=0.6){
                return 10;
            }
        }
        return minP;
    }


    //map the position to a board space
    //return -1 if not on board
    //return -2 if is on bottom
    private int mapBoard(float x, float y) {
        //Log.d("boardX", String.valueOf(x));
       //Log.d("boardY", String.valueOf(y));
        float minD = distance(x,y,0,0);
        int minP = 157; //the closest piece set to Vertex
        if(x <= 0.16 && x >= -0.16&&y>=0.78&&y<=0.95){
            return -2;
        }
        if (x > 0.65 || y > 0.65 || x < -0.65 || y < -0.65){
            return -1;
        }else{
            Matrix m = new Matrix();
            for(int r = 0; r < 6; r++) {
                int rotate = (r)*60;
                for (int i = 1; i <= 26; i++) {
                    m.setTranslate(DrawBoard.translation[i * 2],DrawBoard.translation[i * 2+1]);
                    //float[] values = new float[9];
                    m.postRotate(rotate);
                    float[] values = new float[9];
                    m.getValues(values);
                    float mx = values[m.MTRANS_X];
                    float my = values[m.MTRANS_Y];
                    float d = distance(x, y, mx, my);
                    if (d < minD) {
                        minD = d;
                        if( i <= 20){
                            minP =  20*r + i;
                        }else{
                            minP =  120 + 6*r + (i - 20);
                        }
                    }
                }
            }
            return minP;
        }
    }

    //return the distance between x,y and vx, vy
    private float distance(float x, float y, float vx, float vy) {
        return (float) Math.sqrt( Math.pow(x - vx, 2) + Math.pow(y - vy, 2) );
    }


}
