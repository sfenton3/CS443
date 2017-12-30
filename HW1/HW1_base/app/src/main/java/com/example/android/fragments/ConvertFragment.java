package com.example.android.fragments;

/**
 * Created by scott on 9/30/2017.
 */

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;

public class ConvertFragment extends Fragment{

    final static String ARG_POSITION = "position";
    int mCurrentPosition = -1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.convert_view, container, false);
    }


    public void updateConvertView(int position) {
        final Button b = (Button) getActivity().findViewById(R.id.convertButton);
        final EditText e = (EditText) getActivity().findViewById(R.id.editText);
        final TextView screen = (TextView) getActivity().findViewById(R.id.textView);

        if(position == 2) {
            b.setVisibility(View.INVISIBLE);
            e.setVisibility(View.INVISIBLE);
            screen.setVisibility(View.INVISIBLE);
        }

        if(position == 0) {
            b.setVisibility(View.VISIBLE);
            e.setVisibility(View.VISIBLE);
            e.setText("");
            screen.setText("");


            b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                        final String s = e.getText().toString();
                        double result = (Double.valueOf(s) * 1.8) + 32;
                        String answer = "Temperature " + s + " (C) is " + String.valueOf(result) + " (F)";
                        screen.setText(answer);
                        screen.setVisibility(View.VISIBLE);


                }
            });
        }


        if(position == 1){
            b.setVisibility(View.VISIBLE);
            e.setVisibility(View.VISIBLE);
            e.setText("");
            screen.setText("");

            b.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    final String s = e.getText().toString();
                    double result = (Double.valueOf(s) - 32) * .55555;
                    String answer = "Temperature " + s + " (F) is " + String.valueOf(result) + " (C)";
                    screen.setText(answer);
                    screen.setVisibility(View.VISIBLE);

                }
            });


        }





        mCurrentPosition = position;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current article selection in case we need to recreate the fragment
        outState.putInt(ARG_POSITION, mCurrentPosition);
    }
}
