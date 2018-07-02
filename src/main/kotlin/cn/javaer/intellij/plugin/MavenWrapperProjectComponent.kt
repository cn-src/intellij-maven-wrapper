package cn.javaer.intellij.plugin

import com.intellij.openapi.components.ProjectComponent
import com.intellij.openapi.project.Project


/**
 * @author cn-src
 */
class MavenWrapperProjectComponent(val project: Project) : ProjectComponent {
    override fun initComponent() {
        MavenWrapperAction.config(project)
    }
}