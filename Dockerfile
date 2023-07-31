# 자바 버전
FROM openjdk:17

# 도커 파일에서 사용할 변수
ARG JAR_FILE=build/libs/app.jar

EXPOSE 8080

# 해당 프로잭트에 있는 jar파일 복사
COPY ${JAR_FILE} ./app.jar

# 환경 변수 : 여기서는 time zone만 설정
ENV TZ=Asia/Seoul

# 실행할 명령을 기록 (list 형태로 입력)
ENTRYPOINT ["java","-jar", "-Dspring.profiles.active=prod" ,"./app.jar"]