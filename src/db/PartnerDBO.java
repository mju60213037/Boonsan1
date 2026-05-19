package db;

import enums.EvaluationGrade;
import model.partner.Partner;

import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

// Partner 엔티티 DB 매핑 — partner 테이블 CRUD 담당
public class PartnerDBO extends DBA {

    public Partner findById(String partnerId) {
        String sql = "SELECT id, partner_name, partner_type, contact, responsibility, evaluation_grade "
                + "FROM partner WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, partnerId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return mapPartner(resultSet);
                }
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 협력업체 조회 실패: " + e.getMessage());
        }
        return null;
    }

    public List<Partner> findAll() {
        String sql = "SELECT id, partner_name, partner_type, contact, responsibility, evaluation_grade "
                + "FROM partner ORDER BY id";
        List<Partner> partnerList = new ArrayList<>();
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                partnerList.add(mapPartner(resultSet));
            }
        } catch (SQLException e) {
            System.out.println("[DB 오류] 협력업체 목록 조회 실패: " + e.getMessage());
        }
        return partnerList;
    }

    public boolean save(Partner partner) {
        if (partner == null) {
            return false;
        }
        String sql = "INSERT INTO partner "
                + "(id, partner_name, partner_type, contact, responsibility, evaluation_grade) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            setPartnerParams(statement, partner);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 협력업체 등록 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean update(Partner partner) {
        if (partner == null) {
            return false;
        }
        String sql = "UPDATE partner "
                + "SET partner_name = ?, partner_type = ?, contact = ?, responsibility = ?, evaluation_grade = ? "
                + "WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, partner.getPartnerName());
            statement.setString(2, partner.getPartnerType());
            statement.setString(3, partner.getContact());
            statement.setString(4, partner.getResponsibility());
            statement.setString(5, resolveEvaluationGradeName(partner));
            statement.setString(6, partner.getId());
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 협력업체 수정 실패: " + e.getMessage());
            return false;
        }
    }

    public boolean delete(String partnerId) {
        String sql = "DELETE FROM partner WHERE id = ?";
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, partnerId);
            return statement.executeUpdate() > 0;
        } catch (SQLException e) {
            System.out.println("[DB 오류] 협력업체 삭제 실패: " + e.getMessage());
            return false;
        }
    }

    private Partner mapPartner(ResultSet resultSet) throws SQLException {
        return new Partner(
                resultSet.getString("id"),
                resultSet.getString("partner_name"),
                resultSet.getString("partner_type"),
                resultSet.getString("contact"),
                resultSet.getString("responsibility"),
                resolveEvaluationGrade(resultSet.getString("evaluation_grade"))
        );
    }

    private void setPartnerParams(PreparedStatement statement, Partner partner) throws SQLException {
        statement.setString(1, partner.getId());
        statement.setString(2, partner.getPartnerName());
        statement.setString(3, partner.getPartnerType());
        statement.setString(4, partner.getContact());
        statement.setString(5, partner.getResponsibility());
        statement.setString(6, resolveEvaluationGradeName(partner));
    }

    private String resolveEvaluationGradeName(Partner partner) {
        if (partner.getEvaluationGrade() == null) {
            return EvaluationGrade.AVERAGE.name();
        }
        return partner.getEvaluationGrade().name();
    }

    private EvaluationGrade resolveEvaluationGrade(String value) {
        if (value == null || value.trim().isEmpty()) {
            return EvaluationGrade.AVERAGE;
        }
        try {
            return EvaluationGrade.valueOf(value.trim());
        } catch (IllegalArgumentException e) {
            return EvaluationGrade.AVERAGE;
        }
    }
}
