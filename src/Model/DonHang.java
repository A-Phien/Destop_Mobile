package Model;

import java.time.LocalDateTime;

public class DonHang {
    private int id;
    private int idKh;
    private int idNhanVien; // ID của tài khoản đang đăng nhập
    private LocalDateTime ngayTao;
    private double tongTien;

    public DonHang() {
    }

    public DonHang(int id, int idKh, int idNhanVien, LocalDateTime ngayTao, double tongTien) {
        this.id = id;
        this.idKh = idKh;
        this.idNhanVien = idNhanVien;
        this.ngayTao = ngayTao;
        this.tongTien = tongTien;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getIdKh() { return idKh; }
    public void setIdKh(int idKh) { this.idKh = idKh; }

    public int getIdNhanVien() { return idNhanVien; }
    public void setIdNhanVien(int idNhanVien) { this.idNhanVien = idNhanVien; }

    public LocalDateTime getNgayTao() { return ngayTao; }
    public void setNgayTao(LocalDateTime ngayTao) { this.ngayTao = ngayTao; }

    public double getTongTien() { return tongTien; }
    public void setTongTien(double tongTien) { this.tongTien = tongTien; }
}