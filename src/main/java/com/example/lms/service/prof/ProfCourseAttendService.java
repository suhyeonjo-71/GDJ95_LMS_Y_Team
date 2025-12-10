package com.example.lms.service.prof;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lms.dto.AttendanceDTO;
import com.example.lms.dto.ProfCourseAttendDTO;
import com.example.lms.mapper.prof.ProfCourseAttendMapper;

@Service
@Transactional
public class ProfCourseAttendService {

    @Autowired
    ProfCourseAttendMapper mapper;

    // 주차 리스트 — 1~15주 자동 생성
    public List<Integer> getWeeks(int courseNo) {

    	List<Integer> weeks = new ArrayList<>();
        for (int i = 1; i <= 15; i++) {
            weeks.add(i);
        }
        return weeks;
    }

    // 특정 주차 출석 조회
    public List<ProfCourseAttendDTO> getCourseAttendList(int courseNo, int weekNo) {

        List<ProfCourseAttendDTO> list = mapper.selectCourseAttendList(courseNo, weekNo);

        // mustache에서 라디오 체크 자동 세팅
        for (ProfCourseAttendDTO dto : list) {

            Integer status = dto.getAttendanceStatus();

            if (status == null) {
                dto.setAttend(false);
                dto.setAbsent(false);
                dto.setLate(false);
                continue;
            }

            dto.setAttend(status == 0);
            dto.setAbsent(status == 1);
            dto.setLate(status == 2);
        }

        return list;
    }

    // 출석 저장
    public void saveCourseAttend(List<AttendanceDTO> list) {
        for (AttendanceDTO a : list) {
            mapper.upsertCourseAttend(a);
        }
    }
}


