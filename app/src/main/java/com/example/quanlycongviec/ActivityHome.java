
package com.example.quanlycongviec;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class ActivityHome extends AppCompatActivity {

    private TextView tvMaNguoiDung;
    private TextView tvHoTen;

    // 7 ô ngày trong tuần
    private TextView[] cacNgay;

    private DatabaseHelper databaseHelper;
    private String tenDangNhap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

// Chuyển sang trang thóngaz báo
        findViewById(R.id.imgBell).setOnClickListener(v -> {
            Intent intent = new Intent(
                    ActivityHome.this,
                    ActivityBell.class
            );
            startActivity(intent);
        });
// Chuyển sang trang Nhóm
        findViewById(R.id.imgGroup).setOnClickListener(v -> {
            Intent intent = new Intent(
                    ActivityHome.this,
                    ActivityGroup.class
            );
            startActivity(intent);
        });

// Chuyển sang trang Cá nhân
        findViewById(R.id.imgPerson).setOnClickListener(v -> {
            Intent intent = new Intent(
                    ActivityHome.this,
                    ActivityProfile.class
            );
            startActivity(intent);
        });

        // =====================================================
// THỐNG KÊ → ACTIVITY chart
// =====================================================

        findViewById(R.id.cardThongKe).setOnClickListener(v -> {

            Intent intent = new Intent(
                    ActivityHome.this,
                    ActivityChart.class
            );

            intent.putExtra(
                    "TEN_DANG_NHAP",
                    tenDangNhap
            );

            startActivity(intent);
        });


// =====================================================
// LỊCH LÀM VIỆC → ACTIVITY WORK SCHEDULE
// =====================================================

        findViewById(R.id.cardLich).setOnClickListener(v -> {

            Intent intent = new Intent(
                    ActivityHome.this,
                    ActivityWorkSchedule.class
            );

            intent.putExtra(
                    "TEN_DANG_NHAP",
                    tenDangNhap
            );

            startActivity(intent);
        });


// =====================================================
// ĐĂNG KÝ LỊCH → ACTIVITY SHIFT REGISTRATION
// =====================================================

        findViewById(R.id.cardDangKy).setOnClickListener(v -> {

            Intent intent = new Intent(
                    ActivityHome.this,
                    ActivityShiftRegistration.class
            );

            intent.putExtra(
                    "TEN_DANG_NHAP",
                    tenDangNhap
            );

            startActivity(intent);
        });

        databaseHelper = new DatabaseHelper(this);

        // Ánh xạ giao diện
        tvMaNguoiDung = findViewById(R.id.tvMaNguoiDung);
        tvHoTen = findViewById(R.id.tvHoTen);

        // Ánh xạ 7 ngày trong tuần
        cacNgay = new TextView[]{
                findViewById(R.id.tvNgay0),
                findViewById(R.id.tvNgay1),
                findViewById(R.id.tvNgay2),
                findViewById(R.id.tvNgay3),
                findViewById(R.id.tvNgay4),
                findViewById(R.id.tvNgay5),
                findViewById(R.id.tvNgay6)
        };

        // Lấy tên đăng nhập từ Intent
        tenDangNhap = getIntent().getStringExtra("TEN_DANG_NHAP");

        // Nếu Intent không có dữ liệu thì lấy từ SharedPreferences
        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            tenDangNhap = getSharedPreferences(
                    "QuanLyCongViec",
                    MODE_PRIVATE
            ).getString("TEN_DANG_NHAP", "");
        }

        // Hiển thị mã mời và thông tin tài khoản
        hienThiThongTinTaiKhoan();

        // Hiển thị lịch tuần và đánh dấu ngày hiện tại
        hienThiLichTuan();

        // Nhấn vào tên hoặc "Thêm thông tin" để hoàn thiện hồ sơ
        tvHoTen.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ActivityHome.this,
                    ActivityCompleteProfile.class
            );

            intent.putExtra("TEN_DANG_NHAP", tenDangNhap);
            startActivity(intent);
        });
    }

    // =========================
    // HIỂN THỊ MÃ MỜI TÀI KHOẢN
    // =========================
    private void hienThiThongTinTaiKhoan() {

        if (tenDangNhap == null || tenDangNhap.trim().isEmpty()) {
            tvMaNguoiDung.setText("Mã mời: chưa có");
            tvHoTen.setText("Thêm thông tin");

            Toast.makeText(
                    this,
                    "Không tìm thấy tài khoản đăng nhập",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT ma_moi FROM tai_khoan WHERE ten_dang_nhap = ?",
                    new String[]{tenDangNhap}
            );

            if (cursor.moveToFirst()) {
                String maMoi = cursor.getString(0);

                if (maMoi != null && !maMoi.trim().isEmpty()) {
                    tvMaNguoiDung.setText("Mã mời: " + maMoi);
                } else {
                    tvMaNguoiDung.setText("Mã mời: chưa có");
                }
            } else {
                tvMaNguoiDung.setText("Mã mời: chưa có");
            }

            // Chưa đọc tên hồ sơ vì cần xác định chính xác
            // bảng và cột được sử dụng trong ActivityCompleteProfile.
            tvHoTen.setText("Thêm thông tin");

        } catch (Exception e) {
            tvMaNguoiDung.setText("Mã mời: chưa có");

            Log.e(
                    "HOME_ACCOUNT",
                    "Không thể đọc thông tin tài khoản",
                    e
            );

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // =========================
    // HIỂN THỊ LỊCH TỪ THỨ HAI
    // ĐẾN CHỦ NHẬT
    // =========================
    private void hienThiLichTuan() {

        Calendar homNay = Calendar.getInstance();

        // Tìm ngày thứ Hai của tuần hiện tại
        Calendar thuHai = (Calendar) homNay.clone();

        int thu = thuHai.get(Calendar.DAY_OF_WEEK);

        int soNgayLui;

        if (thu == Calendar.SUNDAY) {
            soNgayLui = 6;
        } else {
            soNgayLui = thu - Calendar.MONDAY;
        }

        thuHai.add(Calendar.DAY_OF_MONTH, -soNgayLui);

        // Tên thứ bằng tiếng Anh: Mon, Tue, Wed...
        SimpleDateFormat dinhDangThu =
                new SimpleDateFormat("EEE", Locale.ENGLISH);

        for (int i = 0; i < 7; i++) {

            Calendar ngay = (Calendar) thuHai.clone();
            ngay.add(Calendar.DAY_OF_MONTH, i);

            String soNgay = String.format(
                    Locale.US,
                    "%02d",
                    ngay.get(Calendar.DAY_OF_MONTH)
            );

            String tenThu = dinhDangThu.format(ngay.getTime());

            TextView tv = cacNgay[i];

            if (tv == null) {
                continue;
            }

            // Hiển thị ngày và thứ trên hai dòng
            tv.setText(soNgay + "\n" + tenThu);
            tv.setGravity(Gravity.CENTER);
            tv.setTypeface(null, Typeface.BOLD);

            // Kiểm tra có phải ngày hiện tại không
            boolean laHomNay =
                    ngay.get(Calendar.YEAR) == homNay.get(Calendar.YEAR)
                            && ngay.get(Calendar.DAY_OF_YEAR)
                            == homNay.get(Calendar.DAY_OF_YEAR);

            if (laHomNay) {

                // Nền trắng bo góc cho ngày hiện tại
                GradientDrawable nen = new GradientDrawable();
                nen.setColor(Color.WHITE);
                nen.setCornerRadius(
                        12 * getResources()
                                .getDisplayMetrics().density
                );

                tv.setBackground(nen);

                // Chữ xanh đậm
                tv.setTextColor(Color.rgb(48, 55, 127));

            } else {

                // Các ngày khác không có nền
                tv.setBackgroundColor(Color.TRANSPARENT);
                tv.setTextColor(Color.WHITE);
            }
        }
    }

    // =========================
    // CẬP NHẬT KHI QUAY LẠI HOME
    // =========================
    @Override
    protected void onResume() {
        super.onResume();

        if (tvMaNguoiDung != null && databaseHelper != null) {
            hienThiThongTinTaiKhoan();
        }

        if (cacNgay != null) {
            hienThiLichTuan();
        }
    }

    // =========================
    // ĐÓNG DATABASE
    // =========================
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}