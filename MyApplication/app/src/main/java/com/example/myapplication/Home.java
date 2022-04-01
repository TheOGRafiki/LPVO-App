package com.example.myapplication;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

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

    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Mcp2221Comm newComm = new Mcp2221Comm(new MCP2221(getActivity()));

        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                try {
                    newComm.openCOM();

                    if(newComm.isComOpen()) {
                        Snackbar.make(binding.getRoot(), "Status of Com Port: " + newComm.isComOpen(), Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    } else {
                        Snackbar.make(binding.getRoot(), "No Connection Available", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        return;
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        binding.buttonSecond.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                EditText editText = (EditText) getView().findViewById(R.id.editText);
                String value = editText.getText().toString();

                if(newComm.isComOpen()) {

                    try {
                        int isSuccess = newComm.writeI2cdata(Integer.valueOf(8).byteValue(),
                                ByteBuffer.wrap(value.getBytes(StandardCharsets.UTF_8)),
                                value.length(),
                                400);

                        if(isSuccess == 0)
                        {
                            Snackbar.make(binding.getRoot(), "Data Sent!" + value, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        else if(isSuccess < 0)
                        {
                            Snackbar.make(binding.getRoot(), "Failed To Send." + value, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                        else
                        {
                            Snackbar.make(binding.getRoot(), "Unknown Error." + value, Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }

                }
                else
                {
                    Snackbar.make(binding.getRoot(), "Connection Closed" + value, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        binding.buttonThird.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(binding.getRoot(), "Requesting Data from: " + newComm, Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                if(newComm.isComOpen()) {
                    ByteBuffer buf = ByteBuffer.allocate(10);
                    newComm.readI2cData(Integer.valueOf(8).byteValue(), buf, 10, 400);

                    EditText editText = (EditText) Objects.requireNonNull(getView()).findViewById(R.id.editText);
                    editText.setText(buf.toString().toCharArray(), 0, buf.array().length);

                }
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}