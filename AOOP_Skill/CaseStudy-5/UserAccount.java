
import java.time.Instant;
import java.util.Optional;
import java.util.Objects;

final class UserAccount {

    private final UserId id;
    private EmailAddress email;
    private VerificationState verification;
    private Optional<PhoneNumber> phone;
    private Optional<PostalAddress> address;
    private AccountStatus status;

    UserAccount(UserId id, EmailAddress email) {
        this.id = id;
        this.email = email;
        this.verification = new Unverified(
            new VerificationToken("TOKEN123"),
            Instant.now().plusSeconds(3600)
        );
        this.phone = Optional.empty();
        this.address = Optional.empty();
        this.status = AccountStatus.PENDING_VERIFICATION;
    }

    public static void main(String[] args) {

        UserId id = new UserId("U101");
        EmailAddress email = new EmailAddress("student@gmail.com");

        UserAccount user = new UserAccount(id, email);

        System.out.println("===== USER ACCOUNT DETAILS =====");
        System.out.println("User ID: " + user.id.value());
        System.out.println("Email: " + user.email.value);
        System.out.println("Verification: " + user.verification);
        System.out.println("Phone: " + user.phone);
        System.out.println("Address: " + user.address);
        System.out.println("Account Status: " + user.status);
        System.out.println("================================");
    }
}

record UserId(String value) {}

record VerificationToken(String value) {}

final class EmailAddress {

    final String value;

    EmailAddress(String value) {
        this.value = Objects.requireNonNull(value).trim().toLowerCase();

        if (!this.value.matches(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email address");
        }
    }
}

final class PhoneNumber {

    final String value;

    PhoneNumber(String value) {
        this.value = Objects.requireNonNull(value).trim();
    }
}

final class PostalAddress {

    final String street, city, postalCode, country;

    PostalAddress(String street, String city, String postalCode, String country) {
        this.street = Objects.requireNonNull(street);
        this.city = Objects.requireNonNull(city);
        this.postalCode = Objects.requireNonNull(postalCode);
        this.country = Objects.requireNonNull(country);
    }
}

sealed interface VerificationState permits Unverified, Verified {}

final class Unverified implements VerificationState {

    VerificationToken token;
    Instant expiry;

    Unverified(VerificationToken token, Instant expiry) {
        this.token = token;
        this.expiry = expiry;
    }

    @Override
    public String toString() {
        return "Unverified";
    }
}

final class Verified implements VerificationState {

    Instant verifiedAt;

    Verified(Instant verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
}

enum AccountStatus {
    PENDING_VERIFICATION,
    ACTIVE,
    SUSPENDED,
    DELETED
}
