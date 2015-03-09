@rem ***************************************************************************
@rem  Copyright 2014 Paxcel Technologies
@rem
@rem  Licensed under the Apache License, Version 2.0 (the "License");
@rem  you may not use this file except in compliance with the License.
@rem  You may obtain a copy of the License at
@rem
@rem  http://www.apache.org/licenses/LICENSE-2.0
@rem
@rem  Unless required by applicable law or agreed to in writing, software
@rem  distributed under the License is distributed on an "AS IS" BASIS,
@rem  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
@rem  See the License for the specific language governing permissions and
@rem  limitations under the License.
@rem ***************************************************************************
@echo off

rem Boxupp startup script 
rem www.boxupp.com
rem Powered by Paxcel Technologies Pvt Ltd
rem Copyright 2013 Paxcel Technologies Pvt Ltd


SETLOCAL ENABLEEXTENSIONS
IF ERRORLEVEL 1 ECHO Unable to enable extensions GOTO end

rem *** Check Vagrant is installed or not

if not "x%PATH:Vagrant=%" == "x%PATH%" goto check_virtualbox
wget  http://www.boxupp.com/data/vagrant.msi
IF errorlevel 1 goto vagrantend
vagrant.msi

rem ***Check Virtual Box is installed in machine or not

:check_virtualbox
REG QUERY HKEY_LOCAL_MACHINE\SOFTWARE\Oracle\VirtualBox >nul 2>&1

IF %errorlevel%==0 GOTO up
if %errorlevel%==1 GOTO install_virtalbox



rem *** Install Virtual Box

:install_virtalbox
echo VirtualBox is not installed installing VirtualBox
wget  http://www.boxupp.com/data/VirtualBox-4.3.exe

rem IF %errorlevel%==0 GOTO up
IF errorlevel 1 goto virtualboxbend
VirtualBox-4.3.exe

rem Boxupp startup script
 
:up
echo Starting BoxUpp
cd ..
SET "BASEDIR="%cd%""

start java -Dfile.encoding=UTF-8 -cp %BASEDIR%;%BASEDIR%\lib\*;%BASEDIR%\config; com.boxupp.init.Boxupp

@echo OFF
for /f "delims=" %%i in ('findstr /i /c:"<portNumber>" config\config.xml') do call     :job "%%i"
goto :eof

:job

set port=%1

set port=%port:/=%
set port=%port:<=+%
set port=%port:>=+%
set port=%port:*+portNumber+=%
set port=%port:+=&rem.%
Setlocal EnableDelayedExpansion
set "host=http://localhost:" 
set "url=%host%%port%"
echo Boxupp is up at %url% !
endlocal

pause


:eof
:virtualboxbend
echo Error downloading Oracle VirtualBox Please Check Your Network
pause

:vagrantend
echo Error downloading Vagrant system Please Check Your Network
pause

:end

