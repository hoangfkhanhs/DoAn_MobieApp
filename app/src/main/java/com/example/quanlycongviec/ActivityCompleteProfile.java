
package com.example.quanlycongviec;

import android.content.ContentValues;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputEditText;

public class ActivityCompleteProfile extends AppCompatActivity {

    private DatabaseHelper databaseHelper;

    private String tenDangNhap;
    private int idTaiKhoan = -1;
    private String loaiTaiKhoan = "CA_NHAN";

    // Trạng thái loại tài khoản
    private boolean daCoHoSo = false;
    private boolean dangTaiDuLieu = false;

    // Thẻ chọn loại tài khoản
    private MaterialCardView cardTaiKhoanCaNhan;
    private MaterialCardView cardTaiKhoanDoanhNghiep;

    // RadioButton
    private RadioButton rbCaNhan;
    private RadioButton rbDoanhNghiep;

    // Form
    private LinearLayout formCaNhan;
    private LinearLayout formDoanhNghiep;

    // Thông tin cá nhân
    private TextInputEditText edtHoTen;
    private TextInputEditText edtNgaySinh;
    private TextInputEditText edtDiaChi;
    private TextInputEditText edtGioiThieu;
    private TextInputEditText edtSDT;
    private TextInputEditText edtEmail;

    // Thông tin doanh nghiệp
    private TextInputEditText edtTenDoanhNghiep;
    private TextInputEditText edtDiaChiDoanhNghiep;
    private TextInputEditText edtSDTDoanhNghiep;
    private TextInputEditText edtEmailDoanhNghiep;
    private TextInputEditText edtMoTaDoanhNghiep;

