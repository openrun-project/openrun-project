#!/bin/bash

PROJECT_NAME="app"
JAR_PATH="/home/ubuntu/app/build/libs/app.jar"
DEPLOY_PATH=/home/ubuntu/$PROJECT_NAME/

# 배포 log 파일 설정
DEPLOY_LOG_PATH="/home/ubuntu/$PROJECT_NAME/deploy.log"

# 배포가 실패한 이유를 보여주는 log 파일 설정
DEPLOY_ERR_LOG_PATH="/home/ubuntu/$PROJECT_NAME/deploy_err.log"

# application에서 발생한 로그를 쌓는 파일 설정
APPLICATION_LOG_PATH="/home/ubuntu/$PROJECT_NAME/application.log"
BUILD_JAR=$(ls $JAR_PATH)
JAR_NAME=$(basename $BUILD_JAR)

# >> : $DEPLOY_LOG_PATH 에 echo 명령어를 통해 로그를 찍어준다.
echo "===== 배포 시작 : $(date +%c) =====" >> $DEPLOY_LOG_PATH

echo "> build 파일명: $JAR_NAME" >> $DEPLOY_LOG_PATH
echo "> build 파일 복사" >> $DEPLOY_LOG_PATH
cp $BUILD_JAR $DEPLOY_PATH

echo "> 현재 동작중인 어플리케이션 pid 체크" >> $DEPLOY_LOG_PATH
CURRENT_PID=$(pgrep -f $JAR_NAME)

if [ -z $CURRENT_PID ]
then
  echo "> 현재 동작중인 어플리케이션 존재 X" >> $DEPLOY_LOG_PATH
else
  echo "> 현재 동작중인 어플리케이션 존재 O" >> $DEPLOY_LOG_PATH
  echo "> 현재 동작중인 어플리케이션 강제 종료 진행" >> $DEPLOY_LOG_PATH
  echo "> kill -9 $CURRENT_PID" >> $DEPLOY_LOG_PATH
  kill -9 $CURRENT_PID
fi

DEPLOY_JAR=$DEPLOY_PATH$JAR_NAME
echo "> DEPLOY_JAR 배포" >> $DEPLOY_LOG_PATH

# 2. multi module 구조라면, profiles.active=설정 적용
nohup java -jar -Dspring.profiles.active=prod $DEPLOY_JAR >> $APPLICATION_LOG_PATH 2> $DEPLOY_ERR_LOG_PATH &


sleep 3

echo "> 배포 종료 : $(date +%c)" >> $DEPLOY_LOG_PATH