![image](https://github.com/user-attachments/assets/2b6d5ce9-b693-428a-bfaa-9fcce0b9afa2)
# Yourun Back-End Server Project

> **챌린지 기반 비대면 러닝 메이트 매칭 Android 서비스**

## 📋 Project Info

**🔄 Fork Information**
- **Original Repository**: [UMC-YouRun/YouRun_Server](https://github.com/UMC-YouRun/YouRun_Server)
- **Forked by**: [이정원](https://github.com/leegaarden)
- **Fork Purpose**: 개인 포트폴리오 및 기능 개선
- **Fork Date**: 2025.09.02

## 🛠️ Tech Stack

- **Backend**: Java 17, Spring Boot 3.2.2
- **Build Tool**: Gradle 8.0
- **Database**: MySQL 8.0
- **Authentication**: Spring Security + JWT
- **Documentation**: Swagger/OpenAPI 3.0

## 🌟 My Contributions & Improvements

### ✨ 개발한 기능
- ⭐ **자동 매칭 서비스** -  @Scheduled를 사용한 데이터 자동 관리 및 자동 매칭 서비스
- 🏃‍➡️ **챌린지 관련 API 전체 개발** - 서비스 내 개인 챌린지 및 크루 챌린지 전체 관리 및 개발

### 🔧 개선한 부분
- 관계 테이블을 도입하여 이후 비즈니스 수정에서 유연하게 대처
- @RestControllerAdvice 기반 전역 예외 처리 아키텍처를 도입하여 에러 핸들링 개선
- 커스넘 어노테이션을 통해 검증 로직 개선 

## 🚀 Quick Start

```bash
# 1. 저장소 클론
git clone https://github.com/leegaarden/yourun.git
cd yourun

# 2. MySQL 데이터베이스 설정
mysql -u root -p
CREATE DATABASE your_database_name CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 3. 환경설정 파일 복사 및 수정
cp src/main/resources/application.yml.example src/main/resources/application.yml
# application.yml에서 데이터베이스 정보 수정

# 4. 빌드 및 실행
./gradlew clean build
./gradlew bootRun
```

**✅ 실행 확인**
- 서버: http://localhost:8080
- API 문서: http://localhost:8080/swagger-ui/index.html

## 🔀 Git Flow Strategy

우리 팀은 **Git Flow** 브랜치 전략을 사용하여 체계적으로 개발했습니다.

### 브랜치 구조
```
main (배포용)
├── develop (개발 메인)
│   ├── feature/login (사용자 인증)
│   ├── feature/discord (결제 실패 알림)
│   └── feature/kakaopay (카카오 정기 결제)
├── release/v1.0.0 (릴리즈 준비)
├── release/v1.1.0
└── hotfix/token-renewal (토큰 갱신 문제 긴급 해)
```

### 브랜치별 역할
- **`main`**: 배포 가능한 안정적인 코드
- **`develop`**: 다음 릴리즈를 위한 개발 브랜치
- **`feature/*`**: 새로운 기능 개발
- **`hotfix/*`**: 배포 후 긴급 버그 수정

### 워크플로우
1. `develop`에서 `feature/기능명` 브랜치 생성
2. 기능 개발 완료 후 `develop`으로 Pull Request
3. 코드 리뷰 후 `develop`에 merge
4. 테스트 완료 후 `main`과 `develop`에 merge

## 📚 API Documentation

### 🔑 Authentication
```bash
POST /api/v1/auth/token
GET  /api/v1/auth/refresh
GET  /api/v1/auth/check-refresh-token
```

### 👤 User
```bash
PUT    /api/v1/users/notification          # 사용자 알림 설정 변경
PUT    /api//v1/users/name                 # 사용자 이름 변경
POST   /api/v1/users/logout                # 사용자 로그아웃
GET    /api/v1/users/mypage                # 사용자 마이페이지 조회
GET    /api/v1/users/check                 # 사용자 출석체크
```

### 💸 PaymentMethod
```bash
PUT /api/v1/payment-methods/{id}/default   # 기본 결제 수단 변경
```

**📖 상세 API 명세**: [Swagger UI](http://localhost:8080/swagger-ui/index.html)

<!--
## 📅 Development Log

### 🎯 Phase 1: 기본 구조 구축 (2024.08)
- **프로젝트 초기 설정** (25/08/15) - commit: [a1b2c3d](https://github.com/yourusername/repo/commit/a1b2c3d)
- **Spring Security 설정** (25/08/18) - commit: [e4f5g6h](https://github.com/yourusername/repo/commit/e4f5g6h)
- **MySQL 데이터베이스 연동** (25/08/20) - commit: [i7j8k9l](https://github.com/yourusername/repo/commit/i7j8k9l)

### 🔐 Phase 2: 인증 시스템 구축 (2025.08 ~ 2025.09)
- **JWT 토큰 기반 인증 구현** (25/08/25) - commit: [m1n2o3p](https://github.com/yourusername/repo/commit/m1n2o3p)
- **회원가입 API 완성** (25/08/28) - commit: [q4r5s6t](https://github.com/yourusername/repo/commit/q4r5s6t)
- **로그인 API 완성** (25/09/01) - commit: [u7v8w9x](https://github.com/yourusername/repo/commit/u7v8w9x)
- **토큰 갱신 로직 추가** (25/09/03) - commit: [y1z2a3b](https://github.com/yourusername/repo/commit/y1z2a3b)

### 👥 Phase 3: 사용자 관리 시스템 (2025.09)
- **사용자 CRUD API 구현** (25/09/05) - commit: [c4d5e6f](https://github.com/yourusername/repo/commit/c4d5e6f)
- **프로필 이미지 업로드 기능** (25/09/08) - commit: [g7h8i9j](https://github.com/yourusername/repo/commit/g7h8i9j)
- **사용자 검색 및 필터링** (25/09/10) - commit: [k1l2m3n](https://github.com/yourusername/repo/commit/k1l2m3n)
- **닉네임 중복 검사 API** (25/09/12) - commit: [o4p5q6r](https://github.com/yourusername/repo/commit/o4p5q6r)

### 🔍 Phase 4: 검색 및 최적화 (2025.09)
- **고급 검색 기능 구현** (25/09/15) - commit: [s7t8u9v](https://github.com/yourusername/repo/commit/s7t8u9v)
- **페이징 처리 최적화** (25/09/18) - commit: [w1x2y3z](https://github.com/yourusername/repo/commit/w1x2y3z)
- **캐싱 시스템 도입** (25/09/20) - commit: [a4b5c6d](https://github.com/yourusername/repo/commit/a4b5c6d)
- **API 응답 속도 개선** (25/09/22) - commit: [e7f8g9h](https://github.com/yourusername/repo/commit/e7f8g9h)

### 📋 Phase 5: 문서화 및 테스트 (2025.09)
- **Swagger API 문서화 완성** (25/09/25) - commit: [i1j2k3l](https://github.com/yourusername/repo/commit/i1j2k3l)
- **단위 테스트 코드 작성** (25/09/28) - commit: [m4n5o6p](https://github.com/yourusername/repo/commit/m4n5o6p)
- **통합 테스트 환경 구축** (25/09/30) - commit: [q7r8s9t](https://github.com/yourusername/repo/commit/q7r8s9t)
- **README 문서 정리** (25/10/01) - commit: [u1v2w3x](https://github.com/yourusername/repo/commit/u1v2w3x)

## 🧪 Testing

```bash
# 전체 테스트 실행
./gradlew test

# 테스트 커버리지 확인
./gradlew jacocoTestReport
open build/reports/jacoco/test/html/index.html

# 특정 테스트 실행
./gradlew test --tests "com.yourpackage.UserServiceTest"
```

**현재 테스트 커버리지**: 95% ✅

## 🚀 Deployment

```bash
# Production 빌드
./gradlew bootJar -Pprod

# JAR 실행
java -jar -Dspring.profiles.active=prod build/libs/your-app-0.0.1-SNAPSHOT.jar

# Docker 배포
docker build -t your-app:latest .
docker run -d -p 8080:8080 --name your-app your-app:latest
```
-->

## 🤝 Contributing

1. **Fork** the repository
2. Create your **feature branch** (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add some amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)  
5. Open a **Pull Request**

### 📋 Code Style
- **Google Java Style Guide** 준수
- **SonarLint** 정적 분석 통과
- **최소 90% 테스트 커버리지** 유지

<!--
## 🏆 Achievements

- ⚡ **성능**: API 응답속도 평균 200ms → 50ms (75% 개선)
- 🐛 **품질**: 버그 발생률 5% → 1% (80% 감소)  
- 🧪 **테스트**: 테스트 커버리지 60% → 95% (35% 증가)
- 📚 **문서화**: API 문서 자동화로 문서 최신화 100% 달성
-->
## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

## 👤 Author

**이정원**
- GitHub: [@leegaarden](https://github.com/leegaarden)
- Email: dlwjddnjs081723@gmail.com
- LinkedIn: [Jeong Won Lee](https://www.linkedin.com/in/jeong-won-lee-835b2b281/)
- Blog: [velog](https://velog.io/@leegarden/posts)

## 🙏 Acknowledgments

- 원본 프로젝트: [organization/original-repo](https://github.com/UMC-CARDIFY/Server)
- 팀원들: [@송민](https://github.com/Miging), [@최석운](https://github.com/Choi-seokun), [@이수용](https://github.com/leesuyong849)

---

⭐ **이 프로젝트가 도움이 되었다면 Star를 눌러주세요!**

_This README was crafted with ❤️ for my portfolio_
