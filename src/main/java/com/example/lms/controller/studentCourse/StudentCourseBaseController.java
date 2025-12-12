package com.example.lms.controller.studentCourse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.lms.dto.StudentCourseDTO;
import com.example.lms.dto.StudentTimetableDTO;
import com.example.lms.dto.SysUserDTO;
import com.example.lms.service.studentCourse.StudentCourseBaseService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class StudentCourseBaseController {

	private final StudentCourseBaseService service;
	
    // 내 수강 과목 목록
    @GetMapping("/myCourses")
    public String myCourses(HttpSession session, Model model) {

        SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        List<StudentCourseDTO> courseList = service.getMyCourseList(loginUser.getUserNo());
        model.addAttribute("courseList", courseList);

        // HEADER
        model.addAttribute("pageTitle", "내 수강과목");
        model.addAttribute("pageDescription", "현재 수강 중인 강의 목록을 확인할 수 있습니다.");
        model.addAttribute("loginUserName", loginUser.getUserName());

        // NAV 활성화
        model.addAttribute("nav_myCourses", true);

        return "studentCourse/myCourses";
    }

    // 학생 시간표
    @GetMapping("/studentTimetable")
    public String studentTimetable(HttpSession session, Model model) {

        SysUserDTO loginUser = (SysUserDTO) session.getAttribute("loginUser");
        if (loginUser == null) return "redirect:/login";

        List<StudentTimetableDTO> timetableList =
                service.getStudentTimetable(loginUser.getUserNo());

        // 5일 × 8교시
        StudentTimetableDTO[][] grid = new StudentTimetableDTO[5][8];
        for (StudentTimetableDTO t : timetableList) {
            int yIndex = t.getCourseTimeYoil() - 1;
            for (int p = t.getCourseTimeStart(); p <= t.getCourseTimeEnd(); p++) {
                grid[yIndex][p - 1] = t;
            }
        }

        // Mustache Grid 변환
        List<Map<String, Object>> periods = new ArrayList<>();
        for (int p = 1; p <= 8; p++) {
            Map<String, Object> periodRow = new HashMap<>();
            periodRow.put("period", p);
            List<Map<String, Object>> yoils = new ArrayList<>();
            for (int y = 0; y < 5; y++) {
                Map<String, Object> cell = new HashMap<>();
                if (grid[y][p - 1] != null) cell.put("timetable", grid[y][p - 1]);
                yoils.add(cell);
            }
            periodRow.put("yoils", yoils);
            periods.add(periodRow);
        }

        model.addAttribute("periods", periods);
        model.addAttribute("grid", grid);

        // HEADER
        model.addAttribute("pageTitle", "내 시간표");
        model.addAttribute("pageDescription", "이번 학기의 시간표를 한눈에 확인할 수 있습니다.");
        model.addAttribute("loginUserName", loginUser.getUserName());

        // NAV 활성화
        model.addAttribute("nav_timetable", true);

        return "studentCourse/studentTimetable";
    }
}
