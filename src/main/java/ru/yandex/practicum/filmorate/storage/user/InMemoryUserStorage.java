//package ru.yandex.practicum.filmorate.storage.user;
//
//import org.springframework.stereotype.Component;
//import ru.yandex.practicum.filmorate.model.User;
//
//import java.util.*;
//
//@Component
//public class InMemoryUserStorage implements UserStorage {
//
//    private final Map<Long, User> users = new HashMap<>();
//
//    @Override
//    public User createUser(User user) {
//        users.put(user.getId(), user);
//        return user;
//    }
//
//    @Override
//    public Optional<User> getUserById(Long id) {
//        return Optional.ofNullable(users.get(id));
//    }
//
//    @Override
//    public List<User> getAllUsers() {
//        return new ArrayList<>(users.values());
//    }
//}