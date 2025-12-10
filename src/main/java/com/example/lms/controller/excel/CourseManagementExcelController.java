package com.example.lms.controller.excel;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.lms.dto.CourseManagementDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.service.excel.CourseManagementExcelService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;


/**
*
* 2025. 12. 03.
* Author - jm
* 학점관리 - 엑셀 기능
*/
@Slf4j
@Controller
@RequestMapping
public class CourseManagementExcelController {
	
	@Autowired
	private CourseManagementExcelService courseManagementExcelService;

	
	/**
	 * 현재 검색 조건에 맞는 학점 목록을 엑셀 파일로 다운로드합니다.
	 */
	@GetMapping("/downloadCourseList")
	public void downloadSysAuthList(
	        @RequestParam(required = false, defaultValue = "") String selectedCourseYearAndSemester,
	        @RequestParam(required = false, defaultValue = "") String searchCourseCondition,
	        HttpServletResponse response,
	        HttpSession session) throws IOException {

	    SysUserDTO sessionUserDto = (SysUserDTO) session.getAttribute("loginUser");
	    String authCd = sessionUserDto.getAuthCode();
	    Integer userNo = sessionUserDto.getUserNo();
	    
	    Map<String, Object> searchParams = new HashMap<>();
	    searchParams.put("searchCourseCondition", searchCourseCondition);
	    searchParams.put("selectedCourseYearAndSemester", selectedCourseYearAndSemester);
	    searchParams.put("userNo", userNo);

	    List<CourseManagementDTO> courseList = new ArrayList<>();
	    
	    // 1. 데이터 조회 및 헤더 설정
	    String[] headers = null; // 초기화
	    boolean isAdmin = authCd.equals("A001");
	    
	    if (isAdmin) {
	    	
	        courseList = courseManagementExcelService.searchCourseManagementListUseAdminForExcel(searchParams);
	        headers = new String[]{"No", "강의번호", "개설년도", "학기", "학과명", "강의명", "교수성명", "개설일", "이수학점", "평가점수", "평가등급", "학생성명"};
	    }  else if (authCd.equals("S001")) {
	    	
	        courseList = courseManagementExcelService.searchCourseManagementListUseStudtForExcel(searchParams);
	        headers = new String[]{"No", "강의번호", "개설년도", "학기", "학과명", "강의명", "교수성명", "개설일", "이수학점", "평가점수", "평가등급"};
	    } else {
	        // 권한 없는 경우 다운로드 로직 중단
	        return;
	    }
	    
	    // 2. 엑셀 파일 생성
	    Workbook workbook = new XSSFWorkbook();
	    Sheet sheet = workbook.createSheet("사용자학점관리목록");
	    
	    // 헤더 행 생성 (공통)
	    Row headerRow = sheet.createRow(0);
	    for (int i = 0; i < headers.length; i++) {
	        headerRow.createCell(i).setCellValue(headers[i]);
	    }

	    // 3. 데이터 채우기 (분기 처리 통합)
	    int rowNum = 1;
	    
	    for (CourseManagementDTO course : courseList) {
	        Row row = sheet.createRow(rowNum++);
	        
	        // 인덱스 0 ~ 10: 공통 항목
	        
	        Integer rownm = course.getRownm();
	        row.createCell(0).setCellValue(rownm != null ? rownm.toString() : "0"); 
	        Integer courseNo = course.getCourseNo();
	        row.createCell(1).setCellValue(courseNo != null ? courseNo.toString() : "0"); 
	        Integer courseYear = course.getCourseYear();
	        row.createCell(2).setCellValue(courseYear != null ? courseYear.toString() : "0");
	        Integer courseSemester = course.getCourseSemester();
	        row.createCell(3).setCellValue(courseSemester != null ? courseSemester.toString() : "0");
	        row.createCell(4).setCellValue(course.getDeptName());
	        row.createCell(5).setCellValue(course.getCourseName());
	        row.createCell(6).setCellValue(course.getProfessorUserName());
	        String createdate = course.getCreatedate();
	        row.createCell(7).setCellValue(createdate != null ? createdate : "-");
	        Integer courseScore = course.getCourseScore();
	        row.createCell(8).setCellValue(courseScore != null ? courseScore.toString() : "0");
	        // Double gradeFinalScore = course.getGradeFinalScore();
	        // row.createCell(9).setCellValue(gradeFinalScore != null ? gradeFinalScore.toString() : "0.0");
	        row.createCell(9).setCellValue(course.getGradeFinalScoreNullChk());
	        row.createCell(10).setCellValue(course.getGradeValue());

	     // 관리자 헤더의 마지막 인덱스 (11)에 학생 이름 추가
	        if (isAdmin) {
	            
	            row.createCell(11).setCellValue(course.getStudentUserName()); 
	        }
	    }
	    
	    // 4. 셀 너비 자동 조정 (공통)
	    for (int i = 0; i < headers.length; i++) {
	        sheet.autoSizeColumn(i);
	    }

	    // 5. HTTP 응답 설정 및 다운로드 (공통)
	    String fileName = "학점관리목록_" + new java.text.SimpleDateFormat("yyyyMMdd_HHmmss").format(new java.util.Date()) + ".xlsx";
	    String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8);

	    response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
	    response.setHeader("Content-Disposition", "attachment;filename=" + encodedFileName + ";filename*=UTF-8''" + encodedFileName);

	    workbook.write(response.getOutputStream());
	    workbook.close();
	}
}
