# vertx-recycle-policy-test
Tests Vert.x Core 3.6.0-SNAPSHOT with pull request that adds  pool recycle policy 

To tests this : 
I did not know how to build vert.x core with all its dependencies so I added 

```
 <descriptorRefs>
      <descriptorRef>jar-with-dependencies</descriptorRef>
</descriptorRefs>
            
```

To the maven-assembly-plugin plugin in the vert.x core

Then I built vert.x core by running

```
mvn clean package
```

Then in this project's pom file I pointed to the Vert.x core by adding the score and system path

```
<properties>
        <vertx-version>3.6.0-SNAPSHOT</vertx-version>
    </properties>
<dependency>
  <groupId>io.vertx</groupId>
  <artifactId>vertx-core</artifactId>
  <version>${vertx-version}</version>
  <scope>system</scope>
  <systemPath>/Users/mpogrebinsky/Development/vert.x/target/vertx-core-3.6.0-SNAPSHOT-jar-with-dependencies.jar</systemPath>
</dependency>
```

Once this project is ran 
I run tcpdump using this command line:

```
tcpdump -i lo0 'src localhost and ( dst port 9999 or src port 9999)'  -s 65535 -vv -S -A -w dump_fifo.pcap
```

Then I change the recycle policy in the Client.java

```
 .setPoolRecyclePolicy(RecyclePolicy.LIFO)
```

And return tcpdump:

```
tcpdump -i lo0 'src localhost and ( dst port 9999 or src port 9999)'  -s 65535 -vv -S -A -w dump_lifo.pcap
```


Also you can monitor the number of connections at any given moment by running:

```
netstat -na | grep ESTABLISHED  | awk '{print $4 }' | sort | uniq -c | grep  127.0.0.1.9999
```

With both Policies we should see 2 connections for 60 seconds

With LIFO we should see one connection getting closed after 60 seconds, and with FIFO we should see both connections staying 
open forever. 

