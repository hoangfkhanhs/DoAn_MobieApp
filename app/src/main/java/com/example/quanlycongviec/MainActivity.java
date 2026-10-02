package com.example.quanlycongviec;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {

    private DatabaseHelper databaseHelper;
    private SQLiteDatabase database;

    private Button btnDangKy;
    private Button btnDangNhap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Hien thi giao dien tran vien
        EdgeToEdge.enable(this);

        // Gan giao dien trang chu
        setContentView(R.layout.activity_main);

        // Xu ly khoang cach he thong
        ViewCompat.setOnApplyWindowInsetsListener(
                findViewById(R.id.main),
                (v, insets) -> {
                    Insets systemBars = insets.getInsets(
                            WindowInsetsCompat.Type.systemBars()
                    );

                    v.setPadding(
                            systemBars.left,
                            systemBars.top,
                            systemBars.right,
                            systemBars.bottom
                    );

                    return insets;
                }
        );

        // Khoi tao database SQLite
        khoiTaoDatabase();

        // Anh xa nut tren giao dien
        btnDangKy = findViewById(R.id.btnDangKy);
        btnDangNhap = findViewById(R.id.btnDangNhap);

        // Chuyển sang màn hình đăng ký
        btnDangKy.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    ActivityRegister.class
            );
            startActivity(intent);
        });

// Chuyển sang màn hình đăng nhập
        btnDangNhap.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    ActivityLogin.class
            );
            startActivity(intent);
        });
    }

    // Khoi tao va kiem tra database
    private void khoiTaoDatabase() {
        try {
            databaseHelper = new DatabaseHelper(this);
            database = databaseHelper.getWritableDatabase();

            int soBang = databaseHelper.demSoBang();
            int soCa = databaseHelper.demSoCa();

            Toast.makeText(
                    this,
                    "SQLite thành công!"
                            + "\nSố bảng: " + soBang
                            + "\nSố ca làm việc: " + soCa,
                    Toast.LENGTH_LONG
            ).show();

        } catch (Exception e) {
            Toast.makeText(
                    this,
                    "Lỗi SQLite: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            android.util.Log.e("SQLite_ERROR", "Lỗi khởi tạo database", e);
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (database != null && database.isOpen()) {
            database.close();
        }

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}