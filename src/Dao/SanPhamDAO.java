package Dao;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import Model.SanPham;
import until.DBConnection;

public class SanPhamDAO {

    // Chiêu thứ 1: Rút kiếm (Lấy toàn bộ danh sách sản phẩm)
    public List<SanPham> layDanhSachSanPham() {
        List<SanPham> list = new ArrayList<>();
        String sql = "SELECT * FROM SanPham";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                SanPham sp = new SanPham();
                sp.setId(rs.getInt("id"));
                sp.setTenSp(rs.getString("ten_sp"));
                sp.setHangSx(rs.getString("hang_sx"));
                sp.setGiaBan(rs.getDouble("gia_ban"));
                sp.setSoLuong(rs.getInt("so_luong"));
                sp.setUrlAnh(rs.getString("url_anh"));
                
                list.add(sp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Chiêu thứ 2: Phóng ám khí (Thêm sản phẩm mới vào kho)
    public boolean themSanPham(SanPham sp) {
        String sql = "INSERT INTO SanPham (ten_sp, hang_sx, gia_ban, so_luong, url_anh) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            // Nhét dữ liệu vào các dấu chấm hỏi (?)
            ps.setString(1, sp.getTenSp());
            ps.setString(2, sp.getHangSx());
            ps.setDouble(3, sp.getGiaBan());
            ps.setInt(4, sp.getSoLuong());
            ps.setString(5, sp.getUrlAnh());
            
            int soDongAnhHuong = ps.executeUpdate();
            return soDongAnhHuong > 0; // Trả về true nếu Insert thành công
            
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Chiêu thứ 3: Chỉnh kiếm (Cập nhật thông tin sản phẩm)
    public boolean suaSanPham(SanPham sp) {
        String sql = "UPDATE SanPham SET ten_sp = ?, hang_sx = ?, gia_ban = ?, so_luong = ?, url_anh = ? WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sp.getTenSp());
            ps.setString(2, sp.getHangSx());
            ps.setDouble(3, sp.getGiaBan());
            ps.setInt(4, sp.getSoLuong());
            ps.setString(5, sp.getUrlAnh());
            ps.setInt(6, sp.getId());

            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Chiêu thứ 4: Tiêu hủy vũ khí (Xóa sản phẩm)
    // Cảnh báo: Chỉ xóa được nếu sản phẩm chưa nằm trong đơn hàng nào!
    public boolean xoaSanPham(int id) {
        String sql = "DELETE FROM SanPham WHERE id = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println(">> [Lỗi] Không thể xóa! Sản phẩm đang có trong đơn hàng.");
            e.printStackTrace();
            return false;
        }
    }

    // Chiêu thứ 5: Dò huyệt (Tìm kiếm sản phẩm theo tên)
    public List<SanPham> timKiemTheoTen(String tuKhoa) {
        List<SanPham> list = new ArrayList<>();
        String sql = "SELECT * FROM SanPham WHERE ten_sp LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + tuKhoa + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SanPham sp = new SanPham();
                    sp.setId(rs.getInt("id"));
                    sp.setTenSp(rs.getString("ten_sp"));
                    sp.setHangSx(rs.getString("hang_sx"));
                    sp.setGiaBan(rs.getDouble("gia_ban"));
                    sp.setSoLuong(rs.getInt("so_luong"));
                    sp.setUrlAnh(rs.getString("url_anh"));
                    list.add(sp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Chiêu thứ 6: Lọc theo hãng sản xuất
    public List<SanPham> locTheoHangSanXuat(String hangSx) {
        List<SanPham> list = new ArrayList<>();
        String sql = "SELECT * FROM SanPham WHERE hang_sx = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, hangSx);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    SanPham sp = new SanPham();
                    sp.setId(rs.getInt("id"));
                    sp.setTenSp(rs.getString("ten_sp"));
                    sp.setHangSx(rs.getString("hang_sx"));
                    sp.setGiaBan(rs.getDouble("gia_ban"));
                    sp.setSoLuong(rs.getInt("so_luong"));
                    sp.setUrlAnh(rs.getString("url_anh"));
                    list.add(sp);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Test thử võ công ngay tại Console, không cần Giao diện
    public static void main(String[] args) {
        SanPhamDAO dao = new SanPhamDAO();
        
        // 1. Thử đâm 1 nhát (Thêm sản phẩm)
        SanPham spMoi = new SanPham(0, "iPhone 15 Pro Max", "Apple", 29990000, 10, "https://cloudinary.com/link_ao.jpg");
        boolean ketQua = dao.themSanPham(spMoi);
        System.out.println("Trạng thái nạp đạn: " + (ketQua ? "Thành công" : "Thất bại"));
        
        // 2. Thử gom hàng (Đọc danh sách)
        List<SanPham> khoHang = dao.layDanhSachSanPham();
        System.out.println("=== KHO HÀNG HIỆN TẠI ===");
        for (SanPham sp : khoHang) {
            System.out.println("- " + sp.getTenSp() + " | Giá: " + sp.getGiaBan() + " | SL: " + sp.getSoLuong());
        }
    }
}