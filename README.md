# Cloud Architecture Project
(CH4 클라우드 아키텍처 설계 & 배포)

Spring Boot 기반 애플리케이션을 AWS 인프라 위에서 실행하고  
Docker와 GitHub Actions를 활용하여 **CI/CD 자동 배포 파이프라인**을 구축한 프로젝트입니다.

---

# Tech Stack

### Backend
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/SpringBoot-3.3-green)
![Spring Data JPA](https://img.shields.io/badge/Spring_Data_JPA-blue)
![Hibernate](https://img.shields.io/badge/Hibernate-brown)
![Validation](https://img.shields.io/badge/Spring-Validation-brightgreen)

### Database
![MySQL](https://img.shields.io/badge/MySQL-RDS-blue)

### Cloud
![AWS](https://img.shields.io/badge/AWS-Cloud-orange)
![EC2](https://img.shields.io/badge/AWS-EC2-red)
![RDS](https://img.shields.io/badge/AWS-RDS-blue)
![S3](https://img.shields.io/badge/AWS-S3-yellow)
![Parameter Store](https://img.shields.io/badge/AWS-ParameterStore-green)

### DevOps
![Docker](https://img.shields.io/badge/Docker-Container-blue)
![DockerHub](https://img.shields.io/badge/Docker-Hub-2496ED)
![GitHub Actions](https://img.shields.io/badge/GitHub-Actions-black)

### API Test
![Postman](https://img.shields.io/badge/Postman-API_Test-orange)

---

# Architecture

GitHub에 코드가 push 되면 GitHub Actions가 실행되어 애플리케이션을 빌드하고  
Docker 이미지를 생성한 뒤 Docker Hub에 push합니다.

EC2에서는 해당 이미지를 pull하여 컨테이너를 실행하도록 구성하였습니다.

```text
Developer
   │
   │ Git Push
   ▼
GitHub Repository
   │
   ▼
GitHub Actions
   │
   │ Build & Test
   │ Docker Image Build
   ▼
Docker Hub
   │
   │ docker pull
   ▼
AWS EC2 (Docker Container)
   │
   ▼
Spring Boot Application
   │
   ├── AWS RDS (MySQL)
   └── AWS S3 (Profile Image Storage)
```

---

# LV0 - AWS Budget

요금 폭탄 방지를 위해 AWS Budget을 설정하였습니다.

- 월 예산 : $100
- 80% 도달 시 이메일 알림 설정

### Budget 설정 화면
<img width="840" height="532" alt="스크린샷 2026-03-13 오전 11 06 51" src="https://github.com/user-attachments/assets/d0c17e56-55d6-4985-a226-43ae7653e09f" />


---

# LV1 - 네트워크 구축 및 EC2 배포

## VPC 설계

- VPC 생성
- Public / Private Subnet 분리
- Public Subnet에 EC2 배포

## Actuator Health Check

```
http://54.180.233.6:8080/actuator/health
```

응답

```json
{
  "status": "UP"
}
```

<img width="590" height="579" alt="스크린샷 2026-03-11 오후 10 34 19" src="https://github.com/user-attachments/assets/0a012eab-e28f-46e7-80d9-cd2e62163b0c" />

---

# LV2 - DB 분리 및 Parameter Store

## RDS (MySQL)

애플리케이션 DB를 AWS RDS로 분리하여 구성했습니다.

## Parameter Store

DB 접속 정보를 AWS Parameter Store에 저장하고  
Spring Boot 실행 시 해당 값을 읽어오도록 설정했습니다.

### Actuator Info

```
http://54.180.233.6:8080/actuator/info
```

응답 예시

```json
{
  "team-name": "sparta-cloud-team"
}
```
<img width="573" height="587" alt="스크린샷 2026-03-12 오후 3 16 27" src="https://github.com/user-attachments/assets/7d38e600-a633-4c73-9e4d-6b74df08f6eb" />



### RDS Security Group

RDS 인바운드 규칙에서 EC2 보안 그룹만 허용하도록 설정했습니다.

<img width="1197" height="378" alt="스크린샷 2026-03-13 오전 11 17 52" src="https://github.com/user-attachments/assets/edf71e44-a6ce-42fb-8b81-aae612985c27" />


---

# LV3 - S3 프로필 이미지 업로드

프로필 이미지를 서버 디스크가 아닌 AWS S3에 저장하도록 구현했습니다.

## API

### 이미지 업로드
```
POST /api/members/{id}/profile-image
```

### 이미지 조회
```
GET /api/members/{id}/profile-image
```

## Presigned URL

```
https://cloud-architecture-jimin-profile.s3.ap-northeast-2.amazonaws.com/members/{id}/{uuid}.jpeg
```

Expiration

```
7 days
```  

<img width="1188" height="713" alt="스크린샷 2026-03-12 오후 4 20 03" src="https://github.com/user-attachments/assets/f62540bd-4cba-4f1b-be60-2e6fa4f7d1c9" />



---

# LV4 - Docker & CI/CD

## Docker

Spring Boot 애플리케이션을 Docker 컨테이너로 실행하도록 구성했습니다.

### Dockerfile

```dockerfile
FROM eclipse-temurin:17-jre

WORKDIR /app

COPY build/libs/cloud-architecture-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar", "--spring.profiles.active=prod"]
```

---

# CI/CD Pipeline

GitHub Actions를 이용하여 자동 배포 환경을 구축했습니다.

## CI

- main 브랜치 push 시 자동 실행
- Gradle Build 수행

## CD

- Docker Image Build
- Docker Hub Push
- EC2에서 Docker Pull 후 컨테이너 실행

---

# Deployment Verification

## GitHub Actions Success

<img width="1128" height="553" alt="스크린샷 2026-03-13 오전 10 48 51" src="https://github.com/user-attachments/assets/ee6c3b14-561c-4a3e-8348-1502d9568a68" />


---

## EC2 Docker Container

```
sudo docker ps
```
<img width="566" height="101" alt="스크린샷 2026-03-13 오전 10 45 41" src="https://github.com/user-attachments/assets/56563898-5e95-4896-9299-3ae613604d4b" />



---

# API Test

## Member 생성

```
POST /api/members
```
<img width="578" height="564" alt="스크린샷 2026-03-11 오후 10 30 51" src="https://github.com/user-attachments/assets/ad356dcc-9676-44bd-a266-6ab0509bed4b" />

<img width="574" height="594" alt="스크린샷 2026-03-12 오전 11 08 54" src="https://github.com/user-attachments/assets/de5b400c-f40f-4991-b6b6-170867fe945e" />


## Member 조회

```
GET /api/members/{id}
```

<img width="580" height="613" alt="스크린샷 2026-03-11 오후 10 31 48" src="https://github.com/user-attachments/assets/c6968483-43d5-46f3-9317-e21fee17e9c8" />
<img width="574" height="592" alt="스크린샷 2026-03-12 오전 11 09 50" src="https://github.com/user-attachments/assets/6e13841b-bab3-4d81-8251-418cd09b78c0" />


---

# Conclusion

- AWS EC2, RDS, S3 기반 클라우드 아키텍처 구축
- Docker를 이용한 애플리케이션 컨테이너화
- GitHub Actions를 이용한 CI/CD 자동 배포 파이프라인 구축
- 코드 Push만으로 서버에 자동 배포되는 환경 구성


---  
## Velog 링크  
[필수기능] https://velog.io/@jiiim_ni/%EB%82%B4%EC%9D%BC%EB%B0%B0%EC%9B%80%EC%BA%A0%ED%94%84-Spring-3%EA%B8%B0-CH-4-%ED%81%B4%EB%9D%BC%EC%9A%B0%EB%93%9C%EC%95%84%ED%82%A4%ED%85%8D%EC%B2%98-%EC%84%A4%EA%B3%84-%EB%B0%B0%ED%8F%AC-%ED%95%84%EC%88%98-%EA%B8%B0%EB%8A%A5  
[도전기능] https://velog.io/@jiiim_ni/%EB%82%B4%EC%9D%BC%EB%B0%B0%EC%9B%80%EC%BA%A0%ED%94%84-Spring-3%EA%B8%B0-CH4-%ED%81%B4%EB%9D%BC%EC%9A%B0%EB%93%9C%EC%95%84%ED%82%A4%ED%85%8D%EC%B2%98-%EC%84%A4%EA%B3%84-%EB%B0%B0%ED%8F%AC-%EB%8F%84%EC%A0%84%EA%B8%B0%EB%8A%A5
