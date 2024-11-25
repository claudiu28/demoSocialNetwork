package ubb.scs.map.demosocialnetwork.repository;

import org.junit.jupiter.api.*;
import ubb.scs.map.demosocialnetwork.domain.Friendship;
import ubb.scs.map.demosocialnetwork.domain.User;
import ubb.scs.map.demosocialnetwork.domain.validator.FriendshipValidation;
import ubb.scs.map.demosocialnetwork.domain.validator.UserValidation;
import ubb.scs.map.demosocialnetwork.repository.database.FriendshipsDB;
import ubb.scs.map.demosocialnetwork.repository.database.UsersDB;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.StreamSupport;

import static org.junit.jupiter.api.Assertions.*;

public class RepositoryDBTest {
    private Repository<Long, User> repositoryUser;
    private Repository<Long, Friendship> repositoryFriendship;

    @BeforeAll
    public static void setUp() throws Exception {
        System.setProperty("test.mode", "true");

    }

    @BeforeEach
    public void init() throws Exception {
        repositoryUser = new UsersDB(new UserValidation());
        repositoryFriendship = new FriendshipsDB(new FriendshipValidation());
    }

    @AfterEach
    public void cleanUp() throws Exception {
        repositoryUser.findAll().forEach(user -> repositoryUser.delete(user.getId()));
        repositoryFriendship.findAll().forEach(friendship -> repositoryFriendship.delete(friendship.getId()));
    }

    @AfterAll
    public static void tearDown() throws Exception {
        System.clearProperty("test.mode");
    }

    @Test
    public void TestCRUDSaveUser() {
        User userOne = new User("Mike", "Doe", "John", "MikeJohn@gmail.com", "password1234");
        User userTwo = new User("Tudor", "Dima", "John", "johnDima@gmail.com", "password1234");

        Optional<User> user1 = repositoryUser.save(userOne);
        Optional<User> user2 = repositoryUser.save(userTwo);

        assertTrue(user1.isPresent());
        assertTrue(user2.isPresent());

        assertEquals("Mike", user1.get().getUsername());
        assertEquals("Tudor", user2.get().getUsername());

        assertEquals("Doe", user1.get().getLastName());
        assertEquals("Dima", user2.get().getLastName());

        assertEquals("John", user1.get().getFirstName());
        assertEquals("John", user2.get().getFirstName());

        assertEquals("MikeJohn@gmail.com", user1.get().getEmail());
        assertEquals("johnDima@gmail.com", user2.get().getEmail());

        assertEquals("password1234", user1.get().getPassword());
        assertEquals("password1234", user2.get().getPassword());
    }

    @Test
    public void TestCRUDDeleteUser() {
        User userOne = new User("Mike", "Doe", "John", "MikeJohn@gmail.com", "password1234");
        User userTwo = new User("Tudor", "Dima", "John", "johnDima@gmail.com", "password1234");
        Optional<User> user1 = repositoryUser.save(userOne);
        Optional<User> user2 = repositoryUser.save(userTwo);

        assertTrue(user1.isPresent());
        assertTrue(user2.isPresent());

        assertEquals("Mike", user1.get().getUsername());
        assertEquals("Tudor", user2.get().getUsername());

        Optional<User> deletedUser1 = repositoryUser.delete((Long) user1.get().getId());
        Optional<User> deletedUser2 = repositoryUser.delete((Long) user2.get().getId());

        assertTrue(deletedUser1.isPresent());
        assertTrue(deletedUser2.isPresent());

        assertEquals(user1.get(), deletedUser1.get());
        assertEquals(user2.get(), deletedUser2.get());

        assertFalse(repositoryUser.findOne(user1.get().getId()).isPresent());
        assertFalse(repositoryUser.findOne(user2.get().getId()).isPresent());
    }

    @Test
    public void TestCRUDUpdateUser() {
        User userOne = new User("Mike", "Doe", "John", "MikeJohn@gmail.com", "password1234");
        Optional<User> user = repositoryUser.save(userOne);
        assertTrue(user.isPresent());

        User updatedUser = new User("Mike", "Smith", "A", "MikeJohn@gmail.com", "password1234");
        updatedUser.setId(user.get().getId());

        Optional<User> userOld = repositoryUser.update(updatedUser);
        assertTrue(userOld.isPresent());

        Optional<User> user1 = repositoryUser.findOne(user.get().getId());
        assertTrue(user1.isPresent());
        assertEquals(updatedUser, user1.get());
        assertEquals("Smith", user1.get().getLastName());
    }

    @Test
    public void TestCRUDFindOneUser() {
        User userOne = new User("Mike", "Doe", "John", "MikeJohn@gmail.com", "password1234");
        User userTwo = new User("Tudor", "Dima", "John", "johnDima@gmail.com", "password1234");

        Optional<User> user1 = repositoryUser.save(userOne);
        Optional<User> user2 = repositoryUser.save(userTwo);

        assertTrue(user1.isPresent());
        assertTrue(user2.isPresent());

        Optional<User> user = repositoryUser.findOne(user1.get().getId());
        assertTrue(user.isPresent());
        assertEquals(user1.get(), user.get());
        assertEquals("Mike", user.get().getUsername());

        user = repositoryUser.findOne(user2.get().getId());
        assertTrue(user.isPresent());
        assertEquals(user2.get(), user.get());
        assertEquals("Tudor", user.get().getUsername());

    }

