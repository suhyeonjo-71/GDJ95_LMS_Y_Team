package com.example.lms.service.prof;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.lms.dto.ProfCourseGradeDTO;
import com.example.lms.mapper.prof.ProfAssignmentMapper;
import com.example.lms.mapper.prof.ProfCourseGradeMapper;

@Service
@Transactional
public class ProfCourseGradeService {
	
	@Autowired
	ProfCourseGradeMapper profCourseGradeMapper;
	
	@Autowired
	ProfAssignmentMapper profAssignmentMapper;
	
	// 기존 데이터 조회
	public ProfCourseGradeDTO getGradeData(int courseNo, int studentUserNo) {
		return profCourseGradeMapper.selectGradeData(courseNo, studentUserNo);
	}
	
	// 성적 저장 
	public void saveGrade(int studentUserNo, int courseNo, 
							double examScore, double assignmentScore, String gradeValue, double finalScore) {
		
		int exists = profCourseGradeMapper.existsGrade(studentUserNo, courseNo);
		
		if(exists > 0) {
			profCourseGradeMapper.updateGrade(studentUserNo, courseNo, examScore, assignmentScore, gradeValue, finalScore);
		} else {
			profCourseGradeMapper.insertGrade(studentUserNo, courseNo, examScore, assignmentScore, gradeValue, finalScore);
		}
		
    }
	
	public void recalculateAndSaveFinalGrade(int courseNo, int assignmentSubmissionNo) {
	        
	        // 과제 제출 번호로 학생 UserNo 조회
	        int studentUserNo = profAssignmentMapper.selectWriterUserNoBySubmissionNo(assignmentSubmissionNo);
	        
	        // 최신 성적 데이터 조회
	        ProfCourseGradeDTO gradeData = getGradeData(courseNo, studentUserNo);
	        
	        // 3. 성적 계산 (최종 점수 및 등급 산출)
	        ProfCourseGradeDTO calculatedDto = calculate(gradeData);
	        
	        // 4. tb_grade 테이블에 최종 결과 저장 
	        saveGrade(
	            calculatedDto.getStudentUserNo(),
	            calculatedDto.getCourseNo(),
	            calculatedDto.getExamScore(),
	            calculatedDto.getAssignmentScore(),
	            calculatedDto.getGradeValue(),
	            calculatedDto.getFinalScore()
	        );
	    }
	
	// 성적 계산
	public ProfCourseGradeDTO calculate(ProfCourseGradeDTO dto) {
		
		// 성적 반영 비율
		int attendanceRatio = 10;
	    int assignmentRatio = 20;
	    int examRatio = 70;
		
		// 출석 점수
		double attendanceScore = dto.getAttendanceRate() / 100 * attendanceRatio;
		// 과제 점수
		double assignmentScoreWeighted = dto.getAssignmentScore() / 100 * assignmentRatio;
		// 시험 점수
		double examScoreWeighted = dto.getExamScore() / dto.getExamMaxScore() * examRatio;
		// 최종 점수
		double finalScore = attendanceScore + assignmentScoreWeighted + examScoreWeighted;
		
		// 최종 등급
		String grade;
		if(finalScore >= 90) grade = "A+";
		else if (finalScore >= 85) grade = "A0";
        else if (finalScore >= 80) grade = "B+";
        else if (finalScore >= 75) grade = "B0";
        else if (finalScore >= 70) grade = "C+";
        else if (finalScore >= 65) grade = "C0";
        else grade = "F";
		
		dto.setAttendanceScore(attendanceScore);
        dto.setAssignmentScoreWeighted(assignmentScoreWeighted);
        dto.setExamScoreWeighted(examScoreWeighted);
        dto.setFinalScore(finalScore);
        dto.setGradeValue(grade); 

        return dto;
	}

}
