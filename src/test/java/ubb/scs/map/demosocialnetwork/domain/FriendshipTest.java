package ubb.scs.map.demosocialnetwork.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ubb.scs.map.demosocialnetwork.domain.validator.FriendshipValidation;
import ubb.scs.map.demosocialnetwork.domain.validator.ValidationException;
import static org.junit.jupiter.api.Assertions.*;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class FriendshipTest {
    private Friendship friendship1;
    private Friendship friendship2;
    private Friendship friendship3;
    private Friendship friendship4;
    private Friendship friendship5;
    private Friendship friendship6;

    private FriendshipValidation validator;
    @BeforeEach
    void setUp() {
        friendship1 = new Friendship(1L,2L,"PENDING", LocalDateTime.now());
        friendship2 = new Friendship(-10L,2L,"PENDING", LocalDateTime.now());
        friendship3 = new Friendship(1L,-2L,"PENDING", LocalDateTime.now());
        friendship4 = new Friendship(1L,2L,"REJECTED", LocalDateTime.now());
        friendship5 = new Friendship(1L,2L,"ACCEPTED", LocalDateTime.now());
        friendship6 = new Friendship(1L,2L,"DECLINED", LocalDateTime.now());
        friendship1.setId(1L);
        friendship2.setId(2L);
        friendship3.setId(3L);
        friendship4.setId(4L);
        friendship5.setId(5L);
        friendship6.setId(6L);
        validator = new FriendshipValidation();
    }

    @Test
    void testFriendship1() {
        assertDoesNotThrow(() -> validator.validate(friendship1));
    }

    @Test
    void testFriendship2() {
        assertThrows(ValidationException.class,() -> validator.validate(friendship2));
    }

    @Test
    void testFriendship3() {
        assertThrows(ValidationException.class,() -> validator.validate(friendship3));
    }

    @Test
    void testFriendship4() {
        assertThrows(ValidationException.class,() -> validator.validate(friendship4));
    }

    @Test
    void testFriendship5() {
        assertDoesNotThrow(() -> validator.validate(friendship5));
    }

    @Test
    void testFriendship6() {
        assertDoesNotThrow(() -> validator.validate(friendship6));
    }

    @Test
    void testFriendshipGetters(){
        Friendship friendship = new Friendship(1L,2L,"PENDING", LocalDateTime.now());
        assertEquals(1L,friendship.getIdUser1());
        assertEquals(2L,friendship.getIdUser2());
        assertEquals("PENDING",friendship.getStatus());
    }
    @Test
    void testFriendshipSetters(){
        Friendship friendship = new Friendship(1L,2L,"PENDING", LocalDateTime.now());
        assertEquals(1L,friendship.getIdUser1());

        friendship.setIdUser1(3L);
        assertEquals(3L,friendship.getIdUser1());

        friendship.setIdUser2(4L);
        assertEquals(4L,friendship.getIdUser2());

        friendship.setStatus("DECLINED");
        assertEquals("DECLINED",friendship.getStatus());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime friendshipDate = friendship.getDate();

        assertTrue(ChronoUnit.SECONDS.between(friendshipDate, now) < 5);
    }

}
