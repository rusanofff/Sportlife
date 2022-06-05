package diploma.sportlife.service.user;

import diploma.sportlife.exception.EntityAlreadyExistsException;
import diploma.sportlife.exception.MismatchedException;
import diploma.sportlife.exception.notfound.UserNotFoundException;
import diploma.sportlife.model.User;
import diploma.sportlife.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class UserServiceImpl implements UserService{
    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> readAll() {
        return new ArrayList<>(userRepository.findAll());
    }

    @Override
    public User findById(Integer id) {
        return userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    @Override
    public User insertUser(User user) {
        if (userRepository.existsParticipantByFullNameAndDateOfBirth(user.getName(),
                user.getSurname(), user.getDateOfBirth())){
            throw new EntityAlreadyExistsException();
        }
        return userRepository.save(user);
    }

    @Override
    public User deleteById(Integer id) {
        User deleteUser = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
        userRepository.deleteById(id);
        return deleteUser;
    }

    @Override
    public User putById(Integer id, User givenUserFromJson) {
        assertUserExists(id);
        givenUserFromJson.setId(id);
        User userUnique = userRepository.findByFullNameAndDateOfBirth(
                givenUserFromJson.getName(), givenUserFromJson.getSurname(),
        givenUserFromJson.getDateOfBirth());

        if (userUnique != null && !Objects.equals(userUnique.getId(), id)){
            throw new MismatchedException();
        }
        return userRepository.save(givenUserFromJson);
    }

    @Override
    public void assertUserExists(Integer id) {
        if (!userRepository.existsById(id)){
            throw new UserNotFoundException();
        }
    }
}
