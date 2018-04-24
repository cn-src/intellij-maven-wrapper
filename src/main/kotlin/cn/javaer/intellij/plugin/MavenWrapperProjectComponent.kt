package cn.javaer.intellij.plugin

import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project
import org.apache.maven.wrapper.PathAssembler
import org.apache.maven.wrapper.WrapperConfiguration
import org.jetbrains.idea.maven.project.MavenProjectsManager
import java.io.File
import java.net.URI
import java.util.*


/**
 * @author zhangpeng
 */
class MavenWrapperProjectComponent(val project: Project) : ProjectComponent {

    private fun config() {
        val projectBaseDir = project.baseDir
        val wrapperMavenDir = projectBaseDir.findChild(".mvn")
        if (wrapperMavenDir != null && wrapperMavenDir.exists()) {
            val properties = Properties()
            try {
                properties.load(wrapperMavenDir.findChild("wrapper")!!.findChild("maven-wrapper.properties")!!.inputStream)
            } catch (ignore: Exception) {

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
                        val mavenDirName = distributionUrl.substring(distributionUrl.lastIndexOf("/") + 1).replace(".zip", "")
                        val mavenHome = File(distributionDir, mavenDirName)
                        if (mavenHome.exists()) {
                            val generalSettings = MavenProjectsManager.getInstance(project).generalSettings
                            if (generalSettings != null) {
                                generalSettings.mavenHome = mavenHome.absolutePath
                                generalSettings.setUserSettingsFile(mavenHome.absolutePath + "/conf/settings.xml")
                            }
                        }
                    }
                } catch (ignore: Exception) {
                }
            }
        }
    }
}