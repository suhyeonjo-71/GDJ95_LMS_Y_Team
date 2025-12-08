package com.example.lms.service.studentCourse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.example.lms.mapper.courseQuestion.CourseQuestionMapper;
import com.example.lms.mapper.studentCourse.StudentCourseMapper;

@Service
public class StudentCourseService {

    @Autowired
    private StudentCourseMapper studentCourseMapper;
    
 // 출석 요약 (출석/지각/결석 + 출석률 + 비율)
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
        
        if (summary.getPresentRate() == null) summary.setPresentRate(0.0);
        if (summary.getLateRate() == null) summary.setLateRate(0.0);
        if (summary.getAbsentRate() == null) summary.setAbsentRate(0.0);
        
        return summary;
    }

    // 1~15주차 전체 출석 상세 목록
    public List<StudentAttendanceDTO> getAttendanceDetailList(int courseNo, int studentUserNo) {
        return studentCourseMapper.selectAttendanceDetailList(courseNo, studentUserNo);
    }

    // ---------------------------------------------------------
    // 최근 질문 조회 + 비밀글 처리
    // ---------------------------------------------------------
    public List<StudentQuestionDTO> getRecentQuestionList(int courseNo, int studentUserNo) {

        List<StudentQuestionDTO> list = studentCourseMapper.selectRecentQuestions(courseNo);

        for (StudentQuestionDTO q : list) {

            boolean isPrivate = Boolean.TRUE.equals(q.getPrivatePost());
            boolean isWriter = q.getWriterUserNo() == studentUserNo;

            boolean canView = !isPrivate || isWriter;
            q.setCanView(canView);

            // 🔥 답변 여부 → answerCount > 0 로 true/false 세팅
            q.setAnswered(q.getAnswerCount() > 0);

            if (!canView) {
                q.setQuestionTitle("비밀글입니다.");
            }
        }

        return list;
    }

    // ---------------------------------------------------------
    // 공지 목록 + Total + 상세
    // ---------------------------------------------------------
    public List<StudentCourseNoticeDTO> getStudentCourseNoticeList(int courseNo, int startRow, int rowPerPage) {
        return studentCourseMapper.selectStudentCourseNoticeList(courseNo, startRow, rowPerPage);
    }

    public int getStudentCourseNoticeTotal(int courseNo) {
        return studentCourseMapper.selectStudentCourseNoticeTotal(courseNo);
    }

    public StudentCourseNoticeDTO getStudentCourseNoticeDetail(int courseNoticeNo) {

        // 조회수 증가
        studentCourseMapper.updateStudentCourseNoticeViewCount(courseNoticeNo);

        // 상세 조회
        return studentCourseMapper.selectStudentCourseNoticeDetail(courseNoticeNo);
    }

    // ---------------------------------------------------------
    // 강의 홈 화면 정보 (studentCourseHome)
    // ---------------------------------------------------------
    public StudentCourseHomeDTO getStudentCourseHome(int courseNo, int studentUserNo) {

        StudentCourseHomeDTO dto = new StudentCourseHomeDTO();

        // 기본 정보
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

        // 최근 공지 3개
        List<StudentCourseNoticeDTO> notices = studentCourseMapper.selectRecentNotices(courseNo);

        if (notices.size() > 0) {
            dto.setNoticeNo1(notices.get(0).getCourseNoticeNo());
            dto.setNoticeTitle1(notices.get(0).getCourseNoticeTitle());
            dto.setNoticeDate1(notices.get(0).getCreatedate());
        }
        if (notices.size() > 1) {
            dto.setNoticeNo2(notices.get(1).getCourseNoticeNo());
            dto.setNoticeTitle2(notices.get(1).getCourseNoticeTitle());
            dto.setNoticeDate2(notices.get(1).getCreatedate());
        }
        if (notices.size() > 2) {
            dto.setNoticeNo3(notices.get(2).getCourseNoticeNo());
            dto.setNoticeTitle3(notices.get(2).getCourseNoticeTitle());
            dto.setNoticeDate3(notices.get(2).getCreatedate());
        }

        // 과제 요약 1개
        StudentAssignmentListDTO ass = studentCourseMapper.selectAssignmentSummary(courseNo, studentUserNo);
        if (ass != null) {
            dto.setAssignmentNo(ass.getAssignmentNo());
            dto.setAssignmentTitle(ass.getAssignmentTitle());
            dto.setAssignmentDeadline(ass.getAssignmentDeadline());
            dto.setAssignmentSubmitted(ass.getAssignmentSubmitted());
        }

        // 출석 요약
        AttendanceSummaryDTO attend = studentCourseMapper.selectAttendanceSummary(courseNo, studentUserNo);
        if (attend != null) {
            dto.setAttendanceCount(attend.getAttendanceCount());
            dto.setAbsentCount(attend.getAbsentCount());
            dto.setLateCount(attend.getLateCount());
            dto.setAttendanceRate(attend.getAttendanceRate());
        }

        // 성적 요약
        GradeSummaryDTO grade = studentCourseMapper.selectGradeSummary(courseNo, studentUserNo);
        if (grade != null) {
            dto.setGradeValue(grade.getGradeValue());
            dto.setGradeScore(grade.getGradeScore());
        }

        // 최근 질문 3개
        List<StudentQuestionDTO> questions = getRecentQuestionList(courseNo, studentUserNo);

        if (questions.size() > 0) {
            dto.setQuestionNo1(questions.get(0).getQuestionNo());
            dto.setQuestionTitle1(questions.get(0).getQuestionTitle());
            dto.setQuestionDate1(questions.get(0).getCreatedate());
            dto.setQuestionAnswered1(questions.get(0).getAnswered());
        }
        if (questions.size() > 1) {
            dto.setQuestionNo2(questions.get(1).getQuestionNo());
            dto.setQuestionTitle2(questions.get(1).getQuestionTitle());
            dto.setQuestionDate2(questions.get(1).getCreatedate());
            dto.setQuestionAnswered2(questions.get(1).getAnswered());
        }
        if (questions.size() > 2) {
            dto.setQuestionNo3(questions.get(2).getQuestionNo());
            dto.setQuestionTitle3(questions.get(2).getQuestionTitle());
            dto.setQuestionDate3(questions.get(2).getCreatedate());
            dto.setQuestionAnswered3(questions.get(2).getAnswered());
        }
        
        return dto;
    }

    // ---------------------------------------------------------
    // 내 수강과목
    // ---------------------------------------------------------
    public List<StudentCourseDTO> getMyCourseList(int studentUserNo) {
        return studentCourseMapper.selectMyCourseList(studentUserNo);
    }

    // ---------------------------------------------------------
    // 강의 상세
    // ---------------------------------------------------------
    public StudentCourseDetailDTO getStudentCourseDetail(int courseNo) {
        return studentCourseMapper.selectStudentCourseDetail(courseNo);
    }

    // ---------------------------------------------------------
    // 시간표
    // ---------------------------------------------------------
    public List<StudentTimetableDTO> getStudentTimetable(int studentUserNo) {
        return studentCourseMapper.selectStudentTimetable(studentUserNo);
    }

    // ---------------------------------------------------------
    // 수강신청 목록 (필터 + 페이징)
    // ---------------------------------------------------------
    public List<StudentCourseDTO> getCourseListForStudentFiltered(
            int studentUserNo,
            Integer yoil,
            String professor,
            String deptCode,
            int startRow,
            int rowPerPage) {

        return studentCourseMapper.selectCourseListForStudentFiltered(
                studentUserNo, yoil, professor, deptCode, startRow, rowPerPage);
    }

    public int countFilteredCourseList(Integer yoil, String professor, String deptCode) {
        return studentCourseMapper.countCourseListFiltered(yoil, professor, deptCode);
    }

    // ---------------------------------------------------------
    // 학과 목록 (수강신청 필터)
    // ---------------------------------------------------------
    public List<DeptDTO> getDeptList() {
        return studentCourseMapper.selectDeptList();
    }

    // ---------------------------------------------------------
    // 학생 과제 목록 조회
    // ---------------------------------------------------------
    public List<StudentAssignmentListDTO> getAssignmentList(int courseNo, int studentUserNo) {
        return studentCourseMapper.selectAssignmentList(courseNo, studentUserNo);
    }
    
    @Autowired
    private CourseQuestionMapper courseQuestionMapper;

    // ---------------------------------------------------------
    // 학생용 질문 전체 리스트 + 페이징 + 비밀글 처리
    // ---------------------------------------------------------
    public Map<String, Object> getStudentQuestionList(
            int courseNo, int studentUserNo, int startRow, int rowPerPage) {

        Map<String, Object> result = new HashMap<>();

        // 전체 개수
        int totalRow = courseQuestionMapper.countQuestion(courseNo);

        // 페이징 계산
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

        // 리스트 조회
        List<CourseQuestionDTO> list =
                courseQuestionMapper.selectPagedQuestionList(courseNo, startRow, rowPerPage);

        int displayIndex = totalRow - startRow;
        for (CourseQuestionDTO q : list) {

            q.setIndex(displayIndex--);

            boolean isPrivate = q.isPrivatePost();
            boolean isWriter = q.getWriterUserNo() == studentUserNo;

            boolean canView = !isPrivate || isWriter;
            q.setCanView(canView);

            if (!canView) {
                q.setCourseQuestionTitle("비밀글입니다.");
            }
        }

        result.put("list", list);
        result.put("pageList", pageList);
        result.put("lastPage", lastPage);

        return result;
    }

}