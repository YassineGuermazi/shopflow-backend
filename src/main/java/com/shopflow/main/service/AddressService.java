package com.shopflow.main.service;

import com.shopflow.main.dto.address.*;
import com.shopflow.main.entity.Address;
import com.shopflow.main.entity.User;
import com.shopflow.main.exception.BusinessException;
import com.shopflow.main.exception.ResourceNotFoundException;
import com.shopflow.main.exception.UnauthorizedException;
import com.shopflow.main.repository.AddressRepository;
import com.shopflow.main.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserRepository userRepository;

    public List<AddressResponse> getMyAddresses() {
        User user = getCurrentUser();
        return addressRepository.findByUserId(user.getId())
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public AddressResponse addAddress(AddressRequest request) {
        User user = getCurrentUser();
        if (request.isPrincipal()) {
            addressRepository.clearPrincipal(user.getId());
        }
        Address address = Address.builder()
                .user(user)
                .rue(request.getRue())
                .ville(request.getVille())
                .codePostal(request.getCodePostal())
                .pays(request.getPays())
                .principal(request.isPrincipal())
                .build();
        return toResponse(addressRepository.save(address));
    }

    @Transactional
    public void deleteAddress(Long id) {
        Address address = addressRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Address not found: " + id));
        if (!address.getUser().getId().equals(getCurrentUser().getId())) {
            throw new UnauthorizedException("Not your address");
        }
        addressRepository.delete(address);
    }

    private AddressResponse toResponse(Address a) {
        return AddressResponse.builder()
                .id(a.getId())
                .rue(a.getRue())
                .ville(a.getVille())
                .codePostal(a.getCodePostal())
                .pays(a.getPays())
                .principal(a.isPrincipal())
                .build();
    }

    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new BusinessException("User not found"));
    }
}
