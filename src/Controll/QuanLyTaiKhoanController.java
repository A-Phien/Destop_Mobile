package Controll;

import Dao.TaiKhoanDAO;
import Model.TaiKhoan;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import until.UserSession;

public class QuanLyTaiKhoanController {

    // ===== BẢNG =====
    @FXML private TableView<TaiKhoan>          tableTK;
    @FXML private TableColumn<TaiKhoan, Integer> colId;
    @FXML private TableColumn<TaiKhoan, String>  colUsername;
    @FXML private TableColumn<TaiKhoan, String>  colVaiTro;

    // ===== FORM =====
    @FXML private Label        lblTieuDe;
    @FXML private TextField    txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private ComboBox<String> cbVaiTro;

    private final TaiKhoanDAO dao = new TaiKhoanDAO();
    private ObservableList<TaiKhoan> danhSach = FXCollections.observableArrayList();
    private TaiKhoan tkDangChon = null;

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsername.setCellValueFactory(new PropertyValueFactory<>("username"));
        colVaiTro.setCellValueFactory(new PropertyValueFactory<>("vaiTro"));

        // Tô màu hàng ADMIN
        tableTK.setRowFactory(tv -> new TableRow<>() {
            @Override protected void updateItem(TaiKhoan tk, boolean empty) {
                super.updateItem(tk, empty);
                if (!empty && tk != null && "ADMIN".equalsIgnoreCase(tk.getVaiTro())) {
                    setStyle("-fx-background-color: rgba(230,126,34,0.15);");
                } else {
                    setStyle("");
                }
            }
        });

        cbVaiTro.setItems(FXCollections.observableArrayList("ADMIN", "STAFF"));
        cbVaiTro.getSelectionModel().select("STAFF");

        tableTK.getSelectionModel().selectedItemProperty()
                .addListener((obs, cu, moi) -> { if (moi != null) dienVaoForm(moi); });

        taiDuLieu();
        datTrangThaiThemMoi();
    }

    private void taiDuLieu() {
        danhSach = FXCollections.observableArrayList(dao.layDanhSachTaiKhoan());
        tableTK.setItems(danhSach);
    }

    private void dienVaoForm(TaiKhoan tk) {
        tkDangChon = tk;
        lblTieuDe.setText("✏  SỬA TÀI KHOẢN");
        txtUsername.setText(tk.getUsername());
        txtPassword.clear(); // Không hiển thị mật khẩu cũ
        txtPassword.setPromptText("Để trống = giữ nguyên mật khẩu cũ");
        cbVaiTro.getSelectionModel().select(tk.getVaiTro());
    }

    @FXML private void handleThemMoi() { datTrangThaiThemMoi(); }

    @FXML
    private void handleLuu() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();
        String vaiTro   = cbVaiTro.getSelectionModel().getSelectedItem();

        if (username.isEmpty()) {
            alert(Alert.AlertType.WARNING, "Thiếu thông tin", "Username không được để trống!");
            return;
        }

        boolean ok;
        if (tkDangChon == null) {
            // THÊM MỚI — bắt buộc phải có password
            if (password.isEmpty()) {
                alert(Alert.AlertType.WARNING, "Thiếu thông tin", "Mật khẩu không được để trống khi tạo mới!");
                return;
            }
            if (dao.kiemTraUsernameExists(username)) {
                alert(Alert.AlertType.WARNING, "Trùng username", "Username \"" + username + "\" đã tồn tại!");
                return;
            }
            TaiKhoan tk = new TaiKhoan(0, username, password, vaiTro);
            ok = dao.themTaiKhoan(tk);
            if (ok) alert(Alert.AlertType.INFORMATION, "Thành công", "Đã tạo tài khoản mới: " + username);
        } else {
            // CẬP NHẬT — nếu bỏ trống password thì giữ nguyên
            String passCuoi = password.isEmpty() ? tkDangChon.getPassword() : password;
            // Không cho đổi username chính mình nếu trùng với người khác
            tkDangChon.setUsername(username);
            tkDangChon.setPassword(passCuoi);
            tkDangChon.setVaiTro(vaiTro);
            ok = dao.capNhatTaiKhoan(tkDangChon);
            if (ok) alert(Alert.AlertType.INFORMATION, "Thành công", "Đã cập nhật tài khoản!");
        }

        if (ok) { taiDuLieu(); datTrangThaiThemMoi(); }
        else alert(Alert.AlertType.ERROR, "Lỗi", "Thao tác thất bại!");
    }

    @FXML
    private void handleXoa() {
        TaiKhoan tk = tableTK.getSelectionModel().getSelectedItem();
        if (tk == null) { alert(Alert.AlertType.WARNING, "Chưa chọn", "Chọn tài khoản cần xóa!"); return; }

        // Không cho xóa chính mình
        if (tk.getId() == UserSession.getInstance().getTaiKhoanHienTai().getId()) {
            alert(Alert.AlertType.WARNING, "Không thể xóa", "Bạn không thể xóa tài khoản đang đăng nhập!");
            return;
        }

        Alert cf = new Alert(Alert.AlertType.CONFIRMATION,
                "Xóa tài khoản \"" + tk.getUsername() + "\"?", ButtonType.OK, ButtonType.CANCEL);
        cf.setHeaderText(null);
        cf.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                if (dao.xoaTaiKhoan(tk.getId())) {
                    taiDuLieu(); datTrangThaiThemMoi();
                    alert(Alert.AlertType.INFORMATION, "Đã xóa", "Xóa tài khoản thành công!");
                } else alert(Alert.AlertType.ERROR, "Lỗi", "Xóa tài khoản thất bại!");
            }
        });
    }

    private void datTrangThaiThemMoi() {
        tkDangChon = null;
        lblTieuDe.setText("➕  TẠO TÀI KHOẢN MỚI");
        txtUsername.clear();
        txtPassword.clear();
        txtPassword.setPromptText("Nhập mật khẩu...");
        cbVaiTro.getSelectionModel().select("STAFF");
        tableTK.getSelectionModel().clearSelection();
    }

    private void alert(Alert.AlertType t, String title, String msg) {
        Alert a = new Alert(t); a.setTitle(title); a.setHeaderText(null); a.setContentText(msg); a.showAndWait();
    }
}
