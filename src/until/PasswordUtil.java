package until;

import org.mindrot.jbcrypt.BCrypt;

public final class PasswordUtil {
    private PasswordUtil() {
    }

    public static String hash(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt(12));
    }

    public static boolean matches(String plainPassword, String storedPassword) {
        if (plainPassword == null || storedPassword == null) {
            return false;
        }

        if (isBCryptHash(storedPassword)) {
            try {
                return BCrypt.checkpw(plainPassword, storedPassword);
            } catch (IllegalArgumentException e) {
                return false;
            }
        }

        return plainPassword.equals(storedPassword);
    }

    public static boolean needsRehash(String storedPassword) {
        return storedPassword == null || !isBCryptHash(storedPassword);
    }

    public static String hashIfNeeded(String password) {
        return isBCryptHash(password) ? password : hash(password);
    }

    private static boolean isBCryptHash(String value) {
        return value != null && value.matches("^\\$2[aby]?\\$\\d{2}\\$.{53}$");
    }
}
