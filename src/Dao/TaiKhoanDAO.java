package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Model.TaiKhoan;
import until.DBConnection;

public class TaiKhoanDAO {

    // ===== CHIÊU THỨ 1: Đăng nhập - Kiểm tra username + password =====
    public TaiKhoan dangNhap(String username, String password) {
        String sql = "SELECT * FROM TaiKhoan WHERE username = ? AND password = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    TaiKhoan tk = new TaiKhoan();
                    tk.setId(rs.getInt("id"));
                    tk.setUsername(rs.getString("username"));
                    tk.setPassword(rs.getString("password"));
                    tk.setVaiTro(rs.getString("vai_tro"));
                    return tk;
                }
            }
        } catch (SQLException e) {
            System.err.println(">> [Lỗi] Không thể đăng nhập!");
            e.printStackTrace();
        }
        return null; // Trả null nếu sai tk/mk
    }

    // ===== CHIÊU THỨ 2: Lấy toàn bộ danh sách tài khoản =====
    public List<TaiKhoan> layDanhSachTaiKhoan() {
        List<TaiKhoan> list = new ArrayList<>();
        String sql = "SELECT * FROM TaiKhoan";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TaiKhoan tk = new TaiKhoan();
                tk.setId(rs.getInt("id"));
                tk.setUsername(rs.getString("username"));
                tk.setPassword(rs.getString("password"));
                tk.setVaiTro(rs.getString("vai_tro"));
                list.add(tk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== CHIÊU THỨ 3: Thêm tài khoản mới =====
    public boolean themTaiKhoan(TaiKhoan tk) {
        String sql = "INSERT INTO TaiKhoan (username, password, vai_tro) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tk.getUsername());
            ps.setString(2, tk.getPassword());
            ps.setString(3, tk.getVaiTro());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(">> [Lỗi] Không thể thêm tài khoản! Username có thể đã tồn tại.");
            e.printStackTrace();
            return false;
        }
    }

    // ===== CHIÊU THỨ 4: Cập nhật thông tin tài khoản =====
    public boolean capNhatTaiKhoan(TaiKhoan tk) {
        String sql = "UPDATE TaiKhoan SET username = ?, password = ?, vai_tro = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, tk.getUsername());
            ps.setString(2, tk.getPassword());
            ps.setString(3, tk.getVaiTro());
            ps.setInt(4, tk.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== CHIÊU THỨ 5: Xóa tài khoản theo ID =====
    public boolean xoaTaiKhoan(int id) {
        String sql = "DELETE FROM TaiKhoan WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== CHIÊU THỨ 6: Kiểm tra username đã tồn tại chưa =====
    public boolean kiemTraUsernameExists(String username) {
        String sql = "SELECT COUNT(*) FROM TaiKhoan WHERE username = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
