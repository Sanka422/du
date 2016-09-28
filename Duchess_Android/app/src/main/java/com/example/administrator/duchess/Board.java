package com.example.administrator.duchess;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by Administrator on 2016-8-14.
 */
public class Board {
    public int[] pieceLocation;
    public int[] board;
    private int currentPlayer;
    public int placeSelected;
    public int piecePlaced;
    public ArrayList<Integer> validMove;
    byte size;
    /*
    0   Rook
    1   Wizard
    2   King
    3   Fortess
    4   Rook
    5   Bishop
    6   Knight
    7   Queen
    8   Bishop
    9   Duchess
    10-14 Pawn
    90  empty
    91  invalid
     */
    Board(byte size){
        this.size = size;
        if(size == 6){
            pieceLocation = new int[15*size];
            for(short i = 0; i < size; i++)
                for (short j = 0; j <15; j++)
                    pieceLocation[15*i+j] = (short)(i*20+1+j);
        }
        board = new int[158];
        for(short i = 0; i< 158;i++)
            board[i] = 90;
        for(int i = 0; i < 6; i++){
            for (int j = 1; j< 16;j++){
                board[i*20+j] = i*15+j-1;
            }
        }
        placeSelected = -1;
        piecePlaced = -1;
    }

    public int[] getPieceLocation() {
        return pieceLocation;
    }

    public ArrayList<Integer> calculateValidMove() {
        validMove= new ArrayList<Integer>();
        int selected = placeSelected;
        validMove.addAll(teleport(selected));
        switch(board[selected]%15){
            case 0:case 4:
                validMove.addAll(verticalHorizontal(selected,0));
                break;
            case 1://wizard, teleport un done
                validMove.addAll(verticalHorizontal(selected,1));
                validMove.addAll(diagonal(selected,1));
                break;
            case 2:
                validMove.addAll(verticalHorizontal(selected,1));
                validMove.addAll(diagonal(selected,1));
                break;
            case 3:
                validMove.addAll(verticalHorizontal(selected,0));
                validMove.addAll(knightMove(selected,0));
                break;
            case 5:case 8:
                validMove.addAll(diagonal(selected,0));
                break;
            case 6:
                validMove.addAll(knightMove(selected,0));
                break;
            case 7:
                validMove.addAll(verticalHorizontal(selected,0));
                validMove.addAll(diagonal(selected,0));
                break;
            case 9:
                validMove.addAll(diagonal(selected,0));
                validMove.addAll(knightMove(selected,0));
                break;
            case 10:case 11:case 12:case 13:case 14://pawn
                validMove.addAll(pawnMove(selected));
                break;
        }

        //validMove.addAll(verticalHorizontal(selected,0));
        //validMove.addAll(diagonal(selected,0));
        //validMove.addAll(knightMove(selected,0));
        return validMove;
    }

    //return all possible teleport
    private Collection<? extends Integer> teleport(int selected) {
        validMove= new ArrayList<Integer>();
        if(wizardNearBy(selected)){
            validMove.addAll(teleport_to());
        }
        return validMove;
    }

    private Collection<? extends Integer> teleport_to() {
        ArrayList<Integer> validMove = new ArrayList<Integer>();
        for(int i = 0; i < 6; i++) {
            if (i%2 == currentPlayer%2) {
                ArrayList<Integer> surround = getSurround(pieceLocation[i*15+1]);
                for (int j: surround){
                    if (board[j] == 90){
                        validMove.add(j);
                    }
                }
            }
        }
        return validMove;
    }

    private boolean wizardNearBy(int selected) {
        ArrayList<Integer> surround = getSurround(selected);
        for (int i: surround){
            if(isFriendlyWizard(i)){
                return true;
            }
        }
        return false;
    }

    private ArrayList<Integer> getSurround(int selected) {
        ArrayList<Integer> validMove = new ArrayList<Integer>();
        validMove.addAll(verticalHorizontal(selected,2));
        validMove.addAll(diagonal(selected,2));
        return validMove;
    }

    private boolean isFriendlyWizard(int i) {
        if (board[i]%15 == 1){
            if(!hostile(board[i])){
                return true;
            }
        }
        return false;
    }


