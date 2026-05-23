package common.utils;

import model.account.Account;

public class AuthContext {
    private static Account currentUser;

    public static void login(Account account) {
        currentUser = account;
    }

    public static void logout() {
        currentUser = null;
    }

    public static Account getCurrentUser() {
        return currentUser;
    }

    public static boolean isAdmin() {
        return business.service.AuthorizationService.isAdmin(currentUser);
    }
}
