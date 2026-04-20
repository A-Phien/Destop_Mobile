package Model;

public class ChiTietDon {
    private int idDon; // Khóa ngoại trỏ về DonHang
    private int idSp;  // Khóa ngoại trỏ về SanPham
    private int soLuong;
    private double donGia; // Giá lúc bán (Tránh trường hợp sau này giá SP thay đổi)

    public ChiTietDon() {
    }

    public ChiTietDon(int idDon, int idSp, int soLuong, double donGia) {
        this.idDon = idDon;
        this.idSp = idSp;
        this.soLuong = soLuong;
        this.donGia = donGia;
    }

    public int getIdDon() { return idDon; }
    public void setIdDon(int idDon) { this.idDon = idDon; }
    
    public int getIdSp() { return idSp; }
    public void setIdSp(int idSp) { this.idSp = idSp; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public double getDonGia() { return donGia; }
    public void setDonGia(double donGia) { this.donGia = donGia; }
    
    // Tiện ích để tính thành tiền của 1 món
    public double getThanhTien() {
        return this.soLuong * this.donGia;
    }
}