    //return true if valid move and will not take a piece, add i to validmove
    //return false if valid move and will take a piece, add i to validmove
    //return false if not valid move
    //type 1 will always return false;
    //type 2 will always return false but always add pos(if it is not invalid(state))
    private boolean valid(ArrayList<Integer> validMove, int i,int type) {
        if(board[i] == 91){
            return false;
        }else if(board[i] == 90){
            validMove.add(i);
            if (type == 0) {
                return true;
            }else{
                return false;
            }
        }else if(hostile(board[i])){
            validMove.add(i);
            return false;
        }else{
            if(type == 2){
                validMove.add(i);
                return false;
            }
        }
        return false;
    }

    //if piece ID is hostile to current player
    private boolean hostile(int pieceID) {;
        if (pieceID/15%2 != currentPlayer%2){
            return true;
        }else{
            return false;
        }
    }

    private Collection<? extends Integer> pawnMove(int selected) {
        ArrayList<Integer> validMove= new ArrayList<Integer>();
        ArrayList<Integer> rMove= verticalHorizontal(selected,1);
        ArrayList<Integer> bMove= diagonal(selected,1);
        for(int i: rMove){
            if (board[i] == 90){
                validMove.add(i);
            }
        }
        for(int i: bMove){
            if (board[i] != 90){
                validMove.add(i);
            }
        }
        return validMove;
    }

    //type 0 for find all, 1 for 1 space possible
    private ArrayList<Integer> verticalHorizontal(int selected, int type) {
        ArrayList<Integer> validMove= new ArrayList<Integer>();
        if (selected == 157){
            for(int i = 0; i < 6; i++) {
                if (!valid(validMove, 124 + i * 6,type)) {
                    continue;
                }
                if(type == 0) {
                    validMove.addAll(verticalDown(124 + i * 6, type));
                }
            }
            return validMove;
        }
        validMove.addAll(horizontalLeft(selected,type));
        validMove.addAll(horizontalRight(selected,type));
        validMove.addAll(verticalDown(selected,type));
        validMove.addAll(verticalUp(selected,type));
        return validMove;
    }

    //type 0 for find all, 1 for 1 space possible
    private ArrayList<Integer> horizontalLeft(int selected, int type) {
        ArrayList<Integer> validMove = new ArrayList<Integer>();
        if (selected < 121){
            for(int i = selected-1; (i+5)%5 != 0; i--) {
                if(!valid(validMove,i,type)) {
                    return validMove;
                }
            }
        }else{
            for(int i = selected-1; (i-120)%3 != 0; i--) {
                if(!valid(validMove,i,type)) {
                    return validMove;
                }
            }
            int row = (selected-121)%6/3;
            int preSlid = ((selected -121 )/6 +1 )%6;
            int t = preSlid*6+120-row+6;
            if(!valid(validMove,t,type)) {
                return validMove;
            }
            validMove.addAll(verticalDown(t,type));
        }
        return validMove;
    }

    //type 0 for find all, 1 for 1 space possible
    private ArrayList<Integer> horizontalRight(int selected, int type) {
        ArrayList<Integer> validMove = new ArrayList<Integer>();
        if (selected < 121){
            for(int i = selected+1; i%5 != 1; i++){
                if(!valid(validMove,i,type)) {
                    return validMove;
                }
            }
        }else{
            for(int i = selected+1; (i-120)%3 != 1; i++){
                if(!valid(validMove,i,type)) {
                    return validMove;
                }
            }
            int row = (selected-121)%6/3;
            int nextSlid = ((selected -121 )/6 +5 )%6;
            int t = nextSlid*20+16+row;
            if(!valid(validMove,t,type)) {
                return validMove;
            }
            validMove.addAll(verticalDown(t,type));
        }
        return validMove;
    }

    //type 0 for find all, 1 for 1 space possible
    private ArrayList<Integer> verticalDown(int selected, int type) {
        ArrayList<Integer> validMove = new ArrayList<Integer>();
        for (int i = down(selected); i != 0; i = down(i)){
            if(!valid(validMove,i,type)) {
                return validMove;
            }
        }
        return validMove;
    }

