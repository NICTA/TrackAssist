@echo off
setlocal

REM hibernate libs included for easy setup on non-dev machines:
set HIBERNATE="..\lib"

REM Add extra memory to run app:
REM %JAVAPATH%\java -Xmx1024m -cp %APACHE%\commons-logging-1.1.jar;%HIBERN%\antlr-2.7.6.jar;%HIBERN%\asm.jar;%HIBERN%\asm-attrs.jar;%HIBERN%\cglib-2.1.3.jar;%HIBERN%\commons-collections-2.1.1.jar;%HIBERN%\dom4j-1.6.1.jar;%HIBERN%\ehcache-1.2.3.jar;%HIBERN%\jdbc2_0-stdext.jar;%HIBERN%\jta.jar;%HIBERN%\hibernate3.jar;%HIBERN%\hibernate-tools.jar;%HIBERN%\hibernate-annotations.jar;%HIBERN%\hibernate-commons-annotations.jar;%HIBERN%\hibernate-entitymanager.jar;%HIBERN%\javassist.jar;%HIBERN%\ejb3-persistence.jar;%POSTGR%\postgresql-8.3-603.jdbc3.jar;%LIB%\ij.jar;%LIB%\ujmp-complete-0.2.5.jar;..\CellTracking.jar au.com.nicta.ct.desktop.Main
java -Xmx1024m -cp %HIBERNATE%\commons-logging-1.1.jar;%HIBERNATE%\antlr-2.7.6.jar;%HIBERNATE%\asm.jar;%HIBERNATE%\asm-attrs.jar;%HIBERNATE%\cglib-2.1.3.jar;%HIBERNATE%\commons-collections-2.1.1.jar;%HIBERNATE%\dom4j-1.6.1.jar;%HIBERNATE%\ehcache-1.2.3.jar;%HIBERNATE%\jdbc2_0-stdext.jar;%HIBERNATE%\jta.jar;%HIBERNATE%\hibernate3.jar;%HIBERNATE%\hibernate-tools.jar;%HIBERNATE%\hibernate-annotations.jar;%HIBERNATE%\hibernate-commons-annotations.jar;%HIBERNATE%\hibernate-entitymanager.jar;%HIBERNATE%\javassist.jar;%HIBERNATE%\ejb3-persistence.jar;..\lib\postgresql-8.3-603.jdbc3.jar;..\lib\ij.jar;..\lib\ujmp-complete-0.2.5.jar;..\dist\TrackAssist.jar au.com.nicta.ct.desktop.Main

endlocal