package db;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

// DB 접근 기반 클래스 — DB 연결 및 SQL 실행의 모든 공통 로직 담당
public class DBA {

    private static final String DB_PROPERTIES_FILE = "db.properties";
    private static final String DB_URL_KEY = "db.url";
    private static final String DB_USER_KEY = "db.user";
    private static final String DB_PASSWORD_KEY = "db.password";

    protected Connection getConnection() throws SQLException {
        Properties properties = loadDbProperties();
        String url = requireProperty(properties, DB_URL_KEY);
        String user = requireProperty(properties, DB_USER_KEY);
        String password = requireProperty(properties, DB_PASSWORD_KEY);
        return DriverManager.getConnection(url, user, password);
    }

    // DB 연결
    protected void connect() {
        System.out.println("[DB] 연결 중... (" + DB_PROPERTIES_FILE + ")");
    }

    // DB 연결 종료
    protected void disconnect() {
        System.out.println("[DB] 연결 종료.");
    }

    // DB 로그인
    protected void login(String user, String password) {
        System.out.println("[DB] 로그인: " + user);
    }

    // SELECT 실행
    protected void executeSelect(String sql) {
        System.out.println("[DB] SELECT: " + sql);
    }

    // INSERT 실행
    protected void executeInsert(String sql) {
        System.out.println("[DB] INSERT: " + sql);
    }

    // UPDATE 실행
    protected void executeUpdate(String sql) {
        System.out.println("[DB] UPDATE: " + sql);
    }

    // DELETE 실행
    protected void executeDelete(String sql) {
        System.out.println("[DB] DELETE: " + sql);
    }

    private Properties loadDbProperties() throws SQLException {
        Properties properties = new Properties();
        try (FileInputStream inputStream = new FileInputStream(DB_PROPERTIES_FILE);
             InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            properties.load(reader);
            return properties;
        } catch (IOException e) {
            throw new SQLException(DB_PROPERTIES_FILE + " 파일을 읽을 수 없습니다.", e);
        }
    }

    private String requireProperty(Properties properties, String key) throws SQLException {
        String value = properties.getProperty(key);
        if (value == null) {
            value = properties.getProperty("\uFEFF" + key);
        }
        if (value == null || value.trim().isEmpty()) {
            throw new SQLException(DB_PROPERTIES_FILE + "에 " + key + " 값이 없습니다.");
        }
        return value.trim();
    }
}
