package com.example.rasel.myapplication;

/**
 * Created by rasel on 5/11/18.
 */

import java.util.ArrayList;

import static java.lang.Math.abs;

public class Chess {

    public int[][] chessBoard = {
            {-5, -4, -3, -2, -1, -3, -4, -5},
            {-6, -6, -6, -6, -6, -6, -6, -6},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 6,  6,  6,  6,  6,  6,  6,  6},
            { 5,  4,  3,  2,  1,  3,  4,  5}
    };

    private int[][] moveCnt = {
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0},
            { 0,  0,  0,  0,  0,  0,  0,  0}
    };

    private Point blackKing = new Point(0, 4);
    private Point whiteKing = new Point(7, 4);
    private Point selectedPoint = new Point(-1, -1);
    private boolean selected = false;
    private String player;
    int moveWithoutTakingAnyPiece = 0;


    public String getPlayer() {

        return player;
    }

    public void setPlayer(String player) {

        this.player = player;
    }

    public Point getKing(String player) {

        Point king = new Point();

        if(player == "white") {

            king = whiteKing;

        } else if(player == "black") {

            king = blackKing;
        }

        return king;
    }

    public void movePiece(Point toPoint) {

        moveWithoutTakingAnyPiece++;

        if(abs(chessBoard[selectedPoint.x][selectedPoint.y]) == 1 && abs(chessBoard[toPoint.x][toPoint.y]) == 5){

            if(selectedPoint.y > toPoint.y) {

                chessBoard[selectedPoint.x][selectedPoint.y - 2] = chessBoard[selectedPoint.x][selectedPoint.y];
                chessBoard[selectedPoint.x][selectedPoint.y - 1] = chessBoard[toPoint.x][toPoint.y];

                moveCnt[selectedPoint.x][selectedPoint.y - 2] = moveCnt[selectedPoint.x][selectedPoint.y] + 1;
                moveCnt[selectedPoint.x][selectedPoint.y] = 0;

                moveCnt[selectedPoint.x][selectedPoint.y - 1] = moveCnt[toPoint.x][toPoint.y] + 1;
                moveCnt[toPoint.x][toPoint.y] = 0;

            } else if(selectedPoint.y < toPoint.y) {

                chessBoard[selectedPoint.x][selectedPoint.y + 2] = chessBoard[selectedPoint.x][selectedPoint.y];
                chessBoard[selectedPoint.x][selectedPoint.y + 1] = chessBoard[toPoint.x][toPoint.y];

                moveCnt[selectedPoint.x][selectedPoint.y + 2] = moveCnt[selectedPoint.x][selectedPoint.y] + 1;
                moveCnt[selectedPoint.x][selectedPoint.y] = 0;

                moveCnt[selectedPoint.x][selectedPoint.y + 1] = moveCnt[toPoint.x][toPoint.y] + 1;
                moveCnt[toPoint.x][toPoint.y] = 0;
            }

            chessBoard[selectedPoint.x][selectedPoint.y] = 0;
            chessBoard[toPoint.x][toPoint.y] = 0;



        }  else {

            if(chessBoard[toPoint.x][toPoint.y] != 0) {

                moveWithoutTakingAnyPiece = 0;
            }

            chessBoard[toPoint.x][toPoint.y] = chessBoard[selectedPoint.x][selectedPoint.y];
            chessBoard[selectedPoint.x][selectedPoint.y] = 0;

            moveCnt[toPoint.x][toPoint.y] = moveCnt[selectedPoint.x][selectedPoint.y] + 1;
            moveCnt[selectedPoint.x][selectedPoint.y] = 0;
        }

        if(chessBoard[toPoint.x][toPoint.y] == -1) { // black king

            blackKing.x = toPoint.x;
            blackKing.y = toPoint.y;

        } else if(chessBoard[toPoint.x][toPoint.y] == 1) { // white king

            whiteKing.x = toPoint.x;
            whiteKing.y = toPoint.y;
        }

        System.out.println("MOve without taking any piece = " + moveWithoutTakingAnyPiece);
    }

    public Boolean isEven(Point point) {

        if((point.x + point.y) % 2 == 0) {

            return true;
        }

        return false;
    }

    public static int getId(Point point) {

        return (8 * point.x) + point.y;
    }

    public Boolean isSelected() {

        return selected;
    }

    public Point getSelectedPoint() {

        return selectedPoint;
    }

    public void setSelectedPoint(Point point) {

        if(point.x < 0 || point.y < 0) {

            selected = false;

        } else {

            selected = true;
        }

        selectedPoint.x = point.x;
        selectedPoint.y = point.y;
    }

    public int getSelectedPiece() {

        return chessBoard[selectedPoint.x][selectedPoint.y];
    }

    public void setSelectedPiece(Point point, int piece) {

        chessBoard[point.x][point.y] = piece;
    }

    public ArrayList<Point> getMoves(Point point) {

        ArrayList<Point> moves = new ArrayList<>();
        int tmpX, tmpY;
        String[] side = {"bottom", "top", "left", "right"};

        String pieceType = chessBoard[point.x][point.y] > 0 ? "white" : "black";
        int piece = abs(chessBoard[point.x][point.y]);

        if(piece == 1) { // King

            int[] x = {-1, -1, -1, 0, 0, 1, 1, 1};
            int[] y = {-1, 0, 1, -1, 1, -1, 0, 1};

            for(int i=0;i<8;i++) {
                tmpX = point.x + x[i];
                tmpY = point.y + y[i];

                if(isInBoard(tmpX, tmpY) && (chessBoard[tmpX][tmpY] == 0 || isOppositeColor(pieceType, tmpX, tmpY))) {

                    moves.add(new Point(tmpX, tmpY));
                }
            }

        } else if(piece == 2) { // Queen

            moves = VHDMove(point, "VH");
            moves.addAll(VHDMove(point, "D"));

        } else if(piece == 3) { // Bishop

            moves = VHDMove(point, "D");

        } else if(piece == 4) { // Knight

            int[] x = {-2, -2, -1, 1, 2, 2, -1, 1};
            int[] y = {-1, 1, -2, -2, -1, 1, 2, 2};

            for(int i=0;i<8;i++) {
                tmpX = point.x + x[i];
                tmpY = point.y + y[i];

                if(isInBoard(tmpX, tmpY) && (chessBoard[tmpX][tmpY] == 0 || isOppositeColor(pieceType, tmpX, tmpY))) {

                    moves.add(new Point(tmpX, tmpY));
                }
            }

        } else if(piece == 5) { // Rook

            moves = VHDMove(point, "VH");

        } else if(piece == 6) { // Pawn

            int length = moveCnt[point.x][point.y] == 0 ? 2 : 1;
            int direction = 1;

            if (pieceType == "white") {

                direction *= -1;
            }

            for(int i=1; i<=length; i++) {

                tmpX = point.x + (i * direction);
                tmpY = point.y;

                if (isInBoard(tmpX, tmpY) && chessBoard[tmpX][tmpY] == 0) {

                    moves.add(new Point(tmpX, tmpY));

                } else {

                    break;
                }

            }

            int[] x = {1, 1};
            int[] y = {-1, 1};

            for(int i=0; i<2; i++) {

                tmpX = point.x + (x[i] * direction);
                tmpY = point.y + y[i];

                if (isInBoard(tmpX, tmpY) && isOppositeColor(pieceType, tmpX, tmpY)) {

                    moves.add(new Point(tmpX, tmpY));
                }
            }

        }

        return moves;
    }

    public ArrayList<Point> getCastlingPoint(Point point) {

        System.out.println("Castling");

        int factor = 1;
        ArrayList<Point> validMoves = new ArrayList<>();
        ArrayList<Point> moves = new ArrayList<>();
        ArrayList<Point> rookPoint = new ArrayList<>();

        String player = (chessBoard[point.x][point.y] > 0 ? "white" : "black");

        if(player == "white") {

            rookPoint.add(new Point(7, 0));
            rookPoint.add(new Point(7, 7));

        } else if(player == "black") {

            rookPoint.add(new Point(0, 0));
            rookPoint.add(new Point(0, 7));
        }

        for(int i=0; i<rookPoint.size(); i++) {

            factor = (i%2) == 0 ? -1 : 1;

            if (abs(chessBoard[point.x][point.y]) == 1 && abs(chessBoard[rookPoint.get(i).x][rookPoint.get(i).y]) == 5 && moveCnt[point.x][point.y] == 0 && moveCnt[rookPoint.get(i).x][rookPoint.get(i).y] == 0) {

                if (!isInCheck(player)) {

                    int x = point.x;
                    int y = point.y + factor;
                    boolean flag = true;

                    while (abs(chessBoard[x][y]) != 5) {

                        if (chessBoard[x][y] != 0) {

                            moves.add(new Point(x, y));
                            flag = false;
                            break;
                        }

                        y = y + factor;
                    }

                    if (flag && moves.size() == filterMoves(point, moves).size()) {

                        validMoves.add(new Point(rookPoint.get(i).x, rookPoint.get(i).y));
                        ;
                    }
                }
            }
        }

        return validMoves;
    }

    public ArrayList<Point> getAllMoves(String player) {

        Point point = new Point();
        ArrayList<Point> moves = new ArrayList<Point>();

        for(int i=0; i<8; i++) {

            for(int j=0; j<8; j++) {

                if((player == "white" && chessBoard[i][j] > 0) || (player == "black" && chessBoard[i][j] < 0)) {

                    point.x = i;
                    point.y = j;
                    moves.addAll(filterMoves(point, getMoves(point)));
                }
            }
        }

        return moves;
    }

    private boolean isInBoard(int tmpX, int tmpY) {

        return tmpX >= 0 && tmpX < 8 && tmpY >= 0 && tmpY < 8;
    }

    private boolean isOppositeColor(String type, int tmpX, int tmpY) {

        return (type == "white" && chessBoard[tmpX][tmpY] < 0) || (type == "black" && chessBoard[tmpX][tmpY] > 0);
    }

    public ArrayList<Point> filterMoves(Point clickedPoint, ArrayList<Point> moves) {

        ArrayList<Point> validMoves = new ArrayList<>();
        int clickedPiece = chessBoard[clickedPoint.x][clickedPoint.y];
        chessBoard[clickedPoint.x][clickedPoint.y] = 0;

        for(int i=0; i<moves.size(); i++) {

            int tmpPiece = chessBoard[moves.get(i).x][moves.get(i).y];
            chessBoard[moves.get(i).x][moves.get(i).y] = clickedPiece;

            if(!isInCheck((clickedPiece > 0 ? "white" : "black"))) {

                validMoves.add(moves.get(i));
            }

            chessBoard[moves.get(i).x][moves.get(i).y] = tmpPiece;

        }

        chessBoard[clickedPoint.x][clickedPoint.y] = clickedPiece;

        return validMoves;
    }

    public boolean isInCheck(String player) {

        ArrayList<Point> checkMoves;

        for(int i=0; i<8; i++) {

            for(int j=0; j<8; j++) {

                if((player == "white" && chessBoard[i][j] < 0) || (player == "black" && chessBoard[i][j] > 0)) {

                    Point point = new Point(i, j);
                    checkMoves = getMoves(point);

                    for(int k=0; k<checkMoves.size(); k++) {

                        if((player == "white" && chessBoard[checkMoves.get(k).x][checkMoves.get(k).y] == 1) || (player == "black" && chessBoard[checkMoves.get(k).x][checkMoves.get(k).y] == -1)) {

                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private ArrayList<Point> VHDMove(Point point, String type) {

        ArrayList<Point> moveList = new ArrayList<>();

        String pieceType = chessBoard[point.x][point.y] > 0 ? "white" : "black";

        int[] x = new int[4];
        int[] y = new int[4];

        if(type == "VH") {

            x = new int[] {-1, 1, 0, 0};
            y = new int[] {0, 0, -1, 1};

        } else if(type == "D") {

            x = new int[] {-1, -1, 1, 1};
            y = new int[] {-1, 1, -1, 1};
        }

        int tmpX = 0, tmpY = 0;


        for(int i=0; i<4; i++) {

            tmpX = point.x + x[i];
            tmpY = point.y + y[i];

            while(isInBoard(tmpX, tmpY) && (chessBoard[tmpX][tmpY] == 0 || isOppositeColor(pieceType, tmpX, tmpY))) {

                moveList.add(new Point(tmpX, tmpY));

                if(chessBoard[tmpX][tmpY] != 0) {

                    break;
                }

                tmpX = tmpX + x[i];
                tmpY = tmpY + y[i];
            }
        }

        return moveList;
    }

}
