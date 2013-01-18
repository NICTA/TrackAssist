setlocal

REM set paths to these locations:
set APACHE="C:\Program Files (x86)\NetBeans 6.9.1\ide\modules\ext"
set HIBERN="C:\Program Files (x86)\NetBeans 6.9.1\java\modules\ext\hibernate"
set POSTGR="C:\Program Files (x86)\NetBeans 6.9.1\ide\modules\ext"
set LIB="C:\Users\davidjr\Documents\NetbeansProjects\jars\LIB"

set JAVAPATH= "C:\Program Files (x86)\Java\jdk1.6.0_23\bin"
set APPPATH="C:\Users\davidjr\Documents\NetbeansProjects\CellTracking\dist"

REM Add extra memory to run app:
%JAVAPATH%\java -Xmx1024m -cp %APACHE%\commons-logging-1.1.jar;%HIBERN%\antlr-2.7.6.jar;%HIBERN%\asm.jar;%HIBERN%\asm-attrs.jar;%HIBERN%\cglib-2.1.3.jar;%HIBERN%\commons-collections-2.1.1.jar;%HIBERN%\dom4j-1.6.1.jar;%HIBERN%\ehcache-1.2.3.jar;%HIBERN%\jdbc2_0-stdext.jar;%HIBERN%\jta.jar;%HIBERN%\hibernate3.jar;%HIBERN%\hibernate-tools.jar;%HIBERN%\hibernate-annotations.jar;%HIBERN%\hibernate-commons-annotations.jar;%HIBERN%\hibernate-entitymanager.jar;%HIBERN%\javassist.jar;%HIBERN%\ejb3-persistence.jar;%POSTGR%\postgresql-8.3-603.jdbc3.jar;%LIB%\ij.jar;%LIB%\ujmp-complete-0.2.5.jar;%APPPATH%\CellTracking.jar au.com.nicta.ct.desktop.Main
endlocal