    //type 0 for find all, 1 for 1 space possible
    private ArrayList<Integer> verticalUp(int selected, int type) {
        ArrayList<Integer> validMove = new ArrayList<Integer>();
        int slid;
        int col;
        int rowT;
        if (selected < 121) {
            for (int i = selected+5; (i-6) %20/5!=3;i+=5) {
                if(!valid(validMove,i,type)) {
                    return validMove;
                }
            }
            slid = (selected-1)/20;
            col = ((selected+2))%5;
            rowT = 0;
        }else{
            slid = (selected-121)/6;
            col = ((selected-121))%3;
            rowT = (selected-121)%6/3+1;
        }
        if (col == 0 || col == 1 || col == 2) {
            for (int i = slid * 6 + 121 + col+rowT*3; (i - 121) / 6 == slid; i += 3) {
                if(!valid(validMove,i,type)) {
                    return validMove;
                }
            }
        }
        if(col == 0){
            if(!valid(validMove,157,type)) {
                return validMove;
            }
            int n = 121+ (slid+3)%6*6+3;
            if(!valid(validMove,n,type)) {
                return validMove;
            }
            validMove.addAll(verticalDown(n,type));
        }else if(col == 1 || col == 2){
            int n = 121+ (slid+5)%6*6+3*(2-col);
            if(!valid(validMove,n,type)) {
                return validMove;
            }
            validMove.addAll(horizontalRight(n,type));
        }else{
            int n = 123+ (slid+1)%6*6+3*(col-3);
            if(!valid(validMove,n,type)) {
                return validMove;
            }
            validMove.addAll(horizontalLeft(n,type));
        }
        return validMove;
    }

    //type 0 for find all, 1 for 1 space possible
    private ArrayList<Integer>  diagonal(int selected,int type){
        ArrayList<Integer> validMove= new ArrayList<Integer>();
        if (selected == 157){
            for(int i = 0; i < 6; i++) {
                if (!valid(validMove, 125 + i * 6,type)) {
                    continue;
                }
                if(type == 0) {
                    validMove.addAll(diagonalRightDown(125 + i * 6, type));
                }
            }
            return validMove;
        }
        validMove.addAll(diagonalLeftDown(selected,type));
        validMove.addAll(diagonalRightDown(selected,type));
        validMove.addAll(diagonalLeftUp(selected,type));
        validMove.addAll(diagonalRightUp(selected,type));
        return validMove;
    }

    private Collection<? extends Integer> diagonalLeftDown(int selected, int type) {
        ArrayList<Integer> validMove= new ArrayList<Integer>();
        if(selected<121){
            for(int i = selected-6; (i+4)%5!=4&&i-(selected-1)/20*20>=0;i-=6){
                if(!valid(validMove,i,type)) {
                    return validMove;
                }
            }
        }else{
            if ((selected-121)%6 == 3){
                int slid = (selected-121)/6;
                if(!valid(validMove,121+6*((slid+1)%6)+5,type)) {
                    return validMove;
                }
                if(!valid(validMove,20*slid+16,type)) {
                    return validMove;
                }
                return validMove;
            }
            for(int i = selected-4;i-(selected-121)/6*6>=121;i-=4){
                if(!valid(validMove,i,type)) {
                    return validMove;
                }
                selected = i;
            }
            int slid = (selected-121)/6;
            int col = (selected-121)%3;
            for(int i = slid*20+col+17;(i+4)%5!=4&&i-slid*20>=0;i-=6){
                if(!valid(validMove,i,type)) {
                    return validMove;
                }
            }
        }
        return validMove;
    }

    private Collection<? extends Integer> diagonalRightDown(int selected, int type) {
        ArrayList<Integer> validMove= new ArrayList<Integer>();
        if(selected<121){
            for(int i = selected-4; i%5!=1&&i-(selected-1)/20*20>0;i-=4){
                if(!valid(validMove,i,type)) {
                    return validMove;
                }
            }
        }else{
            if ((selected-121)%6 == 5){
                int slid = (selected-121)/6;
                if(!valid(validMove,20*((slid+5)%6)+16,type)) {
                    return validMove;
                }
                return validMove;
            }
            for(int i = selected-2;i-(selected-121)/6*6>=121&&(i-121)%3!=0;i-=2){
                if(!valid(validMove,i,type)) {
                    return validMove;
                }
                selected = i;
            }
            int slid = (selected-121)/6;
            int col = (selected-121)%3;
            for(int i = slid*20+col+19;i%20%5!=1&&i-slid*20>=0;i-=4){
                if(!valid(validMove,i,type)) {
                    return validMove;
                }
            }
        }
        return validMove;
    }

