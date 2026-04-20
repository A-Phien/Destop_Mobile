package Model;


public class SanPham {
    private int id;
    private String tenSp;
    private String hangSx;
    private double giaBan; // Tiền Việt Nam có thể dùng double hoặc long
    private int soLuong;
    private String urlAnh; // Link lấy từ Cloudinary

    public SanPham() {
    }

    public SanPham(int id, String tenSp, String hangSx, double giaBan, int soLuong, String urlAnh) {
        this.id = id;
        this.tenSp = tenSp;
        this.hangSx = hangSx;
        this.giaBan = giaBan;
        this.soLuong = soLuong;
        this.urlAnh = urlAnh;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTenSp() { return tenSp; }
    public void setTenSp(String tenSp) { this.tenSp = tenSp; }

    public String getHangSx() { return hangSx; }
    public void setHangSx(String hangSx) { this.hangSx = hangSx; }

    public double getGiaBan() { return giaBan; }
    public void setGiaBan(double giaBan) { this.giaBan = giaBan; }

    public int getSoLuong() { return soLuong; }
    public void setSoLuong(int soLuong) { this.soLuong = soLuong; }

    public String getUrlAnh() { return urlAnh; }
    public void setUrlAnh(String urlAnh) { this.urlAnh = urlAnh; }

    @Override
    public String toString() {
        return this.tenSp; // Rất quan trọng khi hiển thị trên ComboBox hoặc ListView của JavaFX
    }
}
