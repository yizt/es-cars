active=dev
nohup java -Xms1303m -Xmx1303m -XX:MaxPermSize=256m  -classpath WEB-INF/classes/:WEB-INF/lib/*  org.es.skinservice.SkinServiceApp -Dspring.profiles.active=${active} &