    @Test
    public void TestCRUDFindAllUsers() {
        User userOne = new User("Mike", "Doe", "John", "MikeJohn@gmail.com", "password1234");
        User userTwo = new User("Tudor", "Dima", "John", "johnDima@gmail.com", "password1234");
        User userThree = new User("John", "Doe", "Mike", "johnDoe@gmail.com", "password1234");

        Optional<User> user1 = repositoryUser.save(userOne);
        Optional<User> user2 = repositoryUser.save(userTwo);
        Optional<User> user3 = repositoryUser.save(userThree);

        assertTrue(user1.isPresent());
        assertTrue(user2.isPresent());
        assertTrue(user3.isPresent());

        Iterable<User> users = repositoryUser.findAll();
        List<User> Users = StreamSupport.stream(users.spliterator(), false).toList();
        assertEquals(3, Users.size());
        assertEquals(userOne.getLastName(), Users.getFirst().getLastName());
        assertEquals(userTwo.getLastName(), Users.get(1).getLastName());
        assertEquals(userOne.getLastName(), Users.getLast().getLastName());
    }


    @Test
    public void TestCRUDSaveFriendship() {
        User userOne = new User("Mike", "Doe", "John", "MikeJohn@gmail.com", "password1234");
        User userTwo = new User("Tudor", "Dima", "John", "johnDima@gmail.com", "password1234");
        User userThree = new User("John", "Doe", "Mike", "johnDoe@gmail.com", "password1234");

        Optional<User> user1 = repositoryUser.save(userOne);
        Optional<User> user2 = repositoryUser.save(userTwo);
        Optional<User> user3 = repositoryUser.save(userThree);

        assertTrue(user1.isPresent());
        assertTrue(user2.isPresent());
        assertTrue(user3.isPresent());


        LocalDateTime now = LocalDateTime.now();
        Friendship friendshipOne = new Friendship(user1.get().getId(),user2.get().getId(), "ACCEPTED", now);
        Friendship friendshipTwo = new Friendship(user1.get().getId(),user3.get().getId(), "ACCEPTED", now);

        Optional<Friendship> friendship1 = repositoryFriendship.save(friendshipOne);
        Optional<Friendship> friendship2 = repositoryFriendship.save(friendshipTwo);

        assertTrue(friendship1.isPresent());
        assertTrue(friendship2.isPresent());

        assertEquals(user1.get().getId(), friendship1.get().getIdUser1());
        assertEquals(user2.get().getId(), friendship1.get().getIdUser2());

        assertEquals(user1.get().getId(), friendship1.get().getIdUser1());
        assertEquals(user3.get().getId(), friendship2.get().getIdUser2());

        assertEquals("ACCEPTED", friendship1.get().getStatus());

        assertTrue(ChronoUnit.SECONDS.between(friendship1.get().getDate(), now) < 5);
        assertTrue(ChronoUnit.SECONDS.between(friendship2.get().getDate(), now) < 5);

    }

    @Test
    public void TestCRUDDeleteFriendship() {
        User userOne = new User("Mike", "Doe", "John", "MikeJohn@gmail.com", "password1234");
        User userTwo = new User("Tudor", "Dima", "John", "johnDima@gmail.com", "password1234");
        User userThree = new User("John", "Doe", "Mike", "johnDoe@gmail.com", "password1234");

        Optional<User> user1 = repositoryUser.save(userOne);
        Optional<User> user2 = repositoryUser.save(userTwo);
        Optional<User> user3 = repositoryUser.save(userThree);

        assertTrue(user1.isPresent());
        assertTrue(user2.isPresent());
        assertTrue(user3.isPresent());


        LocalDateTime now = LocalDateTime.now();
        Friendship friendshipOne = new Friendship(user1.get().getId(),user2.get().getId(), "ACCEPTED", now);
        Friendship friendshipTwo = new Friendship(user1.get().getId(),user3.get().getId(), "ACCEPTED", now);

        Optional<Friendship> friendship1 = repositoryFriendship.save(friendshipOne);
        Optional<Friendship> friendship2 = repositoryFriendship.save(friendshipTwo);

        assertTrue(friendship1.isPresent());
        assertTrue(friendship2.isPresent());

        Optional<Friendship> deletedFriendship1 = repositoryFriendship.delete((Long) friendship1.get().getId());

        assertTrue(deletedFriendship1.isPresent());

        assertEquals(friendship1.get(), deletedFriendship1.get());

        assertFalse(repositoryUser.findOne(friendship1.get().getId()).isPresent());

    }

