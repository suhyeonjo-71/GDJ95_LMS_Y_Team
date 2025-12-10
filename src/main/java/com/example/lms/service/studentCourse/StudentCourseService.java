package com.example.lms.service.studentCourse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.lms.dto.AttendanceSummaryDTO;
import com.example.lms.dto.CourseQuestionDTO;
import com.example.lms.dto.DeptDTO;
import com.example.lms.dto.GradeSummaryDTO;
import com.example.lms.dto.StudentAssignmentListDTO;
import com.example.lms.dto.StudentAttendanceDTO;
import com.example.lms.dto.StudentCourseDTO;
import com.example.lms.dto.StudentCourseDetailDTO;
import com.example.lms.dto.StudentCourseHomeDTO;
import com.example.lms.dto.StudentCourseNoticeDTO;
import com.example.lms.dto.StudentQuestionDTO;
import com.example.lms.dto.StudentTimetableDTO;
import com.example.lms.mapper.studentCourse.StudentCourseMapper;
import com.example.lms.mapper.studentCourse.StudentCourseQuestionMapper;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class StudentCourseService {

    private final StudentCourseMapper studentCourseMapper;
    private final StudentCourseQuestionMapper courseQuestionMapper;

    // 출석 요약 조회
    public AttendanceSummaryDTO getAttendanceSummary(int courseNo, int studentUserNo) {

        AttendanceSummaryDTO summary =
                studentCourseMapper.selectAttendanceSummary(courseNo, studentUserNo);

        if (summary == null) {
            summary = new AttendanceSummaryDTO();
            summary.setAttendanceCount(0);
            summary.setLateCount(0);
            summary.setAbsentCount(0);
            summary.setAttendanceRate(0.0);
        }

        int present = summary.getAttendanceCount() == null ? 0 : summary.getAttendanceCount();
        int late    = summary.getLateCount() == null ? 0 : summary.getLateCount();
        int absent  = summary.getAbsentCount() == null ? 0 : summary.getAbsentCount();

        int total = present + late + absent;

        if (total == 0) {
            summary.setPresentRate(0.0);
            summary.setLateRate(0.0);
            summary.setAbsentRate(0.0);
        } else {
            summary.setPresentRate((present * 100.0) / total);
            summary.setLateRate((late * 100.0) / total);
            summary.setAbsentRate((absent * 100.0) / total);
        }

        return summary;
    }

    // 전체 주차 출석 상세 조회
    public List<StudentAttendanceDTO> getAttendanceDetailList(int courseNo, int studentUserNo) {
        return studentCourseMapper.selectAttendanceDetailList(courseNo, studentUserNo);
    }

    // 공지 목록 조회
    public List<StudentCourseNoticeDTO> getStudentCourseNoticeList(int courseNo, int startRow, int rowPerPage) {
        return studentCourseMapper.selectStudentCourseNoticeList(courseNo, startRow, rowPerPage);
    }

    // 공지 총 개수 조회
    public int getStudentCourseNoticeTotal(int courseNo) {
        return studentCourseMapper.selectStudentCourseNoticeTotal(courseNo);
    }

    // 공지 상세 조회
    public StudentCourseNoticeDTO getStudentCourseNoticeDetail(int courseNoticeNo) {
        studentCourseMapper.updateStudentCourseNoticeViewCount(courseNoticeNo);
        return studentCourseMapper.selectStudentCourseNoticeDetail(courseNoticeNo);
    }

    // 강의 홈 화면 정보 조회
    public StudentCourseHomeDTO getStudentCourseHome(int courseNo, int studentUserNo) {

        StudentCourseHomeDTO dto = new StudentCourseHomeDTO();

        StudentCourseHomeDTO baseInfo = studentCourseMapper.selectCourseBasicInfo(courseNo);
        if (baseInfo != null) {
            dto.setCourseNo(baseInfo.getCourseNo());
            dto.setCourseName(baseInfo.getCourseName());
            dto.setProfessorName(baseInfo.getProfessorName());
            dto.setCourseScore(baseInfo.getCourseScore());
            dto.setClassroom(baseInfo.getClassroom());
            dto.setCourseTimeYoil(baseInfo.getCourseTimeYoil());
            dto.setCourseTimeStart(baseInfo.getCourseTimeStart());
            dto.setCourseTimeEnd(baseInfo.getCourseTimeEnd());
        }

        dto.setNoticeList(studentCourseMapper.selectRecentNotices(courseNo));

        StudentAssignmentListDTO ass = studentCourseMapper.selectAssignmentSummary(courseNo, studentUserNo);
        if (ass != null) {
            dto.setAssignmentNo(ass.getAssignmentNo());
            dto.setAssignmentTitle(ass.getAssignmentTitle());
            dto.setAssignmentDeadline(ass.getAssignmentDeadline());
            dto.setSubmitted(ass.getSubmitted());
        }

        AttendanceSummaryDTO attend = studentCourseMapper.selectAttendanceSummary(courseNo, studentUserNo);
        if (attend != null) {
            dto.setAttendanceCount(attend.getAttendanceCount());
            dto.setAbsentCount(attend.getAbsentCount());
            dto.setLateCount(attend.getLateCount());
            dto.setAttendanceRate(attend.getAttendanceRate());
        }

        GradeSummaryDTO grade = studentCourseMapper.selectGradeSummary(courseNo, studentUserNo);
        if (grade != null) {
            dto.setGradeValue(grade.getGradeValue());
            dto.setGradeScore(grade.getGradeScore());
        }

        return dto;
    }

    // 내 수강 과목 조회
    public List<StudentCourseDTO> getMyCourseList(int studentUserNo) {
        return studentCourseMapper.selectMyCourseList(studentUserNo);
    }

    // 강의 상세 조회
    public StudentCourseDetailDTO getStudentCourseDetail(int courseNo) {
        return studentCourseMapper.selectStudentCourseDetail(courseNo);
    }

    // 학생 시간표 조회
    public List<StudentTimetableDTO> getStudentTimetable(int studentUserNo) {
        return studentCourseMapper.selectStudentTimetable(studentUserNo);
    }

    // 수강신청 필터 목록 조회
    public List<StudentCourseDTO> getCourseListForStudentFiltered(
            int studentUserNo, Integer yoil, String professor, String deptCode, int startRow, int rowPerPage) {

        return studentCourseMapper.selectCourseListForStudentFiltered(
                studentUserNo, yoil, professor, deptCode, startRow, rowPerPage);
    }

    // 필터링된 강의 목록 개수 조회
    public int countFilteredCourseList(Integer yoil, String professor, String deptCode) {
        return studentCourseMapper.countCourseListFiltered(yoil, professor, deptCode);
    }

    // 학과 목록 조회
    public List<DeptDTO> getDeptList() {
        return studentCourseMapper.selectDeptList();
    }

    // 학생 과제 목록 조회
    public List<StudentAssignmentListDTO> getAssignmentList(int courseNo, int studentUserNo) {
        return studentCourseMapper.selectAssignmentList(courseNo, studentUserNo);
    }

    // 학생 질문 전체 목록 조회 + 페이징 + 비밀글 처리
    public Map<String, Object> getStudentQuestionList(
            int courseNo, int studentUserNo, int startRow, int rowPerPage) {

        Map<String, Object> result = new HashMap<>();

        int totalRow = courseQuestionMapper.countQuestion(courseNo);

        int lastPage = (totalRow == 0) ? 1 : ((totalRow - 1) / rowPerPage + 1);

        int pageGroup = (startRow / rowPerPage) / 5;
        int startPage = pageGroup * 5 + 1;
        int endPage = Math.min(startPage + 4, lastPage);

        List<Map<String, Object>> pageList = new ArrayList<>();
        for (int i = startPage; i <= endPage; i++) {
            Map<String, Object> map = new HashMap<>();
            map.put("page", i);
            map.put("current", false);
            pageList.add(map);
        }

        List<CourseQuestionDTO> list =
                courseQuestionMapper.selectPagedQuestionList(courseNo, startRow, rowPerPage);

        int displayIndex = totalRow - startRow;

        for (CourseQuestionDTO q : list) {

            q.setIndex(displayIndex--);

            boolean isPrivate = (q.getPrivatePost() == 1);
            boolean isWriter = q.getWriterUserNo() == studentUserNo;

            boolean canView = !isPrivate || isWriter;
            q.setCanView(canView);

            if (!canView) {
                q.setCourseQuestionTitle("비밀글입니다.");
                q.setWriterName("비공개");
            }
        }

        result.put("list", list);
        result.put("pageList", pageList);
        result.put("lastPage", lastPage);

        return result;
    }

    // 강의 헤더 조회
    public StudentCourseDetailDTO getStudentCourseHeaderInfo(int courseNo, int studentUserNo) {
        return studentCourseMapper.selectStudentCourseHeaderInfo(courseNo, studentUserNo);
    }
}
