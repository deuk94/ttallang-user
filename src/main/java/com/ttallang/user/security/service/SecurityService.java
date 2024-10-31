package com.ttallang.user.security.service;

import java.util.Map;

public interface SecurityService {
    void signupCustomer(Map<String, String> userData);
    boolean isExistingCustomer(String userName);
}
