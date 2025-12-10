package com.example.lms.controller.excel;

import com.example.lms.dto.DeptDTO;
import com.example.lms.dto.SysAuthDTO;
import com.example.lms.service.excel.DeptManagementExcelService;
import com.example.lms.service.excel.SysAuthManagementExcelService;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
*
* 2025. 12. 01.
* Author - jm
* 내정보 - 사용자권한관리 - 엑셀 기능
*/
@Slf4j
@Controller
@RequestMapping ("/admin") // 공통 경로 매핑
public class SysAuthManagementExcelController {
	
    @Autowired
    private SysAuthManagementExcelService sysAuthManagementExcelService;

    @Value("${excel.file.upload-dir2}")
    private String uploadDir;

//    @Value("${excel.file.form-dir}") // 양식 파일 경로 주입 
//    private String formDir; // 로컬위치가아닌 resources에 넣어서 사용하기 위해 미사용 처리

    /**
     * 사용자 권한 코드 등록용 엑셀 양식 파일을 다운로드합니다.
     * - 서버에 미리 저장된 '사용자권한코드등록양식.xlsx' 파일을 읽어와 다운로드합니다.
     */
    @GetMapping("/downloadSysAuthForm")
    public void downloadDeptForm(HttpServletResponse response) throws IOException {

        final String FORM_RESOURCE_PATH = "/excel/사용자권한코드일괄등록양식.xlsx"; // resources 하위 경로
        final String FORM_FILE_NAME = "사용자권한코드일괄등록양식.xlsx";

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


 // 기존 @PostMapping("/uploadDeptBatch") 대신, @PostMapping("/uploadSysAuthBatch")를 사용합니다.
    @PostMapping("/uploadSysAuthBatch") 
    @ResponseBody
    // 반환 타입 및 파라미터는 그대로 유지
    public ResponseEntity<Map<String, Object>> uploadSysAuthBatch(@RequestParam("excelFile") MultipartFile file) {

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
            
            // 2. 저장된 File 객체를 사용하여 파싱 (Dept -> SysAuth로 변경)
            // ⭐️ deptManagementExcelService.parseExcelToDeptDTO(dest)  -> sysAuthManagementExcelService.parseExcelToSysAuthDTO(dest) ⭐️
            List<SysAuthDTO> sysAuthList = sysAuthManagementExcelService.parseExcelToSysAuthDTO(dest);
            
            // 행 수 제한 추가
            if (sysAuthList.size() > 1000) {
                
            	return ResponseEntity.ok(Map.of(
                    "status", "fail", 
                    "message", "엑셀 파일의 행 수가 1000개를 초과합니다. 최대 1000행까지만 업로드 가능합니다."
                ));
            }

            // ⭐️ deptList.isEmpty() -> sysAuthList.isEmpty()로 변경 ⭐️
            if (sysAuthList.isEmpty()) {
                // 유효한 데이터가 없을 경우 저장된 파일 삭제 고려 (선택 사항)
                // dest.delete(); 
                // ⭐️ 메시지 변경: 학과 정보 -> 권한 정보 ⭐️
                throw new Exception("엑셀 파일에 등록할 유효한 사용자 권한 정보가 없습니다."); 
            }
            
            // 3. 트랜잭션 배치 삽입 실행 (Dept -> SysAuth로 변경)
            // ⭐️ deptManagementExcelService.batchInsertDept(deptList) -> sysAuthManagementExcelService.batchInsertSysAuth(sysAuthList) ⭐️
            int successCount = sysAuthManagementExcelService.batchInsertSysAuth(sysAuthList);

            // ⭐️ 메시지 변경: 학과 정보 -> 권한 정보 ⭐️
            String message = String.format("총 %d개의 사용자 권한 정보가 성공적으로 등록되었습니다.", successCount);
            return ResponseEntity.ok(Map.of("status", "success", "message", message));

        } catch (DuplicateKeyException e) {
            
            log.warn("일괄 업로드 중 데이터베이스 중복 키 오류 발생: {}", e.getMessage());
            
            // Service에서 던진 중복 메시지를 활용하여 사용자에게 명확히 전달
            String exceptionMessage = e.getMessage();
            String userFriendlyMessage;
            
            if (exceptionMessage != null && exceptionMessage.contains("세부 권한 코드")) {
                 // 서비스에서 던진 세부 권한 중복 메시지를 그대로 사용하거나 가공
                 userFriendlyMessage = exceptionMessage + ". 전체 등록이 취소되었습니다.";
            } else {
                 // 기타 DuplicateKeyException (AuthCode 중복 등)
                 userFriendlyMessage = "업로드에 실패했습니다. 이미 등록된 권한 코드(중복 키)가 파일에 포함되어 전체 등록이 취소되었습니다.";
            }
            
            return ResponseEntity.ok(Map.of("status", "fail", "message", userFriendlyMessage));
            
        } catch (Exception e) {
            
            // 기타 파일 처리, 파싱, 런타임 오류 처리
            log.error("엑셀 파일 업로드 및 처리 중 오류 발생: ", e);
            
            String userFriendlyMessage = "일괄 업로드 처리 중 알 수 없는 오류가 발생했습니다. 파일 내용을 확인하거나 관리자에게 문의해주세요.";

            return ResponseEntity.ok(Map.of("status", "fail", "message", userFriendlyMessage));
        } 
        /*
        finally {
            // 5. 로컬에 임시 저장된 파일 삭제 (선택 사항이지만 보안 및 디스크 관리 측면에서 권장)
            if (dest != null && dest.exists()) {
                dest.delete();
                log.info("업로드 임시 파일 삭제 완료: {}", dest.getAbsolutePath());
            }*/
    }

