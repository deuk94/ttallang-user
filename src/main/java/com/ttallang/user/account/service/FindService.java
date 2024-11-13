package com.ttallang.user.account.service;

import com.ttallang.user.account.model.RolesUser;

public interface FindService {
    boolean findUserNameByCustomerPhone(String customerPhone);
    RolesUser getUserNameByCustomerPhone(String customerPhone);
    int sendSMS(String to);
}
