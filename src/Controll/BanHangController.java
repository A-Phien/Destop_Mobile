package Controll;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import until.UserSession;
import Dao.DonHangDAO;
import Dao.SanPhamDAO;
import Model.ChiTietDon;
import Model.ChiTietGioHang;
import Model.KhachHang;
import Model.SanPham;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

public class BanHangController {

    // ===== BẢNG SẢN PHẨM (bên trái - trên) =====
    @FXML
    private TextField txtTimKiem;
    @FXML
    private TableView<SanPham> tableSanPham;
    @FXML
    private TableColumn<SanPham, Integer> colMaSP;
    @FXML
    private TableColumn<SanPham, String> colTenSP;
    @FXML
    private TableColumn<SanPham, Double> colGia;
    @FXML
    private TableColumn<SanPham, Integer> colTonKho;
    @FXML
    private TableColumn<SanPham, String> colHang;

    // ===== BẢNG GIỎ HÀNG (bên trái - dưới) =====
    @FXML
    private TableView<ChiTietGioHang> tableGioHang;
    @FXML
    private TableColumn<ChiTietGioHang, Integer> colMaGio;
    @FXML
    private TableColumn<ChiTietGioHang, String> colTenGio;
    @FXML
    private TableColumn<ChiTietGioHang, Integer> colSLGio;
    @FXML
    private TableColumn<ChiTietGioHang, Double> colGiaGio;
    @FXML
    private TableColumn<ChiTietGioHang, Double> colThanhTien;
    @FXML
    private Label lblTongTien;

    // ===== PANEL CHI TIẾT (bên phải) =====
    @FXML
    private ComboBox<String> cbLoaiSP;
    @FXML
    private TextField txtMaSP;
    @FXML
    private TextField txtTenSP;
    @FXML
    private TextField txtDonGia;
    @FXML
    private TextField txtSoLuongMua;
    @FXML private ImageView imgPreview;

    // ===== PHÂN QUYỀN =====
    @FXML private javafx.scene.control.TabPane mainTabPane;
    @FXML private javafx.scene.control.Tab     tabKho;

    // ===== DỮ LIỆU =====
    private final SanPhamDAO sanPhamDAO = new SanPhamDAO();
    private final DonHangDAO donHangDAO = new DonHangDAO();
    private ObservableList<SanPham> danhSachSanPham = FXCollections.observableArrayList();
    private final ObservableList<ChiTietGioHang> gioHang = FXCollections.observableArrayList();

    // Formatter tiền Việt Nam: 29.990.000
    private final NumberFormat tienVND = NumberFormat.getInstance(new Locale("vi", "VN"));

    // =====================================================================
    // KHỞI TẠO
    // =====================================================================
    @FXML
    public void initialize() {
        cauHinhCotBangSanPham();
        cauHinhCotBangGioHang();
        taiDanhSachSanPham();
        cauHinhComboBoxHang();
        cauHinhLangNgheChonSanPham();
        cauHinhTimKiem();
        tableGioHang.setItems(gioHang);
        capNhatTongTien();
        // Ẩn tab Quản lý kho nếu không phải ADMIN
        if (!UserSession.getInstance().laAdmin()) {
            mainTabPane.getTabs().remove(tabKho);
        }
    }

