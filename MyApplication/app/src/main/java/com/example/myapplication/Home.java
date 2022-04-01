package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.myapplication.databinding.FragmentFirstBinding;
import com.google.android.material.snackbar.Snackbar;
import com.microchip.android.mcp2221comm.Mcp2221Comm;
import com.microchip.android.microchipusb.MCP2221;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Home extends Fragment {

    private FragmentFirstBinding binding;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState
    ) {

        binding = FragmentFirstBinding.inflate(inflater, container, false);
        return binding.getRoot();

    }

    String epsium = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum";

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Mcp2221Comm newComm = new Mcp2221Comm(new MCP2221(getActivity()));

        ProgressBar progressBar = (ProgressBar) getView().findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        // Connect To Device
        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    newComm.openCOM();

                    if (newComm.isComOpen()) {
                        Snackbar.make(binding.getRoot(), "Status of Com Port: " + newComm.isComOpen(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        Switch switchConnection = (Switch) getView().findViewById(R.id.switch1);
                        switchConnection.setChecked(true);

                    } else {
                        Snackbar.make(binding.getRoot(), "No Connection Available", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        TextView editText = (TextView) getView().findViewById(R.id.Logger);
                        editText.setText(epsium.toCharArray(), 0, epsium.length());
                        return;
                    }
                } catch (Exception e) {
                    TextView editText = (TextView) getView().findViewById(R.id.Logger);
                    editText.setText(e.toString().toCharArray(), 0, e.toString().length());

                }
            }
        });

        // Send Data to Device
        binding.buttonSecond.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText editText = (EditText) getView().findViewById(R.id.editText);
                String value = editText.getText().toString();

                progressBar.setVisibility(View.GONE);

                TextView logger = (TextView) getView().findViewById(R.id.Logger);
                logger.setText("".toCharArray(), 0, 0);


                if (newComm.isComOpen()) {
                    progressBar.setVisibility(View.VISIBLE);

                    try {
                        int isSuccess = newComm.writeI2cdata(Integer.valueOf(8).byteValue(),
                                ByteBuffer.wrap(value.getBytes(StandardCharsets.UTF_8)),
                                value.length(),
                                400);

                        if (isSuccess == 0) {
                            Snackbar.make(binding.getRoot(), "Data Sent!" + value, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else if (isSuccess < 0) {
                            Snackbar.make(binding.getRoot(), "Failed To Send." + value, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        } else {
                            Snackbar.make(binding.getRoot(), "Unknown Error." + value, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }

                    progressBar.setVisibility(View.GONE);
                    } catch (Exception e) {
                        editText.setText(e.toString().toCharArray(), 0, e.toString().length());
                        e.printStackTrace();
                    }

                } else {
                    Snackbar.make(binding.getRoot(), "Connection Closed" + value, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        // Request Data From Device
        binding.buttonThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(binding.getRoot(), "Requesting Data from: " + newComm, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                if (newComm.isComOpen()) {
                    ByteBuffer buf = ByteBuffer.allocate(10);
                    newComm.readI2cData(Integer.valueOf(8).byteValue(), buf, 10, 400);

                    EditText editText = (EditText) Objects.requireNonNull(getView()).findViewById(R.id.editText);
                    editText.setText(buf.toString().toCharArray(), 0, buf.array().length);

                }
            }
        });

        // Clear Text
        binding.clearText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView logger = (TextView) getView().findViewById(R.id.Logger);
                logger.setText("".toCharArray(), 0, 0);
            }

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}