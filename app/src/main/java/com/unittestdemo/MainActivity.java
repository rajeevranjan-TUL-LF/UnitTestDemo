package com.unittestdemo;

import android.os.Bundle;

import com.unittestdemo.model.UserModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                openBottomSheet(new ArrayList<>());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void openBottomSheet(List<UserModel> userModels) {

        UPIAppsBottomSheetDialog upiAppsBottomSheetDialog = new UPIAppsBottomSheetDialog(0, userModels, MainActivity.this, result -> {
            if (result == 1) { //success
                Toast.makeText(MainActivity.this, "Task Completed Success", Toast.LENGTH_SHORT).show();
            } else { // Failure
                Toast.makeText(MainActivity.this, "Task Failed", Toast.LENGTH_SHORT).show();
            }
        });
        upiAppsBottomSheetDialog.show(getSupportFragmentManager(), "Opening UPI bottom sheet");
    }
}
