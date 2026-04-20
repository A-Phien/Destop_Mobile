package until;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

public class CloudinaryUploader {

    private static final String CLOUD_NAME = "dqqbvzmrr";
    private static final String API_KEY = "789644833813378";
    private static final String API_SECRET = "15CW3h71RPsMQWIJ60tIVkRhbO0";

    // Khởi tạo trận đồ Cloudinary
    private static final Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
            "cloud_name", CLOUD_NAME,
            "api_key", API_KEY,
            "api_secret", API_SECRET,
            "secure", true
    ));

    public static String uploadImage(File fileAnh) {
        if (fileAnh == null || !fileAnh.exists()) {
            System.err.println(">> [Cảnh báo] File ảnh không tồn tại! Ngươi định ném không khí lên mây à?");
            return null;
        }

        try {
            System.out.println(">> [Hệ thống] Đang vận công đẩy ảnh lên mây...");
            
            // Thực hiện upload, tự động gom file và đặt tên ngẫu nhiên
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(fileAnh, ObjectUtils.emptyMap());
            
            // Lấy đường dẫn an toàn (HTTPS) trả về
            String url = (String) uploadResult.get("secure_url");
            
            System.out.println(">> [Hệ thống] Đẩy ảnh thành công! Link URL: " + url);
            return url;
            
        } catch (IOException e) {
            System.err.println(">> [Cảnh báo] Mạng nhện đứt đoạn! Upload thất bại!");
            e.printStackTrace();
            return null;
        }
    }

    // Hàm test thử nội công, không cần giao diện
    public static void main(String[] args) {
        // Ví dụ Windows: "C:\\Users\\Public\\Pictures\\Sample Pictures\\Koala.jpg"
        File anhTest = new File("C:\\Users\\Admin\\Pictures\\z7722305205414_fd166bba5289c22623af2aa59182917f.jpg"); 
        
        String linkTraVe = uploadImage(anhTest);
        
        if (linkTraVe != null) {
            System.out.println("Hãy copy link này dán lên trình duyệt web để chiêm ngưỡng thành quả: " + linkTraVe);
        }
    }
}