    private Collection<? extends Integer> diagonalLeftUp (int selected, int type) {
        ArrayList<Integer> validMove= new ArrayList<Integer>();
        if(selected<121){
            int slid = (selected-1)/20;
            for(int i = selected+4; (i-4)%5!=1&&i-slid*20<=20;i+=4){
                if(!valid(validMove,i,type)) {
                    return validMove;
                }
                selected = i;
            }
            int col = (selected)%20;
            if(col == 16){
                int n = 20+(slid+1)%6*20;
                if(!valid(validMove,n,type)) {
                    return validMove;
                }
                validMove.addAll(diagonalLeftDown(n,type));
            }else if(col == 17||col==18){
                int n = 123+6*((slid+1)%6)+(col-17)*3;
                if(!valid(validMove,n,type)) {
                    return validMove;
                }
                validMove.addAll(diagonalLeftDown(n,type));
            }else if(col == 19){
                int n = 121+slid*6;
                int m = 125 + ((slid+1)%6)*6;
                if(!valid(validMove,n,type)) {
                    return validMove;
                }
                if(!valid(validMove,m,type)) {
                    return validMove;
                }
                validMove.addAll(diagonalLeftDown(m,type));
            }else if(col == 0){
                int n = 122+slid*6;
                int m = 124 + slid*6;
                int o = 124 + ((slid+1)%6)*6;
                if(!valid(validMove,n,type)) {
                    return validMove;
                }
                if(!valid(validMove,m,type)) {
                    return validMove;
                }
                if(!valid(validMove,o,type)) {
                    return validMove;
                }
                validMove.addAll(diagonalLeftDown(o,type));
            }
        }else{
            int slid = (selected-121)/6;
            if ((selected-121)%6 == 0){
                int n = 125 +6*((slid+1)%6);
                if(!valid(validMove,n,type)) {
                    return validMove;
                }
                validMove.addAll(diagonalLeftDown(n,type));
                return validMove;
            }
            for(int i = selected+2;(i-121)/6==slid&&(i-121)%3!=2;i+=2){
                if(!valid(validMove,i,type)) {
                    return validMove;
                }
                selected = i;
            }
            int col = (selected-121)%6;
            if (col == 3){
                int n = 124+((slid+1)%6)*6;
                if(!valid(validMove,n,type)) {
                    return validMove;
                }
                validMove.addAll(diagonalLeftDown(n,type));
            }else if(col == 4){
                if(!valid(validMove,157,type)) {
                    return validMove;
                }
                int n = 125+((slid + 3)%6)*6;
                if(!valid(validMove,n,type)) {
                    return validMove;
                }
                validMove.addAll(diagonalRightDown(n,type));
            }else if(col == 5){
                int n = 124+((slid+5)%6)*6;
                if(!valid(validMove,n,type)) {
                    return validMove;
                }
                validMove.addAll(diagonalRightUp(n,type));
            }
        }
        return validMove;
    }

