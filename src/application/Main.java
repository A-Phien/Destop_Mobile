package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Mở màn hình Đăng Nhập trước
        Parent root = FXMLLoader.load(getClass().getResource("/View/Login.fxml"));

        Scene scene = new Scene(root);

        primaryStage.setTitle("Đăng Nhập — Quản Lý Cửa Hàng Điện Thoại");
        primaryStage.setScene(scene);
        primaryStage.setWidth(1024);
        primaryStage.setHeight(768);
        primaryStage.setResizable(false);
        primaryStage.centerOnScreen();
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
