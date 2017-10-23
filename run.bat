@ECHO OFF
set restartcode=15
:s
call gradle.bat run
if %ERRORLEVEL% == 1 goto :s

REM :start
REM xcopy /Y build\libs\noukkisBot.jar run\
REM java -jar run\noukkisBot.jar
REM if %ERRORLEVEL% == %restartcode% goto :start