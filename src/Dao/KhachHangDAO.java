package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Model.KhachHang;
import until.DBConnection;

public class KhachHangDAO {

    // ===== CHIÊU THỨ 1: Lấy toàn bộ danh sách khách hàng =====
    public List<KhachHang> layDanhSachKhachHang() {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang ORDER BY ten_kh ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                KhachHang kh = new KhachHang();
                kh.setId(rs.getInt("id"));
                kh.setTenKh(rs.getString("ten_kh"));
                kh.setSdt(rs.getString("sdt"));
                kh.setDiaChi(rs.getString("dia_chi"));
                list.add(kh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // ===== CHIÊU THỨ 2: Tìm khách hàng theo số điện thoại =====
    public KhachHang timKhachHangTheoSdt(String sdt) {
        String sql = "SELECT * FROM KhachHang WHERE sdt = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sdt);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    KhachHang kh = new KhachHang();
                    kh.setId(rs.getInt("id"));
                    kh.setTenKh(rs.getString("ten_kh"));
                    kh.setSdt(rs.getString("sdt"));
                    kh.setDiaChi(rs.getString("dia_chi"));
                    return kh;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Không tìm thấy
    }

    // ===== CHIÊU THỨ 3: Tìm khách hàng theo ID =====
    public KhachHang timKhachHangTheoId(int id) {
        String sql = "SELECT * FROM KhachHang WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    KhachHang kh = new KhachHang();
                    kh.setId(rs.getInt("id"));
                    kh.setTenKh(rs.getString("ten_kh"));
                    kh.setSdt(rs.getString("sdt"));
                    kh.setDiaChi(rs.getString("dia_chi"));
                    return kh;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // ===== CHIÊU THỨ 4: Thêm khách hàng mới =====
    public boolean themKhachHang(KhachHang kh) {
        String sql = "INSERT INTO KhachHang (ten_kh, sdt, dia_chi) VALUES (?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, kh.getTenKh());
            ps.setString(2, kh.getSdt());
            ps.setString(3, kh.getDiaChi());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(">> [Lỗi] Không thể thêm khách hàng! SĐT có thể đã tồn tại.");
            e.printStackTrace();
            return false;
        }
    }

    // ===== CHIÊU THỨ 5: Cập nhật thông tin khách hàng =====
    public boolean capNhatKhachHang(KhachHang kh) {
        String sql = "UPDATE KhachHang SET ten_kh = ?, sdt = ?, dia_chi = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, kh.getTenKh());
            ps.setString(2, kh.getSdt());
            ps.setString(3, kh.getDiaChi());
            ps.setInt(4, kh.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // ===== CHIÊU THỨ 6: Xóa khách hàng theo ID =====
    // Lưu ý: Chỉ xóa được nếu khách hàng chưa có đơn hàng nào (khóa ngoại)
    public boolean xoaKhachHang(int id) {
        String sql = "DELETE FROM KhachHang WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(">> [Lỗi] Không thể xóa khách hàng! Có thể đang có đơn hàng liên quan.");
            e.printStackTrace();
            return false;
        }
    }

    // ===== CHIÊU THỨ 7: Tìm kiếm khách hàng theo tên (LIKE) =====
    public List<KhachHang> timKiemTheoTen(String tuKhoa) {
        List<KhachHang> list = new ArrayList<>();
        String sql = "SELECT * FROM KhachHang WHERE ten_kh LIKE ? ORDER BY ten_kh ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + tuKhoa + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    KhachHang kh = new KhachHang();
                    kh.setId(rs.getInt("id"));
                    kh.setTenKh(rs.getString("ten_kh"));
                    kh.setSdt(rs.getString("sdt"));
                    kh.setDiaChi(rs.getString("dia_chi"));
                    list.add(kh);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
