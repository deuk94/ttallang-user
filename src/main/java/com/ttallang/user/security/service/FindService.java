package com.ttallang.user.security.service;

import net.nurigo.java_sdk.exceptions.CoolsmsException;

public interface FindService {
    boolean findUserNameByCustomerPhone(String customerPhone);
    String sendSms(String to) throws CoolsmsException;
}
