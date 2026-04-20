package until;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Sửa lại Cổng, Username và Password cho đúng với MySQL trên máy của ngươi!
    // Mặc định XAMPP thì user là 'root', password để trống "".
    // Còn nếu xài MySQL Workbench thì password thường là '123456' hoặc do ngươi tự đặt.
    private static final String URL = "jdbc:mysql://localhost:3306/quan_ly_dien_thoai";
    private static final String USER = "root"; 
    private static final String PASS = ""; // ĐIỀN MẬT KHẨU CỦA NGƯƠI VÀO ĐÂY!

    public static Connection getConnection() {
        Connection conn = null;
        try {
            // Không cần Class.forName() với các bản MySQL Connector mới (từ 8.0 trở lên)
            conn = DriverManager.getConnection(URL, USER, PASS);
            System.out.println(">> [Hệ thống] Kết nối Đan Điền (MySQL) thành công!");
        } catch (SQLException e) {
            System.err.println(">> [Cảnh báo] Tẩu hỏa nhập ma! Không thể kết nối cơ sở dữ liệu!");
            e.printStackTrace();
        }
        return conn;
    }
    
    // Hàm dùng để test nhanh xem cầu nối có sập không
    public static void main(String[] args) {
        getConnection();
    }
}