    private Collection<? extends Integer> diagonalRightUp (int selected, int type) {
        ArrayList<Integer> validMove= new ArrayList<Integer>();
        if(selected<121){
            int slid = (selected-1)/20;
            for(int i = selected+6; i%5!=1&&i-slid*20<=20;i+=6){
                if(!valid(validMove,i,type)) {
                    return validMove;
                }
                selected = i;
            }
            int col = (selected)%20;
            if(col == 16){
                int n = 126+((slid+1)%6)*6;
                if(!valid(validMove,n,type)) {
                    return validMove;
                }
                validMove.addAll(diagonalLeftUp(n,type));
            }else if(col == 17){
                int n = 121+6*(slid);
                if(!valid(validMove,n,type)) {
                    return validMove;
                }
                int m = 125+6*(slid);
                if(!valid(validMove,m,type)) {
                    return validMove;
                }
                int o = 121+6*((slid+5)%6);
                if(!valid(validMove,o,type)) {
                    return validMove;
                }
                validMove.addAll(diagonalRightDown(o,type));
            }else if(col == 18){
                int n = 122+6*(slid);
                if(!valid(validMove,n,type)) {
                    return validMove;
                }
                int m = 126+6*(slid);
                if(!valid(validMove,m,type)) {
                    return validMove;
                }
                int o = 18+20*((slid+5)%6);
                if(!valid(validMove,o,type)) {
                    return validMove;
                }
                validMove.addAll(diagonalRightDown(o,type));
            }else if(col == 19){
                int n = 123+slid*6;
                int m = 17 + ((slid+5)%6)*20;
                if(!valid(validMove,n,type)) {
                    return validMove;
                }
                if(!valid(validMove,m,type)) {
                    return validMove;
                }
                validMove.addAll(diagonalRightDown(m,type));
            }else if(col == 0){
                int o = 16 + ((slid+5)%6)*20;
                if(!valid(validMove,o,type)) {
                    return validMove;
                }
                validMove.addAll(diagonalRightDown(o,type));
            }
        }else{
            int slid = (selected-121)/6;
            if ((selected-121)%6 == 2){
                int n = 17 +20*((slid+5)%6);
                if(!valid(validMove,n,type)) {
                    return validMove;
                }
                validMove.addAll(diagonalRightDown(n,type));
                return validMove;
            }
            for(int i = selected+4;(i-121)/6==slid&&(i-121)%3!=0;i+=2){
                if(!valid(validMove,i,type)) {
                    return validMove;
                }
                selected = i;
            }
            int col = (selected-121)%6;
            if (col == 3){
                int n = 124+((slid+5)%6)*6;
                if(!valid(validMove,n,type)) {
                    return validMove;
                }
                validMove.addAll(diagonalRightDown(n,type));
            }else if(col == 4){
                int n = 121+((slid + 5)%6)*6;
                if(!valid(validMove,n,type)) {
                    return validMove;
                }
                validMove.addAll(diagonalRightDown(n,type));
            }else if(col == 5){
                int n = 18+((slid+5)%6)*20;
                if(!valid(validMove,n,type)) {
                    return validMove;
                }
                validMove.addAll(diagonalRightDown(n,type));
            }
        }
        return validMove;
    }

    private Collection<? extends Integer> knightMove(int selected, int type) {
        ArrayList<Integer> validMove= new ArrayList<Integer>();
        if (selected == 157){

            for(int i = 0; i < 6; i++) {
                if (!valid(validMove, 122 + i * 6,type)) {
                    continue;
                }
                if (!valid(validMove, 126 + i * 6,type)) {
                    continue;
                }
            }
            return validMove;
        }
        validMove.addAll(knightDown(selected,type));
        validMove.addAll(knightRight(selected,type));
        validMove.addAll(knightLeft(selected,type));
        validMove.addAll(knightUp(selected,type));
        return validMove;
    }

    private Collection<? extends Integer> knightDown (int selected, int type) {
        ArrayList<Integer> validMove= new ArrayList<Integer>();
        int slid;
        int downTwo;
        int left;
        int right;
        if(selected < 121){
            slid = (selected-1)/20;
            downTwo = down(down(selected));
        }else{
            slid = (selected-121)/6;
            downTwo = down(down(selected));
        }
        if (downTwo - slid*20 >0){
            left = downTwo - 1;
            right = downTwo +1;
            if ((right-1)%5 != 0 ){
                valid(validMove,right,type);
            }
            if ((left-1)%5 != 4 && left >= 1){
                valid(validMove,left,type);
            }
        }
        return validMove;
    }

