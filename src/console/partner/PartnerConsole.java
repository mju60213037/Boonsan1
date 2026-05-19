package console.partner;

import enums.EvaluationGrade;
import model.partner.Partner;
import service.partner.PartnerService;

import java.util.List;

import static common.ConsoleUtil.*;

public class PartnerConsole {

    public static void run() {
        while (true) {
            line();
            System.out.println("[협력업체 관리]");
            System.out.println("1. 협력업체 조건 조회");
            System.out.println("2. 협력업체 등록");
            System.out.println("3. 협력업체 수정");
            System.out.println("4. 협력업체 검색");
            System.out.println("5. 협력업체 목록 조회");
            System.out.println("6. 사용 가능 협력업체 조회");
            System.out.println("7. 이전 메뉴");
            line();
            System.out.print(">> 선택: ");
            String choice = sc.nextLine().trim();

            switch (choice) {
                case "1":
                    searchPartnersByCondition();
                    break;
                case "2":
                    registerPartner();
                    break;
                case "3":
                    updatePartner();
                    break;
                case "4":
                    searchPartner();
                    break;
                case "5":
                    printPartnerList(PartnerService.getPartnerList(), true);
                    break;
                case "6":
                    printPartnerList(PartnerService.getAvailablePartnerList(), false);
                    break;
                case "7":
                    return;
                default:
                    System.out.println("[오류] 올바른 번호를 입력하세요.");
            }
        }
    }

    private static void searchPartnersByCondition() {
        System.out.println("\n[유스케이스] 협력업체를 관리한다 - 조건 조회");
        String partnerName = input("업체명 (Enter: 전체)");
        String partnerType = input("업체유형 (Enter: 전체)");
        EvaluationGrade evaluationGrade = selectEvaluationGradeForSearch();
        String availabilityStatus = selectAvailabilityStatus();

        List<Partner> partnerList = PartnerService.searchPartners(
                partnerName, partnerType, evaluationGrade, availabilityStatus);
        printPartnerList("\n[협력업체 조건 조회 결과]", partnerList);
    }

    private static void registerPartner() {
        System.out.println("\n[유스케이스] 협력업체를 관리한다 - 등록");
        String id = input("협력업체 ID (Enter: 자동 생성)");
        String partnerName = input("협력업체명");
        String partnerType = input("협력업체 유형");
        String contact = input("연락처");
        String responsibility = input("담당 업무");
        EvaluationGrade evaluationGrade = selectEvaluationGrade(true);

        Partner partner = PartnerService.registerPartner(id, partnerName, partnerType,
                contact, responsibility, evaluationGrade);
        if (partner == null) {
            System.out.println("[오류] 협력업체 등록에 실패했습니다. DB 연결 또는 입력값을 확인하세요.");
            return;
        }
        System.out.println("[시스템] 협력업체 등록 완료");
        printPartner(partner);
    }

    private static void updatePartner() {
        System.out.println("\n[협력업체 수정]");
        String id = input("수정할 협력업체 ID");
        Partner currentPartner = PartnerService.findPartnerById(id);
        if (currentPartner == null) {
            System.out.println("[오류] 해당 협력업체를 찾을 수 없습니다.");
            return;
        }

        System.out.println("[시스템] 현재 협력업체 정보");
        printPartner(currentPartner);
        String partnerName = input("새 협력업체명 (Enter: 유지)");
        String partnerType = input("새 협력업체 유형 (Enter: 유지)");
        String contact = input("새 연락처 (Enter: 유지)");
        String responsibility = input("새 담당 업무 (Enter: 유지)");
        EvaluationGrade evaluationGrade = selectEvaluationGrade(false);

        Partner updatedPartner = PartnerService.updatePartner(id, partnerName, partnerType,
                contact, responsibility, evaluationGrade);
        if (updatedPartner == null) {
            System.out.println("[오류] 협력업체 수정에 실패했습니다. DB 연결 또는 입력값을 확인하세요.");
            return;
        }
        System.out.println("[시스템] 협력업체 수정 완료");
        printPartner(updatedPartner);
    }

    private static void searchPartner() {
        System.out.println("\n[협력업체 검색]");
        String id = input("검색할 협력업체 ID");
        Partner partner = PartnerService.findPartnerById(id);
        if (partner == null) {
            System.out.println("[오류] 해당 협력업체를 찾을 수 없습니다.");
            return;
        }
        printPartner(partner);
    }

    private static void printPartnerList(List<Partner> partnerList, boolean includeSuspended) {
        printPartnerList(includeSuspended ? "\n[협력업체 목록 조회]" : "\n[사용 가능 협력업체 조회]",
                partnerList);
    }

    private static void printPartnerList(String title, List<Partner> partnerList) {
        System.out.println(title);
        if (partnerList.isEmpty()) {
            System.out.println("[시스템] 조회 가능한 협력업체가 없습니다.");
            return;
        }

        for (Partner partner : partnerList) {
            printPartner(partner);
        }
    }

    private static void printPartner(Partner partner) {
        String statusLabel = PartnerService.isAvailable(partner) ? "사용 가능" : "거래중지";
        System.out.println("  ID: " + partner.getId()
                + " | 업체명: " + partner.getPartnerName()
                + " | 유형: " + partner.getPartnerType()
                + " | 연락처: " + partner.getContact()
                + " | 담당업무: " + partner.getResponsibility()
                + " | 등급: " + partner.getEvaluationGrade()
                + " | 상태: " + statusLabel);
    }

    private static EvaluationGrade selectEvaluationGrade(boolean required) {
        while (true) {
            System.out.println("평가 등급: 1. EXCELLENT  2. GOOD  3. AVERAGE  4. POOR  5. SUSPENDED"
                    + (required ? "" : "  Enter. 유지"));
            System.out.print(">> 선택: ");
            String choice = sc.nextLine().trim();
            if (!required && choice.isEmpty()) {
                return null;
            }
            switch (choice) {
                case "1": return EvaluationGrade.EXCELLENT;
                case "2": return EvaluationGrade.GOOD;
                case "3": return EvaluationGrade.AVERAGE;
                case "4": return EvaluationGrade.POOR;
                case "5": return EvaluationGrade.SUSPENDED;
                default:
                    System.out.println("[오류] 올바른 번호를 입력하세요.");
            }
        }
    }

    private static EvaluationGrade selectEvaluationGradeForSearch() {
        while (true) {
            System.out.println("평가 등급: 1. EXCELLENT  2. GOOD  3. AVERAGE  4. POOR  5. SUSPENDED  Enter. 전체");
            System.out.print(">> 선택: ");
            String choice = sc.nextLine().trim();
            if (choice.isEmpty()) {
                return null;
            }
            switch (choice) {
                case "1": return EvaluationGrade.EXCELLENT;
                case "2": return EvaluationGrade.GOOD;
                case "3": return EvaluationGrade.AVERAGE;
                case "4": return EvaluationGrade.POOR;
                case "5": return EvaluationGrade.SUSPENDED;
                default:
                    System.out.println("[오류] 올바른 번호를 입력하세요.");
            }
        }
    }

    private static String selectAvailabilityStatus() {
        while (true) {
            System.out.println("사용상태: 1. 전체  2. 사용 가능  3. 거래중지  Enter. 전체");
            System.out.print(">> 선택: ");
            String choice = sc.nextLine().trim();
            if (choice.isEmpty() || "1".equals(choice)) {
                return "";
            }
            switch (choice) {
                case "2": return "AVAILABLE";
                case "3": return "SUSPENDED";
                default:
                    System.out.println("[오류] 올바른 번호를 입력하세요.");
            }
        }
    }
}
