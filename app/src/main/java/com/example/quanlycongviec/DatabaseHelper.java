package com.example.quanlycongviec;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "QuanLyCongViec.db";
    public static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // 1. TAI KHOAN
        db.execSQL(
                "CREATE TABLE tai_khoan (" +
                        "id_tai_khoan INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "ten_dang_nhap TEXT UNIQUE NOT NULL, " +
                        "mat_khau TEXT NOT NULL, " +
                        "loai_tai_khoan TEXT NOT NULL DEFAULT 'CA_NHAN' " +
                        "CHECK(loai_tai_khoan IN ('CA_NHAN','DOANH_NGHIEP')), " +
                        "ma_moi TEXT UNIQUE, " +
                        "anh_dai_dien TEXT, " +
                        "ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP" +
                        ")"
        );

        // 2. HO SO CA NHAN
        db.execSQL(
                "CREATE TABLE ho_so_ca_nhan (" +
                        "id_tai_khoan INTEGER PRIMARY KEY, " +
                        "ho_ten TEXT NOT NULL, " +
                        "ngay_sinh DATE, " +
                        "gioi_tinh TEXT, " +
                        "dia_chi TEXT, " +
                        "so_dien_thoai TEXT UNIQUE, " +
                        "email TEXT UNIQUE, " +
                        "gioi_thieu TEXT, " +
                        "FOREIGN KEY(id_tai_khoan) " +
                        "REFERENCES tai_khoan(id_tai_khoan) ON DELETE CASCADE" +
                        ")"
        );

        // 3. HO SO DOANH NGHIEP
        db.execSQL(
                "CREATE TABLE ho_so_doanh_nghiep (" +
                        "id_tai_khoan INTEGER PRIMARY KEY, " +
                        "ten_doanh_nghiep TEXT NOT NULL, " +
                        "linh_vuc_hoat_dong TEXT, " +
                        "dia_chi TEXT NOT NULL, " +
                        "so_dien_thoai TEXT, " +
                        "email TEXT, " +
                        "gioi_thieu TEXT, " +
                        "FOREIGN KEY(id_tai_khoan) " +
                        "REFERENCES tai_khoan(id_tai_khoan) ON DELETE CASCADE" +
                        ")"
        );

        // 4. BAI DANG CONG VIEC
        db.execSQL(
                "CREATE TABLE bai_dang_cong_viec (" +
                        "id_cong_viec INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "id_doanh_nghiep INTEGER NOT NULL, " +
                        "ten_cong_viec TEXT NOT NULL, " +
                        "vi_tri_cong_viec TEXT NOT NULL, " +
                        "muc_luong TEXT NOT NULL, " +
                        "dia_diem TEXT NOT NULL, " +
                        "so_luong_can_tuyen INTEGER NOT NULL, " +
                        "trang_thai_tuyen TEXT DEFAULT 'DANG_TUYEN' " +
                        "CHECK(trang_thai_tuyen IN ('DANG_TUYEN','DA_DONG')), " +
                        "mo_ta TEXT, " +
                        "yeu_cau TEXT, " +
                        "tu_dong_duyet_lich INTEGER DEFAULT 0, " +
                        "cho_phep_tu_dang_ky INTEGER DEFAULT 1, " +
                        "ngay_dang DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY(id_doanh_nghiep) " +
                        "REFERENCES tai_khoan(id_tai_khoan) ON DELETE CASCADE" +
                        ")"
        );

        // 5. UNG TUYEN CONG VIEC
        db.execSQL(
                "CREATE TABLE ung_tuyen_cong_viec (" +
                        "id_ung_tuyen INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "id_cong_viec INTEGER NOT NULL, " +
                        "id_ung_vien INTEGER NOT NULL, " +
                        "trang_thai TEXT DEFAULT 'CHO_DUYET' " +
                        "CHECK(trang_thai IN ('CHO_DUYET','DA_DUYET','TU_CHOI')), " +
                        "ngay_ung_tuyen DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY(id_cong_viec) " +
                        "REFERENCES bai_dang_cong_viec(id_cong_viec) ON DELETE CASCADE, " +
                        "FOREIGN KEY(id_ung_vien) " +
                        "REFERENCES tai_khoan(id_tai_khoan) ON DELETE CASCADE, " +
                        "UNIQUE(id_cong_viec, id_ung_vien)" +
                        ")"
        );

        // 6. CONG VIEC DA THAM GIA
        db.execSQL(
                "CREATE TABLE cong_viec_da_tham_gia (" +
                        "id_tham_gia INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "id_ca_nhan INTEGER NOT NULL, " +
                        "id_cong_viec INTEGER NOT NULL, " +
                        "trang_thai TEXT DEFAULT 'DANG_LAM' " +
                        "CHECK(trang_thai IN ('DANG_LAM','DA_NGHI')), " +
                        "ngay_bat_dau DATE DEFAULT CURRENT_DATE, " +
                        "ngay_ket_thuc DATE, " +
                        "FOREIGN KEY(id_ca_nhan) " +
                        "REFERENCES tai_khoan(id_tai_khoan) ON DELETE CASCADE, " +
                        "FOREIGN KEY(id_cong_viec) " +
                        "REFERENCES bai_dang_cong_viec(id_cong_viec) ON DELETE CASCADE, " +
                        "UNIQUE(id_ca_nhan, id_cong_viec)" +
                        ")"
        );

        // 7. CA LAM VIEC
        db.execSQL(
                "CREATE TABLE ca_lam_viec (" +
                        "id_ca INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "ten_ca TEXT NOT NULL, " +
                        "gio_bat_dau TEXT NOT NULL, " +
                        "gio_ket_thuc TEXT NOT NULL" +
                        ")"
        );

        // Them 6 ca mac dinh
        db.execSQL(
                "INSERT INTO ca_lam_viec (ten_ca, gio_bat_dau, gio_ket_thuc) VALUES " +
                        "('Ca 1','00:00','04:00')," +
                        "('Ca 2','04:00','08:00')," +
                        "('Ca 3','08:00','12:00')," +
                        "('Ca 4','12:00','16:00')," +
                        "('Ca 5','16:00','20:00')," +
                        "('Ca 6','20:00','00:00')"
        );

        // 8. DANG KY LICH LAM
        db.execSQL(
                "CREATE TABLE dang_ky_lich_lam (" +
                        "id_dang_ky INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "id_cong_viec INTEGER NOT NULL, " +
                        "id_nguoi_lam INTEGER NOT NULL, " +
                        "ngay_lam_viec DATE NOT NULL, " +
                        "id_ca INTEGER NOT NULL, " +
                        "trang_thai TEXT DEFAULT 'CHO_DUYET' " +
                        "CHECK(trang_thai IN ('CHO_DUYET','DA_DUYET','TU_CHOI','DA_HUY')), " +
                        "ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY(id_cong_viec) " +
                        "REFERENCES bai_dang_cong_viec(id_cong_viec) ON DELETE CASCADE, " +
                        "FOREIGN KEY(id_nguoi_lam) " +
                        "REFERENCES tai_khoan(id_tai_khoan) ON DELETE CASCADE, " +
                        "FOREIGN KEY(id_ca) " +
                        "REFERENCES ca_lam_viec(id_ca) ON DELETE CASCADE, " +
                        "UNIQUE(id_cong_viec, id_nguoi_lam, ngay_lam_viec, id_ca)" +
                        ")"
        );

        // 9. THONG BAO
        db.execSQL(
                "CREATE TABLE thong_bao (" +
                        "id_thong_bao INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "id_nguoi_gui INTEGER NOT NULL, " +
                        "id_nguoi_nhan INTEGER NOT NULL, " +
                        "tieu_de TEXT, " +
                        "noi_dung TEXT NOT NULL, " +
                        "loai_thong_bao TEXT DEFAULT 'HE_THONG' " +
                        "CHECK(loai_thong_bao IN ('HE_THONG','LICH_LAM','UNG_TUYEN')), " +
                        "ngay_gio_lam_viec DATETIME, " +
                        "ngay_tao DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                        "FOREIGN KEY(id_nguoi_gui) " +
                        "REFERENCES tai_khoan(id_tai_khoan) ON DELETE CASCADE, " +
                        "FOREIGN KEY(id_nguoi_nhan) " +
                        "REFERENCES tai_khoan(id_tai_khoan) ON DELETE CASCADE" +
                        ")"
        );

        // TRANG THAI XEM THONG BAO
        db.execSQL(
                "CREATE TABLE trang_thai_xem_thong_bao (" +
                        "id_thong_bao INTEGER PRIMARY KEY, " +
                        "id_tai_khoan INTEGER NOT NULL, " +
                        "da_xem INTEGER DEFAULT 0 CHECK(da_xem IN (0,1)), " +
                        "thoi_gian_xem DATETIME, " +
                        "FOREIGN KEY(id_thong_bao) " +
                        "REFERENCES thong_bao(id_thong_bao) ON DELETE CASCADE, " +
                        "FOREIGN KEY(id_tai_khoan) " +
                        "REFERENCES tai_khoan(id_tai_khoan) ON DELETE CASCADE, " +
                        "UNIQUE(id_thong_bao, id_tai_khoan)" +
                        ")"
        );
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db, int oldVersion, int newVersion) {
        // Khi thay doi cau truc bang, can viet lenh migration.
        // Khong xoa bang tu dong de tranh mat du lieu.
    }

    // Them tai khoan moi
    public long themTaiKhoan(
            String tenDangNhap,
            String matKhau,
            String loaiTaiKhoan) {

        SQLiteDatabase db = this.getWritableDatabase();

        long ketQua = -1;

        db.beginTransaction();

        Cursor cursor = null;

        try {
            // Kiểm tra tên đăng nhập đã tồn tại chưa
            cursor = db.rawQuery(
                    "SELECT id_tai_khoan FROM tai_khoan " +
                            "WHERE ten_dang_nhap = ?",
                    new String[]{tenDangNhap}
            );

            boolean daTonTai = cursor.moveToFirst();
            cursor.close();
            cursor = null;

            if (daTonTai) {
                return -1;
            }

            // Tìm mã mời lớn nhất hiện có
            cursor = db.rawQuery(
                    "SELECT MAX(CAST(ma_moi AS INTEGER)) " +
                            "FROM tai_khoan",
                    null
            );

            int maMoiTiepTheo = 1;

            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                maMoiTiepTheo = cursor.getInt(0) + 1;
            }

            cursor.close();
            cursor = null;

            // Định dạng mã mời thành 6 chữ số
            String maMoi = String.format(
                    java.util.Locale.US,
                    "%06d",
                    maMoiTiepTheo
            );

            // Tạo tài khoản
            android.content.ContentValues values =
                    new android.content.ContentValues();

            values.put("ten_dang_nhap", tenDangNhap);
            values.put("mat_khau", matKhau);
            values.put("loai_tai_khoan", loaiTaiKhoan);
            values.put("ma_moi", maMoi);

            ketQua = db.insert("tai_khoan", null, values);

            if (ketQua != -1) {
                db.setTransactionSuccessful();
            }

        } catch (Exception e) {
            android.util.Log.e(
                    "DATABASE_REGISTER",
                    "Lỗi tạo tài khoản",
                    e
            );
            ketQua = -1;

        } finally {
            if (cursor != null) {
                cursor.close();
            }

            db.endTransaction();
        }

        return ketQua;
    }

    // Kiem tra ten dang nhap va mat khau da bam
    public boolean kiemTraTaiKhoan(
            String tenDangNhap,
            String matKhauDaBam) {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.query(
                "tai_khoan",
                new String[]{"id_tai_khoan"},
                "ten_dang_nhap = ? AND mat_khau = ?",
                new String[]{tenDangNhap, matKhauDaBam},
                null, null, null
        );

        boolean hopLe = cursor.moveToFirst();
        cursor.close();

        return hopLe;
    }

    // Dem so bang da tao (chi dem 9 bang nghiep vu)
    public int demSoBang() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM sqlite_master " +
                        "WHERE type='table' AND name NOT LIKE 'sqlite_%'",
                null
        );

        int soBang = 0;

        if (cursor.moveToFirst()) {
            soBang = cursor.getInt(0);
        }

        cursor.close();
        return soBang;
    }

    // Dem so ca lam viec
    public int demSoCa() {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM ca_lam_viec",
                null
        );

        int soCa = 0;

        if (cursor.moveToFirst()) {
            soCa = cursor.getInt(0);
        }

        cursor.close();
        return soCa;
    }
    // Kiểm tra tài khoản đã có hồ sơ được lưu hay chưa
    public String layLoaiTaiKhoanDaCoHoSo(int idTaiKhoan) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = null;

        try {
            cursor = db.rawQuery(
                    "SELECT loai_tai_khoan FROM tai_khoan " +
                            "WHERE id_tai_khoan = ? " +
                            "AND (" +
                            "EXISTS (SELECT 1 FROM ho_so_ca_nhan " +
                            "WHERE ho_so_ca_nhan.id_tai_khoan = tai_khoan.id_tai_khoan) " +
                            "OR " +
                            "EXISTS (SELECT 1 FROM ho_so_doanh_nghiep " +
                            "WHERE ho_so_doanh_nghiep.id_tai_khoan = tai_khoan.id_tai_khoan)" +
                            ")",
                    new String[]{String.valueOf(idTaiKhoan)}
            );

            if (cursor.moveToFirst()) {
                return cursor.getString(0);
            }

            // Chưa có hồ sơ: chưa khóa loại tài khoản
            return null;

        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }
}