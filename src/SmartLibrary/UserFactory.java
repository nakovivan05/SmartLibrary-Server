package SmartLibrary;

import java.util.regex.Pattern;

public class UserFactory {
    private static final Pattern ADMIN_USERNAME_PATTERN = Pattern.compile("^admin_[a-z]{3,}$");
    private static final Pattern READER_USERNAME_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[A-Z]).{6,}$");
    public static User createUser(UserType type, String username, String password) throws ValidationException {
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            throw new ValidationException("Password must be at least 6 characters and contain at least one digit and one capital letter!");
        }
        if (type == UserType.ADMIN) {
            if (!ADMIN_USERNAME_PATTERN.matcher(username).matches()) {
                throw new ValidationException("The admin username must start with admin_ followed by at least 3 small letters!");
            }
            return new Admin(username, password);
        }
        else if (type == UserType.READER) {
            if (!READER_USERNAME_PATTERN.matcher(username).matches()) {
                throw new ValidationException("Reader username must be a valid email address!");
            }
            return new Reader(username, password);
        }
        throw new ValidationException("Invalid user type!");
    }
}