    /**
     * [3단계] 현재 검색 조건에 맞는 학과 목록을 엑셀 파일로 다운로드합니다.
     */
    @GetMapping("/downloadSysAuthList")
    public void downloadSysAuthList(
            @RequestParam(required = false, defaultValue = "") String searchSysAuthCondition,
            HttpServletResponse response) throws IOException {

        // 1. 데이터 조회
        List<SysAuthDTO> sysAuthList = sysAuthManagementExcelService.searchSysAuthInfoListForExcel(searchSysAuthCondition);

        // 2. 엑셀 파일 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("사용자권한코드목록");

        // 헤더
        String[] headers = {"No", "세부권한코드", "세부권한명", "권한코드", "권한명", "세부권한코드 업데이트일자", "세부권한코드 생성일자", "권한코드 업데이트일자", "권한코드 생성일자"};
        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < headers.length; i++) {

            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // 데이터 채우기
        int rowNum = 1;
        for (SysAuthDTO sysAuth : sysAuthList) {

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(sysAuth.getRownum());
            row.createCell(1).setCellValue(sysAuth.getAuthDetailCode());
            row.createCell(2).setCellValue(sysAuth.getAuthDetailName());
            row.createCell(3).setCellValue(sysAuth.getAuthCode());
            row.createCell(4).setCellValue(sysAuth.getAuthName());
            row.createCell(5).setCellValue(sysAuth.getAuthDetailUpdatedate() != null ? sysAuth.getAuthDetailUpdatedate().toString() : "-");
            row.createCell(6).setCellValue(sysAuth.getAuthDetailCreatedate() != null ? sysAuth.getAuthDetailCreatedate().toString() : "-");
            row.createCell(7).setCellValue(sysAuth.getAuthUpdatedate() != null ? sysAuth.getAuthUpdatedate().toString() : "-");
            row.createCell(8).setCellValue(sysAuth.getAuthCreatedate() != null ? sysAuth.getAuthCreatedate().toString() : "-");
        }

        // 셀 너비 자동 조정
        for (int i = 0; i < headers.length; i++) {

            sheet.autoSizeColumn(i);
        }

        // 3. HTTP 응답 설정 및 다운로드

        String fileName = "사용자권한코드목록_" + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".xlsx";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + encodedFileName + ";filename*=UTF-8''" + encodedFileName);

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
