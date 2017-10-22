@ECHO OFF
set restartcode=15

:start
xcopy /Y build\libs\noukkisBot.jar run\
java -jar run\noukkisBot.jar
if %ERRORLEVEL% == %restartcode% goto :start