    // Nút bấm
    private Button btnTiepTuc;
    private ImageButton btnQuayLai;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);

        databaseHelper = new DatabaseHelper(this);

        anhXaView();

        tenDangNhap = layTenDangNhap();

        if (TextUtils.isEmpty(tenDangNhap)) {
            Toast.makeText(
                    this,
                    "Không tìm thấy tài khoản đăng nhập",
                    Toast.LENGTH_SHORT
            ).show();

            finish();
            return;
        }

        xuLyNutBam();
        taiThongTinDaLuu();
        xuLyChonLoaiTaiKhoan();
    }

    // Ánh xạ View theo ID trong XML
    private void anhXaView() {

        cardTaiKhoanCaNhan =
                findViewById(R.id.cardTaiKhoanCaNhan);

        cardTaiKhoanDoanhNghiep =
                findViewById(R.id.cardTaiKhoanDoanhNghiep);

        rbCaNhan = findViewById(R.id.rbCaNhan);
        rbDoanhNghiep = findViewById(R.id.rbDoanhNghiep);

        formCaNhan = findViewById(R.id.formCaNhan);
        formDoanhNghiep = findViewById(R.id.formDoanhNghiep);

        // Cá nhân
        edtHoTen = findViewById(R.id.edtHoTen);
        edtNgaySinh = findViewById(R.id.edtNgaySinh);
        edtDiaChi = findViewById(R.id.edtDiaChi);
        edtGioiThieu = findViewById(R.id.edtGioiThieu);
        edtSDT = findViewById(R.id.edtSDT);
        edtEmail = findViewById(R.id.edtEmail);

        // Doanh nghiệp
        edtTenDoanhNghiep =
                findViewById(R.id.edtTenDoanhNghiep);

        edtDiaChiDoanhNghiep =
                findViewById(R.id.edtDiaChiDoanhNghiep);

        edtSDTDoanhNghiep =
                findViewById(R.id.edtSDTDoanhNghiep);

        edtEmailDoanhNghiep =
                findViewById(R.id.edtEmailDoanhNghiep);

        edtMoTaDoanhNghiep =
                findViewById(R.id.edtMoTaDoanhNghiep);

        btnTiepTuc = findViewById(R.id.btnTiepTuc);
        btnQuayLai = findViewById(R.id.btnQuayLai2);
    }

    // Lấy tên đăng nhập từ Intent hoặc SharedPreferences
    private String layTenDangNhap() {

        String ten = getIntent().getStringExtra("TEN_DANG_NHAP");

        if (!TextUtils.isEmpty(ten)) {
            return ten;
        }

        SharedPreferences preferences =
                getSharedPreferences(
                        "QuanLyCongViec",
                        MODE_PRIVATE
                );

        return preferences.getString("TEN_DANG_NHAP", "");
    }

    // Tải tài khoản và kiểm tra hồ sơ đã tồn tại hay chưa
    private void taiThongTinDaLuu() {

        SQLiteDatabase db = databaseHelper.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(
                    "tai_khoan",
                    new String[]{
                            "id_tai_khoan",
                            "loai_tai_khoan"
                    },
                    "ten_dang_nhap = ?",
                    new String[]{tenDangNhap},
                    null,
                    null,
                    null
            );

            if (!cursor.moveToFirst()) {
                Toast.makeText(
                        this,
                        "Không tìm thấy tài khoản",
                        Toast.LENGTH_SHORT
                ).show();

                finish();
                return;
            }

            idTaiKhoan = cursor.getInt(
                    cursor.getColumnIndexOrThrow("id_tai_khoan")
            );

            String loaiTrongDatabase = docChuoi(
                    cursor,
                    "loai_tai_khoan"
            );

            if ("DOANH_NGHIEP".equals(loaiTrongDatabase)) {
                loaiTaiKhoan = "DOANH_NGHIEP";
            } else {
                loaiTaiKhoan = "CA_NHAN";
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (idTaiKhoan == -1) {
            return;
        }

        // Chỉ khóa loại tài khoản khi đã có hồ sơ trong SQLite
        loaiTaiKhoan = layLoaiTaiKhoanDaCoHoSo(db);

        if (loaiTaiKhoan != null) {
            daCoHoSo = true;
        } else {
            // Chưa có hồ sơ: cho phép chọn loại tài khoản
            daCoHoSo = false;

            // Mặc định hiển thị Cá nhân nhưng vẫn cho đổi loại
            loaiTaiKhoan = "CA_NHAN";
        }

        dangTaiDuLieu = true;

        try {
            if ("DOANH_NGHIEP".equals(loaiTaiKhoan)) {

                rbDoanhNghiep.setChecked(true);
                rbCaNhan.setChecked(false);

                hienThiFormDoanhNghiep();

                taiHoSoDoanhNghiep(db);

            } else {

                rbCaNhan.setChecked(true);
                rbDoanhNghiep.setChecked(false);

                hienThiFormCaNhan();

                taiHoSoCaNhan(db);
            }

        } finally {
            dangTaiDuLieu = false;
        }

        capNhatTrangThaiChonLoaiTaiKhoan();
    }

    // Trả về loại tài khoản nếu đã có hồ sơ.
    // Trả về null nếu cả hai bảng hồ sơ đều chưa có dữ liệu.
    private String layLoaiTaiKhoanDaCoHoSo(SQLiteDatabase db) {

        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT " +
                            "EXISTS (" +
                            "SELECT 1 FROM ho_so_ca_nhan " +
                            "WHERE id_tai_khoan = ?" +
                            "), " +
                            "EXISTS (" +
                            "SELECT 1 FROM ho_so_doanh_nghiep " +
                            "WHERE id_tai_khoan = ?" +
                            ")",
                    new String[]{
                            String.valueOf(idTaiKhoan),
                            String.valueOf(idTaiKhoan)
                    }
            );

            if (cursor.moveToFirst()) {

                boolean coHoSoCaNhan = cursor.getInt(0) == 1;
                boolean coHoSoDoanhNghiep = cursor.getInt(1) == 1;

                if (coHoSoCaNhan) {
                    return "CA_NHAN";
                }

                if (coHoSoDoanhNghiep) {
                    return "DOANH_NGHIEP";
                }
            }

            return null;

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Chọn loại tài khoản
    private void xuLyChonLoaiTaiKhoan() {

        cardTaiKhoanCaNhan.setOnClickListener(v -> {
            if (daCoHoSo || dangTaiDuLieu) {
                return;
            }

            chonLoaiTaiKhoan("CA_NHAN");
        });

        cardTaiKhoanDoanhNghiep.setOnClickListener(v -> {
            if (daCoHoSo || dangTaiDuLieu) {
                return;
            }

            chonLoaiTaiKhoan("DOANH_NGHIEP");
        });

        rbCaNhan.setOnClickListener(v -> {
            if (daCoHoSo || dangTaiDuLieu) {
                return;
            }

            chonLoaiTaiKhoan("CA_NHAN");
        });

        rbDoanhNghiep.setOnClickListener(v -> {
            if (daCoHoSo || dangTaiDuLieu) {
                return;
            }

            chonLoaiTaiKhoan("DOANH_NGHIEP");
        });
    }

    // Chọn loại và hiển thị form tương ứng
    private void chonLoaiTaiKhoan(String loai) {

        if (daCoHoSo) {
            return;
        }

        loaiTaiKhoan = loai;

        if ("DOANH_NGHIEP".equals(loai)) {

            rbDoanhNghiep.setChecked(true);
            rbCaNhan.setChecked(false);

            hienThiFormDoanhNghiep();

        } else {

            rbCaNhan.setChecked(true);
            rbDoanhNghiep.setChecked(false);

            hienThiFormCaNhan();
        }

        capNhatTrangThaiChonLoaiTaiKhoan();
    }

    // Khóa cả hai lựa chọn nếu đã có hồ sơ
    private void capNhatTrangThaiChonLoaiTaiKhoan() {

        boolean choPhepChon = !daCoHoSo;

        rbCaNhan.setEnabled(choPhepChon);
        rbDoanhNghiep.setEnabled(choPhepChon);

        cardTaiKhoanCaNhan.setClickable(choPhepChon);
        cardTaiKhoanDoanhNghiep.setClickable(choPhepChon);

        // Giữ lựa chọn hiện tại và cập nhật viền thẻ
        capNhatVienThe(
                cardTaiKhoanCaNhan,
                "CA_NHAN".equals(loaiTaiKhoan)
        );

        capNhatVienThe(
                cardTaiKhoanDoanhNghiep,
                "DOANH_NGHIEP".equals(loaiTaiKhoan)
        );
    }

    private void capNhatVienThe(
            MaterialCardView card,
            boolean dangChon
    ) {
        if (dangChon) {
            // Thẻ đang được chọn
            card.setStrokeWidth(dpSangPx(2));
            card.setStrokeColor(
                    android.graphics.Color.parseColor("#30377F")
            );
        } else {
            // Thẻ chưa được chọn
            card.setStrokeWidth(dpSangPx(1));
            card.setStrokeColor(
                    android.graphics.Color.parseColor("#D6DAE8")
            );
        }
    }
    private int dpSangPx(int dp) {
        return (int) (
                dp * getResources().getDisplayMetrics().density + 0.5f
        );
    }

    // Hiển thị form cá nhân
    private void hienThiFormCaNhan() {
        formCaNhan.setVisibility(View.VISIBLE);
        formDoanhNghiep.setVisibility(View.GONE);
    }

    // Hiển thị form doanh nghiệp
    private void hienThiFormDoanhNghiep() {
        formCaNhan.setVisibility(View.GONE);
        formDoanhNghiep.setVisibility(View.VISIBLE);
    }

    // Tải hồ sơ cá nhân
    private void taiHoSoCaNhan(SQLiteDatabase db) {

        Cursor cursor = null;

        try {
            cursor = db.query(
                    "ho_so_ca_nhan",
                    new String[]{
                            "ho_ten",
                            "ngay_sinh",
                            "dia_chi",
                            "gioi_thieu",
                            "so_dien_thoai",
                            "email"
                    },
                    "id_tai_khoan = ?",
                    new String[]{String.valueOf(idTaiKhoan)},
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {
                edtHoTen.setText(docChuoi(cursor, "ho_ten"));
                edtNgaySinh.setText(docChuoi(cursor, "ngay_sinh"));
                edtDiaChi.setText(docChuoi(cursor, "dia_chi"));
                edtGioiThieu.setText(docChuoi(cursor, "gioi_thieu"));
                edtSDT.setText(docChuoi(cursor, "so_dien_thoai"));
                edtEmail.setText(docChuoi(cursor, "email"));
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Tải hồ sơ doanh nghiệp
    private void taiHoSoDoanhNghiep(SQLiteDatabase db) {

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
                    new String[]{String.valueOf(idTaiKhoan)},
                    null,
                    null,
                    null
            );

            if (cursor.moveToFirst()) {

                edtTenDoanhNghiep.setText(
                        docChuoi(cursor, "ten_doanh_nghiep")
                );

                edtDiaChiDoanhNghiep.setText(
                        docChuoi(cursor, "dia_chi")
                );

                edtSDTDoanhNghiep.setText(
                        docChuoi(cursor, "so_dien_thoai")
                );

                edtEmailDoanhNghiep.setText(
                        docChuoi(cursor, "email")
                );

                edtMoTaDoanhNghiep.setText(
                        docChuoi(cursor, "gioi_thieu")
                );
            }

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    // Xử lý nút bấm
    private void xuLyNutBam() {

        btnTiepTuc.setOnClickListener(v -> luuThongTin());

        btnQuayLai.setOnClickListener(v -> finish());
    }

    // Lưu hồ sơ vào SQLite
    private void luuThongTin() {

        if (idTaiKhoan == -1) {
            Toast.makeText(
                    this,
                    "Tài khoản không hợp lệ",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        String bangHoSo;
        boolean thanhCong;

        // Xác định loại tài khoản theo lựa chọn hiện tại
        if (rbCaNhan.isChecked()) {
            loaiTaiKhoan = "CA_NHAN";
            bangHoSo = "ho_so_ca_nhan";

            String hoTen = layText(edtHoTen);

            if (TextUtils.isEmpty(hoTen)) {
                edtHoTen.setError("Vui lòng nhập họ tên");
                edtHoTen.requestFocus();
                return;
            }

            values.put("id_tai_khoan", idTaiKhoan);
            values.put("ho_ten", hoTen);

            putNullable(values, "ngay_sinh", layText(edtNgaySinh));
            putNullable(values, "dia_chi", layText(edtDiaChi));
            putNullable(values, "gioi_thieu", layText(edtGioiThieu));
            putNullable(values, "so_dien_thoai", layText(edtSDT));
            putNullable(values, "email", layText(edtEmail));

        } else if (rbDoanhNghiep.isChecked()) {

            loaiTaiKhoan = "DOANH_NGHIEP";
            bangHoSo = "ho_so_doanh_nghiep";

            String tenDoanhNghiep = layText(edtTenDoanhNghiep);
            String diaChi = layText(edtDiaChiDoanhNghiep);

            if (TextUtils.isEmpty(tenDoanhNghiep)) {
                edtTenDoanhNghiep.setError(
                        "Vui lòng nhập tên doanh nghiệp"
                );
                edtTenDoanhNghiep.requestFocus();
                return;
            }

            if (TextUtils.isEmpty(diaChi)) {
                edtDiaChiDoanhNghiep.setError(
                        "Vui lòng nhập địa chỉ doanh nghiệp"
                );
                edtDiaChiDoanhNghiep.requestFocus();
                return;
            }

            values.put("id_tai_khoan", idTaiKhoan);
            values.put("ten_doanh_nghiep", tenDoanhNghiep);
            values.put("dia_chi", diaChi);

            putNullable(values, "so_dien_thoai", layText(edtSDTDoanhNghiep));
            putNullable(values, "email", layText(edtEmailDoanhNghiep));
            putNullable(values, "gioi_thieu", layText(edtMoTaDoanhNghiep));

        } else {
            Toast.makeText(
                    this,
                    "Vui lòng chọn loại tài khoản",
                    Toast.LENGTH_SHORT
            ).show();
            return;
        }

        // Bắt đầu transaction sau khi kiểm tra dữ liệu đầu vào
        db.beginTransaction();

        try {
            // Chặn lưu loại khác nếu hồ sơ đã được tạo trước đó
            String loaiDaCoHoSo = layLoaiTaiKhoanDaCoHoSo(db);

            if (loaiDaCoHoSo != null
                    && !loaiDaCoHoSo.equals(loaiTaiKhoan)) {

                Toast.makeText(
                        this,
                        "Tài khoản đã có loại hồ sơ và không thể đổi loại",
                        Toast.LENGTH_LONG
                ).show();

                return;
            }

            thanhCong = luuHoSo(db, bangHoSo, values);

            if (!thanhCong) {
                throw new Exception("Không thể lưu hồ sơ");
            }

            ContentValues taiKhoanValues = new ContentValues();
            taiKhoanValues.put("loai_tai_khoan", loaiTaiKhoan);

            int soDongCapNhat = db.update(
                    "tai_khoan",
                    taiKhoanValues,
                    "id_tai_khoan = ?",
                    new String[]{String.valueOf(idTaiKhoan)}
            );

            if (soDongCapNhat <= 0) {
                throw new Exception(
                        "Không thể cập nhật loại tài khoản"
                );
            }

            db.setTransactionSuccessful();

        } catch (Exception e) {

            Toast.makeText(
                    this,
                    "Lưu thất bại. Email hoặc số điện thoại có thể đã được sử dụng.",
                    Toast.LENGTH_LONG
            ).show();

            return;

        } finally {
            db.endTransaction();
        }

        // Sau khi lưu thành công, lần sau sẽ khóa loại tài khoản
        daCoHoSo = true;

        Toast.makeText(
                this,
                "Lưu thông tin thành công",
                Toast.LENGTH_SHORT
        ).show();

        setResult(RESULT_OK);
        finish();
    }

    // Thêm mới hoặc cập nhật hồ sơ
    private boolean luuHoSo(
            SQLiteDatabase db,
            String tenBang,
            ContentValues values
    ) {

        Cursor cursor = null;
        boolean daTonTai;

        try {
            cursor = db.query(
                    tenBang,
                    new String[]{"id_tai_khoan"},
                    "id_tai_khoan = ?",
                    new String[]{String.valueOf(idTaiKhoan)},
                    null,
                    null,
                    null
            );

            daTonTai = cursor.moveToFirst();

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }

        if (daTonTai) {
            return db.update(
                    tenBang,
                    values,
                    "id_tai_khoan = ?",
                    new String[]{String.valueOf(idTaiKhoan)}
            ) >= 0;
        }

        return db.insert(tenBang, null, values) != -1;
    }

    // Đọc chuỗi từ Cursor
    private String docChuoi(Cursor cursor, String tenCot) {

        int index = cursor.getColumnIndex(tenCot);

        if (index < 0 || cursor.isNull(index)) {
            return "";
        }

        return cursor.getString(index);
    }

    // Lấy nội dung EditText
    private String layText(TextInputEditText editText) {

        if (editText == null || editText.getText() == null) {
            return "";
        }

        return editText.getText().toString().trim();
    }

    // Lưu chuỗi rỗng thành NULL
    private void putNullable(
            ContentValues values,
            String tenCot,
            String giaTri
    ) {

        if (TextUtils.isEmpty(giaTri)) {
            values.putNull(tenCot);
        } else {
            values.put(tenCot, giaTri);
        }
    }
}