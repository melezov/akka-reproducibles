@echo off
setlocal
pushd
cd "%~dp0"

set JVM_PARAMS=-Xss2m -Xmx2g -XX:+CMSClassUnloadingEnabled

set LOG_LEVEL=
set NO_PAUSE=false
set DO_LOOP=false

:PARSER_LOOP
if "%~1"=="" goto :PARSER_END

if "%~1"=="--loop" (
  set DO_LOOP=true
  goto :PARSER_CONTINUE
)

if "%~1"=="--no-pause" (
  set NO_PAUSE=true
  goto :PARSER_CONTINUE
)

set SBT_PARAMS=%SBT_PARAMS% %1

:PARSER_CONTINUE
shift
goto :PARSER_LOOP
:PARSER_END

set GRUJ_PATH=project\strap\gruj_vs_sbt-launch-0.13.x.jar
set RUN_CMD=java %JVM_PARAMS% -jar %GRUJ_PATH% %LOG_LEVEL% %SBT_PARAMS%

:RUN_LOOP
%RUN_CMD%

if %DO_LOOP%.==true. (
  if %NO_PAUSE%.==false. (
    echo Press Enter to continue or Press CTRL+C to exit!
    pause
  )
  goto :RUN_LOOP
)

popd
endlocal
