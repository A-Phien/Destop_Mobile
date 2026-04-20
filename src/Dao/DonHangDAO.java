package Dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import Model.ChiTietDon;
import Model.ChiTietHoaDonViewModel;
import Model.DonHang;
import Model.HoaDonViewModel;
import Model.KhachHang;
import until.DBConnection;


public class DonHangDAO {

    /**
     * Tuyệt Kỹ Chốt Đơn (Transaction Sinh Tử)
     * @param kh: Thông tin khách mua
     * @param idNhanVien: ID của đứa đang thu ngân
     * @param tongTien: Tổng tiền toàn bộ hóa đơn
     * @param gioHang: Danh sách các máy điện thoại khách mua
     * @return true nếu thành công trót lọt, false nếu có biến và đã rollback
     */
    public boolean thanhToanDonHang(KhachHang kh, int idNhanVien, double tongTien, List<ChiTietDon> gioHang) {
        Connection conn = null;
        try {
            conn = DBConnection.getConnection();
            // KHÓA MẠCH MÁU LẠI! Không cho MySQL tự động lưu từng câu lệnh.
            // Phải làm xong tất cả mới được lưu một thể (Commit).
            conn.setAutoCommit(false);

            // Bước 1: Lưu Khách Hàng (Lấy ID khách hàng)
            String sqlKhach = "INSERT INTO KhachHang (ten_kh, sdt, dia_chi) VALUES (?, ?, ?) " +
                              "ON DUPLICATE KEY UPDATE id=LAST_INSERT_ID(id), ten_kh=?, dia_chi=?";
            // ON DUPLICATE KEY: Tuyệt chiêu nếu SĐT đã có thì không tạo mới mà lấy luôn ID cũ
            int idKhachHang = -1;
            try (PreparedStatement psKhach = conn.prepareStatement(sqlKhach, Statement.RETURN_GENERATED_KEYS)) {
                psKhach.setString(1, kh.getTenKh());
                psKhach.setString(2, kh.getSdt());
                psKhach.setString(3, kh.getDiaChi());
                psKhach.setString(4, kh.getTenKh());
                psKhach.setString(5, kh.getDiaChi());
                psKhach.executeUpdate();

                ResultSet rsKhach = psKhach.getGeneratedKeys();
                if (rsKhach.next()) {
                    idKhachHang = rsKhach.getInt(1);
                }
            }

            // Bước 2: Tạo Đơn Hàng (Lấy ID đơn hàng)
            String sqlDon = "INSERT INTO DonHang (id_kh, id_nhan_vien, tong_tien) VALUES (?, ?, ?)";
            int idDonHang = -1;
            try (PreparedStatement psDon = conn.prepareStatement(sqlDon, Statement.RETURN_GENERATED_KEYS)) {
                psDon.setInt(1, idKhachHang);
                psDon.setInt(2, idNhanVien);
                psDon.setDouble(3, tongTien);
                psDon.executeUpdate();

                ResultSet rsDon = psDon.getGeneratedKeys();
                if (rsDon.next()) {
                    idDonHang = rsDon.getInt(1);
                }
            }

            // Bước 3: Lưu Chi Tiết Đơn VÀ Trừ Kho Sản Phẩm
            String sqlChiTiet = "INSERT INTO ChiTietDon (id_don, id_sp, so_luong, don_gia) VALUES (?, ?, ?, ?)";
            String sqlTruKho = "UPDATE SanPham SET so_luong = so_luong - ? WHERE id = ? AND so_luong >= ?";

            try (PreparedStatement psChiTiet = conn.prepareStatement(sqlChiTiet);
                 PreparedStatement psTruKho = conn.prepareStatement(sqlTruKho)) {

                for (ChiTietDon ctd : gioHang) {
                    // Cài cắm cho Chi Tiết Đơn
                    psChiTiet.setInt(1, idDonHang);
                    psChiTiet.setInt(2, ctd.getIdSp());
                    psChiTiet.setInt(3, ctd.getSoLuong());
                    psChiTiet.setDouble(4, ctd.getDonGia());
                    psChiTiet.addBatch(); // Gom lại đánh 1 phát (Batch processing)

                    // Cài cắm cho Trừ Kho
                    psTruKho.setInt(1, ctd.getSoLuong());
                    psTruKho.setInt(2, ctd.getIdSp());
                    psTruKho.setInt(3, ctd.getSoLuong()); // Đảm bảo kho còn đủ hàng mới trừ

                    int dongBiTru = psTruKho.executeUpdate();
                    if (dongBiTru == 0) {
                        // Nếu kho không đủ hàng, ném ra ngoại lệ để chém đứt luồng này!
                        throw new SQLException("Kho không đủ hàng cho Sản phẩm ID: " + ctd.getIdSp());
                    }
                }
                psChiTiet.executeBatch(); // Xuất chiêu lưu chi tiết đơn
            }

            // HOÀN VIÊN! Nếu đến được dòng này mà không có lỗi, chính thức lưu toàn bộ dữ liệu.
            conn.commit();
            System.out.println(">> [Hệ thống] Chốt đơn thành công! Tiền đã vào túi!");
            return true;

        } catch (SQLException e) {
            System.err.println(">> [Cảnh báo] Mạch máu đứt đoạn! Đang vận công đẩy lùi (Rollback)...");
            e.printStackTrace();
            if (conn != null) {
                try {
                    conn.rollback(); // HỦY TOÀN BỘ TIẾN TRÌNH NHƯ CHƯA CÓ GÌ XẢY RA!
                    System.err.println(">> [Hệ thống] Đã Rollback an toàn! Không có dữ liệu rác lọt vào.");
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return false;
        } finally {
            // Nhớ trả lại trạng thái bình thường và đóng cầu nối
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Lấy toàn bộ danh sách đơn hàng, sắp xếp mới nhất trước
     * Dùng để hiển thị lên TableView màn hình lịch sử
     */
    public List<DonHang> layDanhSachDonHang() {
        List<DonHang> list = new ArrayList<>();
        String sql = "SELECT * FROM DonHang ORDER BY ngay_tao DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                DonHang dh = new DonHang();
                dh.setId(rs.getInt("id"));
                dh.setIdKh(rs.getInt("id_kh"));
                dh.setIdNhanVien(rs.getInt("id_nhan_vien"));
                // Chuyển Timestamp của MySQL sang LocalDateTime của Java
                dh.setNgayTao(rs.getTimestamp("ngay_tao").toLocalDateTime());
                dh.setTongTien(rs.getDouble("tong_tien"));
                list.add(dh);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy danh sách đơn hàng của một khách hàng cụ thể
     * @param idKhachHang: ID của khách hàng cần tra cứu lịch sử mua
     */
    public List<DonHang> layDonHangTheoKhachHang(int idKhachHang) {
        List<DonHang> list = new ArrayList<>();
        String sql = "SELECT * FROM DonHang WHERE id_kh = ? ORDER BY ngay_tao DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idKhachHang);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    DonHang dh = new DonHang();
                    dh.setId(rs.getInt("id"));
                    dh.setIdKh(rs.getInt("id_kh"));
                    dh.setIdNhanVien(rs.getInt("id_nhan_vien"));
                    dh.setNgayTao(rs.getTimestamp("ngay_tao").toLocalDateTime());
                    dh.setTongTien(rs.getDouble("tong_tien"));
                    list.add(dh);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy chi tiết các sản phẩm trong một đơn hàng
     * @param idDonHang: ID của đơn hàng cần xem chi tiết
     * @return Danh sách ChiTietDon (mỗi phần tử là 1 dòng sản phẩm trong đơn)
     */
    public List<ChiTietDon> layChiTietDon(int idDonHang) {
        List<ChiTietDon> list = new ArrayList<>();
        String sql = "SELECT * FROM ChiTietDon WHERE id_don = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idDonHang);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ChiTietDon ctd = new ChiTietDon();
                    ctd.setIdDon(rs.getInt("id_don"));
                    ctd.setIdSp(rs.getInt("id_sp"));
                    ctd.setSoLuong(rs.getInt("so_luong"));
                    ctd.setDonGia(rs.getDouble("don_gia"));
                    list.add(ctd);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * Lấy danh sách hóa đơn kèm tên khách hàng và nhân viên (JOIN 3 bảng).
     * Dùng cho màn hình Lịch sử hóa đơn.
     */
    public List<HoaDonViewModel> layDanhSachHoaDonChiTiet() {
        List<HoaDonViewModel> list = new ArrayList<>();
        String sql = "SELECT dh.id, kh.ten_kh, kh.sdt, tk.username AS ten_nv, dh.ngay_tao, dh.tong_tien " +
                     "FROM DonHang dh " +
                     "JOIN KhachHang kh ON dh.id_kh = kh.id " +
                     "JOIN TaiKhoan  tk ON dh.id_nhan_vien = tk.id " +
                     "ORDER BY dh.ngay_tao DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(new HoaDonViewModel(
                        rs.getInt("id"),
                        rs.getString("ten_kh"),
                        rs.getString("sdt"),
                        rs.getString("ten_nv"),
                        rs.getTimestamp("ngay_tao").toLocalDateTime(),
                        rs.getDouble("tong_tien")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * Lấy chi tiết các sản phẩm trong một đơn hàng (JOIN ChiTietDon + SanPham).
     */
    public List<ChiTietHoaDonViewModel> layChiTietHoaDon(int idDon) {
        List<ChiTietHoaDonViewModel> list = new ArrayList<>();
        String sql = "SELECT sp.ten_sp, ctd.so_luong, ctd.don_gia " +
                     "FROM ChiTietDon ctd " +
                     "JOIN SanPham sp ON ctd.id_sp = sp.id " +
                     "WHERE ctd.id_don = ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idDon);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new ChiTietHoaDonViewModel(
                            rs.getString("ten_sp"),
                            rs.getInt("so_luong"),
                            rs.getDouble("don_gia")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}