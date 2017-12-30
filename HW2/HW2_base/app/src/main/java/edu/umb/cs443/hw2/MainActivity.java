package edu.umb.cs443.hw2;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;


public class MainActivity extends Activity {

    GridView gridView;

    private static int w = 5, curx, cury;
    private static int current;
    private static int positionX;
    int countX = 0;
    int treasureCount;
    int cellCount;
    boolean status = true;

    Random r = new Random();

    static String[] numbers = new String[w * w];


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        gridView = (GridView) findViewById(R.id.gridView1);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.list_item, numbers);

        gridView.setAdapter(adapter);
        init();
        placeX();
        Thread thread = new Thread(placeOnBoard);
        thread.start();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    final int position, long id) {
                int time = 300;
                int temp = current;

                if(status == true) {
                    positionX = position;
                    while (position != temp) {
                        int inc = -1;
                        if ((position / 5) > (temp / 5)) {
                            ThreadCreator(5, time);
                            inc = 5;
                        } else if ((position / 5) < (temp / 5)) {
                            ThreadCreator(-5, time);
                            inc = -5;
                        } else if (position > temp) {
                            ThreadCreator(1, time);
                            inc = 1;
                        } else
                            ThreadCreator(-1, time);
                        time += 300;
                        temp += inc;
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Waiting for move to finish...", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    void placeX(){
        curx = r.nextInt(w);
        cury = r.nextInt(w);
        int placement = cury * w + curx;

        while (numbers[placement] != " "){
            curx = r.nextInt(w);
            cury = r.nextInt(w);
            placement = cury * w + curx;
        }
        if(countX < 4) {
            numbers[placement] = "X";
            countX++;
        }
        ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
    }

    public Handler threadHandler = new Handler(){
        public void handleMessage (android.os.Message message){
            placeX();
        }
    };

    private Runnable placeOnBoard = new Runnable () {
        private static final int DELAY = 3000;
        public void run() {
            try {
                while (true) {
                    Thread.sleep (DELAY);
                    threadHandler.sendEmptyMessage(0);
                }
            } catch (InterruptedException e){
                e.printStackTrace();}}
    };

    public void ThreadCreator(int direction, int time){
        status = false;
        final TextView cell = (TextView) findViewById(R.id.textCell);
        final TextView treasure = (TextView) findViewById(R.id.textTreasure);

        final int direction2 = direction;
        final int time2 = time;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                SystemClock.sleep(time2);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        numbers[current] = " ";
                        if (numbers[current + direction2] == "X") {
                            treasureCount++;
                            countX--;
                        }
                        numbers[current + direction2] = "O";
                        cellCount++;
                        cell.setText(String.valueOf(cellCount) + " Cells ");
                        treasure.setText(String.valueOf(treasureCount) + " Treasures");
                        ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
                        current += direction2;
                        if (current == positionX)
                            status = true;
                    }
                });
            }
        });
        t.start();
    }

    public void reset(View view) {
        countX = 0;
        init();
        placeX();
        cellCount = 0;
        treasureCount = 0;
        TextView cell = (TextView) findViewById(R.id.textCell);
        TextView treasure = (TextView) findViewById(R.id.textTreasure);
        cell.setText(String.valueOf(cellCount) + " Cells ");
        treasure.setText(String.valueOf(treasureCount) + " Treasures");
    }

    void init() {
        for (int i = 0; i < numbers.length; i++) numbers[i] = " ";
        curx = r.nextInt(w);
        cury = r.nextInt(w);
        numbers[cury * w + curx] = "O";
        current = cury * w + curx;
        ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
    }
}