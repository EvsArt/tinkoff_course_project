package edu.java.service;

import java.util.List;
import java.util.stream.Stream;
import lombok.experimental.UtilityClass;

@UtilityClass
@SuppressWarnings("MultipleStringLiterals")
public class SqlQueries {

    public static final String TG_CHAT_TABLE_NAME = "tg_chat";
    public static final String LINK_TABLE_NAME = "link";
    public static final String LINK_TG_CHAT_TABLE_NAME = "link_tg_chat";

    public static final String TG_CHAT_FIELD_ID_NAME = "id";
    public static final String TG_CHAT_FIELD_CHAT_ID_NAME = "chat_id";
    public static final String TG_CHAT_FIELD_NAME_NAME = "name";
    public static final List<String> TG_CHAT_FIELDS_NAMES_WITHOUT_ID = List.of(
        TG_CHAT_FIELD_CHAT_ID_NAME,
        TG_CHAT_FIELD_NAME_NAME
    );
    public static final List<String> TG_CHAT_FIELDS_NAMES = Stream.concat(
        TG_CHAT_FIELDS_NAMES_WITHOUT_ID.stream(),
        Stream.of(TG_CHAT_FIELD_ID_NAME)
    ).toList();
    public static final String LINK_FIELD_ID_NAME = "id";
    public static final String LINK_FIELD_URL_NAME = "url";
    public static final String LINK_FIELD_NAME_NAME = "name";
    public static final String LINK_FIELD_CREATED_AT_NAME = "created_at";
    public static final String LINK_FIELD_LAST_UPDATE_TIME_NAME = "last_update_time";
    public static final String LINK_FIELD_LAST_CHECK_TIME_NAME = "last_check_time";
    public static final List<String> LINK_FIELDS_NAMES_WITHOUT_ID = List.of(
        LINK_FIELD_URL_NAME,
        LINK_FIELD_NAME_NAME,
        LINK_FIELD_CREATED_AT_NAME,
        LINK_FIELD_LAST_UPDATE_TIME_NAME,
        LINK_FIELD_LAST_CHECK_TIME_NAME
    );
    public static final List<String> LINK_FIELDS_NAMES = Stream.concat(
        LINK_FIELDS_NAMES_WITHOUT_ID.stream(),
        Stream.of(LINK_FIELD_ID_NAME)
    ).toList();

    public static final String LINK_TG_CHAT_FIELD_TG_CHAT_NAME = "tg_chat_id";
    public static final String LINK_TG_CHAT_FIELD_LINK_NAME = "link_id";

    /**
     * Creates sql query for select * like
     * 'select * from user'
     *
     * @param tableName name of table where you want to find rows
     * @return created sql query
     */
    public static String findAllQuery(String tableName) {
        validTableName(tableName);
        return "select * from %s".formatted(tableName);
    }

    /**
     * Creates sql query for select like
     * 'select * from user where id = :id'
     * :id is parameter name for JDBCClient
     *
     * @param tableName          name of table where you want to find a row
     * @param conditionFieldName field with where condition
     * @return created sql query
     */
    public static String findWhereQuery(String tableName, String conditionFieldName) {
        validTableName(tableName);
        validFieldName(conditionFieldName);
        return "select * from %s where %s = :%s".formatted(tableName, conditionFieldName, conditionFieldName);
    }

    /**
     * Creates sql query for select like
     * 'select * from user where created_at < :created_at'
     * :created_at is parameter name for JDBCClient
     *
     * @param tableName          name of table where you want to find a row
     * @param conditionFieldName field with where condition
     * @return created sql query
     */
    public static String findWhereBeforeQuery(String tableName, String conditionFieldName) {
        validTableName(tableName);
        validFieldName(conditionFieldName);
        return "select * from %s where %s < :%s".formatted(tableName, conditionFieldName, conditionFieldName);
    }

    /**
     * Creates sql query for select like
     * 'select name from user where id = :id'
     * :id is parameter name for JDBCClient
     *
     * @param tableName          name of table where you want to find a row
     * @param fieldName          name of field you want to find
     * @param conditionFieldName field with where condition
     * @return created sql query
     */
    public static String findFieldWhereQuery(String tableName, String fieldName, String conditionFieldName) {
        validTableName(tableName);
        validFieldName(fieldName);
        validFieldName(conditionFieldName);
        return "select %s from %s where %s = :%s".formatted(
            fieldName,
            tableName,
            conditionFieldName,
            conditionFieldName
        );
    }

    /**
     * Creates sql query for insert like
     * 'insert into user (id, name) values (:id, :name)'
     * :id and :name is parameters names for JDBCClient
     *
     * @param tableName   name of table where you want to insert value
     * @param fieldsNames list of fields you want to insert
     * @return created sql query
     */
    public static String insertQuery(String tableName, List<String> fieldsNames) {
        validTableName(tableName);
        validFieldsList(fieldsNames);
        return "insert into %s (%s) values (%s)".formatted(
            tableName,
            String.join(", ", fieldsNames),
            String.join(", ", fieldsNames.stream().map(":%s"::formatted).toList())
        );
    }

    /**
     * Creates sql query for update like
     * 'update user set name = :name where id = :id'
     * :id and :name is parameters names for JDBCClient
     *
     * @param tableName   name of table where you want to update value
     * @param fieldsNames list of fields you want to update
     * @return created sql query
     */
    public static String updateQuery(String tableName, List<String> fieldsNames, String conditionFieldName) {
        validTableName(tableName);
        validFieldsList(fieldsNames);
        validFieldName(conditionFieldName);
        return "update %s set %s where %s".formatted(
            tableName,
            String.join(", ", fieldsNames.stream().map(it -> "%s = :%s".formatted(it, it)).toList()),
            "%s = %s".formatted(conditionFieldName, ":" + conditionFieldName)
        );
    }

    /**
     * Creates sql query for delete like
     * 'delete from user where id = :id and name = :name'
     * :id is parameter name for JDBCClient
     *
     * @param tableName            name of table where you want to delete a row
     * @param conditionFieldsNames list of fields with where condition
     * @return created sql query
     */
    public static String deleteQuery(String tableName, List<String> conditionFieldsNames) {
        validTableName(tableName);
        validFieldsList(conditionFieldsNames);
        return "delete from %s where %s".formatted(
            tableName,
            String.join(
                " and ",
                conditionFieldsNames.stream().map(field -> "%s = :%s".formatted(field, field)).toList()
            )
        );
    }

    public static String deleteQuery(String tableName, String conditionField) {
        validFieldName(conditionField);
        return deleteQuery(tableName, List.of(conditionField));
    }

    private void validTableName(String tableName) {
        if (tableName == null
            || tableName.isBlank()
            || tableName.split(" ").length != 1
        ) {
            throw new IllegalArgumentException("%s is not a valid table name".formatted(tableName));
        }
    }

    private void validFieldName(String fieldName) {
        if (fieldName == null
            || fieldName.isBlank()
            || fieldName.split(" ").length != 1
        ) {
            throw new IllegalArgumentException("%s is not a valid field name".formatted(fieldName));
        }
    }

    private void validFieldsList(List<String> fieldsList) {
        if (fieldsList == null
            || fieldsList.isEmpty()
        ) {
            throw new IllegalArgumentException("%s is not a valid fields list".formatted(fieldsList));
        }
        fieldsList.forEach(SqlQueries::validFieldName);
    }

}
