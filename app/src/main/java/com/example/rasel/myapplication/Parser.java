package com.example.rasel.myapplication;

/**
 * Created by rasel on 5/11/18.
 */

public class Parser {

    public String data = "";

    Chess chess;

    public Parser(Chess chess) {
        this.chess = chess;
    }


    public void ToStr() {

        data = "";

        data += "player:"+chess.getPlayer()+"&";

        data += "board:";

        for(int i=0; i<8; i++) {

            for(int j=0; j<8; j++) {

                if(i+j != 0) {
                    data += ",";
                }
                data += chess.chessBoard[i][j];
            }
        }

    }

    public void Parse(String inputData) {

        String[] split = inputData.split("&");

        for (int i = 0; i < split.length; i++) {

            String str = split[i];

            if (str.length() != 0) {

                String[] part = str.split(":");

                String key = part[0].trim();
                String value = part[1].trim();

                switch (key) {

                    case "player":

                        chess.setPlayer(value);
                        break;

                    case "board":

                        String[] boardStr = value.split(",");

                        int l = 0;
                        for (int j = 0; j < 8; j++) {
                            for (int k = 0; k < 8; k++) {
                                chess.chessBoard[j][k] = Integer.parseInt(boardStr[l++]);
                                System.out.print(chess.chessBoard[j][k] + "   ");
                            }
                            System.out.println();
                        }

                        break;
                }
            }
        }
    }
}