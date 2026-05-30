package until;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

public class CloudinaryUploader {
    private static final String CLOUD_NAME_ENV = "CLOUDINARY_CLOUD_NAME";
    private static final String API_KEY_ENV = "CLOUDINARY_API_KEY";
    private static final String API_SECRET_ENV = "CLOUDINARY_API_SECRET";

    public static String uploadImage(File imageFile) {
        if (imageFile == null || !imageFile.exists()) {
            System.err.println(">> [Warning] Image file does not exist.");
            return null;
        }

        Cloudinary cloudinary = createCloudinary();
        if (cloudinary == null) {
            System.err.println(">> [Warning] Missing Cloudinary environment variables.");
            return null;
        }

        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> uploadResult = cloudinary.uploader().upload(imageFile, ObjectUtils.emptyMap());
            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            System.err.println(">> [Warning] Cloudinary upload failed.");
            e.printStackTrace();
            return null;
        }
    }

    private static Cloudinary createCloudinary() {
        String cloudName = EnvConfig.get(CLOUD_NAME_ENV);
        String apiKey = EnvConfig.get(API_KEY_ENV);
        String apiSecret = EnvConfig.get(API_SECRET_ENV);

        if (EnvConfig.isBlank(cloudName) || EnvConfig.isBlank(apiKey) || EnvConfig.isBlank(apiSecret)) {
            return null;
        }

        return new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

}
