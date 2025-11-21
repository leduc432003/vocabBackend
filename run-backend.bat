@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@echo off
echo.
echo ========================================
echo VocabMaster Backend - Spring Boot
echo ========================================
echo.
echo Starting Spring Boot application...
echo Backend will be available at: http://localhost:8080
echo H2 Console: http://localhost:8080/h2-console
echo.

@REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo ERROR: Java is not installed or not in PATH
    echo Please install Java JDK 17 or higher
    echo Download from: https://www.oracle.com/java/technologies/downloads/
    pause
    exit /b 1
)

@REM Check if Maven is installed
mvn -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Maven is not installed. This is OK - we will use Maven Wrapper.
    echo.
    @REM You can add Maven Wrapper download logic here if needed
    echo Please run: mvn -N io.takari:maven:wrapper
    echo Or install Maven from: https://maven.apache.org/download.cgi
    pause
    exit /b 1
) else (
    echo Maven found. Starting application...
    echo.
    mvn spring-boot:run
)

pause
