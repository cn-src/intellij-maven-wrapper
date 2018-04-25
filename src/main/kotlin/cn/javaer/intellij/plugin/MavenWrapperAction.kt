package cn.javaer.intellij.plugin

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.actionSystem.PlatformDataKeys
import com.intellij.openapi.project.Project
import org.apache.maven.wrapper.PathAssembler
import org.apache.maven.wrapper.WrapperConfiguration
import org.jetbrains.idea.maven.project.MavenProjectsManager
import java.io.File
import java.net.URI
import java.nio.charset.Charset
import java.util.*

/**
 * @author zhangpeng
 */
class MavenWrapperAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent?) {
        config(e!!.getData(PlatformDataKeys.PROJECT)!!)
    }

    companion object {
        fun config(project: Project) {
            val projectBaseDir = project.baseDir
            val wrapperMavenDir = projectBaseDir.findChild(".mvn")
            val generalSettings = MavenProjectsManager.getInstance(project).generalSettings

            if (wrapperMavenDir != null && wrapperMavenDir.exists()) {
                val properties = Properties()
                try {
                    properties.load(wrapperMavenDir.findChild("wrapper")!!.findChild("maven-wrapper.properties")!!.inputStream)
                } catch (e: Exception) {
                }

                //maven
                if (properties.containsKey("distributionUrl")) {

                    val distributionUrl = properties.getProperty("distributionUrl")
                    try {
                        val wrapperConfiguration = WrapperConfiguration()
                        wrapperConfiguration.distributionBase = "MAVEN_USER_HOME"
                        wrapperConfiguration.distribution = URI(distributionUrl)
                        val userHome = File(System.getProperty("user.home"))
                        val assembler = PathAssembler(File(userHome, ".m2"))
                        val distribution = assembler.getDistribution(wrapperConfiguration)
                        val distributionDir = distribution.distributionDir

                        if (distributionDir.exists()) {
                            val mavenDirName = distributionUrl.substring(distributionUrl.lastIndexOf("/") + 1)
                                    .replace("-bin", "")
                                    .replace(".zip", "")
                            val mavenHome = File(distributionDir, mavenDirName)
                            if (mavenHome.exists()) {
                                if (generalSettings != null) {
                                    generalSettings.mavenHome = mavenHome.absolutePath
                                    if (!File(System.getProperty("user.home"), ".m2/settings.xml").exists()) {
                                        generalSettings.setUserSettingsFile(mavenHome.absolutePath + "/conf/settings.xml")
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
            } else {
                val mavenDir = File(generalSettings.mavenHome)
                if (!mavenDir.exists()) {
                    try {
                        val output = Runtime.getRuntime().exec("mvn -v")
                                .inputStream.readBytes().toString(Charset.defaultCharset())
                        val start = output.indexOf("Maven home:")
                        val end = output.indexOfAny(setOf("\r\n", "\n", "\r"), start)
                        val mavenCommandDir = output.substring(start + "Maven home:".length, end).trim()
                        if (File(mavenCommandDir).exists()) {
                            generalSettings.mavenHome = mavenCommandDir
                        }
                        if (!File(System.getProperty("user.home"), ".m2/settings.xml").exists()) {
                            generalSettings.setUserSettingsFile("$mavenCommandDir/conf/settings.xml")
                        }
                    } catch (e: Exception) {

                    }
                }
            }
        }
    }
}