package com.jackdonahue.investingarena.Service;

import com.jackdonahue.investingarena.Model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

//Used for strictly unit testing, when there are no spring containers involved
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private RedisTemplate<String, Object> redisTemplate;

    //ValueOperations is used indirectly in redisTemplate.opsForValue
    @Mock
    private ValueOperations<String, Object> valueOperations;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
    }

    //Testing the service to see if it can successfully retrieve the user
    @Test
    void testGetUser_Success() {
        String username = "donny";
        User mockUser = new User(username);
        String key = "user:" + username;

        //Return the mocked user from valueOperations
        when(valueOperations.get(key)).thenReturn(mockUser);

        //Test the service
        User user =  userService.getUser(username);

        assertNotNull(user);
        assertEquals(username, user.getUsername());
    }

    //Testing the service to see if it will return null for a nonexistent user
    @Test
    void testGetUser_NotFound() {
        String username = "Notfound";
        String key = "user:" + username;

        //Return null from valueOperations.get because the user should be simulated as nonexistent
        when(valueOperations.get(key)).thenReturn(null);

        Exception exception = assertThrows(RuntimeException.class, () -> userService.getUser(username));
        assertEquals("User not found", exception.getMessage());
    }

    //Testing the service to check if it can successfully save a new user
    @Test
    void testSaveUser_Success() {
        User user = new User("donny");
        String key = "user:" + user.getUsername();

        //Mock hasKey should return false because the user shouldn't exist yet
        when(redisTemplate.hasKey(key)).thenReturn(false);

        boolean result = userService.saveUser(user);

        //Test that the user has been saved and that the data has been saved in Redis
        assertTrue(result);
        verify(valueOperations).set(key, user);
    }

    //Testing the saveUser method to see if it will return null if the user already exists
    @Test
    void testSaveUser_Exists() {
        User user = new User("donny");
        String key = "user:" + user.getUsername();

        //Mock hasKey should return true because the user should already exist
        when(redisTemplate.hasKey(key)).thenReturn(true);

        boolean result = userService.saveUser(user);

        //Test that the user has not been saved in redis
        assertFalse(result);
        verify(valueOperations, never()).set(anyString(), any());
    }
}
