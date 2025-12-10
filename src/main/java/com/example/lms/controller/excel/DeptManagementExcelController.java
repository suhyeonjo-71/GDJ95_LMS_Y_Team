package com.example.lms.controller.excel;

import com.example.lms.dto.DeptDTO;
import com.example.lms.service.excel.DeptManagementExcelService;
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
 * 내정보 - 학과관리 - 엑셀 기능
 */
@Slf4j
@Controller
@RequestMapping ("/admin") // 공통 경로 매핑
public class DeptManagementExcelController {

    @Autowired
    private DeptManagementExcelService deptManagementExcelService;

    @Value("${excel.file.upload-dir}")
    private String uploadDir;

//    @Value("${excel.file.form-dir}") // 양식 파일 경로 주입 
//    private String formDir; // 로컬위치가아닌 resources에 넣어서 사용하기 위해 미사용 처리

    /**
     * 학과 등록용 엑셀 양식 파일을 다운로드합니다.
     * - 서버에 미리 저장된 '학과등록양식.xlsx' 파일을 읽어와 다운로드합니다.
     */
    @GetMapping("/downloadDeptForm")
    public void downloadDeptForm(HttpServletResponse response) throws IOException {

        final String FORM_RESOURCE_PATH = "/excel/학과일괄등록양식.xlsx"; // resources 하위 경로
        final String FORM_FILE_NAME = "학과일괄등록양식.xlsx";

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


    /**
     * [2단계] 학과 엑셀 파일을 업로드하여 일괄 등록 처리합니다.
     * 중복 키 발생 시 트랜잭션 롤백 및 오류 메시지를 반환합니다.
     */
    @PostMapping("/uploadDeptBatch")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> uploadDeptBatch(@RequestParam("excelFile") MultipartFile file) {

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
            List<DeptDTO> deptList =  deptManagementExcelService.parseExcelToDeptDTO(dest);
            
            // 행 수 제한 추가
            if (deptList.size() > 1000) {
                
            	return ResponseEntity.ok(Map.of(
                    "status", "fail", 
                    "message", "엑셀 파일의 행 수가 1000개를 초과합니다. 최대 1000행까지만 업로드 가능합니다."
                ));
            }

            if (deptList.isEmpty()) {
                // 유효한 데이터가 없을 경우 저장된 파일 삭제 고려 (선택 사항)
                // dest.delete(); 
                throw new Exception("엑셀 파일에 등록할 유효한 학과 정보가 없습니다.");
            }
            
            // 3. 트랜잭션 배치 삽입 실행 (중복 시 Service에서 DuplicateKeyException 발생 및 롤백)
            int successCount = deptManagementExcelService.batchInsertDept(deptList);

            String message = String.format("총 %d개의 학과 정보가 성공적으로 등록되었습니다.", successCount);
            return ResponseEntity.ok(Map.of("status", "success", "message", message));

        } catch (DuplicateKeyException e) {
            
            // ⭐️ [수정]: 사용자 친화적 메시지 정의 및 반환 ⭐️
            log.warn("일괄 업로드 중 데이터베이스 중복 키 오류 발생: {}", e.getMessage());
            
            // Service에서 던진 메시지에 특정 정보(예: 중복 코드)가 포함되어 있다면 추출하여 사용하거나,
            // 단순히 명확한 메시지를 사용자에게 전달합니다.
            String userFriendlyMessage = "업로드에 실패했습니다. 파일에 이미 등록된 학과 코드(중복 키)가 포함되어 전체 등록이 취소되었습니다.";
            
            return ResponseEntity.ok(Map.of("status", "fail", "message", userFriendlyMessage));
            
        } catch (Exception e) {
            
            // 기타 파일 처리, 파싱, 런타임 오류 처리
            log.error("엑셀 파일 업로드 및 처리 중 오류 발생: ", e);
            
            // 일반 오류도 사용자에게 상세한 시스템 정보를 노출하지 않도록 메시지를 정제합니다.
            String userFriendlyMessage = "일괄 업로드 처리 중 알 수 없는 오류가 발생했습니다. 파일 내용을 확인하거나 관리자에게 문의해주세요.";

            return ResponseEntity.ok(Map.of("status", "fail", "message", userFriendlyMessage));
        }
    }

    /**
     * [3단계] 현재 검색 조건에 맞는 학과 목록을 엑셀 파일로 다운로드합니다.
     */
    @GetMapping("/downloadDeptList")
    public void downloadDeptList(
            @RequestParam(required = false, defaultValue = "") String searchDeptCondition,
            HttpServletResponse response) throws IOException {

        // 1. 데이터 조회
        List<DeptDTO> deptList = deptManagementExcelService.searchDeptInfoListForExcel(searchDeptCondition);

        // 2. 엑셀 파일 생성
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("학과목록");

        // 헤더
        String[] headers = {"No", "학과코드", "학과명", "수정일자", "생성일자"};
        Row headerRow = sheet.createRow(0);

        for (int i = 0; i < headers.length; i++) {

            headerRow.createCell(i).setCellValue(headers[i]);
        }

        // 데이터 채우기
        int rowNum = 1;
        for (DeptDTO dept : deptList) {

            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(dept.getRownum());
            row.createCell(1).setCellValue(dept.getDeptCode());
            row.createCell(2).setCellValue(dept.getDeptName());
            row.createCell(3).setCellValue(dept.getDeptUpdatedate() != null ? dept.getDeptUpdatedate().toString() : "-");
            row.createCell(4).setCellValue(dept.getDeptCreatedate() != null ? dept.getDeptCreatedate().toString() : "-");
        }

        // 셀 너비 자동 조정
        for (int i = 0; i < headers.length; i++) {

            sheet.autoSizeColumn(i);
        }

        // 3. HTTP 응답 설정 및 다운로드

        String fileName = "학과목록_" + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".xlsx";
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment;filename=" + encodedFileName + ";filename*=UTF-8''" + encodedFileName);

        workbook.write(response.getOutputStream());
        workbook.close();
    }
}
