package chat.mitin.app;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    private static final int AUTO_CLOSE_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Log.d("MainActivity", "Mitin - Registration confirmation shown");

        Intent syncIntent = new Intent(this, SyncService.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(syncIntent);
        } else {
            startService(syncIntent);
        }

        new Handler().postDelayed(() -> {
            moveTaskToBack(true);
        }, AUTO_CLOSE_DELAY);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}
