/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ro.micsa.exchange.utils;

import ro.micsa.exchange.dto.ExchangeRate;
import ro.micsa.exchange.dto.Notification;
import ro.micsa.exchange.model.EmailAlertEntity;
import ro.micsa.exchange.model.ExchangeRateEntity;
import ro.micsa.exchange.model.NotificationEntity;
import ro.micsa.exchange.model.UserEntity;

/**
 *
 * @author georgian
 */
public final class Converter {

    public static final Notification notificationToDTO(NotificationEntity entity) {
        Notification notificationDTO = new Notification();
        notificationDTO.setBankName(entity.getBankName());
        notificationDTO.setComparator(entity.getComparator());
        notificationDTO.setCurrencyType(entity.getCurrencyType());
        notificationDTO.setEnabled(entity.isEnabled());
        notificationDTO.setLanguage(entity.getLanguage());
        notificationDTO.setThreshold(entity.getThreshold());
        notificationDTO.setTransactionType(entity.getTransactionType());
        return notificationDTO;
    }
    
    public static final NotificationEntity notificationToEntity(Notification notificationDTO) {
        NotificationEntity entity = new NotificationEntity();
        entity.setBankName(notificationDTO.getBankName());
        entity.setComparator(notificationDTO.getComparator());
        entity.setCurrencyType(notificationDTO.getCurrencyType());
        entity.setEnabled(notificationDTO.isEnabled());
        entity.setLanguage(notificationDTO.getLanguage());
        entity.setThreshold(notificationDTO.getThreshold());
        entity.setTransactionType(notificationDTO.getTransactionType());
        return entity;
    }
    
    public static final ExchangeRate toDTO(ExchangeRateEntity entity) {
        ExchangeRate dto = new ExchangeRate();
        dto.setBankName(entity.getBankName());
        dto.setCurrencyType(entity.getCurrencyType());
        dto.setDate(entity.getDate());
        dto.setEvolution(entity.getEvolution());
        dto.setLastChangedAt(entity.getLastChangedAt());
        dto.setTransactionType(entity.getTransactionType());
        dto.setValue(entity.getValue());
        return dto;
    }

    public static final ExchangeRateEntity toEntity(ExchangeRate dto) {
        ExchangeRateEntity entity = new ExchangeRateEntity();
        entity.setBankName(dto.getBankName());
        entity.setCurrencyType(dto.getCurrencyType());
        entity.setDate(dto.getDate());
        entity.setEvolution(dto.getEvolution());
        entity.setLastChangedAt(dto.getLastChangedAt());
        entity.setTransactionType(dto.getTransactionType());
        entity.setValue(dto.getValue());
        return entity;
    }

    public static EmailAlertEntity createEmailAlertEntity(UserEntity user, NotificationEntity notification, ExchangeRateEntity matchingRate) {
        EmailAlertEntity alert = new EmailAlertEntity();
        alert.setBankName(notification.getBankName());
        alert.setCurrencyType(notification.getCurrencyType());
        alert.setEmail(user.getEmail());
        alert.setEvolution(matchingRate.getEvolution());
        alert.setFbUserName(user.getFbUserName());
        alert.setLanguage(notification.getLanguage());
        alert.setStatus(EmailAlertEntity.EmailAlertStatus.WAITING);
        alert.setThreshold(notification.getThreshold());
        alert.setTransactionType(notification.getTransactionType());
        alert.setValue(matchingRate.getValue());
        return alert;
    }
}
