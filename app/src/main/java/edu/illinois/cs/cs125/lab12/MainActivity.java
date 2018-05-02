package edu.illinois.cs.cs125.lab12;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * follow the instruction of AI Android. (*)
 *   from www.youtube.com/watch?v=dPqqojFGV4U
 * Main class for our UI design lab.
 */
public class MainActivity extends Activity {
    /**
     * magic number 3.
     */
    private final int three = 3;
    /**
     * magic Number 4.
     */
    private final int four = 4;
    /**
     * number seven.
     */
    private final int seven = 7;
    /**
     * declare a int for board size.
     */
    private final int maxN = 15;
    /**
     * declare for imageView(cells) array.
     */
    private ImageView[][] ivCell = new ImageView[maxN][maxN];
    /**
     * drawCell.
     */
    //0 is empty,1 is player, 2 is bot, 3 is background.
    private Drawable[] drawCell = new Drawable[four];
    /**
     * button to play.
     */
    private Button btnPlay;
    /**
     * String at.
     * @param at the string.
     */
    private void evalEnd(final String at) {
        switch (at) {
            case "11111": winnerPlay = 1; break;
            case "22222": winnerPlay = 2; break;
            default: break;
        }
    }
    /**
     * turn tv.
     */
    private TextView tvTurn;
    /**
     * valuecell.
     */
    private int[][] valueCell = new int[maxN][maxN];
    /**
     * winner is who.
     */
    private int winnerPlay;
    /**
     * first move or not.
     */
    private boolean firstMove;
    /**
     * xmove and ymove.
     */
    private int xMove, yMove;
    /**
     * turnPlay.
     */
    private int turnPlay;
    /**
     * context.
     */
    private Context context;

    /**
     * main activity.
     */
    public MainActivity() {

    }

