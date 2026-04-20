package Model;

/**
 * ViewModel cho chi tiết một đơn hàng.
 * Gộp thông tin từ ChiTietDon + SanPham.
 */
public class ChiTietHoaDonViewModel {
    private String tenSanPham;
    private int    soLuong;
    private double donGia;

    public ChiTietHoaDonViewModel(String tenSanPham, int soLuong, double donGia) {
        this.tenSanPham = tenSanPham;
        this.soLuong    = soLuong;
        this.donGia     = donGia;
    }

    public double getThanhTien() { return soLuong * donGia; }

    public String getTenSanPham() { return tenSanPham; }
    public int    getSoLuong()    { return soLuong; }
    public double getDonGia()     { return donGia; }
}
