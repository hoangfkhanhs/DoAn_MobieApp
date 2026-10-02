package com.example.quanlycongviec;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class ActivityLogin extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;

    private Button btnDangNhap;
    private ImageButton btnQuayLai;

    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Khởi tạo SQLite
        databaseHelper = new DatabaseHelper(this);

        // Ánh xạ giao diện
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        btnDangNhap = findViewById(R.id.btnDangNhap);

        // Nếu có nút quay lại trong XML thì ánh xạ nút này
        btnQuayLai = findViewById(R.id.btnQuayLai2);

        // Nhận tên tài khoản từ màn hình đăng ký nếu có
        String tenTaiKhoan = getIntent().getStringExtra("TEN_DANG_NHAP");

        if (tenTaiKhoan != null) {
            edtUsername.setText(tenTaiKhoan);
            edtPassword.requestFocus();
        }

        // Xử lý đăng nhập
        btnDangNhap.setOnClickListener(v -> dangNhap());

        // Quay lại màn hình trước
        if (btnQuayLai != null) {
            btnQuayLai.setOnClickListener(v -> finish());
        }
    }

    private void dangNhap() {

        String tenDangNhap =
                edtUsername.getText().toString().trim();

        String matKhau =
                edtPassword.getText().toString();

        // Kiểm tra dữ liệu trống
        if (tenDangNhap.isEmpty()) {
            edtUsername.setError("Vui lòng nhập tên tài khoản");
            edtUsername.requestFocus();
            return;
        }

        if (matKhau.isEmpty()) {
            edtPassword.setError("Vui lòng nhập mật khẩu");
            edtPassword.requestFocus();
            return;
        }

        try {
            // Băm mật khẩu theo đúng cách đã dùng khi đăng ký
            String matKhauDaBam =
                    ActivityRegister.bamMatKhau(matKhau);

            // Kiểm tra tài khoản trong SQLite
            boolean hopLe = databaseHelper.kiemTraTaiKhoan(
                    tenDangNhap,
                    matKhauDaBam
            );

            if (hopLe) {

                Toast.makeText(
                        this,
                        "Đăng nhập thành công!",
                        Toast.LENGTH_SHORT
                ).show();

                // Chuyển sang màn hình Home
                Intent intent = new Intent(
                        ActivityLogin.this,
                        ActivityHome.class
                );

                // Gửi tên tài khoản sang trang Home nếu cần
                intent.putExtra("TEN_DANG_NHAP", tenDangNhap);
// Lưu tên tài khoản để sử dụng khi mở lại trang chủ
                getSharedPreferences("QuanLyCongViec", MODE_PRIVATE)
                        .edit()
                        .putString("TEN_DANG_NHAP", tenDangNhap)
                        .apply();

                startActivity(intent);
                startActivity(intent);
                finish();

            } else {

                Toast.makeText(
                        this,
                        "Tên tài khoản hoặc mật khẩu không đúng!",
                        Toast.LENGTH_LONG
                ).show();
            }

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Lỗi đăng nhập: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            android.util.Log.e(
                    "LOGIN_ERROR",
                    "Lỗi khi đăng nhập",
                    e
            );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}