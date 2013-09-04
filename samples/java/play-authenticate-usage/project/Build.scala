import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "play-twitter-client"
    val appVersion      = "1.0-SNAPSHOT"

    val appDependencies = Seq(
      "be.objectify"  %%  "deadbolt-java"     % "2.1-RC2",
      // Comment this for local development of the Play Authentication core
      "com.feth"      %%  "play-authenticate" % "0.3.3-SNAPSHOT",
      "postgresql"    %   "postgresql"        % "9.1-901-1.jdbc4",
      javaCore,
      javaJdbc,
      javaEbean,
      "com.clever-age" % "play2-elasticsearch" % "0.7-SNAPSHOT",
      "org.twitter4j"% "twitter4j-core"% "3.0.3"
    )
    
    val main = play.Project(appName, appVersion, appDependencies).settings(
    	// Hrm, 2.1.3, they broke running tests! So, how much testing are they really doing?
		testOptions in Test ~= { args =>
		  for {
		    arg <- args
		    val ta: Tests.Argument = arg.asInstanceOf[Tests.Argument]
		    val newArg = if(ta.framework == Some(TestFrameworks.JUnit)) ta.copy(args = List.empty[String]) else ta
		  } yield newArg
		},	

      resolvers += Resolver.url("Objectify Play Repository (release)", url("http://schaloner.github.com/releases/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("Objectify Play Repository (snapshot)", url("http://schaloner.github.com/snapshots/"))(Resolver.ivyStylePatterns),

      resolvers += Resolver.url("play-easymail (release)", url("http://joscha.github.com/play-easymail/repo/releases/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("play-easymail (snapshot)", url("http://joscha.github.com/play-easymail/repo/snapshots/"))(Resolver.ivyStylePatterns),

      resolvers += Resolver.url("play-authenticate (release)", url("http://joscha.github.com/play-authenticate/repo/releases/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("play-authenticate (snapshot)", url("http://joscha.github.com/play-authenticate/repo/snapshots/"))(Resolver.ivyStylePatterns),
      
      resolvers += Resolver.url("play-plugin-releases", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-releases/"))(Resolver.ivyStylePatterns),
      resolvers += Resolver.url("play-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns)
    )
//  Uncomment this for local development of the Play Authenticate core:
//    .dependsOn(playAuthenticate).aggregate(playAuthenticate)

}
