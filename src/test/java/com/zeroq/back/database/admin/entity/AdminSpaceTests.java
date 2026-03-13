package com.zeroq.back.database.admin.entity;

import jakarta.persistence.Column;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.assertj.core.api.Assertions.assertThat;

class AdminSpaceTests {

    @Test
    void averageRating_isMappedAsNotNullWithDefaultZero() throws NoSuchFieldException {
        Field averageRatingField = AdminSpace.class.getDeclaredField("averageRating");
        Column column = averageRatingField.getAnnotation(Column.class);

        assertThat(column).isNotNull();
        assertThat(column.nullable()).isFalse();
        assertThat(AdminSpace.builder().build().getAverageRating()).isEqualTo(0.0);
    }
}
