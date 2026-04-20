package Controll;

import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;

import Dao.SanPhamDAO;
import Model.SanPham;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import until.CloudinaryUploader;

public class QuanLySanPhamController {

    // ===== BẢNG =====
    @FXML private TextField txtTimKiem;
    @FXML private TableView<SanPham>          tableSanPham;
    @FXML private TableColumn<SanPham, Integer> colId;
    @FXML private TableColumn<SanPham, String>  colTenSP;
    @FXML private TableColumn<SanPham, String>  colHang;
    @FXML private TableColumn<SanPham, Double>  colGia;
    @FXML private TableColumn<SanPham, Integer> colSoLuong;

    // ===== FORM =====
    @FXML private Label     lblTieuDe;
    @FXML private TextField txtTenSP;
    @FXML private TextField txtHangSX;
    @FXML private TextField txtGiaBan;
    @FXML private TextField txtSoLuong;
    @FXML private TextField txtUrlAnh;
    @FXML private ImageView imgPreview;
    @FXML private Button    btnLuu;

    private final SanPhamDAO dao = new SanPhamDAO();
    private ObservableList<SanPham> danhSach = FXCollections.observableArrayList();
    private SanPham spDangChon = null; // null = chế độ thêm mới
    private final NumberFormat fmt = NumberFormat.getInstance(new Locale("vi", "VN"));

    @FXML
    public void initialize() {
        cauHinhCot();
        taiDuLieu();
        // Click hàng → điền form
        tableSanPham.getSelectionModel().selectedItemProperty()
                .addListener((obs, cu, moi) -> { if (moi != null) dienVaoForm(moi); });
        // Tìm kiếm real-time
        txtTimKiem.textProperty().addListener((obs, cu, moi) -> locBang(moi));
        datTrangThaiThemMoi();
    }

