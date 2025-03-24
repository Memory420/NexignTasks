package com.memory.nexigntasks.Utils;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Конвертер для хранения CallType как 01/02 в БД.
 */
@Converter(autoApply = false)
public class CallTypeConverter implements AttributeConverter<CallType, String> {

    @Override
    public String convertToDatabaseColumn(CallType attribute) {
        if (attribute == null) {
            return null;
        }
        return attribute.getDbCode();
    }

    @Override
    public CallType convertToEntityAttribute(String dbData) {
        if (dbData == null) {
            return null;
        }
        return CallType.fromDbCode(dbData);
    }
}

