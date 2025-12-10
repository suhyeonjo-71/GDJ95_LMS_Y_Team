package com.example.lms.controller.prof;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.lms.dto.AttendanceDTO;
import com.example.lms.dto.ProfCourseAttendDTO;
import com.example.lms.mapper.prof.ProfCourseAttendMapper;
import com.example.lms.service.prof.ProfCourseAttendService;

@Controller
public class ProfCourseAttendController {

    @Autowired
    ProfCourseAttendService service;

    // 주차 리스트 화면
    @GetMapping("/profCourseAttendWeekList")
    public String attendWeekList(@RequestParam int courseNo, Model model) {

        model.addAttribute("courseNo", courseNo);
        model.addAttribute("weekList", service.getWeeks(courseNo));

        return "profCourseAttend/profCourseAttendWeekList";
    }

    // 특정 주차 화면
    @GetMapping("/profCourseAttendList")
    public String attendDetail(@RequestParam int courseNo,
                               @RequestParam int weekNo,
                               Model model) {

        model.addAttribute("courseNo", courseNo);
        model.addAttribute("weekNo", weekNo);
        model.addAttribute("list", service.getCourseAttendList(courseNo, weekNo));

        return "profCourseAttend/profCourseAttendList";
    }

    // 저장
    @PostMapping("/saveCourseAttendList")
    public String saveAttendance(
            @RequestParam Map<String, String> params,
            @RequestParam int courseNo,
            @RequestParam int weekNo
    ) {
        List<AttendanceDTO> list = new ArrayList<>();

        params.forEach((key, value) -> {
            if (key.startsWith("attendanceStatus_")) {

                String userNoStr = key.replace("attendanceStatus_", "");
                int studentUserNo = Integer.parseInt(userNoStr);

                int status = Integer.parseInt(value);

                AttendanceDTO dto = new AttendanceDTO();
                dto.setStudentUserNo(studentUserNo);
                dto.setAttendanceStatus(status);
                dto.setCourseNo(courseNo);
                dto.setWeekNo(weekNo);

                list.add(dto);
            }
        });

        service.saveCourseAttend(list);

        return "redirect:/profCourseAttendList?courseNo=" + courseNo + "&weekNo=" + weekNo;
    }
}
