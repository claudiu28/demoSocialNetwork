package ubb.scs.map.demosocialnetwork.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ubb.scs.map.demosocialnetwork.domain.validator.UserValidation;
import ubb.scs.map.demosocialnetwork.domain.validator.ValidationException;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User user5;
    private User user6;
    private UserValidation validator;

    @BeforeEach
    void setUp() {
        user1 = new User("Mike", "Doe", "John", "mikejohn@gmail.com", "password1234");
        user2 = new User("", "Doe", "John", "mikejohn@gmail.com", "password1234");
        user3 = new User("Mike", "", "John", "mikejohn@gmail.com", "password1234");
        user4 = new User("Mike", "Doe", "", "mikejohn@gmail.com", "password1234");
        user5 = new User("Mike", "Doe", "John", "MikeJohn_gmail.com", "password1234");
        user6 = new User("Mike", "Doe", "John", "mikejohn@gmail.com", "1234");

        user1.setId(1L);
        user2.setId(2L);
        user3.setId(3L);
        user4.setId(4L);
        user5.setId(5L);
        user6.setId(6L);

        validator = new UserValidation();
    }

    @Test
    void testValidUser() {
        assertDoesNotThrow(() -> validator.validate(user1));
    }

    @Test
    void testInvalidUserUsername() {
        assertThrows(ValidationException.class, () -> validator.validate(user2));
    }

    @Test
    void testInvalidUserLastName() {
        assertThrows(ValidationException.class, () -> validator.validate(user3));
    }

    @Test
    void testInvalidUserFirstName() {
        assertThrows(ValidationException.class, () -> validator.validate(user4));
    }

    @Test
    void testInvalidUserEmail() {
        assertThrows(ValidationException.class, () -> validator.validate(user5));
    }

    @Test
    void testInvalidUserPassword() {
        assertThrows(ValidationException.class, () -> validator.validate(user6));
    }

    @Test
    void testVerifyGetters() {

        String username = user1.getUsername();
        String lastName = user1.getLastName();
        String firstName = user1.getFirstName();
        String email = user1.getEmail();
        String password = user1.getPassword();

        assertEquals("Mike", username);
        assertEquals("Doe", lastName);
        assertEquals("John", firstName);
        assertEquals("mikejohn@gmail.com", email);
        assertEquals("password1234", password);
    }

    @Test
    void testVerifySetters() {
        user1.setUsername("Nick28");
        assertEquals("Nick28", user1.getUsername());

        user1.setLastName("Claudiu");
        assertEquals("Claudiu", user1.getLastName());

        user1.setFirstName("Ionut");
        assertEquals("Ionut", user1.getFirstName());

        user1.setEmail("Nick28@gmail.com");
        assertEquals("Nick28@gmail.com", user1.getEmail());

        user1.setPassword("12345678");
        assertEquals("12345678", user1.getPassword());
    }

    @Test
    void testEquals() {
        User userOneCopy = new User("Mike", "Doe", "John", "mikejohn@gmail.com", "password1234");
        User differentUser = new User("Mike", "Smith", "John", "different@gmail.com", "password1234");

        assertEquals(user1, userOneCopy);
        assertNotEquals(user1, differentUser);
        assertNotEquals(null, user1);
    }

    @Test
    void testToString() {
        String expected = "User: [username]: Mike[email]: mikejohn@gmail.com[lastName]: Doe[firstName]: John";
        assertEquals(expected, user1.toString());
    }
}