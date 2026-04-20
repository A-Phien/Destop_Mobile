package Model;

public class TaiKhoan {
    private int id;
    private String username;
    private String password;
    private String vaiTro; // "ADMIN" hoặc "STAFF"

    // Constructor rỗng (Bắt buộc phải có để các Framework hoặc DAO sử dụng)
    public TaiKhoan() {
    }

    public TaiKhoan(int id, String username, String password, String vaiTro) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.vaiTro = vaiTro;
    }

    // --- Getters và Setters ---
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getVaiTro() { return vaiTro; }
    public void setVaiTro(String vaiTro) { this.vaiTro = vaiTro; }

    @Override
    public String toString() {
        return "TaiKhoan{" + "id=" + id + ", username='" + username + '\'' + ", vaiTro='" + vaiTro + '\'' + '}';
    }
}