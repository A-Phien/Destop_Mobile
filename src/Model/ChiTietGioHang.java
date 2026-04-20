package Model;

/**
 * Class đại diện cho 1 dòng trong Giỏ Hàng trên giao diện.
 * Khác ChiTietDon ở chỗ: có thêm tenSp để hiển thị lên TableView.
 */
public class ChiTietGioHang {
    private int idSp;
    private String tenSp;
    private int soLuong;
    private double donGia;

    public ChiTietGioHang() {}

    public ChiTietGioHang(int idSp, String tenSp, int soLuong, double donGia) {
        this.idSp = idSp;
        this.tenSp = tenSp;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    // Tiện ích: tính thành tiền của 1 dòng
    public double getThanhTien() {
        return soLuong * donGia;
    }

    // Convert sang ChiTietDon để truyền cho DAO
    public ChiTietDon toChiTietDon() {
        return new ChiTietDon(0, idSp, soLuong, donGia);
    }

    // --- Getters & Setters ---
    public int getIdSp() { return idSp; }
    public void setIdSp(int idSp) { this.idSp = idSp; }

    public String getTenSp() { return tenSp; }
    public void setTenSp(String tenSp) { this.tenSp = tenSp; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }
}
