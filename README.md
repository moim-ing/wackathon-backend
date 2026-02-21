<div align="center">
  <img src="https://raw.githubusercontent.com/moim-ing/wackathon-frontend/main/public/mucheckPicon.svg"
       alt="뮤첵" width="150" />
  <br />
  <p align="center">
    <img src="https://img.shields.io/badge/Kotlin-1.9.25-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" alt="Kotlin" />
    <img src="https://img.shields.io/badge/Spring%20Boot-3.5.5-6DB33F?style=for-the-badge&logo=spring-boot&logoColor=white" alt="Spring Boot" />
    <img src="https://img.shields.io/badge/MySQL-8.4-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL" />
    <img src="https://img.shields.io/badge/Redis-7.x-DC382D?style=for-the-badge&logo=redis&logoColor=white" alt="Redis" />
    <img src="https://img.shields.io/badge/AWS%20S3-Storage-232F3E?style=for-the-badge&logo=amazon-s3&logoColor=white" alt="AWS S3" />
  </p>
</div>

# 뮤첵 서버

음악 출석체크 서비스를 위한 백엔드 서버입니다. Kotlin + Spring Boot 기반으로 클래스/세션/출석(참여) 관리와 FastAPI 연동을 제공합니다.

## 주요 기능

- 클래스 생성 및 상세 조회, 내 클래스 목록 조회
- 세션 생성/조회/상태 변경 (videoId 기반)
- FastAPI 연동을 통한 음악 추출 및 `sourceKey` 생성
- 출석(참여) 검증 API (`audioFile`/`recordedAt`)
- JWT 기반 로그인/로그아웃 (Redis 블랙리스트)
- 사용자 정보 조회/수정
- Swagger(OpenAPI) 문서 제공

## 기술 스택

- Kotlin 1.9.25, Spring Boot 3.5.5
- MySQL 8.4, Redis 7.x
- Spring Data JDBC, Flyway
- JWT, Springdoc OpenAPI, WebFlux(WebClient)
- AWS SDK S3

## 로컬 실행

### 사전 준비

- Java 17
- Docker (MySQL, Redis)

### 인프라 실행

```bash
docker-compose up -d
```

### 애플리케이션 실행

```bash
./gradlew bootRun
```

기본 포트는 8080입니다.

## 환경 변수 / 설정

`src/main/resources/application.yaml`에 기본값이 있으며, 아래 환경 변수로 덮어쓸 수 있습니다.

- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `SPRING_DATA_REDIS_HOST`
- `SPRING_DATA_REDIS_PORT`
- `JWT_SECRET`
- `JWT_EXPIRATION_IN_MS`
- `CORS_ALLOWED_ORIGINS`
- `AWS_REGION`
- `AWS_S3BUCKET`
- `FASTAPI_BASEURL`
- `FASTAPI_TIMEOUTSECONDS`

AWS S3 업로드를 위해 표준 AWS 자격 증명(`AWS_ACCESS_KEY_ID`, `AWS_SECRET_ACCESS_KEY`)이 필요합니다.

## API 문서

Springdoc OpenAPI가 활성화되어 있습니다. 로컬 실행 후 아래 경로에서 확인할 수 있습니다.

- `/swagger-ui/index.html`

## 데이터베이스 마이그레이션

Flyway를 사용하며, SQL 파일은 다음 경로에 있습니다.

- `src/main/resources/db/migration`

## 폴더 구조

- `src/main/kotlin/com/wafflestudio/spring2025/config`: 설정 및 공통 구성
- `src/main/kotlin/com/wafflestudio/spring2025/common`: 공통 모듈 및 예외
- `src/main/kotlin/com/wafflestudio/spring2025/domain/auth`: 인증/인가
- `src/main/kotlin/com/wafflestudio/spring2025/domain/user`: 사용자
- `src/main/kotlin/com/wafflestudio/spring2025/domain/classes`: 클래스
- `src/main/kotlin/com/wafflestudio/spring2025/domain/sessions`: 세션
- `src/main/kotlin/com/wafflestudio/spring2025/domain/participation`: 출석(참여)
- `src/main/kotlin/com/wafflestudio/spring2025/integration/fastapi`: FastAPI 연동
- `src/main/kotlin/com/wafflestudio/spring2025/infra`: 인프라(S3)

## 배포

`docker-compose.prod.yml` 기준으로 MySQL/Redis와 함께 배포할 수 있습니다. 필요한 환경 변수를 지정한 뒤 실행하세요.

## 기여자

|                                                         윤찬규                                                         | 홍지수 |                                                               정성원                                                               |
|:-------------------------------------------------------------------------------------------------------------------:| :---: |:-------------------------------------------------------------------------------------------------------------------------------:|
| <a href="https://github.com/uykhc"><img src="https://github.com/uykhc.png" width="120" alt="uykhc"/></a><br/>@uykhc | <a href="https://github.com/jaylovegood"><img src="https://github.com/jaylovegood.png" width="120" alt="jaylovegood"/></a><br/>@jaylovegood | <a href="https://github.com/cjwjeong"><img src="https://github.com/cjwjeong.png" width="120" alt="cjwjeong"/></a><br/>@cjwjeong |
|                                                    events, image                                                    | auth, user |                                                      registrations, email                                                       |
