package com.backend.services;

import com.backend.BackendException;
import com.backend.BadLoginException;
import com.backend.bodies.ChatForm;
import com.backend.models.Chat;
import com.backend.models.viewmodels.AuthUserVm;
import com.backend.bodies.UserForm;
import com.backend.bodies.FriendForm;
import com.backend.models.User;
import com.backend.models.viewmodels.ConvertHelpers;
import com.backend.models.viewmodels.UserVm;
import com.backend.repos.ImageRepository;
import com.backend.repos.UserRepository;
import org.springframework.core.io.InputStreamResource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class UserService {
    private final UserRepository userRepo;

    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    public UserService(UserRepository userRepo, ImageRepository imageRepo) {
        this.userRepo = userRepo;
    }

    public List<UserVm> getAll() {
        try {
            return userRepo.findAll().stream().map(ConvertHelpers::toUserVm).collect(Collectors.toList());
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to fetch users", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public UserVm get(long id) {
        return ConvertHelpers.toUserVm(getFull(id));
    }

    public User getFull(long id) {
        try {
            var userOpt = userRepo.findById(id);
            if (userOpt.isEmpty()) {
                throw new BackendException("User not found", HttpStatus.NOT_FOUND);
            }
            return userOpt.get();
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to fetch user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public UserVm get(String token) {
        return ConvertHelpers.toUserVm(getFull(token));
    }

    public User getFull(String token) {
        try {
            var userOpt = userRepo.findByToken(token);
            if (userOpt.isEmpty()) {
                throw new BackendException("User not found", HttpStatus.NOT_FOUND);
            }
            return userOpt.get();
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to fetch user by token", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public AuthUserVm get(String usernameOrPassword, String password) {
        try {
            var userOpt = userRepo.findByEmailAndPassword(usernameOrPassword, password);
            if (userOpt.isPresent()) {
                return ConvertHelpers.toAuthUserVm(userOpt.get());
            }

            userOpt = userRepo.findByUsernameAndPassword(usernameOrPassword, password);
            if (userOpt.isPresent()) {
                return ConvertHelpers.toAuthUserVm(userOpt.get());
            }
            throw new BadLoginException("Bad username/email or password");
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to fetch auth user by username/email and password", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean validUserToken(String token) {
        try {
            var userOpt = userRepo.findByToken(token);
            return userOpt.isPresent();
        } catch (DataAccessException ignored) {
            return false;
        }
    }

    public void add(UserForm userForm) {
        try {
            var user = new User(userForm.email(), userForm.username(), userForm.password());
            // Manually generate a token for the user when it is created
            user.setToken(generateToken());
            userRepo.save(user);
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to create user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private String generateToken() {
        var ranString = UUID.randomUUID().toString();
        return ranString.replace("-", "");
    }

    public UserVm getByEmail(String email) {
        return ConvertHelpers.toUserVm(getByEmailFull(email));
    }

    public User getByEmailFull(String email) {
        try {
            var userOpt = userRepo.findByEmail(email);
            if (userOpt.isEmpty()) {
                throw new BackendException("User not found", HttpStatus.NOT_FOUND);
            }
            return userOpt.get();
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to fetch user by email", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<UserVm> getAllLike(String searchFor) {
        try {
            var users = userRepo.findAllLike(searchFor);
            return users.stream().map(ConvertHelpers::toUserVm).collect(Collectors.toList());
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to fetch users by search string", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public List<UserVm> getFriends(String token) {
        try {
            var user = getFull(token);
            return user.getFriends().stream().map(ConvertHelpers::toUserVm).collect(Collectors.toList());
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to fetch friends", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void addFriend(FriendForm friendForm) {
        try {
            var user = getFull(friendForm.token());
            var otherUser = getFull(friendForm.friendId());
            user.addFriend(otherUser);
            otherUser.addFriend(user);
            userRepo.save(user);
            userRepo.save(otherUser);
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to add friend", HttpStatus.INTERNAL_SERVER_ERROR);

        }
    }

    public void removeFriend(FriendForm friendForm) {
        try {
            var user = getFull(friendForm.token());
            var otherUser = getFull(friendForm.friendId());
            user.removeFriend(otherUser);
            otherUser.removeFriend(user);
            userRepo.save(user);
            userRepo.save(otherUser);
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to remove friend", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    public void save(User user) {
        try {
            userRepo.save(user);
        } catch (DataAccessException ignored) {
            throw new BackendException("Failed to save user", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
