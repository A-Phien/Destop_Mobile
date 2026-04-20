package Model;


public class KhachHang {
    private int id;
    private String tenKh;
    private String sdt;
    private String diaChi;

    public KhachHang() {
    }

    // Constructor khi insert chưa có ID
    public KhachHang(String tenKh, String sdt, String diaChi) {
        this.tenKh = tenKh;
        this.sdt = sdt;
        this.diaChi = diaChi;
    }

    public KhachHang(int id, String tenKh, String sdt, String diaChi) {
        this.id = id;
        this.tenKh = tenKh;
        this.sdt = sdt;
        this.diaChi = diaChi;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTenKh() { return tenKh; }
    public void setTenKh(String tenKh) { this.tenKh = tenKh; }

    public String getSdt() { return sdt; }
    public void setSdt(String sdt) { this.sdt = sdt; }

    public String getDiaChi() { return diaChi; }
    public void setDiaChi(String diaChi) { this.diaChi = diaChi; }
}