package dev.svistunov.springdemo.services;

import dev.svistunov.springdemo.dto.request.UserDetailsInputDto;
import dev.svistunov.springdemo.dto.request.UserDetailsSearchDto;
import dev.svistunov.springdemo.dto.response.UserDetailsDto;
import dev.svistunov.springdemo.exception.UserNotFoundException;
import dev.svistunov.springdemo.model.User;
import dev.svistunov.springdemo.repository.UserRepository;
import dev.svistunov.springdemo.util.PhoneNumberUtils;
import org.modelmapper.ModelMapper;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class UserDetailService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserDetailsMapper detailsModelMapper;

    UserDetailService(UserRepository userRepository, ModelMapper modelMapper, UserContactsMapper contactsModelMapper,
                      UserDetailsMapper detailsModelMapper, PhotoService photoService, Environment env) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.detailsModelMapper = detailsModelMapper;
    }

    // Работа с детальной информацией
    public List<UserDetailsDto> getAllUserDetails() {
        return userRepository.findAll()
                .stream()
                .map(detailsModelMapper::toDto)
                .toList();
    }

    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
     * Возвращает список детальной информации о пользователе с фильтрацией, пагинацией и сортировкой
     * @param searchDto
     * @param pageable
     * @return
     */
    public Page<UserDetailsDto> searchUserDetails(UserDetailsSearchDto searchDto, Pageable pageable) {
        Specification<User> spec = (root, query, cb) -> null;

        if (searchDto.id() != null) {
            spec = spec.and((root, query, cb) -> cb.equal(
                root.get("id"), searchDto.id()
            ));
        }

        if (StringUtils.hasText(searchDto.lastName())) {
            spec = spec.and((root, query, cb) -> cb.like(
                    cb.lower(root.get("lastName")), "%" + searchDto.lastName().toLowerCase() + "%")
            );
        }

        if (StringUtils.hasText(searchDto.firstName())) {
            spec = spec.and((root, query, cb) -> cb.like(
                    cb.lower(root.get("firstName")), "%" + searchDto.firstName().toLowerCase() + "%")
            );
        }

        if (StringUtils.hasText(searchDto.middleName())) {
            spec = spec.and((root, query, cb) -> cb.like(
                    cb.lower(root.get("middleName")), "%" + searchDto.middleName().toLowerCase() + "%")
            );
        }

        if (StringUtils.hasText(searchDto.email())) {
            spec = spec.and((root, query, cb) -> cb.equal(
                    root.get("email"), searchDto.email())
            );
        }

        if (searchDto.birthDateFrom() != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(
                    root.get("birthDate"), searchDto.birthDateFrom())
            );
        }

        if (searchDto.birthDateTo() != null) {
            spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(
                    root.get("birthDate"), searchDto.birthDateTo())
            );
        }

        if (StringUtils.hasText(searchDto.phoneNumber())){
            spec = spec.and((root, query, cb) -> cb.equal(
                    root.get("phoneNumber"), PhoneNumberUtils.normalizePhoneNumber(searchDto.phoneNumber(), "RU"))
            );
        }

        /*Sort sort = null;
        Sort.Direction direction = Sort.Direction.ASC;
        if (StringUtils.hasText(sortStr)){
            String[] tokens = sortStr.split(",", 2);

            if (tokens.length != 0) {
                if (tokens.length == 2) {
                    direction = tokens[1].equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
                }
                sort = Sort.by(new Sort.Order(direction, tokens[0]));
            }
        }

        if (sort == null){
            sort = Sort.by(new Sort.Order(Sort.Direction.ASC, "id"));
        }*/

        return userRepository.findAll(spec, pageable)
                .map(detailsModelMapper::toDto);
    }

    public UserDetailsDto createUserDetails(UserDetailsInputDto userDetailsDto) {
        User user = detailsModelMapper.toEntity(userDetailsDto);
        User savedUser = userRepository.save(user);
        return detailsModelMapper.toDto(savedUser);
    }

    private UserDetailsDto convertToUserDetailsDto(User user) {
        return modelMapper.map(user, UserDetailsDto.class);
    }

    public UserDetailsDto getUserDetailsById(Long id) {
        User user = getById(id);
        return detailsModelMapper.toDto(user);
    }

    public UserDetailsDto updateUserDetails(Long id, UserDetailsInputDto userDetailsDto) {
        User user = getById(id);
        modelMapper.map(userDetailsDto, user);

        User savedUser = userRepository.save(user);
        return detailsModelMapper.toDto(savedUser);
    }

    public void deleteUserDetails(Long id) {
        User user = this.getById(id);
        user.setLastName(null);
        user.setMiddleName(null);
        user.setBirthDate(null);
        userRepository.save(user);
    }
}
