package common.utils;

import java.util.regex.Pattern;

public class Validator {

    // Regex đơn giản, đủ dùng cho form nhập liệu
    private static final Pattern PHONE_PATTERN = Pattern.compile("^(0|\\+84)\\d{9}$");
    private static final Pattern EMAIL_PATTERN =
            Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    // Check xem chuỗi rỗng/null
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    // Check không rỗng
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    // Check số nguyên dương (>= 0)
    public static boolean isPositiveInteger(String str) {
        if (isEmpty(str)) return false;
        try {
            int n = Integer.parseInt(str.trim());
            return n >= 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // Check số điện thoại VN: 0xxxxxxxxx hoặc +84xxxxxxxxx
    public static boolean isValidPhone(String phone) {
        if (isEmpty(phone)) return false;
        String normalized = phone.trim().replaceAll("\\s+", "");
        return PHONE_PATTERN.matcher(normalized).matches();
    }

    // Check email cơ bản
    public static boolean isValidEmail(String email) {
        if (isEmpty(email)) return false;
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }
}