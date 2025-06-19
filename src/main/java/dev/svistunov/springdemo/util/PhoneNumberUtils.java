package dev.svistunov.springdemo.util;

import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber.PhoneNumber;

public class PhoneNumberUtils {

    // Преобразует номер телефона в международный формат
    public static String normalizePhoneNumber(String phoneNumber, String defaultRegion) {
        try {
            PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
            PhoneNumber numberProto = phoneUtil.parse(phoneNumber, defaultRegion);

            return phoneUtil.format(numberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (Exception e) {
            throw new IllegalArgumentException("Недопустимый номер телефона: " + phoneNumber);
        }
    }
}