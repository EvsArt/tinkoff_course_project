package edu.java.service;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.catchThrowable;

class SqlQueriesTest {

    @Test
    void findAllQuery() {
        String tableName = "myTable";
        String expQuery = "select * from %s".formatted(tableName);

        String resQuery = SqlQueries.findAllQuery(tableName);

        assertThat(resQuery).isEqualTo(expQuery);
    }

    @Test
    void findAllQueryWithWrongArgument() {
        String tableName = "";

        Throwable result = catchThrowable(() -> SqlQueries.findAllQuery(tableName));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findAllQueryWithNullArgument() {
        String tableName = null;

        Throwable result = catchThrowable(() -> SqlQueries.findAllQuery(tableName));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findAllQueryWithNotOneWordArgument() {
        String tableName = "user; drop table user";

        Throwable result = catchThrowable(() -> SqlQueries.findAllQuery(tableName));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findWhereQuery() {
        String tableName = "myTable";
        String fieldName = "myField";
        String expQuery = "select * from %s where %s = :%s".formatted(tableName, fieldName, fieldName);

        String resQuery = SqlQueries.findWhereQuery(tableName, fieldName);

        assertThat(resQuery).isEqualTo(expQuery);
    }

    @Test
    void findWhereQueryWithWrongTableNameArgument() {
        String tableName = "";
        String fieldName = "myField";

        Throwable result = catchThrowable(() -> SqlQueries.findWhereQuery(tableName, fieldName));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findWhereQueryWithWrongFieldNameArgument() {
        String tableName = "myTable";
        String fieldName = "";

        Throwable result = catchThrowable(() -> SqlQueries.findWhereQuery(tableName, fieldName));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findWhereQueryWithNullTableNameArgument() {
        String tableName = null;
        String fieldName = "myField";

        Throwable result = catchThrowable(() -> SqlQueries.findWhereQuery(tableName, fieldName));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findWhereQueryWithNullFieldNameArgument() {
        String tableName = "myField";
        String fieldName = null;

        Throwable result = catchThrowable(() -> SqlQueries.findWhereQuery(tableName, fieldName));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findWhereQueryWithNotOneWordTableNameArgument() {
        String tableName = "user; drop table user";
        String fieldName = "myField";

        Throwable result = catchThrowable(() -> SqlQueries.findWhereQuery(tableName, fieldName));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void findWhereQueryWithNotOneWordFieldNameArgument() {
        String tableName = "myTable";
        String fieldName = "user; drop table user";

        Throwable result = catchThrowable(() -> SqlQueries.findWhereQuery(tableName, fieldName));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void insertQuery() {
        String tableName = "myTable";
        List<String> fieldsNames = List.of("myField1", "myField2");

        String fieldsList = String.join(", ", fieldsNames);
        String parametersList = String.join(", ", fieldsNames.stream().map(it -> ":" + it).toList());

        String expQuery = "insert into %s (%s) values (%s)".formatted(tableName, fieldsList, parametersList);

        String resQuery = SqlQueries.insertQuery(tableName, fieldsNames);

        assertThat(resQuery).isEqualTo(expQuery);
    }

    @Test
    void insertQueryWithWrongTableNameArgument() {
        String tableName = "";
        List<String> fieldsNames = List.of("myField1", "myField2");

        Throwable result = catchThrowable(() -> SqlQueries.insertQuery(tableName, fieldsNames));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void insertQueryWithNullTableNameArgument() {
        String tableName = null;
        List<String> fieldsNames = List.of("myField1", "myField2");

        Throwable result = catchThrowable(() -> SqlQueries.insertQuery(tableName, fieldsNames));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void insertQueryWithNotOneWordTableNameArgument() {
        String tableName = "myTable; drop table myTable";
        List<String> fieldsNames = List.of("myField1", "myField2");

        Throwable result = catchThrowable(() -> SqlQueries.insertQuery(tableName, fieldsNames));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void insertQueryWithEmptyFieldsNamesArgument() {
        String tableName = "myTable";
        List<String> fieldsNames = List.of();

        Throwable result = catchThrowable(() -> SqlQueries.insertQuery(tableName, fieldsNames));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void insertQueryWithNullFieldsNamesArgument() {
        String tableName = "myTable";
        List<String> fieldsNames = null;

        Throwable result = catchThrowable(() -> SqlQueries.insertQuery(tableName, fieldsNames));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void insertQueryWithWrongFieldInFieldsNamesArgument() {
        String tableName = "myTable";
        List<String> fieldsNames = List.of("myField", "myField; drop table myTable");

        Throwable result = catchThrowable(() -> SqlQueries.insertQuery(tableName, fieldsNames));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateQuery() {
        String tableName = "myTable";
        List<String> fieldsNames = List.of("myField1", "myField2");
        String conditionField = "id";

        // myField1 = :myField1, myField2 = :myField2
        String fieldsSetList = String.join(", ", fieldsNames.stream()
            .map(field -> "%s = :%s".formatted(field, field))
            .toList()
        );
        // id = :id
        String condition = "%s = :%s".formatted(conditionField, conditionField);

        String expQuery = "update %s set %s where %s".formatted(tableName, fieldsSetList, condition);

        String resQuery = SqlQueries.updateQuery(tableName, fieldsNames, conditionField);

        assertThat(resQuery).isEqualTo(expQuery);
    }

    @Test
    void updateQueryWithWrongTableNameArgument() {
        String tableName = "";
        List<String> fieldsNames = List.of("myField1", "myField2");
        String conditionField = "id";

        Throwable result = catchThrowable(() -> SqlQueries.updateQuery(tableName, fieldsNames, conditionField));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateQueryWithNullTableNameArgument() {
        String tableName = null;
        List<String> fieldsNames = List.of("myField1", "myField2");
        String conditionField = "id";

        Throwable result = catchThrowable(() -> SqlQueries.updateQuery(tableName, fieldsNames, conditionField));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateQueryWithNotOneWordTableNameArgument() {
        String tableName = "myTable; drop table myTable";
        List<String> fieldsNames = List.of("myField1", "myField2");
        String conditionField = "id";

        Throwable result = catchThrowable(() -> SqlQueries.updateQuery(tableName, fieldsNames, conditionField));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateQueryWithEmptyFieldsNamesArgument() {
        String tableName = "myTable";
        List<String> fieldsNames = List.of();
        String conditionField = "id";

        Throwable result = catchThrowable(() -> SqlQueries.updateQuery(tableName, fieldsNames, conditionField));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateQueryWithNullFieldsNamesArgument() {
        String tableName = "myTable";
        List<String> fieldsNames = null;
        String conditionField = "id";

        Throwable result = catchThrowable(() -> SqlQueries.updateQuery(tableName, fieldsNames, conditionField));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateQueryWithWrongFieldInFieldsNamesArgument() {
        String tableName = "myTable";
        List<String> fieldsNames = List.of("myField", "myField; drop table myTable");
        String conditionField = "id";

        Throwable result = catchThrowable(() -> SqlQueries.updateQuery(tableName, fieldsNames, conditionField));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateQueryWithWrongConditionFieldArgument() {
        String tableName = "myTable";
        List<String> fieldsNames = List.of("myField", "myField2");
        String conditionField = "";

        Throwable result = catchThrowable(() -> SqlQueries.updateQuery(tableName, fieldsNames, conditionField));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateQueryWithNullConditionFieldArgument() {
        String tableName = "myTable";
        List<String> fieldsNames = List.of("myField", "myField2");
        String conditionField = null;

        Throwable result = catchThrowable(() -> SqlQueries.updateQuery(tableName, fieldsNames, conditionField));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void updateQueryWithNotOneWordConditionFieldArgument() {
        String tableName = "myTable";
        List<String> fieldsNames = List.of("myField", "myField2");
        String conditionField = "id; drop table myTable";

        Throwable result = catchThrowable(() -> SqlQueries.updateQuery(tableName, fieldsNames, conditionField));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteQuery() {
        String tableName = "myTable";
        String conditionFieldName = "id";

        // id = :id
        String condition = "%s = :%s".formatted(conditionFieldName, conditionFieldName);

        String expQuery = "delete from %s where %s".formatted(tableName, condition);

        String resQuery = SqlQueries.deleteQuery(tableName, conditionFieldName);

        assertThat(resQuery).isEqualTo(expQuery);
    }

    @Test
    void deleteQueryWithWrongTableNameArgument() {
        String tableName = "";
        String conditionFieldName = "id";

        Throwable result = catchThrowable(() -> SqlQueries.deleteQuery(tableName, conditionFieldName));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteQueryWithWrongConditionFieldNameArgument() {
        String tableName = "myTable";
        String conditionFieldName = "";

        Throwable result = catchThrowable(() -> SqlQueries.deleteQuery(tableName, conditionFieldName));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteQueryWithNullTableNameArgument() {
        String tableName = null;
        String conditionFieldName = "id";

        Throwable result = catchThrowable(() -> SqlQueries.deleteQuery(tableName, conditionFieldName));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteQueryWithNullConditionFieldNameArgument() {
        String tableName = "myField";
        String conditionFieldName = null;

        Throwable result = catchThrowable(() -> SqlQueries.deleteQuery(tableName, conditionFieldName));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteQueryWithNotOneWordTableNameArgument() {
        String tableName = "user; drop table user";
        String conditionFieldName = "id";

        Throwable result = catchThrowable(() -> SqlQueries.deleteQuery(tableName, conditionFieldName));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void deleteQueryWithNotOneWordConditionFieldNameArgument() {
        String tableName = "myTable";
        String conditionFieldName = "user; drop table user";

        Throwable result = catchThrowable(() -> SqlQueries.findWhereQuery(tableName, conditionFieldName));

        assertThat(result).isInstanceOf(IllegalArgumentException.class);
    }

}