    private Collection<? extends Integer> knightRight (int selected, int type) {
        ArrayList<Integer> validMove= new ArrayList<Integer>();
        int rightTwo;
        int down = down(selected);
        //right down position
        if (down != 0) {
            if (down < 121) {
                rightTwo = down + 2;
                if (rightTwo % 5 != 1 && rightTwo % 5 != 2) {
                    valid(validMove,rightTwo,type);
                }
            } else {
                if ((down - 121) % 3 == 1) {
                    rightTwo = ((slid(down) + 5) % 6) * 20 + 16;
                } else if ((down - 121) % 3 == 0) {
                    rightTwo = down + 2;
                } else {
                    //(down - 121)%3 == 2
                    rightTwo = ((slid(down) + 5) % 6) * 20 + 11;
                }
                valid(validMove,rightTwo,type);
            }
        }
        //right up position
        if(selected < 121){
            if ((selected-1)%20<15){
                rightTwo = selected+7;
                if (rightTwo%5!= 1 && rightTwo%5!=2){
                    valid(validMove,rightTwo,type);
                }
            }else{
                if(selected%5 == 1||selected%5 == 2||selected%5 == 3){
                    rightTwo = slid(selected)*6+120+selected%5;
                }else{
                    //selected%5 == 0/4
                    rightTwo = ((slid(selected)+5)%6)*20+16-((selected-1)%5-3)*5;
                }
                valid(validMove,rightTwo,type);
            }
        }else{
            switch ((selected-121)%6){
                case 0:
                    rightTwo = selected+5;
                    valid(validMove,rightTwo,type);
                    break;
                case 3:
                    int r3 = ((slid(selected)+4)%6)*6+121;
                    int r4 = ((slid(selected)+5)%6)*6+121;
                    valid(validMove,r3,type);
                    valid(validMove,r4,type);
                    break;
                case 1:case 2:case 4:case 5:
                    int row = (selected-121)%6/3;
                    rightTwo = 22 + ((slid(selected)+5)%6)*20+row-((selected-121)%3)*5;
                    valid(validMove,rightTwo,type);
                    break;
            }
        }
        return validMove;
    }

    private Collection<? extends Integer> knightLeft (int selected, int type) {
        ArrayList<Integer> validMove= new ArrayList<Integer>();
        int rightTwo = 0;
        int down = down(selected);
        //left down position
        if (down != 0) {
            if (down < 121) {
                rightTwo = down - 2;
                if (rightTwo % 5 != 0 && rightTwo % 5 != 4) {
                    valid(validMove,rightTwo,type);
                }
            } else {
                if ((down - 121) % 3 == 1) {
                    rightTwo = ((slid(down) + 1) % 6) * 6 + 126;
                } else if ((down - 121) % 3 == 0) {
                    rightTwo = ((slid(down) + 1) % 6) * 6 + 123;
                } else {
                    //(down - 121)%3 == 2
                    rightTwo = down - 2;
                }
                valid(validMove,rightTwo,type);
            }
        }
        //left up position
        if(selected < 121){
            if ((selected-1)%20<15){
                rightTwo = selected+3;
                if (rightTwo%5!= 0 && rightTwo%5!=4){
                    valid(validMove,rightTwo,type);
                }
            }else{
                switch (selected%5){
                    case 0:
                        rightTwo = slid(selected)*6+121;
                        break;
                    case 3:case 4:
                        rightTwo = ((slid(selected)+1)%6)*6+123+(selected%5-3)*3;
                        break;
                    case 1:case 2:
                        rightTwo = ((slid(selected)+1)%6)*20+15+(selected%5-1)*5;
                        break;
                }
                valid(validMove,rightTwo,type);
            }
        }else{
            switch ((selected-121)%6){
                case 2:
                    rightTwo = selected+1;
                    valid(validMove,rightTwo,type);
                    break;
                case 3:
                    int r1 = ((slid(selected)+1)%6)*6+121;
                    int r2 = ((slid(selected)+2)%6)*6+121;
                    valid(validMove,r1,type);
                    valid(validMove,r2,type);
                    break;
                case 5:
                    valid(validMove,157,type);
                    break;
                case 4:
                    r1 = ((slid(selected) + 1) % 6) * 6 + 124;
                    r2 = ((slid(selected) + 2) % 6) * 6 + 124;
                    valid(validMove,r1,type);
                    valid(validMove,r2,type);
                    break;
                case 1:case 0:
                    rightTwo = 122 + ((slid(selected)+1)%6)*6+(selected-121)%6*3;
                    valid(validMove,rightTwo,type);
                    break;
            }
        }
        return validMove;
    }

