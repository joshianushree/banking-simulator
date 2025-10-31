import java.math.BigDecimal;
import java.util.regex.Pattern;

/**
 * Utility class for validating banking-related inputs such as
 * account numbers, holder names, emails, PINs, and transaction amounts.
 * All methods are static and null-safe.
 */
public final class ValidationUtils {

    private ValidationUtils() {} // Prevent instantiation

    // === REGEX PATTERNS ===
    private static final Pattern ACC_NO_PATTERN = Pattern.compile("^\\d{11}$"); // exactly 11 digits
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-z ]{3,}$"); // only letters/spaces, min 3 chars
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");
    private static final Pattern PIN_PATTERN = Pattern.compile("^\\d{4}$"); // exactly 4 digits

    // === ACCOUNT VALIDATIONS ===

    /** Validates that the account number is exactly 11 digits. */
    public static boolean isValidAccountNumber(String acc) {
        return acc != null && ACC_NO_PATTERN.matcher(acc.trim()).matches();
    }

    /** Validates holder name (letters and spaces, at least 3 characters). */
    public static boolean isValidHolderName(String name) {
        return name != null && NAME_PATTERN.matcher(name.trim()).matches();
    }

    /** Validates email address format (basic RFC pattern). */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /** Validates account type (SAVINGS, CURRENT, or STUDENT). */
    public static boolean isValidAccountType(String type) {
        if (type == null) return false;
        String t = type.trim().toUpperCase();
        return t.equals("SAVINGS") || t.equals("CURRENT") || t.equals("STUDENT");
    }

    /**
     * Validates initial deposit amount based on account type:
     * - STUDENT: no minimum
     * - SAVINGS/CURRENT: minimum ₹1000
     */
    public static boolean isValidInitialDeposit(String accType, BigDecimal amount) {
        if (amount == null) return false;
        String t = accType == null ? "" : accType.trim().toUpperCase();
        if (t.equals("STUDENT")) {
            return amount.compareTo(BigDecimal.ZERO) >= 0;
        } else {
            return amount.compareTo(new BigDecimal("1000")) >= 0;
        }
    }

    /** Ensures amount is positive (strictly greater than zero). */
    public static boolean isPositiveAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /** Validates that a PIN is exactly 4 digits. */
    public static boolean isValidPin(String pin) {
        return pin != null && PIN_PATTERN.matcher(pin.trim()).matches();
    }
}
