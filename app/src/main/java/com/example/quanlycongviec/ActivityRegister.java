package com.example.quanlycongviec;

import android.content.Intent;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.text.method.HideReturnsTransformationMethod;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class ActivityRegister extends AppCompatActivity {

    private EditText edtUsername;
    private EditText edtPassword;
    private EditText edtRePassword;

    private Button btnDangNhap;
    private ImageButton btnQuayLai;
    private ImageView imgEyePassword;
    private ImageView imgEyeRePassword;

    private DatabaseHelper databaseHelper;

    private boolean hienMatKhau = false;
    private boolean hienNhapLaiMatKhau = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Kết nối SQLite
        databaseHelper = new DatabaseHelper(this);

        // Ánh xạ các thành phần giao diện
        edtUsername = findViewById(R.id.edtUsername);
        edtPassword = findViewById(R.id.edtPassword);
        edtRePassword = findViewById(R.id.edtRePassword);

        btnDangNhap = findViewById(R.id.btnDangNhap);
        btnQuayLai = findViewById(R.id.btnQuayLai2);

        imgEyePassword = findViewById(R.id.imgEyePassword);
        imgEyeRePassword = findViewById(R.id.imgEyeRePassword);

        // Nhấn nút tạo tài khoản
        btnDangNhap.setOnClickListener(v -> taoTaiKhoan());

        // Quay lại màn hình trước
        btnQuayLai.setOnClickListener(v -> finish());

        // Hiện hoặc ẩn mật khẩu
        imgEyePassword.setOnClickListener(v -> {
            hienMatKhau = !hienMatKhau;

            if (hienMatKhau) {
                edtPassword.setTransformationMethod(
                        HideReturnsTransformationMethod.getInstance()
                );
                imgEyePassword.setContentDescription("Ẩn mật khẩu");
            } else {
                edtPassword.setTransformationMethod(
                        PasswordTransformationMethod.getInstance()
                );
                imgEyePassword.setContentDescription("Hiện mật khẩu");
            }

            edtPassword.setSelection(edtPassword.getText().length());
        });

        // Hiện hoặc ẩn mật khẩu nhập lại
        imgEyeRePassword.setOnClickListener(v -> {
            hienNhapLaiMatKhau = !hienNhapLaiMatKhau;

            if (hienNhapLaiMatKhau) {
                edtRePassword.setTransformationMethod(
                        HideReturnsTransformationMethod.getInstance()
                );
                imgEyeRePassword.setContentDescription("Ẩn mật khẩu");
            } else {
                edtRePassword.setTransformationMethod(
                        PasswordTransformationMethod.getInstance()
                );
                imgEyeRePassword.setContentDescription("Hiện mật khẩu");
            }

            edtRePassword.setSelection(
                    edtRePassword.getText().length()
            );
        });
    }

    // Chức năng tạo tài khoản
    private void taoTaiKhoan() {

        String tenDangNhap =
                edtUsername.getText().toString().trim();

        String matKhau =
                edtPassword.getText().toString();

        String nhapLaiMatKhau =
                edtRePassword.getText().toString();

        // 1. Kiểm tra thông tin trống
        if (tenDangNhap.isEmpty()
                || matKhau.isEmpty()
                || nhapLaiMatKhau.isEmpty()) {

            Toast.makeText(
                    this,
                    "Vui lòng nhập đầy đủ thông tin!",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // 2. Kiểm tra độ dài tên tài khoản
        if (tenDangNhap.length() < 4) {

            edtUsername.setError(
                    "Tên tài khoản phải có ít nhất 4 ký tự"
            );

            edtUsername.requestFocus();
            return;
        }

        // 3. Kiểm tra độ dài mật khẩu
        if (matKhau.length() < 6) {

            edtPassword.setError(
                    "Mật khẩu phải có ít nhất 6 ký tự"
            );

            edtPassword.requestFocus();
            return;
        }

        // 4. Kiểm tra nhập lại mật khẩu
        if (!matKhau.equals(nhapLaiMatKhau)) {

            edtRePassword.setError(
                    "Mật khẩu nhập lại không khớp"
            );

            edtRePassword.requestFocus();
            return;
        }

        try {
            // Băm mật khẩu trước khi lưu
            String matKhauDaBam = bamMatKhau(matKhau);

            // CA_NHAN là loại tài khoản mặc định trong DatabaseHelper
            long ketQua = databaseHelper.themTaiKhoan(
                    tenDangNhap,
                    matKhauDaBam,
                    "CA_NHAN"
            );

            if (ketQua != -1) {

                Toast.makeText(
                        this,
                        "Tạo tài khoản thành công!",
                        Toast.LENGTH_LONG
                ).show();

                // Chuyển sang màn hình đăng nhập
                Intent intent = new Intent(
                        ActivityRegister.this,
                        ActivityLogin.class
                );

                intent.putExtra("TEN_DANG_NHAP", tenDangNhap);

                startActivity(intent);
                finish();

            } else {

                Toast.makeText(
                        this,
                        "Tên tài khoản đã tồn tại!",
                        Toast.LENGTH_LONG
                ).show();
            }

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Lỗi tạo tài khoản: " + e.getMessage(),
                    Toast.LENGTH_LONG
            ).show();

            android.util.Log.e(
                    "REGISTER_ERROR",
                    "Không thể tạo tài khoản",
                    e
            );
        }
    }

    // Băm mật khẩu SHA-256
    // Khi đăng nhập cần băm mật khẩu bằng cùng cách này.
    public static String bamMatKhau(String matKhau) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] bytes = digest.digest(
                    matKhau.getBytes(StandardCharsets.UTF_8)
            );

            StringBuilder builder = new StringBuilder();

            for (byte b : bytes) {
                builder.append(
                        String.format("%02x", b & 0xff)
                );
            }

            return builder.toString();

        } catch (Exception e) {
            throw new RuntimeException(
                    "Không thể mã hóa mật khẩu",
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