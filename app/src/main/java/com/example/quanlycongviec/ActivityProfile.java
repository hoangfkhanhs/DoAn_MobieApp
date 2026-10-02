package com.example.quanlycongviec;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.InputStream;

public class ActivityProfile extends AppCompatActivity {

    private DatabaseHelper dbHelper;

    private TextView tvHoTen;
    private TextView tvNgaySinh;
    private TextView tvGioiThieu;
    private TextView tvDiaChi;
    private TextView tvSDT;
    private TextView tvEmail;

    private ImageView imgAvatar;

    private String tenDangNhap = "";

    private static final String THONG_TIN_TRONG = "Thêm thông tin";

    // Bộ chọn ảnh
    private ActivityResultLauncher<String[]> chonAnhLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        dbHelper = new DatabaseHelper(this);

        // ==========================================
        // ÁNH XẠ VIEW
        // ==========================================

        anhXaView();

        // ==========================================
        // LẤY TÊN ĐĂNG NHẬP
        // ==========================================

        layTenDangNhap();

        // ==========================================
        // HIỂN THỊ THÔNG TIN
        // ==========================================

        hienThiThongTin();

        // ==========================================
        // ĐĂNG KÝ BỘ CHỌN ẢNH
        // ==========================================

        chonAnhLauncher = registerForActivityResult(
                new ActivityResultContracts.OpenDocument(),
                uri -> {

                    if (uri != null) {

                        luuAnhDaiDien(uri);
                    }
                }
        );

        // ==========================================
        // NÚT ĐỔI ẢNH ĐẠI DIỆN
        // ==========================================

        findViewById(R.id.layoutDoiAnh).setOnClickListener(v -> {

            chonAnhLauncher.launch(
                    new String[]{"image/*"}
            );

        });

        // ==========================================
        // NHẤN TRỰC TIẾP ẢNH ĐỂ ĐỔI
        // ==========================================

        if (imgAvatar != null) {

            imgAvatar.setOnClickListener(v -> {

                chonAnhLauncher.launch(
                        new String[]{"image/*"}
                );

            });
        }

        // ==========================================
        // CHỈNH SỬA THÔNG TIN
        // ==========================================

        findViewById(R.id.btnSuaThongTin)
                .setOnClickListener(v -> {

                    Intent intent = new Intent(
                            ActivityProfile.this,
                            ActivityCompleteProfile.class
                    );

                    intent.putExtra(
                            "TEN_DANG_NHAP",
                            tenDangNhap
                    );

                    startActivity(intent);
                });

        // ==========================================
        // ĐỔI MẬT KHẨU
        // ==========================================

        findViewById(R.id.btnDoiMatKhau)
                .setOnClickListener(v -> {

                    Toast.makeText(
                            this,
                            "Bạn cần kết nối nút này với màn hình đổi mật khẩu.",
                            Toast.LENGTH_SHORT
                    ).show();

                });

        // ==========================================
        // NAV HOME
        // ==========================================

        findViewById(R.id.navHome)
                .setOnClickListener(v -> {

                    Intent intent = new Intent(
                            ActivityProfile.this,
                            ActivityHome.class
                    );

                    intent.putExtra(
                            "TEN_DANG_NHAP",
                            tenDangNhap
                    );

                    startActivity(intent);

                    finish();
                });

        // ==========================================
        // NAV NOTIFICATION
        // ==========================================