    /**
     * Run when this activity comes to the foreground.
     *
     * @param savedInstanceState unused
     */
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
        setListen();
        loadResources();
        designBoardGame();


    }

    /**
     * set listen.
     */
    private void setListen() {
        btnPlay = findViewById(R.id.btnPlay);
        tvTurn = findViewById((R.id.tvTurn));

        btnPlay.setText("Play Game");
        tvTurn.setText("Press Button Play Game");

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                initGame();
                playGame();
            }
                                   });
    }

    /**
     * play the game.
     */
    private void playGame() {
        Random r = new Random();
        turnPlay = r.nextInt(2) + 1;

        if (turnPlay == 1) {
            Toast.makeText(context, "Player play first!", Toast.LENGTH_SHORT).show();
            playerTurn();
        } else {
            Toast.makeText(context, "Bot play first!", Toast.LENGTH_SHORT).show();
            botTurn();
        }
    }

    /**
     * bot moves first.
     */
    private void botTurn() {
        Log.d("tuanh", "bot turn");
        tvTurn.setText("Bot");
        if (firstMove) {
            firstMove = false;
            xMove = seven;
            yMove = seven;
            makeAMove();
        } else {
            findBotMove();
            makeAMove();
        }
    }

    /**
     * the row.
     */
    private final int[] iRow = {-1, -1, -1, 0, 1, 1, 1, 0};
    /**
     * the column.
     */
    private final int[] iCol = {-1, 0, 1, 1, 1, 0, -1, -1};
    /**
     * Find bot move.
     */
    private void findBotMove() {
        List<Integer> listX = new ArrayList<Integer>();
        List<Integer> listY = new ArrayList<Integer>();
        final int range = 2;
        for (int i = 0; i < maxN; i++) {
            for (int j = 0; j < maxN; j++) {
                if (valueCell[i][j] != 0) {
                    for (int t = 1; t <= range; t++) {
                        for (int k = 0; k < 8; k++) {
                            int x = i + iRow[k] * t;
                            int y = j + iCol[k] * t;
                            if (inBoard(x, y) && valueCell[x][y] == 0) {
                                listX.add(x);
                                listY.add(y);
                            }
                        }
                    }
                }
            }
        }
        int lx = listX.get(0);
        int ly = listY.get(0);
        int res = Integer.MAX_VALUE - 10;
        for (int i = 0; i < listX.size(); i++) {
            int x = listX.get(i);
            int y = listY.get(i);
            valueCell[x][y] = 2;
            int rr = getValuePosition();
            if (rr < res) {
                res = rr;
                lx = x;
                ly = y;
            }
            valueCell[x][y] = 0;
        }
        xMove = lx;
        yMove = ly;
    }

    /**
     * get value.
     * @return get position.
     */
    private int getValuePosition() {
        int rr = 0;
        int p1 = turnPlay;
        for (int i = 0; i < maxN; i++) {
            rr += checkValue(maxN - 1, i, -1, 0, p1);
        }
        for (int i = 0; i < maxN; i++) {
            rr += checkValue(i, maxN - 1, 0, -1, p1);
        }
        for (int i = maxN - 1; i >= 0; i--) {
            rr += checkValue(i, maxN - 1, -1, -1, p1);
        }
        for (int i = maxN - 2; i >= 0; i--) {
            rr += checkValue(maxN - 1, i, -1, -1, p1);
        }
        for (int i = maxN - 1; i >= 0; i--) {
            rr += checkValue(i, 0, -1, 1, p1);
        }
        for (int i = maxN - 1; i >= 1; i--) {
            rr += checkValue(maxN - 1, i, -1, 1, p1);
        }
        return rr;
    }

    /**
     * ints.
     * @param xd i.
     * @param yd i1.
     * @param vx i2.
     * @param vy i3.
     * @param p1 i4.
     * @return checked value.
     */
    private int checkValue(final int xd, final int yd, final int vx, final int vy, final int p1) {
        int i, j;
        int rr = 0;
        i = xd;
        j = yd;
        String at = String.valueOf(valueCell[i][j]);
        while (true) {
            i += vx;
            j += vy;
            if (inBoard(i, j)) {
                at = at + String.valueOf(valueCell[i][j]);
                if (at.length() == 6) {
                    rr += eval(at, p1);
                    at = at.substring(1, 6);
                }
            } else {
                break;
            }
        }
        return rr;
    }

    /**
     * make a move.
     */
    private void makeAMove() {
        Log.d("tuanh", "make a move with" + xMove + ";" + yMove + ";" + turnPlay);
        ivCell[xMove][yMove].setImageDrawable(drawCell[turnPlay]);
        valueCell[xMove][yMove] = turnPlay;
        //check if anyone win
        if (noEmptyCell()) {
            Toast.makeText(context, "Draw!!", Toast.LENGTH_SHORT).show();
            return;
        } else if (checkWinner()) {
            if (winnerPlay == 1) {
                Toast.makeText(context, "Winner is Player" + winnerPlay, Toast.LENGTH_SHORT).show();
                tvTurn.setText("Winner is Player");
            } else {
                Toast.makeText(context, "Winner is Bot" + winnerPlay, Toast.LENGTH_SHORT).show();
                tvTurn.setText("Winner is Bot");
            }
            return;
        }
        if (turnPlay == 1) {
            turnPlay = (1 + 2) - turnPlay;
            botTurn();
        } else {
            turnPlay = 3 - turnPlay;
            playerTurn();
        }
    }

    /**
     * check winner.
     * @return true or not.
     */
    private boolean checkWinner() {
        if (winnerPlay != 0) {
            return true;
        }
        vectorEnd(xMove, 0, 0, 1, xMove, yMove);
        vectorEnd(0, yMove, 1, 0, xMove, yMove);
        if (xMove + yMove >= maxN - 1) {
            vectorEnd(maxN - 1, xMove + yMove - maxN + 1, -1, 1, xMove, yMove);
        } else {
            vectorEnd(xMove + yMove, 0, -1, 1, xMove, yMove);
        }
        if (xMove <= yMove) {
            vectorEnd(xMove - yMove + maxN - 1, maxN - 1, -1, -1, xMove, yMove);
        } else {
            vectorEnd(maxN - 1, maxN - 1 - (xMove - yMove), -1, -1, xMove, yMove);
        }
        if (winnerPlay != 0) {
            return true;
        } else {
            return false;
        }
    }

    /**
     *
     * @param xx xx.
     * @param yy yy.
     * @param vx vx.
     * @param vy vy.
     * @param rx rx.
     * @param ry ry.
     */
    private void vectorEnd(final int xx, final int yy, final int vx, final int vy, final int rx,
                           final int ry) {
        if (winnerPlay != 0) {
            return;
        }
        final int range = 4;
        final int five = 5;
        int i, j;
        int xBelow = rx - range * vx;
        int yBelow = ry - range * vy;
        int xAbove = rx + range * vx;
        int yAbove = ry + range * vy;
        String at = "";
        i = xx; j = yy;
        while (!inside(i, xBelow, xAbove) || !inside(j, yBelow, yAbove)) {
            i += vx; j += vy;
        }
        while (true) {
            at = at + String.valueOf(valueCell[i][j]);
            if (at.length() == five) {
                evalEnd(at);
                at = at.substring(1, five); //substring of at from 1 to 5
            }
            i += vx;
            j += vy;
            if (!inBoard(i, j) || !inside(i, xBelow, xAbove) || !inside(j, yBelow, yAbove)
                    || winnerPlay != 0) {
                break;
            }
        }
    }

    /**
     * method inBoard.
     * @param i the x value.
     * @param j the y value.
     * @return check present on the board or not.
     */
    private boolean inBoard(final int i, final int j) {
        if (i < 0 || i > maxN - 1 || j < 0 || j > maxN - 1) {
            return false;
        }
        return true;
    }

    /**
     *
     * @param i i.
     * @param xBelow x below.
     * @param xAbove x above.
     * @return x.
     */
    private boolean inside(final int i, final int xBelow, final int xAbove) {
        return (i - xBelow) * (i - xAbove) <= 0;
    }

    /**
     * no empty cell or not.
     * @return true or false.
     */
    private boolean noEmptyCell() {
        for (int i = 0; i < maxN; i++) {
            for (int j = 0; j < maxN; j++) {
                if (valueCell[i][j] == 0) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * turn play equals 1.
     */
    private void playerTurn() {
        Log.d("tuanh", "player turn");
        tvTurn.setText("Player");
        firstMove = false;
        isClicked = false;
    }

    /**
     * initiate game.
     */
    private void initGame() {
        firstMove = true;
        winnerPlay = 0;
        for (int i = 0; i < maxN; i++) {
            for (int j = 0; j < maxN; j++) {
                ivCell[i][j].setImageDrawable(drawCell[0]);
                valueCell[i][j] = 0;
            }
        }
    }

    /**
     * load resources.
     */
    private void loadResources() {
        drawCell[three] = context.getResources().getDrawable(R.drawable.cell_bg);
        //background
        //copy 2 image for 2 drawable player and bot
        //edit id
        drawCell[0] = null;
        drawCell[1] = context.getResources().getDrawable(R.drawable.thor);
        drawCell[2] = context.getResources().getDrawable(R.drawable.thanos);
    }

    /**
     * is clicked or not.
     */
    private boolean isClicked;
    /**
     * design board game.
     */
    @SuppressLint("NewApi")
    private void designBoardGame() {
        int sizeOfCell = Math.round(screenWidth() / maxN);
        LinearLayout.LayoutParams lpRow =
                new LinearLayout.LayoutParams(sizeOfCell * maxN, sizeOfCell);
        LinearLayout.LayoutParams lpCell =
                new LinearLayout.LayoutParams(sizeOfCell, sizeOfCell);

        LinearLayout linBoardGame = findViewById(R.id.linBoardGame);
        //create cells
        for (int i = 0; i < maxN; i++) {
            LinearLayout linRow = (LinearLayout) new LinearLayout(context);
            //make a row
            for (int j = 0; j < maxN; j++) {
              ivCell[i][j] = new ImageView(context);
              //make a cell
              //cell has 3 status, empty(default),player,bot
                ivCell[i][j].setBackground(drawCell[three]);
                final int x = i;
                final int y = j;
                ivCell[i][j].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        if (valueCell[x][y] == 0) {
                            if (turnPlay == 1 || !isClicked) {
                                Log.d("tuanh", "click to cell");
                                isClicked = true;
                                xMove = x;
                                yMove = y;
                                makeAMove();
                            }
                        }
                    }
                });
                linRow.addView(ivCell[i][j], lpCell);
            }
            linBoardGame.addView(linRow, lpRow);
        }
    }

    /**
     * screen mid.
     * @return float.
     */
    private float screenWidth() {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.widthPixels;
    }
    /**
     * ah.
     * @param at player.
     * @param p1 the player.
     * @return evaluations of moves.
     */
    private int eval(final String at, final int p1) {
        int b1 = 1;
        int b2 = 1;
        if (p1 == 1) {
            b1 = 2;
            b2 = 1;
        } else {
            b1 = 1;
            b2 = 2;
        }
        switch (at) {
            case "111110": return b1 = 100000000;
            case "011111": return b1 = 100000000;
            case "211111": return b1 = 100000000;
            case "111112": return b1 = 100000000;
            case "011110": return b1 = 10000000;
            case "101110": return b1 = 1002;
            case "011101": return b1 = 1002;
            case "011112": return b1 = 1000;
            case "011100": return b1 = 102;
            case "001110": return b1 = 102;
            case "210111": return b1 = 100;
            case "211110": return b1 = 100;
            case "211011": return b1 = 100;
            case "211101": return b1 = 100;
            case "010100": return b1 = 10;
            case "011000": return b1 = 10;
            case "001100": return b1 = 10;
            case "000110": return b1 = 10;
            case "211000": return b1 = 1;
            case "201100": return b1 = 1;
            case "200110": return b1 = 1;
            case "200011": return b1 = 1;
            case "222220": return b2 = -100000000;
            case "022222": return b2 = -100000000;
            case "122222": return b2 = -100000000;
            case "222221": return b2 = -100000000;
            case "022220": return b2 = -10000000;
            case "202220": return b2 = -1002;
            case "022202": return b2 = -1002;
            case "022221": return b2 = -1000;
            case "022200": return b2 = -102;
            case "002220": return b2 = -102;
            case "120222": return b2 = -100;
            case "122220": return b2 = -100;
            case "122022": return b2 = -100;
            case "122202": return b2 = -100;
            case "020200": return b2 = -10;
            case "022000": return b2 = -10;
            case "002200": return b2 = -10;
            case "000220": return b2 = -10;
            case "122000": return b2 = -1;
            case "102200": return b2 = -1;
            case "100220": return b2 = -1;
            case "100022": return b2 = -1;
            default: break;
        }
        return 0;
    }
}
