# 컨테이너의 베이스 이미지
FROM openjdk:17-jdk-slim
# 작성자 정보
LABEL maintainer="JGD"
# 도커 내 디렉토리 설정
WORKDIR /app/user
## 소스 코드 복사
COPY . .
# Gradle 클린 빌드 수행
RUN ./gradlew clean build -x test
# 프로젝트 war빌드 파일 복사
COPY build/libs/user-0.0.1-SNAPSHOT.war app.war
## java시간대를 서울 기준으로 환경 설정
ENV JAVA_OPTS="-Duser.timezone=Asia/Seoul"
# 도커 시간대 서울 기준으로 변경
RUN ln -fs /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    dpkg-reconfigure --frontend noninteractive tzdata
# 외부에서 사용할 포트 번호 지정
EXPOSE 8080
# 컨테이너 동작 시 자동으로 실행 할 서비스나 스크립트
ENTRYPOINT ["java", "-jar", "app.war"]

## ---- Build Stage ----
#FROM gradle:7.6-jdk17 AS builder
#
## 작성자 정보
#LABEL maintainer="JGD"
#
## Gradle 빌드 컨텍스트 설정
#WORKDIR /app
#
## 소스 코드 복사 및 빌드
#COPY . .
#RUN ./gradlew clean build -x test
#
## gradlew 실행 권한 부여
#RUN chmod +x ./gradlew
#
## ---- Run Stage ----
#FROM openjdk:17-jdk-slim
#
## 워킹 디렉토리 설정
#WORKDIR /app/user
#
## WAR 파일 복사
#COPY --from=builder /app/build/libs/user-0.0.1-SNAPSHOT.war app.war
#
## Java 시간대 환경 설정
#ENV JAVA_OPTS="-Duser.timezone=Asia/Seoul"
#
## 도커 시간대 서울 기준으로 변경
#RUN ln -fs /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
#    dpkg-reconfigure --frontend noninteractive tzdata
#
## 외부에서 사용할 포트 번호 지정
#EXPOSE 8080
#
## 컨테이너 동작 시 자동으로 실행할 서비스
#ENTRYPOINT ["java", "-jar", "app.war"]
