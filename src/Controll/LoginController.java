package Controll;

import Dao.TaiKhoanDAO;
import Model.TaiKhoan;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import until.UserSession;

public class LoginController {

    @FXML private TextField     txtUsername;
    @FXML private PasswordField txtPassword;
    @FXML private Label         lblError;

    private final TaiKhoanDAO taiKhoanDAO = new TaiKhoanDAO();

    @FXML
    public void initialize() {
        // Enter ở username → nhảy sang password
        txtUsername.setOnAction(e -> txtPassword.requestFocus());
        // Enter ở password → đăng nhập luôn
        txtPassword.setOnAction(e -> handleDangNhap());
        // Gõ lại → xóa thông báo lỗi
        txtUsername.textProperty().addListener((o, c, n) -> lblError.setText(""));
        txtPassword.textProperty().addListener((o, c, n) -> lblError.setText(""));
    }

    @FXML
    private void handleDangNhap() {
        String username = txtUsername.getText().trim();
        String password = txtPassword.getText();

        // Validate đầu vào
        if (username.isEmpty() || password.isEmpty()) {
            setError("⚠  Vui lòng nhập đầy đủ tên đăng nhập và mật khẩu!");
            return;
        }

        // Kiểm tra với database
        TaiKhoan tk = taiKhoanDAO.dangNhap(username, password);

        if (tk == null) {
            setError("✗  Sai tên đăng nhập hoặc mật khẩu. Vui lòng thử lại!");
            txtPassword.clear();
            txtPassword.requestFocus();
            return;
        }

        // Đăng nhập thành công — lưu session
        UserSession.getInstance().dangNhap(tk);

        // Mở màn hình chính, đóng màn hình login
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/View/QuanLyKho.fxml"));
            Stage stage = (Stage) txtUsername.getScene().getWindow();

            Scene mainScene = new Scene(root);
            stage.setTitle("Quản Lý Cửa Hàng Điện Thoại  —  "
                    + tk.getUsername().toUpperCase()
                    + "  [" + tk.getVaiTro() + "]");
            stage.setScene(mainScene);
            stage.setMinWidth(1100);
            stage.setMinHeight(750);
            stage.setResizable(true);
            stage.centerOnScreen();

        } catch (Exception e) {
            setError("Lỗi hệ thống! Không thể mở màn hình chính.");
            e.printStackTrace();
        }
    }

    private void setError(String msg) {
        lblError.setText(msg);
    }
}
