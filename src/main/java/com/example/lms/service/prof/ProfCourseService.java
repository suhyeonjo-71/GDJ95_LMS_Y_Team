package com.example.lms.service.prof;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lms.dto.ProfCourseDTO;
import com.example.lms.dto.ProfCourseTimeDTO;
import com.example.lms.mapper.prof.ProfCourseMapper;

@Service
@Transactional
public class ProfCourseService {
	          
	@Autowired
	private ProfCourseMapper courseMapper;
	
	// 교수별 강의 리스트
	public List<ProfCourseDTO> getFilteredCourseList(int professorUserNo, Integer year, String semester, String status, int startRow, int rowPerPage) {
        return courseMapper.selectFilteredCourseList(professorUserNo, year, semester, status, startRow, rowPerPage);
    }

	// 리스트 페이징
    public int getFilteredCourseCount(int professorUserNo, Integer year, String semester, String status) {
        return courseMapper.selectFilteredCourseCount(professorUserNo, year, semester, status);
    }
    
    // 필터 년도 저장
    public List<Integer> getDistinctYears(int professorUserNo) {
		return courseMapper.selectDistinctYearsByProfessor(professorUserNo);
	}
	
	// 강의 + 강의 시간 등록
	public String addCourse(ProfCourseDTO course, List<ProfCourseTimeDTO> timeList) {
		
		// 1) 모든 시간 중복 여부 체크 (강의 등록 전)
		for(ProfCourseTimeDTO t : timeList) {
			int dup = courseMapper.checkDuplicateTime(
					t.getCourseTimeYoil(),
	                t.getCourseTimeStart(),
	                t.getCourseTimeEnd(),
	                course.getCourseClassroom(),
	                course.getProfessorUserNo(),
	                course.getCourseNo() 
	        );
			
			if(dup > 0) {
				return "해당 요일/시간은 이미 다른 강의가 존재합니다.";
			}
		}
		
		// 2) 중복 x : 강의 저장
		courseMapper.insertCourse(course);
		int courseNo = course.getCourseNo();
		
		// 3) 강의 시간 저장
		for (ProfCourseTimeDTO t : timeList) {
	        t.setCourseNo(courseNo);
	        t.setProfessorUserNo(course.getProfessorUserNo());
	        courseMapper.insertCourseTime(t);
	    }
		
		return null;
	}
	
	// 대시보드 (강의 + 강의시간 리스트로 묶기)
	public ProfCourseDTO getCourseDetail(int courseNo) {

        List<ProfCourseDTO> raw = courseMapper.selectCourseDetail(courseNo);

        if (raw == null || raw.isEmpty()) return null;

        ProfCourseDTO course = raw.get(0);
        course.setTimeList(new ArrayList<>());

        for (ProfCourseDTO row : raw) {

            if (row.getCourseTimeYoil() != null) {

                ProfCourseTimeDTO t = new ProfCourseTimeDTO();
                t.setCourseTimeYoil(row.getCourseTimeYoil());
                t.setCourseTimeStart(row.getCourseTimeStart());
                t.setCourseTimeEnd(row.getCourseTimeEnd());

                applySelectedFlags(t);

                course.getTimeList().add(t);
            }
        }

        return course;
    }
	
	// 수정 (강의 + 강의 시간 전체 갈아끼우기)
    public String modifyCourse(ProfCourseDTO course, List<ProfCourseTimeDTO> timeList) {

    	// 1) 시간 중복 체크 (update 전)
        for (ProfCourseTimeDTO t : timeList) {

            int dup = courseMapper.checkDuplicateTime(
                    t.getCourseTimeYoil(),
                    t.getCourseTimeStart(),
                    t.getCourseTimeEnd(),
                    course.getCourseClassroom(),
                    course.getProfessorUserNo(),
                    course.getCourseNo() 
            );

            if (dup > 0) {
                return "해당 요일/시간은 이미 다른 강의가 존재합니다.";
            }
        }

        // 2) 강의 수정
        courseMapper.updateCourse(course);

        // 3) 기존 시간 삭제
        courseMapper.deleteCourseTime(course.getCourseNo());

        // 4) 새 시간 등록
        for (ProfCourseTimeDTO t : timeList) {
            t.setCourseNo(course.getCourseNo());
            t.setProfessorUserNo(course.getProfessorUserNo());
            courseMapper.insertCourseTime(t);
        }

        return null; 
    }

    // 삭제
    public void removeCourse(int courseNo) {

        // 강의 시간 삭제
        courseMapper.deleteCourseTime(courseNo);

        // 강의 삭제
        courseMapper.deleteCourse(courseNo);
    }
    
    // selected
    private void applySelectedFlags(ProfCourseTimeDTO t) {

        int yoil = t.getCourseTimeYoil();
        t.setYoil1(yoil == 1);
        t.setYoil2(yoil == 2);
        t.setYoil3(yoil == 3);
        t.setYoil4(yoil == 4);
        t.setYoil5(yoil == 5);

        int start = t.getCourseTimeStart();
        t.setStart1(start == 1);
        t.setStart2(start == 2);
        t.setStart3(start == 3);
        t.setStart4(start == 4);
        t.setStart5(start == 5);
        t.setStart6(start == 6);
        t.setStart7(start == 7);
        t.setStart8(start == 8);
        t.setStart9(start == 9);
        t.setStart10(start == 10);

        int end = t.getCourseTimeEnd();
        t.setEnd1(end == 1);
        t.setEnd2(end == 2);
        t.setEnd3(end == 3);
        t.setEnd4(end == 4);
        t.setEnd5(end == 5);
        t.setEnd6(end == 6);
        t.setEnd7(end == 7);
        t.setEnd8(end == 8);
        t.setEnd9(end == 9);
        t.setEnd10(end == 10);
    }
}
