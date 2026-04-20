package Controll;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import Dao.DonHangDAO;
import Model.ChiTietHoaDonViewModel;
import Model.HoaDonViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class LichSuHoaDonController {

    // ===== BẢNG DANH SÁCH ĐƠN HÀNG =====
    @FXML private TextField txtTimKiem;
    @FXML private TableView<HoaDonViewModel>          tableDonHang;
    @FXML private TableColumn<HoaDonViewModel, Integer> colIdDon;
    @FXML private TableColumn<HoaDonViewModel, String>  colNgay;
    @FXML private TableColumn<HoaDonViewModel, String>  colKhachHang;
    @FXML private TableColumn<HoaDonViewModel, String>  colSdt;
    @FXML private TableColumn<HoaDonViewModel, String>  colNhanVien;
    @FXML private TableColumn<HoaDonViewModel, Double>  colTongTien;

    // ===== CHI TIẾT ĐƠN HÀNG =====
    @FXML private Label lblTieuDeChiTiet;
    @FXML private TableView<ChiTietHoaDonViewModel>          tableChiTiet;
    @FXML private TableColumn<ChiTietHoaDonViewModel, String>  colTenSP;
    @FXML private TableColumn<ChiTietHoaDonViewModel, Integer> colSoLuong;
    @FXML private TableColumn<ChiTietHoaDonViewModel, Double>  colDonGia;
    @FXML private TableColumn<ChiTietHoaDonViewModel, Double>  colThanhTien;

    private final DonHangDAO dao = new DonHangDAO();
    private ObservableList<HoaDonViewModel> danhSach = FXCollections.observableArrayList();
    private final NumberFormat fmt = NumberFormat.getInstance(new Locale("vi", "VN"));

    @FXML
    public void initialize() {
        cauHinhCotDonHang();
        cauHinhCotChiTiet();
        taiDuLieu();

        // Click đơn hàng → load chi tiết
        tableDonHang.getSelectionModel().selectedItemProperty()
                .addListener((obs, cu, moi) -> { if (moi != null) hienChiTiet(moi); });

        // Tìm kiếm real-time
        txtTimKiem.textProperty().addListener((obs, cu, moi) -> locBang(moi));
    }

    private void cauHinhCotDonHang() {
        colIdDon.setCellValueFactory(new PropertyValueFactory<>("idDon"));
        colNgay.setCellValueFactory(new PropertyValueFactory<>("ngayTaoStr"));
        colKhachHang.setCellValueFactory(new PropertyValueFactory<>("tenKhachHang"));
        colSdt.setCellValueFactory(new PropertyValueFactory<>("sdtKhachHang"));
        colNhanVien.setCellValueFactory(new PropertyValueFactory<>("tenNhanVien"));

        colTongTien.setCellValueFactory(new PropertyValueFactory<>("tongTien"));
        colTongTien.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double v, boolean e) {
                super.updateItem(v, e);
                setText(e || v == null ? null : fmt.format(v) + " ₫");
            }
        });
    }

    private void cauHinhCotChiTiet() {
        colTenSP.setCellValueFactory(new PropertyValueFactory<>("tenSanPham"));
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuong"));

        colDonGia.setCellValueFactory(new PropertyValueFactory<>("donGia"));
        colDonGia.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double v, boolean e) {
                super.updateItem(v, e);
                setText(e || v == null ? null : fmt.format(v) + " ₫");
            }
        });

        colThanhTien.setCellValueFactory(new PropertyValueFactory<>("thanhTien"));
        colThanhTien.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double v, boolean e) {
                super.updateItem(v, e);
                setText(e || v == null ? null : fmt.format(v) + " ₫");
            }
        });
    }

    private void taiDuLieu() {
        danhSach = FXCollections.observableArrayList(dao.layDanhSachHoaDonChiTiet());
        tableDonHang.setItems(danhSach);
        lblTieuDeChiTiet.setText("— Chọn một đơn hàng để xem chi tiết —");
        tableChiTiet.setItems(FXCollections.emptyObservableList());
    }

    private void hienChiTiet(HoaDonViewModel hd) {
        lblTieuDeChiTiet.setText(String.format(
                "Chi tiết đơn #%d  |  Khách: %s  |  Tổng: %s ₫",
                hd.getIdDon(), hd.getTenKhachHang(), fmt.format(hd.getTongTien())));

        List<ChiTietHoaDonViewModel> chiTiet = dao.layChiTietHoaDon(hd.getIdDon());
        tableChiTiet.setItems(FXCollections.observableArrayList(chiTiet));
    }

    private void locBang(String tuKhoa) {
        if (tuKhoa == null || tuKhoa.isBlank()) {
            tableDonHang.setItems(danhSach);
            return;
        }
        String kw = tuKhoa.toLowerCase().trim();
        tableDonHang.setItems(danhSach.filtered(hd ->
                hd.getTenKhachHang().toLowerCase().contains(kw) ||
                hd.getSdtKhachHang().contains(kw) ||
                hd.getTenNhanVien().toLowerCase().contains(kw) ||
                String.valueOf(hd.getIdDon()).contains(kw)
        ));
    }

    @FXML
    private void handleLamMoi() {
        taiDuLieu();
        txtTimKiem.clear();
    }
}
