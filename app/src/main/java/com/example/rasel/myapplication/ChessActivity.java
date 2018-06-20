package com.example.rasel.myapplication;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.Toast;

import java.net.URISyntaxException;
import java.util.ArrayList;

import static java.lang.Math.abs;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rasel on 5/11/18.
 */

public class ChessActivity extends AppCompatActivity {

    int[][] chess = new int[8][8];

    TableLayout tblChess;
    TableRow tblRow;
    ImageView imgView;
    Chess myChess = new Chess();
    Parser parser = new Parser(myChess);

    Dialog dialog;

    ArrayList<Point> myMoves = new ArrayList<>();

    final Context context = this;
    Point powerPoint;
    String powerPlayer;
    String android_id, receiverDeviceId, pair;
    boolean gameStart = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chess);

        android_id = Settings.Secure.getString(this.getContentResolver(),
                Settings.Secure.ANDROID_ID);

        receiverDeviceId = getIntent().getStringExtra("receiverDeviceId");
        final String thisPlayerAs = getIntent().getStringExtra("playerAs");

        pair = "\"from\":\""+android_id+"\", \"to\":\""+receiverDeviceId+"\"";

        if(thisPlayerAs.compareTo("white") == 0) {

            MySocket.socket.emit("invite", receiverDeviceId);

        } else if(thisPlayerAs.compareTo("black") == 0) {

            gameStart = true;
            String data = "{"+ pair +"}";
            MySocket.socket.emit("confirmation", data);
        }

        MySocket.socket.on("confirmation", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        JSONObject data = (JSONObject) args[0];
                        try {
                            if(receiverDeviceId.compareTo(data.getString("from")) == 0) {
                                gameStart = true;
                                Toast.makeText(getApplicationContext(), "Invitation accepted", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        MySocket.socket.on("data", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];

                        try {

                            if(data.getString("from").compareTo(android_id) == 0 || data.getString("to").compareTo(android_id) == 0)
                            {

                                if(data.has("mated")) {

                                    String mated = data.getString("mated");

                                    if(mated.compareTo(thisPlayerAs) == 0) {
                                        Toast.makeText(getApplicationContext(), "You lose!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(getApplicationContext(), "You won!", Toast.LENGTH_SHORT).show();
                                    }

                                    gameStart = false;

                                } else if(data.has("draw")) {

                                    Toast.makeText(getApplicationContext(), "Game is draw!", Toast.LENGTH_SHORT).show();
                                    gameStart = false;

                                } else if(data.has("chess")) {

                                    String chessData = data.getString("chess");
                                    parser.Parse(chessData);
                                    draw();

                                    if (myChess.getPlayer().compareTo("white") == 0) {
                                        myChess.setPlayer("black");
                                    } else if (myChess.getPlayer().compareTo("black") == 0) {
                                        myChess.setPlayer("white");
                                    }

                                    String player = myChess.getPlayer();

                                    if (myChess.isInCheck(player)) {

                                        if (myChess.getAllMoves(player).size() == 0) {

                                            chessData = "{" + pair + ", \"mated\":\"" + player + "\"}";
                                            MySocket.socket.emit("data", chessData);
                                            System.out.println("Mate");
                                        }

                                        highlightCheck(player);

                                    } else if (myChess.getAllMoves(player).size() == 0) {

                                        chessData = "{" + pair + ", \"draw\":\"Yes\"}";
                                        MySocket.socket.emit("data", chessData);
                                        System.out.println("Draw");
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

        chess = myChess.chessBoard;

        float density = this.getResources()
                .getDisplayMetrics()
                .density;

        tblChess = (TableLayout) findViewById(R.id.tblChess);

        dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog);
        dialog.setTitle("Choose your power");

        myChess.setPlayer("white");
        Point point = new Point();

        for(int i=0; i < 8; i++) {

            tblRow = new TableRow(this);

            for(int j=0; j < 8; j++) {

                point.x = i;
                point.y = j;

                imgView = new ImageView(this);

                imgView.setId(myChess.getId(point));

                imgView.setLayoutParams(new TableRow.LayoutParams(dpToPx(45), dpToPx(45)));

                drawGrid(imgView, point, chess[i][j]);

                tblRow.addView(imgView);

                final int x = i;
                final int y = j;

                imgView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        Point clickedPoint = new Point(x, y);
                        //System.out.println(myChess.getPlayer());

                        if(myChess.isSelected()) {

                            makeMove(clickedPoint);

                        } else {

                            int piece = chess[x][y];
                            String player = myChess.getPlayer();

                            if (gameStart && thisPlayerAs.compareTo(player) == 0 && ((player == "white" && piece > 0) || (player == "black" && piece < 0))) {

                                myMoves = myChess.filterMoves(clickedPoint, myChess.getMoves(clickedPoint));

                                if(abs(piece) == 1) {

                                    myMoves.addAll(myChess.getCastlingPoint(clickedPoint));
                                }

                                if (myMoves.size() > 0) {

                                    myChess.setSelectedPoint(clickedPoint);
                                    highlightMoves();
                                }
                            }
                        }
                    }
                });
            }

            tblChess.addView(tblRow);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        String data = "{"+ pair +"}";
        MySocket.socket.emit("cancelInvitation", data);
        finish();
    }

    private void highlightMoves() {

        Point point = new Point();

        for (int k = 0; k < myMoves.size(); k++) {

            point.x = myMoves.get(k).x;
            point.y = myMoves.get(k).y;

            int id = myChess.getId(point);

            findViewById(id).setBackgroundResource(R.drawable.view_default);
        }
    }

    private void highlightCheck(String player) {

        //Point point = myChess.getKing(player);
        Point point = new Point();

        for(int i=0; i<8; i++) {
            for(int j=0; j<8; j++) {
                if((player.compareTo("black") == 0 && myChess.chessBoard[i][j] == -1) || (player.compareTo("white") == 0 && myChess.chessBoard[i][j] == 1)) {
                    point.x = i;
                    point.y = j;
                    break;
                }
            }
        }
        int id = myChess.getId(point);
        findViewById(id).setBackgroundResource(R.drawable.check);
    }

    public void makeMove(Point point) {

        if(point.x == myChess.getSelectedPoint().x && point.y == myChess.getSelectedPoint().y) {

            deselectAll();

            myChess.setSelectedPoint(new Point(-1, -1)); //false

        } else {

            boolean flag = false;

            for (int k = 0; k < myMoves.size(); k++) {

                //System.out.println("Valid = (x, y) = (" + myMoves.get(k).x + ", " + myMoves.get(k).y + ")");

                if (myMoves.get(k).x == point.x && myMoves.get(k).y == point.y) {

                    flag = true;
                    break;
                }
            }

            if(flag) {

                if(abs(myChess.getSelectedPiece()) == 6 && (point.x == 0 || point.x == 7)) {

                    powerPoint = new Point(point.x, point.y);
                    powerPlayer = myChess.getPlayer();
                    showPowerDialog();

                } else {

                    myChess.movePiece(point);
                    myChess.setSelectedPoint(new Point(-1, -1));

                    parser.ToStr();
                    String data = "{" + pair + ", \"chess\":\"" + parser.data + "\"}";
                    MySocket.socket.emit("data", data);
                }
            }

        }
    }

    private void showPowerDialog() {

        int powers[] = {2, 3, 4, 5, 6};

        String prefix = "w";

        if(powerPlayer == "black") {
            prefix = "b";
        }

        for(int p=0; p<5; p++) {

            int pId = getResources().getIdentifier("p" + powers[p], "id", getPackageName());

            int rId = getResources().getIdentifier(prefix + powers[p], "drawable", getPackageName());

            ((ImageView) dialog.findViewById(pId)).setImageResource(rId);
        }

        dialog.show();
    }

    public void setPower(View view) {

        int power = Integer.parseInt(view.getTag().toString());

        if(powerPlayer == "black") {
            power = (-1) * power;
        }

        myChess.setSelectedPiece(powerPoint, power);
        System.out.println("Setting power = "  + power);

        parser.ToStr();
        String data = "{"+ pair +", \"chess\":\""+ parser.data +"\"}";
        MySocket.socket.emit("data", data);

        //draw();

        dialog.dismiss();
    }

    private void deselectAll() {

        Point point = new Point();
        View view = null;

        for(int k=0; k < myMoves.size(); k++) {

            point.x = myMoves.get(k).x;
            point.y = myMoves.get(k).y;

            int id = myChess.getId(point);
            view = findViewById(id);

            view.setBackgroundResource(0);

            setBackground(view, point);
        }
    }

    private void draw() {

        Point point = new Point();

        for(int i=0; i < 8; i++) {

            for(int j=0; j < 8; j++) {

                point.x = i;
                point.y = j;

                drawGrid(null, point, myChess.chessBoard[i][j]);
            }
        }
    }

    private void drawGrid(View view, Point point, int piece) {

        int gridId = myChess.getId(point);

        int resourceId = -1;

        if(view == null) {

            view = findViewById(gridId);
        }

        setBackground(view, point);

        if(piece > 0) {

            resourceId = getResources().getIdentifier("w" + piece, "drawable", getPackageName());

        } else if(piece < 0) {

            resourceId = getResources().getIdentifier("b" + abs(piece), "drawable", getPackageName());

        }

        if(resourceId > 0) {

            ((ImageView)view).setImageResource(resourceId);

        } else {

            ((ImageView)view).setImageResource(0);
        }
    }

    private void setBackground(View view, Point point) {

        if(myChess.isEven(point) ) {

            view.setBackgroundColor(Color.WHITE);

        } else {

            view.setBackgroundColor(Color.GRAY);

        }
    }

    public int dpToPx(int dp) {
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return (int)(dp * density);
    }

}
