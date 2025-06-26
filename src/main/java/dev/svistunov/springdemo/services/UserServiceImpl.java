package dev.svistunov.springdemo.services;

import dev.svistunov.springdemo.dto.request.UserContactsInputDto;
import dev.svistunov.springdemo.dto.request.UserContactsSearchDto;
import dev.svistunov.springdemo.dto.response.UserContactsDto;
import dev.svistunov.springdemo.exception.UserNotFoundException;
import dev.svistunov.springdemo.model.User;
import dev.svistunov.springdemo.repository.UserRepository;
import dev.svistunov.springdemo.services.interfaces.UserContactsMapper;
import dev.svistunov.springdemo.services.interfaces.UserService;
import dev.svistunov.springdemo.util.PhoneNumberUtils;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserContactsMapper contactsModelMapper;

    UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper, UserContactsMapper contactsModelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.contactsModelMapper = contactsModelMapper;
    }

    // Работа с пользователями
    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public List<User> getAll() {
        return userRepository.findAll();
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    @Override
    public User create(User user) {
        return userRepository.save(user);
    }

    @Override
    public UserContactsDto createUserContact(UserContactsInputDto userDto) {
        User user = contactsModelMapper.toEntity(userDto);
        User createdUser = userRepository.save(user);
        return contactsModelMapper.toDto(createdUser);
    }

    @Override
    public User update(Long id, User user) {
        User oldUser = this.getById(id);
        modelMapper.map(user, oldUser);
        return userRepository.save(oldUser);
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

    // Работа с контактной информацией
    @Override
    public List<UserContactsDto> getAllContacts() {
        return userRepository.findAll()
                .stream()
                .map(this::convertToDto)
                .toList();
    }

    /**
     * Возвращает список пользователей (контактов) с фильтрацией, пагинацией и сортировкой
     * @param searchDto
     * @param pageable
     * @return
     */
    public Page<UserContactsDto> searchUserDetails(UserContactsSearchDto searchDto, Pageable pageable) {
        Specification<User> spec = (root, query, cb) -> null;

        if (searchDto.id() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(
                    root.get("id"), searchDto.id()
            ));
        }

        if (StringUtils.hasText(searchDto.firstName())) {
            spec = spec.and((root, query, cb) -> cb.like(
                    cb.lower(root.get("firstName")), "%" + searchDto.firstName().toLowerCase() + "%")
            );
        }

        if (StringUtils.hasText(searchDto.email())) {
            spec = spec.and((root, query, cb) -> cb.equal(
                    root.get("email"), searchDto.email())
            );
        }


        if (StringUtils.hasText(searchDto.phoneNumber())){
            spec = spec.and((root, query, cb) -> cb.equal(
                    root.get("phoneNumber"), PhoneNumberUtils.normalizePhoneNumber(searchDto.phoneNumber(), "RU"))
            );
        }

        return userRepository.findAll(spec, pageable)
                .map(contactsModelMapper::toDto);
    }

    private UserContactsDto convertToDto(User user) {
        return modelMapper.map(user, UserContactsDto.class);
    }

    public UserContactsDto getUserContactsById(Long id) {
        User user = getById(id);
        return contactsModelMapper.toDto(user);
    }

    public UserContactsDto updateContacts(Long id, UserContactsInputDto userContactsDto) {
        User user = getById(id);
        //modelMapper.map(userContactsDto, user);
        user.setFirstName(userContactsDto.getFirstName());
        user.setEmail(userContactsDto.getEmail());
        user.setPhoneNumber(userContactsDto.getPhoneNumber());

        User savedUser = userRepository.save(user);
        return contactsModelMapper.toDto(savedUser);
    }
}
