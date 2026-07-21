@REM Maven Wrapper for Windows
@setlocal
@set MAVEN_PROJECTBASEDIR=%CD%
@set MAVEN_OPTS=-Xmx1024m
@if not defined JAVA_HOME (
  for %%i in (java.exe) do set JAVA_EXEC=%%~$PATH:i
) else (
  set JAVA_EXEC=%JAVA_HOME%\bin\java.exe
)
@"%JAVA_EXEC%" --enable-native-access=ALL-UNNAMED -Dmaven.multiModuleProjectDirectory="%MAVEN_PROJECTBASEDIR%" -cp "%MAVEN_PROJECTBASEDIR%\.mvn\wrapper\maven-wrapper.jar" org.apache.maven.wrapper.MavenWrapperMain %*
@endlocal