        findViewById(R.id.navNotification)
                .setOnClickListener(v -> {

                    try {

                        Intent intent = new Intent(
                                ActivityProfile.this,
                                ActivityBell.class
                        );

                        intent.putExtra(
                                "TEN_DANG_NHAP",
                                tenDangNhap
                        );

                        startActivity(intent);

                    } catch (Exception e) {

                        Toast.makeText(
                                this,
                                "Không thể mở trang thông báo.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                });

        // ==========================================
        // NAV GROUP
        // ==========================================

        findViewById(R.id.navGroup)
                .setOnClickListener(v -> {

                    try {

                        Intent intent = new Intent(
                                ActivityProfile.this,
                                ActivityGroup.class
                        );

                        intent.putExtra(
                                "TEN_DANG_NHAP",
                                tenDangNhap
                        );

                        startActivity(intent);

                    } catch (Exception e) {

                        Toast.makeText(
                                this,
                                "Không thể mở trang nhóm.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }

                });
    }

    // =========================================================
    // ÁNH XẠ VIEW
    // =========================================================

    private void anhXaView() {

        tvHoTen = findViewById(R.id.tvHoTen);

        tvNgaySinh = findViewById(R.id.tvNgaySinh);

        tvGioiThieu = findViewById(R.id.tvGioiThieu);

        tvDiaChi = findViewById(R.id.tvDiaChi);

        tvSDT = findViewById(R.id.tvSDT);

        tvEmail = findViewById(R.id.tvEmail);

        imgAvatar = findViewById(R.id.imgAvatar);
    }

    // =========================================================
    // LẤY TÊN ĐĂNG NHẬP
    // =========================================================

    private void layTenDangNhap() {

        // Ưu tiên lấy từ Intent
        Intent intent = getIntent();

        if (intent != null) {

            String ten = intent.getStringExtra(
                    "TEN_DANG_NHAP"
            );

            if (ten != null &&
                    !ten.trim().isEmpty()) {

                tenDangNhap = ten.trim();
            }
        }

        // Nếu Intent không có dữ liệu
        // thì lấy từ SharedPreferences
        if (tenDangNhap.isEmpty()) {

            SharedPreferences preferences =
                    getSharedPreferences(
                            "QuanLyCongViec",
                            MODE_PRIVATE
                    );

            tenDangNhap = preferences.getString(
                    "TEN_DANG_NHAP",
                    ""
            );
        }
    }

    // =========================================================
    // HIỂN THỊ THÔNG TIN
    // =========================================================

    private void hienThiThongTin() {

        // Mặc định
        ganText(tvHoTen, null);

        ganText(tvNgaySinh, null);

        ganText(tvGioiThieu, null);

        ganText(tvDiaChi, null);

        ganText(tvSDT, null);

        ganText(tvEmail, null);

        if (tenDangNhap.isEmpty()) {

            Toast.makeText(
                    this,
                    "Không tìm thấy tài khoản đang đăng nhập.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        SQLiteDatabase db =
                dbHelper.getReadableDatabase();

        Cursor cursorTaiKhoan = null;

        try {

            // ==========================================
            // LẤY THÔNG TIN TÀI KHOẢN
            // ==========================================

            cursorTaiKhoan = db.query(
                    "tai_khoan",

                    new String[]{
                            "id_tai_khoan",
                            "loai_tai_khoan",
                            "anh_dai_dien"
                    },

                    "ten_dang_nhap = ?",

                    new String[]{
                            tenDangNhap
                    },

                    null,
                    null,
                    null
            );

            if (!cursorTaiKhoan.moveToFirst()) {

                Toast.makeText(
                        this,
                        "Không tìm thấy tài khoản.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // ==========================================
            // ID TÀI KHOẢN
            // ==========================================

            long idTaiKhoan =
                    cursorTaiKhoan.getLong(
                            cursorTaiKhoan.getColumnIndexOrThrow(
                                    "id_tai_khoan"
                            )
                    );

            // ==========================================
            // LOẠI TÀI KHOẢN
            // ==========================================

            String loaiTaiKhoan =
                    cursorTaiKhoan.getString(
                            cursorTaiKhoan.getColumnIndexOrThrow(
                                    "loai_tai_khoan"
                            )
                    );

            // ==========================================
            // ĐƯỜNG DẪN ẢNH
            // ==========================================

            String duongDanAnh =
                    cursorTaiKhoan.getString(
                            cursorTaiKhoan.getColumnIndexOrThrow(
                                    "anh_dai_dien"
                            )
                    );

            // Hiển thị ảnh
            hienThiAnhDaiDien(duongDanAnh);

            // ==========================================
            // HIỂN THỊ HỒ SƠ
            // ==========================================

            if ("DOANH_NGHIEP".equals(loaiTaiKhoan)) {

                hienThiHoSoDoanhNghiep(
                        db,
                        idTaiKhoan
                );

            } else {

                hienThiHoSoCaNhan(
                        db,
                        idTaiKhoan
                );
            }

        } catch (Exception e) {

            Log.e(
                    "ACTIVITY_PROFILE",
                    "Lỗi tải hồ sơ",
                    e
            );

            Toast.makeText(
                    this,
                    "Không thể tải thông tin hồ sơ.",
                    Toast.LENGTH_SHORT
            ).show();

        } finally {

            if (cursorTaiKhoan != null) {

                cursorTaiKhoan.close();
            }
        }
    }

    // =========================================================
    // HỒ SƠ CÁ NHÂN
    // =========================================================

    private void hienThiHoSoCaNhan(
            SQLiteDatabase db,
            long idTaiKhoan
    ) {

        Cursor cursor = null;

        try {

            cursor = db.query(
                    "ho_so_ca_nhan",

                    new String[]{
                            "ho_ten",
                            "ngay_sinh",
                            "gioi_thieu",
                            "dia_chi",
                            "so_dien_thoai",
                            "email"
                    },

                    "id_tai_khoan = ?",

                    new String[]{
                            String.valueOf(idTaiKhoan)
                    },

                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {

                // Họ tên
                ganText(
                        tvHoTen,
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "ho_ten"
                                )
                        )
                );

                // Ngày sinh
                ganText(
                        tvNgaySinh,
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "ngay_sinh"
                                )
                        )
                );

                // Giới thiệu
                ganText(
                        tvGioiThieu,
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "gioi_thieu"
                                )
                        )
                );

                // Địa chỉ
                ganText(
                        tvDiaChi,
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "dia_chi"
                                )
                        )
                );

                // Số điện thoại
                ganText(
                        tvSDT,
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "so_dien_thoai"
                                )
                        )
                );

                // Email
                ganText(
                        tvEmail,
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "email"
                                )
                        )
                );
            }

        } finally {

            if (cursor != null) {

                cursor.close();
            }
        }
    }

    // =========================================================
    // HỒ SƠ DOANH NGHIỆP
    // =========================================================

    private void hienThiHoSoDoanhNghiep(
            SQLiteDatabase db,
            long idTaiKhoan
    ) {

        Cursor cursor = null;

        try {

            cursor = db.query(
                    "ho_so_doanh_nghiep",

                    new String[]{
                            "ten_doanh_nghiep",
                            "dia_chi",
                            "so_dien_thoai",
                            "email",
                            "gioi_thieu"
                    },

                    "id_tai_khoan = ?",

                    new String[]{
                            String.valueOf(idTaiKhoan)
                    },

                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {

                // Tên doanh nghiệp
                ganText(
                        tvHoTen,
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "ten_doanh_nghiep"
                                )
                        )
                );

                // Doanh nghiệp không có ngày sinh
                ganText(
                        tvNgaySinh,
                        null
                );

                // Giới thiệu
                ganText(
                        tvGioiThieu,
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "gioi_thieu"
                                )
                        )
                );

                // Địa chỉ
                ganText(
                        tvDiaChi,
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "dia_chi"
                                )
                        )
                );

                // Số điện thoại
                ganText(
                        tvSDT,
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "so_dien_thoai"
                                )
                        )
                );

                // Email
                ganText(
                        tvEmail,
                        cursor.getString(
                                cursor.getColumnIndexOrThrow(
                                        "email"
                                )
                        )
                );
            }

        } finally {

            if (cursor != null) {

                cursor.close();
            }
        }
    }

    // =========================================================
    // GÁN TEXT
    // =========================================================

    private void ganText(
            TextView textView,
            String giaTri
    ) {

        if (textView == null) {
            return;
        }

        if (giaTri == null ||
                giaTri.trim().isEmpty()) {

            textView.setText(
                    THONG_TIN_TRONG
            );

        } else {

            textView.setText(
                    giaTri.trim()
            );
        }
    }

    // =========================================================
    // HIỂN THỊ ẢNH ĐẠI DIỆN
    // =========================================================

    private void hienThiAnhDaiDien(
            String duongDanAnh
    ) {

        if (imgAvatar == null) {
            return;
        }

        // Không có ảnh
        if (duongDanAnh == null ||
                duongDanAnh.trim().isEmpty()) {

            return;
        }

        InputStream inputStream = null;

        try {

            Uri uri = Uri.parse(
                    duongDanAnh
            );

            inputStream =
                    getContentResolver()
                            .openInputStream(uri);

            if (inputStream == null) {

                Log.e(
                        "ACTIVITY_PROFILE",
                        "Không mở được InputStream của ảnh."
                );

                return;
            }

            Bitmap bitmap =
                    BitmapFactory.decodeStream(
                            inputStream
                    );

            if (bitmap != null) {

                imgAvatar.setImageBitmap(
                        bitmap
                );

            } else {

                Log.e(
                        "ACTIVITY_PROFILE",
                        "Bitmap bị null, không thể giải mã ảnh."
                );
            }

        } catch (Exception e) {

            Log.e(
                    "ACTIVITY_PROFILE",
                    "Lỗi hiển thị ảnh đại diện",
                    e
            );

        } finally {

            if (inputStream != null) {

                try {

                    inputStream.close();

                } catch (Exception e) {

                    Log.e(
                            "ACTIVITY_PROFILE",
                            "Không thể đóng InputStream",
                            e
                    );
                }
            }
        }
    }

    // =========================================================
    // LƯU ẢNH ĐẠI DIỆN
    // =========================================================

    private void luuAnhDaiDien(Uri uri) {

        if (uri == null) {
            return;
        }

        // ==========================================
        // KIỂM TRA TÀI KHOẢN
        // ==========================================

        if (tenDangNhap == null ||
                tenDangNhap.trim().isEmpty()) {

            Toast.makeText(
                    this,
                    "Không xác định được tài khoản.",
                    Toast.LENGTH_SHORT
            ).show();

            return;
        }

        // ==========================================
        // LƯU QUYỀN ĐỌC URI
        // ==========================================

        try {

            getContentResolver()
                    .takePersistableUriPermission(
                            uri,
                            Intent.FLAG_GRANT_READ_URI_PERMISSION
                    );

        } catch (Exception e) {

            Log.e(
                    "ACTIVITY_PROFILE",
                    "Không thể lưu quyền đọc ảnh.",
                    e
            );
        }

        // ==========================================
        // KIỂM TRA ẢNH TRƯỚC KHI LƯU
        // ==========================================

        InputStream inputStream = null;

        try {

            inputStream =
                    getContentResolver()
                            .openInputStream(uri);

            if (inputStream == null) {

                Toast.makeText(
                        this,
                        "Không thể đọc ảnh đã chọn.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            Bitmap bitmap =
                    BitmapFactory.decodeStream(
                            inputStream
                    );

            if (bitmap == null) {

                Toast.makeText(
                        this,
                        "Ảnh không hợp lệ.",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            // Hiển thị ảnh ngay
            imgAvatar.setImageBitmap(
                    bitmap
            );

        } catch (Exception e) {

            Log.e(
                    "ACTIVITY_PROFILE",
                    "Không thể đọc ảnh.",
                    e
            );

            Toast.makeText(
                    this,
                    "Không thể đọc ảnh đã chọn.",
                    Toast.LENGTH_SHORT
            ).show();

            return;

        } finally {

            if (inputStream != null) {

                try {

                    inputStream.close();

                } catch (Exception ignored) {
                }
            }
        }

        // ==========================================
        // LƯU URI VÀO SQLITE
        // ==========================================

        try {

            SQLiteDatabase db =
                    dbHelper.getWritableDatabase();

            ContentValues values =
                    new ContentValues();

            values.put(
                    "anh_dai_dien",
                    uri.toString()
            );

            int soDongCapNhat =
                    db.update(
                            "tai_khoan",

                            values,

                            "ten_dang_nhap = ?",

                            new String[]{
                                    tenDangNhap
                            }
                    );

            if (soDongCapNhat > 0) {

                Toast.makeText(
                        this,
                        "Đã cập nhật ảnh đại diện.",
                        Toast.LENGTH_SHORT
                ).show();

            } else {

                Toast.makeText(
                        this,
                        "Không tìm thấy tài khoản.",
                        Toast.LENGTH_SHORT
                ).show();
            }

        } catch (Exception e) {

            Log.e(
                    "ACTIVITY_PROFILE",
                    "Lỗi lưu ảnh đại diện.",
                    e
            );

            Toast.makeText(
                    this,
                    "Không thể lưu ảnh đại diện.",
                    Toast.LENGTH_SHORT
            ).show();
        }
    }

    // =========================================================
    // TẢI LẠI KHI QUAY LẠI TRANG
    // =========================================================

    @Override
    protected void onResume() {

        super.onResume();

        if (dbHelper != null) {

            layTenDangNhap();

            hienThiThongTin();
        }
    }
}