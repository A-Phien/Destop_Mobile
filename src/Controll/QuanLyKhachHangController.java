package Controll;

import Dao.KhachHangDAO;
import Model.KhachHang;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class QuanLyKhachHangController {

    // ===== BẢNG =====
    @FXML
    private TextField txtTimKiem;
    @FXML
    private TableView<KhachHang> tablKH;
    @FXML
    private TableColumn<KhachHang, Integer> colId;
    @FXML
    private TableColumn<KhachHang, String> colTen;
    @FXML
    private TableColumn<KhachHang, String> colSdt;
    @FXML
    private TableColumn<KhachHang, String> colDiaChi;

    // ===== FORM =====
    @FXML
    private Label lblTieuDe;
    @FXML
    private TextField txtTen;
    @FXML
    private TextField txtSdt;
    @FXML
    private TextField txtDiaChi;

    private final KhachHangDAO dao = new KhachHangDAO();
    private ObservableList<KhachHang> danhSach = FXCollections.observableArrayList();
    private KhachHang khDangChon = null;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTen.setCellValueFactory(new PropertyValueFactory<>("tenKh"));
        colSdt.setCellValueFactory(new PropertyValueFactory<>("sdt"));
        colDiaChi.setCellValueFactory(new PropertyValueFactory<>("diaChi"));

        tablKH.getSelectionModel().selectedItemProperty()
                .addListener((obs, cu, moi) -> {
                    if (moi != null)
                        dienVaoForm(moi);
                });

        txtTimKiem.textProperty().addListener((obs, cu, moi) -> locBang(moi));

        taiDuLieu();
        datTrangThaiThemMoi();
    }

    private void taiDuLieu() {
        danhSach = FXCollections.observableArrayList(dao.layDanhSachKhachHang());
        tablKH.setItems(danhSach);
    }

    private void locBang(String kw) {
        if (kw == null || kw.isBlank()) {
            tablKH.setItems(danhSach);
            return;
        }
        String k = kw.toLowerCase().trim();
        tablKH.setItems(danhSach.filtered(kh -> kh.getTenKh().toLowerCase().contains(k) || kh.getSdt().contains(k)));
    }

    private void dienVaoForm(KhachHang kh) {
        khDangChon = kh;
        lblTieuDe.setText("✏  SỬA KHÁCH HÀNG");
        txtTen.setText(kh.getTenKh());
        txtSdt.setText(kh.getSdt());
        txtDiaChi.setText(kh.getDiaChi() != null ? kh.getDiaChi() : "");
    }

    @FXML
    private void handleThemMoi() {
        datTrangThaiThemMoi();
    }

    @FXML
    private void handleLuu() {
        String ten = txtTen.getText().trim();
        String sdt = txtSdt.getText().trim();
        String diaChi = txtDiaChi.getText().trim();

        if (ten.isEmpty() || sdt.isEmpty()) {
            alert(Alert.AlertType.WARNING, "Thiếu thông tin", "Tên và SĐT không được để trống!");
            return;
        }

        boolean ok;
        if (khDangChon == null) {
            ok = dao.themKhachHang(new KhachHang(ten, sdt, diaChi));
            if (ok)
                alert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm khách hàng mới!");
        } else {
            khDangChon.setTenKh(ten);
            khDangChon.setSdt(sdt);
            khDangChon.setDiaChi(diaChi);
            ok = dao.capNhatKhachHang(khDangChon);
            if (ok)
                alert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật thông tin khách hàng!");
        }

        if (ok) {
            taiDuLieu();
            datTrangThaiThemMoi();
        } else
            alert(Alert.AlertType.ERROR, "Lỗi", "Thao tác thất bại! SĐT có thể đã tồn tại.");
    }

    @FXML
    private void handleXoa() {
        KhachHang kh = tablKH.getSelectionModel().getSelectedItem();
        if (kh == null) {
            alert(Alert.AlertType.WARNING, "Chưa chọn", "Chọn khách hàng cần xóa!");
            return;
        }

        Alert cf = new Alert(Alert.AlertType.CONFIRMATION,
                "Xóa khách hàng \"" + kh.getTenKh() + "\"?\nKhông thể xóa nếu đã có đơn hàng!",
                ButtonType.OK, ButtonType.CANCEL);
        cf.setHeaderText(null);
        cf.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                if (dao.xoaKhachHang(kh.getId())) {
                    taiDuLieu();
                    datTrangThaiThemMoi();
                    alert(Alert.AlertType.INFORMATION, "Đã xóa", "Xóa khách hàng thành công!");
                } else
                    alert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa! Khách hàng đang có đơn hàng.");
            }
        });
    }

    private void datTrangThaiThemMoi() {
        khDangChon = null;
        lblTieuDe.setText("➕  THÊM KHÁCH HÀNG MỚI");
        txtTen.clear();
        txtSdt.clear();
        txtDiaChi.clear();
        tablKH.getSelectionModel().clearSelection();
    }

    private void alert(Alert.AlertType t, String title, String msg) {
        Alert a = new Alert(t);
        a.setTitle(title);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }
}
