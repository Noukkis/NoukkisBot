@ECHO OFF

:bot
xcopy build\libs\noukkisBot.jar wrk\
java -jar wrk\noukkisBot.jar
set exitcode=%ERRORLEVEL%
del wrk\noukkisBot.jar

if %exitcode% == 15 goto :restart
goto :end

:restart
echo restarting bot
goto :bot

:end
echo bot stopped
pause