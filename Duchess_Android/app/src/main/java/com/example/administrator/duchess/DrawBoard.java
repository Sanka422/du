package com.example.administrator.duchess;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.opengl.Matrix;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.Random;

/**
 * Created by Administrator on 2016-8-14.
 */
public class DrawBoard {
    GLRenderer render;
    private BoardFan[] boardBase;
    private final float[] mMVPMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];
    Context mContext;
    public static float uvs[];
    public FloatBuffer uvBuffer;
    public TextManager tm;
    public TextManager po;
    private final float color[][] = {
            {0,0,1,1},    //blue
            {1,0,1,1},    //magenta
            {0,1,0,1},    //green
            {1,0,0,1},    //red
            {0,1,1,1},    //clan
            {1,1,0,1},    //yellow
            {0.37f,0.62f,0.63f,1f},
            {0f,0.5f,0.5f,0.8f},
            {1,1,1,1},         //black 8
            {0,0,0,1},          //white 9
            {0.5f,0.f,0.0f,1f},       //10
            {0.5f,0.5f,0.5f,1f}
    };
    public static final float translation[] = {
            0,0,
            -0.210f,    0.549f, //1
            -0.111f,    0.590f, //2
            0f,         0.592f, //3
            0.111f,     0.590f, //4
            0.210f,     0.549f, //5
            -0.186f,    0.485f, //6
            -0.100f,    0.485f, //7
            0f,         0.485f, //8
            0.100f,     0.485f, //9
            0.186f,     0.485f, //10
            -0.170f,    0.415f, //11
            -0.090f,    0.415f, //12
            0f,         0.415f, //13
            0.090f,     0.415f, //14
            0.170f,     0.415f, //15
            -0.155f,    0.330f, //16
            -0.085f,    0.315f, //17
            0f,         0.310f, //18
            0.085f,     0.315f, //19
            0.155f,     0.330f, //20
            0f,         0.210f, //121
            0.085f,     0.220f, //122
            0.145f,     0.240f, //123
            0f,         0.130f, //124
            0.085f,     0.140f, //125
            0.145f,     0.170f  //126
    };
    private short drawRookOrder[] ={0,1,2,0,2,3};
    float rookCoords[] = {
            -0.025f,0.025f,0.0f,
            -0.025f,-0.025f,0.0f,
            0.025f, -0.025f,0.0f,
            0.025f,0.025f,0.0f
    };
    private Piece rook;
    private short drawBishopOrder[] ={0,1,2};
    float bishopCoords[] = {
            0.025f,0.025f,0.0f,
            -0.025f,0.025f,0.0f,
            0.0f, -0.025f,0.0f
    };
    private Piece bishop;
    private short drawKnightOrder[] ={0,2,3,0,3,1,0,1,4,0,4,5};
    float knightCoords[] = {
            -0.025f,-0.025f,0.0f,
            0f,0f,0f,
            -0.025f, 0.025f,0.0f,
            0f,0.025f,0f,
            0.025f,0f,0f,
            0.025f,-0.025f,0f
    };
    private  Piece knight;
    private short drawQueenOrder[] ={0,1,2,0,2,3,0,3,4,0,4,5,0,5,6,0,6,7,0,7,8,
            0,8,9,0,9,10,0,10,11,0,11,12,0,12,13,0,13,14,0,14,15,0,15,16,0,16,1};
    float queenCoords[] = {
            0f,0f,0f,
            -0.025f,0f,0.0f,    //left
            -0.012f,0.006f,0f,
            -0.02f,0.02f,0f,    //
            -0.006f,0.012f,0f,
            0f,0.025f,0f,       //up
            0.006f,0.012f,0f,
            0.02f,0.02f,0f,     //
            0.006f,0.012f,0f,
            0.025f,0f,0f,       //right
            0.012f,-0.006f,0f,
            0.02f,-0.02f,0f,    //
            0.006f,-0.012f,0f,
            0f,-0.025f,0f,      //down
            -0.006f,-0.012f,0f,
            -0.02f,-0.02f,0f,   //
            -0.012f,-0.006f,0f
    };
    private Piece queen;
    private short drawDuchessOrder[] ={0,1,2,3,4,5,6,7,8,9,10,11};
    float duchessCoords[] = {
            0.03f,0.03f,0f,
            0.008f,0.03f,0f,
            0.017f,0.008f,0f,//
            -0.03f,0.03f,0f,
            -0.008f,0.03f,0f,
            -0.017f,0.008f,0f,//
            0f,-0.03f,0f,
            0.015f,-0.005f,0f,
            -0.015f,-0.005f,0f,//
            0,-0.015f,0f,
            0.015f,0.02f,0f,
            -0.015f,0.02f,0f
    };
    private Piece duchess;
    private short drawFortressOrder[] ={0,1,2,1,2,3,4,5,6,5,6,7,8,9,10,9,10,11,12,13,14,13,14,15,
            16,17,18,16,18,19};
    float fortressCoords[] = {
            0.03f,0.03f,0f,
            0.008f,0.03f,0f,
            0.03f,0.008f,0f,
            0.008f,0.008f,0f,//
            -0.03f,0.03f,0f,
            -0.008f,0.03f,0f,
            -0.03f,0.008f,0f,
            -0.008f,0.008f,0f,//
            0.03f,-0.03f,0f,
            0.008f,-0.03f,0f,
            0.03f,-0.008f,0f,
            0.008f,-0.008f,0f,//
            -0.03f,-0.03f,0f,
            -0.008f,-0.03f,0f,
            -0.03f,-0.008f,0f,
            -0.008f,-0.008f,0f,//
            -0.015f,0.015f,0f,
            -0.015f,-0.015f,0f,
            0.015f,-0.015f,0f,
            0.015f,0.015f,0f
    };
    private Piece fortress;
    private short drawKingOrder[] ={0,1,2,0,2,4,2,3,4};
    float kingCoords[] = {
            0.015f,0.025f,0f,
            0.03f,0f,0f,
            0f,-0.03f,0f,
            -0.03f,0f,0f,
            -0.015f,0.025f,0f

    };
    private Piece king;
    private short drawpawnOrder[] ={0,1,2,0,2,3,0,3,4,0,4,5,0,5,6,0,6,7,0,7,8,
            0,8,9,0,9,10,0,10,11,0,11,12,0,12,13,0,13,14,0,14,15,0,15,16,0,16,1};
    float pawnCoords[] = {
            0f,0f,0f,
            -0.013f,0f,0.0f,    //left
            -0.012f,0.006f,0f,
            -0.009f,0.009f,0f,    //
            -0.006f,0.012f,0f,
            0f,0.013f,0f,       //up
            0.006f,0.012f,0f,
            0.009f,0.009f,0f,     //
            0.012f,0.006f,0f,
            0.013f,0f,0f,       //right
            0.012f,-0.006f,0f,
            0.009f,-0.009f,0f,    //
            0.006f,-0.012f,0f,
            0f,-0.013f,0f,      //down
            -0.006f,-0.012f,0f,
            -0.009f,-0.009f,0f,   //
            -0.012f,-0.006f,0f
    };
    private Piece pawn;
    private short drawWizardOrder[] ={
            0,1,2,1,2,3,
            2,3,4,3,4,5,
            4,5,6,5,6,7,
            6,7,8,7,8,9,
            8,9,10,9,10,11,
            10,11,12,11,12,13,
            12,13,14,13,14,15,
            14,15,16,15,16,17,
            16,17,18,17,18,19,
            18,19,20,19,20,21,
            20,21,22,21,22,23,
            22,23,24,23,24,25,
            24,25,26,25,26,27,
            26,27,28,27,28,29,
            28,29,30,29,30,31,
            30,31,0,31,0,1
    };
    float wizardCoords[] = {
            -0.013f,0f,0f,    //left
            -0.039f,0f,0f,
            -0.012f,0.006f,0f,
            -0.036f,0.018f,0f,
            -0.009f,0.009f,0f,    //
            -0.027f,0.027f,0f,
            -0.006f,0.012f,0f,
            -0.018f,0.036f,0f,
            0f,0.013f,0f,       //up
            0f,0.039f,0f,
            0.006f,0.012f,0f,
            0.018f,0.036f,0f,
            0.009f,0.009f,0f,     //
            0.027f,0.027f,0f,
            0.012f,0.006f,0f,
            0.036f,0.018f,0f,
            0.013f,0f,0f,       //right
            0.039f,0f,0f,
            0.012f,-0.006f,0f,
            0.036f,-0.018f,0f,
            0.009f,-0.009f,0f,    //
            0.027f,-0.027f,0f,
            0.006f,-0.012f,0f,
            0.018f,-0.036f,0f,
            0f,-0.013f,0f,      //down
            0f,-0.039f,0f,
            -0.006f,-0.012f,0f,
            -0.018f,-0.036f,0f,
            -0.009f,-0.009f,0f,   //
            -0.027f,-0.027f,0f,
            -0.012f,-0.006f,0f,
            -0.036f,-0.018f,0f
    };
    private Piece wizard;
    private short drawButtomOrder[] ={
            0,1,2,0,2,3
    };
    float buttomCoords[] = {
            -0.15f,0.07f,0f,    //left
            -0.15f,-0.07f,0f,
            0.15f,-0.07f,0f,
            0.15f,0.07f,0f

    };
    private Piece buttom;
    private short promoteBoardOrder[] ={
            0,1,2,0,2,3
    };
    float promoteBoardCoords[] = {
            -0.6f,0.12f,0f,    //left
            -0.6f,-0.12f,0f,
            0.6f,-0.12f,0f,
            0.6f,0.12f,0f

    };
    private Piece promoteBoard;
    private short promoteButtomOrder[] ={
            0,1,2,0,2,3
    };
    float promoteButtomCoords[] = {
            -0.05f,0.05f,0f,    //left
            -0.05f,-0.05f,0f,
            0.05f,-0.05f,0f,
            0.05f,0.05f,0f

    };
    private Piece promoteButtom;

    public DrawBoard(Context c, GLRenderer glRenderer){
        render = glRenderer;
        mContext = c;
        rook = new Piece(drawRookOrder,rookCoords);
        bishop = new Piece(drawBishopOrder,bishopCoords);
        knight = new Piece(drawKnightOrder,knightCoords);
        queen = new Piece(drawQueenOrder,queenCoords);
        duchess = new Piece(drawDuchessOrder,duchessCoords);
        fortress = new Piece(drawFortressOrder,fortressCoords);
        king = new Piece(drawKingOrder,kingCoords);
        pawn = new Piece(drawpawnOrder,pawnCoords);
        wizard = new Piece(drawWizardOrder,wizardCoords);
        boardBase = new BoardFan[6];
        buttom = new Piece(drawButtomOrder,buttomCoords);
        promoteBoard = new Piece(promoteBoardOrder,promoteBoardCoords);
        promoteButtom = new Piece(promoteButtomOrder,promoteButtomCoords);
        for(int i = 0; i< 6; i++)
            boardBase[i]   = new BoardFan();

        SetupImage();
        SetupText();
    }

    public void draw(float[] mViewMatrix, float[] mProjectionMatrix, int[] pieceLocation, Board board, float[] mt) {
        float[] scratch = new float[16];

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        //draw empty board
        for(int i = 0; i < 6; i++){
            int angle = i* 60;
            Matrix.setRotateM(mRotationMatrix, 0, angle, 0, 0, 1.0f);
            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
            boardBase[0].drawBoard(scratch);
        }

        //draw buttom
        Matrix.setRotateM(mRotationMatrix, 0, 0, 0, 0, 1.0f);
        Matrix.translateM(mRotationMatrix, 0, -0f, -0.85f, 0);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        buttom.draw(scratch,color[8]);

        //draw colored board
        if (board.placeSelected != -1) {
            colorBoard(board.placeSelected,6);
        }

        if(board.validMove != null){
            drawValidMove(board);
        }

        if(board.piecePlaced != -1){
            colorBoard(board.piecePlaced,10);
        }
        //draw piece
        for(short n = 0; n < pieceLocation.length; n++){
            int rotate;
            int position;
            int l = pieceLocation[n];
            //when off board
            if(l == -1){
                continue;
            }
            if (l < 121){
                rotate = ((l-1)/20 +3)%6 *60;
                position = (l-1)%20+1;
            }else if(l == 157) {
                rotate = 0;
                position = 0;
            }else {
                rotate = ((l - 121) / 6 + 3) %6 * 60;
                position = 21+(l-121)%6;
            }
            Matrix.setRotateM(mRotationMatrix, 0, rotate, 0, 0, 1.0f);
            Matrix.translateM(mRotationMatrix, 0, translation[position*2], translation[1+position*2], 0);
            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
            int k = n / 15;
            float[] c = color[k];
            switch (n%15){
                case 10:case 11:case 12:case 13:case 14:
                    pawn.draw(scratch,c);
                    break;
                case 5:case 8:
                    bishop.draw(scratch,c);
                    break;
                case 6:
                    knight.draw(scratch,c);
                    break;
                case 7:
                    queen.draw(scratch,c);
                    break;
                case 9:
                    duchess.draw(scratch,c);
                    break;
                case 0:case 4:
                    rook.draw(scratch,c);
                    break;
                case 1:
                    wizard.draw(scratch,c);
                    break;
                case 2:
                    king.draw(scratch,c);
                    break;
                case 3:
                    fortress.draw(scratch,c);
                    break;
                default:
                    break;
            }
        }
        Matrix.setRotateM(mRotationMatrix, 0, 0, 0, 0, 1.0f);
        Matrix.translateM(mRotationMatrix, 0, 0, 0, 0);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);

        tm.Draw(mt);

        if(render.phase == GLRenderer.Phase.Promote){
            drawPromote(mt);
        }
    }

    private void drawPromote(float[] mt) {

        float[] scratch = new float[16];
        Matrix.setRotateM(mRotationMatrix, 0, 0, 180, 0, 1.0f);
        Matrix.translateM(mRotationMatrix, 0, -0.025f, -0.80f, 0);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        promoteBoard.draw(scratch,color[11]);
        Matrix.setRotateM(mRotationMatrix, 0, 180, 0, 0, 1.0f);
        Matrix.translateM(mRotationMatrix, 0, -0.50f, 0.85f, 0);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        promoteButtom.draw(scratch,color[8]);
        if(render.board.pieceLocation[render.board.getCurrentPlayer()*14+4] == -1 ||
                render.board.pieceLocation[render.board.getCurrentPlayer()*14] == -1 ) {
            rook.draw(scratch, color[render.board.getCurrentPlayer()]);
        }else{
            rook.draw(scratch, color[11]);
        }
        Matrix.setRotateM(mRotationMatrix, 0, 180, 0, 0, 1.0f);
        Matrix.translateM(mRotationMatrix, 0, -0.35f, 0.85f, 0);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        promoteButtom.draw(scratch,color[8]);
        if(render.board.pieceLocation[render.board.getCurrentPlayer()*14+5] == -1 ||
                render.board.pieceLocation[render.board.getCurrentPlayer()*14+8] == -1 ) {
            bishop.draw(scratch, color[render.board.getCurrentPlayer()]);
        }else{
            bishop.draw(scratch, color[11]);
        }
        Matrix.setRotateM(mRotationMatrix, 0, 180, 0, 0, 1.0f);
        Matrix.translateM(mRotationMatrix, 0, -0.20f, 0.85f, 0);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        promoteButtom.draw(scratch,color[8]);
        if(render.board.pieceLocation[render.board.getCurrentPlayer()*14+1] == -1) {
            wizard.draw(scratch,color[render.board.getCurrentPlayer()]);
        }else{
            wizard.draw(scratch, color[11]);
        }
        Matrix.setRotateM(mRotationMatrix, 0, 180, 0, 0, 1.0f);
        Matrix.translateM(mRotationMatrix, 0, -0.05f, 0.85f, 0);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        promoteButtom.draw(scratch,color[8]);
        if(render.board.pieceLocation[render.board.getCurrentPlayer()*14+6] == -1) {
            knight.draw(scratch,color[render.board.getCurrentPlayer()]);
        }else{
            knight.draw(scratch, color[11]);
        }
        Matrix.setRotateM(mRotationMatrix, 0, 180, 0, 0, 1.0f);
        Matrix.translateM(mRotationMatrix, 0, 0.10f, 0.85f, 0);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        promoteButtom.draw(scratch,color[8]);
        if(render.board.pieceLocation[render.board.getCurrentPlayer()*14+7] == -1) {
            queen.draw(scratch,color[render.board.getCurrentPlayer()]);
        }else{
            queen.draw(scratch, color[11]);
        }
        Matrix.setRotateM(mRotationMatrix, 0, 180, 0, 0, 1.0f);
        Matrix.translateM(mRotationMatrix, 0, 0.25f, 0.85f, 0);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        promoteButtom.draw(scratch,color[8]);
        if(render.board.pieceLocation[render.board.getCurrentPlayer()*14+3] == -1) {
            fortress.draw(scratch,color[render.board.getCurrentPlayer()]);
        }else{
            fortress.draw(scratch, color[11]);
        }
        Matrix.setRotateM(mRotationMatrix, 0, 180, 0, 0, 1.0f);
        Matrix.translateM(mRotationMatrix, 0, 0.40f, 0.85f, 0);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        promoteButtom.draw(scratch,color[8]);
        if(render.board.pieceLocation[render.board.getCurrentPlayer()*14+9] == -1) {
            duchess.draw(scratch,color[render.board.getCurrentPlayer()]);
        }else{
            duchess.draw(scratch, color[11]);
        }
        po.Draw(mt);
        Matrix.setRotateM(mRotationMatrix, 0, 180, 0, 0, 1.0f);
        Matrix.translateM(mRotationMatrix, 0, 0.55f, 0.85f, 0);
        Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
        promoteButtom.draw(scratch,color[8]);
        pawn.draw(scratch, color[render.board.getCurrentPlayer()]);
        po.Draw(mt);
    }

    private void drawValidMove(Board board) {
        for(int i : board.validMove){
            colorBoard(i,7);
        }
    }

    //get 4 point return the array for drawing on board(fan)
    private float[] makeQuad(int a, int b, int c, int d){
        float[] co = new float[12];
        co[0] = BoardFan.squareCoords[a*3];
        co[1] = BoardFan.squareCoords[a*3+1];
        co[2] = BoardFan.squareCoords[a*3+2];
        co[3] = BoardFan.squareCoords[b*3];
        co[4] = BoardFan.squareCoords[b*3+1];
        co[5] = BoardFan.squareCoords[b*3+2];
        co[6] = BoardFan.squareCoords[c*3];
        co[7] = BoardFan.squareCoords[c*3+1];
        co[8] = BoardFan.squareCoords[c*3+2];
        co[9] = BoardFan.squareCoords[d*3];
        co[10] = BoardFan.squareCoords[d*3+1];
        co[11] = BoardFan.squareCoords[d*3+2];
        return co;
    }

    //color the position on board with the c . the color index
    void colorBoard(int ps,int c){
        float[] scratch = new float[16];
        int rotate;
        int position;
        if( ps == 157) {
            float[] co = new float[21];
            short[] order = {
                    0, 1, 2,
                    0, 2, 3,
                    0, 3, 4,
                    0, 4, 5,
                    0, 5, 6,
                    0, 6, 1
            };
            co[0] = 0;
            co[1] = 0;
            co[2] = 0;
            for ( int i = 0; i < 6; i++) {
                android.graphics.Matrix m = new android.graphics.Matrix();
                m.setTranslate(BoardFan.squareCoords[0], BoardFan.squareCoords[1]);
                m.postRotate(i*60);
                float[] values = new float[9];
                m.getValues(values);
                float mx = values[m.MTRANS_X];
                float my = values[m.MTRANS_Y];
                co[(i+1)*3] = mx;
                co[(i+1)*3+1] = my;
                co[(i+1)*3+2] = 0;
            }
            Piece p;
            p = new Piece(order,co);
            Matrix.setRotateM(mRotationMatrix, 0, 0, 0, 0, 1.0f);
            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
            p.draw(scratch,color[c]);
        }else{
            if (ps < 121) {
                rotate = ((ps - 1) / 20 + 3) % 6 * 60;
                position = (ps - 1) % 20 + 1;
            } else {
                rotate = ((ps - 121) / 6 + 3) % 6 * 60;
                position = 21 + (ps - 121) % 6;
            }
            Piece p;
            float[] co = new float[12];
            short[] order = {0, 1, 2, 1, 2, 3};
            if (position <= 20) {
                int a = (position - 1) / 5;
                int b = (position - 1) % 5;
                co = makeQuad(35 - b - a * 6, 35 - b - a * 6 - 1, 35 - b - (a + 1) * 6, 35 - b - (a + 1) * 6 - 1);
            } else {
                if (position == 21 || position == 22) {
                    int b = (position - 1) % 5;
                    co = makeQuad(9 - b, 9 - b - 1, 9 - b - 5, 9 - b - 6);
                } else if (position == 23) {
                    co = makeQuad(7, 6, 2, 1);
                    android.graphics.Matrix m = new android.graphics.Matrix();
                    m.setTranslate(BoardFan.squareCoords[10 * 3], BoardFan.squareCoords[10 * 3 + 1]);
                    m.postRotate(-60);
                    float[] values = new float[9];
                    m.getValues(values);
                    float mx = values[m.MTRANS_X];
                    float my = values[m.MTRANS_Y];
                    co[9] = mx;
                    co[10] = my;
                    co[11] = 0;
                } else if (position == 24) {
                    co = makeQuad(4, 3, 1, 0);
                } else if (position == 25) {
                    co = makeQuad(3, 2, 0, 0);
                    android.graphics.Matrix m = new android.graphics.Matrix();
                    m.setTranslate(BoardFan.squareCoords[4 * 3], BoardFan.squareCoords[4 * 3 + 1]);
                    m.postRotate(-60);
                    float[] values = new float[9];
                    m.getValues(values);
                    float mx = values[m.MTRANS_X];
                    float my = values[m.MTRANS_Y];
                    co[9] = mx;
                    co[10] = my;
                    co[11] = 0;
                } else if (position == 26) {
                    co = makeQuad(10, 9, 5, 4);
                    rotate -= 60;
                }
            }
            p = new Piece(order,co);
            Matrix.setRotateM(mRotationMatrix, 0, rotate, 0, 0, 1.0f);
            Matrix.multiplyMM(scratch, 0, mMVPMatrix, 0, mRotationMatrix, 0);
            p.draw(scratch,color[c]);
        }
    }

    public void SetupText() {
        // Create our text manager
        tm = new TextManager();
        // Tell our text manager to use index 1 of textures loaded
        tm.setTextureID(1);
        // Pass the uniform scale
        tm.setUniformscale(2.0f);
        // Create our new textobject
        TextObject txt = new TextObject("Move", 285f, 50f);
        // Add it to our manager
        tm.addText(txt);
        // Prepare the text for rendering
        tm.PrepareDraw();

        // Create our text manager
        po = new TextManager();
        // Tell our text manager to use index 1 of textures loaded
        po.setTextureID(1);
        // Pass the uniform scale
        po.setUniformscale(2.0f);
        // Create our new textobject
        txt = new TextObject("Promote", 250f, 120f);
        // Add it to our manager
        po.addText(txt);
        // Prepare the text for rendering
        po.PrepareDraw();
    }

    public void SetupImage() {
        Random rnd = new Random();

        // 30 imageobjects times 4 vertices times (u and v)
        uvs = new float[30*4*2];

        // We will make 30 randomly textures objects
        for(int i=0; i<30; i++)
        {
            int random_u_offset = rnd.nextInt(2);
            int random_v_offset = rnd.nextInt(2);

            // Adding the UV's using the offsets
            uvs[(i*8) + 0] = random_u_offset * 0.5f;
            uvs[(i*8) + 1] = random_v_offset * 0.5f;
            uvs[(i*8) + 2] = random_u_offset * 0.5f;
            uvs[(i*8) + 3] = (random_v_offset+1) * 0.5f;
            uvs[(i*8) + 4] = (random_u_offset+1) * 0.5f;
            uvs[(i*8) + 5] = (random_v_offset+1) * 0.5f;
            uvs[(i*8) + 6] = (random_u_offset+1) * 0.5f;
            uvs[(i*8) + 7] = random_v_offset * 0.5f;
        }

        // The texture buffer
        ByteBuffer bb = ByteBuffer.allocateDirect(uvs.length * 4);
        bb.order(ByteOrder.nativeOrder());
        uvBuffer = bb.asFloatBuffer();
        uvBuffer.put(uvs);
        uvBuffer.position(0);

        // Generate Textures, if more needed, alter these numbers.
        int[] texturenames = new int[2];
        GLES20.glGenTextures(2, texturenames, 0);

        // Again for the text texture
        int id = mContext.getResources().getIdentifier("drawable/font", null, mContext.getPackageName());
        Bitmap bmp = BitmapFactory.decodeResource(mContext.getResources(), id);
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0 + 1);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, texturenames[1]);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR);
        GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);
        GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bmp, 0);
        bmp.recycle();
    }
}
