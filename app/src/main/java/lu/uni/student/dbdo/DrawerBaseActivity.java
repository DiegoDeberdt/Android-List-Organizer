package lu.uni.student.dbdo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

public class DrawerBaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawer_base);
    }
}