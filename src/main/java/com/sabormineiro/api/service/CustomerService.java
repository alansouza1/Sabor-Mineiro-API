package com.sabormineiro.api.service;

import com.sabormineiro.api.dto.CustomerDTO;
import com.sabormineiro.api.entity.Address;
import com.sabormineiro.api.entity.CEP;
import com.sabormineiro.api.entity.Client;
import com.sabormineiro.api.entity.User;
import com.sabormineiro.api.exception.ResourceNotFoundException;
import com.sabormineiro.api.repository.AddressRepository;
import com.sabormineiro.api.repository.CEPRepository;
import com.sabormineiro.api.repository.ClientRepository;
import com.sabormineiro.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomerService {

    private final ClientRepository clientRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final CEPRepository cepRepository;

    @Transactional
    public Client resolveClient(Long clientId, CustomerDTO customerDTO) {
        if (clientId != null) {
            return clientRepository.findById(clientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Client not found with ID: " + clientId));
        }

        if (customerDTO == null) {
            throw new IllegalArgumentException("Customer details are required for guest checkout");
        }

        User systemUser = findDefaultSystemUser();
        String normalizedPhone = customerDTO.getPhone().replaceAll("\\D", "");

        return clientRepository.findByUser(systemUser).orElseGet(() -> {
            log.info("Creating new client profile for system user: {}", systemUser.getEmail());
            return clientRepository.save(Client.builder()
                    .phone(normalizedPhone)
                    .cpf(normalizedPhone.length() >= 11 ? normalizedPhone.substring(0, 11) : normalizedPhone)
                    .user(systemUser)
                    .build());
        });
    }

    @Transactional
    public Address resolveAddress(Long addressId, Client client, CustomerDTO customerDTO) {
        if (addressId != null) {
            Address address = addressRepository.findById(addressId)
                    .orElseThrow(() -> new ResourceNotFoundException("Address not found with ID: " + addressId));
            
            if (!address.getClient().getId().equals(client.getId())) {
                throw new IllegalArgumentException("Address does not belong to the provided client");
            }
            return address;
        }

        CEP defaultCep = cepRepository.findByCep("00000000").orElseGet(() -> 
            cepRepository.save(CEP.builder()
                    .cep("00000000")
                    .city("Belo Horizonte")
                    .state("MG")
                    .build())
        );

        log.debug("Creating temporary address for order of client: {}", client.getId());
        return addressRepository.save(Address.builder()
                .street(customerDTO.getAddress())
                .number("S/N")
                .neighborhood("Center")
                .isDefault(false)
                .client(client)
                .cep(defaultCep)
                .build());
    }

    private User findDefaultSystemUser() {
        return userRepository.findByEmail("admin@sabormineiro.com")
                .orElseGet(() -> userRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Critical error: No system users available")));
    }
}
