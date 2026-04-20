package Model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * ViewModel cho màn hình Lịch sử hóa đơn.
 * Gộp thông tin từ DonHang + KhachHang + TaiKhoan.
 */
public class HoaDonViewModel {
    private int    idDon;
    private String tenKhachHang;
    private String sdtKhachHang;
    private String tenNhanVien;
    private LocalDateTime ngayTao;
    private double tongTien;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public HoaDonViewModel(int idDon, String tenKhachHang, String sdtKhachHang,
                           String tenNhanVien, LocalDateTime ngayTao, double tongTien) {
        this.idDon        = idDon;
        this.tenKhachHang = tenKhachHang;
        this.sdtKhachHang = sdtKhachHang;
        this.tenNhanVien  = tenNhanVien;
        this.ngayTao      = ngayTao;
        this.tongTien     = tongTien;
    }

    /** Trả về ngày tạo dạng chuỗi dd/MM/yyyy HH:mm để hiển thị trên TableView */
    public String getNgayTaoStr() {
        return ngayTao != null ? ngayTao.format(FMT) : "";
    }

    public int    getIdDon()        { return idDon; }
    public String getTenKhachHang() { return tenKhachHang; }
    public String getSdtKhachHang() { return sdtKhachHang; }
    public String getTenNhanVien()  { return tenNhanVien; }
    public LocalDateTime getNgayTao() { return ngayTao; }
    public double getTongTien()     { return tongTien; }
}
