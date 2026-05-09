package com.bandhub.zsi.fan;

import com.bandhub.zsi.fan.dto.FanRegistrationRequest;
import com.bandhub.zsi.fan.dto.FanRegistrationResponse;
import com.bandhub.zsi.user.UserAdminService;
import com.bandhub.zsi.user.dto.CreateUserRequest;
import com.bandhub.zsi.user.dto.ResetPasswordRequest;
import org.springframework.stereotype.Service;

@Service
public class FanRegistrationService {

    private static final String FAN_ROLE = "FAN";

    private final UserAdminService userAdminService;

    public FanRegistrationService(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    public FanRegistrationResponse register(FanRegistrationRequest request) {
        // 1. Tworzymy uzytkownika w Keycloaku przez istniejacy serwis admin (re-use).
        //    Hasło zostanie ustawione w kroku 2 jako non-temporary, zeby fan mogl zalogowac sie od razu.
        CreateUserRequest createUserRequest = new CreateUserRequest(
                request.username(),
                null,
                request.firstName(),
                request.lastName(),
                request.email(),
                true
        );
        String userId = userAdminService.createUser(createUserRequest);

        // 2. Ustawienie hasla jako non-temporary -> brak required action UPDATE_PASSWORD przy logowaniu.
        userAdminService.resetPassword(userId, new ResetPasswordRequest(request.password(), false));

        // 3. Przypisujemy rolę FAN, zeby JWT mial ROLE_FAN przy autoryzacji.
        try {
            userAdminService.assignRole(userId, FAN_ROLE);
        } catch (RuntimeException ex) {
            // Brak roli FAN w realmie nie powinien blokowac rejestracji - fan zaloguje sie bez roli.
        }

        return new FanRegistrationResponse(userId, request.username());
    }
}
