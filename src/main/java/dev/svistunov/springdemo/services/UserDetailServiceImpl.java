package dev.svistunov.springdemo.services;

import dev.svistunov.springdemo.dto.request.UserDetailsInputDto;
import dev.svistunov.springdemo.dto.request.UserDetailsSearchDto;
import dev.svistunov.springdemo.dto.response.UserDetailsDto;
import dev.svistunov.springdemo.exception.UserNotFoundException;
import dev.svistunov.springdemo.model.User;
import dev.svistunov.springdemo.repository.UserRepository;
import dev.svistunov.springdemo.services.interfaces.UserDetailService;
import dev.svistunov.springdemo.services.interfaces.UserDetailsMapper;
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
public class UserDetailServiceImpl implements UserDetailService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final UserDetailsMapper detailsModelMapper;

    UserDetailServiceImpl(UserRepository userRepository, ModelMapper modelMapper,
                          UserDetailsMapper detailsModelMapper, Environment env) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.detailsModelMapper = detailsModelMapper;
    }

    // Работа с детальной информацией
    @Override
    public List<UserDetailsDto> getAllUserDetails() {
        return userRepository.findAll()
                .stream()
                .map(detailsModelMapper::toDto)
                .toList();
    }

    @Override
    public User getById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    /**
     * Возвращает список детальной информации о пользователе с фильтрацией, пагинацией и сортировкой
     * @param searchDto
     * @param pageable
     * @return
     */
    @Override
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

        return userRepository.findAll(spec, pageable)
                .map(detailsModelMapper::toDto);
    }

    @Override
    public UserDetailsDto createUserDetails(UserDetailsInputDto userDetailsDto) {
        User user = detailsModelMapper.toEntity(userDetailsDto);
        User savedUser = userRepository.save(user);
        return detailsModelMapper.toDto(savedUser);
    }

    private UserDetailsDto convertToUserDetailsDto(User user) {
        return modelMapper.map(user, UserDetailsDto.class);
    }

    @Override
    public UserDetailsDto getUserDetailsById(Long id) {
        User user = getById(id);
        return detailsModelMapper.toDto(user);
    }

    @Override
    public UserDetailsDto updateUserDetails(Long id, UserDetailsInputDto userDetailsDto) {
        User user = getById(id);
        modelMapper.map(userDetailsDto, user);

        User savedUser = userRepository.save(user);
        return detailsModelMapper.toDto(savedUser);
    }

    @Override
    public void deleteUserDetails(Long id) {
        User user = this.getById(id);
        user.setLastName(null);
        user.setMiddleName(null);
        user.setBirthDate(null);
        userRepository.save(user);
    }
}