    private void cauHinhCot() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTenSP.setCellValueFactory(new PropertyValueFactory<>("tenSp"));
        colHang.setCellValueFactory(new PropertyValueFactory<>("hangSx"));
        colSoLuong.setCellValueFactory(new PropertyValueFactory<>("soLuong"));
        colGia.setCellValueFactory(new PropertyValueFactory<>("giaBan"));
        colGia.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double v, boolean e) {
                super.updateItem(v, e);
                setText(e || v == null ? null : fmt.format(v) + " ₫");
            }
        });
    }

    private void taiDuLieu() {
        danhSach = FXCollections.observableArrayList(dao.layDanhSachSanPham());
        tableSanPham.setItems(danhSach);
    }

    private void locBang(String tuKhoa) {
        if (tuKhoa == null || tuKhoa.isBlank()) { tableSanPham.setItems(danhSach); return; }
        String kw = tuKhoa.toLowerCase().trim();
        tableSanPham.setItems(danhSach.filtered(sp ->
                sp.getTenSp().toLowerCase().contains(kw) || sp.getHangSx().toLowerCase().contains(kw)));
    }

    private void dienVaoForm(SanPham sp) {
        spDangChon = sp;
        lblTieuDe.setText("✏  SỬA SẢN PHẨM");
        txtTenSP.setText(sp.getTenSp());
        txtHangSX.setText(sp.getHangSx());
        txtGiaBan.setText(String.valueOf((long) sp.getGiaBan()));
        txtSoLuong.setText(String.valueOf(sp.getSoLuong()));
        txtUrlAnh.setText(sp.getUrlAnh() != null ? sp.getUrlAnh() : "");
        hienAnh(sp.getUrlAnh());
    }

    private void hienAnh(String url) {
        if (url != null && !url.isBlank()) {
            try { imgPreview.setImage(new Image(url, true)); }
            catch (Exception e) { imgPreview.setImage(null); }
        } else { imgPreview.setImage(null); }
    }

    // ===== XỬ LÝ NÚT =====

    @FXML private void handleThemMoi() { datTrangThaiThemMoi(); }

    @FXML
    private void handleChonAnh() {
        FileChooser fc = new FileChooser();
        fc.setTitle("Chọn ảnh sản phẩm");
        fc.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Ảnh (*.jpg, *.png, *.webp)", "*.jpg","*.jpeg","*.png","*.webp"));
        File file = fc.showOpenDialog(imgPreview.getScene().getWindow());
        if (file == null) return;

        // Upload nền để không đơ UI
        btnLuu.setDisable(true);
        new Thread(() -> {
            String url = CloudinaryUploader.uploadImage(file);
            Platform.runLater(() -> {
                btnLuu.setDisable(false);
                if (url != null) {
                    txtUrlAnh.setText(url);
                    hienAnh(url);
                    showAlert(Alert.AlertType.INFORMATION, "Thành công", "Upload ảnh lên Cloudinary thành công!");
                } else {
                    showAlert(Alert.AlertType.ERROR, "Thất bại", "Upload ảnh thất bại! Kiểm tra kết nối mạng.");
                }
            });
        }).start();
    }

    @FXML
    private void handleLuu() {
        String tenSp  = txtTenSP.getText().trim();
        String hangSx = txtHangSX.getText().trim();
        String giaStr = txtGiaBan.getText().trim().replace(",", "").replace(".", "");
        String slStr  = txtSoLuong.getText().trim();

        if (tenSp.isEmpty() || hangSx.isEmpty() || giaStr.isEmpty() || slStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng điền đầy đủ các trường bắt buộc (*)!");
            return;
        }

        double giaBan; int soLuong;
        try {
            giaBan  = Double.parseDouble(giaStr);
            soLuong = Integer.parseInt(slStr);
            if (giaBan <= 0 || soLuong < 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.WARNING, "Dữ liệu sai", "Giá bán phải > 0 và số lượng phải >= 0!");
            return;
        }

        String urlAnh = txtUrlAnh.getText().trim();
        boolean ok;

        if (spDangChon == null) {
            // THÊM MỚI
            ok = dao.themSanPham(new SanPham(0, tenSp, hangSx, giaBan, soLuong, urlAnh));
            if (ok) showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã thêm sản phẩm mới!");
        } else {
            // CẬP NHẬT
            spDangChon.setTenSp(tenSp); spDangChon.setHangSx(hangSx);
            spDangChon.setGiaBan(giaBan); spDangChon.setSoLuong(soLuong);
            spDangChon.setUrlAnh(urlAnh);
            ok = dao.suaSanPham(spDangChon);
            if (ok) showAlert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật sản phẩm!");
        }

        if (ok) { taiDuLieu(); datTrangThaiThemMoi(); }
        else showAlert(Alert.AlertType.ERROR, "Lỗi", "Thao tác thất bại! Vui lòng thử lại.");
    }

    @FXML
    private void handleXoa() {
        SanPham sp = tableSanPham.getSelectionModel().getSelectedItem();
        if (sp == null) { showAlert(Alert.AlertType.WARNING, "Chưa chọn", "Chọn sản phẩm cần xóa!"); return; }

        Alert cf = new Alert(Alert.AlertType.CONFIRMATION,
                "Xóa \"" + sp.getTenSp() + "\"?\nKhông thể xóa nếu đã có trong đơn hàng!", ButtonType.OK, ButtonType.CANCEL);
        cf.setTitle("Xác nhận xóa");
        cf.setHeaderText(null);
        cf.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                if (dao.xoaSanPham(sp.getId())) { taiDuLieu(); datTrangThaiThemMoi();
                    showAlert(Alert.AlertType.INFORMATION, "Đã xóa", "Xóa sản phẩm thành công!"); }
                else showAlert(Alert.AlertType.ERROR, "Lỗi", "Không thể xóa! Sản phẩm đang có trong đơn hàng.");
            }
        });
    }

    private void datTrangThaiThemMoi() {
        spDangChon = null;
        lblTieuDe.setText("➕  THÊM SẢN PHẨM MỚI");
        txtTenSP.clear(); txtHangSX.clear(); txtGiaBan.clear();
        txtSoLuong.clear(); txtUrlAnh.clear(); imgPreview.setImage(null);
        tableSanPham.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert a = new Alert(type); a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}