    // =====================================================================
    // CẤU HÌNH CỘT BẢNG
    // =====================================================================
    private void cauHinhCotBangSanPham() {
        colMaSP.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTenSP.setCellValueFactory(new PropertyValueFactory<>("tenSp"));
        colTonKho.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        colHang.setCellValueFactory(new PropertyValueFactory<>("hangSx"));

        // Cột giá: format thành "29.990.000 ₫"
        colGia.setCellValueFactory(new PropertyValueFactory<>("giaBan"));
        colGia.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : tienVND.format(item) + " ₫");
            }
        });
    }

    private void cauHinhCotBangGioHang() {
        colMaGio.setCellValueFactory(new PropertyValueFactory<>("idSp"));
        colTenGio.setCellValueFactory(new PropertyValueFactory<>("tenSp"));
        colSLGio.setCellValueFactory(new PropertyValueFactory<>("soLuong"));

        colGiaGio.setCellValueFactory(new PropertyValueFactory<>("donGia"));
        colGiaGio.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : tienVND.format(item) + " ₫");
            }
        });

        colThanhTien.setCellValueFactory(new PropertyValueFactory<>("thanhTien"));
        colThanhTien.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Double item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : tienVND.format(item) + " ₫");
            }
        });
    }

    // =====================================================================
    // TẢI & LỌC DỮ LIỆU
    // =====================================================================
    private void taiDanhSachSanPham() {
        danhSachSanPham = FXCollections.observableArrayList(sanPhamDAO.layDanhSachSanPham());
        tableSanPham.setItems(danhSachSanPham);
    }

    private void cauHinhComboBoxHang() {
        // Lấy danh sách hãng không trùng, sắp xếp A-Z
        List<String> danhSachHang = danhSachSanPham.stream()
                .map(SanPham::getHangSx)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
        danhSachHang.add(0, "Tất cả");
        cbLoaiSP.setItems(FXCollections.observableArrayList(danhSachHang));
        cbLoaiSP.getSelectionModel().selectFirst();

        // Lọc bảng theo hãng được chọn
        cbLoaiSP.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == null || newVal.equals("Tất cả")) {
                tableSanPham.setItems(danhSachSanPham);
            } else {
                tableSanPham.setItems(danhSachSanPham.filtered(sp -> sp.getHangSx().equals(newVal)));
            }
        });
    }

    private void cauHinhLangNgheChonSanPham() {
        // Khi click chọn 1 sản phẩm → hiển thị chi tiết bên phải
        tableSanPham.getSelectionModel().selectedItemProperty().addListener((obs, cu, moi) -> {
            if (moi != null)
                hienThiChiTietSanPham(moi);
        });
    }

    private void cauHinhTimKiem() {
        txtTimKiem.textProperty().addListener((obs, cu, moi) -> {
            if (moi == null || moi.trim().isEmpty()) {
                tableSanPham.setItems(danhSachSanPham);
                return;
            }
            String tuKhoa = moi.toLowerCase().trim();
            tableSanPham.setItems(danhSachSanPham.filtered(sp -> sp.getTenSp().toLowerCase().contains(tuKhoa) ||
                    sp.getHangSx().toLowerCase().contains(tuKhoa)));
        });
    }

    // Hiển thị thông tin SP được chọn vào các TextField bên phải
    private void hienThiChiTietSanPham(SanPham sp) {
        txtMaSP.setText(String.valueOf(sp.getId()));
        txtTenSP.setText(sp.getTenSp());
        txtDonGia.setText(tienVND.format(sp.getGiaBan()) + " ₫");
        txtSoLuongMua.setText("1");

        // Load ảnh từ Cloudinary URL (load nền không block UI)
        if (sp.getUrlAnh() != null && !sp.getUrlAnh().isBlank()) {
            try {
                imgPreview.setImage(new Image(sp.getUrlAnh(), true));
            } catch (Exception e) {
                imgPreview.setImage(null);
            }
        } else {
            imgPreview.setImage(null);
        }
    }

    // =====================================================================
    // XỬ LÝ NÚT BẤM
    // =====================================================================

    @FXML
    private void handleThemVaoGio() {
        SanPham spChon = tableSanPham.getSelectionModel().getSelectedItem();
        if (spChon == null) {
            hienCanhBao("Chưa chọn sản phẩm", "Vui lòng chọn 1 sản phẩm từ danh sách!");
            return;
        }

        int soLuongMua;
        try {
            soLuongMua = Integer.parseInt(txtSoLuongMua.getText().trim());
            if (soLuongMua <= 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            hienCanhBao("Số lượng không hợp lệ", "Vui lòng nhập số nguyên dương!");
            return;
        }

        // Nếu SP đã có trong giỏ → cộng thêm số lượng
        for (ChiTietGioHang item : gioHang) {
            if (item.getIdSp() == spChon.getId()) {
                int slTong = item.getSoLuong() + soLuongMua;
                if (slTong > spChon.getSoLuong()) {
                    hienCanhBao("Kho không đủ", "Tổng số lượng vượt quá tồn kho (" + spChon.getSoLuong() + " cái)!");
                    return;
                }
                item.setSoLuong(slTong);
                tableGioHang.refresh();
                capNhatTongTien();
                return;
            }
        }

        // SP chưa có trong giỏ → thêm mới
        if (soLuongMua > spChon.getSoLuong()) {
            hienCanhBao("Kho không đủ", "Chỉ còn " + spChon.getSoLuong() + " sản phẩm trong kho!");
            return;
        }
        gioHang.add(new ChiTietGioHang(spChon.getId(), spChon.getTenSp(), soLuongMua, spChon.getGiaBan()));
        capNhatTongTien();
    }

    @FXML
    private void handleXoaKhoiGio() {
        ChiTietGioHang spChon = tableGioHang.getSelectionModel().getSelectedItem();
        if (spChon == null) {
            hienCanhBao("Chưa chọn sản phẩm", "Vui lòng chọn 1 sản phẩm trong giỏ hàng để xóa!");
            return;
        }
        gioHang.remove(spChon);
        capNhatTongTien();
    }

    @FXML
    private void handleXuatHoaDon() {
        if (gioHang.isEmpty()) {
            hienCanhBao("Giỏ hàng trống", "Vui lòng thêm sản phẩm vào giỏ trước khi xuất hóa đơn!");
            return;
        }

        // --- Dialog nhập thông tin khách hàng ---
        Dialog<KhachHang> dialog = new Dialog<>();
        dialog.setTitle("Xuất Hóa Đơn");
        dialog.setHeaderText("Nhập thông tin khách hàng");

        ButtonType btnXuatType = new ButtonType("Xác nhận xuất", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(btnXuatType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(12);
        grid.setPadding(new Insets(20, 120, 10, 20));

        TextField txtTenKH = new TextField();
        txtTenKH.setPromptText("Nguyễn Văn A");
        TextField txtSDT = new TextField();
        txtSDT.setPromptText("0901234567");
        TextField txtDiaChi = new TextField();
        txtDiaChi.setPromptText("123 Đường ABC, TP.HCM");

        grid.add(new Label("Tên khách hàng: *"), 0, 0);
        grid.add(txtTenKH, 1, 0);
        grid.add(new Label("Số điện thoại: *"), 0, 1);
        grid.add(txtSDT, 1, 1);
        grid.add(new Label("Địa chỉ:"), 0, 2);
        grid.add(txtDiaChi, 1, 2);

        // Hiển thị tổng tiền trong dialog
        double tongTien = gioHang.stream().mapToDouble(ChiTietGioHang::getThanhTien).sum();
        Label lblTong = new Label("Tổng tiền: " + tienVND.format(tongTien) + " ₫");
        lblTong.setStyle("-fx-font-weight: bold; -fx-font-size: 14; -fx-text-fill: #e74c3c;");
        grid.add(lblTong, 0, 3);
        grid.getColumnCount();
        javafx.scene.layout.GridPane.setColumnSpan(lblTong, 2);

        dialog.getDialogPane().setContent(grid);

        // Disable nút Xác nhận nếu chưa điền đủ Tên + SĐT
        Button btnXuat = (Button) dialog.getDialogPane().lookupButton(btnXuatType);
        btnXuat.setDisable(true);
        Runnable kiemTraInput = () -> btnXuat.setDisable(
                txtTenKH.getText().trim().isEmpty() || txtSDT.getText().trim().isEmpty());
        txtTenKH.textProperty().addListener((o, cu, moi) -> kiemTraInput.run());
        txtSDT.textProperty().addListener((o, cu, moi) -> kiemTraInput.run());

        dialog.setResultConverter(btn -> btn == btnXuatType
                ? new KhachHang(txtTenKH.getText().trim(), txtSDT.getText().trim(), txtDiaChi.getText().trim())
                : null);

        Optional<KhachHang> result = dialog.showAndWait();
        result.ifPresent(kh -> xuLyThanhToan(kh, tongTien));
    }

    private void xuLyThanhToan(KhachHang kh, double tongTien) {
        // Convert ChiTietGioHang → ChiTietDon để truyền cho DAO
        List<ChiTietDon> danhSachChiTiet = gioHang.stream()
                .map(ChiTietGioHang::toChiTietDon)
                .collect(Collectors.toList());

        // Lấy ID nhân viên đang đăng nhập từ session
        int idNhanVien = UserSession.getInstance().getTaiKhoanHienTai().getId();
        boolean thanhCong = donHangDAO.thanhToanDonHang(kh, idNhanVien, tongTien, danhSachChiTiet);

        if (thanhCong) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Thành công!");
            alert.setHeaderText("Xuất hóa đơn thành công");
            alert.setContentText(
                    "Khách hàng: " + kh.getTenKh() +
                            "\nSĐT: " + kh.getSdt() +
                            "\nTổng tiền: " + tienVND.format(tongTien) + " ₫");
            alert.showAndWait();

            gioHang.clear();
            capNhatTongTien();
            taiDanhSachSanPham(); // Reload để cập nhật tồn kho mới
        } else {
            new Alert(Alert.AlertType.ERROR, "Xuất hóa đơn thất bại! Vui lòng thử lại.", ButtonType.OK).showAndWait();
        }
    }

    // =====================================================================
    // TIỆN ÍCH
    // =====================================================================
    private void capNhatTongTien() {
        double tong = gioHang.stream().mapToDouble(ChiTietGioHang::getThanhTien).sum();
        lblTongTien.setText("TỔNG TIỀN: " + tienVND.format(tong) + " ₫");
    }

    private void hienCanhBao(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}