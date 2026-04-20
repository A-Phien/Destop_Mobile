package until;

import Model.TaiKhoan;

/**
 * Singleton lưu thông tin tài khoản đang đăng nhập.
 * Dùng ở bất kỳ đâu: UserSession.getInstance().getTaiKhoanHienTai()
 */
public class UserSession {

    private static UserSession instance;
    private TaiKhoan taiKhoanHienTai;

    private UserSession() {}

    public static UserSession getInstance() {
        if (instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    /** Gọi khi đăng nhập thành công */
    public void dangNhap(TaiKhoan tk) {
        this.taiKhoanHienTai = tk;
    }

    /** Gọi khi đăng xuất */
    public void dangXuat() {
        this.taiKhoanHienTai = null;
    }

    public TaiKhoan getTaiKhoanHienTai() {
        return taiKhoanHienTai;
    }

    /** Kiểm tra đã đăng nhập chưa */
    public boolean daDangNhap() {
        return taiKhoanHienTai != null;
    }

    /** Kiểm tra có phải ADMIN không */
    public boolean laAdmin() {
        return daDangNhap() && "ADMIN".equalsIgnoreCase(taiKhoanHienTai.getVaiTro());
    }
}
