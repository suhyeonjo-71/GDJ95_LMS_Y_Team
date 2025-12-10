package com.example.lms.controller.excel;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.example.lms.dto.SysAuthDTO;
import com.example.lms.dto.SysUserExcelDTO;
import com.example.lms.service.excel.UserManagementExcelService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

/**
*
* 2025. 12. 01.
* Author - jm
* 내정보 - 사용자관리 - 엑셀 기능
*/
@Slf4j
@Controller
@RequestMapping ("/admin") // 공통 경로 매핑
public class UserManagementExcelController {
	
	@Autowired
	private UserManagementExcelService userManagementExcelService;
	
    @Value("${excel.file.upload-dir3}")
    private String uploadDir;
    
    /**
     * 사용자 권한 코드 등록용 엑셀 양식 파일을 다운로드합니다.
     * - 서버에 미리 저장된 '사용자권한코드등록양식.xlsx' 파일을 읽어와 다운로드합니다.
     */
    @GetMapping("/downloadSysUserForm")
    public void downloadSysUserForm(HttpServletResponse response) throws IOException {

        final String FORM_RESOURCE_PATH = "/excel/사용자등록일괄등록양식.xlsx"; // resources 하위 경로
        final String FORM_FILE_NAME = "사용자등록일괄등록양식.xlsx";

        // ⭐️ 1. ClassPathResource를 사용하여 리소스 객체 생성 ⭐️
        Resource resource = new ClassPathResource(FORM_RESOURCE_PATH);

        if (!resource.exists()) {
            log.error("엑셀 양식 리소스 파일이 JAR 내부에 존재하지 않습니다: {}", FORM_RESOURCE_PATH);
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "요청된 엑셀 양식 파일을 찾을 수 없습니다.");
            return;
        }