    @Test
    public void TestCRUDFindOneFriendship() {
        User userOne = new User("Mike", "Doe", "John", "MikeJohn@gmail.com", "password1234");
        User userTwo = new User("Tudor", "Dima", "John", "johnDima@gmail.com", "password1234");
        User userThree = new User("John", "Doe", "Mike", "johnDoe@gmail.com", "password1234");

        Optional<User> user1 = repositoryUser.save(userOne);
        Optional<User> user2 = repositoryUser.save(userTwo);
        Optional<User> user3 = repositoryUser.save(userThree);

        assertTrue(user1.isPresent());
        assertTrue(user2.isPresent());
        assertTrue(user3.isPresent());

        LocalDateTime now = LocalDateTime.now();
        Friendship friendshipOne = new Friendship(user1.get().getId(),user2.get().getId(), "ACCEPTED", now);
        Friendship friendshipTwo = new Friendship(user1.get().getId(),user3.get().getId(), "ACCEPTED", now);

        Optional<Friendship> friendship1 = repositoryFriendship.save(friendshipOne);
        Optional<Friendship> friendship2 = repositoryFriendship.save(friendshipTwo);

        assertTrue(friendship1.isPresent());
        assertTrue(friendship2.isPresent());

        Optional<Friendship> findFriendship1 = repositoryFriendship.findOne((Long) friendship1.get().getId());

        assertTrue(findFriendship1.isPresent());

        assertEquals(friendship1.get(), findFriendship1.get());

        assertFalse(repositoryUser.findOne(friendship1.get().getId()).isPresent());
    }

    @Test
    public void TestCRUDFindAllFriendships() {
        User userOne = new User("Mike", "Doe", "John", "MikeJohn@gmail.com", "password1234");
        User userTwo = new User("Tudor", "Dima", "John", "johnDima@gmail.com", "password1234");
        User userThree = new User("John", "Doe", "Mike", "johnDoe@gmail.com", "password1234");

        Optional<User> user1 = repositoryUser.save(userOne);
        Optional<User> user2 = repositoryUser.save(userTwo);
        Optional<User> user3 = repositoryUser.save(userThree);

        assertTrue(user1.isPresent());
        assertTrue(user2.isPresent());
        assertTrue(user3.isPresent());

        LocalDateTime now = LocalDateTime.now();
        Friendship friendshipOne = new Friendship(user1.get().getId(),user2.get().getId(), "ACCEPTED", now);
        Friendship friendshipTwo = new Friendship(user1.get().getId(),user3.get().getId(), "ACCEPTED", now);

        Optional<Friendship> friendship1 = repositoryFriendship.save(friendshipOne);
        Optional<Friendship> friendship2 = repositoryFriendship.save(friendshipTwo);

        assertTrue(friendship1.isPresent());
        assertTrue(friendship2.isPresent());

        Iterable<Friendship> friendships = repositoryFriendship.findAll();
        List<Friendship> friendshipsList = StreamSupport.stream(friendships.spliterator(), false).toList();
        assertEquals(2, friendshipsList.size());
        assertEquals(friendshipOne.getIdUser1(), friendshipsList.getFirst().getIdUser1());
        assertEquals(friendshipTwo.getIdUser1(), friendshipsList.getLast().getIdUser1());
        assertEquals("ACCEPTED", friendshipsList.getFirst().getStatus());
        assertEquals("ACCEPTED", friendshipsList.getLast().getStatus());

    }

    @Test
    public void TestCRUDUpdateFriendships() {
        User userOne = new User("Mike", "Doe", "John", "MikeJohn@gmail.com", "password1234");
        User userTwo = new User("Tudor", "Dima", "John", "johnDima@gmail.com", "password1234");
        User userThree = new User("John", "Doe", "Mike", "johnDoe@gmail.com", "password1234");

        Optional<User> user1 = repositoryUser.save(userOne);
        Optional<User> user2 = repositoryUser.save(userTwo);
        Optional<User> user3 = repositoryUser.save(userThree);

        assertTrue(user1.isPresent());
        assertTrue(user2.isPresent());
        assertTrue(user3.isPresent());

        LocalDateTime now = LocalDateTime.now();
        Friendship friendshipOne = new Friendship(user1.get().getId(),user2.get().getId(), "ACCEPTED", now);
        Friendship friendshipTwo = new Friendship(user1.get().getId(),user3.get().getId(), "ACCEPTED", now);

        Optional<Friendship> friendship1 = repositoryFriendship.save(friendshipOne);
        Optional<Friendship> friendship2 = repositoryFriendship.save(friendshipTwo);

        assertTrue(friendship1.isPresent());
        assertTrue(friendship2.isPresent());

        Friendship updatedFriendship = new Friendship(user1.get().getId(),user3.get().getId(), "DECLINED", now);
        updatedFriendship.setId(friendship2.get().getId());

        Optional<Friendship> friendshipOld = repositoryFriendship.update(updatedFriendship);
        assertTrue(friendshipOld.isPresent());

        Optional<Friendship> friendship = repositoryFriendship.findOne(friendship2.get().getId());
        assertTrue(friendship.isPresent());
        assertEquals(updatedFriendship, friendship2.get());
        assertEquals("DECLINED", friendship.get().getStatus());

    }
}
