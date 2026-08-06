# 🎓 LMS (학습 관리 시스템)

> **데이터 정합성과 자동 계산 구조를 설계한 학습 관리 시스템**

> **개발 기간:** 2025.11.21 ~ 2025.12.12 (3인 팀 프로젝트)

> **GitHub:** [https://github.com/suhyeonjo-71/GDJ95_LMS_Y_Team](https://github.com/suhyeonjo-71/GDJ95_LMS_Y_Team)

---

## 📌 프로젝트 소개

강의 관리, 과제 제출, 출석 및 성적 산출 등 학사 운영에 필요한 주요 프로세스를 다루는 대학 LMS 플랫폼입니다. 과제 점수와 출석 데이터를 기반으로 최종 성적이 자동으로 집계·갱신되는 데이터 정합성 중심의 구조를 구현하였습니다.

---

## 🛠 Tech Stack

### Backend
`Java 17` `Spring MVC` `MyBatis`

### Frontend
`JSP` `HTML5` `CSS3` `jQuery` `Ajax`

### Database & Infra
`MySQL 8.0` `Maven` `Git` `GitHub`

---

## 👨‍💻 본인 담당 역할 및 핵심 성과 (교수 기능 단독 구현)

### 1. 강의 등록 - 시간 중복 방지 및 원자적 트랜잭션 처리
- **해결:** 서비스 계층에서 중복 시간 사전 검증 후 `@Transactional`로 강의·시간 데이터를 원자적 저장, `useGeneratedKeys`로 생성된 PK를 하위 테이블 FK로 즉시 바인딩
- 🔗 **[핵심 구현 코드 파일 보기 (`ProfessorCourseServiceImpl.java`)](https://github.com/suhyeonjo-71/GDJ95_LMS_Y_Team/blob/main/src/main/java/com/lms/service/ProfessorCourseServiceImpl.java)**

### 2. 강의별 과제 관리 - 성적 자동 반영 구조 설계
- **해결:** 과제 점수 저장 시 `recalculateAndSaveFinalGrade()` 즉시 호출, 출석률·과제 평균·시험 점수를 SQL로 재집계하여 3개 테이블 데이터를 1쿼리로 처리 및 성적 테이블 자동 갱신
- 🔗 **[핵심 구현 코드 파일 보기 (`ProfessorAssignmentServiceImpl.java`)](https://github.com/suhyeonjo-71/GDJ95_LMS_Y_Team/blob/main/src/main/java/com/lms/service/ProfessorAssignmentServiceImpl.java)**

### 3. 출석 관리 - UPSERT 단일 쿼리 및 DB 집계 최적화
- **해결:** `ON DUPLICATE KEY UPDATE`로 UPSERT 단일 쿼리 처리, SQL `SUM`·`CASE` 구문으로 출석률을 DB에서 직접 계산하고 `COALESCE`로 NULL 기본값 처리
- 🔗 **[핵심 구현 쿼리 파일 보기 (`ProfessorAttendanceMapper.xml`)](https://github.com/suhyeonjo-71/GDJ95_LMS_Y_Team/blob/main/src/main/resources/mapper/ProfessorAttendanceMapper.xml)**

### 4. 성적 관리 - NPE 방지 및 중복 저장 방지
- **해결:** `COALESCE` 다중 적용으로 NPE 사전 방지, `existsGrade()`로 성적 존재 여부 확인 후 INSERT/UPDATE 분기하여 데이터 중복 저장 방지
- 🔗 **[핵심 구현 코드 파일 보기 (`ProfessorGradeServiceImpl.java`)](https://github.com/suhyeonjo-71/GDJ95_LMS_Y_Team/blob/main/src/main/java/com/lms/service/ProfessorGradeServiceImpl.java)**
---

## 💡 한계점 및 개선 방향

- **권한 검증 공통화:** 메서드별 중복된 권한 검사 로직을 Spring Interceptor 기반 공통 검증 레이어로 이전하여 코드 중복 제거 예정
- **성적 산출 비율 가변화:** 강의 테이블(`tb_course`)에 비율 컬럼을 추가하여 교수가 직접 성적 반영 비율(출석/과제/시험)을 설정할 수 있도록 개선 예정
- **테스트 자동화:** 수동 기능 검증 방식을 개선하기 위해 JUnit / Mockito 기반 Service 레이어 단위 테스트 작성 예정
