package com.ttallang.user.security.service;

import com.ttallang.user.security.model.RolesUser;
import net.nurigo.java_sdk.exceptions.CoolsmsException;

public interface FindService {
    boolean findUserNameByCustomerPhone(String customerPhone);
    RolesUser getUserNameByCustomerPhone(String customerPhone);
    boolean sendSms(String to) throws CoolsmsException;
}