        // 2. HTTP 응답 설정 (파일명 인코딩)
        String encodedFileName = URLEncoder.encode(FORM_FILE_NAME, StandardCharsets.UTF_8);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + encodedFileName + ";filename*=UTF-8''" + encodedFileName);

        // 파일 크기 설정 (선택 사항)
        try {
            response.setContentLength((int) resource.contentLength());
        } catch (IOException e) {
            log.warn("파일 크기 설정 실패: {}", e.getMessage());
        }

        // ⭐️ 3. 리소스의 InputStream을 응답 스트림에 복사 ⭐️
        try (InputStream is = resource.getInputStream();
             OutputStream os = response.getOutputStream()) {

            // Spring에서 제공하는 유틸리티 메서드로 스트림 복사를 간결하게 처리
            FileCopyUtils.copy(is, os);
            os.flush();
        }
        // try-with-resources 덕분에 스트림은 자동으로 닫힙니다.
    }
    
    
    @PostMapping("/uploadSysUserBatch") 
    @ResponseBody
    // 반환 타입 및 파라미터는 그대로 유지
    public ResponseEntity<Map<String, Object>> uploadSysUserBatch(@RequestParam("excelFile") MultipartFile file) {

        if (file.isEmpty()) {
        	
            return ResponseEntity.ok(Map.of("status", "fail", "message", "업로드 파일이 없습니다."));
        }

        File directory = new File(uploadDir);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        
        String originalFilename = file.getOriginalFilename();
        String safeFilename = System.currentTimeMillis() + "_" + originalFilename;
        File dest = new File(uploadDir, safeFilename); // 저장될 파일 객체

        try {
            // 1. 파일 저장
            file.transferTo(dest); 
            log.info("엑셀 파일 로컬 저장 완료: {}", dest.getAbsolutePath());
            
            // 2. 저장된 File 객체를 사용하여 파싱
            List<SysUserExcelDTO> sysUserList = userManagementExcelService.parseExcelToSysUserExcelDTO(dest);
            
            // 행 수 제한 추가
            if (sysUserList.size() > 1000) {
                
            	return ResponseEntity.ok(Map.of(
                    "status", "fail", 
                    "message", "엑셀 파일의 행 수가 1000개를 초과합니다. 최대 1000행까지만 업로드 가능합니다."
                ));
            }

            if (sysUserList.isEmpty()) {
            	
                throw new Exception("엑셀 파일에 등록할 유효한 사용자 정보가 없습니다."); 
            }
            
            // 3. 트랜잭션 배치 삽입 실행 
            int successCount = userManagementExcelService.batchInsertSysUser(sysUserList);

            String message = String.format("총 %d개의 사용자 정보가 성공적으로 등록되었습니다.", successCount);
            return ResponseEntity.ok(Map.of("status", "success", "message", message));

        } catch (DuplicateKeyException e) {
            
            log.warn("일괄 업로드 중 데이터베이스 중복 키 오류 발생: {}", e.getMessage());
            
            // Service에서 던진 중복 메시지를 활용하여 사용자에게 명확히 전달
            String exceptionMessage = e.getMessage();
            String userFriendlyMessage;
            
            // DuplicateKeyException은 보통 Service에서 행 번호를 포함하여 던집니다.
            userFriendlyMessage = exceptionMessage + " (사용자 ID 중복). 전체 등록이 롤백되었습니다.";
            
            return ResponseEntity.ok(Map.of("status", "fail", "message", userFriendlyMessage));
            
        } catch (Exception e) {
            
            // 기타 파일 처리, 파싱, 런타임 오류 처리
            log.error("엑셀 파일 업로드 및 처리 중 오류 발생: ", e);
            
            String exceptionMessage = e.getMessage();
            String userFriendlyMessage;

            if (exceptionMessage != null && (exceptionMessage.contains("행 처리 중 오류 발생") || exceptionMessage.contains("필수 입력 항목"))) {
                 // 서비스 레이어에서 던진 상세 파싱/유효성 검증 메시지를 클라이언트에 그대로 노출
                 userFriendlyMessage = "일괄 업로드 실패: " + exceptionMessage;
            } else {
                 // 예상치 못한 시스템/IO 오류
                 userFriendlyMessage = "일괄 업로드 처리 중 시스템 오류가 발생했습니다. 파일을 다시 확인하거나 관리자에게 문의해주세요.";
            }

            return ResponseEntity.ok(Map.of("status", "fail", "message", userFriendlyMessage));
        }
    }

    /**
     * [3단계] 현재 검색 조건에 맞는 사용자 목록을 엑셀 파일로 다운로드합니다.
     */
    @GetMapping("/downloadSysUserList")
    public void downloadSysUserList(
            @RequestParam(required = false, defaultValue = "") String searchUserCondition,
            HttpServletResponse response) throws IOException {

        // 1. 데이터 조회
        List<SysUserExcelDTO> sysUserList = userManagementExcelService.searchUserInfoMapListForExcel(searchUserCondition);

        // 2. 엑셀 파일 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("사용자목록");

        // 헤더
        String[] headers = {"No", "사용자ID", "사용자세부권한코드", "사용자세부권한코드명", "사용자권한코드", "사용자권한코드명", "사용자계정상태코드", "사용자계정상태명",
        		 "사용자명", "사용자비밀번호", "사용자이메일", "사용자연락처", "사용자생년월일", "사용자학과코드", "사용자학과명", "사용자학년코드", "사용자학년", "사용자주소",
        		 "사용자상세주소", "사용자우편번호", "사용자마지막로그인일자", "사용자로그인실패횟수", "사용자생성일자", "사용자업데이트일자"};
        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < headers.length; i++) {

            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // 데이터 채우기
        int rowNum = 1;
        for (SysUserExcelDTO sysUser : sysUserList) {

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(sysUser.getUserNo()); 
            row.createCell(1).setCellValue(sysUser.getUserId());
            row.createCell(2).setCellValue(sysUser.getUserAuth());
            row.createCell(3).setCellValue(sysUser.getAuthDetailName());
            row.createCell(4).setCellValue(sysUser.getAuthCode());
            row.createCell(5).setCellValue(sysUser.getAuthName());
            row.createCell(6).setCellValue(sysUser.getUserStatus());
            row.createCell(7).setCellValue(sysUser.getStatusName());
            row.createCell(8).setCellValue(sysUser.getUserName());
            row.createCell(9).setCellValue(sysUser.getUserPassword());
            row.createCell(10).setCellValue(sysUser.getUserEmail());
            row.createCell(11).setCellValue(sysUser.getUserPhone());
            String userBirthStr = sysUser.getUserBirth() != null 
                    ? new java.text.SimpleDateFormat("yyyy-MM-dd").format(sysUser.getUserBirth()) 
                    : "-";
            row.createCell(12).setCellValue(userBirthStr); 
            row.createCell(13).setCellValue(sysUser.getDeptCode());
            row.createCell(14).setCellValue(sysUser.getDeptName());
            Integer userGrade = sysUser.getUserGrade();
            row.createCell(15).setCellValue(userGrade != null ? userGrade.toString() : "-");
            row.createCell(16).setCellValue(sysUser.getGradeName());
            row.createCell(17).setCellValue(sysUser.getUserAddr());
            row.createCell(18).setCellValue(sysUser.getUserAddrDetail());
            row.createCell(19).setCellValue(sysUser.getUserZipcode());
            row.createCell(20).setCellValue(sysUser.getUserLastLogin() != null ? sysUser.getUserLastLogin().toString() : "-"); 
            Integer loginFailCnt = sysUser.getUserLoginFailCnt();
            row.createCell(21).setCellValue(loginFailCnt != null ? loginFailCnt.toString() : "0");
            row.createCell(22).setCellValue(sysUser.getUserCreatedate() != null ? sysUser.getUserCreatedate().toString() : "-"); 
            row.createCell(23).setCellValue(sysUser.getUserUpdatedate() != null ? sysUser.getUserUpdatedate().toString() : "-");
        }

        // 셀 너비 자동 조정
        for (int i = 0; i < headers.length; i++) {

            sheet.autoSizeColumn(i);
        }

        // 3. HTTP 응답 설정 및 다운로드

        String fileName = "사용자목록_" + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".xlsx";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + encodedFileName + ";filename*=UTF-8''" + encodedFileName);

        workbook.write(response.getOutputStream());
        workbook.close();
    }

}