    private Collection<? extends Integer> knightUp (int selected, int type) {
        ArrayList<Integer> validMove= new ArrayList<Integer>();
        int up = selected+10;
        if(slid(up) == slid(selected)){
            int right = up +1;
            int left = up - 1;
            if(right%5 != 1){
                valid(validMove,right,type);
            }
            if(left%5 != 0){
                valid(validMove,left,type);
            }
        }else{
            if(selected<121){
                int row = (selected-1)%20/5-2;
                int col = (selected-1)%5;
                int right;
                int left;
                if (col == 0){
                    left = 20 + ((slid(selected)+1)%6)*20 - row;
                    right = 126 + ((slid(selected)+1)%6)*6 - row;
                }else if (col == 1){
                    left = 123 + ((slid(selected)+1)%6)*6 - row;
                    right = 121 + slid(selected)*6 + 3*row;
                }else if(col == 2){
                    right = 122 + slid(selected)*6 + 3*row;
                    left = 126 + ((slid(selected)+1)%6)*6 - row;
                }else if(col == 3) {
                    right = 123 + slid(selected)*6 + 3*row;
                    left = 121 + slid(selected)*6 + 3*row;
                }else{
                    right = 16 + ((slid(selected)+5)%6)*20 + row;
                    left = 122 + slid(selected)*6 + 3*row;
                }
                valid(validMove,right,type);
                valid(validMove,left,type);
            }else{
                int l,r;
                switch ((selected-121)%6){
                    case 0:
                        int r1 = ((slid(selected)+1)%6)*6+124;
                        int r2 = ((slid(selected)+2)%6)*6+124;
                        int r3 = ((slid(selected)+4)%6)*6+124;
                        int r4 = ((slid(selected)+5)%6)*6+124;
                        valid(validMove,r1,type);
                        valid(validMove,r2,type);
                        valid(validMove,r3,type);
                        valid(validMove,r4,type);
                        break;
                    case 1:
                        valid(validMove,157,type);
                        r = ((slid(selected)+5)%6)*6+121;
                        valid(validMove,r,type);
                        break;
                    case 2:
                        l = ((slid(selected)+5)%6)*6+124;
                        valid(validMove,l,type);
                        r = ((slid(selected)+5)%6)*20+18;
                        valid(validMove,r,type);
                        break;
                    case 3:
                        l = 125+ ((slid(selected)+3)%6)*6;
                        r = 125+ ((slid(selected)+4)%6)*6;
                        int l1 = 125+((slid(selected)+2)%6)*6;
                        int l2 = 125+((slid(selected)+5)%6)*6;
                        valid(validMove,l,type);
                        valid(validMove,r,type);
                        valid(validMove,l1,type);
                        valid(validMove,l2,type);
                        break;
                    case 4:
                        l = 124+ ((slid(selected)+4)%6)*6;
                        int ll = 124 + ((slid(selected)+3)%6)*6;
                        r = 122+ ((slid(selected)+5)%6)*6;
                        valid(validMove,l,type);
                        valid(validMove,ll,type);
                        valid(validMove,r,type);
                        break;
                    case 5:
                        l = 125+ ((slid(selected)+5)%6)*6;
                        r = 19+ ((slid(selected)+5)%6)*20;
                        valid(validMove,l,type);
                        valid(validMove,r,type);
                        break;
                }
            }
        }
        return validMove;
    }

    //return the 1 down pos of the input pos, return 0 if no possible down position
    private int down(int pos){
        if(pos <121){
            if(pos-5 >= slid(pos)*20){
                return pos-5;
            }else{
                return 0;
            }
        }else{
            if(pos-3 >= 121+slid(pos)*6){
                return pos-3;
            }else{
                int col = (pos - 121)%3;
                return slid(pos)*20+18+col;
            }
        }
    }


    //return the slid the pos is in
    private int slid(int pos){
        if(pos<121){
            return (pos-1)/20;
        }else {
            return (pos-121)/6;
        }
    }

    public void makeMove() {
        int from = placeSelected;
        int to = piecePlaced;
        Log.d("to", String.valueOf(to));
        if(board[to]!=90) {
            pieceLocation[board[to]] = -1;
        }
        pieceLocation[board[from]] = to;
        board[to] = board[from];
        board[from] = 90;
    }

    public void nextPlayer(){
        currentPlayer = (currentPlayer+1)%size;
    }

    public boolean promoteValid() {
        if(piecePlaced == 157 && board[piecePlaced] <= currentPlayer*15+14 &&board[piecePlaced] >= currentPlayer*15+10){
            for(int i = 0; i<10;i++){
                if(pieceLocation[i+currentPlayer*15] == -1){
                    return true;
                }
            }
        }
        return false;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(int currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public boolean own(int i) {
        if (i/15 == currentPlayer){
            return true;
        }else{
            return false;
        }
    }
}
